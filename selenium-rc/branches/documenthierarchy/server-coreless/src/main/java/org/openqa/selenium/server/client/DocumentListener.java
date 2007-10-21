package org.openqa.selenium.server.client;

import java.util.EventListener;

/**
 * Event listener for a document.
 * 
 * @author Matthew Purland
 */
public interface DocumentListener extends EventListener {
	/**
	 * Event that will fire when a document is closed.
	 * 
	 * @param document The document
	 */
	void documentClosed(Document document);
	
	/**
	 * Event that will fire when a document is loaded.
	 * 
	 * @param document The document
	 */
	void documentLoaded(Document document);
}
