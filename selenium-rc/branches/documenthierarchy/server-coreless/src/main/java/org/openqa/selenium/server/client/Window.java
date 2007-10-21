package org.openqa.selenium.server.client;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.server.command.CommandResult;
import org.openqa.selenium.server.command.RemoteCommand;

/**
 * A window representing an actual physical browser window. A single window has
 * a document which may contain other documents.
 * 
 * @author Matthew Purland
 */
public interface Window {
	public static final String DEFAULT_WINDOW_NAME = "";

	/**
	 * Is this a modal level window?
	 */
	boolean isModal();

	/**
	 * Get all opened child windows by this window.
	 * 
	 * @return Returns an immutable list of child windows.
	 */
	List<Window> getChildWindowList();

	/**
	 * Get the document. If the document does not have any other documents then
	 * it is the current document.
	 */
	Document getDocument();

	/**
	 * Get the current document for a window.
	 */
	Document getCurrentDocument();
	
	/**
	 * Add the document to the window.
	 * 
	 * If it is a top level document then 
	 */
	Document addDocument(String uniqueId, String title, String frameAddress, String frameName, String frameId);
	
//	/**
//	 * Set the root document of the window.
//	 * 
//	 * @param document The document
//	 */
//	void setDocument(Document document);
//	
	/**
	 * Set the current document for the window.
	 * 
	 * @param document The document
	 */
	void setCurrentDocument(Document document);
	
	/**
	 * Add a child window to this window.
	 */
	void addChildWindow(Window window);
	
	/**
	 * Remove a child window from this window.
	 */
	void removeChildWindow(Window window);
	
	/**
	 * Add a document as just being started to the started document queue.
	 * 
	 * @param document The document to add
	 */
	void addStartedDocument(Document document);

	/**
	 * Find the document with the specific unique id.
	 * 
	 * @param uniqueId
	 *            The unique id
	 * @return Returns the document with the unique id; null if not found.
	 */
	Document findDocumentByUniqueId(String uniqueId);
	
	/**
	 * Find the document with the specified frame address.
	 * 
	 * @param frameAddress The frame address
	 * @return Returns the document with the given frame address; null if not found.
	 */
	Document findDocumentByFrameAddress(String frameAddress);

	/**
	 * Get title from the current document of the document. This is a
	 * convenience method.
	 */
	String getTitle();

	/**
	 * Get name of the window. This is the name that was used to open the window
	 * usually.
	 */
	String getName();

	/**
	 * Close the window. This will close the document, including all child
	 * documents. This will also close all child windows.
	 */
	void close();
	
	/**
	 * Is the window valid?  Does it have any valid documents?
	 */
	boolean isValid();

	/**
	 * Return a list of all window names. This includes the name of this window.
	 */
	List<String> getAllWindowNames();
	
	/**
	 * Return a list of all window titles.  This includes the title of the window.
	 */
	List<String> getAllWindowTitles();

	/**
	 * Select a frame address in the window.
	 * 
	 * @param frameAddress
	 *            The frame address
	 * @return Returns true if the frame was selected successfully; false
	 *         otherwise.
	 */
	boolean selectFrame(String frameAddress);

	/**
	 * Find the window with the specific window name.
	 * 
	 * @param windowName
	 *            The window name
	 * @return Returns the window if found; null otherwise.
	 */
	Window findWindowByName(String windowName);

	/**
	 * Find the window with the specific window title.
	 * 
	 * @param windowTitle
	 *            The window title
	 * @return Returns the window if found; null otherwise.
	 */
	Window findWindowByTitle(String windowTitle);
	
	/**
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
	boolean waitForDocumentToLoad(String frameAddress, long timeoutInMilliseconds) throws InterruptedException;
	
	/**
	 * Check if the given document is contained within this window.
	 * 
	 * @param document The document
	 */
	boolean contains(Document document);
	
	/**
	 * Close the document that is contained within this window.
	 * 
	 * @param document The document to close
	 */
	void closeDocument(Document document);
	
	/**
	 * Handle the given result for the document in this window and return a result.
	 * 
	 * @param result The result to handle
	 * @param parametersMap The parameters map
	 * @return Returns a command result to 
	 */
	CommandResult handleResult(Session session, Document document, String result, Map<String, String> parametersMap);
	
	/**
	 * Run a command on the window.  This will run it on the current document.
	 */
	CommandResult run(RemoteCommand<CommandResult> remoteCommand);
	
	/**
	 * Add a listener to the window.
	 * 
	 * @param windowListener Window listener to add
	 */
	void addListener(WindowListener windowListener);
	
	/**
	 * Remove a listener from the window.
	 * 
	 * @param windowListener Window listener to remove
	 */
	void removeListener(WindowListener windowListener);
}
