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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

public class ElementFindingTest extends AbstractDriverTestCase {

  @Test public void shouldReturnTitleOfPageIfSet() {
    driver.get(pages.xhtmlTestPage);
    assertThat(driver.getTitle(), equalTo(("XHTML Test Page")));

    driver.get(pages.simpleTestPage);
    assertThat(driver.getTitle(), equalTo("Hello WebDriver"));
  }

  @Test public void shouldNotBeAbleToLocateASingleElementThatDoesNotExist() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.id("nonExistantButton"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test public void shouldBeAbleToClickOnLinkIdentifiedByText() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.linkText("click me")).click();
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test public void DriverShouldBeAbleToFindElementsAfterLoadingMoreThanOnePageAtATime() {
    driver.get(pages.formPage);
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.linkText("click me")).click();
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test public void shouldBeAbleToClickOnLinkIdentifiedById() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.id("linkId")).click();
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test public void shouldThrowAnExceptionWhenThereIsNoLinkToClickAndItIsFoundWithLinkText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.linkText("Not here either"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test public void shouldfindAnElementBasedOnId() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.id("checky"));

    assertThat(element.isSelected(), is(false));
  }

  @Test public void shouldNotBeAbleTofindElementsBasedOnIdIfTheElementIsNotThere() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.id("notThere"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindChildrenOfANode() {
    driver.get(pages.xhtmlTestPage);
    List<WebElement> elements = driver.findElements(By.xpath("/html/head"));
    WebElement head = elements.get(0);
    List<WebElement> importedScripts = head.findElements(By.tagName("script"));
    assertThat(importedScripts.size(), equalTo(2));
  }

  @Ignore(SELENESE)
  @Test public void ReturnAnEmptyListWhenThereAreNoChildrenOfANode() {
    driver.get(pages.xhtmlTestPage);
    WebElement table = driver.findElement(By.id("table"));
    List<WebElement> rows = table.findElements(By.tagName("tr"));

    assertThat(rows.size(), equalTo(0));
  }

  @Ignore(SELENESE)
  @Test public void shouldFindElementsByName() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.name("checky"));

    assertThat(element.getValue(), is("furrfu"));
  }

  @Test public void shouldFindElementsByClass() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("extraDiv"));
    assertTrue(element.getText().startsWith("Another div starts here."));
  }

  @Test public void shouldFindElementsByClassWhenItIsTheFirstNameAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameA"));
    assertThat(element.getText(), equalTo("An H2 title"));
  }

  @Test public void shouldFindElementsByClassWhenItIsTheLastNameAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameC"));
    assertThat(element.getText(), equalTo("An H2 title"));
  }

  @Test public void shouldFindElementsByClassWhenItIsInTheMiddleAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameBnoise"));
    assertThat(element.getText(), equalTo("An H2 title"));
  }
  
  @Test public void shouldFindElementByClassWhenItsNameIsSurroundedByWhitespace() {
    driver.get(pages.xhtmlTestPage);
    
    WebElement element = driver.findElement(By.className("spaceAround"));
    assertThat(element.getText(), equalTo("Spaced out"));
  }
  
  @Ignore(SELENESE)
  @Test public void shouldFindElementsByClassWhenItsNameIsSurroundedByWhitespace() {
    driver.get(pages.xhtmlTestPage);
    
    List<WebElement> elements = driver.findElements(By.className("spaceAround"));
    assertThat(elements.size(), equalTo(1));
    assertThat(elements.get(0).getText(), equalTo("Spaced out"));
  }

  @Test public void shouldNotFindElementsByClassWhenTheNameQueriedIsShorterThanCandidateName() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.className("nameB"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindMultipleElementsByXPath() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.xpath("//div"));

    assertTrue(elements.size() > 1);
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindMultipleElementsByLinkText() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.linkText("click me"));

    assertTrue("Expected 2 links, got " + elements.size(), elements.size() == 2);
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindMultipleElementsByPartialLinkText() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.partialLinkText("ick me"));

    assertTrue(elements.size() == 2);
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindElementByPartialLinkText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.partialLinkText("anon"));
    } catch (NoSuchElementException e) {
      fail("Expected element to be found");
    }
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindMultipleElementsByName() {
    driver.get(pages.nestedPage);

    List<WebElement> elements = driver.findElements(By.name("checky"));

    assertTrue(elements.size() > 1);
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindMultipleElementsById() {
    driver.get(pages.nestedPage);

    List<WebElement> elements = driver.findElements(By.id("2"));

    assertEquals(8, elements.size());
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToFindMultipleElementsByClassName() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.className("nameC"));

    assertTrue(elements.size() > 1);
  }

  // You don't want to ask why this is here
  @Test public void WhenFindingByNameShouldNotReturnById() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.name("id-name1"));
    assertThat(element.getValue(), is("name"));

    element = driver.findElement(By.id("id-name1"));
    assertThat(element.getValue(), is("id"));

    element = driver.findElement(By.name("id-name2"));
    assertThat(element.getValue(), is("name"));

    element = driver.findElement(By.id("id-name2"));
    assertThat(element.getValue(), is("id"));
  }

  @Ignore(SELENESE)
  @Test public void shouldFindGrandChildren() {
    driver.get(pages.formPage);
    WebElement form = driver.findElement(By.id("nested_form"));
    form.findElement(By.name("x"));
  }

  @Ignore(SELENESE)
  @Test public void shouldNotFindElementOutSideTree() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.name("login"));
    try {
      element.findElement(By.name("x"));
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Ignore(SELENESE)
  @Test public void shouldReturnElementsThatDoNotSupportTheNameProperty() {
    driver.get(pages.nestedPage);

    driver.findElement(By.name("div1"));
    // If this works, we're all good
  }

  @Test public void shouldFindHiddenElementsByName() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.name("hidden"));
    } catch (NoSuchElementException e) {
      fail("Expected to be able to find hidden element");
    }
  }

  @Ignore(SELENESE)
  @Test public void shouldfindAnElementBasedOnTagName() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.tagName("input"));

    assertNotNull(element);
  }

  @Ignore(SELENESE)
  @Test public void shouldfindElementsBasedOnTagName() {
    driver.get(pages.formPage);

    List<WebElement> elements = driver.findElements(By.tagName("input"));

    assertNotNull(elements);
  }

  @Test public void findingByCompoundClassNameIsAnError() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.className("a b"));
      fail("Compound class names aren't allowed");
    } catch (IllegalLocatorException e) {
      // This is expected
    }

    try {
      driver.findElements(By.className("a b"));
      fail("Compound class names aren't allowed");
    } catch (IllegalLocatorException e) {
      // This is expected
    }
  }

  @JavascriptEnabled
  @Test public void shouldBeAbleToClickOnLinksWithNoHrefAttribute() {
    driver.get(pages.javascriptPage);

    WebElement element = driver.findElement(By.linkText("No href"));
    element.click();

    // if any exception is thrown, we won't get this far. Sanity check
    assertEquals("Changed", driver.getTitle());
  }

  @Ignore({SELENESE})
  @Test public void shouldNotBeAbleToFindAnElementOnABlankPage() {
    driver.get("about:blank");

    try {
      // Search for anything. This used to cause an IllegalStateException in IE.
      driver.findElement(By.tagName("a"));
      fail("Should not have been able to find a link");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Ignore({IPHONE})
  @NeedsFreshDriver
  @Test public void shouldNotBeAbleToLocateASingleElementOnABlankPage() {
    // Note we're on the default start page for the browser at this point.

    try {
      driver.findElement(By.id("nonExistantButton"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @JavascriptEnabled
  @Test public void RemovingAnElementDynamicallyFromTheDomShouldCauseAStaleRefException() {
    driver.get(pages.javascriptPage);

    RenderedWebElement toBeDeleted = (RenderedWebElement) driver.findElement(By.id("deleted"));
    assertTrue(toBeDeleted.isDisplayed());

    driver.findElement(By.id("delete")).click();

    try {
      toBeDeleted.isDisplayed();
      fail("Element should be stale at this point");
    } catch (StaleElementReferenceException e) {
      // this is expected
    }
  }

  @Test public void findingALinkByXpathUsingContainsKeywordShouldWork() {
    driver.get(pages.nestedPage);

    try {
      driver.findElement(By.xpath("//a[contains(.,'hello world')]"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Should not have thrown an exception");
    }
  }

  @JavascriptEnabled
  @Test public void shouldBeAbleToFindAnElementByCssSelector() {
    driver.get(pages.xhtmlTestPage);
    if (!supportsSelectorApi()) {
      System.out.println("Skipping test: selector API not supported");
      return;
    }
    driver.findElement(By.cssSelector("div.content"));
  }

  @JavascriptEnabled
  @Test public void shouldBeAbleToFindAnElementsByCssSelector() {
    driver.get(pages.xhtmlTestPage);
    if (!supportsSelectorApi()) {
      System.out.println("Skipping test: selector API not supported");
      return;
    }
    driver.findElements(By.cssSelector("p"));
  }
}
