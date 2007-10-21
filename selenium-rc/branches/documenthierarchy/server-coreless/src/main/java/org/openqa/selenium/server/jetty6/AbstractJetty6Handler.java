package org.openqa.selenium.server.jetty6;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.ResourceHandler;
import org.openqa.selenium.server.jetty.Handler;
import org.openqa.selenium.server.jetty.Handler.Method;

/**
 * Abstract implementation for a Jetty 6 resource handler.
 * 
 * @author Matthew Purland
 */
public abstract class AbstractJetty6Handler extends ResourceHandler {
	/**
	 * Sets all the don't-cache headers on the response.
	 */
	protected void setNoCacheHeaders(HttpServletResponse response) {
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		response.setHeader(HttpHeaders.PRAGMA, "no-cache");
		response.setHeader(HttpHeaders.EXPIRES, HttpFields.__01Jan1970);
	}
	
	public void handleHandler(Handler handler, String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException {
		Request baseRequest = (request instanceof Request) ? (Request) request
				: HttpConnection.getCurrentConnection().getRequest();
		
		String queryString = baseRequest.getQueryString();
		String requestURL = baseRequest.getRequestURL().toString();
		String contextPath = baseRequest.getContextPath();
		Map parameterMap = baseRequest.getParameterMap();
		OutputStream responseOutputStream = response.getOutputStream();		
		String methodString = request.getMethod();
		boolean requestWasHandled = false;

		// Get the method; null if it isn't supported.
		Method method = Method.getMethod(methodString);
		
		if (method != null) {
			// Create a new byte array output stream
			// The handler should not be able to flush the stream, thus a
			// separate output stream is passed to it
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			InputStream requestInputStream = request.getInputStream();
			
			// @todo need to add requestScheme, requestHost, and requestPort to be passed
			requestWasHandled = handler.handle(requestURL, contextPath, queryString, parameterMap, method, requestInputStream, byteArrayOutputStream);
			
			if (requestWasHandled) {
				// Flush the finished output stream from the handler
				byteArrayOutputStream.flush();
	
				// Write the output stream from the handler to the response output stream
				responseOutputStream.write(byteArrayOutputStream.toByteArray());
				
				// Flush the response output stream
				responseOutputStream.flush();
			}
		}
		
		baseRequest.setHandled(requestWasHandled);
		
		if (requestWasHandled) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else if (method == null) {
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}
		else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public abstract void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException;
}
