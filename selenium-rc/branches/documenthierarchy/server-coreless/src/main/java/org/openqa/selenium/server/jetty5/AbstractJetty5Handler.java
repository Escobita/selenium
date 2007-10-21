package org.openqa.selenium.server.jetty5;

import java.io.IOException;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.ResourceHandler;
import org.openqa.selenium.server.jetty.Handler;
import org.openqa.selenium.server.jetty.WebHandler;
import org.openqa.selenium.server.jetty.WebRequest;
import org.openqa.selenium.server.jetty.WebResponse;

/**
 * Abstract implementation for a Jetty 5 handler.  This should be extended
 * by any other Jetty 5 handlers.
 * 
 * @author Matthew Purland
 */
public abstract class AbstractJetty5Handler extends ResourceHandler {
	
	private WebHandler webHandler;
	
	public AbstractJetty5Handler(WebHandler webHandler) {
		this.webHandler = webHandler;
	}
	
	/**
	 * Sets all the don't-cache headers on the response.
	 */
	protected void setNoCacheHeaders(HttpResponse response) {
		response.setField(HttpFields.__CacheControl, "no-cache");
		response.setField(HttpFields.__Pragma, "no-cache");
		response.setField(HttpFields.__Expires, HttpFields.__01Jan1970);
	}

	/**
	 * @todo Can redo beginning translation logic jetty5/jetty6 into injected handler helper
	 * 
	 * @param handler
	 * @param pathInContext
	 * @param pathParams
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void handleHandler(Handler handler, String pathInContext,
			String pathParams, HttpRequest request, HttpResponse response)
			throws IOException {
		WebRequest webRequest = new Jetty5WebRequest(request);
		WebResponse webResponse = new Jetty5WebResponse(response);
		
		webHandler.handleHandler(handler, pathInContext, webRequest, webResponse);
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract void handle(String pathInContext, String pathParams,
			HttpRequest request, HttpResponse response) throws HttpException,
			IOException;
}
