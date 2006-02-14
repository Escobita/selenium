package com.thoughtworks.selenium.outbedded;

public class SuggestTest extends OutbeddedTomcatAJAXTest {

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
        
        boolean isIE = selenium.getEvalBool("isIE");
        boolean isNetscape = selenium.getEvalBool("isNetscape");
        String verificationText;
        if (isIE) {
            selenium.keyDown("_id0:_id3", 120);
        } else {
            selenium.keyPress("_id0:_id3", 120);
        }
        if (isNetscape) {
            verificationText = "foox1";
        } else {
            verificationText = "foo1";
        }
        Thread.sleep(2000);
        selenium.verifyTextPresent(verificationText);
    }
}
