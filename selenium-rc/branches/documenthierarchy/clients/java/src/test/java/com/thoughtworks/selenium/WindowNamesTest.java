package com.thoughtworks.selenium;

import org.openqa.selenium.server.browser.BrowserType;

public class WindowNamesTest extends SeleneseTestCase
{

   public void setUp() throws Exception {
		String url = "http://www.google.com";
		setUp(url, BrowserType.Browser.FIREFOX.toString());
   }
   
   public void testWindowNames() throws Throwable {
		selenium.open("http://www.google.com/webhp?hl=en");
        
		assertEquals("Google", selenium.getTitle());
		String[] windowNames = selenium.getAllWindowNames();
        for (int i = 0; i < windowNames.length; i++) {
            String windowName = windowNames[i];
            System.out.println("Window Name: " + windowName);
        }
        selenium.selectWindow(null);
        String[] windowIds = selenium.getAllWindowNames();
        for (int i = 0; i < windowIds.length; i++) {
            String windowId = windowIds[i];
            System.out.println("Window Id: " + windowId);
        }
        String[] windowTitles = selenium.getAllWindowTitles();
        for (int i = 0; i < windowTitles.length; i++) {
            String windowTitle = windowTitles[i];
            System.out.println("Window Title: " + windowTitle);
        }
        //selenium.setSpeed("500");
        selenium.selectWindow("Google");
        
		selenium.type("q", "Selenium OpenQA");
		assertEquals("Selenium OpenQA", selenium.getValue("q"));
        // TODO DGF pulling over the logs seems to be breaking the build
		// String s = selenium.getLogMessages();
        // System.out.println("The log messages are the following:\n" + s);
		selenium.click("btnG");
		selenium.waitForPageToLoad("5000");
		
		assertEquals("Selenium OpenQA - Google Search", selenium.getTitle());
	}
	
}
