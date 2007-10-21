package org.openqa.selenium.server.command.handler;

import java.util.Map;

import org.openqa.selenium.server.client.Client;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.runner.CommandRunner;

/**
 * A command handler that processes and handles incoming commands and if needed will send these
 * commands to an appropriate {@link CommandRunner}.
 * 
 * @author Matthew Purland
 */
public interface CommandHandler {
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
	CommandResult handleCommand(Session session, String commandName, String commandResult, Map<String, String> parametersMap);
}
