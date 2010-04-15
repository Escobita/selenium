package org.openqa.selenium.internal;

import com.sun.istack.internal.Nullable;
import org.openqa.selenium.WebElement;

/**
 * Interface representing basic keyboard operations.
 *
 * Created by Eran Mes (eran.mes@gmail.com)
 */
public interface Keyboard {
  void sendKeys(@Nullable WebElement toElement, CharSequence... keysToSend);
  void pressKey(@Nullable WebElement toElement, CharSequence keyToPress);
  void releaseKey(@Nullable WebElement toElement, CharSequence keyToRelease);
}
