/*
Copyright 2007-2010 WebDriver committers

Copyright 2007-2010 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.openqa.selenium.android; 

import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.LoggerProvider;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.Source;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


public class PageElementExtractor {

  // Validity time (in seconds) of cached page source 
  public static final int VALIDITY_TIME = 30;
  
  public PageElementExtractor(AndroidDriver driver) {
    this(driver, "");
  }
  
  public PageElementExtractor(AndroidDriver driver, String pageSource) {
    mDriver = driver;
    // As we don't have log4j installed the first attempt always fails
    // so we have a workaround here to handle this.
    int attempts = 0;
    while (attempts++ < 4) {
      try {
        Log.d("PageElementExtractor:ctor", "Attempt: " + attempts);
        MicrosoftTagTypes.register();
        MasonTagTypes.register();
        net.htmlparser.jericho.Config.LoggerProvider = LoggerProvider.STDERR;
        setPageSource(pageSource);
        Log.d("PageElementExtractor:ctor", "Attempt: " + attempts + ": Success!");
        break;
      } catch (Throwable e) {
        Log.e("PageElementExtractor:ctor", "Throwable, Attempt: " + attempts);
      }
    }
  }

  public Element getElementById(String using) {
    String result = (String)mDriver.executeScript(
        "return document.getElementById(arguments[0]).outerHTML", using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find element with id: " + using);
    }
    // To parse the element it has to be a valid HTML document
    setPageSource("<html><body>" + result + "</body></html>");
    return source.getElementById(using);
  }

  public Element getElementByName(String using) {
    String result = (String)mDriver.executeScript(
        "return document.getElementsByName(arguments[0])[0].outerHTML", using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find element with name: " + using);
    }
    setPageSource(result);
    return source.getFirstElement();
  }

  public List<Element> getElementsByName(String using) {
    String result = (String)mDriver.executeScript(
        "var tags = document.getElementsByName(arguments[0]); " +
        "var result = '['; " +
        "for (i = 0; i < tags.length; i++){" +
        "var tmp = tags[i].outerHTML;" +
        "tmp = tmp.replace(/\"/g, '\\\\\\\"'); " +
        "result += '\\\"' + tmp + '\\\", ';}" +
        "result = result.substr(0, result.length - 2); " +
        "if (!result == ''){result+= ']'};return result;", using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find elements with name: " + using);
    }
    return jsonToElements(result);
  }
  
  public Element getElementByTagName(String using) {
    String result = (String)mDriver.executeScript(
        "return document.getElementsByTagName(arguments[0])[0].outerHTML", using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find element with tag name: " + using);
    }
    setPageSource(result);
    return source.getFirstElement();
  }
  
  public List<Element> getElementsByTagName(String using) {
    String result = (String)mDriver.executeScript(
        "var tags = document.getElementsByTagName(arguments[0]); " +
        "var result = '['; for (i = 0; i < tags.length; i++){" +
        "var tmp = tags[i].outerHTML; " +
        "tmp = tmp.replace(/\"/g, '\\\\\\\"'); " +
        "result += '\\\"' + tmp + '\\\", ';}" +
        "result = result.substr(0, result.length -2);" +
        "if (!result == ''){result += ']';}" +
        "return result;",
        using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find elements with tag name: " + using);
    }
    return jsonToElements(result);
  }
  
  public Element getElementByXPath(String using) {
    String result = (String)mDriver.executeScript(
        "var head = document.getElementsByTagName('head')[0]; " +
        "var script_tag = document.createElement('script'); " +
        "script_tag.type = 'text/javascript'; " +
        "script_tag.innerHTML= 'window.jsxpath = { exportInstaller : true };'; " +
        "head.appendChild(script_tag);" +
        "var head = document.getElementsByTagName('head')[0]; " +
        "var script_tag = document.createElement('script'); " +
        "script_tag.type = 'text/javascript'; " +
        "script_tag.src = encodeURI('http://code.google.com/p/selenium/source/browse/" +
        "#svn/branches/android/android/driver/res/jsxpath.js'); " +
        "head.appendChild(script_tag); " +
        "if (!window.document.evaluate) {window.install(window);};" +
        "return document.evaluate(arguments[0], document, null, " +
            "XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.outerHTML;",
        using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find element with xpath: " + using);
    }
    setPageSource(result);
    return source.getFirstElement();
  }
  
  public List<Element> getElementsByXpath(String using) {
    String result = (String)mDriver.executeScript(
        "var head = document.getElementsByTagName('head')[0]; " +
        "var script_tag = document.createElement('script'); " +
        "script_tag.type = 'text/javascript'; " +
        "script_tag.innerHTML= 'window.jsxpath = { exportInstaller : true };'; " +
        "head.appendChild(script_tag);" +
        "var head = document.getElementsByTagName('head')[0]; " +
        "var script_tag = document.createElement('script'); " +
        "script_tag.type = 'text/javascript'; " +
        "script_tag.src = encodeURI('http://svn.coderepos.org/share/lang/javascript/javascript-xpath/" +
        "trunk/release/javascript-xpath-latest.js'); " +
        "head.appendChild(script_tag); " +
        "if (!window.document.evaluate) {window.install(window);};" +
        "var it = document.evaluate(arguments[0], document, null, " +
            "XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);" +
        "var found = '[';" +
        "for (i = 0; i < it.snapshotLength; i++ ){" +
        "var tmp = it.snapshotItem(i).outerHTML; " +
        "tmp = tmp.replace(/\"/g, '\\\\\\\"');found += '\\\"'+tmp+'\\\"'; " +
        "found += ((i == it.snapshotLength -1 )?']':', ');}" +
        "return found;",
        using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find elements with xpath: " + using);
    }
    return jsonToElements(result);
  }
  
  public Element getElementByLinkText(String using) {
    String result = (String)mDriver.executeScript(
        "var links = document.links; " +
        "for (i=0; i < links.length; i++){" +
        "if (links[i].innerHTML == arguments[0])" +
        "{return links[i].outerHTML;}}",
        using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find element with link text: " + using);
    }
    setPageSource(result);
    return source.getFirstElement();
  }
  
  public List<Element> getElementsByLinkText(String using) {
    String result = (String)mDriver.executeScript(
        "var links = document.links; " +
        "var result = '['; " +
        "for (i = 0; i < links.length; i++) {" +
        "if (links[i].innerHTML == arguments[0]) {" +
        "var tmp = links[i].outerHTML; tmp = tmp.replace(/\"/g, '\\\\\\\"');" +
        "result += '\\\"' + tmp + '\\\", '; }} " +
        "result = result.substr(0, result.length - 2); " +
        "if (!result == ''){result += ']';}" +
        "return result;",
        using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find elements with link text: " + using);
    }
    return jsonToElements(result);
  }
  
  public Element getElementByPartialLinkText(String using) {
    String result = (String)mDriver.executeScript(
        "var links = document.links; " +
        "for (i=0; i < links.length; i++){" +
        "if (links[i].innerHTML.indexOf(arguments[0]) > -1)" +
        "{return links[i].outerHTML;}}",
        using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find element with partial link text: " + using);
    }
    setPageSource(result);
    return source.getFirstElement();
  }
  
  public List<Element> getElementsByPartialLinkText(String using) {
    String result = (String)mDriver.executeScript(
        "var links = document.links; " +
        "var result = '['; " +
        "for (i = 0; i < links.length; i++){" +
        "if (links[i].innerHTML.indexOf(arguments[0]) > -1){" +
        "var tmp = links[i].outerHTML; " +
        "tmp = tmp.replace(/\"/g, '\\\\\\\"'); " +
        "result += '\\\"' + tmp + '\\\", ';}}" +
        "result = result.substr(0, result.length - 2);" +
        "if (!result == ''){result += ']';}" +
        "return result;", using);
    if ((result.length() == 0) || (result == null)) {
      throw new NoSuchElementException("Cannot find elements with link text: " + using);
    }
    return jsonToElements(result);
  }
  
  public void setPageSource(String pageSource) {
    try {
      // Parse the file
      if (pageSource.length() > 0) {
        source = new Source(new StringReader(pageSource));
        sourceTS = new Date();  // Reset the timestamp
      }
    } catch (Throwable e) {
      Log.e("AndroidHTMLParser", "Exception: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }
  
  private List<Element> jsonToElements(String result) {
    List<Element> elements = new ArrayList<Element>();
    String pageSource = "";
    try {
      JSONArray jsonArray = new JSONArray(result);
      Source s = null;
      for (int i = 0; i < jsonArray.length(); i++) {
        if (!(jsonArray.getString(i) == null)) {
          s = new Source(jsonArray.getString(i));
          elements.add(s.getFirstElement());
          pageSource += s;
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    setPageSource(pageSource);
    return elements;
  }
  
  private void validatePageSource() throws Exception {
    if (source == null || source.length() <= 0) {
        Log.e("AndroidHTMLParser", "HTML document not initialized or empty");
        throw new Exception("HTML document not initialized or empty");
    }
    Log.w("PageElementExtractor:validate", "Time since last reload: " +
        (new Date().getTime() - sourceTS.getTime()));
    if (new Date().getTime() - sourceTS.getTime() > VALIDITY_TIME * 1000) {
      Log.w("PageElementExtractor:validate", "Reloading...");
      setPageSource(mDriver.getPageSource());
    }
  }

  private Source source = null;
  private Date sourceTS;
  private AndroidDriver mDriver;
}
