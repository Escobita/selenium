package org.openqa.selenium.server.command;

import org.openqa.selenium.server.client.Session;

/**
 * Represents a command that is a Null Object and does not
 * actually do anything.
 * 
 * @author Matthew Purland
 */
public final class NoCommand extends AbstractCommand<NoCommandResult> {
	private static final String NO_COMMAND = "";
	
	public NoCommand() {
		super(NO_COMMAND, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected NoCommandResult runCommand(Session session) {
		return new NoCommandResult();
	}

	/**
	 * {@inheritDoc}
	 */
	public void validateAndSetParameters() throws CommandValidationException {
		// No validation necessary...
	}
}
