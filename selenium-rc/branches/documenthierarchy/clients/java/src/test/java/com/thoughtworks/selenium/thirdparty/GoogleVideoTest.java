package com.thoughtworks.selenium.thirdparty;

import org.openqa.selenium.server.browser.BrowserType;

import com.thoughtworks.selenium.SeleneseTestCase;

public class GoogleVideoTest extends SeleneseTestCase {

	public void setUp() throws Exception {
        setUp("http://video.google.com", BrowserType.Browser.PI_FIREFOX.toString());
    }

    public void testGoogle() {
        selenium.open("/");
        selenium.type("q", "hello world");
        selenium.click("button-search");
        selenium.waitForPageToLoad("5000");
    }
}
