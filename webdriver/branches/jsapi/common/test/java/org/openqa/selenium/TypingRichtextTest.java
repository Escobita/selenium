// Copyright 2009 Google Inc.  All Rights Reserved.

package org.openqa.selenium;

import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.REMOTE;


/**
 * Tests for typing on richtext enabled documents. In W3 browsers, rich text is
 * enabled by setting {@code document.designMode = 'on'}. In IE, rich text is
 * enabled with {@code document.body.contentEditable = true}.
 *
 * @author jmleyba@google.com (Jason Leyba)
 */
public class TypingRichtextTest extends AbstractDriverTestCase {
  private void runDesignModeEnabledDocumentTypingTest(
      String expected, CharSequence... keySequence) {
    driver.get(richTextPage);
    driver.switchTo().frame("editFrame");

    WebElement element = driver.findElement(By.xpath("//body"));
    if (keySequence.length > 0) {
      element.sendKeys(keySequence);
    } else {
      element.sendKeys(expected);
    }
    assertEquals(expected, element.getText());
  }


  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingLowercase() {
    runDesignModeEnabledDocumentTypingTest("abcdefghijklmnopqrstuvwxyz");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingUppercaseLetters() {
    runDesignModeEnabledDocumentTypingTest("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingNumberRowKeys() {
    runDesignModeEnabledDocumentTypingTest("`1234567890-=");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingNumberRowKeysShifted() {
    runDesignModeEnabledDocumentTypingTest("~!@#$%^&*()_+");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingNumberPadKeys() {
    runDesignModeEnabledDocumentTypingTest("0123456789",
        Keys.NUMPAD0, Keys.NUMPAD1,
        Keys.NUMPAD2, Keys.NUMPAD3,
        Keys.NUMPAD4, Keys.NUMPAD5,
        Keys.NUMPAD6, Keys.NUMPAD7,
        Keys.NUMPAD8, Keys.NUMPAD9);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingAllPrintableKeysWithoutShift() {
    runDesignModeEnabledDocumentTypingTest(
        "`1234567890-=abcdefghijklmnopqrstuvwxyz[]\\;\',./");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingAllPrintableKeysWithShift() {
    runDesignModeEnabledDocumentTypingTest(
        "~!@#$%^&*()_+ABCDEFGHIJKLMNOPQRSTUVWXYZ{}|:\"<>?");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingAllPrintableKeys() {
    String printableKeys =  // Mmmmm..scrambled
        "`123$%^&*()4567890-=abcdefg{}|:\"<>?hijklmntuvwxQRSTUyz[]\\;\',./" +
        "~!@#_+ABCDEFGHIopqrsJKLMNOPVWXYZ";
    runDesignModeEnabledDocumentTypingTest(printableKeys);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingSpace() {
    runDesignModeEnabledDocumentTypingTest("a b c", "a b", Keys.SPACE, "c");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testCanStartNewlinesByTypingEnter() {
    driver.get(richTextPage);
    driver.switchTo().frame("editFrame");
    WebElement element = driver.findElement(By.xpath("//body"));

    element.sendKeys("line one");
    assertEquals("line one", element.getText());

    element.sendKeys("\nline two");
    assertEquals("line one\nline two", element.getText());

    element.sendKeys(Keys.ENTER, "line three");
    assertEquals("line one\nline two\nline three", element.getText());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testArrowKeysAreNotPrintable() {
    runDesignModeEnabledDocumentTypingTest("",
        Keys.ARROW_LEFT, Keys.ARROW_UP, Keys.ARROW_RIGHT, Keys.ARROW_DOWN);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testNavigatingWithArrowKeys() {
    runDesignModeEnabledDocumentTypingTest("dbac1",
        "a", Keys.LEFT,
        "b", Keys.RIGHT,
        "c", Keys.UP,
        "d", Keys.DOWN,
        "1");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testNavigatingBetweenLinesWithArrowKeys() {
    runDesignModeEnabledDocumentTypingTest(
        "------\nline 1\nline 2\nline 3",
        "line 2\n", Keys.UP,
        "line 1\n", Keys.DOWN,
        "line 3",
        Keys.UP, Keys.UP, Keys.UP,
        "------\n");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testTypingBackspaceAndDelete() {
    runDesignModeEnabledDocumentTypingTest("acdfgi",
        "abcdefghi",
        Keys.LEFT, Keys.LEFT, Keys.DELETE,
        Keys.LEFT, Keys.LEFT, Keys.BACK_SPACE,
        Keys.LEFT, Keys.LEFT, "\b");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testHomeAndEndJumpToEnds() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runDesignModeEnabledDocumentTypingTest(
        "line 1\nbegin middle end\nline 3",
        "line 1\nline 3\n",
        Keys.ARROW_UP,
        "middle\n", Keys.ARROW_LEFT,
        Keys.HOME,
        "begin ",
        Keys.END,
        " end");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testPageUpDownJumpToEnds() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runDesignModeEnabledDocumentTypingTest(
        "line 1\nline 2\nline 3\nline 4",
        "line 2\nline 3\n",
        Keys.PAGE_UP,
        "line 1\n",
        Keys.PAGE_DOWN,
        "line 4");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testForwardSelectionReplacement() {
    runDesignModeEnabledDocumentTypingTest("a\nMIDDLE\nc",
        "a\nb\nc\n",
        Keys.UP, Keys.UP, Keys.SHIFT, Keys.RIGHT,
        "middle");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testSelectionReplacement() {
    driver.get(richTextPage);
    driver.switchTo().frame("editFrame");
    WebElement element = driver.findElement(By.xpath("//body"));

    element.sendKeys("one");
    assertEquals("one", element.getText());

    element.sendKeys(Keys.SHIFT, Keys.UP);
    element.sendKeys("two");
    assertEquals("two", element.getText());

    element.sendKeys(Keys.UP);
    element.sendKeys(Keys.SHIFT, Keys.DOWN);
    element.sendKeys("three");
    assertEquals("three", element.getText());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testReverseSelectionReplacement() {
    runDesignModeEnabledDocumentTypingTest("a\n!@#",
        "a\nb\nc\n",
        Keys.SHIFT, Keys.LEFT, Keys.LEFT, Keys.LEFT, Keys.LEFT,
        "123");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testSelectAllAndType() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runDesignModeEnabledDocumentTypingTest("now is",
        "was", Keys.chord(Keys.CONTROL, "a"), "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testSelectAllAndBackspace() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runDesignModeEnabledDocumentTypingTest(
        "", "was", Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testSelectAllAndDelete() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    runDesignModeEnabledDocumentTypingTest(
        "", "was", Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testCutCopyPaste() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    CharSequence cut = Keys.chord(Keys.CONTROL, "x");
    CharSequence copy = Keys.chord(Keys.CONTROL, "c");
    CharSequence paste = Keys.chord(Keys.CONTROL, "v");

    driver.get(richTextPage);
    driver.switchTo().frame("editFrame");
    WebElement element = driver.findElement(By.xpath("//body"));

    element.sendKeys("abc");
    assertEquals("abc", element.getText());

    element.sendKeys(Keys.LEFT, Keys.LEFT, Keys.SHIFT, Keys.LEFT);
    element.sendKeys(cut);
    assertEquals("bc", element.getText());

    element.sendKeys(Keys.RIGHT, Keys.RIGHT);
    element.sendKeys(paste, paste);
    assertEquals("bcaa", element.getText());

    element.sendKeys(Keys.SHIFT, Keys.LEFT, Keys.LEFT);
    element.sendKeys(copy);
    element.sendKeys(Keys.RIGHT, paste, paste, paste);
    assertEquals("bcaaaaaaaa", element.getText());
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testHomeSelection() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    CharSequence selectToFrontChord = Keys.chord(Keys.SHIFT, Keys.HOME);
    runDesignModeEnabledDocumentTypingTest("now is",
        "was", selectToFrontChord, "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testEndSelection() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    CharSequence selectToEndChord = Keys.chord(Keys.SHIFT, Keys.END);
    runDesignModeEnabledDocumentTypingTest("now is",
        "was", Keys.HOME, selectToEndChord, "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testPageUpSelection() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    CharSequence selectToFrontChord = Keys.chord(Keys.SHIFT, Keys.PAGE_UP);
    runDesignModeEnabledDocumentTypingTest("now is",
        "content\nwas\n", selectToFrontChord, "now is");
  }

  @JavascriptEnabled
  @Ignore(value = {HTMLUNIT, IPHONE, REMOTE},
      reason = "HtmlUnit does not support richtext; "
          + "Others are untested useragents")
  public void testPageDownSelection() {
    if (Platform.getCurrent().equals(Platform.MAC)) {
      return;
    }
    CharSequence selectToEndChord = Keys.chord(Keys.SHIFT, Keys.PAGE_DOWN);
    runDesignModeEnabledDocumentTypingTest("now is",
        "content\nwas\n", Keys.PAGE_UP, selectToEndChord, "now is");
  }
}
