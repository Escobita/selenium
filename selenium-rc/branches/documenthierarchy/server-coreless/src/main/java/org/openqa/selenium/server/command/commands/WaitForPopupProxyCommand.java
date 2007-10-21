package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.client.Window;
import org.openqa.selenium.server.command.AbstractProxyCommand;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

/**
 * Proxy command to wait for a popup to load.
 * 
 * @author Matthew Purland
 */
public class WaitForPopupProxyCommand extends
		AbstractProxyCommand<CommandResult> {
	private static Logger logger = Logger
			.getLogger(WaitForPopupProxyCommand.class);

	public WaitForPopupProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.WAIT_FOR_POPUP, commandParameterMap);
	}

	/**
	 * {@inheritDoc}`
	 */
	@Override
	protected CommandResult runCommand(Session session) {
		CommandResult commandResult = null;

		Map<String, String> parametersMap = getCommandParameterMap();

		// window ID/window name
		String windowID = parametersMap.get("1"); // windowID
		String timeoutInMillisecondsString = parametersMap.get("2"); // timeout

		long timeoutInMilliseconds = Long.parseLong(timeoutInMillisecondsString);

		Window foundWindow = session.getWindowManager().waitForWindowToLoad(windowID, timeoutInMilliseconds);

		if (foundWindow != null) {
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
