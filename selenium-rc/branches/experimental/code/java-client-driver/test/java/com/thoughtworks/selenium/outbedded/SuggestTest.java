package com.thoughtworks.selenium.outbedded;

public class SuggestTest extends OutbeddedTomcatAJAX {

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testAJAX() throws Throwable {
        selenium.open("/inputSuggestAjax.jsf");
        selenium.verifyTextPresent("suggest");
        selenium.type("_id0:_id3", "foo");
        // DGF On Mozilla a keyPress is needed, and types a letter.
        // On IE6, a keyDown is needed, and no letter is typed. :-p
        // NS On firefox, keyPress needed, no letter typed.
        
        boolean isIE = selenium.getEvalBool("isIE");
        boolean isFirefox = selenium.getEvalBool("isFirefox");
        boolean isNetscape = selenium.getEvalBool("isNetscape");
        String verificationText = null;
        if (isIE) {
            selenium.keyDown("_id0:_id3", 120);
        } else {
            selenium.keyPress("_id0:_id3", 120);
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
