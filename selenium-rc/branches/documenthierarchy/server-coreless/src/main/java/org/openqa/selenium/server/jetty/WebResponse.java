package org.openqa.selenium.server.jetty;

import java.io.OutputStream;

/**
 * Wrapper to extract implementation of web server specific and allow
 * implementation for a response such as HttpResponse.
 * 
 * @author Matthew Purland
 */
public interface WebResponse {
	void removeField(String field);
	void setField(String field, String value);
	String getContentType();
	OutputStream getOutputStream();
	void setStatus(int status);
	int getStatus();
}
