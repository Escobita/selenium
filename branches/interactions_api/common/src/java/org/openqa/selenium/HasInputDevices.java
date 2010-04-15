package org.openqa.selenium;

import org.openqa.selenium.internal.Keyboard;

/**
 * Interface implemented by each driver that allows access to the raw input devices.
 * Created by Eran Mes (eran.mes@gmail.com)
 */
public interface HasInputDevices {
  Keyboard getKeyboard();
  
}
