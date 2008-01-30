package org.openqa.selenium.server.jetty5;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpContext;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.IO;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.SeleniumServerException;
import org.openqa.selenium.server.browser.HtmlIdentifierManager;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.jetty.AbstractSeleniumWebServer;
import org.openqa.selenium.server.jetty.DriverHandler;
import org.openqa.selenium.server.jetty.ModifiedIO;
import org.openqa.selenium.server.jetty.ProxyHandler;
import org.openqa.selenium.server.jetty.ResultsHandler;
import org.openqa.selenium.server.jetty.SeleniumDependencyManager;
import org.openqa.selenium.server.jetty.StaticContentHandler;
import org.openqa.selenium.server.jetty.WebHandler;
import org.openqa.selenium.server.proxy.InjectionManager;

public class Jetty5SeleniumWebServer extends AbstractSeleniumWebServer {
	private static Logger logger = Logger
			.getLogger(Jetty5SeleniumWebServer.class);

	private Server server;

	/**
	 * Construct a new default instance with a default port.
	 * 
	 * @param seleniumServer
	 *            The selenium server instance
	 */
	public Jetty5SeleniumWebServer(SeleniumServer seleniumServer, SeleniumDependencyManager seleniumDependencyManager) {
		super(seleniumServer, seleniumDependencyManager);
		logger.info("Starting Jetty 5 selenium web server...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure() throws SeleniumServerException {
		super.configure();

		SeleniumConfiguration seleniumConfiguration = getSeleniumServer()
				.getSeleniumConfiguration();

		int port = getPort();

		server = new Server();
		SocketListener socketListener = new SocketListener();
		socketListener.setMaxIdleTimeMs(600000);
		 socketListener.setMaxThreads(500);
		socketListener.setPort(port);
		server.addListener(socketListener);

		HttpContext root = new HttpContext();
		root.setContextPath("/");

		ModifiedIO jetty5ModifiedIO = new ModifiedIO(IO.bufferSize);
		SeleniumDependencyManager seleniumDependencyManager = getSeleniumDependencyManager();
		
		seleniumDependencyManager.setUp(jetty5ModifiedIO);

		InjectionManager injectionManager = seleniumDependencyManager.getInjectionManager();
		SessionManager sessionManager = seleniumDependencyManager.getSessionManager();
		ResultsHandler resultsHandler = seleniumDependencyManager.getResultsHandler();
		DriverHandler driverHandler = seleniumDependencyManager.getDriverHandler();
		StaticContentHandler staticContentHandler = seleniumDependencyManager.getStaticContentHandler();
		
		// Create web handler to drive main handlers
		WebHandler webHandler = new WebHandler();

		// @todo doesn't need it...
		ProxyHandler proxyHandler = new ProxyHandler();
		Jetty5ProxyHandler jettyProxyHandler = new Jetty5ProxyHandler(
				webHandler, proxyHandler, seleniumConfiguration,
				injectionManager, jetty5ModifiedIO);
		root.addHandler(jettyProxyHandler);

		// ProxyHandler proxyHandler;
		// if (customProxyHandler == null) {
		// proxyHandler = new ProxyHandler();
		// root.addHandler(proxyHandler);
		// } else {
		// proxyHandler = customProxyHandler;
		// root.addHandler(proxyHandler);
		// }

		server.addContext(root);

		HttpContext context = new HttpContext();
		context.setContextPath(SELENIUM_SERVER_PATH);
		context.setMimeMapping("xhtml", "application/xhtml+xml");

		// @todo Add configuration to pass instead

		// String overrideJavascriptDir = System.getProperty("selenium.javascript.dir");
		// if (overrideJavascriptDir != null) {
		// staticContentHandler.addStaticContent(new FsResourceLocator(new
		// File(overrideJavascriptDir)));
		// }

		Jetty5StaticContentHandler jettyStaticContentHandler = new Jetty5StaticContentHandler(
				webHandler, staticContentHandler);

		// if (logOutFileName==null) {
		// logOutFileName = System.getProperty("selenium.log.fileName");
		// }
		// if (logOutFileName != null) {
		// setLogOut(logOutFileName);
		// }

		context.addHandler(jettyStaticContentHandler);
		// context.addHandler(new SingleTestSuiteResourceHandler());

		
		Jetty5ResultsHandler jettyResultsHandler = new Jetty5ResultsHandler(
				webHandler, resultsHandler);
		context.addHandler(jettyResultsHandler);

		// Associate the SeleniumDriverResourceHandler with the /selenium-server/driver context
		HttpContext driverContext = new HttpContext();
		driverContext.setContextPath(SELENIUM_SERVER_DRIVER_PATH);
		// @todo Place all object creation and injection in another class that the web server can
		// call

		Jetty5DriverHandler jettyDriverHandler = new Jetty5DriverHandler(
				webHandler, driverHandler);
		driverContext.addHandler(jettyDriverHandler);
		
		server.addContext(context);
		server.addContext(driverContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startWebServer() throws SeleniumServerException, Exception {
		server.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void stopWebServer() throws SeleniumServerException, Exception {
		server.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isStopped() {
		// If it is not started then it is stopped
		return !server.isStarted();
	}

}
