package org.openqa.selenium.server.locator;

import org.openqa.selenium.server.client.Document;
import org.openqa.selenium.server.client.Window;

/**
 * Locator to locate a frame.
 * 
 * @author Matthew Purland
 */
public class FrameLocator implements Locator<Window, Document> {
	/**
	 * {@inheritDoc}
	 */
	public Document locate(Window target, String argument) {
		return target.findDocumentByFrameAddress(argument);
	}
}
