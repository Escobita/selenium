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

package com.thoughtworks.selenium.outbedded;

import java.io.File;

import junit.framework.TestCase;

import com.thoughtworks.selenium.BrowserLauncher;
import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.launchers.DestroyableRuntimeExecutingBrowserLauncher;
import com.thoughtworks.selenium.launchers.ManualPromptUserLauncher;
import com.thoughtworks.selenium.launchers.WindowsIEBrowserLauncher;

/**
 * @author Ben Griffiths
 */
public class OutbeddedTomcatAJAXTest extends TestCase {

    DefaultSelenium selenium;
    ServletContainer container;

    protected void setUp() throws Exception {
        super.setUp();

        String tomcatHome = checkForTomcatInstall();

        container = new OutbeddedTomcat(tomcatHome);
        Deployer deployer = new AJAXTestDeployer();

        container.deployAppAndSelenium(deployer);

        CommandProcessor processor = container.start();

        selenium = new DefaultSelenium(processor, getBrowserLauncher());

        selenium.start();

    }

    protected BrowserLauncher getBrowserLauncher() {
//      There are two significant reasons to avoid using the system preference to determine which browser executes:
        
//      1.)  Currently there is a bug which prevents the browser from exiting after a single test case runs.  
//      This means that either new copies of the browser or in new tabs are opened for each test case that runs.
        
//      2.)  Explicitly stating which browser is to be run means that if we are given a stack trace, we know exactly 
//      which browser was being driven.
        
//      I think that all significant browser flavors should be executed using specific launchers so that we are assured 
//      that no matter who runs the test, the same body of test cases will be executed.
        
//      To facilitate executing the same set of tests against multiple browsers, the code checks for the system property 
//      "browserToTest"; if this property is set to the name of a browser executable, then this code will attempt to 
//      execute that browser.  If the system property is not set, then IE will be executed:
        
        String browserToTest = System.getProperty("browserToTest");
        if (browserToTest!=null) {
            if (browserToTest.equals("ie")) {
                return new WindowsIEBrowserLauncher();
            }
            return new DestroyableRuntimeExecutingBrowserLauncher(browserToTest);
        }
        return new ManualPromptUserLauncher();
      }
    private String checkForTomcatInstall() {
        String tomcatHome = getTomcatHome();
        File f = new File(tomcatHome);
        if (!f.exists()) {
            String s = "could not find Tomcat.\n" +
                    "We use the environment variable CATALINA_HOME to find tomcat.\n" +
                    "If you are running in eclipse, there are several debug settings needed for\n" +
                    "this to work; you should\n" +
                    "   -go to Run->Debug\n" +
                    "   -go to the Arguments pane\n" +
                    "   -go to the VM Arguments text box\n" +
                    "   -insert the following:\n" +
                    "\n" +
                    "       -DCATALINA_HOME=\"${env_var:CATALINA_HOME}\"\n" +
                    "       -Dcode_root=${project_loc}\n" +
                    "       -Djava.library.path=${project_loc}\\java\\lib\n" +
                    "       -DbrowserToTest=\"ie\"" +
                    "\n" +
                    "and during your next test run, all of your wildest dreams will come true.\n" +
                    "\n" +
                    "NOTE: if you do not have a 'browserToTest' setting, you will be prompted on\n" +
                    "the console to bring up the browser to test yourself.  The valid settings are\n" +
                    "any browser executable that is on your path or the string 'ie' (which uses\n" +
                    "a special means to find and run Internet Explorer).";
            throw new AssertionError(s);
        }
        return tomcatHome;
    }

    private String getTomcatHome() {
        String defaultTomcat = "C:\\jakarta-tomcat-5.0.29";
        if (System.getProperty("TOMCAT_HOME") != null) {
            return System.getProperty("TOMCAT_HOME");
        }
        if (System.getProperty("CATALINA_HOME") != null) {
            return System.getProperty("CATALINA_HOME");
        }
        return defaultTomcat;
    }


    protected void tearDown() throws Exception {
        Thread.sleep(2 * 1000);
        container.stop();
        selenium.stop();
    }
}
