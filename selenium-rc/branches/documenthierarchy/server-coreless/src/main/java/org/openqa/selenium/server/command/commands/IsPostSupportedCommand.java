package org.openqa.selenium.server.command.commands;

import java.util.Map;

import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractLocalCommand;
import org.openqa.selenium.server.command.BooleanRemoteCommandResult;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedLocalCommand;

/**
 * Command for "isPostSupported".  This will always return false for backwards compatibility.
 * 
 * @todo Is this still needed?
 * 
 * @author Matthew Purland
 */
public class IsPostSupportedCommand extends AbstractLocalCommand<BooleanRemoteCommandResult> {

	public IsPostSupportedCommand(Map<String, String> commandParameterMap) {
		super(SupportedLocalCommand.IS_POST_SUPPORTED.getCommandName(), commandParameterMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BooleanRemoteCommandResult runCommand(Session session) {
		// Post is not supported...
		return new BooleanRemoteCommandResult(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		// Nothing to validate...
	}

}
