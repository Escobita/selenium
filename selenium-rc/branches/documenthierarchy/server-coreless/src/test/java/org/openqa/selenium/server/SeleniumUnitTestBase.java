package org.openqa.selenium.server;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.configuration.DefaultSeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.jetty.ModifiedIO;
import org.openqa.selenium.server.jetty.SeleniumDependencyManager;
import org.openqa.selenium.server.jetty5.Jetty5StaticContentHandler;

/**
 * Unit test base class to provide creation of objects.
 * 
 * @author Matthew Purland
 */
public abstract class SeleniumUnitTestBase extends TestCase {
	private static Logger logger = Logger.getLogger(SeleniumUnitTestBase.class);

	private SeleniumDependencyManager seleniumDependencyManager;

	private SeleniumConfiguration seleniumConfiguration;

	public SeleniumConfiguration getSeleniumConfiguration() {
		return seleniumConfiguration;
	}

	public SeleniumDependencyManager getSeleniumDependencyManager() {
		return seleniumDependencyManager;
	}

	public void setUp() {
		seleniumConfiguration = new DefaultSeleniumConfiguration();

		seleniumDependencyManager = new SeleniumDependencyManager(
				seleniumConfiguration);

		seleniumDependencyManager.setUp(new ModifiedIO());
	}
}
