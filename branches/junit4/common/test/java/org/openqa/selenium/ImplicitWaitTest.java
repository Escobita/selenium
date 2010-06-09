package org.openqa.selenium;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

/**
 * @author jmleyba@gmail.com (Jason Leyba)
 */
@Ignore(value = {IPHONE})
public class ImplicitWaitTest extends AbstractDriverTestCase {

  @Before
  public void setUp() throws Exception {
    driver.manage().timeouts().implicitlyWait(0, MILLISECONDS);
  }

  @After
  public void tearDown() throws Exception {
    driver.manage().timeouts().implicitlyWait(0, MILLISECONDS);
  }

  @JavascriptEnabled
  @Test public void shouldImplicitlyWaitForASingleElement() {
    driver.get(pages.dynamicPage);
    WebElement add = driver.findElement(By.id("adder"));

    driver.manage().timeouts().implicitlyWait(1100, MILLISECONDS);

    add.click();
    driver.findElement(By.id("box0"));  // All is well if this doesn't throw.
  }

  @JavascriptEnabled
  @Test public void shouldStillFailToFindAnElementWhenImplicitWaitsAreEnabled() {
    driver.get(pages.dynamicPage);
    driver.manage().timeouts().implicitlyWait(500, MILLISECONDS);
    try {
      driver.findElement(By.id("box0"));
      fail("Expected to throw.");
    } catch (NoSuchElementException expected) {
    }
  }

  @JavascriptEnabled
  @Test public void shouldReturnAfterFirstAttemptToFindOneAfterDisablingImplicitWaits() {
    driver.get(pages.dynamicPage);
    driver.manage().timeouts().implicitlyWait(1100, MILLISECONDS);
    driver.manage().timeouts().implicitlyWait(0, MILLISECONDS);
    try {
      driver.findElement(By.id("box0"));
      fail("Expected to throw.");
    } catch (NoSuchElementException expected) {
    }
  }

  @JavascriptEnabled
  @Ignore(SELENESE)
  @Test public void shouldImplicitlyWaitUntilAtLeastOneElementIsFoundWhenSearchingForMany() {
    driver.get(pages.dynamicPage);
    WebElement add = driver.findElement(By.id("adder"));

    driver.manage().timeouts().implicitlyWait(2000, MILLISECONDS);
    add.click();
    add.click();

    List<WebElement> elements = driver.findElements(By.className("redbox"));
    assertFalse(elements.isEmpty());
  }

  @JavascriptEnabled
  @Ignore(SELENESE)
  @Test public void shouldStillFailToFindElementsWhenImplicitWaitsAreEnabled() {
    driver.get(pages.dynamicPage);
    driver.manage().timeouts().implicitlyWait(500, MILLISECONDS);
    List<WebElement> elements = driver.findElements(By.className("redbox"));
    assertTrue(elements.isEmpty());
  }

  @JavascriptEnabled
  @Ignore(SELENESE)
  @Test public void shouldReturnAfterFirstAttemptToFindManyAfterDisablingImplicitWaits() {
    driver.get(pages.dynamicPage);
    WebElement add = driver.findElement(By.id("adder"));

    driver.manage().timeouts().implicitlyWait(1100, MILLISECONDS);
    driver.manage().timeouts().implicitlyWait(0, MILLISECONDS);
    add.click();

    List<WebElement> elements = driver.findElements(By.className("redbox"));
    assertTrue(elements.isEmpty());
  }
}
