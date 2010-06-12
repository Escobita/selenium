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

import org.junit.Test;
import org.openqa.selenium.remote.server.rest.Handler;
import org.openqa.selenium.remote.server.rest.ResultConfig;
import org.openqa.selenium.remote.server.rest.ResultType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

public class ResultConfigTest {
  private LogTo logger = new NullLogTo();

  @Test public void shouldMatchBasicUrls() throws Exception {
    ResultConfig config = new ResultConfig("/fish", StubHandler.class, null, logger);

    assertThat(config.getHandler("/fish"), is(notNullValue()));
    assertThat(config.getHandler("/cod"), is(nullValue()));
  }

  @Test public void shouldNotAllowNullToBeUsedAsTheUrl() {
    try {
      new ResultConfig(null, StubHandler.class, null, logger);
      fail("Should have failed");
    } catch (IllegalArgumentException e) {
      exceptionWasExpected();
    }
  }

  @Test public void shouldNotAllowNullToBeUsedForTheHandler() {
    try {
      new ResultConfig("/cheese", null, null, logger);
      fail("Should have failed");
    } catch (IllegalArgumentException e) {
      exceptionWasExpected();
    }
  }

  @Test public void shouldMatchNamedParameters() throws Exception {
    ResultConfig config = new ResultConfig("/foo/:bar", NamedParameterHandler.class, null, logger);
    Handler handler = config.getHandler("/foo/fishy");

    assertThat(handler, is(notNullValue()));
  }

  @Test public void shouldSetNamedParametersOnHandler() throws Exception {
    ResultConfig config = new ResultConfig("/foo/:bar", NamedParameterHandler.class, null, logger);
    NamedParameterHandler handler = (NamedParameterHandler) config.getHandler("/foo/fishy");

    assertThat(handler.getBar(), is("fishy"));
  }

  private void exceptionWasExpected() {
  }

  public static class NamedParameterHandler implements Handler {

    private String bar;

    public String getBar() {
      return bar;
    }

    public void setBar(String bar) {
      this.bar = bar;
    }

    public ResultType handle() {
      return ResultType.SUCCESS;
    }
  }

}
