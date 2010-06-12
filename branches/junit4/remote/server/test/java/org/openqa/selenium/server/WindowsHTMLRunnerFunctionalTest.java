package org.openqa.selenium.server;

import org.junit.Test;

public class WindowsHTMLRunnerFunctionalTest extends HTMLRunnerTestBase {

    @Test public void firefox() throws Exception {
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
        runHTMLSuite("*iehta", false);
    }

}
