package org.openqa.selenium.server.client;

import java.util.Map;

import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.handler.DriverCommandHandler;

/**
 * Driver client for handling state and commands between the server and the client driver.
 * 
 * @see DriverCommandHandler
 * @author Matthew Purland
 */
public class DriverClient extends AbstractClient {
	private DriverCommandHandler driverCommandHandler;
	
	/**
	 * Create a new client driver client with the given driver command handler.
	 * 
	 * @param driverCommandHandler
	 *            The driver command handler
	 */
	public DriverClient(DriverCommandHandler driverCommandHandler) {
		this.driverCommandHandler = driverCommandHandler;
	}

	public void close() {
		// @todo Close stream to driver client?
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult handleCommand(Session session, String commandName, String commandResult, Map<String, String> parametersMap) {
		return driverCommandHandler.handleCommand(session, commandName, commandResult, parametersMap);
	}
}
