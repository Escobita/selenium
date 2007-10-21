package org.openqa.selenium.server.jetty;

import java.io.InputStream;

public class ClasspathResourceLocator implements ResourceLocator {

	/**
	 * {@inheritDoc}
	 */
	public InputStream getResource(String pathInContext) {
		InputStream inputStream = ClasspathResourceLocator.class.getResourceAsStream(pathInContext);
		
		return inputStream;
	}



}
