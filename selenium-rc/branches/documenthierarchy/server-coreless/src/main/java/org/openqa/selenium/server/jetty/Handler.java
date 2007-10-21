package org.openqa.selenium.server.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Handler for handling requests independent of a web server implementation.
 * 
 * @author Matthew Purland
 */
public interface Handler {
	/**
	 * Method for an HTTP request.
	 */
	public enum Method {
		GET("GET"), POST("POST"), CONNECT("CONNECT");

		private final String method;

		Method(String method) {
			this.method = method;
		}

		/**
		 * Get the method by the method string.
		 * 
		 * @param method
		 *            The method string
		 * @return Returns the method by the method string; null if not found.
		 */
		public static Method getMethod(String method) {
			for (Method valueMethod : Method.values()) {
				if (valueMethod.getMethod().equals(method)) {
					return valueMethod;
				}
			}

			return null;
		}

		/**
		 * Get the method.
		 * 
		 * @return Returns the method.
		 */
		public String getMethod() {
			return method;
		}

	}

	/**
	 * Handle a request passed to the handler. A web server handler that is handling this should
	 * only write the underlying output stream to the response output stream if this method returns
	 * true.
	 * 
	 * @param requestURL
	 *            The request URL
	 * @param contextPath
	 *            Path for the context /some/resource
	 * @param queryString
	 *            The query string
	 * @param parameterMap
	 *            Map of parameter and values in which keys/values are strings/string[].
	 * @param method
	 *            The method used
	 * @param outputStream
	 *            The output stream to write to. Output written will not take effect until the
	 *            request is successfully handled, but placed in an output stream.
	 * 
	 * @throws IOException when an I/O exception occurs such as a file cannot be read, etc...
	 * 
	 * @return Returns true if the request was handled.
	 */
	boolean handle(String contextPath, String queryString, Map parameterMap,
			Method method, WebRequest webRequest, WebResponse webResponse, OutputStream outputStream)  throws IOException;
}
