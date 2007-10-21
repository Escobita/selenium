package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Document;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractProxyCommand;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

/**
 * Proxy command to wait for a page to load.
 * 
 * @author Matthew Purland
 */
public class WaitForPageToLoadProxyCommand extends
		AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger
			.getLogger(WaitForPageToLoadProxyCommand.class);

	public WaitForPageToLoadProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.WAIT_FOR_PAGE_TO_LOAD, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		CommandResult commandResult = null;

		Map<String, String> parametersMap = getCommandParameterMap();

		// Timeout specified in milliseconds
		String timeoutString = parametersMap.get("1"); // timeout
		int timeoutInMilliseconds = Integer.valueOf(timeoutString);

		boolean waitedSuccessfully = session.getWindowManager().waitForAnyDocumentToLoad(timeoutInMilliseconds);
		
		if (waitedSuccessfully) {
			commandResult = new OKCommandResult();
		}

		return commandResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		// Nothing to validate...
	}
}
