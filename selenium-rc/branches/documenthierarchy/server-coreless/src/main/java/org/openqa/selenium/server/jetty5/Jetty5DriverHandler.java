package org.openqa.selenium.server.jetty5;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.openqa.selenium.server.jetty.DriverHandler;
import org.openqa.selenium.server.jetty.WebHandler;

/**
 * Handler to handle remote Selenium requests for Jetty 5.
 * 
 * @author Matthew Purland
 */
public class Jetty5DriverHandler extends AbstractJetty5Handler {

	private static Logger logger = Logger.getLogger(Jetty5DriverHandler.class);

	private static final String TEXT_PLAIN = "text/plain";
	
	private DriverHandler driverHandler;	
	
	public Jetty5DriverHandler(WebHandler webHandler, DriverHandler driverHandler) {
		super(webHandler);
		this.driverHandler = driverHandler;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
		// Set the content type
		// The driver handler will return plain text
		response.setContentType(TEXT_PLAIN);
		
		// Set the headers to not cache
		setNoCacheHeaders(response);

		handleHandler(driverHandler, pathInContext, pathParams, request, response);
	}
}
