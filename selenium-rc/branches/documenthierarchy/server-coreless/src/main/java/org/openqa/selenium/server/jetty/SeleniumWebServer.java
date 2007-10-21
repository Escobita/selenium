package org.openqa.selenium.server.jetty;

import org.openqa.selenium.server.SeleniumServerException;
import org.openqa.selenium.server.browser.launchers.BrowserLauncherFactory;
import org.openqa.selenium.server.client.SessionManager;
import org.openqa.selenium.server.command.commands.CommandFactory;

/**
 * Interface for a Selenium web server.
 * 
 * @author Matthew Purland
 */
public interface SeleniumWebServer {
	/**
	 * Start the server.
	 * 
	 * @throws SeleniumServerException when the server cannot be started or other server related error
	 */
	void start() throws SeleniumServerException;

	/**
	 * Configure the server.  Will be called if not configured before starting or restarting.
	 * 
	 * @throws SeleniumServerException when the server cannot be started or other server related error
	 */
	void configure() throws SeleniumServerException;
	
	/**
	 * Stop the server.
	 * 
	 * @throws SeleniumServerException when the server cannot be stopped or other server related error
	 */
	void stop() throws SeleniumServerException;
	
	/**
	 * Restart the server.
	 * 
	 * @throws SeleniumServerException when the server cannot be stopped, started, or other server related error
	 */
	void restart() throws SeleniumServerException;
	
	/**
	 * Get the port that the server is listening on.
	 * 
	 * @return Returns the port.
	 */
	int getPort();
	
	/**
	 * Get whether the server is stopped.
	 * 
	 * @return Returns true if the underlying web server is stopped.
	 */
	boolean isStopped();
}
