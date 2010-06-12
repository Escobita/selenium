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

package org.openqa.selenium.remote;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class BeanToJsonConverterTest {

  @Test public void shouldBeAbleToConvertASimpleString() throws Exception {
    String json = new BeanToJsonConverter().convert("cheese");

    assertThat(json, is("cheese"));
  }

  @Test public void shouldConvertAMapIntoAJsonObject() throws Exception {
    Map<String, String> toConvert = new HashMap<String, String>();
    toConvert.put("cheese", "cheddar");
    toConvert.put("fish", "nice bit of haddock");

    String json = new BeanToJsonConverter().convert(toConvert);

    JSONObject converted = new JSONObject(json);
    assertThat((String) converted.get("cheese"), is("cheddar"));
  }

  @Test public void shouldConvertASimpleJavaBean() throws Exception {
    String json = new BeanToJsonConverter().convert(new SimpleBean());

    JSONObject converted = new JSONObject(json);
    assertThat((String) converted.get("foo"), is("bar"));
  }

  @Test public void shouldConvertArrays() throws Exception {
    String json = new BeanToJsonConverter().convert(new BeanWithArray());

    JSONObject converted = new JSONObject(json);
    JSONArray allNames = (JSONArray) converted.get("names");
    assertThat(allNames.length(), is(3));
  }

  @Test public void shouldConvertCollections() throws Exception {
    String json = new BeanToJsonConverter().convert(new BeanWithCollection());

    JSONObject converted = new JSONObject(json);
    JSONArray allNames = (JSONArray) converted.get("something");
    assertThat(allNames.length(), is(2));
  }

  @Test public void shouldConvertNumbersAsLongs() throws Exception {
    
    String json = new BeanToJsonConverter().convert(new Exception());
    Map map = new JsonToBeanConverter().convert(Map.class, json);

    List stack = (List) map.get("stackTrace");
    Map line = (Map) stack.get(0);

    Object o = line.get("lineNumber");
    assertTrue("line number is of type: " + o.getClass(), o instanceof Long);
  }

  @Test public void shouldNotChokeWhenCollectionIsNull() throws Exception {
    try {
      new BeanToJsonConverter().convert(new BeanWithNullCollection());
    } catch (Exception e) {
      e.printStackTrace();
      fail("That shouldn't have happened");
    }
  }

  @Test public void shouldConvertEnumsToStrings() throws Exception {
    // If this doesn't hang indefinitely, we're all good
    new BeanToJsonConverter().convert(State.INDIFFERENT);
  }

  @Test public void shouldConvertEnumsWithMethods() throws Exception {
    // If this doesn't hang indefinitely, we're all good
    new BeanToJsonConverter().convert(WithMethods.CHEESE);
  }

  @Test public void nullAndAnEmptyStringAreEncodedDifferently() throws Exception {
    BeanToJsonConverter converter = new BeanToJsonConverter();

    String nullValue = converter.convert(null);
    String emptyString = converter.convert("");

    assertFalse(emptyString.equals(nullValue));
  }

  @Test public void shouldBeAbleToConvertAPoint() throws Exception {
    Point point = new Point(65, 75);

    try {
      new BeanToJsonConverter().convert(point);
    } catch (StackOverflowError e) {
      fail("This should never happen");
    }
  }

  @Test public void shouldEncodeClassNameAsClassProperty() throws Exception {
    String json = new BeanToJsonConverter().convert(new SimpleBean());
    JSONObject converted = new JSONObject(json);

    assertEquals(SimpleBean.class.getName(), converted.get("class"));
  }

  @Test public void shouldBeAbleToConvertASessionId() throws JSONException {
    SessionId sessionId = new SessionId("some id");
    String json = new BeanToJsonConverter().convert(sessionId);
    JSONObject converted = new JSONObject(json);

    assertEquals("some id", converted.getString("value"));
  }

  @Test public void shouldBeAbleToConvertAJsonObject() throws JSONException {
    JSONObject obj = new JSONObject();
    obj.put("key", "value");
    String json = new BeanToJsonConverter().convert(obj);
    JSONObject converted = new JSONObject(json);

    assertEquals("value", converted.getString("key"));
  }

  @Test public void shouldBeAbleToConvertACapabilityObject() throws JSONException {
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability("key", "alpha");

    String json = new BeanToJsonConverter().convert(caps);
    JSONObject converted = new JSONObject(json);

    assertEquals("alpha", converted.getString("key"));
  }

  @Test public void shouldConvertAProxyPacProperly() throws JSONException {
    ProxyPac pac = new ProxyPac();
    pac.map("*/selenium/*").toProxy("http://localhost:8080/selenium-server");
    pac.map("/[a-zA-Z]{4}.microsoft.com/").toProxy("http://localhost:1010/selenium-server/");
    pac.map("/flibble*").toNoProxy();
    pac.mapHost("www.google.com").toProxy("http://fishy.com/");
    pac.mapHost("seleniumhq.org").toNoProxy();
    pac.defaults().toNoProxy();

    String json = new BeanToJsonConverter().convert(pac);
    JSONObject converted = new JSONObject(json);

    assertEquals("http://localhost:8080/selenium-server",
        converted.getJSONObject("proxiedUrls").get("*/selenium/*"));
    assertEquals("http://localhost:1010/selenium-server/",
        converted.getJSONObject("proxiedRegexUrls").get("/[a-zA-Z]{4}.microsoft.com/"));
    assertEquals("/flibble*", converted.getJSONArray("directUrls").get(0));
    assertEquals("seleniumhq.org", converted.getJSONArray("directHosts").get(0));
    assertEquals("http://fishy.com/", converted.getJSONObject("proxiedHosts").get("www.google.com"));
    assertEquals("'DIRECT'", converted.get("defaultProxy"));
  }

  private static class SimpleBean {

    public String getFoo() {
      return "bar";
    }
  }

  private static class BeanWithArray {

    public String[] getNames() {
      return new String[]{"peter", "paul", "mary"};
    }
  }

  private static class BeanWithCollection {

    @SuppressWarnings("unchecked")
    public Set getSomething() {
      Set<Integer> integers = new HashSet<Integer>();
      integers.add(1);
      integers.add(43);
      return integers;
    }
  }

  private static class BeanWithNullCollection {

    @SuppressWarnings("unchecked")
    public List getList() {
      return null;
    }
  }

  public static enum State {

    GOOD,
    BAD,
    INDIFFERENT
  }

  public static enum WithMethods {

    CHEESE() {
      public void eat(String foodStuff) {
        // Does nothing
      }
    },
    EGGS() {
      public void eat(String foodStuff) {
        // Does nothing too
      }
    };

    public abstract void eat(String foodStuff);
  }
}
