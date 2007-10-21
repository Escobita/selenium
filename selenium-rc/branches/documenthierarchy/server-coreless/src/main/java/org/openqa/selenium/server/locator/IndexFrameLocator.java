package org.openqa.selenium.server.locator;

import org.openqa.selenium.server.client.Document;
import org.openqa.selenium.server.client.Window;

/**
 * Locate a frame based on the frame index.
 * 
 * @author Matthew Purland
 */
public class IndexFrameLocator implements Locator <Window, Document> {

	/**
	 * {@inheritDoc}
	 */
	public Document locate(Window target, String argument) {
		return target.getCurrentDocument().findDocumentByIndex(Integer.parseInt(argument));
	}
}
