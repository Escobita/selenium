package org.openqa.selenium.server.client;

import java.util.EventListener;

/**
 * Listener for events on a window.
 * 
 * @author Matthew Purland
 */
public interface WindowListener extends EventListener {
	/**
	 * Event that will fire when a window is closed.
	 * @param window The window
	 */
	void windowClosed(Window window);
	
	/**
	 * Event that will fire when a window is closed.
	 * @param window The window
	 */
	void windowLoaded(Window window);
}
