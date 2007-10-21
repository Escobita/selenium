package org.openqa.selenium.server.command;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.openqa.selenium.server.client.Session;

public class RemoteCommandTest extends TestCase {
	public void testCreateCommand() {
		Map<String, String> parametersMap = new HashMap<String, String>();
		final String commandName = "open";
		
		RemoteCommand remoteCommand = new RemoteCommand(commandName, parametersMap);
		
		System.out.println("remoteCommand=" + remoteCommand);
		assertNotNull(remoteCommand);
	}
}
