package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractProxyCommand;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

public class GetTitleProxyCommand extends
		AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger
			.getLogger(GetTitleProxyCommand.class);

	public GetTitleProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.GET_TITLE, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		CommandResult commandResult = null;

		String windowTitle = session.getWindowManager().getCurrentWindowTitle();
		
		commandResult = new OKCommandResult(windowTitle);

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
