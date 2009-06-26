package org.openqa.selenium.chromium;

import org.openqa.selenium.TestSuiteBuilder;
import org.openqa.selenium.NeedsFreshDriver;
import org.openqa.selenium.Ignore;
import static org.openqa.selenium.Ignore.Driver.CHROME;

import junit.framework.Test;
import junit.framework.TestCase;

public class ChromeDriverTestSuite extends TestCase {

	public static Test suite() throws Exception {
		return new TestSuiteBuilder()
					.addSourceDir("common")
					.addSourceDir("chrome")
					.usingDriver(ChromeDriver.class)
					.exclude(CHROME)
          .includeJavascriptTests()
          .keepDriverInstance()
					.create();
	}
}
