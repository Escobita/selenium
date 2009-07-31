// Copyright 2009 Google Inc.  All Rights Reserved.

package org.openqa.selenium;

import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.REMOTE;

/**
 * Tests for typing on {@code INPUT} and {@code TEXTAREA} form elements.
 *
 * @author jmleyba@gmail.com (Jason Leyba)
 */
public class TypingFormsTest extends AbstractDriverTestCase {

  private void runInputTypingTest(String expectedValue,
                                  CharSequence... keySequence) {
    driver.get(javascriptPage);
    WebElement element = driver.findElement(By.id("keyReporter"));
    if (keySequence.length > 0) {
      element.sendKeys(keySequence);
    } else {
      element.sendKeys(expectedValue);
    }
    assertEquals(expectedValue, element.getValue());
  }


  private void runTextAreaTypingTest(String expectedValue,
                                     CharSequence... keySequence) {
    driver.get(javascriptPage);
    WebElement element = driver.findElement(By.id("keyUpArea"));
    if (keySequence.length > 0) {
      element.sendKeys(keySequence);
    } else {
      element.sendKeys(expectedValue);
    }
    assertEquals(expectedValue, element.getValue());
  }


// ----------------------------------------------------------------------------
//
//  Tests for typing on an INPUT element.
//
// ----------------------------------------------------------------------------

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingLowercaseOnAnInputElement() {
    runInputTypingTest("abcdefghijklmnopqrstuvwxyz");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingUppercaseLettersOnAnInputElement() {
    runInputTypingTest("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingNumberRowKeysOnAnInputElement() {
    runInputTypingTest("`1234567890-=");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingNumberRowKeysShiftedOnAnInputElement() {
    runInputTypingTest("~!@#$%^&*()_+");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingNumberPadKeysOnAnInputElement() {
    runInputTypingTest("0123456789",
        Keys.NUMPAD0, Keys.NUMPAD1,
        Keys.NUMPAD2, Keys.NUMPAD3,
        Keys.NUMPAD4, Keys.NUMPAD5,
        Keys.NUMPAD6, Keys.NUMPAD7,
        Keys.NUMPAD8, Keys.NUMPAD9);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingAllPrintableKeysWithoutShiftOnAnInputElement() {
    runInputTypingTest("`1234567890-=abcdefghijklmnopqrstuvwxyz[]\\;\',./");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingAllPrintableKeysWithShiftOnAnInputElement() {
    runInputTypingTest("~!@#$%^&*()_+ABCDEFGHIJKLMNOPQRSTUVWXYZ{}|:\"<>?");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingAllPrintableKeysOnAnInputElement() {
    String printableKeys =  // Mmmmm..scrambled
        "`123$%^&*()4567890-=abcdefg{}|:\"<>?hijklmntuvwxQRSTUyz[]\\;\',./" +
        "~!@#_+ABCDEFGHIopqrsJKLMNOPVWXYZ";
    runInputTypingTest( printableKeys);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingSpaceOnAnInputElement() {
    runInputTypingTest("a b c", "a b", Keys.SPACE, "c");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testCanSubmitFormByTypingEnterOnAnInputElement() {
    driver.get(formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys(Keys.ENTER);
    assertEquals("Failed to submit when typing <Keys.ENTER>",
        "We Arrive Here", driver.getTitle());

    driver.get(formPage);
    nestedForm = driver.findElement(By.id("nested_form"));
    input = nestedForm.findElement(By.name("x"));
    input.sendKeys("\n");
    assertEquals("Failed to submit when typing <\\n>",
        "We Arrive Here", driver.getTitle());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testArrowKeysAreNotPrintableInAnInputElement() {
    runInputTypingTest("",
        Keys.ARROW_LEFT, Keys.ARROW_UP, Keys.ARROW_RIGHT, Keys.ARROW_DOWN);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testNavigatingWithArrowKeysInAnInputElement() {
    String expected = Platform.getCurrent().equals(Platform.MAC)
        ? "dbac1" : "bacd1";
    runInputTypingTest(expected,
        "a", Keys.LEFT,
        "b", Keys.RIGHT,
        "c", Keys.UP,
        "d", Keys.DOWN,
        "1");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingBackspaceAndDeleteInAnInputElement() {
    runInputTypingTest("acdfgi",
        "abcdefghi",
        Keys.LEFT, Keys.LEFT, Keys.DELETE,
        Keys.LEFT, Keys.LEFT, Keys.BACK_SPACE,
        Keys.LEFT, Keys.LEFT, "\b");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testHomeAndEndJumpToEndsInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runInputTypingTest("begin middle end",
        "middle", Keys.HOME, "begin ", Keys.END, " end");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testForwardSelectionReplacementInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runInputTypingTest("middle",
        "red", Keys.HOME,
        Keys.chord(Keys.SHIFT, Keys.RIGHT, Keys.RIGHT, Keys.RIGHT),
        "middle");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testReverseSelectionReplacementInAnInputElement() {
    runInputTypingTest("middle",
        "red",
        Keys.chord(Keys.SHIFT, Keys.LEFT, Keys.LEFT, Keys.LEFT),
        "middle");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testSelectAllAndTypeInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runInputTypingTest("now is",
        "was", Keys.chord(Keys.CONTROL, "a"), "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testSelectAllAndBackspaceInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runInputTypingTest("", "was", Keys.chord(Keys.CONTROL, "a"),
        Keys.BACK_SPACE);
    runInputTypingTest("", "was", Keys.chord(Keys.CONTROL, "a"), "\b");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testSelectAllAndDeleteInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runInputTypingTest("", "was", Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testCutCopyPasteInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    CharSequence selectAll = Keys.chord(Keys.CONTROL, "a");
    CharSequence cut = Keys.chord(Keys.CONTROL, "x");
    CharSequence copy = Keys.chord(Keys.CONTROL, "c");
    CharSequence paste = Keys.chord(Keys.CONTROL, "v");

    driver.get(javascriptPage);
    WebElement element = driver.findElement(By.id("keyReporter"));
    element.sendKeys("world");
    assertEquals("world", element.getValue());
    element.sendKeys(selectAll, cut);
    assertEquals("", element.getValue());
    element.sendKeys("hello, ", paste);
    assertEquals("hello, world", element.getValue());
    element.sendKeys(selectAll, copy, paste, Keys.RIGHT);
    element.sendKeys(paste);
    assertEquals("hello, worldhello, world", element.getValue());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testHomeSelectionInAnInputElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
     CharSequence selectToFrontChord = Keys.chord(Keys.SHIFT, Keys.HOME);
    runInputTypingTest("now is",
         "was", selectToFrontChord, "now is");
   }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testEndSelectionInAnInputElement() {
   if (Platform.getCurrent().equals(Platform.MAC)) {
     return;
   }
   CharSequence selectToEndChord = Keys.chord(Keys.SHIFT, Keys.END);
   runInputTypingTest("now is",
       "was", Keys.HOME, selectToEndChord, "now is");
  }


// ----------------------------------------------------------------------------
//
//  Tests for typing on a TEXT AREA element.
//
// ----------------------------------------------------------------------------

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingLowercaseOnATextAreaElement() {
    runTextAreaTypingTest("abcdefghijklmnopqrstuvwxyz");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingUppercaseLettersOnATextAreaElement() {
    runTextAreaTypingTest("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingNumberRowKeysOnATextAreaElement() {
    runTextAreaTypingTest("`1234567890-=");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingNumberRowKeysShiftedOnATextAreaElement() {
    runTextAreaTypingTest("~!@#$%^&*()_+");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingNumberPadKeysOnATextAreaElement() {
    runTextAreaTypingTest("0123456789",
        Keys.NUMPAD0, Keys.NUMPAD1,
        Keys.NUMPAD2, Keys.NUMPAD3,
        Keys.NUMPAD4, Keys.NUMPAD5,
        Keys.NUMPAD6, Keys.NUMPAD7,
        Keys.NUMPAD8, Keys.NUMPAD9);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingAllPrintableKeysWithoutShiftOnATextAreaElement() {
    runTextAreaTypingTest(
        "`1234567890-=abcdefghijklmnopqrstuvwxyz[]\\;\',./");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingAllPrintableKeysWithShiftOnATextAreaElement() {
    runTextAreaTypingTest(
        "~!@#$%^&*()_+ABCDEFGHIJKLMNOPQRSTUVWXYZ{}|:\"<>?");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingAllPrintableKeysOnATextAreaElement() {
    String printableKeys =  // Mmmmm..scrambled
        "`123$%^&*()4567890-=abcdefg{}|:\"<>?hijklmntuvwxQRSTUyz[]\\;\',./" +
        "~!@#_+ABCDEFGHIopqrsJKLMNOPVWXYZ";
    runTextAreaTypingTest(printableKeys);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingSpaceOnATextAreaElement() {
    runTextAreaTypingTest("a b c', 'a b', Keys.SPACE, 'c");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testCanStartNewlinesByTypingEnterInATextArea() {
    driver.get(javascriptPage);
    WebElement textArea = driver.findElement(By.id("keyUpArea"));
    textArea.sendKeys("line one");
    assertEquals("line one", textArea.getValue());
    textArea.sendKeys("\nline two");
    assertEquals("line one\nline two", textArea.getValue());
    textArea.sendKeys(Keys.ENTER, "line three");
    assertEquals("line one\nline two\nline three", textArea.getValue());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testArrowKeysAreNotPrintableInATextAreaElement() {
    runTextAreaTypingTest("",
        Keys.ARROW_LEFT, Keys.ARROW_UP, Keys.ARROW_RIGHT, Keys.ARROW_DOWN);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testNavigatingWithArrowKeysInATextAreaElement() {
    runTextAreaTypingTest("dbac1",
        "a", Keys.LEFT,
        "b", Keys.RIGHT,
        "c", Keys.UP,
        "d", Keys.DOWN,
        "1");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testNavigatingBetweenLinesWithArrowKeysInATextAreaElement() {
    runTextAreaTypingTest("------\nline 1\nline 2\nline 3",
        "line 2\n", Keys.UP,
        "line 1\n", Keys.DOWN,
        "line 3",
        Keys.UP, Keys.UP, Keys.UP,
        "------\n");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testTypingBackspaceAndDeleteInATextAreaElement() {
    runTextAreaTypingTest("acdfgi",
        "abcdefghi",
        Keys.LEFT, Keys.LEFT, Keys.DELETE,
        Keys.LEFT, Keys.LEFT, Keys.BACK_SPACE,
        Keys.LEFT, Keys.LEFT, "\b");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testHomeAndEndJumpToEndsInATextAreaElement() {
    runTextAreaTypingTest("line 1\nbegin middle end\nline 3\n",
        "line 1\nline 3\n",
        Keys.ARROW_UP,
        "middle\n", Keys.ARROW_LEFT,
        Keys.HOME,
        "begin ",
        Keys.END,
        " end");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testPageUpDownJumpToEndsInATextAreaElement() {
    runTextAreaTypingTest("line 1\nline 2\nline 3\nline 4",
        "line 2\nline 3\n",
        Keys.PAGE_UP,
        "line 1\n",
        Keys.PAGE_DOWN,
        "line 4");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testForwardSelectionReplacementInATextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runTextAreaTypingTest("line 1\nmiddle\nline 3\n",
        "line 1\nline 2\nline 3\n",
        Keys.ARROW_UP, Keys.ARROW_UP,
        Keys.SHIFT, Keys.chord(Keys.END),
        "middle");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testReverseSelectionReplacementInATextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runTextAreaTypingTest("line 1\nmiddle\nline 3\n",
        "line 1\nline 2\nline 3\n",
        Keys.ARROW_UP, Keys.ARROW_UP, Keys.chord(Keys.END),
        Keys.SHIFT, Keys.chord(Keys.HOME),
        "middle");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testSelectAllAndTypeInATextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runTextAreaTypingTest("now is",
        "was", Keys.chord(Keys.CONTROL, "a"), "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testSelectAllAndBackspaceInATextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runTextAreaTypingTest("",
        "was", Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testSelectAllAndDeleteInATextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runTextAreaTypingTest("",
        "was", Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testCutCopyPasteInATextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }

    CharSequence selectAll = Keys.chord(Keys.CONTROL, "a");
    CharSequence cut = Keys.chord(Keys.CONTROL, "x");
    CharSequence copy = Keys.chord(Keys.CONTROL, "c");
    CharSequence paste = Keys.chord(Keys.CONTROL, "v");

    driver.get(javascriptPage);
    WebElement element = driver.findElement(By.id("keyUpArea"));
    element.sendKeys("world");
    assertEquals("world", element.getValue());

    element.sendKeys(selectAll, cut);
    assertEquals("", element.getValue());

    element.sendKeys("hello, ", paste, Keys.ENTER);
    assertEquals("hello, world\n", element.getValue());

    element.sendKeys(selectAll, copy, paste, Keys.RIGHT);
    element.sendKeys(paste);
    assertEquals("Double paste failed",
        "hello, world\nhello, world\n", element.getValue());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testMultilineSelectionEditingInTextAreaElement() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }

    CharSequence jumpToStartChord = Keys.chord(Keys.HOME);
    CharSequence jumpToEndChord = Keys.chord(Keys.END);
    CharSequence pageUpChord = Keys.chord(Keys.PAGE_UP);
    CharSequence pageDownChord = Keys.chord(Keys.PAGE_DOWN);

    driver.get(javascriptPage);
    WebElement element = driver.findElement(By.id("keyUpArea"));

    element.sendKeys("line1\nline2\nline3");
    assertEquals("line1\nline2\nline3", element.getValue());
    element.sendKeys(Keys.ARROW_UP, jumpToStartChord,
                     Keys.SHIFT, jumpToEndChord,
                     "middle");
    assertEquals("line1\nmiddle\nline3", element.getValue());

    element.sendKeys(jumpToStartChord, Keys.SHIFT, Keys.UP);
    element.sendKeys("beginning\n");
    assertEquals("beginning\nmiddle\nline3", element.getValue());

    element.sendKeys(jumpToStartChord, Keys.SHIFT, jumpToEndChord);
    element.sendKeys(Keys.SHIFT, pageUpChord);
    element.sendKeys(Keys.SHIFT, pageDownChord);
    element.sendKeys("the end");
    assertEquals("beginning\nthe end", element.getValue());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testHomeSelectionInATextAreaElement() {
    CharSequence selectToFrontChord = Keys.chord(Keys.SHIFT, Keys.HOME);
    runTextAreaTypingTest("now is",
        "was", selectToFrontChord, "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testEndSelectionInATextAreaElement() {

    CharSequence selectToEndChord = Keys.chord(Keys.SHIFT, Keys.END);
    runTextAreaTypingTest("now is",
        "was", Keys.HOME, selectToEndChord, "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testPageUpSelectionInATextAreaElement() {

    CharSequence selectToFrontChord = Keys.chord(Keys.SHIFT, Keys.PAGE_UP);
    runTextAreaTypingTest("now is",
        "content\nwas\n", selectToFrontChord, "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE}, reason = "Untested useragents")
  public void testPageDownSelectionInATextAreaElement() {

    CharSequence selectToEndChord = Keys.chord(Keys.SHIFT, Keys.PAGE_DOWN);
    runTextAreaTypingTest("now is",
        "content\nwas\n", Keys.PAGE_UP, selectToEndChord, "now is");
  }
}
