package org.openqa.selenium.iphone;

import org.junit.Test;
import org.openqa.selenium.AbstractDriverTestCase;

public class CreateIPhoneDriverTest extends AbstractDriverTestCase {
  @Test public void createDriver() throws Exception {
    new IPhoneDriver();
  }

  @Test public void deleteSession() throws Exception {
    IPhoneDriver driver = new IPhoneDriver();
    driver.quit();
  }

  @Test public void createDriverWithTrailingSlash() throws Exception {
    new IPhoneDriver(IPhoneDriver.DEFAULT_IWEBDRIVER_URL + "/");
  }
}
