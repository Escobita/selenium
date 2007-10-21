package org.openqa.selenium.server.browser.launchers;

import java.util.Set;

import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.SeleniumTestBase;
import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.browser.BrowserType.Browser;
import org.openqa.selenium.server.client.DriverClient;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.client.WindowManager;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

public class BrowserLauncherTest extends SeleniumTestBase {
	private final static String BROWSER_LAUNCHER_URL = "http://www.google.com";

	private BrowserLauncherFactory browserLauncherFactory;

	private CommandFactory commandFactory;
	
	private SessionManager sessionManager;
	
	private SeleniumConfiguration seleniumConfiguration;

	public void setUp() throws Exception {
		super.setUp();

		start();

		SeleniumServer seleniumServer = getSeleniumServer();
		seleniumConfiguration = seleniumServer
				.getSeleniumConfiguration();
		browserLauncherFactory = new BrowserLauncherFactory(
				seleniumConfiguration);
		commandFactory = new CommandFactory(seleniumConfiguration);
		sessionManager = new SessionManager(seleniumConfiguration, browserLauncherFactory, commandFactory);
	}
	
	public void testDisabled() {
		
	}

	/**
	 * Test going thorugh browser launchers, launching them, and asserting they have been
	 * successfully closed.
	 * 
	 * May need to run test manually to see if it works correctly on systems.
	 */
	public void DISABLEDtestBrowserLauncherClose() {
		Set<Browser> supportedBrowsersSet = BrowserLauncherFactory
				.getSupportedBrowsers();

		for (Browser supportedBrowser : supportedBrowsersSet) {
			BrowserType browserType = new BrowserType(supportedBrowser);

			BrowserLauncher browserLauncher = browserLauncherFactory
					.getBrowserLauncher(browserType);

			DriverClient driverClient = sessionManager.createDriverClient(commandFactory);
//			BrowserClient browserClient = sessionManager.createBrowserClient("1", commandFactory);
			
			WindowManager windowManager = new WindowManager();
			
			Session session = new Session(seleniumConfiguration, browserType, browserLauncher, driverClient, windowManager);
//			session.addBrowserClient(browserClient);
			browserLauncher.setSession(session);
			browserLauncher.launchRemoteSession(BROWSER_LAUNCHER_URL, false,
					false);
			boolean hasClosedSuccessfully = browserLauncher.close();
			assertTrue("Browser launcher" + browserLauncher
					+ " did not close successfully.", hasClosedSuccessfully);
		}
	}
}
