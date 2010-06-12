package org.openqa.selenium.server;

import org.junit.Test;
import org.openqa.selenium.Ignore;


@Ignore
public class FirefoxMiniHTMLRunnerTest extends HTMLRunnerTestBase {
    
    public FirefoxMiniHTMLRunnerTest() { 
        super();
        super.suiteName="TestSuite.html";
    }
    public FirefoxMiniHTMLRunnerTest(String name) {
        super(name);
        super.suiteName="TestSuite.html";
    }
    
    @Test public void firefoxMini() throws Exception{
        runHTMLSuite("*firefox", false);
    }
    
    

}
