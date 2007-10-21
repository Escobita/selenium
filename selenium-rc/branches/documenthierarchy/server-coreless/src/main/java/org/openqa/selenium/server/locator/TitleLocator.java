package org.openqa.selenium.server.locator;

import org.openqa.selenium.server.client.Window;
import org.openqa.selenium.server.client.WindowManager;

/**
 * Locator to find a window based on the title.
 * 
 * @author Matthew Purland
 */
public class TitleLocator implements Locator<WindowManager, Window> {
	/**
	 * {@inheritDoc}
	 */
	public Window locate(WindowManager target, String argument) {
		return target.findWindowByTitle(argument);
	}
}
