package com.thoughtworks.selenium;

import org.openqa.selenium.server.browser.BrowserType;

public class AttachFileTest extends SeleneseTestCase {

    public void setUp() throws Exception {
        String url = "http://www.snipshot.com";
        setUp(url, BrowserType.Browser.CHROME.toString());
    }

    public void testAttachfile() throws Throwable {
		selenium.open("/");
		assertEquals("Snipshot: Edit pictures online", selenium.getTitle());
		
		selenium.attachFile("file", "http://www.google.com/intl/en_ALL/images/logo.gif");
		
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("save")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		assertTrue(selenium.isElementPresent("resize"));
		assertTrue(selenium.isElementPresent("crop"));
        
        
    }

}
