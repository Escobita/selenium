package org.openqa.selenium.server.configuration;

import org.apache.commons.configuration.Configuration;
import org.openqa.selenium.server.SeleniumTestBase;

/**
 * Test case to test that valid configuration for the
 * {@link SeleniumValidatableConfiguration} work correctly.
 * 
 * @author Matthew Purland
 */
public class SeleniumValidatableConfigurationTest extends SeleniumTestBase {
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Assert the given configuration to be valid.
	 * 
	 * @param configuration
	 *            The configuration
	 * 
	 * @throws InvalidConfigurationException
	 */
	public void assertValidConfiguration(Configuration configuration) {
		try {
			// Start the selenium server with the specified configuration
			start(configuration);
		}
		catch (InvalidConfigurationException ex) {
			getSeleniumServer().stop();
			
			throw ex;
		}
	}

	/**
	 * Assert the given configuration to be invalid.
	 * 
	 * @param configuration
	 *            The configuration
	 */
	public void assertInvalidConfiguration(Configuration configuration) {
		boolean hasFailed = false;
		
		try {
			start(configuration);
			
			hasFailed = true;
		} catch (InvalidConfigurationException ex) {
			// We expect for this configuration to be invalid
		}
		
		if (hasFailed) {
			getSeleniumServer().stop();
			fail("The configuration given is expected to be invalid.");
		}
	}

	/**
	 * Test the forcedBrowserMode argument for configuration.
	 */
	public void testForcedBrowserModeConfiguration() {
		String[] args = new String[] { SeleniumConfigurationOption.FORCED_BROWSER_MODE.getOptionArgument() };
		
		Configuration configuration = new ArgumentsConfiguration(args);
		
		// Should not throw an InvalidConfigurationException
		assertValidConfiguration(configuration);
	}

	// @todo Add invalid test for assertInvalidConfiguration for a bad argument
}
