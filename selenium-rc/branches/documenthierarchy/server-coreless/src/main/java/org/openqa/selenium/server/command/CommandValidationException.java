package org.openqa.selenium.server.command;

/**
 * Exception that occurs when a command is being validated before being run. This can occur because
 * the command parameters are incorrect, or the command was constructed incorrectly, or
 * inappropriately.
 * 
 * @author Matthew Purland
 */
public class CommandValidationException extends RuntimeException {
	/**
	 * Construct a new command validation exception.
	 */
	public CommandValidationException(Command command) {
		super("Command did not validate successfully: " + command.toString());
	}

	/**
	 * Construct a new command validation exception with a message.
	 * 
	 * @param mesg
	 *            The message
	 */
	public CommandValidationException(Command command, String mesg) {
		super(command.toString() + " : " + mesg);
	}

	/**
	 * Construct a new command validation exception with a message and a throwable.
	 * 
	 * @param mesg
	 *            The message
	 * @param throwable
	 *            The throwable
	 */
	public CommandValidationException(Command command, String mesg, Throwable throwable) {
		super(command.toString() + " : " + mesg, throwable);
	}
}
