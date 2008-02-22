package org.openqa.selenium.server.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.browser.BrowserType.Browser;
import org.openqa.selenium.server.browser.launchers.BrowserLauncher;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.browser.launchers.LauncherUtils;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.commands.CommandFactory;
import org.openqa.selenium.server.command.handler.DriverCommandHandler;
import org.openqa.selenium.server.command.runner.LocalCommandRunner;
import org.openqa.selenium.server.command.runner.ProxyCommandRunner;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;

/**
 * Manages sessions for clients.
 * 
 * @author Matthew Purland
 */
public class SessionManager {

	private static Logger logger = Logger.getLogger(SessionManager.class);

	// Last session id
	private String lastSessionId;

	// 
	private Map<String, Session> sessionIdToSessionMap = new HashMap<String, Session>();

	// Reusing browser sessions
	private Map<Browser, String> browserTypeToSessionIdMap = new HashMap<Browser, String>();

	private SeleniumConfiguration seleniumConfiguration;

	private BrowserLauncherFactory browserLauncherFactory;

	private CommandFactory commandFactory;

	public SessionManager(SeleniumConfiguration seleniumConfiguration,
			BrowserLauncherFactory browserLauncherFactory,
			CommandFactory commandFactory) {
		this.browserLauncherFactory = browserLauncherFactory;
		this.seleniumConfiguration = seleniumConfiguration;
		this.commandFactory = commandFactory;
	}

	/**
	 * Gets the session from the given session id.
	 * 
	 * @param sessionId
	 *            The session id
	 * @return Returns the session; null if not found.
	 */
	public Session getSessionFromSessionId(String sessionId) {
		Session session = sessionIdToSessionMap.get(sessionId);

		return session;
	}

	public boolean addSession(Session session) {
		// The session id
		String sessionId = session.getSessionId();

		boolean addSuccessful = false;
		
		if (!sessionIdToSessionMap.containsKey(sessionId)) {
			logger.info("Adding session (" + session + ") to the session map.");
			setLastSessionId(sessionId);
			addSuccessful = sessionIdToSessionMap.put(session.getSessionId(), session) != null;
		} else {
			logger.info("Could not add session (" + session
					+ ") because it already exists.");
		}
		
		return addSuccessful;
	}

	public boolean removeSession(String sessionId) {
		logger.info("Removing sessionId (" + sessionId
				+ ") from the session map.");

		return sessionIdToSessionMap.remove(sessionId) != null;
	}

	/**
	 * Get the last session id to be created from the session manager.
	 * 
	 * @return Returns the last session id.
	 */
	public String getLastSessionId() {
		return lastSessionId;
	}

	/**
	 * Set the last session id.
	 * 
	 * @param lastSessionId
	 *            The last session id tos et
	 */
	private void setLastSessionId(String lastSessionId) {
		this.lastSessionId = lastSessionId;
	}

	/**
	 * Get a new browser session with the browser type and starting URL.
	 * 
	 * @todo Redo launchNewBrowser to not return a session since it doesn't
	 *       create a session at all.
	 * 
	 * @param driverClient
	 *            The driver client to create the session for
	 * 
	 * @param browserType
	 *            The browser type
	 * @param startURL
	 *            The starting URL
	 * 
	 * @return Returns a new session if the browser was launched successfully.
	 */
	protected void launchNewBrowser(Session session, BrowserLauncher browserLauncher,
			BrowserType browserType, String startURL) {
		logger.info("Launching new browser (browserType=" + browserType
				+ ", startURL=" + startURL + ")");

		boolean multiWindowMode = seleniumConfiguration.isMultiWindowMode();
		boolean debugMode = seleniumConfiguration.isDebugMode();

		browserLauncher.launchRemoteSession(startURL, multiWindowMode,
				debugMode);
		
		// Wait for the newly launched browser to launch and report back as
		// ready
		Window foundWindow = session.getWindowManager().waitForAnyWindowToLoad(seleniumConfiguration.getTimeout());
		
		if (foundWindow == null) {
			// FIXME throw proper exception
			throw new RuntimeException("problem launching a new browser window");
		}
		
		// Select the loaded window
		boolean selectedSuccessfully = session.getWindowManager().selectWindow(foundWindow);
	}

	/**
	 * Creates a driver client by setting up a local command runner and a driver
	 * command handler for the driver client.
	 * 
	 * @param sessionManager
	 *            The session manager
	 * @return Returns the new driver client.
	 */
	public DriverClient createDriverClient(CommandFactory commandFactory) {
		// logger.info("Session does not have a valid driver client.")

		LocalCommandRunner localCommandRunner = new LocalCommandRunner();
		// RemoteCommandRunner remoteCommandRunner = new RemoteCommandRunner();
		ProxyCommandRunner proxyCommandRunner = new ProxyCommandRunner();

		DriverCommandHandler driverCommandHandler = new DriverCommandHandler(
				seleniumConfiguration, localCommandRunner, /* remoteCommandRunner, */
				proxyCommandRunner, commandFactory);
		DriverClient driverClient = new DriverClient(driverCommandHandler);

		return driverClient;
	}

	protected Session createSession(BrowserType browserType,
			BrowserLauncher browserLauncher, DriverClient driverClient) {
		
		WindowManager windowManager = new WindowManager();
		
		Session session = new Session(seleniumConfiguration, browserType, browserLauncher,
				driverClient, windowManager);
		
		windowManager.setSession(session);

		// Initialize the browser launcher with the given session
		browserLauncher.init(session);

		addSession(session);

		return session;
	}

	/**
	 * Creates a new session from the browser type and starting URL. This will
	 * launch a new browser.
	 * 
	 * @todo Should remove launchNewBrowser and have it be indepependent of
	 *       creating a session
	 * 
	 * @param browserType
	 *            The browser type to launch
	 * @param startURL
	 *            The starting URL to launch the browser at
	 * @return
	 */
	public Session createSession(BrowserType browserType, String startURL) {
		if (seleniumConfiguration.isProxyInjectionMode()) {
			if (browserType.getBrowser().equals(Browser.IEXPLORE)) {
				logger
						.info("Browser type is *iexplore, changing to *piiexplore");
				browserType = new BrowserType(Browser.PI_IEXPLORE, browserType
						.getPathToBrowser());
			} else if (browserType.getBrowser().equals(Browser.FIREFOX)) {
				logger.info("Browser type is *firefox, changing to *pifirefox");
				browserType = new BrowserType(Browser.PI_FIREFOX, browserType
						.getPathToBrowser());
			}
		}
		
		if (seleniumConfiguration.isOptionSpecified(SeleniumConfigurationOption.FORCED_BROWSER_MODE)) {
			String browserString = seleniumConfiguration.getForcedBrowserMode();
			logger.info("Browser type forced to " + browserString);
			browserType = new BrowserType(new Browser(browserString));
		}

		BrowserLauncher browserLauncher = browserLauncherFactory
				.getBrowserLauncher(browserType);

		DriverClient driverClient = createDriverClient(commandFactory);

		boolean reusingBrowserSessions = seleniumConfiguration
				.isBrowserSessionReuse();

		Session session = null;

		// Reusing browser sessions...
		if (reusingBrowserSessions) {
			String sessionId = browserTypeToSessionIdMap.get(browserType
					.getBrowser());

			// No session for that browser, we have to create one...
			if (sessionId == null) {
				session = createSession(browserType, browserLauncher,
						driverClient);

				// Add it to the map so we can find it later
				browserTypeToSessionIdMap.put(browserType.getBrowser(), session.getSessionId());
				
				launchNewBrowser(session, browserLauncher, browserType, startURL);
				
				boolean multiWindowMode = seleniumConfiguration.isMultiWindowMode();
				boolean debugMode = seleniumConfiguration.isDebugMode();
				boolean proxyInjectionMode = seleniumConfiguration.isProxyInjectionMode();
				
				Map<String, String> commandParameterMap = new HashMap<String, String>();
				commandParameterMap.put("url", LauncherUtils.getDefaultRemoteSessionUrl(startURL, session, multiWindowMode, debugMode, proxyInjectionMode, 0, "localhost"));
				RemoteCommand openCommand = new RemoteCommand(
						"open", commandParameterMap);
				session.getWindowManager().run(openCommand);
			}
			// Session exists...get from map
			else {
				session = sessionIdToSessionMap.get(sessionId);
			}
		}
		// Not reusing browser sessions...create a new session
		else {
			session = createSession(browserType, browserLauncher, driverClient);
			
			launchNewBrowser(session, browserLauncher, browserType, startURL);
		}

		// Send a setContext command to the browser before continuing
		Map<String, String> commandParameterMap = new HashMap<String, String>();
		commandParameterMap.put("sessionId", session.getSessionId());
		RemoteCommand setContextRemoteCommand = new RemoteCommand(
				"setContext", commandParameterMap);

		session.getWindowManager().run(setContextRemoteCommand);

		return session;
	}

	/**
	 * Stop all of the browsers. This will invalidate each session.
	 */
	public void stopAllBrowsers() {
		Collection<Session> sessionSet = sessionIdToSessionMap.values();

		// @todo Do we want to remove them from the map too?
		for (Session session : sessionSet) {
			session.close();
		}
	}
}
