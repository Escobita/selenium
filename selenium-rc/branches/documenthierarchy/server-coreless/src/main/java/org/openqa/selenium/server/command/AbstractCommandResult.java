package org.openqa.selenium.server.command;

/**
 * Abstract implementation for a command result.
 * 
 * @author Matthew Purland
 */
public abstract class AbstractCommandResult implements CommandResult {
	private String commandResult = "";
	
	/**
	 * Constructs a command result with a given command result.
	 * 
	 * @param commandResult The command result
	 */
	public AbstractCommandResult(String commandResult) {
		this.commandResult = commandResult;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Command Result (result=" + commandResult + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCommandResult() {
		return commandResult;
	}
	
	protected static String getFormattedCommandResult(String commandResult, String result) {
		String formattedCommandResult = commandResult + "," + result;
		return formattedCommandResult;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = 17;
		
		result = 37 * result + commandResult.hashCode();
		
		return result;
	}
}
