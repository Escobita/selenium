package org.openqa.selenium.server.client;

import junit.framework.TestCase;

import org.openqa.selenium.server.configuration.DefaultSeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.jetty.SeleniumDependencyManager;

public class SessionManagerTest extends TestCase {
	private SessionManager sessionManager;
	
	public void setUp() {
		SeleniumConfiguration seleniumConfiguration = new DefaultSeleniumConfiguration();
		
		SeleniumDependencyManager dependencyManager = new SeleniumDependencyManager(seleniumConfiguration);
		
		sessionManager = new SessionManager(dependencyManager.getSeleniumConfiguration(), dependencyManager.getBrowserLauncherFactory(), dependencyManager.getCommandFactory() );
	}
	
	public void testSessionManagerAdd() {
		//sessionManager.addSession(null);
	}
}
