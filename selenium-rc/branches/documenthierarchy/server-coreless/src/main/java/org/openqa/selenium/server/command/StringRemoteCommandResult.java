package org.openqa.selenium.server.command;

/**
 * A string based remote command result. This is used for returning a specific
 * string.
 * 
 * @author Matthew Purland
 */
public class StringRemoteCommandResult extends AbstractRemoteCommandResult {

	public StringRemoteCommandResult(String commandResult) {
		super(commandResult);
	}

}
