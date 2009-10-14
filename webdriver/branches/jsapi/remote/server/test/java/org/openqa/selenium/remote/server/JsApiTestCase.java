package org.openqa.selenium.remote.server;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import junit.framework.TestCase;

import java.net.URL;

/**
 * Runs a WebDriverJS test.
 *
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class JsApiTestCase extends TestCase {

  private static final long TWO_MINUTES = 2 * 60 * 1000;

  private final URL testUrl;
  private final WebDriver driver;

  public JsApiTestCase(URL testUrl, WebDriver driver) {
    this.testUrl = testUrl;
    this.driver = driver;
    this.setName(testUrl.getPath());
  }

  @Override
  protected void runTest() throws Throwable {
    driver.get(testUrl.toString());
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    long start = System.currentTimeMillis();

    do {
      Object result = executor.executeScript(Query.IS_FINISHED.script);
      if (null != result && (Boolean) result) {
        break;
      }

      long now = System.currentTimeMillis();
      long ellapsed = now - start;
      assertTrue("TIMEOUT after " + ellapsed + "ms", ellapsed <= TWO_MINUTES);
    } while (true);

    Object result = executor.executeScript(Query.NUM_PASSED.script);
    Long numPassed = result == null ? 0: (Long) result;

    result = executor.executeScript(Query.NUM_TESTS.script);
    Long numTests = result == null ? 0: (Long) result;

    String report = (String) executor.executeScript(Query.GET_REPORT.script);
    assertEquals(report, numTests, numPassed);
    assertTrue("No tests run!", numTests >= 0);
  }

  private static enum Query {
    IS_FINISHED("return !!wd && wd.TestRunner.SINGLETON.isFinished();"),
    NUM_PASSED("return wd.TestRunner.SINGLETON.getNumPassed();"),
    NUM_TESTS("return wd.TestRunner.SINGLETON.getNumTests();"),
    GET_REPORT("return wd.TestRunner.SINGLETON.getReport();");

    private final String script;

    private Query(String script) {
      this.script = "var wd = window.webdriver; " + script;
    }
  }
}
