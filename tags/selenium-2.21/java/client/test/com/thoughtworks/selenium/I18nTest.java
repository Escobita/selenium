/*
 * Created on Apr 17, 2006
 *
 */
package com.thoughtworks.selenium;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class I18nTest extends InternalSelenseTestBase {

  @Before
  public void navigateToPage() {
    selenium.open("../tests/html/test_locators.html");
  }

  @Test
  public void testRomance() throws UnsupportedEncodingException {
    String expected =
        "\u00FC\u00F6\u00E4\u00DC\u00D6\u00C4 \u00E7\u00E8\u00E9 \u00BF\u00F1 \u00E8\u00E0\u00F9\u00F2";
    String id = "romance";
    verifyText(expected, id);
  }

  @Test
  public void testKorean() throws UnsupportedEncodingException {
    String expected = "\uC5F4\uC5D0";
    String id = "korean";
    verifyText(expected, id);
  }

  @Test
  public void testChinese() throws UnsupportedEncodingException {
    String expected = "\u4E2D\u6587";
    String id = "chinese";
    verifyText(expected, id);
  }

  @Test
  public void testJapanese() throws UnsupportedEncodingException {
    String expected = "\u307E\u3077";
    String id = "japanese";
    verifyText(expected, id);
  }

  @Test
  public void testDangerous() throws UnsupportedEncodingException {
    String expected = "&%?\\+|,%*";
    String id = "dangerous";
    verifyText(expected, id);
  }

  @Test
  public void testDangerousLabels() {
    String[] labels = selenium.getSelectOptions("dangerous-labels");
    Assert.assertEquals("Wrong number of labels", 3, labels.length);
    Assert.assertEquals("mangled label", "veni, vidi, vici", labels[0]);
    Assert.assertEquals("mangled label", "c:\\foo\\bar", labels[1]);
    Assert.assertEquals("mangled label", "c:\\I came, I \\saw\\, I conquered", labels[2]);
  }

  private void verifyText(String expected, String id) throws UnsupportedEncodingException {
    assertTrue(selenium.isTextPresent(expected));
    String actual = selenium.getText(id);
    byte[] result = actual.getBytes("UTF-8");
    for (int i = 0; i < result.length; i++) {
      Byte b = new Byte(result[i]);
    }
    Assert.assertEquals(id + " characters didn't match", expected, actual);
  }


}
