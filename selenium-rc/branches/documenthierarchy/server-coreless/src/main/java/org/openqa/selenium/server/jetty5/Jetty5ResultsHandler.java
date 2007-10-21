package org.openqa.selenium.server.jetty5;

import java.io.IOException;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.openqa.selenium.server.jetty.ResultsHandler;
import org.openqa.selenium.server.jetty.WebHandler;

/**
 * Jetty 5 results handler.  Will pass to ResultsHandler implementation.
 * 
 * @author Matthew Purland
 */
public class Jetty5ResultsHandler extends AbstractJetty5Handler {
	public ResultsHandler resultsHandler;

	public Jetty5ResultsHandler(WebHandler webHandler, ResultsHandler resultsHandler) {
		super(webHandler);
		this.resultsHandler = resultsHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(String pathInContext, String pathParams,
			HttpRequest request, HttpResponse response) throws HttpException,
			IOException {

		response.setContentType("text/html");

		handleHandler(resultsHandler, pathInContext, pathParams, request, response);
	}

}
