package org.openqa.selenium.server.command.runner;

import org.openqa.selenium.server.SeleniumUnitTestBase;
import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.RemoteCommandResult;
import org.openqa.selenium.server.command.RemoteCommandStub;

import junit.framework.TestCase;

public class RemoteCommandRunnerTest extends SeleniumUnitTestBase {

	private final RemoteCommandRunner remoteCommandRunner = new RemoteCommandRunner();
	private Session session;
	private RemoteCommand<CommandResult> remoteCommand;
	
	private final Thread waitingThread = new Thread(
			new Runnable() {
				public void run() {
					remoteCommandRunner.run(remoteCommand, session);
				}
			}
	);
	
	private final Thread closingThread = new Thread(
			new Runnable() {
				public void run() {
					remoteCommandRunner.close();
				}
			}
	);
	
	public void setUp() {
		super.setUp();
		
//		BrowserType browserType = BrowserType.getBrowserType("*mock");
//		String startURL = "http://localhost:4444/test";
//		session = getSeleniumDependencyManager().getSessionManager().createSession(browserType, startURL);
//		remoteCommand = new RemoteCommandStub();
	}
	
	public void testClose() {

//		waitingThread.run();
		

		
//		closingThread.run();
		
		// @todo Assert that the thread that is waiting to put in command queue
	}

	public void testRunCommandSessionRemoteCommand() {
//		fail("Not yet implemented");
	}

	public void testGetNextCommandToRun() {
//		fail("Not yet implemented");
	}

	public void testHandleResult() {
//		fail("Not yet implemented");
	}

	public void testSetCommandResult() {
//		fail("Not yet implemented");
	}

	public void testGetRunningCommand() {
//		fail("Not yet implemented");
	}

	public void testRun() {
//		fail("Not yet implemented");
	}

}
