package org.openqa.selenium.server.jetty;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.HtmlIdentifierManager;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.proxy.InjectionManager;

public class SeleniumDependencyManager {
	private static Logger logger = Logger
	.getLogger(SeleniumDependencyManager.class);
	
	private SeleniumConfiguration seleniumConfiguration;

	private HtmlIdentifierManager htmlIdentifierManager;

	private ModifiedIO modifiedIO;

	private SessionManager sessionManager;

	private InjectionManager injectionManager;

	private BrowserLauncherFactory browserLauncherFactory;

	private CommandFactory commandFactory;
	private ResultsHandler resultsHandler;
	private DriverHandler driverHandler;
	private StaticContentHandler staticContentHandler;
	
	public SeleniumDependencyManager(
			SeleniumConfiguration seleniumConfiguration) {
		this.seleniumConfiguration = seleniumConfiguration;
	}

	public void setUp(ModifiedIO modifiedIO) {
		logger.info("Setting up Selenium Dependency Manager...");
		htmlIdentifierManager = new HtmlIdentifierManager(seleniumConfiguration);
		this.modifiedIO = modifiedIO;
		
		// Set up dependency objects for web server...
		browserLauncherFactory = new BrowserLauncherFactory(
				seleniumConfiguration);
		commandFactory = new CommandFactory(seleniumConfiguration);
		//			
		sessionManager = new SessionManager(seleniumConfiguration,
				browserLauncherFactory, commandFactory);
		
		injectionManager = new InjectionManager(
				seleniumConfiguration, sessionManager, htmlIdentifierManager,
				modifiedIO);
		
		staticContentHandler = new StaticContentHandler(
				seleniumConfiguration, injectionManager, modifiedIO);
		
		resultsHandler = new ResultsHandler();
		
		driverHandler = new DriverHandler(
					seleniumConfiguration, sessionManager, injectionManager);
	}

	public BrowserLauncherFactory getBrowserLauncherFactory() {
		return browserLauncherFactory;
	}

	public CommandFactory getCommandFactory() {
		return commandFactory;
	}

	public HtmlIdentifierManager getHtmlIdentifierManager() {
		return htmlIdentifierManager;
	}

	public InjectionManager getInjectionManager() {
		return injectionManager;
	}

	public ModifiedIO getModifiedIO() {
		return modifiedIO;
	}

	public SeleniumConfiguration getSeleniumConfiguration() {
		return seleniumConfiguration;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public DriverHandler getDriverHandler() {
		return driverHandler;
	}

	public ResultsHandler getResultsHandler() {
		return resultsHandler;
	}

	public StaticContentHandler getStaticContentHandler() {
		return staticContentHandler;
	}
}
