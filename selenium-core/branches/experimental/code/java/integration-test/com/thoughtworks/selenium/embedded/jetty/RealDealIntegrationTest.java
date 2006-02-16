/*
 * Copyright 2004 ThoughtWorks, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.thoughtworks.selenium.embedded.jetty;

import com.thoughtworks.selenium.*;
import com.thoughtworks.selenium.launchers.DestroyableRuntimeExecutingBrowserLauncher;
import com.thoughtworks.selenium.launchers.ManualPromptUserLauncher;
import com.thoughtworks.selenium.launchers.SystemDefaultBrowserLauncher;
import com.thoughtworks.selenium.launchers.WindowsIEBrowserLauncher;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author Paul Hammant
 * @version $Revision: 131 $
 */
public class RealDealIntegrationTest extends TestCase {

    Selenium selenium;

    protected void setUp() throws Exception {
        super.setUp();
        File codeRoot = getCodeRoot();
        selenium = new DefaultSelenium(
                new JettyCommandProcessor(new File(codeRoot, "javascript/tests/html"), DefaultSelenium.DEFAULT_SELENIUM_CONTEXT,
                        new DirectoryStaticContentHandler(new File(codeRoot, "javascript"))),
                getBrowserLauncher()
        );
        selenium.start();
    }

    protected BrowserLauncher getBrowserLauncher() {
//        There are two significant reasons to avoid using the system preference to determine which browser executes:
//
//            1.)  Currently there is a bug which prevents the browser from exiting after a single test case runs.  
//            This means that either new copies of the browser or in new tabs are opened for each test case that runs.
//
//            2.)  Explicitly stating which browser is to be run means that if we are given a stack trace, we know exactly 
//            which browser was being driven.
//
//            I think that all significant browser flavors should be executed using specific launchers so that we are assured 
//            that no matter who runs the test, the same body of test cases will be executed.
        
//        To facilitate executing the same set of tests against multiple browsers, the code checks for the system property 
//        "browserToTest"; if this property is set to the name of a browser executable, then this code will attempt to 
//        execute that browser.  If the system property is not set, then IE will be executed:

        String browserToTest = System.getProperty("browserToTest");
        if (browserToTest!=null) {
            if (browserToTest.equals("ie")) {
                return new WindowsIEBrowserLauncher();
            }
            return new DestroyableRuntimeExecutingBrowserLauncher(browserToTest);
        }
        return new ManualPromptUserLauncher();
    }

    private File getCodeRoot() throws Exception {
        File codeRoot;
        String codeRootProperty = System.getProperty("code_root");
        if (codeRootProperty == null) {
            throw new Exception("'code_root' not specified");
        } else {
            codeRoot = new File(codeRootProperty);
            if (!codeRoot.exists()) {
                throw new Exception("'code_root' not a dir");
            }
        }
        return codeRoot;
    }

    protected void tearDown() throws Exception {
        Thread.sleep(2 * 1000);
        selenium.stop();
        
        // the following sleep prevents the "Can't QI object for IDispatch" jacob
        // error as it tries to start a new copy of IE when the test cases are brief
        // (e.g., a single "testComplete" op); also prevents a hang when 3 or more
        // tests are run from this module.
        //
        // I have a better solution in mind, but for now I just want to document
        // that this works (at least on my box today, anyway...)
        Thread.sleep(2 * 1000);
    }

    public void testWithJavaScript() {
        selenium.setContext("A real test, using the real Selenium on the browser side served by Jetty, driven from Java",
                SeleniumLogLevels.DEBUG);
        selenium.open("/test_click_page1.html");
        selenium.verifyText("link", "Click here for next page");
        String[] links = selenium.getAllLinks();
        assertTrue(links.length > 3);
        assertEquals("linkToAnchorOnThisPage", links[3]);
        selenium.clickAndWait("link");
        selenium.verifyLocation("/test_click_page2.html");
        selenium.clickAndWait("previousPage");
        selenium.verifyLocation("/test_click_page1.html");
        selenium.testComplete();
    }
    
   public void testAgain() {
        testWithJavaScript();
    }
    
    
    public void testFailure() {
        selenium.setContext("A real negative test, using the real Selenium on the browser side served by Jetty, driven from Java",
                SeleniumLogLevels.DEBUG);
        selenium.open("/test_click_page1.html");
        try {
            selenium.verifyText("XXX", "This text doesn't even appear on the page!");
            fail("No exception was thrown!");
        } catch (SeleniumException se) {
           assertTrue("Exception message isn't as expected: " + se.getMessage(), se.getMessage().indexOf("XXX not found") != -1);
       }
       selenium.testComplete();
   }

    public void testMinimal() {
         selenium.setContext("minimal 'test' -- to see how little I need to do to repro firefox hang",
                SeleniumLogLevels.DEBUG);
        selenium.testComplete();
    }
}

