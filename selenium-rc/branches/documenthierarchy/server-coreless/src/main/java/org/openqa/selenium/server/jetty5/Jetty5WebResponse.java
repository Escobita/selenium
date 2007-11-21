package org.openqa.selenium.server.jetty5;

import java.io.OutputStream;

import org.mortbay.http.HttpResponse;
import org.openqa.selenium.server.jetty.WebResponse;

/**
 * Jetty 5 implementation of WebResponse. Wrapper for Jetty 5 specific
 * implementation around HttpResponse.
 * 
 * @author Matthew Purland
 */
public class Jetty5WebResponse implements WebResponse {

	private HttpResponse httpResponse;

	public Jetty5WebResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeField(String field) {
		httpResponse.removeField(field);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getContentType() {
		return httpResponse.getContentType();
	}

	/**
	 * {@inheritDoc}
	 */
	public OutputStream getOutputStream() {
		return httpResponse.getOutputStream();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setStatus(int status) {
		httpResponse.setStatus(status);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setField(String field, String value) {
		httpResponse.setField(field, value);
	}

	public int getStatus() {
		return httpResponse.getStatus();
}

}
