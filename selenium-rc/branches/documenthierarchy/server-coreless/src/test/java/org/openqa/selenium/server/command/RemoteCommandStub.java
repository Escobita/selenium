package org.openqa.selenium.server.command;

import java.util.Map;

public class RemoteCommandStub extends RemoteCommand<CommandResult> {

	public static final String COMMAND_NAME = "remoteCommandStub";
	
	public RemoteCommandStub() {
		super(COMMAND_NAME, null);
	}
}
