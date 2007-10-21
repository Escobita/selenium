package org.openqa.selenium.server.configuration;

/**
 * Exception that occurs when a configuration was not found or specified.
 * 
 * @author Matthew Purland
 */
public class ConfigurationOptionNotFoundException extends Exception {
	/**
	 * Construct a new configuration option not found exception.
	 */
	public ConfigurationOptionNotFoundException() {
		super();
	}

	/**
	 * Construct a new configuration option not found exception with a message.
	 * 
	 * @param mesg
	 *            The message
	 */
	public ConfigurationOptionNotFoundException(String mesg) {
		super(mesg);
	}

	/**
	 * Construct a new configuration not found exception with a message and a throwable.
	 * 
	 * @param mesg
	 *            The message
	 * @param throwable
	 *            The throwable
	 */
	public ConfigurationOptionNotFoundException(String mesg, Throwable throwable) {
		super(mesg, throwable);
	}
}
