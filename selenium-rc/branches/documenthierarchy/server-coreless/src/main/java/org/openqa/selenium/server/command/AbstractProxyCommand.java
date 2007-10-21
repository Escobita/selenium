package org.openqa.selenium.server.command;

import java.util.Map;

import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;
import org.openqa.selenium.server.command.runner.RemoteCommandRunner;

/**
 * Abstract implementation for a proxy command.
 * 
 * @author Matthew Purland
 *
 * @param <T>
 */
public abstract class AbstractProxyCommand<T extends CommandResult> extends
AbstractCommand<T> implements ProxyCommand<T> {

	public AbstractProxyCommand(SupportedProxyCommand command, Map<String, String> commandParameterMap) throws CommandValidationException {
		super(command.getCommandName(), commandParameterMap);
	}	
}