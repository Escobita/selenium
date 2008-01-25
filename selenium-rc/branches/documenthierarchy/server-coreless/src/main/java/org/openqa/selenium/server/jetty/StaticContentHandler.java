package org.openqa.selenium.server.jetty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpFields;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.proxy.InjectionManager;

/**
 * Selenium implementation for {@link StaticContentHandler}.
 * 
 * @author Matthew Purland
 */
public class StaticContentHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(StaticContentHandler.class);

	public static final SimpleDateFormat LAST_MODIFIED_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");

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

			// grab the resource and set the Last-Modified date
			URL url = resourceLocator.getResource(contextPath);
			if (url == null) {
				logger.warn("Could not find resource for context path: "
						+ contextPath);
				continue;
			}
			
			File f = new File(url.getFile());
			
			// grab the last modified time from the file system and zero out the ms since what we'll get in 
			// the header doesn't have ms
			Calendar lastModified = Calendar.getInstance();
			lastModified.setTimeInMillis(f.lastModified());
			lastModified.set(Calendar.MILLISECOND, 0);
			
			// for some reason IE was ignoring this unless it was in GMT format?
			LAST_MODIFIED_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
			webResponse.setField(HttpFields.__LastModified, LAST_MODIFIED_FORMAT.format(lastModified.getTime()));
			
			try {
				Calendar ifModifiedSince = Calendar.getInstance();
				String requestIfModifiedSince = webRequest.getField(HttpFields.__IfModifiedSince);
				
				if (requestIfModifiedSince != null) {
					try {
						//requestIfModifiedSince = new String(requestIfModifiedSince);
						Date ifModifiedSinceDate = LAST_MODIFIED_FORMAT.parse(requestIfModifiedSince);
						ifModifiedSince.setTime(ifModifiedSinceDate);
					}
					catch (NumberFormatException ex) {
						// squelch exception.  workaround IE number format exception from request
						logger.warn("Squelching bad number format exception in parse.", ex);
					}
					if (lastModified.before(ifModifiedSince) || lastModified.equals(ifModifiedSince)) {
						webResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
						return true;
					} 
				}
			} catch (ParseException e) {
				logger.warn("Can not properly handle cache.  Unable to parse " + HttpFields.__IfModifiedSince);
			}
			
			InputStream inputStream = url.openStream();
			
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
