package org.openqa.selenium.server;

import org.junit.Test;


public class LinuxHTMLRunnerMultiWindowTest extends HTMLRunnerTestBase {
    public LinuxHTMLRunnerMultiWindowTest() {
        super.multiWindow = true;
    }
    
    public LinuxHTMLRunnerMultiWindowTest(String name) {
        super(name);
        super.multiWindow = true;
    }
    
    @Test public void firefox() throws Exception{
        runHTMLSuite("*firefox", false);
    }
    
    @Test public void chrome() throws Exception {
        runHTMLSuite("*chrome", false);
    }
    
}
