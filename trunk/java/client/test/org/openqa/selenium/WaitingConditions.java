/*
Copyright 2010 WebDriver committers
Copyright 2010 Google Inc.

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


package org.openqa.selenium;

import java.util.Set;
import java.util.concurrent.Callable;

public class WaitingConditions {

  private WaitingConditions() {
    // utility class
  }

  public static Callable<WebElement> elementToExist(
      final WebDriver driver, final String elementId) {
    return new Callable<WebElement>() {

      public WebElement call() throws Exception {
        return driver.findElement(By.id(elementId));
      }

      @Override
      public String toString() {
        return String.format("element with ID %s to exist", elementId);
      }
    };
  }

  public static Callable<String> elementTextToEqual(
      final WebElement element, final String value) {
    return new Callable<String>() {

      public String call() throws Exception {
        String text = element.getText();
        if (value.equals(text)) {
          return text;
        }

        return null;
      }

      @Override
      public String toString() {
        return "element text did not equal: " + value;
      }
    };
  }

  public static Callable<String> elementTextToEqual(
      final WebDriver driver, final By locator, final String value) {
    return new Callable<String>() {

      public String call() throws Exception {
        String text = driver.findElement(locator).getText();
        if (value.equals(text)) {
          return text;
        }

        return null;
      }

      @Override
      public String toString() {
        return "element text did not equal: " + value;
      }
    };
  }



  public static Callable<String> elementTextToContain(
      final WebElement element, final String value) {
    return new ElementTextContainsCallable(value, element);
  }

  private static final class ElementTextContainsCallable implements Callable<String> {
    private final String value;
    private final WebElement element;
    private String actualText;

    private ElementTextContainsCallable(String value, WebElement element) {
      this.value = value;
      this.element = element;
    }

    public String call() throws Exception {
      actualText = element.getText();
      if (actualText.contains(value)) {
        return actualText;
      }

      return null;
    }

    @Override
    public String toString() {
      return String.format("element text (%s) to contain: %s", actualText, value);
    }
  }


  public static Callable<String> elementValueToEqual(
      final WebElement element, final String expectedValue) {
    return new Callable<String>() {

      public String lastValue = "";

      public String call() throws Exception {
        lastValue = element.getAttribute("value");
        if (expectedValue.equals(lastValue)) {
          return lastValue;
        }

        return null;
      }

      @Override
      public String toString() {
        return "element value to equal: " + expectedValue + " was: " + lastValue;
      }
    };
  }

  public static Callable<String> pageTitleToBe(
      final WebDriver driver, final String expectedTitle) {
    return new Callable<String>() {

      public String call() throws Exception {
        String title = driver.getTitle();

        if (expectedTitle.equals(title)) {
          return title;
        }

        return null;
      }

      @Override
      public String toString() {
        return "title to be: " + expectedTitle;
      }
    };
  }

  public static Callable<Point> elementLocationToBe(
      final WebElement element, final Point expectedLocation) {
    return new Callable<Point>() {
      private Point currentLocation = new Point(0, 0);
      public Point call() throws Exception {
        currentLocation = element.getLocation();
        if (currentLocation.equals(expectedLocation)) {
          return expectedLocation;
        }

        return null;
      }

      @Override
      public String toString() {
        return "location to be: " + expectedLocation + " is: " + currentLocation;
      }
    };
  }

  public static Callable<WebElement> elementSelectionToBe(
      final WebElement element, final boolean selected) {
    return new Callable<WebElement>() {
      public WebElement call() throws Exception {
        if (element.isSelected() == selected) {
          return element;
        }

        return null;
      }
    };
  }

  public static Callable<Set<String>> windowHandleCountToBe(final WebDriver driver,
                                                            final int count) {
    return new Callable<Set<String>>() {
      public Set<String> call() throws Exception {
        Set<String> handles = driver.getWindowHandles();

        if (handles.size() == count) {
          return handles;
        }
        return null;
      }
    };
  }

}
