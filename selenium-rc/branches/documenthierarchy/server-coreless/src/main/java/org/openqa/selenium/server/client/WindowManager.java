package org.openqa.selenium.server.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.command.AbstractCommand;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.StringRemoteCommandResult;
import org.openqa.selenium.server.command.commands.CommandFactory.SupportedProxyCommand;

/**
 * Manages a stateful list of windows.
 * 
 * @author Matthew Purland
 */
public class WindowManager implements WindowListener {
	private static Logger logger = Logger.getLogger(WindowManager.class);

	private List<Window> windowList = new ArrayList<Window>();
	
	private Map<String, Document> uniqueIdToDocumentMap = new ConcurrentHashMap<String, Document>();

	private Stack<Window> currentWindowStack = new Stack<Window>();
	
	private BlockingQueue<Document> startedDocumentQueue = new LinkedBlockingQueue<Document>();
	private BlockingQueue<Window> startedWindowQueue = new LinkedBlockingQueue<Window>();

	private boolean isValid = true;
	
	private Session session;
	
	private Lock unloadLock = new ReentrantLock();

	public void setSession(Session session) {
		this.session = session;
	}
	
	/**
	 * Find the document from the given unique id.
	 * 
	 * @param uniqueId
	 *            The unique id
	 * @return Returns the document if it exists in the session; null otherwise.
	 */
	public Document findDocumentByUniqueId(String uniqueId) {		
		return uniqueIdToDocumentMap.get(uniqueId);
	}
	
	/**
	 * Set the current window for proxy injection mode.
	 * 
	 * @param window
	 *            The window to set as current
	 */
	protected void setCurrentWindow(Window window) {
		// @todo why are we synchronizing on windowList, not currentWindowStack?
		synchronized (currentWindowStack) {
			logger.debug("Setting new current window " + window);
			
			//if (!window.equals(getCurrentWindow())) {
				currentWindowStack.push(window);
			//}
		}
	}
	
	/**
	 * Get the current window.
	 */
	public Window getCurrentWindow() {
		// Lock to prevent getting current window while
		// a window is unloading to correct current window stack
		unloadLock.lock();
		try {
			synchronized (currentWindowStack) {
				Window currentWindow = null;
				
				if (!currentWindowStack.isEmpty()) {
					currentWindow = currentWindowStack.peek();
				}
				
				return currentWindow;
			}
		}
		finally {
			unloadLock.unlock();
		}
	}
	
	/**
	 * Check if the session has a current window.
	 */
	protected boolean hasCurrentWindow() {
		synchronized (currentWindowStack) {
			return getCurrentWindow() != null 
				&& getCurrentWindow().isValid();
		}
	}
	
	public void addStartedWindow(Window window) {
		synchronized (startedWindowQueue) {
			if (!startedWindowQueue.contains(window)) {
				logger.debug("Adding started window " + window);
				startedWindowQueue.add(window);
			}
			else {
				logger.debug("Cannot add started window.  Already contains duplicate window " + window);
			}
		}
	}
	
	/**
	 * Add a window to the list of windows for the session.
	 * 
	 * @param window
	 *            The window to add
	 */
	protected void addWindow(Window window) {
		if (window == null) {
			throw new IllegalArgumentException("window cannot be null");
		}

		windowList.add(window);
		
		// if we don't have any current windows, add one
		if (currentWindowStack.isEmpty()) {
			setCurrentWindow(window);
		}
		// Otherwise, if we get a new client for with the same frame address and window location
		// as our current, we should use it as the current instead.  This likely means the 
		// current will be unloaded shortly.
//		else if (getCurrentBrowserClient().getWindowName().equals(browserClient.getWindowName())
//				// FIXME this is wrong....
//				&& getCurrentBrowserClient().getWindowName().equals(browserClient.getLocalFrameAddress())){
//			logger.info("The new browser client matched the window name and frame address of the current, resetting current to the new");
//			setCurrentBrowserClient(browserClient);
//		}
	}
	
	public void close() {
		for (Window window : windowList) {
			window.close();
		}
		
		isValid = false;
	}
	
	private Window findNextWindow() {
		Iterator<Window> currentWindowStackIterator = currentWindowStack.listIterator();
		Window foundWindow = null;
		
		while (foundWindow == null && currentWindowStackIterator.hasNext()) {
			Window window = currentWindowStackIterator.next();
		
			if (window.isValid()) {
				foundWindow = window;
			}
		}
		
		return foundWindow;
	}
	
	/**
	 * Select the given window by name.
	 * 
	 * @param window The window
	 * @return Returns true if the select window was successful; false otherwise.
	 */
	public boolean selectWindow(Window window) {
		return selectWindow(window.getName());
	}
	
	/**
	 * Select a window based on the following criteria.
	 * 
	 * <p>
	 * <ol>
	 * <li>Window name</li>
	 * <li>Window title</li>
	 * </ol>
	 * </p>
	 * 
	 * If the window name is the string "null" then it should follow that the
	 * original window should attempted to be selected.
	 * 
	 * @param windowName The window name, or title
	 * @return Returns true if the select window was successful; false otherwise.
	 */
	public boolean selectWindow(String windowName) {
		boolean selectWindowSuccessful = false;
		Window foundWindow = null;
		
		logger.debug("Trying to select window for windowName=" + windowName);
		
			// If the select window is trying to select the original window
			// instantiated by the browser
			if (windowName == null || "null".equals(windowName)) {
				foundWindow = getCurrentWindow();
				
				synchronized (currentWindowStack) {
					currentWindowStack.pop();
				}
				
				if (foundWindow != null) {
					logger.debug("Selecting window off current window stack " + foundWindow);
					selectWindowSuccessful = true;
				}
			}
			else {
				// First, try to match a window by name
				for (Window window : windowList) {
					foundWindow = window.findWindowByName(windowName);
		
					if (foundWindow != null && foundWindow.isValid()) {
						logger.debug("Selecting window from window list by name " + foundWindow);
						selectWindowSuccessful = true;
						break;
					}
				}
		
				// Second, try to match a window by title
				// Only try looking if we haven't matched by window name
				if (!selectWindowSuccessful) {
					for (Window window : windowList) {
						foundWindow = window.findWindowByTitle(windowName);
			
						if (foundWindow != null) {
							logger.debug("Selecting window from window list by title " + foundWindow);
							selectWindowSuccessful = true;
							break;
						}
					}
				}
			}
			
			if (selectWindowSuccessful) {
				logger.debug("Selecting window " + foundWindow
						+ " setting current window and setting current document to top, was looking for windowName=" + windowName);
				setCurrentWindow(foundWindow);
				foundWindow.setCurrentDocument(foundWindow.getDocument());
			}
			else {
				logger.debug("Could not select window, was looking for windowName=" + windowName);
			}

		return selectWindowSuccessful;
	}
	
	public List<String> getAllWindowNames() {
		List<String> windowNamesList = new ArrayList<String>();
		
		// Get all windows and sub window names of each window 
		for (Window window : windowList) {
			List<String> subWindowNamesList = window.getAllWindowNames();
			
			windowNamesList.addAll(subWindowNamesList);
			
//			if (!windowNamesList.contains(window.getName())) {
//				windowNamesList.add(browserClient.getWindowName());
//			}
		}
		
		return windowNamesList;
	}
	
	/**
	 * Wait for a window to load on the current session and timeout in the
	 * number of seconds. This method will block until a matching window is
	 * found, or until the specified timeout elapses.
	 * 
	 * If windowName is null then the first window to load will be matched.
	 * 
	 * @param windowName
	 * 			  The window name
	 * @param timeoutInMilliseconds
	 *            Timeout in milliseconds
	 * 
	 * @return Returns the window if the window was loaded and detected successfully;
	 *         null otherwise.
	 */
	public Window waitForWindowToLoad(String windowName, long timeoutInMilliseconds) {
		boolean loadedSuccessfully = false;
		Window foundWindow = null;
		
		long startTimeInMs = System.currentTimeMillis();
		long elapsedTime = 0;
		
		Set<Window> checkedWindows = new HashSet<Window>();

		// If given a default window name, wait for any window
		if ("".equals(windowName)) {
			windowName = null;
		}
		
		try {
			while (elapsedTime < timeoutInMilliseconds) {
				long timeRemaining = timeoutInMilliseconds - elapsedTime;
	
				logger.info("Waiting for window '" + windowName
						+ "' wait time elapsed: " + elapsedTime
						+ " wait time remaining: " + timeRemaining);
	
				Window startedWindow = null;
	
				try {
					startedWindow = startedWindowQueue.poll(timeRemaining,
							TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					logger.error("**************we were interrupted while waiting");
				}
				
				elapsedTime = System.currentTimeMillis() - startTimeInMs;
				
				if (startedWindow == null) {
					continue;
				}	
				logger.debug("Found started window: " + startedWindow);
				
				// if no window name or frame address was given, we match on any loaded client
				if (windowName == null) {
					foundWindow = startedWindow;
				}
				// otherwise, check both window name only if started window is valid
				else if (startedWindow.isValid()) {
					Window window = startedWindow.findWindowByName(windowName);
					
					if (window != null && window.isValid()) {
						// If our started window matches the window we tried to match by name
						if (startedWindow == window) {
							foundWindow = window;
						}
					}
					// Now match by title if we can
					else {
						logger.debug("Trying to find window by title " + windowName);
						foundWindow = startedWindow.findWindowByTitle(windowName);
					}
				}
				
				// If we found a matching document
				if (foundWindow != null)
				{
					logger.info("Found matching window " + foundWindow + " window name '" + windowName + "'");
					
					// We found the window, now wait for the default document to load.
					try {
						loadedSuccessfully = foundWindow.waitForDocumentToLoad(Document.DEFAULT_FRAME_ADDRESS, timeoutInMilliseconds);
						
						if (loadedSuccessfully) {
							logger.debug("Window was found.  Setting current window.");
							setCurrentWindow(foundWindow);
						}
						break;
					}
					catch (InterruptedException ex) {
						logger.debug("***** we were interrupted while waiting on found window " + foundWindow);
					}
				}
				// Only add a client that exists and hasn't unloaded
				else if (startedWindow != null) {
					// Add it back in the list if we didn't match it correctly
					if (!checkedWindows.contains(startedWindow)) {
						checkedWindows.add(startedWindow);
					}
				}	

			}
		} finally {
			// @todo this is making the assumption that we are not waiting on more then
			// one window at a time.  the goal is to only check each window once b/c
			// we were eating up processor checking out many times in a loop on the same windows
			startedWindowQueue.addAll(checkedWindows);
		}
		
		if (foundWindow == null) {
			logger.info("Waiting timed out waiting for window '" + windowName + "'");
		}

		return foundWindow;
	}
	
	protected boolean waitForCurrentWindowToLoad(long timeoutInMilliseconds) {
		long startTimeInMs = System.currentTimeMillis();
		long elapsedTime = 0;
		
		Window currentWindow = null;
		
		while (elapsedTime < timeoutInMilliseconds && currentWindow == null) {
			long timeRemaining = timeoutInMilliseconds - elapsedTime;

			logger.info("Waiting for current window wait time elapsed: " + elapsedTime
					+ " wait time remaining: " + timeRemaining);
			
			currentWindow = getCurrentWindow();
			elapsedTime = System.currentTimeMillis() - startTimeInMs;
			
			if (currentWindow == null) {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException ex) {
					// Exit the while loop...we were interrupted
					elapsedTime = timeoutInMilliseconds;
				}
			}
		}
		
		return currentWindow != null;
	}
	
	/**
	 * Wait for any window to load.
	 * 
	 * @param timeoutInMilliseconds Timeout in milliseconds
	 * @return Returns the window if a window was loaded; null otherwise.
	 */
	public Window waitForAnyWindowToLoad(long timeoutInMilliseconds) {
		return waitForWindowToLoad(null, timeoutInMilliseconds);
	}
	
	/**
	 * Wait for any document to load on the current window.
	 * 
	 * @param timeoutInMilliseconds Timeout in milliseconds
	 * @return returns true if the document was found successfully; false otherwise.
	 */
	public boolean waitForAnyDocumentToLoad(long timeoutInMilliseconds) {
		boolean waitForAnyDocumentToLoadSuccessful = false;
		long startTimeInMs = System.currentTimeMillis();
		long elapsedTime = 0;
		
			while (elapsedTime < timeoutInMilliseconds) {
				long timeRemaining = timeoutInMilliseconds - elapsedTime;
			
				logger.info("Waiting for any document to load wait time elapsed: " + elapsedTime
						+ " wait time remaining: " + timeRemaining);
				
				Document startedDocument = null;
				try {
					startedDocument = startedDocumentQueue.poll(timeRemaining, TimeUnit.MILLISECONDS);
					elapsedTime = System.currentTimeMillis() - startTimeInMs;
				}
				catch (InterruptedException ex) {
					logger.error("**************poll interrupted while waiting");	
				}
				
				if (startedDocument != null && startedDocument.isValid()) {
					Window window = findWindowContainingDocument(startedDocument);
					
					if (window != null) {
						logger.debug("Found matching document for any document " + startedDocument);
						waitForAnyDocumentToLoadSuccessful = true;
						
						// THIS WAS 
						
//						window.setCurrentDocument(startedDocument);
						window.addStartedDocument(startedDocument);
						try {
							window.waitForDocumentToLoad(startedDocument.getFrameAddress(), timeRemaining);
							
							setCurrentWindow(window);
							break;
						}
						catch (InterruptedException ex) {
							logger.error("**************window interrupted while waiting");	
						}
					}
				}
				
//				for (Window window : windowList) {
//					if (window != null && window.isValid()) {
//						waitForAnyDocumentToLoadSuccessful = window.waitForDocumentToLoad(null, timeRemaining);
//						
//						if (waitForAnyDocumentToLoadSuccessful) {
//							break;
//						}
//					}
						

			}
		
		return waitForAnyDocumentToLoadSuccessful;
	}
	
	/**
	 * Wait for the document matching frameAddress to load on the current window.
	 * 
	 * @param frameAddress The frame address
	 * @param timeoutInMilliseconds Timeout in milliseconds
	 * @return returns true if the document was found successfully; false otherwise.
	 */
	public boolean waitForDocumentToLoad(String frameAddress, long timeoutInMilliseconds) {
		boolean waitForDocumentToLoadSuccessful = false;
		long startTimeInMs = System.currentTimeMillis();
		long elapsedTime = 0;
		
		while (elapsedTime < timeoutInMilliseconds && waitForDocumentToLoadSuccessful == false) {
			long timeRemaining = timeoutInMilliseconds - elapsedTime;
			boolean currentWindowLoadedSuccessfully = waitForCurrentWindowToLoad(timeoutInMilliseconds);
		
			logger.info("Waiting for document to load wait time elapsed: " + elapsedTime
					+ " wait time remaining: " + timeRemaining);
			
			if (currentWindowLoadedSuccessfully) {
				Window currentWindow = getCurrentWindow();
				logger.info("Waiting for document on current window: " + currentWindow);
				
				if (currentWindow != null && currentWindow.isValid()) {
					try {
						waitForDocumentToLoadSuccessful = currentWindow.waitForDocumentToLoad(frameAddress, timeoutInMilliseconds);
					}
					catch (InterruptedException ex) {
						logger.debug("***** we were interrupted while waiting on current window " + currentWindow);
					}
				}
				
				elapsedTime = System.currentTimeMillis() - startTimeInMs;
			}
		}
		

		
		return waitForDocumentToLoadSuccessful;
	}
	
	/**
	 * Open a new URL in the current window.
	 * 
	 * @param url The url to open
	 */
	public CommandResult open(String url) {		
		Map<String, String> parametersMap = new HashMap<String, String>();
		parametersMap.put("1", url); // url
		
		RemoteCommand<CommandResult> openCommand = new RemoteCommand(
				SupportedProxyCommand.OPEN.getCommandName(), parametersMap);
//		openCommand.setWaitingType(RemoteCommand.WaitingType.DONT_WAIT);
		
		logger.debug("Performing open on url=" + url);
		
		// There are two cases for an open
		// 1. The open is being done with a frame already selected
		// 2. The open is being done from the top frame selected (window level)
		
		Window currentWindow = getCurrentWindow();
		Document currentDocument = null;
		String windowName = null;
		String frameAddress = Document.DEFAULT_FRAME_ADDRESS;

		if (currentWindow != null) {
			currentDocument = currentWindow.getCurrentDocument();
//			windowName = currentWindow.getName();
			
			if (currentWindow.getCurrentDocument() != null) {
				frameAddress = currentWindow.getCurrentDocument().getFrameAddress();
			}
		}
		
		logger.debug("Before open.  Will wait for windowName=" + windowName + " or frameAddress=" + frameAddress);
		
		CommandResult result = run(openCommand);
		
		// after the open command was sent and a result was returned, wait for a window to load
		// on the same name/location as we sent the open to
		if ((result != null) && "OK".equals(result.getCommandResult())) {
			logger.debug("Waiting for window to load after open");
			// @todo Change to use default selenium timeout 30s?
			final int timeoutInMilliseconds = 3000000;
			
			// FIXME we should be waiting on "top" frame if we're doing an open
//			loadedSuccessfully = windowManager.waitForLoad(timeoutInMs, 
//					window.getName(), window.getCurrentDocument().getFrameAddress());
			
			// Wait for a window to load on the same window as the current window
			boolean selectedSuccessfully = false;

			//unloadLock.lock();
			
			try {
			// Waiting for a frame
			if (currentWindow != null) {
				// Wait for frame to load
				if (currentWindow.isValid() && currentDocument != null) {
					try {
						selectedSuccessfully = currentWindow.waitForDocumentToLoad(frameAddress, timeoutInMilliseconds);		
					}
					catch (InterruptedException ex) {
						logger.debug("**** interrupted while waiting on current window after open " + currentWindow);
					}
				}
				
				// Waiting for a window
				if (!selectedSuccessfully) {
					Window foundWindow = null;
					
					if (windowName == null) {
						foundWindow = waitForAnyWindowToLoad(timeoutInMilliseconds);
					} 
					else {
						foundWindow = waitForWindowToLoad(windowName, timeoutInMilliseconds);
					}
					if (foundWindow != null) {
						selectedSuccessfully = selectWindow(foundWindow);
					}
				}
			}
			else {
//				selectedSuccessfully = waitForAnyDocumentToLoad(timeoutInMilliseconds);
				selectedSuccessfully = waitForDocumentToLoad(Document.DEFAULT_FRAME_ADDRESS, timeoutInMilliseconds);
			}

			
			if (selectedSuccessfully) {
				result = new OKCommandResult();
			}
			}
			finally {
				//unloadLock.unlock();
			}
		}
		else {
			logger.debug("Did not receive OK from open, not waiting for result.");
		}
		return result;
	}
	
	/**
	 * Get the title of the current window.
	 */
	public String getCurrentWindowTitle() {
		String windowTitle = null;
		
		Window currentWindow = getCurrentWindow();
		if (currentWindow != null) {
			windowTitle = currentWindow.getTitle();
		}
		
		return windowTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "WindowManager (" 
		// @todo we need to be careful with all list access for ConcurrentModificationExceptions for Lists throughout selenium
//		+ "windowList=" + windowList
		+ ", currentWindowStack=" + currentWindowStack
		+ ")";
	}
	
	/**
	 * Find a window containing the specified document.
	 * 
	 * @param document The document
	 * @return Returns the window containing the specified document; null if no window was found.
	 */
	public Window findWindowContainingDocument(Document document) {
		Window foundWindow = null;
		
		logger.debug("Finding window containing document " + document);
		for (Window window : windowList) {
			if (window.contains(document)) {
				logger.debug("Found window containing document, window " + window);
				foundWindow = window;
				break;
			}
		}
		
		return foundWindow;
	}
	
	/**
	 * Find a window containing the specified document unique id.
	 * 
	 * @param document The document
	 * @return Returns the window containing the specified document; null if no window was found.
	 */
	public Window findWindowContainingDocumentUniqueId(String uniqueId) {
		Window foundWindow = null;
		
		logger.debug("Finding window containing unique id " + uniqueId);
		Document document = findDocumentByUniqueId(uniqueId);
		
		if (document != null) {
			foundWindow = findWindowContainingDocument(document);
		}
		
		return foundWindow;
	}
	
	/**
	 * Find a window by name.
	 * 
	 * @param windowName The window name
	 * @return Returns the window if found; null if not found.
	 */
	public Window findWindowByName(String windowName) {
		Window foundWindow = null;
		
		for (Window window : windowList) {
			foundWindow = window.findWindowByName(windowName);
			
			if (foundWindow != null) {
				break;
			}
		}
		
		return foundWindow;
	}
	
	/**
	 * Find a window by title.
	 * 
	 * @param windowName The window name
	 * @return Returns the window if found; null if not found.
	 */
	public Window findWindowByTitle(String windowName) {
		Window foundWindow = null;
		
		for (Window window : windowList) {
			foundWindow = window.findWindowByName(windowName);
			
			if (foundWindow != null) {
				break;
			}
		}
		
		return foundWindow;
	}
	
	/**
	 * Respond to a remote client with the given output stream and command.
	 * 
	 * @param outputStream
	 *            The output stream
	 * @param command
	 *            The command
	 * @throws IOException
	 *             when an I/O exception occurs
	 */
	protected String respondToRemoteClient(RemoteCommand remoteCommand) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream(1000);

		try {
			// @todo Change to use UTF8 constant
			Writer writer = new OutputStreamWriter(buf, "UTF-8");
			if (remoteCommand != null) {
				writer.write(remoteCommand.getBrowserCommandResponse());
			}
			for (int pad = 998 - buf.size(); pad-- > 0;) {
				writer.write(" ");
			}
			writer.write("\015\012");
			writer.close();
		} catch (IOException ex) {
			logger.error(ex);
		}

		String bufString;
		
		try {
			bufString = buf.toString("UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			logger.error("Encoding browser command response to UTF8 not supported.", ex);
			bufString = buf.toString();
		}
		
		return bufString;
	}
	
	public CommandResult handleResult(String result, Map<String, String> commandParametersMap, String uniqueId, String windowName, String windowTitle, String frameAddress, String frameName, String frameId, boolean browserStart, boolean browserUnload, boolean modalDialog, boolean hasJavaScriptState) {		
		CommandResult commandResult = null;

		Document document = null;
		
		logger.debug("Handling result " + result + " finding window...");
		Window window = null;
		
		// Temporary solution to fix when adding windows
		// one will be added first before it can be found
		// again to avoid two threads adding the same window
		synchronized (this) {
		window = findWindowContainingDocumentUniqueId(uniqueId);
		
		if (window != null) {
			logger.debug("Found window containing document unique id " + uniqueId + " window " + window);
		}
		else {
			logger.debug("Could not find window containing document unique id.");
			window = findWindowByName(windowName);
		}
		// Couldn't find a window...add one
		if (window == null) {
			logger.debug("Window name '" + windowName + "' was not found.  Creating a new window.");
			
			// Use default window name
			if (windowName == null) {
				windowName = Window.DEFAULT_WINDOW_NAME;
			}
			
			window = new Window(windowName, null);
			window.addListener(this);
			addWindow(window);
			addStartedWindow(window);
		}				
		}
		
//		synchronized (window) {
		// Browser just started
		if (browserStart) {
			document = handleBrowserStart(window, uniqueId, frameAddress, frameName, frameId, windowTitle, modalDialog);
		}
		else {
			// First, try finding a matching document by our unique id
			document = findDocumentByUniqueId(uniqueId);
		}
		
			if (document != null) {
				if ("TIMEOUT".equals(result)) {
					logger.warn("Timeout occurred while reading posted data, shutting down document: " + document);
					document.close();
				}
				else if (document != null) {			
					logger.debug("handling command result on document unique id: " + uniqueId);
					
					// Use the found document as a starting point, but if this is a modal result
					// search up the document hierarchy until we find a running command.  The
					// assumption is that this will be the trigger that is launching the modal.
					// and what triggered the modal is who needs to handle this modal result.
					if (modalDialog && document.getRunningCommand() == null) {
						logger.debug("searching for trigger to modal dialog result");
						Document parent = document.getParentDocument();
						while (parent != null) {
							RemoteCommand<CommandResult> command = parent.getRunningCommand();
							String commandId = commandParametersMap.get("commandId");
							logger.debug("parent command: " + command + " result commandId: " + commandId);
							if ((command != null) && (commandId.equals(command.getCommandId()))) {
								document = parent;
								break;
							}
							parent = parent.getParentDocument();
						}
					}
					
					
					if (window != null) {
						logger.debug("Handling command result in window");
						// Handle any other results
						// commandResult = document.handleResult(result, commandParametersMap);
						
						commandResult = window.handleResult(session, document, result, commandParametersMap);
					}
					else {
						logger.debug("Not handling command result, document is not in a window???");
					}
					
					// Only handle the browser unload AFTER we have handled the result
					// This avoids closing documents shutting down remote command runner
					// and causing a blank command result and causing a hang
					// Browser just unloaded
					if (browserUnload) {
						handleBrowserUnload(document);
						//if ("UNLOAD".equals(commandResult.getCommandResult())) {
							commandResult = new OKCommandResult();
						//}
 					}
					
					logger.debug("Waiting to get next command to run for document " + document);
			//
					// @todo Should only be waiting for requests that need to wait for a command
					if (!modalDialog && !hasJavaScriptState) {
						RemoteCommand remoteCommand = document.getNextCommandToRun();
						
						// Should never be null...
						if (remoteCommand != null) {
							// FIXME WRONG!!! need to attach javascript before a command is run, not here
							// Only attach javascript if there was a window found

							
							logger.debug("Got next command from remote command runner.  Responding back to client for command " + remoteCommand);
							String responseToRemoteClient = respondToRemoteClient(remoteCommand);
				
							// Make sure the response isn't blank...
							if (!"".equals(responseToRemoteClient)) {
								logger
										.debug("Responding back to browser with the following: "
												+ responseToRemoteClient.trim());
								commandResult = new StringRemoteCommandResult(responseToRemoteClient);
							}
						}
						else {
							logger.debug("Popped null command from remote command runner? Bug!!!");
						}
					}
	//				
	//				return result;	
					logger.debug("command result after handling command result: " + commandResult);
				}
			}
			else {
				logger.warn("document not found cannot create new document for uniqueId=" + uniqueId);
			}
//		}
		return commandResult;
	}
	
	public Document handleBrowserStart(Window window, String uniqueId, String frameAddress, String frameName, String frameId, String seleniumTitle, boolean modalDialog) {
		Document document = null;
		
		// If we just loaded this document, then add it
		logger.debug("Browser start detected.  Attempting to add document.");
		
		// @todo get rid of these locks and push locking down into window
		unloadLock.lock();
		try {
			logger.debug("acquired lock");
			logger.debug("Before starting to add started document uniqueId=" + uniqueId + " to window " + window);
		
			document = window.addDocument(uniqueId, seleniumTitle, frameAddress, frameName, frameId);
			
			// @todo Add method in window manager to support onDocumentLoad from Window adddocument
			// will do the below calls
			startedDocumentQueue.add(document);
			uniqueIdToDocumentMap.put(uniqueId, document);
			logger.debug("Added started document " + document + " to window " + window);
		}
		finally {
			unloadLock.unlock();
			logger.debug("released unload lock");
		}
		
		return document;
	}
	
	public void handleBrowserUnload(Document document) {
		logger.debug("Browser unload detected.  Closing document " + document);
		
		// @todo get rid of these locks and push locking down into window
		unloadLock.lock();
		try {
			logger.debug("acquired unload lock");
			Window window = findWindowContainingDocument(document);
			
			logger.debug("Found window, will try to remove window from stack " + window);
			if (window != null) {
				// If we're unloading this document, close it
				// Close the document, invalidate all child documents etc
				window.closeDocument(document);
			}
			else {
				document.close();
			}
			
			startedDocumentQueue.remove(document);
		}
		finally {
			unloadLock.unlock();
			logger.debug("released unload lock");
		}
	}
	
	/**
	 * Check if there are any valid windows.
	 */
	public boolean hasValidWindows() {
		boolean hasValidWindow = false;
		
		for (Window window : windowList) {
			if (window.isValid()) {
				hasValidWindow = true;
				break;
			}
		}
		
		return hasValidWindow;
	}
	
	public CommandResult run(RemoteCommand<CommandResult> remoteCommand) {

		// @todo add stuff here
		int retries = 5;
		
		CommandResult result = null;
		Document currentDocument = null;
		while (result == null && retries > 0) {
			if (!isValid) {
				logger.debug("---shutting down run for command " + remoteCommand + " window manager has been closed");
				break;
			}
			
			logger.debug("Running remote command " + remoteCommand
					+ " on current window " + getCurrentWindow());	
			
			// Clear the document and window queue before running
			// each command.
			startedDocumentQueue.clear();
			startedWindowQueue.clear();

			if (getCurrentWindow() != null && getCurrentWindow().isValid()) {
				if (getCurrentWindow() != null /*&& remoteCommand != null && !"open".equals(remoteCommand.getCommand())*/) {
					// We have a remote command, create the javascript for it if it exists
					remoteCommand.setAttachedJavascript(session.createJavascript(getCurrentWindow()));
				}
				result = getCurrentWindow().run(remoteCommand);
			}
			
			if (result == null) {
				try {
					Thread.sleep(100);
					retries--;
				} catch (InterruptedException ex) {
					logger.error("Interrupted exception while trying to sleep when retrying run command: " + remoteCommand);
				}
			}
			else if (currentDocument != null) {
				startedDocumentQueue.remove(currentDocument);
			}
		} 
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void windowClosed(Window window) {
		// commented below out to avoid infinite loop
		//removeWindow(window);
//		currentWindowStack.remove(window);
		
		// If we just closed the window and it's the current window
		//if (!window.isValid()) {
		// below sync is to avoid 
		synchronized (currentWindowStack) {
			logger.debug("Current window stack before (" + currentWindowStack.size() +  "): " + currentWindowStack);
			if (currentWindowStack.contains(window)) {
				logger.debug("Window unloaded.  Removing from current window stack.");
				
				while (currentWindowStack.contains(window)) {
					currentWindowStack.remove(window);
				}
			}
			logger.debug("Current window stack after (" + currentWindowStack.size() +  "): " + currentWindowStack);
		//}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void windowLoaded(Window window) {
		addStartedWindow(window);
	}	
}
