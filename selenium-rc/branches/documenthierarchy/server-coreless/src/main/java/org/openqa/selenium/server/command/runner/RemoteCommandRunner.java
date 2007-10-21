package org.openqa.selenium.server.command.runner;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.StringRemoteCommandResult;

/**
 * Runner to run remote commands on a specified remote client. The remote
 * command runner should send commands remotely to the remote client and wait
 * (this does not to block) until it receives a result back.
 * 
 * @author Matthew Purland
 * @param <T>
 */
public class RemoteCommandRunner extends
		AbstractCommandRunner<RemoteCommand<CommandResult>> {

	private static Logger logger = Logger.getLogger(RemoteCommandRunner.class);

	private BlockingQueue<RemoteCommand<CommandResult>> commandQueue = new SynchronousQueue<RemoteCommand<CommandResult>>();

	private BlockingQueue<CommandResult> commandResultQueue = new SynchronousQueue<CommandResult>();

	RemoteCommand<CommandResult> runningCommand = null;
	
	private boolean shutdown = false;
	public boolean hasModal = false;
	
	// To help the runCommand sync up for command results to tell the command
	// runner that we are processing a result so wait up
	private Boolean handlingResult = false;
	
	private Queue<Thread> waitingBrowserQueue = new ConcurrentLinkedQueue<Thread>();

	Integer lastSequenceId = new Integer(-1);
	final Lock resultLock = new ReentrantLock();
	final Condition sequenceProcessed = resultLock.newCondition();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RemoteCommandRunner (commandQueue id=" 
			+ System.identityHashCode(commandQueue)
			+ ")";
	}

	/**
	 * Close the remote command runner.  Interrupting any threads that
	 * are waiting.
	 */
	public void close() {
		for (Thread thread : waitingBrowserQueue) {
			thread.interrupt();
		}
		shutdown = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	synchronized protected CommandResult runCommand(Session session,
			RemoteCommand command) throws InterruptedException {
		this.runningCommand = command;
//		waitingBrowserQueue.add(Thread.currentThread());
		CommandResult commandResult = null;

		try {
			String commandDebug = (command != null ? command.getCommand()
					: "empty command")
					+ "  (" + System.identityHashCode(command) + ")";
			logger.debug("putting commandQueue ("
					+ System.identityHashCode(commandQueue) + ") : "
					+ commandDebug);
			commandQueue.put(command);
			logger.debug("put commandQueue ("
					+ System.identityHashCode(commandQueue) + ") : "
					+ commandDebug);

			logger.debug("taking commandResult ("
					+ System.identityHashCode(commandResultQueue) + ") : ");
			
			// continue to pull the result queue until we get a result or someone shuts us down
			// only keep polling results for commands that should wait for a command result
			while (commandResult == null && command.getWaitingType().equals(RemoteCommand.WaitingType.WAIT_FOR_COMMAND_RESULT)) {
				boolean wasHandlingResult = handlingResult;
				
				commandResult = commandResultQueue.poll(100, TimeUnit.MILLISECONDS);
				
					// Skip shutting down until we're not handling a result
					if (wasHandlingResult && !handlingResult) {
						
					}
					if ((commandResult == null) && shutdown && !hasModal && !handlingResult)
					{
						logger.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>server shutdown while waiting for a response.  Exiting...");
						break;
					}
			}
			
			if (command.getWaitingType().equals(RemoteCommand.WaitingType.DONT_WAIT)) {
				logger.debug("Not waiting for command result, setting command result to OK");
				commandResult = new OKCommandResult();
			}
			commandDebug = (commandResult != null ? commandResult
					.getCommandResult() : "empty command result")
					+ " id: " + System.identityHashCode(commandResult);
			logger.debug("took commandResult ("
					+ System.identityHashCode(commandResultQueue) + ") : "
					+ commandDebug);

		} catch (InterruptedException ex) {
			logger.warn("Interrupted while running command: " + this.runningCommand + " ...rethrowing", ex);
			throw ex;
		}

		this.runningCommand = null;
//		waitingBrowserQueue.remove(Thread.currentThread());
		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 */
	public RemoteCommand<CommandResult> getNextCommandToRun() {
		RemoteCommand<CommandResult> command = null;
		waitingBrowserQueue.add(Thread.currentThread());
		try {
			logger.debug("taking commandQueue ("
					+ System.identityHashCode(commandQueue) + ") : ");
			
			while (!shutdown && command == null) {
				command = commandQueue.poll(100, TimeUnit.MILLISECONDS);
			}
			
			if (shutdown) {
				logger.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>runner shutdown while waiting for next command to run.  Exiting...");
			}
		
			String commandDebug = (command != null ? command.getCommand()
					: "empty command")
					+ " id: " + System.identityHashCode(command);
			logger.debug("took commandQueue ("
					+ System.identityHashCode(commandQueue) + ") : "
					+ commandDebug);
		} catch (InterruptedException ex) {
			logger.warn(
					"Interrupted exception while trying waiting to pop from command runner: "
							+ this, ex);
		}
		finally {
			waitingBrowserQueue.remove(Thread.currentThread());
		}
		return command;
	}
	
	/**
	 * Handle a command result that is being received.
	 */
	public CommandResult handleResult(Session session, String result, String commandId, Integer sequenceId) throws InterruptedException {
		
		logger.debug("Handling result for commandId=" + commandId + ", sequenceId=" + sequenceId);
		resultLock.lock();
		try {
			logger.debug("Acquired lock for commandId=" + commandId + ", sequenceId=" + sequenceId + ", lastSequenceId=" + lastSequenceId);
			while (sequenceId > (lastSequenceId + 1)) {
				sequenceProcessed.await();
			}
			boolean hasCommandId = commandId != null && !"".equals(commandId);
			CommandResult commandResult = null;
			
				handlingResult = true;
	
				// if this isn't a start command, the set the response on the
				// current runner
				if (!"START".equals(result) && !"\r\n\r\n".equals(result)) {
					logger.debug("The currently running command is: " + runningCommand);
					
					if (runningCommand != null)
					{
						logger.debug("Trying to set command result...commandId=" + commandId);
						if (hasCommandId) {
							if (commandId.equals(runningCommand.getCommandId())) {
								logger.info("Sending command result " + result + " for running command " + runningCommand);
								commandResult = new StringRemoteCommandResult(result);
								setCommandResult(commandResult);
							}
							else {
								logger.debug("Received result: " + result + " but did not match running command's command id (" + commandId + " != " + runningCommand.getCommandId() + ")");
							}
						}
						else {
							logger.info("Handling command result for no command id, result: " + result);
						}
		
					}
				}
				handlingResult = false;
				
				return commandResult;
		} finally {
			logger.debug("Releasing lock for result for commandId=" + commandId + ", sequenceId=" + sequenceId + ", lastSequenceId=" + lastSequenceId);
			// notify any waiting threads that a sequence has been handled so they can wake up and check if it's their turn
			lastSequenceId = sequenceId;
			sequenceProcessed.signalAll();			
			
			resultLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void setCommandResult(CommandResult commandResult) {
		try {
			String commandDebug = (commandResult != null ? commandResult
					.getCommandResult() : "empty command result")
					+ " id: " + System.identityHashCode(commandResult);
			logger.debug("putting commandResult ("
					+ System.identityHashCode(commandResultQueue) + ") : "
					+ commandDebug);
			commandResultQueue.put(commandResult);
			logger.debug("put commandResult ("
					+ System.identityHashCode(commandResultQueue) + ") : "
					+ commandDebug);
		} catch (InterruptedException ex) {
			logger.debug(
					"Interrupted while trying to put into queue for command result for command: "
							+ this, ex);
		}
	}

	public RemoteCommand<CommandResult> getRunningCommand() {
		return runningCommand;
	}

}
