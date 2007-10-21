package org.openqa.selenium.server.command.runner;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.Command;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;

/**
 * Abstract implementation for {@link CommandRunner}. Uses a queues to keep track of a command and
 * running them.
 * 
 * @author Matthew Purland
 * 
 * @param <T>
 *            Command with the command result defined
 * @param <S>
 *            Command result
 */
public abstract class AbstractCommandRunner<T extends Command> implements
		CommandRunner<T> {

	private static Logger logger = Logger
			.getLogger(AbstractCommandRunner.class);
	
	/**
	 * Run the command and return the command result.
	 * 
	 * @returns Returns the command result.
	 */
	protected abstract CommandResult runCommand(Session session, T command) throws InterruptedException;

	/**
	 * {@inheritDoc}
	 */
	public CommandResult run(T command, Session session) {
		CommandResult commandResult = null;

		logger.info("Running command " + command + " with session " + session + " waiting for command to be retrieved.");
	
		try {
			command.validateAndSetParameters();

			commandResult = runCommand(session, command);
		} catch (CommandValidationException ex) {
			logger.warn("Command " + command
					+ " is not valid.  Cannot run...");
		} catch (InterruptedException ex) {
			logger.warn("Command " + command + " was interrupted while trying to run.");
		}
		
		// @todo Is there any way we can change the API to return a NoCommandResult instead of null?

		return commandResult;
	}
}
