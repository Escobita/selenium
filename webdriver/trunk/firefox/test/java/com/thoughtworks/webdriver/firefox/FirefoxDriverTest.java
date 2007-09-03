package com.thoughtworks.webdriver.firefox;

import com.thoughtworks.webdriver.JavascriptEnabledDriverTest;
import com.thoughtworks.webdriver.NoSuchElementException;
import com.thoughtworks.webdriver.WebDriver;

import java.io.File;

public class FirefoxDriverTest extends JavascriptEnabledDriverTest {
    protected WebDriver getDriver() {
        String firefoxBinary = System.getProperty("firefox.bin");
        File binary = null;
        if (firefoxBinary != null) {
            System.out.println("Using: " + firefoxBinary);
            binary = new File(firefoxBinary);
        }
        return new FirefoxDriver(binary);
    }

    protected boolean isUsingSameDriverInstance() {
        return true;
    }

    public void testShouldContinueToWorkIfUnableToFindElementById() {
        driver.get(formPage);

        try {
            driver.selectElement("id=notThere");
            fail("Should not be able to select element by id here");
        } catch (NoSuchElementException e) {
            // This is expected
        }

        // Is this works, then we're golden
        driver.get(xhtmlTestPage);
    }

    public void testShouldSwitchFocusToANewWindowWhenItIsOpenedAndNotStopFutureOperations() {
        driver.get(xhtmlTestPage);

        driver.selectElement("link=Open new window").click();
        assertEquals("XHTML Test Page", driver.getTitle());

        driver.switchTo().window("result");
        assertEquals("We Arrive Here", driver.getTitle());

        driver.get(iframePage);
        driver.selectElement("id=iframe_page_heading");
    }
}
