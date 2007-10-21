package org.openqa.selenium.server.jetty;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.SeleniumServerException;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;
import org.openqa.selenium.server.configuration.SeleniumConfigurationListener.PropertyConfigurationChangeEvent;
import org.openqa.selenium.server.configuration.SeleniumConfigurationListener.PropertyConfigurationChangeListener;

public abstract class AbstractSeleniumWebServer implements SeleniumWebServer {

	private static Logger logger = Logger
			.getLogger(AbstractSeleniumWebServer.class);

	// Path to the selenium server
	public static final String SELENIUM_SERVER_PATH = "/selenium-server";

	// Path to the selenium server driver
	public static final String SELENIUM_SERVER_DRIVER_PATH = "/selenium-server/driver";

	// Path to the info servlet
	public static final String SELENIUM_SERVER_INFO_SERVLET_PATH = "/selenium-server/info";

	private SeleniumServer seleniumServer;

	private SeleniumDependencyManager seleniumDependencyManager;
	
	private int port;

	public AbstractSeleniumWebServer(SeleniumServer seleniumServer,
			SeleniumDependencyManager seleniumDependencyManager) {
		this.seleniumServer = seleniumServer;
		this.seleniumDependencyManager = seleniumDependencyManager;
	}

	/**
	 * Port change listener...can be used by either implementation.
	 */
	protected PropertyConfigurationChangeListener portChangeListener = new SeleniumWebServerPropertyConfigurationChangeListener(
			this) {
		private Logger logger = Logger.getLogger(this.getClass());

		@Override
		public void propertyChange(PropertyConfigurationChangeEvent event) {
			SeleniumWebServer seleniumWebServer = getSeleniumWebServer();

			logger
					.info("Restarting selenium web server due to port configuration change...");

			// Restart the web server when the port is changed
			seleniumWebServer.restart();
		}
	};

	protected abstract class SeleniumWebServerPropertyConfigurationChangeListener
			implements PropertyConfigurationChangeListener {
		private SeleniumWebServer seleniumWebServer;

		public SeleniumWebServerPropertyConfigurationChangeListener(
				SeleniumWebServer seleniumWebServer) {
			this.seleniumWebServer = seleniumWebServer;
		}

		public SeleniumWebServer getSeleniumWebServer() {
			return seleniumWebServer;
		}

		abstract public void propertyChange(
				PropertyConfigurationChangeEvent event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void configure() throws SeleniumServerException {
		SeleniumConfiguration configuration = seleniumServer
				.getSeleniumConfiguration();

		// Add port listener when the port configuration is changed
		// @todo Problem occurs when configuration is changed, the server is restarted while
		// requests are still being serviced...
		configuration.addConfigurationListener(
				SeleniumConfigurationOption.PORT, portChangeListener);

		this.port = configuration.getPort();
	}

	/**
	 * Start the actual web server...
	 */
	protected abstract void startWebServer() throws SeleniumServerException,
			Exception;

	/**
	 * Stop the actual web server...
	 */
	protected abstract void stopWebServer() throws SeleniumServerException,
			Exception;

	/**
	 * {@inheritDoc}
	 */
	public void start() throws SeleniumServerException {
		try {
			// Don't configure Jetty until the last possible second
			configure();

			startWebServer();

			logger.info("Selenium Web Server started successfully...");
		} catch (Exception ex) {
			throw new SeleniumServerException(
					"Problem when starting the selenium web server.", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() throws SeleniumServerException {
		try {
			stopWebServer();

			SeleniumConfiguration configuration = seleniumServer
					.getSeleniumConfiguration();

			if (configuration != null) {
				// Remove the listener
				configuration.removeConfigurationListener(
						SeleniumConfigurationOption.PORT, portChangeListener);
			}
			logger.info("Selenium Web Server stopped successfully...");
		} catch (Exception ex) {
			throw new SeleniumServerException(
					"Problem when stopping the selenium web server.", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void restart() throws SeleniumServerException {
		logger.info("Selenium Web Server restarting...");

		if (!isStopped()) {
			stop();

			// @todo should we be calling destroy here?
			// server.destroy();
		}

		// Start the server with new configuration
		start();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Get the selenium server.
	 * 
	 * @return Returns the selenium server.
	 */
	protected SeleniumServer getSeleniumServer() {
		return seleniumServer;
	}

	public SeleniumDependencyManager getSeleniumDependencyManager() {
		return seleniumDependencyManager;
	}

}
