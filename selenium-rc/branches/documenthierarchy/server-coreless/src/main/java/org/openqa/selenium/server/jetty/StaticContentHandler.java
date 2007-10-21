package org.openqa.selenium.server.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpFields;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.jetty.Handler.Method;
import org.openqa.selenium.server.proxy.InjectionManager;

/**
 * Selenium implementation for {@link StaticContentHandler}.
 * 
 * @author Matthew Purland
 */
public class StaticContentHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(StaticContentHandler.class);

	private List<ResourceLocator> resourceLocatorList = new ArrayList<ResourceLocator>();

	private SeleniumConfiguration seleniumConfiguration;
	private InjectionManager injectionManager;
	private ModifiedIO modifiedIO;
	
	public StaticContentHandler(SeleniumConfiguration seleniumConfiguration, InjectionManager injectionManager, ModifiedIO modifiedIO) {
		this.seleniumConfiguration = seleniumConfiguration;
		this.injectionManager = injectionManager;
		this.modifiedIO = modifiedIO;

		addStaticContent(new ClasspathResourceLocator());
	}

	/**
	 * Add static content for a resource locator to the static content handler.
	 * 
	 * @param resourcelocator
	 *            The resource locator.
	 */
	public void addStaticContent(ResourceLocator resourcelocator) {
		resourceLocatorList.add(resourcelocator);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean handle(String contextPath, String queryString, Map parameterMap,
			Method method, WebRequest webRequest, WebResponse webResponse, OutputStream outputStream)  throws IOException {		
		boolean requestWasHandled = false;
		
        if (contextPath.equals("/core/RemoteRunner.html") && seleniumConfiguration.isProxyInjectionMode()) {
        	contextPath = contextPath.replaceFirst("/core/RemoteRunner.html",
                    "/core/InjectedRemoteRunner.html");
        }
       
        // Tell browser to cache Selenium JS for up to 30 minutes
        if (contextPath.endsWith(".js")) {
        	// @todo verify this assumption
        	// we know this was working, but anders thinks it may be better to use the file
        	// modified date rather then a fixed timeline from the proxy server 
//			webResponse.setField(HttpFields.__CacheControl, "max-age=1800");
        	//FIXME, we could get this to grab the last modified date off the files
        	webResponse.setField("Last-Modified", "Mon, 10 Sep 2007 00:00:00 GMT");
        }

		
		//logger.info("Attempting to handle static content for context path before modification: "
		//		+ contextPath);
		//logger.info("Attempting to handle static content for requestURL: " + requestURL);
		// Convert the given contextPath
		contextPath = getResourceContextPath(webRequest.getRequestURL(), contextPath);
		// /core/RemoteRunner.html
		//logger.info("Attempting to handle static content for context path: "
		//		+ contextPath);

		// Find the file resource for the URL and then put it to the output stream
		for (ResourceLocator resourceLocator : resourceLocatorList) {
			InputStream inputStream = resourceLocator.getResource(contextPath);

			if (inputStream != null) {
				try {
					// Inject static content on the classpath that may need to be injected
					if (seleniumConfiguration.isProxyInjectionMode()) {
						injectionManager.injectJavaScript(webRequest.getRequestURL(), webRequest.getPath(), webResponse, inputStream, outputStream);
						outputStream.flush();
					}
					else {
						modifiedIO.copy(inputStream, outputStream);
						outputStream.flush();
					}
					requestWasHandled = true;
				} catch (IOException ex) {
					logger.error(ex);
				}
			} else {
				logger.warn("Could not find resource for context path: "
						+ contextPath);
			}

		}
		
		return requestWasHandled;
	}

}
