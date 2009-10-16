package org.openqa.selenium.remote.server;

import org.openqa.selenium.TestSuiteBuilder;

import junit.framework.TestCase;
import junit.framework.Test;

/**
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class RemoteServerTestSuite extends TestCase {
  public static Test suite() throws Exception {
    return new TestSuiteBuilder()
        .addSourceDir("remote/server")
        .usingNoDriver()
        .create();
  }
}
