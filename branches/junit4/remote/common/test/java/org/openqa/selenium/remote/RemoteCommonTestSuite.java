package org.openqa.selenium.remote;

import org.junit.Test;
import org.openqa.selenium.TestSuiteBuilder;

public class RemoteCommonTestSuite {

  public static Test suite() throws Exception {
    return new TestSuiteBuilder()
        .addSourceDir("remote/common")
        .usingNoDriver()
        .create();
  }
}
