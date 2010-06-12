package org.openqa.selenium.server;

import org.junit.Test;

public class WindowsHTMLRunnerMultiWindowFunctionalTest extends HTMLRunnerTestBase {
    public WindowsHTMLRunnerMultiWindowFunctionalTest() {
        super.multiWindow = true;
    }
    
    public WindowsHTMLRunnerMultiWindowFunctionalTest(String name) {
        super(name);
        super.multiWindow = true;
    }
    
    @Test public void firefox() throws Exception{
        runHTMLSuite("*firefox", false);
    }
    
    @Test public void iExplore() throws Exception {
        runHTMLSuite("*iexplore", false);
    }
    
    @Test public void chrome() throws Exception {
        runHTMLSuite("*chrome", false);
    }
    
    @Test public void opera() throws Exception {
        runHTMLSuite("*opera", false);
    }
    
    @Test public void hTA() throws Exception {
        try {
            runHTMLSuite("*iehta", false);
            fail("Didn't catch expected exception");
        } catch (UnsupportedOperationException e) {
            System.out.println("caught expected exception");
        }
    }

}
