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
 * Proxy command for selecting a window.
 * 
 * @author Matthew Purland
 */
public class SelectWindowProxyCommand extends AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger.getLogger(SelectWindowProxyCommand.class);

	public SelectWindowProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.SELECT_WINDOW, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		CommandResult commandResult = null;

		Map<String, String> parametersMap = getCommandParameterMap();

		// window ID/window name
		String windowID = parametersMap.get("1"); // windowID

		boolean selectedSuccessfully = session.getWindowManager().selectWindow(windowID);
		
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
