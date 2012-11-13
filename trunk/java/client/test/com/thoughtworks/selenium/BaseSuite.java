/*
Copyright 2011 Selenium committers

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

package com.thoughtworks.selenium;

import java.util.logging.Logger;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.openqa.selenium.environment.GlobalTestEnvironment;
import org.openqa.selenium.environment.TestEnvironment;
import org.openqa.selenium.v1.SeleniumTestEnvironment;

public class BaseSuite {
  
  private static final Logger log = Logger.getLogger(BaseSuite.class.getName());

  public static ExternalResource testEnvironment = new ExternalResource() {
    @Override
    protected void before() throws Throwable {
      log.info("Preparing test environment");
      GlobalTestEnvironment.get(SeleniumTestEnvironment.class);
    }
    @Override
    protected void after() {
      log.info("Cleaning test environment");
      TestEnvironment environment = GlobalTestEnvironment.get();
      if (environment != null) {
        environment.stop();
        GlobalTestEnvironment.set(null);
      }
    }
  };
  
  public static ExternalResource browser = new ExternalResource() {
    @Override
    protected void after() {
      log.info("Stopping browser");
      try {
        InternalSelenseTestBase.destroyDriver();
      } catch (SeleniumException ignored) {
        // Nothing sane to do
      }
    }
  };
  
  @ClassRule
  public static TestRule chain =
      RuleChain.outerRule(testEnvironment).around(browser);

}
