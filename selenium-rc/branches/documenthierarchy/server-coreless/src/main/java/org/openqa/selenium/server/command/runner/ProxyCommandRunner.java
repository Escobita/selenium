package org.openqa.selenium.server.command.runner;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.ProxyCommand;

/**
 * Runner to run proxy commands.
 * 
 * @author Matthew Purland
 */
public class ProxyCommandRunner
		extends
		AbstractCommandRunner<ProxyCommand> {
	private static Logger logger = Logger
	.getLogger(ProxyCommandRunner.class);

//	public ProxyCommandRunner() {
//		super(new SynchronousQueue<ProxyCommand>());
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CommandResult runCommand(Session session, ProxyCommand command) {
		// @todo Get thread from a thread pool and run in separate thread?
		// @todo Do we need to run separately because it is a proxy command?
		return command.run(session);
	}
}
