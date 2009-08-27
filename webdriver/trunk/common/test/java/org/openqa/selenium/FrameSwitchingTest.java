/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.openqa.selenium;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.openqa.selenium.Ignore.Driver.CHROME;
import static org.openqa.selenium.Ignore.Driver.FIREFOX;
import static org.openqa.selenium.Ignore.Driver.IPHONE;

public class FrameSwitchingTest extends AbstractDriverTestCase {
    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldContinueToReferToTheSameFrameOnceItHasBeenSelected() {
        driver.get(framesetPage);

        driver.switchTo().frame(2);
        WebElement checkbox = driver.findElement(By.xpath("//input[@name='checky']"));
        checkbox.toggle();
        checkbox.submit();

        assertThat(driver.findElement(By.xpath("//p")).getText(), equalTo("Success!"));
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldAutomaticallyUseTheFirstFrameOnAPage() {
        driver.get(framesetPage);

        // Notice that we've not switched to the 0th frame
        WebElement pageNumber = driver.findElement(By.xpath("//span[@id='pageNumber']"));
        assertThat(pageNumber.getText().trim(), equalTo("1"));
    }

    @Ignore({CHROME, FIREFOX}) //TODO(danielwh): This test is being very flaky for me in Firefox, I'll look into it
	public void testShouldFocusOnTheReplacementWhenAFrameFollowsALinkToA_TopTargettedPage() {
        driver.get(framesetPage);

        driver.findElement(By.linkText("top")).click();

        assertThat(driver.getTitle(), equalTo("XHTML Test Page"));
        assertThat(driver.findElement(By.xpath("/html/head/title")).getText(), equalTo("XHTML Test Page"));
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldNotAutomaticallySwitchFocusToAnIFrameWhenAPageContainingThemIsLoaded() {
        driver.get(iframePage);
        driver.findElement(By.id("iframe_page_heading"));
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldAllowAUserToSwitchFromAnIframeBackToTheMainContentOfThePage() {
        driver.get(iframePage);
        driver.switchTo().frame(0);

        try {
            driver.switchTo().defaultContent();
            driver.findElement(By.id("iframe_page_heading"));
        } catch (Exception e) {
            fail("Should have switched back to main content");
        }
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldAllowTheUserToSwitchToAnIFrameAndRemainFocusedOnIt() {
        driver.get(iframePage);
        driver.switchTo().frame(0);

        driver.findElement(By.id("submitButton")).click();
        String hello = driver.findElement(By.id("greeting")).getText();
        assertThat(hello, equalTo("Success!"));
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldBeAbleToSelectAFrameByName() {
        driver.get(framesetPage);

        driver.switchTo().frame("second");
        assertThat(driver.findElement(By.id("pageNumber")).getText(), equalTo("2"));
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldSelectChildFramesByUsingADotSeparatedString() {
        driver.get(framesetPage);

        driver.switchTo().frame("fourth.child2");
        assertThat(driver.findElement(By.id("pageNumber")).getText(), equalTo("11"));
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldSwitchToChildFramesTreatingNumbersAsIndex() {
        driver.get(framesetPage);

        driver.switchTo().frame("fourth.1");
        assertThat(driver.findElement(By.id("pageNumber")).getText(), equalTo("11"));
    }

    @NoDriverAfterTest
    @Ignore({IPHONE, CHROME})
    public void testClosingTheFinalBrowserWindowShouldNotCauseAnExceptionToBeThrown() {
        driver.get(simpleTestPage);
        try {
        	driver.close();
        } catch (Exception e) {
        	fail("This is not expected. " + e.getMessage());
        }
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
    public void testShouldBeAbleToFlipToAFrameIdentifiedByItsId() {
        driver.get(framesetPage);

        driver.switchTo().frame("fifth");

        try {
            driver.findElement(By.id("username"));
        } catch (NoSuchElementException e) {
            fail("Driver did not switch by frame id");
        }
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldThrowAnExceptionWhenAFrameCannotBeFound() {
        driver.get(xhtmlTestPage);

        try {
            driver.switchTo().frame("Nothing here");
            fail("Should not have been able to switch");
        } catch (NoSuchFrameException e) {
            // This is expected
        }
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldThrowAnExceptionWhenAFrameCannotBeFoundByIndex() {
        driver.get(xhtmlTestPage);

        try {
            driver.switchTo().frame(27);
            fail("Should not have been able to switch");
        } catch (NoSuchFrameException e) {
            // This is expected
        }
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldBeAbleToFindElementsInIframesByName() {
        driver.get(iframePage);

        driver.switchTo().frame("iframe1");
        WebElement element = driver.findElement(By.name("id-name1"));

        assertNotNull(element);
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testShouldBeAbleToFindElementsInIframesByXPath() {
        driver.get(iframePage);

        driver.switchTo().frame("iframe1");

        WebElement element = driver.findElement(By.xpath("//*[@id = 'changeme']"));

        assertNotNull(element);
    }

    @Ignore(value = CHROME, reason = "Not yet implemented")
	public void testGetCurrentUrl() {
        driver.get(framesetPage);

        driver.switchTo().frame("second");
        assertThat(driver.getCurrentUrl(), equalTo("http://localhost:3000/common/page/2?title=Fish"));

        driver.get(iframePage);
        assertThat(driver.getCurrentUrl(), equalTo("http://localhost:3000/common/iframes.html"));

        driver.switchTo().frame("iframe1");
        assertThat(driver.getCurrentUrl(), equalTo("http://localhost:3000/common/formPage.html"));
    }
}
