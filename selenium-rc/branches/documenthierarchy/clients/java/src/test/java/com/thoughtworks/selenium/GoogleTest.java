package com.thoughtworks.selenium;

import org.openqa.selenium.server.browser.BrowserType;

public class GoogleTest extends SeleneseTestCase {

    public void setUp() throws Exception {
        String url = "http://www.google.com";
        setUp(url, BrowserType.Browser.FIREFOX.toString());
    }

    public void testGoogle() throws Throwable {
        selenium.open("http://www.google.com/webhp?hl=en");

        assertEquals("Google", selenium.getTitle());
        selenium.type("q", "Selenium OpenQA");
        assertEquals("Selenium OpenQA", selenium.getValue("q"));
        selenium.click("btnG");
        selenium.waitForPageToLoad("5000");
        assertEquals("Selenium OpenQA - Google Search", selenium.getTitle());
    }

}
