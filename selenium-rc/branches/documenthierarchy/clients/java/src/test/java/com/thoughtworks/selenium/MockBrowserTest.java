package com.thoughtworks.selenium;

import org.openqa.selenium.server.browser.BrowserType;

public class MockBrowserTest extends SeleneseTestCase {

    public void setUp() throws Exception {
        setUp("http://x", BrowserType.Browser.MOCK.toString());
    }

    public void testMock() {
        selenium.open("/");
        selenium.click("foo");
        assertEquals("Incorrect title", "x", selenium.getTitle());
        assertTrue("alert wasn't present", selenium.isAlertPresent());
        assertArrayEquals("getAllButtons should return one empty string", new String[]{""}, selenium.getAllButtons());
        assertArrayEquals("getAllLinks was incorrect", new String[]{"1"}, selenium.getAllLinks());
        assertArrayEquals("getAllFields was incorrect", new String[]{"1", "2", "3"}, selenium.getAllFields());
        
    }
    
    private void assertArrayEquals(String message, String[] expected, String[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(message, arrayToString(expected), arrayToString(actual));
    }
    
    private String arrayToString(String[] array) {
        if (array == null) return "null";
        int lastIndex = array.length - 1;
        StringBuffer sb = new StringBuffer('[');
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != lastIndex) {
                sb.append(';');
            }
        }
        sb.append("] length=");
        sb.append(array.length);
        return sb.toString();
    }
}
