package com.thoughtworks.selenium.outbedded;

import junit.framework.*;

import com.thoughtworks.selenium.*;
import com.thoughtworks.selenium.launchers.*;

public class ServerSuggestTest extends TestCase {

    DefaultSelenium selenium;
    
    protected void setUp() throws Exception {
        CommandProcessor processor = new CommandBridgeClient("http://localhost:8180/selenium/driver/");
        BrowserLauncher launcher = new ManualPromptUserLauncher();
        selenium = new DefaultSelenium(processor, launcher);
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
}
