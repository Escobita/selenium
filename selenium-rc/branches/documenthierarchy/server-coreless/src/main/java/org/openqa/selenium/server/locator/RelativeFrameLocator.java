package org.openqa.selenium.server.locator;

import org.openqa.selenium.server.client.Document;
import org.openqa.selenium.server.client.Window;

/**
 * Locator to locate a relative frame from the current frame.
 *
 * @author Matthew Purland
 */
public class RelativeFrameLocator implements Locator<Window, Document> {
	/**
	 * {@inheritDoc}
	 */
	public Document locate(Window target, String argument) {
		if ("parent".equals(argument)) {
			return target.getCurrentDocument().getParentDocument();
		}
		else if ("top".equals(argument)) {
			return target.getDocument();
		}
		else if ("up".equals(argument)) {
			return target.getCurrentDocument().getParentDocument();
		}

		throw new IllegalArgumentException("Bad argument " + argument);
	}
}
