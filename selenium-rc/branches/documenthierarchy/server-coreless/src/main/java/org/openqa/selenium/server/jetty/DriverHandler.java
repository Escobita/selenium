package org.openqa.selenium.server.jetty;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.browser.BrowserType;
import org.openqa.selenium.server.client.DriverClient;
import org.openqa.selenium.server.client.Session;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.StringRemoteCommandResult;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.proxy.InjectionManager;

/**
 * Independent implementation for a selenium driver handler to handle driver specific logic.
 * 
 * @author Matthew Purland
 */
public class DriverHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(DriverHandler.class);

	private SessionManager sessionManager;

	private SeleniumConfiguration seleniumConfiguration;

	private InjectionManager injectionManager;

	private Lock handleLock = new ReentrantLock();
	
	private StringBuffer logMessagesBuffer = new StringBuffer();

	public DriverHandler(SeleniumConfiguration seleniumConfiguration,
			SessionManager sessionManager, InjectionManager injectionManager) {
		this.seleniumConfiguration = seleniumConfiguration;
		this.sessionManager = sessionManager;
		this.injectionManager = injectionManager;
	}

	private void handleLogMessages(String logData) {
		String logMessages = ""; //grepStringsStartingWith("logLevel=", logData);
		if (logMessages == null) {
			return;
		}
		logMessages = "\t" + logMessages.replaceAll("\n", "\t"); // put a tab in front of all the
		// messages
		logMessages = logMessages.replaceFirst("\t$", "");
		logMessagesBuffer.append(logMessages);
		logger.info(" >>>> " + logMessages);
	}

	/**
	 * Extract the posted data from an incoming request, stripping away a piggybacked data
	 * 
	 * @param inputStream
	 *            The input stream to read the posted data from
	 * @param parameterMap
	 *            Parameter map to check if postedData was a parameter
	 * @return a string containing the posted data (with piggybacked log info stripped)
	 * @throws IOException
	 * @throws SocketTimeoutException when a socket is timed out while reading from the stream
	 */
	private String readPostedData(InputStream inputStream, Map parameterMap)
			throws IOException {
		// if the request was sent as application/x-www-form-urlencoded, we can get the decoded data
		// right away...
		// we do this because it appears that Safari likes to send the data back as
		// application/x-www-form-urlencoded
		// even when told to send it back as application/xml. So in short, this function pulls back
		// the data in any
		// way it can!
		if (getParameter("postedData", parameterMap) != null) {
			return getParameter("postedData", parameterMap);
		}

		StringBuffer sb = new StringBuffer();
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		InputStreamReader r = new InputStreamReader(bis, "UTF-8");
		int c;
		String postedData = "";
		try {
			while ((c = r.read()) != -1) {
				sb.append((char) c);
			}
			postedData = sb.toString();
		} catch (SocketTimeoutException e) {
			logger.debug("Socket timeout while reading from input stream " + inputStream);
			throw e;
		}

		//String postedData = getStringUTF8FromInputStream(inputStream);
		

		// we check here because, depending on the Selenium Core version you have, specifically the
		// selenium-testrunner.js,
		// the data could be sent back directly or as URL-encoded for the parameter "postedData"
		// (see above). Because
		// firefox and other browsers like to send it back as application/xml (opposite of Safari),
		// we need to be prepared
		// to decode the data ourselves. Also, we check for the string starting with the key because
		// in the rare case
		// someone has an outdated version selenium-testrunner.js, which, until today (3/25/2007)
		// sent back the data
		// *un*-encoded, we'd like to be as flexible as possible.
		if (postedData.startsWith("postedData=")) {
			postedData = postedData.substring(11);
			postedData = URLDecoder.decode(postedData, "UTF-8");
		}

		return postedData;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean handle(String contextPath, String queryString,
			Map parameterMap, Method method, WebRequest webRequest,
			WebResponse webResponse, OutputStream outputStream)
			throws IOException {
		Thread.currentThread().setName("Thread-" +  Thread.currentThread().getId());
		
		// Create print writer out of output stream so we can write to it...
		// @todo Abstract away into WebHandler to pass correct UTF PrintWriter
		Writer out = new OutputStreamWriter(outputStream, "UTF-8");
		InputStream requestInputStream = webRequest.getInputStream();
		String requestURL = webRequest.getRequestURL();

		Map<String, String> commandParametersMap = new HashMap<String, String>();
		boolean requestWasHandled = false;
		CommandResult commandResult = null;

		String command = getParameter("cmd", parameterMap);
		String commandId = getParameter("commandId", parameterMap);
		String sequenceId = getParameter("sequenceNumber", parameterMap);
		String sessionId = getParameter("sessionId", parameterMap);
		String seleniumStart = getParameter("seleniumStart", parameterMap);
		String seleniumUnload = getParameter("seleniumUnload", parameterMap);
		String seleniumModalDialog = getParameter("modalDialog", parameterMap);
		String loggingParam = getParameter("logging", parameterMap);
		String javaScriptStateParam = getParameter("state", parameterMap);
		
		boolean isLogging = "true".equals(loggingParam);
		boolean hasJavaScriptState = "true".equals(javaScriptStateParam);
		boolean browserStart = "true".equals(seleniumStart);
		boolean browserUnload = "true".equals(seleniumUnload);
		boolean modalDialog = "true".equals(seleniumModalDialog);
		boolean hasReadTimedOut = false;
		
		String postedData = "";
		
		// If unloading just set OK
		// Sometimes a browser (IE in specific) does not send a 
		// a timeout correctly.  The below fix is to not read
		// posted data for specific events because the stream
		// will be dead
//		if (browserUnload) {
//			postedData = "OK";
//		}
//		// If starting just set START
//		else if (browserStart) {
//			postedData = "START";
//		}
//		// Otherwise, read posted data
//		else {
			try {
				postedData = readPostedData(requestInputStream, parameterMap);
			}
			// If the read times out
			catch (IOException ex) {
				hasReadTimedOut = true;
				// FIXME hack for now
				postedData = "TIMEOUT";
			}
//		}
		// Setup the command parameters map
		for (Object parameterNameObject : parameterMap.keySet()) {
			String parameterName = (String) parameterNameObject;
			String parameterValue = getParameter(parameterName, parameterMap);
			commandParametersMap.put(parameterName,	parameterValue);
		}

		// Array of parameters to remove if they exist
		String[] parametersToRemove = new String[] { "cmd", "sessionId",
		/*
		 * "seleniumWindowName", "localFrameAddress", "uniqueId", "seleniumStart"
		 */};

		// Remove parameters from the map
		for (int i = 0; i < parametersToRemove.length; i++) {
			if (commandParametersMap.containsKey(parametersToRemove[i])) {
				commandParametersMap.remove(parametersToRemove[i]);
			}
		}

//		if (browserUnload) {
//			handleLock.lock();
//		}
		
		logger.info(
				"\n============================================================\n"
				+ "Handling " + method + " request " + requestURL + "?" + queryString + "\n"
				+ "Posted Data: " + postedData + "\n"
				+ " command: " + command + "\n"
				+ " sessionId: " + sessionId + "\n"
				+ "============================================================\n");
		
		// Get the session (if it exists)
		Session session = sessionManager.getSessionFromSessionId(sessionId);

		if (session != null) {
			// A logging request
			if (isLogging) {
				//handleLogMessages(postedData);
				commandResult = new StringRemoteCommandResult("\r\n\r\n");
			}
			// A command from a driver client
			else if (command != null) {
				commandResult = session.handleCommand(command, commandParametersMap);
			}
			// A command result from browser
			// It must have a sequenceId
			else if (sequenceId != null || browserStart || browserUnload) {
				String windowName = getParameter("seleniumWindowName",
						parameterMap);
				String frameAddress = getParameter("localFrameAddress",
						parameterMap);
				String uniqueId = getParameter("uniqueId", parameterMap);
				String windowTitle = getParameter("seleniumTitle", parameterMap);
				String frameName = getParameter("seleniumFrameName",
						parameterMap);
				String frameId = getParameter("seleniumFrameId", parameterMap);
				
				// @todo Validate input data.  Don't process result unless params are correct.
				
				commandResult = session.handleResult(postedData, commandParametersMap, uniqueId, sequenceId, windowName, windowTitle, frameAddress, frameName, frameId, browserStart, browserUnload, modalDialog, hasJavaScriptState);
			}
			// Unknown request
			else {
				// @todo should be throwing an error?
				logger.debug("Bad request: Must have command or sequenceId");
			}
		}
		// Session is not valid
		else {
			// Received a command to create the new browser session?
			// Handle the one command that needs to be executed out of a command handler
			if ("getNewBrowserSession".equals(command)) {
				//String browserString = commandParametersMap.get("browserString");
				//String startURL = commandParametersMap.get("startURL");
				String browserString = commandParametersMap.get("1");
				String startURL = commandParametersMap.get("2");

				BrowserType browserType = BrowserType
						.getBrowserType(browserString);

				if (browserType != null) {
					logger.info("Creating new session for browser type "
							+ browserType + " at " + startURL);
					session = sessionManager.createSession(browserType,
							startURL);
					// Return OK,sessionId
					commandResult = new OKCommandResult(session.getSessionId());
				} else {
					logger.warn("browserString was invalid or not specified.");
				}
			}
			else {
				// FIXME, we should be throwing an error if the session cannot be found
				// http://wiki.openqa.org/display/SRC/Specifications+for+Selenium+Remote+Control+Client+Driver+Protocol
				logger.debug("Bad request: Must have valid session.  You must call 'getNewBrowserSession' to create a new session.");
			}
		}

// 		// The browser is sending, have browser client handler handle it.
//		// OR...the browser has just loaded and is acknowledging its sessionId.
//		// If we have logging as a parameter, handle the log messages
//		if (isLogging) {
//
//		} 
//		else if (session != null
//				&& (method.equals(Method.POST) || browserStart)) {
//
//	
//			commandResult = session.handleResult(postedData, commandParametersMap, uniqueId, windowName, windowTitle, frameAddress, frameName, browserStart, browserUnload, modalDialog, hasJavaScriptState);
//		}
//		// The client driver is sending, have driver client handler handle it.
//		else if (method.equals(Method.GET) && command != null) {
//			// boolean commandWasValid = true;
//			Thread.currentThread().setName("Driver client GET " + Thread.currentThread().getId());
//
//			// Map<String, String> invalidArgumentsMap = null;
//			//			
//			// try {
//			// SeleniumCommandTranslator.getInvalidArgumentsMap(command, commandParametersMap);
//			// }
//			// catch (RuntimeException ex) {
//			// logger.error(ex);
//			// }
//			// if (invalidArgumentsMap != null && !invalidArgumentsMap.isEmpty()) {
//			// logger.warn("Command " + command + " not a valid command.");
//			// commandWasValid = false;
//			// }
//
//			// }
//			// else {
//			// requestWasHandled = true;
//			//				
//			// Set<String> invalidArgumentsMapKeySet = invalidArgumentsMap.keySet();
//			//				
//			// for (String invalidArgument : invalidArgumentsMapKeySet) {
//			// String invalidArgumentReason = invalidArgumentsMap.get(invalidArgument);
//			//					
//			// out.write("Argument " + invalidArgument + " was invalid because: " +
//			// invalidArgumentReason);
//			// }
//			// }
//		} 
//		else if (session == null) {
//
//			logger.warn("Session is invalid.  You must call 'getNewBrowserSession' to create a new session.");
//		} 
//		else {
//			logger.info("Not handling " + requestURL + "?" + queryString);
//		}

		// if the session is no longer valid, remove it from the session manager
		if ((session != null) && (!session.isValid()))
		{
			sessionManager.removeSession(sessionId);
		}
		
		// Only set the request to handled if the command was handled
		if (commandResult != null) {
			requestWasHandled = true;
			logger.debug("writing command result: " + commandResult.getCommandResult());
			
			// Print the command result such as OK,something
			// Must not be println otherwise trailing \r\n occurs in results
			// in client driver
			out.write(commandResult.getCommandResult());
			logger.debug("wrote command result");
		}

		// Flush the output to the underlying output stream
		out.flush();

		logger.debug("handled");
		Thread.currentThread().setName("Thread-" +  Thread.currentThread().getId());
		return requestWasHandled;
	}
}
