/*
Copyright 2012 Selenium Committers

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

package org.openqa.selenium.javascript;

import static com.google.common.base.Preconditions.checkNotNull;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.environment.GlobalTestEnvironment;
import org.openqa.selenium.environment.InProcessTestEnvironment;
import org.openqa.selenium.environment.TestEnvironment;
import org.openqa.selenium.environment.webserver.AppServer;
import org.openqa.selenium.testing.InProject;
import org.openqa.selenium.testing.drivers.WebDriverBuilder;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * JUnit4 test runner for Closure-based JavaScript tests.
 */
public class JavaScriptTestSuite extends ParentRunner<Runner> {

  private final ImmutableList<Runner> children;

  private WebDriver webDriver = null;

  public JavaScriptTestSuite(Class<?> testClass) throws InitializationError {
    super(testClass);

    long timeout = Math.max(0, Long.getLong("js.test.timeout", 0));

    Supplier<WebDriver> driverSupplier = new Supplier<WebDriver>() {
      @Override
      public WebDriver get() {
        checkNotNull(webDriver, "WebDriver has not been started yet!");
        return webDriver;
      }
    };

    children = createChildren(driverSupplier, timeout);
  }

  private static ImmutableList<Runner> createChildren(
      final Supplier<WebDriver> driverSupplier, final long timeout) {
    final File baseDir = InProject.locate("Rakefile").getParentFile();
    final Function<String, URL> pathToUrlFn = new Function<String, URL>() {
      @Override
      public URL apply(String s) {
        AppServer appServer = GlobalTestEnvironment.get().getAppServer();
        try {
          return new URL(appServer.whereIs("/" + s));
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      }
    };


    List<File> tests = TestFileLocator.findTestFiles();
    Iterable<Runner> runners = Iterables.transform(tests, new Function<File, Runner>() {
      @Override
      public Runner apply(File file) {
        final String path = TestFileLocator.getTestFilePath(baseDir, file);
        Description description = Description.createSuiteDescription(
            path.replaceAll(".html$", ""));

        Statement testStatement = new ClosureTestStatement(
            driverSupplier, path, pathToUrlFn, timeout);
        return new StatementRunner(testStatement, description);
      }
    });
    return ImmutableList.copyOf(runners);
  }

  @Override
  protected List<Runner> getChildren() {
    return children;
  }

  @Override
  protected Description describeChild(Runner child) {
    return child.getDescription();
  }

  @Override
  protected void runChild(Runner child, RunNotifier notifier) {
    child.run(notifier);
  }

  @Override
  protected Statement classBlock(RunNotifier notifier) {
    final Statement suite = super.classBlock(notifier);
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        TestEnvironment testEnvironment = null;
        try {
          testEnvironment = GlobalTestEnvironment.get(InProcessTestEnvironment.class);
          webDriver = new WebDriverBuilder().get();
          suite.evaluate();
        } finally {
          if (testEnvironment != null) {
            testEnvironment.stop();
          }
          if (webDriver != null) {
            webDriver.quit();
          }
        }
      }
    };
  }

  private static class StatementRunner extends Runner {

    private final Description description;
    private final Statement testStatement;

    private StatementRunner(Statement testStatement, Description description) {
      this.testStatement = testStatement;
      this.description = description;
    }

    @Override
    public Description getDescription() {
      return description;
    }

    @Override
    public void run(RunNotifier notifier) {
      notifier.fireTestStarted(description);
      try {
        testStatement.evaluate();
      } catch (Throwable throwable) {
        Failure failure = new Failure(description, throwable);
        notifier.fireTestFailure(failure);
      } finally {
        notifier.fireTestFinished(description);
      }
    }
  }
}
