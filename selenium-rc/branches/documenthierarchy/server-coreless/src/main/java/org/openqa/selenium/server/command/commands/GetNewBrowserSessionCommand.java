package org.openqa.selenium.server.command.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.client.DriverClient;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.command.AbstractLocalCommand;
import org.openqa.selenium.server.command.CommandValidationException;
import org.openqa.selenium.server.command.OKCommandResult;

/**
 * Local command to get a new browser session based on the browser type and url.
 * 
 * @author Matthew Purland
 */
public class GetNewBrowserSessionCommand extends
		AbstractLocalCommand<OKCommandResult> {
	private static Logger logger = Logger
			.getLogger(GetNewBrowserSessionCommand.class);

	public static final String GET_NEW_BROWSER_SESSION_COMMAND = "getNewBrowserSession";

	private DriverClient driverClient;

	private BrowserType browserType;

	private String startURL;
	
	/**
	 * Construct a new getNewBrowserSession command from the given browser manager, driver client,
	 * browser string, and startURL.
	 * 
	 * @param browserManager
	 *            The browser manager
	 * @param driverClient
	 *            The driver client
	 * @param browserString
	 *            The browser string
	 * @param startURL
	 *            The starting URL
	 */
	public GetNewBrowserSessionCommand(DriverClient driverClient, String browserString, String startURL) {
		this(BrowserType.getBrowserType(browserString), startURL);
	}
	
	/**
	 * Get parameter map for the two parameters of the command.
	 * 
	 * @param browserString The browser string
	 * @param startURL The starting URL
	 * @return
	 */
	private static Map<String, String> getParameterMap(String browserString, String startURL) {
		Map<String, String> commandParameterMap = new HashMap<String, String>();
		
		commandParameterMap.put("browserString", browserString);
		commandParameterMap.put("startURL", startURL);
		
		return commandParameterMap;
	}
	
	
	/**
	 * Construct a new getNewBrowserSession command from the given browser manager, driver client,
	 * browser type, and startURL.
	 * 
	 * @param browserManager
	 *            The browser manager
	 * @param driverClient
	 *            The driver client
	 * @param browserString
	 *            The browser type
	 * @param startURL
	 *            The starting URL
	 */
	public GetNewBrowserSessionCommand(BrowserType browserType, String startURL) {
		super(GET_NEW_BROWSER_SESSION_COMMAND, getParameterMap(browserType.toString(), startURL));
		
		this.driverClient = driverClient;
		this.browserType = browserType;
		this.startURL = startURL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateAndSetParameters() throws CommandValidationException {
		Map<String, String> commandParameterMap = getCommandParameterMap();
		
//		if (browserManager == null) {
//			throw new CommandValidationException("browser manager cannot be null");
//		}
//		
//		if (driverClient == null) {
//			throw new CommandValidationException("driver client cannot be null");
//		}
//		
//		if (browserType == null) {
//			throw new CommandValidationException(commandParameterMap.get("browserString")
//					+ " browser string is not a supported browser and path");
//		}
//
//		try {
//			// Just see if we can create a URL with the given url
//			new URL(startURL);
//		} catch (MalformedURLException ex) {
//			throw new CommandValidationException(startURL
//					+ " is not a valid URL", ex);
//		}
		

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OKCommandResult runCommand(Session session) {
		// Create a session from the driver client and the given browser type
		// Session session = sessionManager.createSession(driverClient, browserType,
		// browserLauncher);

		// Launch a new browser on the given session
//		BrowserLauncher browserLauncher = browserManager.launchNewBrowser(browserType, startURL);
//		
//		Session session = sessionManager.createSession(driverClient, browserType, browserLauncher);
//		
//
//		// Return OK,sessionId
//		OKCommandResult commandResult = new OKCommandResult(session
//				.getSessionId());

		return null;
	}
}
