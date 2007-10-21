package org.openqa.selenium.server.configuration;

/**
 * An exception for when there is an invalid configuration.
 * 
 * @author Matthew Purland
 */
public class InvalidConfigurationException extends RuntimeException {

	/**
	 * Construct a new exception for an invalid configuration with a message.
	 * 
	 * @param mesg
	 *            The message
	 */
	public InvalidConfigurationException(String mesg) {
		super(mesg);
	}

	/**
	 * Construct a new exception for an invalid configuration with a message and
	 * a throwable.
	 * 
	 * @param mesg
	 *            The message
	 * @param throwable
	 *            The throwable
	 */
	public InvalidConfigurationException(String mesg, Throwable throwable) {
		super(mesg, throwable);
	}
}
