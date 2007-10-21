package org.openqa.selenium.server.command;

/**
 * Remote command result from a command.
 * 
 * @author Matthew Purland
 */
public interface CommandResult {
	/**
	 * Returns the string representation of the command result.
	 * 
	 * @return Returns a string of the command result.
	 */
	String getCommandResult();
}
