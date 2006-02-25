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

package com.thoughtworks.selenium;

import junit.framework.*;

/**
 * @author Paul Hammant
 * @version $Revision: 131 $
 */
public class RealDealIntegrationTest extends TestCase {

    Selenium selenium;

    protected void setUp() throws Exception {
        super.setUp();
        selenium = new DefaultSelenium("localhost", 8080, "c:\\Program Files\\Internet Explorer\\iexplore.exe", "http://localhost:8080");
        selenium.start();
    }

    protected void tearDown() throws Exception {
        selenium.stop();
    }

    public void testWithJavaScript() {
        selenium.setContext("A real test, using the real Selenium on the browser side served by Jetty, driven from Java",
                SeleniumLogLevels.DEBUG);
        selenium.open("/selenium-server/tests/html/test_click_page1.html");
        selenium.verifyText("link", "Click here for next page");
        String[] links = selenium.getAllLinks();
        assertTrue(links.length > 3);
        assertEquals("linkToAnchorOnThisPage", links[3]);
        selenium.clickAndWait("link");
        selenium.verifyLocation("/selenium-server/tests/html/test_click_page2.html");
        selenium.clickAndWait("previousPage");
        selenium.verifyLocation("/selenium-server/tests/html/test_click_page1.html");
        selenium.testComplete();
    }
    
   public void testAgain() {
        testWithJavaScript();
    }
    
    
    public void testFailure() {
        selenium.setContext("A real negative test, using the real Selenium on the browser side served by Jetty, driven from Java",
                SeleniumLogLevels.DEBUG);
        selenium.open("/selenium-server/tests/html/test_click_page1.html");
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

