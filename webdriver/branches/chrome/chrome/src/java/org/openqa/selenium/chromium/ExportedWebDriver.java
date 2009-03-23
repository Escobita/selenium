package org.openqa.selenium.chromium;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

interface ExportedWebDriver extends StdCallLibrary {
  public int SUCCESS = 0;

  // ----------------------------------------------------------------------------
  // String Related.
  // ----------------------------------------------------------------------------
  int wdStringLength(Pointer string, IntByReference length);

  int wdFreeString(Pointer string);

  int wdCopyString(Pointer string, int length, char[] rawString);

  // ----------------------------------------------------------------------------
  // Driver Related.
  // ----------------------------------------------------------------------------
  int wdNewDriverInstance(PointerByReference ptr);

  int wdFreeDriverInstance(Pointer driver);

  // ----------------------------------------------------------------------------
  // Browser Related
  // ----------------------------------------------------------------------------
  int wdGetVisible(Pointer driver, IntByReference isVisible);

  int wdSetVisible(Pointer driver, int visible);

  int wdClose(Pointer driver);

  // ----------------------------------------------------------------------------
  // Page Related.
  // ----------------------------------------------------------------------------
  int wdGet(Pointer driver, WString url);

  int wdGoBack(Pointer driver);

  int wdGoForward(Pointer driver);

  int wdGetCurrentUrl(Pointer driver, PointerByReference url);

  int wdGetTitle(Pointer driver, PointerByReference title);

  int wdGetPageSource(Pointer driver, PointerByReference source);

  int wdSwitchToActiveElement(Pointer driver, PointerByReference element);

  int wdSwitchToFrame(Pointer driver, int frame_index);

  int wdSwitchToFrame(Pointer driver, WString frame_name);

  int wdSwitchToWindow(Pointer driver, WString window_name);

  // ----------------------------------------------------------------------------
  // Cookie Related.
  // ----------------------------------------------------------------------------
  int wdGetCookies(Pointer driver, PointerByReference wrapper);

  int wdAddCookie(Pointer driver, WString string);

  // ----------------------------------------------------------------------------
  // Element Related.
  // ----------------------------------------------------------------------------
  int wdFreeElement(Pointer element);

  int wdFindElementById(Pointer driver, WString id, PointerByReference element);

  int wdFindElementsById(Pointer driver, WString id, PointerByReference collection);

  int wdFindElementByTagName(Pointer driver, WString tag, PointerByReference element);

  int wdFindElementsByTagName(Pointer driver, WString tag, PointerByReference collection);

  int wdFindElementByClassName(Pointer driver, WString cls, PointerByReference element);

  int wdFindElementsByClassName(Pointer driver, WString cls, PointerByReference collection);

  int wdFindElementByLinkText(Pointer driver, WString text, PointerByReference element);

  int wdFindElementsByLinkText(Pointer driver, WString text, PointerByReference collection);

  int wdFindElementByPartialLinkText(Pointer driver, WString pattern, PointerByReference element);

  int wdFindElementsByPartialLinkText(
      Pointer driver, WString pattern, PointerByReference collection);

  int wdFindElementByName(Pointer driver, WString name, PointerByReference element);

  int wdFindElementsByName(Pointer driver, WString name, PointerByReference collection);

  int wdFindElementByXPath(Pointer driver, WString xpath, PointerByReference element);

  int wdFindElementsByXPath(Pointer driver, WString xpath, PointerByReference collection);

  // ----------------------------------------------------------------------------
  // Element Collection Related.
  // ----------------------------------------------------------------------------
  int wdFreeElementCollection(Pointer collection);

  int wdGetCollectionLength(Pointer collection, IntByReference length);

  int wdGetElementAtIndex( Pointer collection, int index, PointerByReference result);

  // ----------------------------------------------------------------------------
  // Element Operation Related.
  // ----------------------------------------------------------------------------
  int wdSubmit(Pointer element);

  int wdClear(Pointer element);

  int wdClick(Pointer element);

  int wdIsEnabled(Pointer element, IntByReference selected);

  int wdGetAttribute(Pointer element, WString string, PointerByReference wrapper);

  int wdGetValueOfCssProperty(Pointer element, WString name, PointerByReference wrapper);

  int wdIsSelected(Pointer element, IntByReference selected);

  int wdSetSelected(Pointer element);

  int wdToggle(Pointer element, IntByReference toReturn);

  int wdSendKeys(Pointer element, WString string);

  int wdIsDisplayed(Pointer element, IntByReference displayed);

  int wdGetText(Pointer element, PointerByReference wrapper);

  int wdGetElementName(Pointer element, PointerByReference wrapper);

  int wdGetLocation(Pointer element, NativeLongByReference x, NativeLongByReference y);

  int wdGetSize(Pointer element, NativeLongByReference width, NativeLongByReference height);

  // ----------------------------------------------------------------------------
  // Structure Wrapper Classes.
  // ----------------------------------------------------------------------------
  public static class StringWrapper {
    private final String value;

    public StringWrapper(ExportedWebDriver lib, PointerByReference ptr) {
      value = extractString(lib, ptr.getValue());
    }

    private String extractString(ExportedWebDriver lib, Pointer string) {
      IntByReference length = new IntByReference();
      if (lib.wdStringLength(string, length) != 0) {
        lib.wdFreeString(string);
        throw new RuntimeException("Cannot determine length of string");
      }
      char[] rawString = new char[length.getValue()];
      if (lib.wdCopyString(string, length.getValue(), rawString) != 0) { 
        lib.wdFreeString(string);
        throw new RuntimeException("Cannot copy string from native data to Java string");
      }

      String value = Native.toString(rawString);
      lib.wdFreeString(string);
      return value;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
