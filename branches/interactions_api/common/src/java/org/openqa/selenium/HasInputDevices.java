package org.openqa.selenium;

/**
 * Interface implemented by each driver that allows access to the raw input devices.
 */
public interface HasInputDevices {
  Keyboard getKeyboard();
  Mouse getMouse();
}
