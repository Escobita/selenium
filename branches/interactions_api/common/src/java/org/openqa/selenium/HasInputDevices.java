package org.openqa.selenium;

import org.openqa.selenium.internal.Keyboard;

/**
 * Interface implemented by each driver that allows access to the raw input devices.
 */
public interface HasInputDevices {
  Keyboard getKeyboard();
  
}
