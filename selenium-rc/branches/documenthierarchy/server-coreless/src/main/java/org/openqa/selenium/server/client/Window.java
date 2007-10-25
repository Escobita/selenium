package org.openqa.selenium.server.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.OKCommandResult;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.runner.RemoteCommandRunner;
import org.openqa.selenium.server.locator.IndexFrameLocator;
import org.openqa.selenium.server.locator.RelativeFrameLocator;

/**
 * A window representing an actual physical browser window. A single window has
 * a document which may contain other documents.
 * 
 * @todo Create abstract to use for window implementations for diff browsers.
 * 
 * @author Matthew Purland
 */
public class Window {
	private static Logger logger = Logger.getLogger(Window.class);
	
	public static final String DEFAULT_WINDOW_NAME = "";
	
	private Document document;
	
	private Document currentDocument;
	
	// List of child windows
	private List<Window> childWindowList = new ArrayList<Window>();

	// Queue of started documents in the window that have not been successfully waited for yet
	private BlockingQueue<Document> startedDocumentQueue = new LinkedBlockingQueue<Document>();
	
	// Name of the window
	private String name;
	
	private Lock internalStatechangeLock = new ReentrantLock();
		
	// If this window is a modal dialog
	private boolean isModal;
	
	private Queue<Thread> waitingThreadQueue = new ConcurrentLinkedQueue<Thread>();	
	
	private List<WindowListener> windowListenerList = new ArrayList<WindowListener>();

	public static String FRAME_REGULAR_EXPRESSION = "\\[([0-9])\\]";
	public static Pattern FRAME_PATTERN = Pattern.compile(FRAME_REGULAR_EXPRESSION);
	
	/**
	 * Creates a window with a default "top" document.
	 * 
	 * @param windowName Name of the window
	 * @param document The document that will act as "top"
	 */
	public Window(String windowName, Document document) {
		this.name = windowName;
		
		if (document != null) {
			this.document = document;
			setCurrentDocument(document);
		}
	}
	
	/**
	 * Get all opened child windows by this window.
	 * 
	 * @return Returns an immutable list of child windows.
	 */
	public List<Window> getChildWindowList() {
		return Collections.unmodifiableList(childWindowList);
	}

	/**
	 * Get the document. If the document does not have any other documents then
	 * it is the current document.
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * Get the current document for a window.
	 */
	public Document getCurrentDocument() {
		return currentDocument;
	}
	
	/**
	 * Find the document with the specific unique id.
	 * 
	 * @param uniqueId
	 *            The unique id
	 * @return Returns the document with the unique id; null if not found.
	 */
	public Document findDocumentByUniqueId(String uniqueId) {
		Document foundDocument = null;
		
		if (document != null) {
			foundDocument = document.findDocumentByUniqueId(uniqueId);
		}
		
		return foundDocument;
	}	

	/**
	 * Get title from the current document of the document. This is a
	 * convenience method.
	 */
	public String getTitle() {
		logger.debug("Getting title for current document " + getCurrentDocument());
		// The title for the current document
		// This is represented in JavaScript as document.title
		return getDocument().getTitle();
	}

	/**
	 * Get name of the window. This is the name that was used to open the window
	 * usually.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Close the window. This will close the document, including all child
	 * documents. This will also close all child windows.
	 */
	public void close() {
		internalStatechangeLock.lock();
		
		try {			
			logger.debug("Closing window " + this);
			
			if (document != null) {
				// First, close the document
				document.close();
			}
			
			// Second, close each child window
			for (Window childWindow : childWindowList) {
				childWindow.close();
			}
			
			// Interrupt each thread waiting
			for (Thread thread : waitingThreadQueue) {
				thread.interrupt();
			}
			
			fireWindowOnClosed(this);
		}
		finally {
			internalStatechangeLock.unlock();
		}
	}

	/**
	 * Is the window valid?  Does it have any valid documents?
	 */
	public boolean isValid() {
		internalStatechangeLock.lock();
		
		try {
			// First, if the document tree is valid
			boolean isValid = document != null && document.isValid();
			
			// Second, if this document is valid, check child windows
			// If we have any valid child windows
			if (!isValid) {
				for (Window childWindow : childWindowList) {
					isValid = childWindow.isValid();
					
					// If the child window is/has a valid window then break
					if (isValid) {
						break;
					}
				}
			}
			
			return isValid;
		}
		finally {
			internalStatechangeLock.unlock();
		}
	}
	
	/**
	 * Add a document as just being started to the started document queue.
	 * 
	 * @param document The document to add
	 */
	public void addStartedDocument(Document document) {
		if (document == null) {
			throw new IllegalArgumentException("cannot add null document to started document queue");
		}
		
		if (!startedDocumentQueue.contains(document)) {
			logger.debug("Adding started document " + document + " to window " + this);
			startedDocumentQueue.add(document);
		}
		else {
			logger.debug("Could not add duplicate started document " + document + " to window " + this);
		}
	}
	
	/**
	 * Return a list of all window names. This includes the name of this window.
	 */
	public List<String> getAllWindowNames() {
		List<String> windowNamesList = new ArrayList<String>();
		
		windowNamesList.add(getName());
		
		for (Window childWindow : childWindowList) {
			// @todo Can we add duplicates?
			windowNamesList.addAll(childWindow.getAllWindowNames());
		}
		
		return windowNamesList;
	}
	
	/**
	 * Return a list of all window titles.  This includes the title of the window.
	 */
	public List<String> getAllWindowTitles() {
		List<String> windowTitlesList = new ArrayList<String>();
		
		for (Window childWindow : childWindowList) {
			String childWindowTitle = childWindow.getTitle();
			
			// @todo Can we add duplicates?
			if (!windowTitlesList.contains(childWindowTitle)) {
				windowTitlesList.add(childWindowTitle);
			}
		}
		
		return windowTitlesList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setDocument(Document newDocument) {
		internalStatechangeLock.lock();
		
		try {
			logger.debug("Setting new document " + newDocument + " on window " + this);
			this.document = newDocument;
		}
		finally {
			internalStatechangeLock.unlock();
		}
	}	
	
	/**
	 * Set the current document for the window.
	 * 
	 * @param document The document
	 */
	public void setCurrentDocument(Document newCurrentDocument) {
		internalStatechangeLock.lock();
		
		try {
			logger.debug("Setting new current document " + newCurrentDocument + " on window " + this);
			this.currentDocument = newCurrentDocument;
		}
		finally {
			internalStatechangeLock.unlock();
		}
	}

	/**
	 * Select a frame address in the window.
	 * 
	 * @param frameAddress
	 *            The frame address
	 * @return Returns true if the frame was selected successfully; false
	 *         otherwise.
	 */
	public boolean selectFrame(String frameAddress) {
		
		boolean selectFrameSuccessful = false;
		Document foundDocument = null;
		
		// @todo need locators implemeneted dom=, etc  another point is that when a locator isn't specified
		// also, when we just give frameAddress without a locator identifier this should really only check based on
		// frame name, not internal address. (e.g. top.frames[0]...) once this is in place, we will only need to 
		// check the child documents since name is relative to child
		
		if (frameAddress.startsWith("relative=")) {
			RelativeFrameLocator relativeFrameLocator = new RelativeFrameLocator();
			foundDocument = relativeFrameLocator.locate(this, frameAddress.substring("relative=".length()));
		}
		else if (frameAddress.startsWith("index=")) {
			IndexFrameLocator indexFrameLocator = new IndexFrameLocator();
			foundDocument = indexFrameLocator.locate(this, frameAddress.substring("index=".length()));
		}
		
		if (foundDocument == null) {
			// First look through the child documents, this is because if it's a name, e.g. mainframe it will be relative to the current		
			for (Document childDocument : currentDocument.getChildDocumentList()) {
				foundDocument = childDocument.findDocumentByFrameAddress(frameAddress);
					
				if (foundDocument != null) {
					break;
				}
			}
		}

		// Then check self
		if (foundDocument == null) {
			// Then look at the current document
			foundDocument = currentDocument.findDocumentByFrameAddress(frameAddress);
		}

		// If we still haven't found the window, check from top
		if (foundDocument == null) {
			foundDocument = document.findDocumentByFrameAddress(frameAddress);
		}
		
		// First, if we found a document in our document then select it
		if (foundDocument != null && foundDocument.isValid()) {
			setCurrentDocument(foundDocument);
			selectFrameSuccessful = true;
		}
		
		if (selectFrameSuccessful) {
			logger.debug("Selecting frame for document " + foundDocument
					+ " was looking for frameAddress=" + frameAddress);
		}
		else {
			logger.debug("Could not select window, was looking for frameAddress=" + frameAddress);
		}
		
		return selectFrameSuccessful;
	}
	
	/**
	 * Is this a modal level window?
	 */
	public boolean isModal() {
		return isModal;
	}

	/**
	 * Find the window with the specific window name.
	 * 
	 * @param windowName
	 *            The window name
	 * @return Returns the window if found; null otherwise.
	 */
	public Window findWindowByName(String windowName) {
		Window foundWindow = null;
		
		// First, if this window name matches
		if (windowName.equals(getName())) {
			foundWindow = this;
		}
		// Otherwise, if any child window name matches
		else {
			for (Window childWindow : childWindowList) {
				foundWindow = childWindow.findWindowByName(windowName);
				
				if (foundWindow != null) {
					break;
				}
			}
		}
		
		return foundWindow;
	}
	
	/**
	 * Find the window with the specific window title.
	 * 
	 * @param windowTitle
	 *            The window title
	 * @return Returns the window if found; null otherwise.
	 */
	public Window findWindowByTitle(String windowTitle) {
		Window foundWindow = null;
		
		// First, if this window title matches
		if (isValid() && windowTitle.equals(getTitle())) {
			foundWindow = this;
		}
		// Otherwise, if any child window title matches
		else {
			for (Window childWindow : childWindowList) {
				foundWindow = childWindow.findWindowByTitle(windowTitle);
				
				if (foundWindow != null) {
					break;
				}
			}
		}
		
		return foundWindow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Window ("
		+ "uniqueId=" + System.identityHashCode(this)
		+ ", document=" + document
		+ ", currentDocument=" + currentDocument
		+ ", name=" + name
		+ ", isModal=" + isModal
		+ ")";
	}

	/**
	 * Check if the given document is contained within this window.
	 * 
	 * @param document The document
	 */
	public boolean contains(Document document) {
		return this.document != null && this.document.contains(document);
	}

	/**
	 * Wait for a document to load on the window and timeout in the
	 * number of seconds. This method will block until a matching window is
	 * found, or until the specified timeout elapses.
	 * 
	 * @param frameAddress 
	 * 			  The frame address to wait for
	 * @param timeoutInMilliseconds
	 *            Timeout in milliseconds
	 * 
	 * @return Returns true if the document was loaded and detected successfully;
	 *         false otherwise.
	 * @throws InterruptedException 
	 */
	public boolean waitForDocumentToLoad(String frameAddress, long timeoutInMilliseconds) throws InterruptedException {
		boolean loadedSuccessfully = false;
		long startTimeInMilliseconds = System.currentTimeMillis();
		long elapsedTime = 0;
		
		Set<Document> checkedDocumentSet = new HashSet<Document>();
		
		waitingThreadQueue.add(Thread.currentThread());
		
		while (elapsedTime < timeoutInMilliseconds) {
			long timeRemaining = timeoutInMilliseconds - elapsedTime;
			
			logger.info("Waiting for document frame address '" + frameAddress + "'"
					+ " wait time elapsed: " + elapsedTime
					+ " wait time remaining: " + timeRemaining
					+ " window: " + this);
			
			Document startedDocument = null;
			Document foundDocument = null;

			startedDocument = startedDocumentQueue.poll(timeRemaining,
				TimeUnit.MILLISECONDS);
			
			if (checkedDocumentSet.contains(startedDocument)) {
				Thread.sleep(1000);
				checkedDocumentSet.clear();
			}
			else {
				checkedDocumentSet.add(startedDocument);
			}
			
			logger.debug("Found started document: " + startedDocument);

			elapsedTime = System.currentTimeMillis() - startTimeInMilliseconds;
			
			if (startedDocument != null && startedDocument.isValid()) {
				// if no frame address was given, we match on any started document
				if (frameAddress == null) {
					foundDocument = startedDocument;
				}
				// otherwise, check both window name and frame address
				else {
					boolean startedDocumentMatches = startedDocument.matchesFrameAddress(frameAddress);
					if (startedDocumentMatches) {
						foundDocument = startedDocument;
					}
				}
				
				// If we found a matching document
				if (foundDocument != null && foundDocument.isValid())
				{
					logger.info("Found matching document " + foundDocument
							+ " frame address '" + frameAddress + "'");
					
					internalStatechangeLock.lock();
					try {
						setCurrentDocument(foundDocument);
						loadedSuccessfully = true;
						break;
					}
					finally {
						internalStatechangeLock.unlock();
					}
				}
				// If we didn't find it, put it back.
				else {
					addStartedDocument(startedDocument);		
				}	
			}
		}
		
		if (!loadedSuccessfully) {
			logger.info("Waiting timed out waiting for frame address '" + frameAddress + "'");
		}
		waitingThreadQueue.remove(Thread.currentThread());

		return loadedSuccessfully;
	}

	/**
	 * Add a child window to this window.
	 */
	public void addChildWindow(Window document) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Remove a child window from this window.
	 */
	public void removeChildWindow(Window window) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Close the document that is contained within this window.
	 * 
	 * @param document The document to close
	 */
	public void closeDocument(Document document) {
		logger.debug("Closing document in window " + this);
		
		internalStatechangeLock.lock();
		try {
			// If document structure is this document
			if (getDocument() == document) {
				logger.debug("Document structure is the closing document.  Closing window...");			
				// Close the window
				this.close();
			}
//			// If current document is this document
//			else if (getCurrentDocument() == document) {
//				logger.debug("Current document is the closing document");
//				setCurrentDocument(null);
//			}
		}
		finally {
			internalStatechangeLock.unlock();
		}
		
		document.close();
	}
	
	/**
	 * Finds the given documents parent in this windows document tree
	 */
	private Document findDocumentParent(Document aDocument) {
		Document parentDocument = null;
		
		// parse the parent from the frame address
		String frameAddress = aDocument.getFrameAddress();
		if (frameAddress.lastIndexOf(".") != -1) {
			String parentFrameAddress = "frameAddress=" + frameAddress.substring(0, frameAddress.lastIndexOf("."));
			parentDocument = findDocumentByFrameAddress(parentFrameAddress);
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>found document parent: " + parentDocument);
		}
		
		return parentDocument;
	}
	
	/**
	 * Find the document with the specified frame address.
	 * 
	 * @param frameAddress The frame address
	 * @return Returns the document with the given frame address; null if not found.
	 */
	public Document findDocumentByFrameAddress(String frameAddress) {
		
		if (frameAddress.startsWith("frameAddress")) {
			Document foundDocument = findDocumentByInternalFrameAddress(frameAddress);
			
			return foundDocument;
		}
		
		return document.findDocumentByFrameAddress(frameAddress);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Document findDocumentByInternalFrameAddress(String frameAddress) {
		Matcher matcher = FRAME_PATTERN.matcher(frameAddress);
		Document currentDocument = document;
		while (matcher.find()) {
			currentDocument = currentDocument.getChildDocuments().get(Integer.valueOf(matcher.group(1)));
			
			// if we weren't able to find the document, break
			if (currentDocument == null) {
				break;
			}
		}
		
		return currentDocument;
	}

	/**
	 * Add the document to the window.
	 * 
	 * If it is a top level document then 
	 */
	public synchronized Document addDocument(String uniqueId, String title, String frameAddress, String frameName, String frameId) {
		internalStatechangeLock.lock();
		Document addedDocument = null;
		try {
			Document newDocument = null;
			boolean isTopLevelDocument = false;
	
			logger.debug("Attempting to add new document to document tree on window " + this);
			
			// Tokenize the string given from the AUT frame address from Selenium core
			StringTokenizer frameAddressTokenizer = new StringTokenizer(frameAddress, ".");
			int numberOfTokensProcessed = 0;
			String builtFrameAddress = "";
			
			while (frameAddressTokenizer.hasMoreTokens()) {
				String frameAddressToken = frameAddressTokenizer.nextToken();
				boolean isLastToken = !frameAddressTokenizer.hasMoreTokens();
				
				numberOfTokensProcessed++;
				
				// If this is not the first token then add a dot before it
				if (numberOfTokensProcessed != 1) {
					builtFrameAddress += ".";
				}
				builtFrameAddress += frameAddressToken;	
				
				isTopLevelDocument = builtFrameAddress.equals(Document.DEFAULT_FRAME_ADDRESS);
				
				Document foundDocument = findDocumentByFrameAddress("frameAddress=" + builtFrameAddress);
			
				// Only try adding a document if we don't already have one in our DOM, or if it's the last token and the one
				// in this position is not valid 
				// !foundDocument.isValid() &&  before we were checking for this, testing just clobering the live document
				if (foundDocument == null || (isLastToken)) {
					
					String documentId = uniqueId;
					
					String tokenFrameName = frameName;
					
					// @todo should just place this construction in DefaultDocument, not here
					RemoteCommandRunner remoteCommandRunner = new RemoteCommandRunner();
					
					if (!isLastToken) {
						documentId = "temporaryDocumentId";
						tokenFrameName = "temporaryFrameName";
						remoteCommandRunner = null;
						logger.debug("Adding temp document with null command runner");
					}
					else {
						logger.debug("Adding document with command runner " + remoteCommandRunner);
					}
	
	
					newDocument = new Document(this, documentId, builtFrameAddress, tokenFrameName, frameId, remoteCommandRunner, isLastToken);
					newDocument.setTitle(title);
					
					logger.debug("Trying to add document " + newDocument + " to window " + this);	
						
					// If it's the document we want to add, add it as started
					if (isLastToken) {
						addStartedDocument(newDocument);
						addedDocument = newDocument;
					}
					
					// If we are replacing a document, we take the appropriate action
					if (foundDocument != null) {
						List<Document> childDocumentList = foundDocument.getChildDocumentList();
						
						// Add child documents back in
						for (Document childDocument : childDocumentList) {
							newDocument.addChildDocument(childDocument);
						}
						
						// If replacing a top document when top is actually being added
						if (isTopLevelDocument && isLastToken) {
							setDocument(newDocument);
							fireWindowOnLoaded(this);
						}
						else {
							// Dispose of the replaced document
							Document parent = foundDocument.getParentDocument();
							logger.debug("Copying child list and removing \n" 
										+ foundDocument
										+ "\nfrom parent: " + parent);
							boolean removed = parent.removeChildDocument(foundDocument);
							logger.debug("removed: " + removed);
	
							logger.debug("Adding child document to window " + this);
							parent.addChildDocument(newDocument);
						}
						
					}
					// If we're adding a temporary top level document
					else if (foundDocument == null && isTopLevelDocument) {
						setDocument(newDocument);
						
						if (isLastToken) {
							fireWindowOnLoaded(this);
						}
					}
					else if (getDocument() != null) {
						logger.debug("Adding child document to window " + this);
						
						
						Document parentDocument = findDocumentParent(newDocument);
						
						// We found the correct parent document, add it
						if (parentDocument != null) {
							parentDocument.addChildDocument(newDocument);
						}
						// Let this document find child itself to add
						else {
							getDocument().addChildDocument(newDocument);
						}
					}
				}
				else {
					logger.debug("Could not add new document, found document " + foundDocument);
				}
			}
				
			
		}
		finally {
			internalStatechangeLock.unlock();
		}
		return addedDocument;
	}

	/**
	 * Handle the given result for the document in this window and return a result.
	 * 
	 * @param result The result to handle
	 * @param parametersMap The parameters map
	 * @return Returns a command result to 
	 */
	public CommandResult handleResult(Session session, Document document, String result, Map<String, String> parametersMap) {
		// @todo Put logic for modal dialogs in here 
		// If this window has a modal dialog open, and document is not contained within
		// that modal dialog then we must either fail here, or wait until the modal dialog
		// is closed until we can process further
		return document.handleResult(session, result, parametersMap);
	}
	
	private void fireWindowOnLoaded(Window window) {
		logger.debug("Firing window on loaded event for window " + this);
		synchronized (windowListenerList) {
			for (WindowListener windowListener : windowListenerList) {
				windowListener.windowLoaded(window);
			}
		}
	}
	
	private void fireWindowOnClosed(Window window) {
		logger.debug("Firing window on closed event for window " + this);
		synchronized (windowListenerList) {
			for (WindowListener windowListener : windowListenerList) {
				windowListener.windowClosed(window);
			}
		}
	}

	/**
	 * Add a listener to the window.
	 * 
	 * @param windowListener Window listener to add
	 */
	public void addListener(WindowListener windowListener) {
		synchronized (windowListenerList) {
			windowListenerList.add(windowListener);
		}
	}

	/**
	 * Remove a listener from the window.
	 * 
	 * @param windowListener Window listener to remove
	 */
	public void removeListener(WindowListener windowListener) {
		synchronized (windowListenerList) {
			windowListenerList.remove(windowListener);
		}
	}
	
	private CommandResult run(Document document,
			RemoteCommand<CommandResult> remoteCommand) {
		
		// @todo add stuff here
		logger.debug("Running remote command " + remoteCommand
				+ " on document: " + document);
		
		CommandResult commandResult = null;
		
		// Temporary hack?? Do we need to catch this exception?
		try {
//			Window window = findWindowContainingDocument(document);
			
			commandResult = document.runCommand(remoteCommand);
			logger.debug("Got command result " + commandResult);
		}
		catch (InterruptedException ex) {
			logger.warn("****** Interrupted exception " + ex + " while running remote command " + remoteCommand + " returning OK");
			
			commandResult = new OKCommandResult();
		}
		
		return commandResult;
	}

	/**
	 * Run a command on the window.  This will run it on the current document.
	 */
	public CommandResult run(RemoteCommand<CommandResult> remoteCommand) {
		Document currentDocument = getCurrentDocument();
		CommandResult commandResult = null;
		
		logger.debug("Running (before) command " + remoteCommand + " in window " + this);
		
		// below is a hack...
		if (!remoteCommand.getCommand().equals("waitForPageToLoad")) {
			startedDocumentQueue.clear();
		}
		
		logger.debug("Running (after) command " + remoteCommand + " in window " + this);
		
		if (currentDocument != null && currentDocument.isValid()) {
			logger.debug("Current document is valid, running command " + remoteCommand + " in window " + this);
			commandResult = run(currentDocument, remoteCommand);
		}
		
		return commandResult;
	}
}
