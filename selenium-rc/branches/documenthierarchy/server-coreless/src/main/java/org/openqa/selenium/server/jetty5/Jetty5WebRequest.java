package org.openqa.selenium.server.jetty5;

import java.io.InputStream;
import java.util.Map;

import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpRequest;
import org.openqa.selenium.server.jetty.WebRequest;

/**
 * Jetty 5 implementation of WebRequest. Wrapper for Jetty 5 specific
 * implementation around HttpRequest.
 * 
 * @author Matthew Purland
 */
public class Jetty5WebRequest implements WebRequest {
	private HttpRequest httpRequest;
	
	public Jetty5WebRequest(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHost() {
		return httpRequest.getHost();
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream getInputStream() {
		return httpRequest.getInputStream();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMethod() {
		return httpRequest.getMethod();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getParameterStringArrayMap() {
		return httpRequest.getParameterStringArrayMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPath() {
		return httpRequest.getPath();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPort() {
		return httpRequest.getPort();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getQuery() {
		return httpRequest.getQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRequestURL() {
		return httpRequest.getRequestURL().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getScheme() {
		return httpRequest.getScheme();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHandled(boolean handled) {
		httpRequest.setHandled(handled);
	}

	public void setField(String field, String value) {
		httpRequest.setField(field, value);
	}

	public String getField(String name) {
		return httpRequest.getField(name);
}
}
