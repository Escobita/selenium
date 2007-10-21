package org.openqa.selenium.server.command;

/**
 * Abstract implementation for a remote command result.
 * 
 * @author Matthew Purland
 */
public class AbstractRemoteCommandResult extends AbstractCommandResult
		implements RemoteCommandResult {

	/**
	 * Construct a new remote command result.
	 * 
	 * @param commandResult
	 *            The command result string
	 */
	public AbstractRemoteCommandResult(String commandResult) {
		super(commandResult);
	}

}
