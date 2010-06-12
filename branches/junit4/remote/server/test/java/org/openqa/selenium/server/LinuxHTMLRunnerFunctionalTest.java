package org.openqa.selenium.server;

import org.junit.Test;


public class LinuxHTMLRunnerFunctionalTest extends HTMLRunnerTestBase {
    @Test public void firefox() throws Exception{
        runHTMLSuite("*firefox", false);
    }
    
    @Test public void chrome() throws Exception {
        runHTMLSuite("*chrome", false);
    }
    
}
