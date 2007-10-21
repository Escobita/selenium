package org.openqa.selenium.server.jetty6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.openqa.selenium.server.jetty.StaticContentHandler;
import org.openqa.selenium.server.jetty.Handler.Method;

/**
 * Handler to serve Jetty 6 static content to a client.
 * 
 * It may be instantiated to serve resources slowly.
 * 
 * @author Matthew Purland
 */
public class Jetty6StaticContentHandler extends AbstractJetty6Handler {
	private static Logger logger = Logger
			.getLogger(Jetty6StaticContentHandler.class);

	private StaticContentHandler staticContentHandler;

	/**
	 * Construct a new Jetty 6 static content handler with the given static content handler.
	 * 
	 * @todo pass in SeleniumConfiguration to get on demand slowResources mode, do same with jetty5
	 * @todo add configuration listener for change slowResources mode to reconstruct anything needed
	 * 
	 * @param staticContentHandler
	 *            The static content handler to delegate actions to.
	 */
	public Jetty6StaticContentHandler(StaticContentHandler staticContentHandler) {
		this.staticContentHandler = staticContentHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
        //response.setContentType("text/plain");
		handleHandler(staticContentHandler, target, request, response, dispatch);
	}

	/**
	 * Pause for the specified amount of milliseconds.
	 * 
	 * @param millis
	 *            Number of milliseconds to pause.
	 */
	private void pause(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
		}
	}
}
