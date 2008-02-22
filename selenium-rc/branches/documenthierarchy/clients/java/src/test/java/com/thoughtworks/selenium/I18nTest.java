/*
 * Created on Apr 17, 2006
 *
 */
package com.thoughtworks.selenium;

import java.io.UnsupportedEncodingException;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.browser.BrowserType;

public class I18nTest extends SeleneseTestCase {
    
    private static String browser = null;

    public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
    public static Test suite() {
        return new I18nTestSetup(new TestSuite(I18nTest.class),
				BrowserType.Browser.MOCK.toString(), true);
    }
    
    public void setUp() throws Exception {
    	setUp(null, getBrowser());
        selenium.open("/selenium-server/tests/html/test_i18n.html");
	}

	protected static class I18nTestSetup extends TestSetup {

        boolean launchServer;
        SeleniumServer server;
        
        public I18nTestSetup(Test test, String browser, boolean launchServer) {
            super(test);
            
            // this is pretty ugly to call back like this, but wanted to get the 
            // browser back to the test so it could leverage the super setUp method
            TestSuite suite = (TestSuite)test;
            I18nTest t = (I18nTest)suite.testAt(0);
            t.setBrowser(browser);
            
            this.launchServer = launchServer;
        }
        
        public void setUp() throws Exception {
            if (launchServer) {
                server = SeleniumServer.getInstance();
                server.start();
            }
        }
        
        public void tearDown() throws Exception {
            if (launchServer) server.stop();
        }
        
    }
    
    
    public void testRomance() throws UnsupportedEncodingException {
        String expected = "\u00FC\u00F6\u00E4\u00DC\u00D6\u00C4 \u00E7\u00E8\u00E9 \u00BF\u00F1 \u00E8\u00E0\u00F9\u00F2";
        String id = "romance";
        verifyText(expected, id);
    }
    
    public void testKorean() throws UnsupportedEncodingException {
        String expected = "\uC5F4\uC5D0";
        String id = "korean";
        verifyText(expected, id);
    }
    
    public void testChinese() throws UnsupportedEncodingException {
        String expected = "\u4E2D\u6587";
        String id = "chinese";
        verifyText(expected, id);
    }
    
    public void testJapanese() throws UnsupportedEncodingException {
        String expected = "\u307E\u3077";
        String id = "japanese";
        verifyText(expected, id);
    }
    
    public void testDangerous() throws UnsupportedEncodingException {
        String expected = "&%?\\+|,%*";
        String id = "dangerous";
        verifyText(expected, id);
    }
    
    public void testDangerousLabels() throws UnsupportedEncodingException {
        String[] labels = selenium.getSelectOptions("dangerous-labels");
        assertEquals("Wrong number of labels", 3, labels.length);
        assertEquals("mangled label", "veni, vidi, vici", labels[0]);
        assertEquals("mangled label", "c:\\foo\\bar", labels[1]);
        assertEquals("mangled label", "c:\\I came, I \\saw\\, I conquered", labels[2]);
    }

    private void verifyText(String expected, String id) throws UnsupportedEncodingException {
        System.out.println(getName());
        System.out.println(expected);
        assertTrue(selenium.isTextPresent(expected));
        String actual = selenium.getText(id);
        byte[] result = actual.getBytes("UTF-8");
        for (int i = 0; i < result.length; i++) {
            Byte b = new Byte(result[i]);
            System.out.println("BYTE " + i + ": " + b.toString());
        }
        assertEquals(id + " characters didn't match", expected, actual);
    }


}
