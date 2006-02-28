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

/**
 * Defines an object that runs Selenium commands; <i>end users should primarily interact with this object</i>.
 * 
 * Normally you'll begin by creating a new Selenium object and then running the <code>start</code>
 * methed to prepare a new test session.  When you are finished with the current browser session,
 * call stop() to clean up the session and kill the browser. 
 * 
 * 
 * @see com.thoughtworks.selenium.DefaultSelenium
 * @author Paul Hammant
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public interface Selenium  {

    void answerOnNextPrompt(String value);
    void chooseCancelOnNextConfirmation();
    void check(String field);
    void click(String field);
    void clickAndWait(String field);
    void close();
    void keyPress(String locator, int keycode);
    void keyDown(String locator, int keycode);
    void mouseOver(String locator);
    void mouseDown(String locator);
    void fireEvent(String element, String event);
    void goBack();
    void open(String path);
    void select(String field, String value);
    void selectAndWait(String field, String value);
    void selectWindow(String window);
    void store(String field, String value);
    void storeAttribute(String element, String value);
    void storeText(String element, String value);
    void storeValue(String field, String value);
    void submit(String formLocator);
    void type(String field, String value);
    void typeAndWait(String field, String value);
    void uncheck(String field);
    void verifyAlert(String alert);
    void verifyAttribute(String element, String value);
    void verifyConfirmation(String confirmation);
    void verifyEditable(String field);
    void verifyElementNotPresent(String type);
    void verifyElementPresent(String type);
    void verifyLocation(String location);
    void verifyNotEditable(String field);
    void verifyNotVisible(String element);
    void verifyPrompt(String text);
    void verifySelectOptions(String field, String[] values);
    void verifySelected(String field, String value);
    void verifyTable(String table, String value);
    void verifyText(String type, String text);
    void verifyTextPresent(String text);
    void verifyTextNotPresent(String text);
    void verifyTitle(String title);
    void verifyValue(String field, String value);
    void verifyVisible(String element);
    void waitForValue(String field, String value);
    void waitForCondition(String script, long timeout);
    /** Writes a message to the status bar and adds a note to the 
     * browser-side log. Note that the browser-side logs will <i>not</i>
     * be sent back to the server, and are invisible to the driver.
     * @param context the message to be sent to the browser
     */
    void setContext(String context);
    void setContext(String context, String logLevel);
    String[] getAllButtons();
	String[] getAllLinks();
	String[] getAllFields();
    String getAttribute(String target);
    String getChecked(String locator);
    String getEval(String script);
    String getEffectiveStyle(String element);
    String getEffectiveStyleProperty(String element, String property);
    String getTable(String tableLocator);
    String getText(String type);
    String getValue(String field);
    String getTitle();
    String getAbsoluteLocation();
    String getPrompt();
    String getConfirmation();
    String getAlert();
    /** Launches the browser with a new Selenium session */
    void start();
    /** Ends the test session, killing the browser */
    void stop();
    /** Returns a complete list of Selenium "doX" actions */
    String[] getAllActions();
    /** Returns a complete list of Selenium "getX" actions */
    String[] getAllAccessors();
    /** Returns a complete list of Selenium "assertX" actions */
    String[] getAllAsserts();
}
