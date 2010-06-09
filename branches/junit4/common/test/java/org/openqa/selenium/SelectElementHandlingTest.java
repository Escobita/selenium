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

package org.openqa.selenium;

import java.util.List;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

public class SelectElementHandlingTest extends AbstractDriverTestCase {

  @Ignore({IE, SELENESE, IPHONE})
  @Test public void shouldBePossibleToDeselectASingleOptionFromASelectWhichAllowsMultipleChoices() {
    driver.get(pages.formPage);

    WebElement multiSelect = driver.findElement(By.id("multi"));
    List<WebElement> options = multiSelect.findElements(By.tagName("option"));

    WebElement option = options.get(0);
    assertThat(option.isSelected(), is(true));
    option.toggle();
    assertThat(option.isSelected(), is(false));
    option.toggle();
    assertThat(option.isSelected(), is(true));

    option = options.get(2);
    assertThat(option.isSelected(), is(true));
  }

  @Ignore({IE, SELENESE, IPHONE})
  @Test public void shouldNotBeAbleToDeselectAnOptionFromANormalSelect() {
    driver.get(pages.formPage);

    WebElement select = driver.findElement(By.xpath("//select[@name='selectomatic']"));
    List<WebElement> options = select.findElements(By.tagName("option"));
    WebElement option = options.get(0);

    try {
      option.toggle();
      fail("Should not have succeeded");
    } catch (UnsupportedOperationException e) {
      // This is expected
    }
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToChangeTheSelectedOptionInASelect() {
    driver.get(pages.formPage);
    WebElement selectBox = driver.findElement(By.xpath("//select[@name='selectomatic']"));
    List<WebElement> options = selectBox.findElements(By.tagName("option"));
    WebElement one = options.get(0);
    WebElement two = options.get(1);
    assertThat(one.isSelected(), is(true));
    assertThat(two.isSelected(), is(false));

    two.setSelected();
    assertThat(one.isSelected(), is(false));
    assertThat(two.isSelected(), is(true));
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToSelectMoreThanOneOptionFromASelectWhichAllowsMultipleChoices() {
    driver.get(pages.formPage);

    WebElement multiSelect = driver.findElement(By.id("multi"));
    List<WebElement> options = multiSelect.findElements(By.tagName("option"));
    for (WebElement option : options) {
      option.setSelected();
    }

    for (int i = 0; i < options.size(); i++) {
      WebElement option = options.get(i);
      assertThat("Option at index is not selected but should be: " + i, option.isSelected(),
                 is(true));
    }
  }
}
