package com.thoughtworks.selenium;

import junit.framework.*;

public class ApacheMyFacesSuggestTest extends TestCase {

    DefaultSelenium selenium;
    
    protected void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 8080, "c:\\Program Files\\Internet Explorer\\iexplore.exe", "http://www.irian.at");
        selenium.start();
    }
    
    public void testAJAX() throws Throwable {
        selenium.open("http://www.irian.at/myfaces-sandbox/inputSuggestAjax.jsf");
        selenium.verifyTextPresent("suggest");
        String elementID = "_idJsp0:_idJsp3";
        selenium.type(elementID, "foo");
        // DGF On Mozilla a keyPress is needed, and types a letter.
        // On IE6, a keyDown is needed, and no letter is typed. :-p
        // NS On firefox, keyPress needed, no letter typed.
        
        boolean isIE = selenium.getEvalBool("isIE");
        boolean isFirefox = selenium.getEvalBool("isFirefox");
        boolean isNetscape = selenium.getEvalBool("isNetscape");
        String verificationText = null;
        if (isIE) {
            selenium.keyDown(elementID, 120);
        } else {
            selenium.keyPress(elementID, 120);
        }
        if (isNetscape) {
            verificationText = "foox1";
        } else if (isIE || isFirefox) {
            verificationText = "foo1";
        }
        else {
            fail("which browser is this?");
        }
        Thread.sleep(2000);
        selenium.verifyTextPresent(verificationText);
    }
    
    public void tearDown() {
        selenium.testComplete();
    }
}
