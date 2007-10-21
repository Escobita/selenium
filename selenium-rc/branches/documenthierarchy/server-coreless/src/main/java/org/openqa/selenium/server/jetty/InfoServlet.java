package org.openqa.selenium.server.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.configuration.SeleniumConfiguration;
import org.openqa.selenium.server.configuration.SeleniumConfigurationOption;

/**
 * Info servlet to get a running instance report from the currently running Selenium Server instance such as configuration and clients.
 * 
 * This is a basic servlet and should have no dependencies on any Jetty implementation, except the Servlet API.
 * 
 * @todo Need to expand further to allow commands for setting new configuration parameters, hot starting the server instances, viewing commands and queues, connected clients, etc.
 * @todo Need to expand further to allow viewing of sibling servers.
 * 
 * @todo redo to use jsp + servlet?
 * 
 * @author Matthew Purland
 */
public class InfoServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(InfoServlet.class);

	private SeleniumServer server;

	public InfoServlet(SeleniumServer server) {
		this.server = server;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		final String servletName = "Info Servlet";

		logger.info("Processing info...");

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("<title>" + servletName + "</title>");
		response.getWriter().println("<h1>" + servletName + "</h1>");

		URL classURL = SeleniumServer.class.getProtectionDomain()
				.getCodeSource().getLocation();
		File classFile = new File(classURL.getFile());
		long lastModified = classFile.lastModified();
		Date lastModifiedDate = new Date(lastModified);
		String timeLastBuilt = DateFormat.getInstance()
				.format(lastModifiedDate);

		SeleniumConfiguration configuration = server.getSeleniumConfiguration();
		
		int port = configuration.getPort();

		String portParameter = request.getParameter("port");
		
		if (portParameter != null) {
			try {
				response.getWriter().println("Attempting to set the port to " + portParameter);
				response.flushBuffer();
				configuration.setOption(SeleniumConfigurationOption.PORT, Integer.valueOf(portParameter));
			}
			catch (NumberFormatException ex) {
				response.getWriter().println("The port given must be a number.");
			}
		}
		else {
			response.getWriter().println("<pre>");
			response.getWriter().println("Selenium Server");
			response.getWriter().println("\tVersion: " + server.getVersion());
			response.getWriter().println("\tTime last built: " + timeLastBuilt);
			response.getWriter().println("\tPort: " + port);
			response.getWriter().println("</pre>");
		}
		response.flushBuffer();
		
//		BrowserManager browserManager = new BrowserManager(configuration);
//		browserManager.getNewBrowserSession(null, new BrowserType(Browser.IEHTA), "http://www.google.com");
	}
}
