/*
Copyright 2012 Selenium committers
Copyright 2012 Software Freedom Conservancy

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

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JUnit4TestBase;
import org.openqa.selenium.testing.JavascriptEnabled;

import static org.openqa.selenium.testing.Ignore.Driver.ANDROID;
import static org.openqa.selenium.testing.Ignore.Driver.CHROME;
import static org.openqa.selenium.testing.Ignore.Driver.IPHONE;
import static org.openqa.selenium.testing.Ignore.Driver.OPERA;
import static org.openqa.selenium.testing.Ignore.Driver.SELENESE;
import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.elementToBeHidden;
import static org.openqa.selenium.WaitingConditions.elementTextToEqual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates how to use WebDriver with a file input element.
 * 
 * @author jmleyba@gmail.com (Jason Leyba)
 */
@Ignore(value = {IPHONE, ANDROID}, reason = "File uploads not allowed on the iPhone")
public class UploadTest extends JUnit4TestBase {

  private static final String LOREM_IPSUM_TEXT = "lorem ipsum dolor sit amet";
  private static final String FILE_HTML = "<div>" + LOREM_IPSUM_TEXT + "</div>";

  private File testFile;

  @Before
  public void setUp() throws Exception {
    testFile = createTmpFile(FILE_HTML);
  }

  @JavascriptEnabled
  @Ignore(value = {CHROME, SELENESE, OPERA},
      reason = "Chrome, Opera: File input elements are not supported yet")
  @Test
  public void testFileUploading() throws Exception {
    driver.get(pages.uploadPage);
    driver.findElement(By.id("upload")).sendKeys(testFile.getAbsolutePath());
    driver.findElement(By.id("go")).submit();

    // Uploading files across a network may take a while, even if they're really small
    WebElement label = driver.findElement(By.id("upload_label"));
    waitFor(elementToBeHidden(label), 30, TimeUnit.SECONDS);

    driver.switchTo().frame("upload_target");

    WebElement body = driver.findElement(By.xpath("//body"));
    waitFor(elementTextToEqual(body, LOREM_IPSUM_TEXT));
  }

  private File createTmpFile(String content) throws IOException {
    File f = File.createTempFile("webdriver", "tmp");
    f.deleteOnExit();

    OutputStream out = new FileOutputStream(f);
    PrintWriter pw = new PrintWriter(out);
    pw.write(content);
    pw.flush();
    pw.close();
    out.close();

    return f;
  }
}
