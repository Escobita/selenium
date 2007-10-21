package org.openqa.selenium.server.client;

import org.openqa.selenium.server.command.handler.CommandHandler;
import org.openqa.selenium.server.command.runner.RemoteCommandRunner;

/**
 * Abstract implementation for a remote client {@link RemoteClient}.
 * 
 * @author Matthew Purland
 *
 * @param <T> The command handler
 */
public abstract class AbstractRemoteClient extends AbstractClient implements RemoteClient {

	private RemoteCommandRunner remoteCommandRunner;
	
	public AbstractRemoteClient(RemoteCommandRunner remoteCommandRunner) {
		this.remoteCommandRunner = remoteCommandRunner;
	}
	
	public RemoteCommandRunner getRemoteCommandRunner() {
		return remoteCommandRunner;
	}
}
