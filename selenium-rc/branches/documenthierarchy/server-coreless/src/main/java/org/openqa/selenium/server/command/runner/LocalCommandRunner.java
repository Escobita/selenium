package org.openqa.selenium.server.command.runner;

import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.LocalCommand;

/**
 * Command runner responsible for running local commands.
 * 
 * @author Matthew Purland
 */
public class LocalCommandRunner extends AbstractCommandRunner<LocalCommand> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session, LocalCommand command) {
		// @todo Get thread from a thread pool and run in separate thread?
		return command.run(session);
	}
}
