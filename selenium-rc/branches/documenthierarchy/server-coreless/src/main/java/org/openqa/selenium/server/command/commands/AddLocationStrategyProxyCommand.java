package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractProxyCommand;
import org.openqa.selenium.server.command.CSVCommandResult;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

public class AddLocationStrategyProxyCommand extends
		AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger
			.getLogger(AddLocationStrategyProxyCommand.class);

	public AddLocationStrategyProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.ADD_LOCATION_STRATEGY, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		Map<String, String> parametersMap = getCommandParameterMap();

		String locator = parametersMap.get("1"); // locator
		String locatorCode = parametersMap.get("2"); // code for locator
		session.addLocatorStrategy(locator, locatorCode);
		
		CommandResult commandResult = new OKCommandResult();

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
