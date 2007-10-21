package org.openqa.selenium.server.jetty6;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.selenium.server.jetty.ResultsHandler;

public class Jetty6ResultsHandler extends AbstractJetty6Handler {

	public ResultsHandler resultsHandler;
	
	public Jetty6ResultsHandler(ResultsHandler resultsHandler) {
		this.resultsHandler = resultsHandler;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
		 response.setContentType("text/html");
		 
		handleHandler(resultsHandler, target, request, response, dispatch);
	}

}
