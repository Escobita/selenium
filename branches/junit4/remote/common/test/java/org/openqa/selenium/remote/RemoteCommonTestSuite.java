package org.openqa.selenium.remote;

import org.openqa.selenium.TestSuiteBuilder;


import org.junit.Test; import static org.junit.Assert.*;

public class RemoteCommonTestSuite {

  public static Test suite() throws Exception {
    return new TestSuiteBuilder()
        .addSourceDir("remote/common")
        .usingNoDriver()
        .create();
  }
}
