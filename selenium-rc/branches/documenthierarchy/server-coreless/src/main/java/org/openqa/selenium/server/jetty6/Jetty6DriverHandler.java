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
import org.openqa.selenium.server.jetty.DriverHandler;
import org.openqa.selenium.server.jetty.Handler.Method;

/**
 * Handler to handle remote Selenium requests for Jetty 6.
 * 
 * @author Matthew Purland
 */
public class Jetty6DriverHandler extends AbstractJetty6Handler {

	private static Logger logger = Logger.getLogger(Jetty6DriverHandler.class);

	private static final String TEXT_PLAIN = "text/plain";
	
	private DriverHandler driverHandler;
	
	public Jetty6DriverHandler(DriverHandler driverHandler) {
		this.driverHandler = driverHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		Request baseRequest = (request instanceof Request) ? (Request) request
				: HttpConnection.getCurrentConnection().getRequest();
		//super.handle(target, request, response, dispatch);

		// Set the content type
		// The driver handler will return plain text
		response.setContentType(TEXT_PLAIN);
		
		// Set the headers to not cache
		setNoCacheHeaders(response);
		
//		String queryString = baseRequest.getQueryString();
//		String requestURL = baseRequest.getRequestURL().toString();
//		String contextPath = baseRequest.getContextPath();
//		Map parameterMap = baseRequest.getParameterMap();
//		OutputStream responseOutputStream = response.getOutputStream();		
//		String methodString = request.getMethod();
		boolean requestWasHandled = false;

		// Get the method; null if it isn't supported.
//		Method method = Method.getMethod(methodString);

		handleHandler(driverHandler, target, request, response, dispatch);
	}
}
