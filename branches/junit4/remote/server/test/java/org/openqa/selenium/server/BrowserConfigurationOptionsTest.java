package org.openqa.selenium.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class BrowserConfigurationOptionsTest {

    @Test public void initializationWithNoOptions() {
      new BrowserConfigurationOptions("");
    }
    
    @Test public void initializationWithGoodSingleOption() {
      BrowserConfigurationOptions options = new BrowserConfigurationOptions("profile=foo");
      assertEquals("foo", options.getProfile());
      assertTrue(options.hasOptions());
    }
    
    @Test public void initializationWithGoodSingleOptionAndWhitespace() {
      BrowserConfigurationOptions options = new BrowserConfigurationOptions("profile= foo bar");
      assertEquals("foo bar", options.getProfile());
      assertTrue(options.hasOptions());
    }
    
    @Test public void initializationWithBadSingleOption() {
      BrowserConfigurationOptions options = new BrowserConfigurationOptions("profile_foo");
      assertNull(options.getProfile());
      assertFalse(options.hasOptions());
    }
    
    @Test public void initializationWithGoodOptionsAndWhitespace() {
      BrowserConfigurationOptions options = 
        new BrowserConfigurationOptions("profile=foo ; unknown=bar");
      assertEquals("foo", options.getProfile());
      assertTrue(options.hasOptions());
    }
    
    @Test public void toStringEquivalentToSerialize() {
        String[] tests = { "", "foo", "foo bar", null };
        
        BrowserConfigurationOptions options = new BrowserConfigurationOptions();
        
        for (String test : tests) {
            options.set("profile", test);
            assertEquals(options.serialize(), options.toString());
        }
    }
}