package org.openqa.selenium.internal;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Interface representing basic keyboard operations.
 *
 * Created by Eran Mes (eran.mes@gmail.com)
 */
public interface Keyboard {
  void sendKeys(WebElement toElement, CharSequence... keysToSend);
  void pressKey(WebElement toElement, Keys keyToPress);
  void releaseKey(WebElement toElement, Keys keyToRelease);
}
