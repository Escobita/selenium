package org.openqa.selenium.server.browser;

import org.openqa.selenium.server.browser.BrowserType.Browser;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Test case to test correct functionality of BrowserType.
 * 
 * @author Matthew Purland
 */
public class BrowserTypeTest extends TestCase {
	
	public void assertNotEquals(Object obj1, Object obj2) {
		boolean isEqual1 = obj1.equals(obj2);
		boolean isEqual2 = obj2.equals(obj1);
		
		if (isEqual1 || isEqual2) {
			throw new AssertionFailedError("Given objects are equal.");
		}
	}
	
	/**
	 * Assert that the enums in BrowserType are correct.
	 */
	public void testBrowserTypeValues() {
		Browser browserTypeFirefox = Browser.FIREFOX;
		Browser browserTypeIexplore = Browser.IEXPLORE;
		Browser browserTypeCustom = Browser.CUSTOM;
		Browser browserTypeChrome = Browser.CHROME;
		Browser browserTypeIehta = Browser.IEHTA;
		Browser browserTypePIFirefox = Browser.PI_FIREFOX;
		Browser browserTypePIIExplore = Browser.PI_IEXPLORE;
		
		assertEquals("*firefox", browserTypeFirefox.toString());
		assertEquals("*iexplore", browserTypeIexplore.toString());
		assertEquals("*custom", browserTypeCustom.toString());
		assertEquals("*chrome", browserTypeChrome.toString());
		assertEquals("*iehta", browserTypeIehta.toString());
		assertEquals("*pifirefox", browserTypePIFirefox.toString());
		assertEquals("*piiexplore", browserTypePIIExplore.toString());
	}
	
	/**
	 * Test that the getBrowserType method works correctly.
	 */
	public void testBrowserStringForBrowserAndBrowserString() {
		final String pathToBrowser = "/path/to/browser";
		
		final Browser browserFirefox = Browser.FIREFOX;
		final String browserString1 = browserFirefox.toString() + " " + pathToBrowser;
		
		BrowserType browserType1 = BrowserType.getBrowserType(browserString1);
		
		// Assert the browser type is set
		assertEquals(browserFirefox.toString() + " " + pathToBrowser, browserType1.toString());
		
		// Assert the path to the browser is set
		assertEquals(pathToBrowser, browserType1.getPathToBrowser());
	}
	
	/**
	 * Test that the getBrowserType works correctly for a single browser.
	 */
	public void testBrowserStringForSingleBrowser() {		
		final Browser browserFirefox = Browser.FIREFOX;
		final String browserString1 = browserFirefox.toString();
		
		BrowserType browserType1 = BrowserType.getBrowserType(browserString1);
		
		// Assert the browser type is set
		assertEquals(browserFirefox.toString(), browserType1.toString());
	}
	
	/**
	 * Test that the BrowserType equals returns false for different browsers.
	 */
	public void testBrowserTypeEqualsFalseBrowser() {	
		final Browser browser1 = Browser.FIREFOX;
		final Browser browser2 = Browser.IEXPLORE;
		
		BrowserType browserType1 = new BrowserType(browser1);
		BrowserType browserType2 = new BrowserType(browser2);
		
		assertNotEquals(browserType1, browserType2);
	}
	
	/**
	 * Test that the BrowserType equals returns false for different path to browsers.
	 */
	public void testBrowserTypeEqualsFalsePathToBrowser() {
		final String pathToBrowser1 = "/path/to/browser/1";
		final String pathToBrowser2 = "/path/to/browser/2";
		
		final Browser browser = Browser.FIREFOX;
		
		BrowserType browserType1 = new BrowserType(browser, pathToBrowser1);
		BrowserType browserType2 = new BrowserType(browser, pathToBrowser2);
		
		assertNotEquals(browserType1, browserType2);
	}
	
	/**
	 * Test that the BrowserType equals for two different equal instances.
	 */
	public void testBrowserTypeEquals() {
		final String pathToBrowser1 = "/path/to/browser/1";
		final String pathToBrowser2 = "/path/to/browser/1";
		
		final Browser browser1 = Browser.FIREFOX;
		final Browser browser2 = Browser.FIREFOX;
		
		BrowserType browserType1 = new BrowserType(browser1, pathToBrowser1);
		BrowserType browserType2 = new BrowserType(browser2, pathToBrowser2);
		
		assertEquals(browserType1, browserType2);
	}
	
	/**
	 * Test that the BrowserType does not equal for two completely different objects.
	 */
	public void testBrowserTypeEqualsDifferentObjects() {
		final Browser browser = Browser.FIREFOX;
		BrowserType browserType = new BrowserType(browser);
		
		assertNotEquals(browserType, "");
	}
}
