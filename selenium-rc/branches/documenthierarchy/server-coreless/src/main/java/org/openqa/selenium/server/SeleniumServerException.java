package org.openqa.selenium.server;

/**
 * Exception to indicate a problem has occurred with the selenium server such as
 * starting or stopping the server.
 * 
 * @author Matthew Purland
 */
public class SeleniumServerException extends RuntimeException {
	/**
	 * Construct a new exception for a problem with the selenium server.
	 * 
	 * @param mesg
	 *            The message
	 */
	public SeleniumServerException(String mesg) {
		super(mesg);
	}

	/**
	 * Construct a new exception for a problem with the selenium server with a
	 * message and a throwable.
	 * 
	 * @param mesg
	 *            The message
	 * @param throwable
	 *            The throwable
	 */
	public SeleniumServerException(String mesg, Throwable throwable) {
		super(mesg, throwable);
	}
}
