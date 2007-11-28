package org.openqa.selenium.server.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.RemoteCommand;
import org.openqa.selenium.server.command.runner.RemoteCommandRunner;

/**
 * A document that represents a document under the DOM which may contain
 * multiple documents. Internally this represents a frame.
 * 
 * All commands on a document will reference the current document.
 * 
 * If the current document refers to this document then it will run it on
 * itself.
 * 
 * @author Matthew Purland
 */
public class Document {
	public static final String DEFAULT_FRAME_ADDRESS = "top";	
	
	private static Logger logger = Logger.getLogger(Document.class);

	private Lock internalStatechangeLock = new ReentrantLock();

	private Map<Integer, Document> childDocumentList = new HashMap<Integer, Document>();

	// If the document has been started yet or not.
	private boolean hasStarted;
	
	// If the document is valid, an unloaded, closed, or timeout document is invalid
	private boolean isValid = true; // Valid by default
	
	// Frame address of the document
	private String frameAddress;

	// Name of the frame expression for the document
	private String frameName;
	
	// Id of the frame for the document
	private String frameId;
	
	// Title of the document
	private String title;
	
	// Unique id of the document
	private String uniqueId;

	private RemoteCommandRunner remoteCommandRunner;

	private Document parent;
	
	private Window parentWindow;
	
	public RemoteCommand<CommandResult> getRunningCommand() {
		return remoteCommandRunner.getRunningCommand();
	}
	
	public Document(Window parentWindow, String uniqueId,
			String frameAddress, String frameName, String frameId, RemoteCommandRunner remoteCommandRunner, boolean hasStarted) {
		this.parentWindow = parentWindow;
		this.uniqueId = uniqueId;
		
		// If it's blank or null set to default "top"
		if (frameAddress == null || "".equals(frameAddress)) {
			this.frameAddress = Document.DEFAULT_FRAME_ADDRESS;
		}
		else {
			this.frameAddress = frameAddress;
		}
		
		this.frameName = frameName;
		this.frameId = frameId;
		this.remoteCommandRunner = remoteCommandRunner;
		//this.hasStarted = hasStarted;
		this.isValid = hasStarted;
	}

	/**
	 * Get the list of child documents that are under this document.
	 * 
	 * @return Returns an immutable list of child documents.
	 */
	public Map<Integer, Document> getChildDocuments() {
		return childDocumentList;
	}
	
	/**
	 * Get the list of child documents that are under this document.
	 * 
	 * @return Returns an immutable list of child documents.
	 */
	public List<Document> getChildDocumentList() {
		return new ArrayList<Document>(childDocumentList.values());
	}
	
	/**
	 * Removes a child document to this document.
	 * 
	 * @param document The document to remove
	 * @return true if the document was removed, otherwise false
	 */
	public boolean removeChildDocument(Document document) {
		internalStatechangeLock.lock();
		boolean success = false;
		try {
			if (childDocumentList.remove(document) != null) {
				success = true;
			}
		} finally {
			internalStatechangeLock.unlock();
		}
		return success;
	}

	/**
	 * Add a child document to this document.
	 * 
	 * @param document The child document to add
	 * @return true if the document was added, otherwise false
	 */
	public boolean addChildDocument(Document document) {
		internalStatechangeLock.lock();
		boolean childDocumentAdded = false;
		try {
			logger.debug("Attempting to add child document " + document + " to this document " + this);
			if (this != document) {
				
				// If document is a child of this then add it
				if (document.isChildDocument(this)) {
					logger.debug("Adding child document " + document + " to this document " + this);
					childDocumentList.put(document.getDocumentIndex(), document);
					document.setParentDocument(this);
					childDocumentAdded = true;
				}
				else {
					for (Document childDocument : childDocumentList.values()) {
						// Recurse through the child documents
						childDocumentAdded = childDocument.addChildDocument(document);
						if (childDocumentAdded) {
							break;
						}
					}
				}
	
				if (!childDocumentAdded) {
					logger.debug("Couldnt find child document of this " + this + " not adding child document " + document);
				}
			}
		} finally {
			internalStatechangeLock.unlock();
		}
		return childDocumentAdded;
	}	
	
	/**
	 * Find the document that matches the given unique id.
	 * 
	 * @param uniqueId
	 *            The unique id
	 * @return Returns the document if found; null otherwise.
	 */
	public Document findDocumentByUniqueId(String uniqueId) {
		Document foundDocument = null;

		// If it matches this document, we found it
		if (isValid() && uniqueId.equals(getUniqueId())) {
			foundDocument = this;
		} else {
			// Go through each child document and try to get a matching document
			for (Document document : getChildDocumentList()) {
				Document subDocument = document.findDocumentByUniqueId(uniqueId);

				// If it is valid document, we found it
				if (subDocument != null) {
					foundDocument = subDocument;
					break;
				}
			}
		}

		return foundDocument;
	}
	
	public Document findDocumentByIndex(Integer index) {
		Document foundDocument = null;

		// If it matches this document, we found it
		if (isValid() && index.equals(getDocumentIndex())) {
			foundDocument = this;
		} else {
			// Go through each child document and try to get a matching document
			for (Document document : getChildDocumentList()) {
				Document subDocument = document.findDocumentByIndex(index);

				// If it is valid document, we found it
				if (subDocument != null) {
					foundDocument = subDocument;
					break;
				}
			}
		}

		return foundDocument;
	}
	
	/**
	 * Get the unique id of this document.
	 */
	public String getUniqueId() {
		return uniqueId;
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	public Document getCurrentDocument() {
//		Document currentDocument = this;
//
//		// If we have a non empty document list and the document list contains
//		// our current document
//		if (!childDocumentList.isEmpty()
//				&& childDocumentList.contains(this.currentDocument)) {
//			currentDocument = this.currentDocument;
//		}
//
//		return currentDocument;
//	}

	/**
	 * Get the frame address of this document.
	 */
	public String getFrameAddress() {
		return frameAddress;
	}
	
	/**
	 * Get the frame name of this document.
	 */
	public String getFrameName() {
		return frameName;
	}
	
	/**
	 * Get the frame id of this document.
	 */
	public String getFrameId() {
		return frameId;
	}

	/**
	 * Get the title of this document.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Close the document and all child documents.  Invalidates itself
	 * and all child documents
	 */
	public void close() {
		logger.debug("Closing document " + this);
		internalStatechangeLock.lock();
		
		isValid = false;
		
		if (remoteCommandRunner != null) {
			remoteCommandRunner.close();
		}
		
		for (Document childDocument : childDocumentList.values()) {
			childDocument.close();
		}
		internalStatechangeLock.unlock();
	}
	
	/**
	 * Is the document valid?  If this document is not valid then
	 * any child documents are not valid.
	 */
	public boolean isValid() {
		return isValid;
	}
	
	
	private boolean getWhetherThisFrameMatchExpression(Window window, String targetFrameAddress) {
		boolean documentMatches = false;
		Map<String, String> parametersMap = new HashMap<String, String>();
		// Should be top
		Document document = window.getDocument();		
		
		parametersMap.put("1", // currentFrameAddress
				document.getFrameAddress());
		parametersMap.put("2", targetFrameAddress); // target

		RemoteCommand<CommandResult> getWhetherThisFrameMatchFrameExpressionCommand = new RemoteCommand(
				"getWhetherThisFrameMatchFrameExpression", parametersMap);

		logger
				.info("Running open getWhetherThisFrameMatchFrameExpression command (parameters="
						+ parametersMap + ") on: " + this);

		try {
			CommandResult commandResult = runCommand(getWhetherThisFrameMatchFrameExpressionCommand);
			
			if ((commandResult != null)
					&& "OK,true".equals(commandResult.getCommandResult())) {
				documentMatches = true;
				// browserClient.addMatchingFrameAddress(localFrameAddress);
			} else {
				// If it didn't match...remove the matching frame address if
				// it is one
	//			browserClient.removeMatchingFrameAddress(frameAddress);
			}
		}
		catch (InterruptedException ex) {
			logger.debug("***** we were interrupted while waiting for command to finish");
		}

		return documentMatches;
	}
	
	public Document findDocumentByFrameMatchExpression(String frameMatchExpression) {
		boolean documentMatches = false;
		Document foundDocument = null;
		
		documentMatches = getWhetherThisFrameMatchExpression(parentWindow, frameMatchExpression);
		
//		if (documentMatches) {
//			foundDocument = this;
//		}
//		else {
//			for (Document childDocument : aDocument.getChildDocumentList()) {
//				documentMatches = getWhetherThisFrameMatchExpression(parentWindow, frameMatchExpression);
//				
//				if (documentMatches) {
//					foundDocument = childDocument;
//					break;
//				}
//			}
//		}
		
		if (documentMatches) {
			foundDocument = parentWindow.getDocument();
		}
		
		return foundDocument;
	}

	/**
	 * Check if this document matches the given frame address.
	 */
	public boolean matchesFrameAddress(String frameAddress) {
		boolean documentMatches = false;
		
		logger.info("Trying to match document frame '" + frameAddress
				+ "' document" + this);		
		
		// First, check if the given frame address matches this frame address
		
			if (frameAddress.equals(getFrameAddress())) {
				documentMatches = true;
			}
			else if (frameAddress.equals(getFrameName())) {
				documentMatches = true;
			}
			else if (frameAddress.equals(getFrameId())) {
				documentMatches = true;
			}
			else if (frameAddress.startsWith("id=")
					&& frameAddress.substring("id=".length()).equals(getFrameId())) {
				documentMatches = true;
			}
			else if (frameAddress.startsWith("name=")
					&& frameAddress.substring("name=".length()).equals(getFrameName())) {
				documentMatches = true;
			}
			// @todo Add isLocatorSyntax function for checking if locator
			else if (frameAddress.startsWith("relative=")
					|| frameAddress.startsWith("dom=")
					|| frameAddress.startsWith("xpath=")
					|| frameAddress.startsWith("link=")
					|| frameAddress.startsWith("css=")
					|| frameAddress.startsWith("identifier=")) {
				Document foundDocument = findDocumentByFrameMatchExpression(frameAddress);

				if (foundDocument != null) {
					documentMatches = true;
				}
			}

		// Second, we haven't found a match based on frame address alone
		// We need to ask the document if it matches

//			Map<String, String> parametersMap = new HashMap<String, String>();
//			parametersMap.put("currentFrameString",
//					getCurrentBrowserClient().getLocalFrameAddress());
//			parametersMap.put("target", frameAddress);
//
//			RemoteCommand<CommandResult> getWhetherThisFrameMatchFrameExpressionCommand = new DefaultRemoteCommand(
//					"getWhetherThisFrameMatchFrameExpression", parametersMap);
//
//			logger
//					.info("Running open getWhetherThisFrameMatchFrameExpression command (parameters="
//							+ parametersMap + ") on: " + browserClient);
//
//			CommandResult commandResult = runCommand(getWhetherThisFrameMatchFrameExpressionCommand);
//
//			if ((commandResult != null)
//					&& "OK,true".equals(commandResult.getCommandResult())) {
//				documentMatches = true;
//				// browserClient.addMatchingFrameAddress(localFrameAddress);
//			} else {
//				// If it didn't match...remove the matching frame address if
//				// it is one
//				browserClient.removeMatchingFrameAddress(frameAddress);
//			}
	

//		if (!browserClient.hasUnloaded()) {
//			// Check if the given frame address matches this frame address
//			// first...
//			if (frameAddress.equals(getFrameAddress())) {
//				documentMatches = true;
//			}
//			// If we haven't found a match based on frame address alone
//			// We need to ask the document if it matches
//			else { // if (hasCurrentBrowserClient()) {
//
//			}
//		}

		return documentMatches;
	}

	/**
	 * Get a matching document for the given frame address.
	 */
	public Document findDocumentByFrameAddress(String frameAddress) {
		Document foundDocument = null;
		// First, if valid and it matches this document, we found it
		if (matchesFrameAddress(frameAddress)) {
			foundDocument = this;
		}
		// Only check child documents if the frame address starts to match
		else { // if (frameAddress.startsWith(frameAddress)) {
//			synchronized (childDocumentList) {
				for (Document document : getChildDocumentList()) {
					Document subDocument = document.findDocumentByFrameAddress(frameAddress);
	
					// If it is valid document, we found it
					if (subDocument != null) {
						foundDocument = subDocument;
						break;
					}
				}
//			}
		}

		return foundDocument;
	}
	
	/**
	 * Returns the parent document or null if the document has no parent.
	 */
	public Document getParentDocument() {
		return this.parent;
	}
	
	/**
	 * Sets the parent document.
	 */
	public void setParentDocument(Document parent) {
		this.parent = parent;
	}

	/**
	 * If the document contains the specified document.
	 * 
	 * @param document The document to find
	 * @return Returns true if this document does contain the document; false otherwise.
	 */
	public boolean contains(Document document) {
		boolean containsDocument = false;
		
		if (this == document) {
			containsDocument = true;
		}
		else {
			synchronized (childDocumentList) {
				for (Document childDocument : childDocumentList.values()) {
					containsDocument = childDocument.contains(document);
					
					if (containsDocument) {
						break;
					}
				}
			}
		}
		
		return containsDocument;
	}

	/**
	 * Runs a command on the current document.
	 * 
	 * @param remoteCommand
	 *            The remote command to run
	 * @return Returns a command result if it was successful; null otherwise.
	 * 
	 * @throws InterruptedException when a command is interrupted
	 */
	public CommandResult runCommand(RemoteCommand<CommandResult> remoteCommand) throws InterruptedException {
		CommandResult commandResult = null;
		
		if (isValid()) {
			commandResult = remoteCommandRunner.run(remoteCommand, null);
		}
		
		return commandResult;
	}

	/**
	 * Handle the given result and return a result.
	 * 
	 * @param result The result to handle
	 * @param parametersMap The parameters map
	 * @return Returns a command result to 
	 */
	public CommandResult handleResult(Session session, String result, Map<String, String> parametersMap) {
		String localFrameAddress = parametersMap.get("localFrameAddress");
		
		// The frame address of this document has changed
//		if (localFrameAddress != null && !localFrameAddress.equals(getFrameAddress())) {
//			logger.debug("Frame address changed from '" + getFrameAddress() + "' to '" + localFrameAddress + " in document " + this);
//			this.frameAddress = localFrameAddress;
//		}
		
		CommandResult commandResult = null;
//
//		// Get the unique ID passed from the browser
//		String uniqueId = parametersMap.get("uniqueId");
		String commandId = parametersMap.get("commandId");

		Integer sequenceId = Integer.valueOf(parametersMap.get("sequenceNumber"));
//		boolean isModal = Boolean.valueOf(parametersMap.get("modalDialog"));
//		
		logger.debug("Handling result=" + result + ", document=" + this + ", parametersMap=" + parametersMap);
//		boolean justStarted = commandResult != null && commandResult.equals("START");

		try {
			commandResult = remoteCommandRunner.handleResult(session, result, commandId, sequenceId);
		} catch (InterruptedException e) {
			// FIXME, what will we do in this case?
			e.printStackTrace();
		}
		
		return commandResult;
		
		//return browserClient.handleResult(result, parametersMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		List<Document> newChildDocumentList = getChildDocumentList();
		return "DefaultDocument ("
		    + "uniqueId=" +  uniqueId
			+ ", isValid=" + isValid() 
			+ ", frameAddress=" + frameAddress
			+ ", frameName=" + frameName
			+ ", title=" + title
			+ ", childDocumentList(size=" + newChildDocumentList.size() + ")=" + newChildDocumentList
			+ ")";
	}

	/**
	 * Set the title of the document.
	 */
	public void setTitle(String title) {
		logger.debug("Changing title of document " + this + " from '" + this.title + "' to '" + title + "'");
		this.title = title;
	}

	/**
	 * Get next command to run.
	 */
	public RemoteCommand getNextCommandToRun() {
		return remoteCommandRunner.getNextCommandToRun();
	}

	Integer documentIndex = null;
	/**
	 * Returns the index of this document, e.g for top.frames[1] the index would
     * be 1, for top.frames[1].frames[2] the index would be 2
	 */
	public Integer getDocumentIndex() {

		if (documentIndex == null) {
			// first get the index of the last ".", e.g. if top.frames[0].frames[1], we want to get to .frames[1]
			int startSearch = frameAddress.lastIndexOf(".");
			int startBracket = frameAddress.lastIndexOf("[");
			int endBracket = frameAddress.lastIndexOf("]");
			
			// if we have a top level document, return -1....this will usually not be used for top level documents
			if (startSearch == -1) {
				documentIndex = new Integer(-1);
			} else if (startBracket != -1 && endBracket != -1) {
				String index = frameAddress.substring((startBracket + 1), endBracket);
				documentIndex = Integer.valueOf(index);
			}
		}
		
		return documentIndex;
	}
	
	/**
	 * Check if this document is a child document of the given document.
	 * 
	 * Check that the frame addresses aren't equal and that the child frame address starts with this
	 * e.g. this == top, document == top.frames[0]
	 * 
	 * @param document The document
	 * @return Returns true if this document is a child document; false otherwise.
	 */
	public boolean isChildDocument(Document document) {
		StringTokenizer frameAddressTokenizer = new StringTokenizer(frameAddress, ".");		
		StringTokenizer documentFrameAddressTokenizer = new StringTokenizer(document.getFrameAddress(), ".");
		// top starts with top.frames[1] == false
		// top.frames[1] starts with top == true
		// top.frames[1].frames[1] is not a child of a top

		boolean isChildDocument = !frameAddress.equals(document.getFrameAddress()) 
			&& frameAddress.startsWith(document.getFrameAddress())
			&& (frameAddressTokenizer.countTokens() - 1) == documentFrameAddressTokenizer.countTokens();
		
		logger.debug("frameAddress: " + frameAddress + " document frame address: " + document.getFrameAddress() + " isChild: " + isChildDocument);
		
		return isChildDocument;
	}

	/**
	 * Set whether the document has started or not.
	 * 
	 * @param hasStarted
	 */
	public void setStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	/**
	 * Contains the specified frame address.
	 * 
	 * @param frameAddress The frame address
	 */
	public boolean contains(String frameAddress) {
		boolean containsFrameAddress = false;
		
		if (this.frameAddress.equals(frameAddress)) {
			containsFrameAddress = true;
		}
		else {
			for (Document childDocument : getChildDocumentList()) {
				containsFrameAddress = childDocument.contains(frameAddress);
				
				if (containsFrameAddress) {
					break;
				}
			}
		}
		
		return containsFrameAddress;
	}

	/**
	 * Set the unique id for the document.
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
}
