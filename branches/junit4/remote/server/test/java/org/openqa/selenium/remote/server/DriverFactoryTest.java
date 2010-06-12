/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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

package org.openqa.selenium.remote.server;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DriverFactoryTest {
  private DriverFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new DriverFactory();
  }

  @Test public void shouldBeAbleToRegisterNewDrivers() {
    Capabilities capabilities = DesiredCapabilities.htmlUnit();
    assertFalse(factory.hasMappingFor(capabilities));

    factory.registerDriver(capabilities, DriverOne.class);

    assertTrue(factory.hasMappingFor(capabilities));
  }

  @Test public void shouldReturnMatchIfOneFieldMatchesAndOnlyOneDriverIsRegistered() {
    DesiredCapabilities template = new DesiredCapabilities();
    template.setBrowserName("foo");
    template.setVersion("1.0");
    template.setPlatform(Platform.getCurrent());

    factory.registerDriver(template, DriverOne.class);

    DesiredCapabilities example = new DesiredCapabilities();
    example.setBrowserName(template.getBrowserName());
    Class<? extends WebDriver> result = factory.getBestMatchFor(example);

    assertEquals(DriverOne.class, result);
  }

  @Test public void shouldReturnDriverWhereTheMostCapabilitiesMatch() {
    DesiredCapabilities first = new DesiredCapabilities();
    first.setBrowserName("foo");
    first.setVersion("1.0");

    DesiredCapabilities second = new DesiredCapabilities();
    second.setBrowserName("bar");  // Different name
    second.setVersion("1.0");

    factory.registerDriver(first, DriverOne.class);
    factory.registerDriver(second, DriverTwo.class);

    DesiredCapabilities example = new DesiredCapabilities();
    example.setBrowserName("foo");

    Class<? extends WebDriver> result = factory.getBestMatchFor(example);
    assertEquals(DriverOne.class, result);

    example.setBrowserName("bar");
    result = factory.getBestMatchFor(example);
    assertEquals(DriverTwo.class, result);
  }
  
  @Test public void shouldReturnMostRecentlyAddedDriverWhenAllCapabilitiesAreEqual() {
    Capabilities capabilities = DesiredCapabilities.firefox();

    factory.registerDriver(capabilities, DriverOne.class);
    factory.registerDriver(capabilities, DriverTwo.class);

    Class<? extends WebDriver> result = factory.getBestMatchFor(capabilities);

    assertEquals(DriverTwo.class, result);
  }

  @Test public void shouldConsiderJavascriptCapabilities() {
    DesiredCapabilities nojavascript = new DesiredCapabilities("browser", "v1", Platform.LINUX);
    nojavascript.setJavascriptEnabled(false);
    DesiredCapabilities javascript = new DesiredCapabilities("browser", "v1", Platform.LINUX);
    javascript.setJavascriptEnabled(true);

    factory.registerDriver(nojavascript, DriverOne.class);
    factory.registerDriver(javascript, DriverTwo.class);

    assertEquals(DriverOne.class, factory.getBestMatchFor(nojavascript));
    assertEquals(DriverTwo.class, factory.getBestMatchFor(javascript));
  }
  
  public static abstract class DriverOne implements WebDriver {}
  public static abstract class DriverTwo implements WebDriver {}
}
