package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractProxyCommand;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

/**
 * Proxy command for opening on a given session.
 * 
 * @author Matthew Purland
 */
public class OpenProxyCommand extends AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger.getLogger(OpenProxyCommand.class);

	public OpenProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.OPEN, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		Map<String, String> parametersMap = getCommandParameterMap();

		String url = parametersMap.get("1"); // url

		logger.info("Running open proxy command (parameters=" + parametersMap
				+ ")");
		CommandResult commandResult = session.getWindowManager().open(url);
		logger.debug("After open proxy command...");
		
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
