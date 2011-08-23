package org.openqa.selenium;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.Ignore.Driver.ANDROID;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;

@Ignore(value = ANDROID, reason = "Android: Race condition when click returns, "
    + "the UI did not finish scrolling..")
public class ClickAnchorTest extends AbstractDriverTestCase {
  @Ignore(value = HTMLUNIT, reason = "Page scrolling requires rendering")
  @JavascriptEnabled
  public void testClickingOnAnchorScrollsPage() {
    String scrollScript = "var pageY;";
    scrollScript += "if (typeof(window.pageYOffset) == 'number') {";
    scrollScript += "pageY = window.pageYOffset;";
    scrollScript += "} else {";
    scrollScript += "pageY = document.documentElement.scrollTop;";
    scrollScript += "}";
    scrollScript += "return pageY;";

    driver.get(pages.macbethPage);

    driver.findElement(By.partialLinkText("last speech")).click();

    long yOffset = (Long) ((JavascriptExecutor) driver)
        .executeScript(scrollScript);

    //Focusing on to click, but not actually following,
    //the link will scroll it in to view, which is a few pixels further than 0 
    assertThat("Did not scroll", yOffset, is(greaterThan(300L)));

  }
}
