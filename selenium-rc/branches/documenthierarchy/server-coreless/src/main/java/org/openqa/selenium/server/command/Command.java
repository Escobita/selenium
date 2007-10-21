package org.openqa.selenium.server.command;

import java.util.Map;

import org.openqa.selenium.server.client.Session;


/**
 * Represents a given command.  It will provide the command to be
 * executed, the parameters, and the result of the execution.
 * 
 * @author Matthew Purland
 */
public interface Command<T extends CommandResult> {

	/**
	 * Returns the given command as a string.
	 * 
	 * @return Returns the given command represented as a string.
	 */
	String getCommand();

	/**
	 * Return a map of the given command parameters.
	 * 
	 * @return Returns a map of the command parameters.
	 */
	Map<String, String> getCommandParameterMap();	
	
	/**
	 * Gets the command result after the command was executed.
	 * 
	 * @return Returns the command result after the command was executed.
	 */
	T getCommandResult();
	
	/**
	 * Validate and set the given command parameters.
	 * 
	 * @throws CommandValidationException
	 *             when command parameters are incorrect
	 */
	 void validateAndSetParameters() throws CommandValidationException;
	
	/**
	 * Run the given command and return the given command result.  This method
	 * blocks until the given command has completed.
	 * 
	 * @return Returns the given command result.
	 */
	T run(Session session);
	
	/**
	 * Set the command result once the command has run and received a result.
	 * 
	 * @param commandResult The command result
	 */
	public void setCommandResult(T commandResult);
}
