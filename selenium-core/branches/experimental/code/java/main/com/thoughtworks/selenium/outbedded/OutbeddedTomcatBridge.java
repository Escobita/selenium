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

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.launchers.SystemDefaultBrowserLauncher;

import java.io.File;

/**
 * @author Nelson Sproul
 */
public class OutbeddedTomcatBridge {

    Selenium selenium;
    ServletContainer container;

    protected String launchBridge() throws Exception {

        String tomcatHome = checkForTomcatInstall();

        container = new OutbeddedTomcat(tomcatHome);
        Deployer deployer = new DeployerImpl();

        container.deployAppAndSelenium(deployer);

        String commandServerURL = container.buildDriverURL();
        
        System.out.println("started command bridge at " + commandServerURL);
        
        return commandServerURL;
    }

    private String checkForTomcatInstall() {
        String tomcatHome = getTomcatHome();
        File f = new File(tomcatHome);
        if (!f.exists()) throw new AssertionError("Tomcat not found at " + tomcatHome);
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
    
    /** Stops Tomcat */
    public void stop() {
        container.stop();
    }

    /** Starts Tomcat */
    public void start() {
        // allocates a CommandProcessor, but we don't use it.  We'll be communicating across process 
        // boundary, so it wouldn't be useful since there's no way to hand it to the client.
        container.start();
    }
    
    public static void main(String[] args) throws Exception {
        String serverOrClient = args[0];
        String expectedCommandBridgeURL = "http://localhost:8080/selenium-driver/driver";
        if (serverOrClient.equals("server")) {
            OutbeddedTomcatBridge bridge = new OutbeddedTomcatBridge();
            String commandBridgeURL = bridge.launchBridge();
            if (!commandBridgeURL.equals(expectedCommandBridgeURL)) {
                throw new RuntimeException("oops -- expected " + expectedCommandBridgeURL + " but saw " + commandBridgeURL);
            }
            bridge.start();
            int t = 4;
            System.out.println("Bridge server started at " + commandBridgeURL + " for " + t + " secs...");
            Thread.sleep(t * 1000);
            bridge.stop();
            System.out.println("Bridge server exiting");            
        }
        else if (serverOrClient.equals("client")) {
            System.out.println("Client to test " + expectedCommandBridgeURL + " starting...");
            
            Selenium selenium = new DefaultSelenium(
                    new CommandBridgeClient(expectedCommandBridgeURL),
                    new SystemDefaultBrowserLauncher()
            );
            selenium.start();
            selenium.setContext("A real test, using the real Selenium on the browser side served by Tomcat using Cargo, driven from Java");
            selenium.open("/test_click_page1.html");
            selenium.verifyText("link", "Click here for next page");
            selenium.clickAndWait("link");
            selenium.verifyLocation("/test_click_page2.html");
            selenium.clickAndWait("previousPage");
            selenium.testComplete();
            
            System.out.println("Done");
        }
        else {
            throw new RuntimeException("unknown arg: " + serverOrClient);
        }
    }
}
