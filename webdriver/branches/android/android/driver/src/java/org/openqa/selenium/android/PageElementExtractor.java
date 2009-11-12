package org.openqa.selenium.android; 

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.LoggerProvider;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.Source;

import android.util.Log;

import java.io.StringReader;
import java.util.Date;


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

  public Element getElementById(String id) throws Exception {
    String el = (String)mDriver.executeScript("return document.getElementById('" +
        id + "').outerHTML", new Object[0]);

    // To parse the element it has to be a valid HTML document
    setPageSource("<html><body>" + el + "</body></html>");
    
    return source.getElementById(id);
  }

  public Element getElementByName(String name) throws Exception {
    validatePageSource();

    return source.getFirstElement(name);
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
