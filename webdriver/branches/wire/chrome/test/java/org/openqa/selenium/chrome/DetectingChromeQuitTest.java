// Copyright 2009 Google Inc.  All Rights Reserved.

package org.openqa.selenium.chrome;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.NoDriverAfterTest;
import org.openqa.selenium.Platform;

/**
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class DetectingChromeQuitTest extends AbstractDriverTestCase {

  @NoDriverAfterTest
  public void testShouldDetectChromeDiedWhenTheProcessIsNotKilledByTheChromeBinary() {
    killChrome();

    try {
      driver.getWindowHandles();
      fail("Should throw a DeadChromeException");
    } catch (DeadChromeException expected) {
      // Do nothing
    }
  }

  private void killChrome() {
    ChromeCommandExecutor executor = (ChromeCommandExecutor) ((ChromeDriver) driver).getExecutor();
    executor.getChromeBinary()
        .getChromeProcess()
        .destroy();
  }
}
