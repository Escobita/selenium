package org.openqa.selenium.server.jetty6;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.SeleniumServerException;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;
import org.openqa.selenium.server.configuration.SeleniumConfigurationListener.PropertyConfigurationChangeEvent;
import org.openqa.selenium.server.configuration.SeleniumConfigurationListener.PropertyConfigurationChangeListener;
import org.openqa.selenium.server.jetty.AbstractSeleniumWebServer;
import org.openqa.selenium.server.jetty.DriverHandler;
import org.openqa.selenium.server.jetty.InfoServlet;
import org.openqa.selenium.server.jetty.ResultsHandler;
import org.openqa.selenium.server.jetty.SeleniumWebServer;
import org.openqa.selenium.server.jetty.StaticContentHandler;

/**
 * Default implementation for {@link SeleniumWebServer}.
 * 
 * @author Matthew Purland
 */
public class Jetty6SeleniumWebServer extends AbstractSeleniumWebServer {
	private static Logger logger = Logger
			.getLogger(Jetty6SeleniumWebServer.class);

	private Server server;
	
	/**
	 * Construct a new default instance with a default port.
	 * 
	 * @param seleniumServer
	 *            The selenium server instance
	 */
	public Jetty6SeleniumWebServer(SeleniumServer seleniumServer) {
		super(seleniumServer);
		logger.info("Starting Jetty 6 selenium web server...");
	}

	/**
	 * {@inheritDoc}
	 */
	public void configure() {

		super.configure();
		
		SeleniumConfiguration configuration = getSeleniumServer().getSeleniumConfiguration();
		
		int port = getPort();
		
		server = new Server(port);

		// Add connector
		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setMaxIdleTime(60000);

		server.setConnectors(new Connector[] { connector });

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		server.setHandler(contexts);

		// Setup /selenium-server context
		Context seleniumServerContext = new Context(contexts,
				SELENIUM_SERVER_PATH);

		// Setup mime types
		MimeTypes mimeTypes = new MimeTypes();
		mimeTypes.addMimeMapping("xhtml", "application/xhtml+xml");
		seleniumServerContext.setMimeTypes(mimeTypes);

		seleniumServerContext.setResourceBase(".");
		seleniumServerContext.setClassLoader(Thread.currentThread()
				.getContextClassLoader());

		// @todo add slow mode for configuration
		// boolean serveSlowResources = configuration.isSlowMode();

		HandlerList contextHandlerList = new HandlerList();

		
		// Add static content handler
		// @todo Need to pass configuration for slow resources to static content handler
		StaticContentHandler seleniumContentHandler = new StaticContentHandler(
				false);
		Jetty6StaticContentHandler jettyStaticContentHandler = new Jetty6StaticContentHandler(
				seleniumContentHandler);
		// Set static content handler
		//seleniumServerContext.setHandler(jettyStaticContentHandler);
		
        ResultsHandler resultsHandler = new ResultsHandler();
        Jetty6ResultsHandler jettyResultsHandler = new Jetty6ResultsHandler(resultsHandler);
		
		contextHandlerList.addHandler(jettyStaticContentHandler);
		contextHandlerList.addHandler(jettyResultsHandler);
		seleniumServerContext.setHandler(contextHandlerList);
		
		
		Context seleniumServerInfoServletContext = new Context(contexts,
				SELENIUM_SERVER_INFO_SERVLET_PATH);
		// Add info servlet
		seleniumServerInfoServletContext.addServlet(new ServletHolder(
				new InfoServlet(seleniumServer)), "/*");

		// Must use ContextHandler. Not Context.
		// Add driver handler
		ContextHandler seleniumServerDriverContext = new ContextHandler(
				contexts, SELENIUM_SERVER_DRIVER_PATH);
		HandlerCollection driverHandlerCollection = new HandlerCollection();

		// @todo Place all object creation and injection in another class that the web server can call
		BrowserLauncherFactory browserLauncherFactory = new BrowserLauncherFactory(configuration);
		CommandFactory commandFactory = new CommandFactory();
		
		SessionManager sessionManager = new SessionManager(configuration, browserLauncherFactory, commandFactory);
		//BrowserManager browserManager = new BrowserManager(configuration, browserLauncherFactory);
		
		DriverHandler seleniumDriverHandler = new DriverHandler(sessionManager);

		Jetty6DriverHandler jettyDriverHandler = new Jetty6DriverHandler(
				seleniumDriverHandler);

		driverHandlerCollection.addHandler(jettyDriverHandler);
		seleniumServerDriverContext.addHandler(driverHandlerCollection);

		logger.info("Configured Jetty 6 Selenium Web Server successfully...");
	}
}
