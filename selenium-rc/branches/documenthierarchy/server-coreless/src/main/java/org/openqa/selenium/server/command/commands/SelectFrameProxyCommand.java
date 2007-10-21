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
 * Proxy command for selecting a frame.
 * 
 * @author Matthew Purland
 */
public class SelectFrameProxyCommand extends
		AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger
			.getLogger(SelectFrameProxyCommand.class);

	public SelectFrameProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.SELECT_FRAME, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		CommandResult commandResult = null;

		Map<String, String> parametersMap = getCommandParameterMap();

		// Frame address as the locator
		String frameAddress = parametersMap.get("1"); // locator

		boolean selectedSuccessfully = session.getWindowManager().getCurrentWindow().selectFrame(frameAddress);
		
		if (selectedSuccessfully) {
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
