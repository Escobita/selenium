package org.openqa.selenium.server.jetty;

import java.io.ByteArrayOutputStream;

import org.openqa.selenium.server.SeleniumUnitTestBase;

public class DriverHandlerTest extends SeleniumUnitTestBase {
	/**
	 * Open a browser and send command through driver handler.
	 */
	public void testHandleGetNewBrowserSession() {		
		final String command = "getNewBrowserSession";
		final String browserString = "*mock";
		final String startURL = "http://localhost:4444";
		
		DriverHandler driverHandler = getSeleniumDependencyManager().getDriverHandler();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
//		driverHandler.handle(contextPath, queryString, parameterMap, method, webRequest, webResponse, outputStream);
	}
}
