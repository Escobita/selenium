package org.openqa.selenium.remote.server;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Automated test runner for the JS API.
 * 
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class JsApiTestSuite extends TestCase {

  private static final Logger LOG =
      Logger.getLogger(JsApiTestSuite.class.getName());
  private static final String TEST_PATH = "selenium/tests";
  private static final JsApiTestServer TEST_SERVER = new JsApiTestServer();

  // NOTE(jmleyba): If we instantiate FirefoxDriver inside of suite(), the
  // profile opens with a security error, causing tests to fail. This does not
  // happen if we start it inside the TestSetup.
  private static final JsApiClosure<WebDriver> DRIVER_CLOSURE =
      new JsApiClosure<WebDriver>();

  public static Test suite() throws Exception {
    TestSuite suite = new TestSuite();
    suite.setName(JsApiTestSuite.class.getName());

    LOG.info("Searching for test files");
    File rootDir = JsApiTestServer.getRootDirectory();
    File testsDir = new File(rootDir, TEST_PATH);
    for (File file : testsDir.listFiles(new TestFilenameFilter())) {
      String path =
          file.getAbsolutePath().replace(rootDir.getAbsolutePath() + "/", "");
      URL url = new URL(TEST_SERVER.whereIs("/remote", path));
      TestCase test = new JsApiTestCase(url, DRIVER_CLOSURE);
      LOG.info("Adding test: " + test.getName());
      suite.addTest(test);
    }

    return new TestSetup(suite) {
      @Override
      protected void setUp() throws Exception {
        Thread t = new Thread() {
          @Override
          public void run() {
          }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(1000);
        TEST_SERVER.start();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(false);  // Native events aren't 100% yet.
        DRIVER_CLOSURE.set(new FirefoxDriver(profile));
      }

      @Override
      protected void tearDown() throws Exception {
        TEST_SERVER.stop();
        DRIVER_CLOSURE.get().quit();
      }
    };
  }

  private static class TestFilenameFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
      return name.endsWith("_test.html");
    }
  }
}
