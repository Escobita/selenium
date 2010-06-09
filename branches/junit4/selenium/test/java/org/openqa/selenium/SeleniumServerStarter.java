package org.openqa.selenium;

import junit.extensions.TestSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test; import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.openqa.selenium.internal.PortProber;

import static org.openqa.selenium.internal.PortProber.pollPort;

public class SeleniumServerStarter {

  private static final String SELENIUM_JAR = "build/selenium-server-standalone.jar";
  private Process serverProcess;

  @Before
  public void setUp() throws Exception {
    // Walk up the path until we find the "third_party/selenium" directory
    File seleniumJar = findSeleniumJar();

    if (!seleniumJar.exists()) {
      throw new IllegalStateException("Cannot locate selenium jar");
    }

    String port = startSeleniumServer(seleniumJar);

    // Wait until the server process is running (port 4444)
    if (!pollPort(Integer.valueOf(port))) {
      throw new RuntimeException("Unable to start selenium server");
    }

    new Thread(new Runnable() {
      public void run() {
        InputStream is = serverProcess.getInputStream();
        try {
          int c = 0;
          while ((c = is.read()) != -1) {
            System.err.print((char) c);
          }
        } catch (IOException e) {
        }
      }
    }).start();
  }

  private String startSeleniumServer(File seleniumJar) throws IOException {
    String port = System.getProperty("webdriver.selenium.server.port", "5555");
    ProcessBuilder builder = new ProcessBuilder(
        "java", "-jar", seleniumJar.getAbsolutePath(),
        "-port", port);
    builder.redirectErrorStream(true);
    serverProcess = builder.start();
    return port;
  }

  private File findSeleniumJar() {
    File dir = new File(".");
    File seleniumJar = null;
    while (dir != null) {
      seleniumJar = new File(dir, SELENIUM_JAR);
      if (seleniumJar.exists()) {
        break;
      }
      dir = dir.getParentFile();
    }
    return seleniumJar;
  }

  @After
  public void tearDown() throws Exception {
    if (serverProcess != null) {
      serverProcess.destroy();
    }
  }
}
