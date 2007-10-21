package org.openqa.selenium.server.jetty;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class AbstractHandler implements Handler {
	/**
	 * Get the specified parameter from the parameter map as a single value instead of an array.
	 * 
	 * @param parameterName
	 *            Name of the parameter
	 * @param parameterMap
	 *            The parameter map
	 * @return Returns the specified parameter value.
	 */
	public String getParameter(String parameterName, Map parameterMap) {
		String[] commandValues = (String[]) parameterMap.get(parameterName);
		String value = null;

		if (commandValues != null && commandValues.length > 0) {
			value = commandValues[0];
		}

		return value;
	}
	
	protected ByteArrayOutputStream getByteArrayOutputStreamFromInputStream(InputStream inputStream) throws IOException {
		// 64kb for each array
		final int ARRAY_SIZE = 64 * 1024;
		byte[] byteArray = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		while (inputStream.available() > 0) {
			int bytesAvailable = inputStream.available();
			
			if (bytesAvailable < ARRAY_SIZE || byteArray == null) {
				byteArray = new byte[bytesAvailable];
			}
			
			int bytesRead = inputStream.read(byteArray);

			dos.write(byteArray);
		}

		dos.flush();
		bos.flush();
		return bos;
	}
	
	/**
	 * Returns a single byte array from the entire input stream available data. This method will not
	 * block.
	 * 
	 * @param inputStream
	 *            The input stream
	 * @return Returns a single byte array of data.
	 * @throws IOException
	 *             when an I/O problem occurs when reading from the input stream
	 */
	protected byte[] getByteArrayFromInputStream(InputStream inputStream)
			throws IOException {
		return getByteArrayOutputStreamFromInputStream(inputStream).toByteArray();
	}
	
	protected String getStringUTF8FromInputStream(InputStream inputStream) throws IOException {
		return getByteArrayOutputStreamFromInputStream(inputStream).toString("UTF-8");
	}
	
	/**
	 * Returns portion of requestURL that is after the given context path.
	 * 
	 * As in the parameters this should return /some/resource
	 * 
	 * @param requestURL
	 *            The request URL such as http://localhost:4444/selenium-server/some/resource
	 * @param contextPath
	 *            The context path such as /selenium-server
	 * @return Returns context path for a given resource.
	 */
	protected String getResourceContextPath(String requestURL,
			String contextPath) {
		String resourceContextPath = "";

//		if (requestURL.contains(contextPath)) {
//
//			resourceContextPath = requestURL.substring(requestURL
//					.indexOf(contextPath)
//					+ contextPath.length(), requestURL.length());
//		}
		
//		return resourceContextPath;
		
		return contextPath;
	}
}
