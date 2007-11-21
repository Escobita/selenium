package org.openqa.selenium.server.jetty;

import java.io.InputStream;
import java.net.URL;

public class ClasspathResourceLocator implements ResourceLocator {

	/**
	 * {@inheritDoc}
	 */
	public InputStream getResourceStream(String pathInContext) {
		InputStream inputStream = ClasspathResourceLocator.class.getResourceAsStream(pathInContext);
		return inputStream;
	}

	public URL getResource(String pathInContext) {
		return ClasspathResourceLocator.class.getResource(pathInContext);
	}

}
