package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractProxyCommand;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

public class CloseProxyCommand extends AbstractProxyCommand<OKCommandResult> {
	private static Logger logger = Logger.getLogger(CloseProxyCommand.class);

	public CloseProxyCommand(Map<String, String> commandParameterMap) {
		super(SupportedProxyCommand.CLOSE, commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OKCommandResult runCommand(Session session) {	
		// The OK returned by the unload will actually close the client
//		BrowserClient browserClient = session.getCurrentBrowserClient();
//
//		session.removeBrowserClient(browserClient);
		
		OKCommandResult commandResult = new OKCommandResult();
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
