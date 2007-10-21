package org.openqa.selenium.server.command;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.launchers.LauncherUtils;
import org.openqa.selenium.server.client.Session;

/**
 * Default implementation for a remote command.
 * 
 * @author Matthew Purland
 */
public class RemoteCommand<T extends CommandResult> extends AbstractCommand<T> {
	private static Logger logger = Logger.getLogger(RemoteCommand.class);

	private static int commandIdCounter = 0;
	
	private boolean hasReceivedResult = false;

	private String attachedJavascript;
	
	private String commandId;
	
	private WaitingType waitingType = WaitingType.WAIT_FOR_COMMAND_RESULT;
	
	public enum WaitingType {
		WAIT_FOR_COMMAND_RESULT,
		DONT_WAIT
	}
	
	/**
	 * Construct a new remote command from the given command and list of command parameters as
	 * values.
	 * 
	 * @param command
	 *            The command
	 * @param commandParameterMap
	 *            The map of command parameters
	 */
	public RemoteCommand(String command,	Map<String, String> commandParameterMap) {
		super(command, commandParameterMap);
		
		this.commandId = createNewCommandId();
	}
	
	protected String createNewCommandId() {
		// Increment counter
		commandIdCounter++;
		return String.valueOf(commandIdCounter);
	}	

	/**
	 * Check to determine if the remote command has finished executing and has a result.
	 * 
	 * @return Returns true if the remote command has a result.
	 */
	public boolean hasReceivedResult() {
		return hasReceivedResult;
	}

	/**
	 * Check to determine if the remote command has finished executing and has a result.
	 * 
	 * @return Returns true if the remote command has a result.
	 */
	public String getBrowserCommandResponse() {
		StringBuffer buffer = new StringBuffer();

		String browserCommandResponse = "";
		
		Set<String> commandParameterMapKeySet = getCommandParameterMap()
				.keySet();

		buffer.append("cmd=" + LauncherUtils.urlEncode(getCommand()));

		// int i = 1;

		for (String commandParameterKey : commandParameterMapKeySet) {
//			int i = SeleniumCommandTranslator.getIndexFromArgumentName(
//					getCommand(), commandParameterKey);
			
			int i = 0;
			try { 
				i = Integer.parseInt(commandParameterKey);
				buffer.append("&"
						+ i
						+ "="
						+ LauncherUtils.urlEncode(getCommandParameterMap().get(
								commandParameterKey)));
				// i++;
			}
			catch (NumberFormatException ex) {
				// Do nothing...
			}
		}
		
		buffer.append("&commandId=" + getCommandId());

		browserCommandResponse = buffer.toString();
		
		if (getAttachedJavascript() != null && !"".equals(getAttachedJavascript())) {
			browserCommandResponse = browserCommandResponse + "\n" + getAttachedJavascript();
		}
		
		return browserCommandResponse;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T run(Session session) {
		T commandResult = super.run(session);

		hasReceivedResult = true;

		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		// @todo Can we get command result in the toString?
		// @todo Command result was removed because of blocking getCommandResult
		return "Remote Command (command=" + getCommand() + " parameters="
				+ getCommandParameterMap() + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected T runCommand(Session session) {
		// No implementation for a remote command...
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		// @todo Should we have remote command validators be called in here?
		// Nothing to validate yet...
	}

	/**
	 * Get the attached javascript.
	 */
	protected String getAttachedJavascript() {
		return attachedJavascript;
	}

	/**
	 * Set the attached javascript.
	 * 
	 * @param attachedJavascript
	 *            The javascript to set
	 */
	public void setAttachedJavascript(String attachedJavascript) {
		this.attachedJavascript = attachedJavascript;
	}

	public String getCommandId() {
		return commandId;
	}
	
	/**
	 * Set the type of waiting for the command.
	 */
	public void setWaitingType(WaitingType waitingType) {
		this.waitingType = waitingType;
	}

	/**
	 * Get the waiting type for the command.
	 */
	public WaitingType getWaitingType() {
		return waitingType;
	}
}
