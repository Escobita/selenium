package org.openqa.selenium.server;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.openqa.selenium.server.configuration.ArgumentsConfiguration;

public abstract class SeleniumTestBase extends TestCase {
	private SeleniumServer seleniumServer;

	/**
	 * Set up the selenium server.
	 */
	public void setUp() throws Exception {
		seleniumServer = new SeleniumServer();
	}

	/**
	 * Tears down the server.
	 */
	public void tearDown() throws Exception {
		seleniumServer.stop();
	}

	/**
	 * Get the selenium server instance.
	 * 
	 * @return Return the selenium server instance.
	 */
	public SeleniumServer getSeleniumServer() {
		return seleniumServer;
	}

	/**
	 * Start the selenium server with the given configuration.
	 * 
	 * @param configuration
	 *            The configuration
	 */
	public void start(Configuration configuration) {
		seleniumServer.start(configuration);
	}

	/**
	 * Start the selenium server with a default configuration. Configuration will assume defaults.
	 */
	public void start() {
		seleniumServer.start();
	}
}
