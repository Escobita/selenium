package org.openqa.selenium.server.command;

/**
 * Represents the Null Object pattern for a command result that
 * does not exist.
 * 
 * @author Matthew Purland
 */
public final class NoCommandResult extends AbstractCommandResult {

	private static final String NO_COMMAND_RESULT = "";
	
	/**
	 * Construct a remote command result that is an empty command result.
	 */
	public NoCommandResult() {
		super(NO_COMMAND_RESULT);
	}
}
