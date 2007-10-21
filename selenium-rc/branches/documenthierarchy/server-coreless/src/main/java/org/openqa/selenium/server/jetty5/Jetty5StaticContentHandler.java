package org.openqa.selenium.server.jetty5;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.openqa.selenium.server.jetty.StaticContentHandler;
import org.openqa.selenium.server.jetty.WebHandler;
import org.openqa.selenium.server.jetty.WebRequest;
import org.openqa.selenium.server.jetty.WebResponse;

/**
 * Jetty 5 static content handler.  Will pass to StaticContentHandler implementation.
 * 
 * @author Matthew Purland
 */
public class Jetty5StaticContentHandler extends AbstractJetty5Handler {
	private static Logger logger = Logger
			.getLogger(Jetty5StaticContentHandler.class);

	private StaticContentHandler staticContentHandler;

	/**
	 * Construct a new Jetty 5 static content handler with the given static content handler.
	 * 
	 * @todo pass in SeleniumConfiguration to get on demand slowResources mode, do same with jetty5
	 * @todo add configuration listener for change slowResources mode to reconstruct anything needed
	 * 
	 * @param staticContentHandler
	 *            The static content handler to delegate actions to.
	 */
	public Jetty5StaticContentHandler(WebHandler webHandler, StaticContentHandler staticContentHandler) {
		super(webHandler);
		this.staticContentHandler = staticContentHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(String pathInContext, String pathParams,
			HttpRequest request, HttpResponse response) throws HttpException,
			IOException {
		// response.setContentType("text/plain");
		handleHandler(staticContentHandler, pathInContext, pathParams, request,
				response);
	}
}
