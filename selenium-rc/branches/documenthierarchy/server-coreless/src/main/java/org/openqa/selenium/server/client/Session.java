package org.openqa.selenium.server.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.util.FileUtils;
import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.browser.launchers.BrowserLauncher;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;

/**
 * Session that connects a client driver {@link DriverClient} instance to many
 * {@link BrowserClient} browser instance. This allows a client driver to issue
 * commands to the server and maintain a "session" between a driver issuing
 * commands and a browser window.
 * 
 * A session cannot be created (should not) until the driver client has sent a
 * command to open a new browser session, and the browser has responded and
 * identified itself with a uniqueId.
 * 
 * @todo In order to be a valid session, all browser clients in the session must
 *       be opened from the same parent window.
 * 
 * @todo Improve client driver API to issue a clientDriverId to maintain
 *       identification between client drivers to allow many browser instances
 *       to client drivers.
 * 
 * @author Matthew Purland
 */
public class Session {
	private static Logger logger = Logger.getLogger(Session.class);

	private DriverClient driverClient;

	private Map<String, String> javascriptStateInitializersMap = new HashMap<String, String>();

	private String sessionId = "";

	private BrowserType browserType;

	// The browser launcher to contact
	private BrowserLauncher browserLauncher;

	private boolean isValid = false;
	
	private SeleniumConfiguration seleniumConfiguration;

	private WindowManager windowManager;
	
	private List<File> tempFilesList;
	
	/**
	 * Construct a session from the driver client. A session must contain at
	 * least a single browser client in order to be qualified as a session.
	 * 
	 * @param driverClient
	 *            The driver client
	 */
	public Session(SeleniumConfiguration seleniumConfiguration, BrowserType browserType, BrowserLauncher browserLauncher,
			DriverClient driverClient, WindowManager windowManager) {
		this.driverClient = driverClient;
		this.browserType = browserType;
		this.browserLauncher = browserLauncher;
		this.sessionId = createNewSessionId();
		this.seleniumConfiguration = seleniumConfiguration;
		this.windowManager = windowManager;
		this.isValid = true;
	}

	/**
	 * Create a new session id and return it.
	 * 
	 * @return Returns a new session id based on the current time.
	 */
	protected String createNewSessionId() {
		return Long.toString(System.currentTimeMillis() % 1000000);
	}

	/**
	 * Get the driver client for the session.
	 * 
	 * @return Returns the driver client.
	 */
	public DriverClient getDriverClient() {
		return driverClient;
	}

	/**
	 * Get the browser type.
	 * 
	 * @return Returns the browser type.
	 */
	public BrowserType getBrowserType() {
		return browserType;
	}

	/**
	 * Gets the browser launcher from the browser client.
	 * 
	 * @return Returns the browser launcher.
	 */
	public BrowserLauncher getBrowserLauncher() {
		return browserLauncher;
	}

	/**
	 * Get the session id.
	 * 
	 * @return Returns the session id.
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Check whether session is valid to use.
	 * 
	 * @return Returns true if the session is valid to use.
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * Get the window manager.
	 * 
	 * @return Returns the window manager.
	 */
	public WindowManager getWindowManager() {
		return windowManager;
	}

	/**
	 * Close the session. Close any associated clients and invalidate the
	 * session.
	 */
	public void close() {
		boolean reusingBrowserSessions = seleniumConfiguration.isBrowserSessionReuse();

		driverClient.close();
		
		if (reusingBrowserSessions) {
			//setCurrentBrowserClient(null);
		}
		else {
			// Invalidate the session, it should no longer be used
			isValid = false;
			
			// @todo Should we flush output to the launcher before it is closed?
			browserLauncher.close();
	
			windowManager.close();
		}
		
		// @todo Remove and clear all temp files
	}
	
	private String extractVarName(String jsInitializer) {
		int x = jsInitializer.indexOf('=');
		if (x == -1) {
			// apparently a method call, not an assignment
			// for 'browserBot.recordedAlerts.push("lskdjf")',
			// return 'browserBot.recordedAlerts':
			x = jsInitializer.lastIndexOf('(');
			if (x == -1) {
				throw new RuntimeException("expected method call, saw "
						+ jsInitializer);
			}
			x = jsInitializer.lastIndexOf('.', x - 1);
			if (x == -1) {
				throw new RuntimeException("expected method call, saw "
						+ jsInitializer);
			}
		}
		return jsInitializer.substring(0, x);
	}

	private String grepStringsStartingWith(String pattern, String s) {
		String[] lines = s.split("\n");
		StringBuffer sb = new StringBuffer();
		for (String line : lines) {
			if (line.startsWith(pattern)) {
				sb.append(line.substring(pattern.length())).append('\n');
			}
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}

	public void handleJavaScriptState(//Document document,
			String uniqueId,
			String stateData) {
		String jsInitializers = grepStringsStartingWith("state:", stateData);
		if (jsInitializers == null) {
			return;
		}
		for (String jsStateInitializer : jsInitializers.split("\n")) {
			String jsVarName = extractVarName(jsStateInitializer);
			// injectionManager.saveJsStateInitializer(session, uniqueId, jsVarName, jsInitializer);
			saveJsStateInitializer(//document,
					uniqueId, jsVarName, jsStateInitializer);
		}
	}	
	
	public String createJavascript(Window window) {
		StringBuffer sb = new StringBuffer(restoreJsStateInitializer());

		// @todo Change to have window tell you if it is default
		if (!Window.DEFAULT_WINDOW_NAME.equals(window.getName())) {
			sb.append("setSeleniumWindowName(unescape('");
			try {
				sb.append(URLEncoder.encode(window.getName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("URLEncoder failed: " + e);
			}
			sb.append("'));");
		}
		
		return sb.toString();
	}

	public void saveJsStateInitializer(//Document document,
			String uniqueId, String jsVarName, String jsStateInitializer) {

// FIXME is this really needed now??? - Matt
//		if (!browserClient.getUniqueId().equals(uniqueId)) {
//			logger.debug("Before saving JavaScript state, new unique id seen "
//					+ uniqueId + " clearing js state map.");
//			javascriptStateInitializersMap.clear();
//		}

		logger.debug("Saving JavaScript state for browser client " + this + " "
				+ jsVarName + ": " + jsStateInitializer);

		StringBuffer sb = new StringBuffer("if (uniqueId!='");
		sb.append(uniqueId).append("') {").append(jsStateInitializer).append(
				"}");
		javascriptStateInitializersMap.put(jsVarName, sb.toString());
	}

	public String restoreJsStateInitializer() {
		if (javascriptStateInitializersMap.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (String jsVarName : javascriptStateInitializersMap.keySet()) {
			String jsStateInitializer = javascriptStateInitializersMap
					.get(jsVarName);
			sb.append(jsStateInitializer).append('\n');
			logger.debug("Restoring JavaScript state for browser client "
					+ this + ": key=" + jsVarName + ": " + jsStateInitializer);
		}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Session (id=" + sessionId 
			+ ", isValid=" + isValid
			+ ", windowManager=" + windowManager
			+ ", driverClient=" + driverClient + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + getSessionId().hashCode();

		return result;
	}
	
	public CommandResult handleCommand(String command, Map<String, String> commandParametersMap) {
		//DriverClient driverClient = getDriverClient();
		CommandResult commandResult = null;
		
		Thread.currentThread().setName("Driver COMMAND " + command + " " + Thread.currentThread().getId());
		
		if (driverClient != null) {
			// Passed "" will be blank because there is no result yet...
			commandResult = driverClient.handleCommand(this, command, "", commandParametersMap);
		} else {
			logger.warn("Driver client for session " + this
					+ " is null.  Closing session...");
			this.close();
		}
		
		return commandResult;
	}
	
	public CommandResult handleResult(String result, Map<String, String> commandParametersMap, String uniqueId, String sequenceId, String windowName, String windowTitle, String frameAddress, String frameName, String frameId, boolean browserStart, boolean browserUnload, boolean modalDialog, boolean hasJavaScriptState) {
		CommandResult commandResult = null;
		
		Thread.currentThread().setName("Browser result " + Thread.currentThread().getId());
		
		logger.info("uniqueId=" + uniqueId 
				+ ", frameAddress=" + frameAddress
				+ ", windowName=" + windowName
				);
		
		// If we have javascript state, then handle it
		if (hasJavaScriptState) {
			handleJavaScriptState(uniqueId, result);
		}

		if (result == null || "".equals(result) || hasJavaScriptState) {
			//commandResult = new StringRemoteCommandResult("\r\n\r\n");
			result = "\r\n\r\n";
		}
		//else {
			commandResult = windowManager.handleResult(result, commandParametersMap, uniqueId, windowName, windowTitle, frameAddress, frameName, frameId, browserStart, browserUnload, modalDialog, hasJavaScriptState);
		//}
 			
		return commandResult;
	}
	
    /**
     * Retrieves the temp files for the specified sessionId, or <code>null</code> if there are no such files. 
     **/
    public List<File> getTempFiles() {
    	if (tempFilesList == null) { // first time we call it
    		tempFilesList = new ArrayList<File>();
    	}
        return tempFilesList;
    }
    
    public File downloadFile(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL <" + urlString + ">, " + e.getMessage());
        }
        File outputFile = FileUtils.getFileUtils().createTempFile("se-",".file",null);
        Project p = new Project();
        // @todo Add log listener
        //p.addBuildListener(new AntJettyLoggerBuildListener(log));
        Get g = new Get();
        g.setProject(p);
        g.setSrc(url);
        g.setDest(outputFile);
        g.execute();
        return outputFile;
    }
    
    public void addLocatorStrategy(String locator, String locatorCode) {
    	
    }
}
