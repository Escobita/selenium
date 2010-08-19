package org.openqa.selenium.safari;

import org.openqa.selenium.AbstractDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SafariDriverTest extends AbstractDriverTestCase {

  public void testSimpleOperations() throws Exception {
    driver.get("http://www.google.com/");
    
    assertTrue(driver.getCurrentUrl().contains("http://www.google"));
    
    System.out.println("Page source length : " + driver.getPageSource().length());
    
    driver.get("http://code.google.com/p/selenium/");
    
    assertEquals("http://code.google.com/p/selenium/", driver.getCurrentUrl());
    
    driver.navigate().back();
    
    assertEquals("Google", driver.getTitle());
    
    driver.navigate().forward();
    
    assertEquals("http://code.google.com/p/selenium/", driver.getCurrentUrl());
    
    //Thread.sleep(10000);
  }  
}
