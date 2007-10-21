package org.openqa.selenium.server.command.handler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.LocalCommand;
import org.openqa.selenium.server.command.ProxyCommand;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.command.runner.LocalCommandRunner;
import org.openqa.selenium.server.command.runner.ProxyCommandRunner;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Command handler to handle commands received by the client drivers and send them to the
 * appropriate command runners.
 * 
 * @author Matthew Purland
 */
public class DriverCommandHandler implements CommandHandler {
	private static Logger logger = Logger.getLogger(DriverCommandHandler.class);

	// Runner for local commands
	private LocalCommandRunner localCommandRunner;

	// Runner for proxy commands
	private ProxyCommandRunner proxyCommandRunner;

	private CommandFactory commandFactory;

	private SeleniumConfiguration seleniumConfiguration;

	/**
	 * Construct a new driver command handler with the browser manager and session manager.
	 * 
	 * @param localCommandRunner
	 *            The local command runner
	 * @param remoteCommandRunner
	 *            The remote command runner
	 * @param proxyCommandRunner
	 *            The proxy command runner
	 * @param commandFactory
	 *            The command factory
	 */
	public DriverCommandHandler(SeleniumConfiguration seleniumConfiguration,
			LocalCommandRunner localCommandRunner, ProxyCommandRunner proxyCommandRunner,
			CommandFactory commandFactory) {
		this.seleniumConfiguration = seleniumConfiguration;
		this.localCommandRunner = localCommandRunner;
		this.proxyCommandRunner = proxyCommandRunner;
		this.commandFactory = commandFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult handleCommand(Session session, String commandName,
			String commandResult, Map<String, String> parametersMap) {
		CommandResult result = null;

		LocalCommand<CommandResult> localCommand = null;
		RemoteCommand<CommandResult> remoteCommand = null;
		ProxyCommand<CommandResult> proxyCommand = null;

		
		// @todo should command runners run in a separate thread? if so should we have a master
		// command runner/thread?

		// Try getting the local command..if it exists.
		localCommand = commandFactory.getLocalCommand(commandName, session,
				parametersMap);

		if (localCommand != null) {
			// Use the command runner and run the command on the driver client
			result = localCommandRunner.run(localCommand, session);
			logger.debug("Local command " + localCommand
					+ " finished with result: " + result);
		} else {
						
			// Only get a proxy command if it is in PI mode
			if (seleniumConfiguration.isProxyInjectionMode()) {
				proxyCommand = commandFactory.getProxyCommand(commandName,
						session, parametersMap);

				if (proxyCommand != null) {
					logger.debug("Proxying command " + commandName + " session="
							+ session + " parametersMap=" + parametersMap);

					result = proxyCommandRunner.run(proxyCommand, session);
					logger.debug("Proxy command " + proxyCommand
							+ " finished with result " + result);
				} 
			}

			if (proxyCommand == null) {
				logger.debug("Creating remote command \"" + commandName
						+ "\" with session " + session + " and parameters "
						+ parametersMap);
				remoteCommand = new RemoteCommand(commandName, parametersMap);
				logger.debug("remoteCommand=" + remoteCommand);

				// Send unrecognized commands to remote/browser client
				if (remoteCommand != null) {										
					// Should block until we receive a command result
					result = session.getWindowManager().run(remoteCommand);
					logger.debug("Remote command " + remoteCommand
							+ " finished with result: " + result);
				} else {
					logger.warn("Unsupported command=" + commandName
							+ " parameters=" + parametersMap);
				}
			}
		}

		return result;
	}

	/**
	 * Get the local command runner.
	 */
	public LocalCommandRunner getLocalCommandRunner() {
		return localCommandRunner;
	}

	/**
	 * Get the proxy command runner.
	 */
	public ProxyCommandRunner getProxyCommandRunner() {
		return proxyCommandRunner;
	}
}
