package org.openqa.selenium;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;


import org.junit.Test; import static org.junit.Assert.*;

/**
 * Test suite for running the WebDriver JS API test cases against all of its
 * supported browsers.
 *
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class JsApiTestSuite {

  public static Test suite() throws Exception {
//    TestSuite all = new TestSuite();
//    all.setName(JsApiTestSuite.class.getSimpleName());
//    all.addTest(createDriverSuite(FirefoxDriver.class, Ignore.Driver.FIREFOX));
//    all.addTest(createDriverSuite(ChromeDriver.class, Ignore.Driver.CHROME));
//    return all;
    fail("ouch");
    return null;
  }

  private static Test createDriverSuite(Class<? extends WebDriver> driverClass,
                                        Ignore.Driver driverTag) throws Exception {
    return new TestSuiteBuilder()
        .usingDriver(driverClass)
        .exclude(driverTag)
        .includeJsApiTests()
        .create();
  }
}
