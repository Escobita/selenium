package org.openqa.selenium.server.client;

import java.util.Map;

import org.openqa.selenium.server.command.CommandResult;

/**
 * Client that can handle commands and run commands for a specific client end. (e.g. Client driver,
 * browser, or server).
 * 
 * @todo Need to add server client for adding servers as clients such as delegating commands to
 *       other servers to run clustered tests async or faster.
 * 
 * @param T
 *            Command handler for the client
 * 
 * @author Matthew Purland
 */
public interface Client {

	/**
	 * Handle the given specified command with the given list of command values.
	 * 
	 * @param session
	 * 			  The session this command is handled on
	 * @param commandName
	 *            The command name
	 * @param commandResult
	 * 			  The command result (if a command result sent, otherwise this should be blank)
	 * @param parametersMap
	 *            The map of parameter names to their values
	 * 
	 * @return Returns a valid command result if it was handled successfully; null otherwise.
	 */
	CommandResult handleCommand(Session session, String commandName,
			String commandResult, Map<String, String> parametersMap);
	
	/**
	 * Close and end communication through the client. This will usually will be called by
	 * {@link SessionManager}.
	 */
	void close();
}
