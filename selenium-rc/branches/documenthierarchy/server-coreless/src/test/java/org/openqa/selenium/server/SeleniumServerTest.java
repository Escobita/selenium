package org.openqa.selenium.server;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.openqa.selenium.server.configuration.ArgumentsConfiguration;
import org.openqa.selenium.server.configuration.InvalidConfigurationException;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Test case for Selenium Server.
 * 
 * @author Matthew Purland
 */
public class SeleniumServerTest extends TestCase {

	private static final String INVALID_ARGUMENT = "-someInvalidArgument";
	
	private SeleniumServer seleniumServer;
	
	protected void setUp() {
		seleniumServer = new SeleniumServer();
	}
	
	protected void tearDown() {
		if (seleniumServer != null) {
			seleniumServer.stop();
		}
		
		SeleniumServer.stopServer();
	}
	
	/**
	 * Test the startup of the Selenium server the way that it would be invoked usually by the
	 * command line.
	 */
	public void testSeleniumServerStartUp() {
		SeleniumServer.startServer(new String[0]);

		seleniumServer = SeleniumServer.getInstance();
		
		assertTrue("Selenium server did not startup correctly.", seleniumServer
				.isStarted());
	}

	/**
	 * Test the startup of the Selenium server the way that it would be invoked by main.
	 */
	public void testMainSeleniumServerStartUp() {
//		SeleniumServer seleniumServer = SeleniumServer.getInstance();
//		seleniumServer.reset();

		SeleniumServer.main(new String[0]);

		seleniumServer = SeleniumServer.getInstance();
		
		assertTrue("Selenium server did not startup correctly.", seleniumServer
				.isStarted());
	}

	/**
	 * Test an invalid configuration.
	 */
	public void testInvalidConfiguration() {
		String[] args = new String[] { INVALID_ARGUMENT };

//		SeleniumServer seleniumServer = SeleniumServer.getInstance();
//		seleniumServer.reset();

		try {
			SeleniumServer.startServer(args);
			fail("Configuration with invalid configuration did not fail correctly.  This may be perhaps configuration validation is not happening correctly.");
		} catch (InvalidConfigurationException ex) {
			// We expect the configuration to be invalid
		}

		// Check that selenium server did not start up
		assertFalse(
				"Selenium server started up, but should not with invalid configuration.",
				SeleniumServer.getInstance().isStarted());
	}

	/**
	 * Test an invalid configuration through main(String[] args).
	 */
	public void testMainInvalidConfiguration() {
		String[] args = new String[] { INVALID_ARGUMENT };

//		SeleniumServer seleniumServer = SeleniumServer.getInstance();
//		seleniumServer.reset();

		try {
			SeleniumServer.main(args);
			fail("Configuration with invalid configuration did not fail correctly. This may be perhaps configuration validation is not happening correctly.");
		} catch (InvalidConfigurationException ex) {
			// We expect the configuration to be invalid
		}

		// Check that selenium server did not start up
		assertFalse(
				"Selenium server started up, but should not with invalid configuration.",
				SeleniumServer.getInstance().isStarted());
	}
	
	/**
	 * Test a valid configuration through system properties.
	 */
	public void testConfigurationSystemProperties() {
		System.setProperty("selenium.debugMode", "true");
		
//		SeleniumServer existingSeleniumServer  = SeleniumServer.getInstance();
		
//		existingSeleniumServer.stop();
		
//		SeleniumServer seleniumServer = new SeleniumServer();
//		seleniumServer.reset();
		
		
		Configuration configuration = new ArgumentsConfiguration(new String[0]);
		seleniumServer.start(configuration);
		
		SeleniumConfiguration seleniumConfiguration = seleniumServer.getSeleniumConfiguration();
		
		assertTrue("selenium debug mode is not set after setting system property", seleniumConfiguration.isDebugMode());
		System.setProperty("selenium.debugMode", "false");
	}
	
	/**
	 * Test a valid configuration through arguments.
	 */
	public void testConfigurationArgumentsProxyInjectionMode() {
		String[] args = new String[] { "-proxyInjectionMode" };

//		SeleniumServer existingSeleniumServer  = SeleniumServer.getInstance();
		
//		existingSeleniumServer.stop();
		
//		SeleniumServer seleniumServer = new SeleniumServer();
		seleniumServer.start(args);
		
		SeleniumConfiguration seleniumConfiguration = seleniumServer.getSeleniumConfiguration();
		
		assertTrue("selenium server did not startup correctly with configuration", seleniumServer.isStarted());
		assertTrue("selenium server did not set proxy injection mode correctly", seleniumConfiguration.isProxyInjectionMode());
	}
}
