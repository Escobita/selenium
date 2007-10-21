package org.openqa.selenium.server.command;

/**
 * An "OK" result from a command that it was successful.
 * 
 * @author Matthew Purland
 */
public class OKCommandResult extends AbstractCommandResult {

	private static final String OK = "OK";
	
	public OKCommandResult() {
		super(OK);
	}
	
	public OKCommandResult(String result) {
		super(getFormattedCommandResult(OK, result));
	}
}
