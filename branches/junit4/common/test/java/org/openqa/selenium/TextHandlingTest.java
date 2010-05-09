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

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.openqa.selenium.environment.GlobalTestEnvironment;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.openqa.selenium.Ignore.Driver.CHROME;
import static org.openqa.selenium.Ignore.Driver.FIREFOX;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;

public class TextHandlingTest extends AbstractDriverTestCase {

  private String newLine;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    newLine = "\n";
  }

  @Test public void shouldReturnTheTextContentOfASingleElementWithNoChildren() {
    driver.get(pages.simpleTestPage);
    String selectText = driver.findElement(By.id("oneline")).getText();
    assertThat(selectText, equalTo("A single line of text"));

    String getText = driver.findElement(By.id("oneline")).getText();
    assertThat(getText, equalTo("A single line of text"));
  }

  @Test public void shouldReturnTheEntireTextContentOfChildElements() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("multiline")).getText();

    assertThat(text.contains("A div containing"), is(true));
    assertThat(text.contains("More than one line of text"), is(true));
    assertThat(text.contains("and block level elements"), is(true));
  }

  @Ignore(SELENESE)
  @Test public void shouldIgnoreScriptElements() {
    driver.get(pages.javascriptEnhancedForm);
    WebElement labelForUsername = driver.findElement(By.id("labelforusername"));
    String text = labelForUsername.getText();

    assertThat(labelForUsername.findElements(By.tagName("script")).size(), is(1));
    assertThat(text, not(containsString("document.getElementById")));
    assertThat(text, is("Username:"));
  }

  @Test public void shouldRepresentABlockLevelElementAsANewline() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("multiline")).getText();

    assertThat(text, startsWith("A div containing" + newLine));
    assertThat(text, containsString("More than one line of text" + newLine));
    assertThat(text, endsWith("and block level elements"));
  }

  @Test public void shouldCollapseMultipleWhitespaceCharactersIntoASingleSpace() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("lotsofspaces")).getText();

    assertThat(text, equalTo("This line has lots of spaces."));
  }

  @Test public void shouldTrimText() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("multiline")).getText();

    assertThat(text, startsWith("A div containing"));
    assertThat(text, endsWith("block level elements"));
  }

  @Test public void shouldConvertANonBreakingSpaceIntoANormalSpaceCharacter() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("nbsp")).getText();

    assertThat(text, equalTo("This line has a non-breaking space"));
  }

  @Ignore({IPHONE, SELENESE})
  @Test public void shouldTreatANonBreakingSpaceAsAnyOtherWhitespaceCharacterWhenCollapsingWhitespace() {
    driver.get(pages.simpleTestPage);
    WebElement element = driver.findElement(By.id("nbspandspaces"));
    String text = element.getText();

    assertThat(text, equalTo("This line has a non-breaking space and spaces"));
  }

  @Ignore(IPHONE)
  @Test public void HavingInlineElementsShouldNotAffectHowTextIsReturned() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("inline")).getText();

    assertThat(text,
               equalTo("This line has text within elements that are meant to be displayed inline"));
  }

  @Test public void shouldReturnTheEntireTextOfInlineElements() {
    driver.get(pages.simpleTestPage);
    String text = driver.findElement(By.id("span")).getText();

    assertThat(text, equalTo("An inline element"));
  }

//    @Test public void shouldRetainTheFormatingOfTextWithinAPreElement() {
//        driver.get(simpleTestPage);
//        String text = driver.findElement(By.id("preformatted")).getText();
//
//        assertThat(text, equalTo("This section has a\npreformatted\n   text block\n" +
//                "  within in\n" +
//                "        "));
//    }

  @Ignore(value = {SELENESE, IPHONE}, reason = "iPhone: sendKeys is broken")
  @Test public void shouldBeAbleToSetMoreThanOneLineOfTextInATextArea() {
    driver.get(pages.formPage);
    WebElement textarea = driver.findElement(By.id("withText"));
    textarea.clear();
    String expectedText = "I like cheese" + newLine + newLine + "It's really nice";
    textarea.sendKeys(expectedText);

    String seenText = textarea.getValue();
    assertThat(seenText, equalTo(expectedText));
  }

  @Ignore(SELENESE)
  @Test public void shouldBeAbleToEnterDatesAfterFillingInOtherValuesFirst() {
    driver.get(pages.formPage);
    WebElement input = driver.findElement(By.id("working"));
    String expectedValue = "10/03/2007 to 30/07/1993";
    input.sendKeys(expectedValue);
    String seenValue = input.getValue();

    assertThat(seenValue, equalTo(expectedValue));
  }

  @Test public void shouldReturnEmptyStringWhenTextIsOnlySpaces() {
    driver.get(pages.xhtmlTestPage);

    String text = driver.findElement(By.id("spaces")).getText();
    assertThat(text, equalTo(""));
  }

  @Test public void shouldReturnEmptyStringWhenTextIsEmpty() {
    driver.get(pages.xhtmlTestPage);

    String text = driver.findElement(By.id("empty")).getText();
    assertThat(text, equalTo(""));
  }

  @Test public void shouldReturnEmptyStringWhenTagIsSelfClosing() {
    driver.get(pages.xhtmlTestPage);

    String text = driver.findElement(By.id("self-closed")).getText();
    assertThat(text, equalTo(""));
  }

  @Test public void shouldHandleSiblingBlockLevelElements() {
    driver.get(pages.simpleTestPage);

    String text = driver.findElement(By.id("twoblocks")).getText();

    assertThat(text, is("Some text" + newLine + "Some more text"));
  }

  @Ignore({FIREFOX, HTMLUNIT, IE, CHROME, SELENESE})
  @Test public void shouldHandleNestedBlockLevelElements() {
    driver.get(pages.simpleTestPage);

    String text = driver.findElement(By.id("nestedblocks")).getText();

    assertThat(text, is("Cheese" + newLine + "Some text" + newLine + "Some more text" + newLine
                        + "and also" + newLine + "Brie"));
  }

  @Test public void shouldHandleWhitespaceInInlineElements() {
    driver.get(pages.simpleTestPage);

    String text = driver.findElement(By.id("inlinespan")).getText();

    assertThat(text, is("line has text"));
  }

  @Ignore(value = {SELENESE, IPHONE})
  @Test public void ReadALargeAmountOfData() {
    driver.get(GlobalTestEnvironment.get().getAppServer().whereIs("macbeth.html"));
    String source = driver.getPageSource().trim().toLowerCase();

    assertThat(source.endsWith("</html>"), is(true));
  }

  @Test public void GetTextWithLineBreakForInlineElement() {
    driver.get(pages.simpleTestPage);

    WebElement label = driver.findElement(By.id("label1"));
    String labelText = label.getText();

    assertThat(labelText, matchesPattern("foo[\\n\\r]+bar"));
  }

  private Matcher<String> matchesPattern(String javaRegex) {
    final Pattern pattern = Pattern.compile(javaRegex);

    return new TypeSafeMatcher<String>() {
      @Override
      public boolean matchesSafely(String s) {
        return pattern.matcher(s).matches();
      }

      public void describeTo(Description description) {
        description.appendText("a string matching the pattern " + pattern);
      }
    };
  }

  @JavascriptEnabled
  @Ignore({SELENESE, IPHONE})
  @Test public void shouldOnlyIncludeVisibleText() {
    driver.get(pages.javascriptPage);

    String empty = driver.findElement(By.id("suppressedParagraph")).getText();
    String explicit = driver.findElement(By.id("outer")).getText();

    assertEquals("", empty);
    assertEquals("sub-element that is explicitly visible", explicit);
  }
}
