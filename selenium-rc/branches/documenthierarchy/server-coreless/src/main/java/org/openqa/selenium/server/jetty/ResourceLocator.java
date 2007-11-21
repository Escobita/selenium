package org.openqa.selenium.server.jetty;

import java.io.InputStream;
import java.net.URL;

/**
 * Locator to locate a resource within a specified path.
 * 
 * @author Matthew Purland
 */
public interface ResourceLocator {
	InputStream getResourceStream(String pathInContext);
	URL getResource(String pathInContext);
}
