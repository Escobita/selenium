package org.openqa.selenium.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.configuration.ArgumentsConfiguration;
import org.openqa.selenium.server.configuration.DefaultSeleniumConfiguration;
import org.openqa.selenium.server.configuration.InvalidConfigurationException;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationListener;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;
import org.openqa.selenium.server.configuration.SeleniumValidatableConfiguration;
import org.openqa.selenium.server.configuration.ValidatableConfiguration;
import org.openqa.selenium.server.jetty.SeleniumDependencyManager;
import org.openqa.selenium.server.jetty.SeleniumWebServer;
import org.openqa.selenium.server.jetty5.Jetty5SeleniumWebServer;

/**
 * @todo Add javadocs from old SeleniumServer here
 * 
 * @author Matthew Purland
 */
public class SeleniumServer {
	private static Logger logger = Logger.getLogger(SeleniumServer.class);

	private static final String SELENIUM_PROPERTIES_FILE = "selenium.properties";

	// Placeholder
	private static final String SELENIUM_VERSION = "2.0";

	// Singleton instance of the server
	private static SeleniumServer seleniumServerInstance;

	// The configuration
	private Configuration configuration;

	// The selenium configuration instance
	private SeleniumConfiguration seleniumConfiguration;
	
	// The dependency manager that manages creating instances
	private SeleniumDependencyManager seleniumDependencyManager;

	// Start status of the server
	private boolean isStarted = false;

	// Selenium Web server instance
	private SeleniumWebServer seleniumWebServer;

	// Server shutdown hook
	private Thread shutdownHook;

	/**
	 * Get the configuration.
	 * 
	 * @return Returns the configuration for the server.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Get the selenium configuration.
	 * 
	 * @return Returns the selenium configuration for the server.
	 */
	public SeleniumConfiguration getSeleniumConfiguration() {
		return seleniumConfiguration;
	}

	/**
	 * Get the version of the selenium server.
	 * 
	 * @return Returns the version string of the selenium server.
	 */
	public String getVersion() {
		return SELENIUM_VERSION;
	}

	/**
	 * Check if the selenium server is started.
	 * 
	 * @return Returns true if the server is started; false otherwise;
	 */
	public boolean isStarted() {
		return !seleniumWebServer.isStopped();
	}

	/**
	 * Set whether the server has been started.
	 * 
	 * @param isStarted
	 *            True if the server has been started; false otherwise.
	 * 
	 * @throws SeleniumServerException
	 *             when the server cannot be started or stopped or other server related error
	 */
	private void setStarted(boolean startServer) throws SeleniumServerException {
		if (seleniumWebServer != null) {
			if (!startServer) {
				// Stop the server
				if (seleniumWebServer != null) {
					try {
						seleniumWebServer.stop();
					} finally {
						SessionManager sessionManager = seleniumDependencyManager.getSessionManager();
						sessionManager.stopAllBrowsers();
						try {
							if (shutdownHook != null) {
								Runtime.getRuntime().removeShutdownHook(shutdownHook);
							}
						} catch (IllegalStateException e) {
						} // if we're shutting down, it's too late for that!
					}
				}
			} else {
				seleniumWebServer.start();
			}
		}
	}

	/**
	 * Set the configuration of the server to the specified configuration.
	 * 
	 * @param configuration
	 *            The configuration
	 */
	private void setConfiguration(Configuration configuration) {
		this.configuration = configuration;

		// If the configuration is not null. Such as when the configuration fails.
		if (configuration != null) {
			SeleniumConfigurationListener listener = new SeleniumConfigurationListener(
					configuration);
			seleniumConfiguration = new DefaultSeleniumConfiguration(
					configuration, listener);
		} else {
			this.seleniumConfiguration = null;
		}
	}
	
	private Map getModifiedSystemProperties() {
		final String SYSTEM_PROPERTY_SELENIUM_PREFIX = "selenium.";
		
		Map systemPropertiesMap = System.getProperties();
		Map<String, Object> modifiedSystemPropertiesMap = new HashMap<String, Object>();
		
		Set systemPropertiesKeySet = systemPropertiesMap.keySet();
		
		for (Object systemProperty : systemPropertiesKeySet) {
			if (systemProperty instanceof String) {
				String systemPropertyString = (String) systemProperty;
				if (systemPropertyString.startsWith(SYSTEM_PROPERTY_SELENIUM_PREFIX)) {
					String newSystemProperty = systemPropertyString.substring(systemPropertyString.indexOf(SYSTEM_PROPERTY_SELENIUM_PREFIX) + SYSTEM_PROPERTY_SELENIUM_PREFIX.length());
					modifiedSystemPropertiesMap.put(newSystemProperty, systemPropertiesMap.get(systemProperty));
				}
			}
		}
		
		return modifiedSystemPropertiesMap;
	}

	/**
	 * Configure the selenium server with the given configuration.
	 * 
	 * @param configuration
	 *            The configuration
	 * 
	 * @throws InvalidConfigurationException
	 *             when a configuration problem occurs
	 */
	private void configure(Configuration configuration)
			throws InvalidConfigurationException {
		CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

		// Add the given passed configuration to the composite configuration for
		// validation
		compositeConfiguration.addConfiguration(configuration);

		File seleniumPropertiesFile = new File(SELENIUM_PROPERTIES_FILE);

		// If a properties file exists
		if (seleniumPropertiesFile != null && seleniumPropertiesFile.canRead()) {
			// @todo enable properties configuration
			try {
				// Allow user to specify selenium.properties file to override values
				// of system.
				compositeConfiguration
						.addConfiguration(new PropertiesConfiguration(
								SELENIUM_PROPERTIES_FILE));
			} catch (ConfigurationException ex) {
				logger
						.error(
								"There was a configuration problem with the selenium.properties configuration.",
								ex);
			}
		}
		
		// Validate the configuration before starting the server
		try {
			// Don't validate SystemConfiguration
			Map systemPropertiesMap = getModifiedSystemProperties();
			
			// Only add to configuration when we have system properties
			if (!systemPropertiesMap.isEmpty()) {
				compositeConfiguration.addConfiguration(new MapConfiguration(systemPropertiesMap));
			}
			setConfiguration(compositeConfiguration);

			ValidatableConfiguration seleniumValidatableConfiguration = new SeleniumValidatableConfiguration(
					getSeleniumConfiguration());

			seleniumValidatableConfiguration.validate();
		} catch (InvalidConfigurationException ex) {
			setConfiguration(null);
			logger.error("There was a configuration problem.", ex);
			throw ex;
		}
	}

	/**
	 * Start the server with the specified configuration. The given configuration will be validated
	 * before the server can be started.
	 * 
	 * @param configuration
	 *            The configuration
	 * @throws InvalidConfigurationException
	 *             when a configuration problem occurs
	 */
	public void start(Configuration configuration)
			throws InvalidConfigurationException {

		try {
			// Configure the selenium server with the given configuration
			configure(configuration);

			// It is configured now...get the selenium configuration and choose what server to start
			boolean isJetty5 = true;

			// Add our shutdown hook
			shutdownHook = new Thread(new ShutdownHook(this));
			shutdownHook.setName("SeleniumServerShutDownHook");
			Runtime.getRuntime().addShutdownHook(shutdownHook);

			seleniumDependencyManager = new SeleniumDependencyManager(getSeleniumConfiguration());

			if (isJetty5) {
				seleniumWebServer = new Jetty5SeleniumWebServer(this, seleniumDependencyManager);
			} else {
				// seleniumWebServer = new Jetty6SeleniumWebServer(this);
			}
			setStarted(true);
			logger.info("Selenium Server startup successful...");
		} catch (InvalidConfigurationException ex) {
			logger
					.error("Selenium Server startup failed.  See error messages above.");
			throw ex;
		} catch (SeleniumServerException ex) {
			logger.error("Selenium Server startup failed...", ex);
			throw ex;
		}
	}

	/**
	 * Start the server with the specified map for configuration. The given configuration will be
	 * validated before the server can be started.
	 */
	public void start(SeleniumConfiguration seleniumConfiguration) {
		start(seleniumConfiguration.getConfiguration());
	}

	/**
	 * Start the server with the specified map for configuration. The given configuration will be
	 * validated before the server can be started.
	 */
	public void start(Map<String, String> configurationMap) {
		start(new MapConfiguration(configurationMap));
	}

	/**
	 * Stop the server.
	 */
	public void stop() {
		setStarted(false);
	}
	
	/**
	 * Start the server with the specified configuration as args.
	 * 
	 * @throws InvalidConfigurationException
	 *             when a configuration problem occurs
	 */
	public void start(String[] args) throws InvalidConfigurationException {
		// Create a configuration from the given string arguments
		Configuration argumentConfiguration = new ArgumentsConfiguration(args);

		// Start the selenium server instance to use our specified arguments
		// configuration
		start(argumentConfiguration);
	}
	
	/**
	 * Start the server with no specified configuration.  Will use defaults.
	 */
	public void start() {
		start(new String[0]);
	}

	/**
	 * Start the server with the specified configuration. This will usually only be called by
	 * main(String[]).
	 * 
	 * @param configuration
	 *            The configuration
	 * 
	 * @throws InvalidConfigurationException
	 *             when a configuration problem occurs
	 */
	public static void startServer(String[] args)
			throws InvalidConfigurationException {
		SeleniumServer seleniumServer = getInstance();

		seleniumServer.start(args);
	}

	/**
	 * Stops the running instance of the selenium server.
	 */
	public static void stopServer() {
		SeleniumServer seleniumServer = getInstance();

		seleniumServer.stop();
	}

	/**
	 * Get the single instance of the selenium server.
	 * 
	 * @return Returns the singleton instance of {@link SeleniumServer}.
	 */
	public static SeleniumServer getInstance() {
		if (seleniumServerInstance == null) {
			seleniumServerInstance = new SeleniumServer();
		}

		return seleniumServerInstance;
	}

	/**
	 * Starts up the server on the specified port (or default if no port was specified) and then
	 * starts interactive mode if specified.
	 * 
	 * @param args
	 *            Commands entered such as "-port" followed by a number, or "-interactive"
	 */
	public static void main(String[] args) {
		startServer(args);
	}
	
	protected void finalize() throws Throwable {
		stop();
	}

	private class ShutdownHook implements Runnable {
		SeleniumServer server;

		ShutdownHook(SeleniumServer server) {
			this.server = server;
		}

		public void run() {
			logger.info("Shutting down...");
			server.stop();
		}
	}
	
	public static int getDefaultPort() {
		return (Integer) SeleniumConfigurationOption.PORT.getDefaultValue();
	}
}
