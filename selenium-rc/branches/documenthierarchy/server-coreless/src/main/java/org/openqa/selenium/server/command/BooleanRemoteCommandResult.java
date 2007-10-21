package org.openqa.selenium.server.command;

/**
 * A boolean {@link CommandResult} that will either have a true or false value.
 * 
 * @author Matthew Purland
 */
public class BooleanRemoteCommandResult extends OKCommandResult {
	private boolean booleanResult;
	
	public BooleanRemoteCommandResult(boolean booleanResult) {
		super(String.valueOf(booleanResult));
		this.booleanResult = booleanResult;
	}

	/**
	 * Returns true if the command result was true.
	 * 
	 * @return Returns true if result was true; false otherwise;
	 */
	public boolean isTrue() {
		return booleanResult;
	}

	/**
	 * Returns true if the command result was false.
	 * 
	 * @return Returns true if result was false; false otherwise;
	 */
	public boolean isFalse() {
		return !booleanResult;
	}
}
