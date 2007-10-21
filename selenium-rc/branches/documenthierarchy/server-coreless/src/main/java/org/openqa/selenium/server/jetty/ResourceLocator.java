package org.openqa.selenium.server.jetty;

import java.io.InputStream;

/**
 * Locator to locate a resource within a specified path.
 * 
 * @author Matthew Purland
 */
public interface ResourceLocator {
	InputStream getResource(String pathInContext);
}
