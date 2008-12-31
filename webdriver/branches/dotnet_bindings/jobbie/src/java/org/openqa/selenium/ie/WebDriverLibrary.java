package org.openqa.selenium.ie;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.WString;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface WebDriverLibrary extends StdCallLibrary {	
	// String methods
	int wdStringLength(Pointer string, IntByReference length);
	int wdFreeString(Pointer string);
	int wdCopyString(Pointer source, int length, char[] dest);
	
	// Element collection methods
	int wdcGetCollectionLength(Pointer collection, IntByReference sizeRef);
	int wdcGetElementAtIndex(Pointer collection, int index, PointerByReference result);
	
	// Driver methods
	int wdNewDriverInstance(PointerByReference ptr);
	int wdFreeDriver(Pointer driver);
	
	int wdClose(Pointer driver);
	int wdGet(Pointer driver, WString url);
	int wdGoBack(Pointer driver);
	int wdGoForward(Pointer driver);
	int wdGetCurrentUrl(Pointer driver, PointerByReference ptr);
	int wdeGetDetailsOnceScrolledOnToScreen(Pointer element, HWNDByReference hwnd, IntByReference x, IntByReference y, IntByReference width, IntByReference height);
	int wdGetTitle(Pointer driver, PointerByReference ptr);
	int wdGetPageSource(Pointer driver, PointerByReference ptr);
	int wdGetVisible(Pointer driver, IntByReference result);
	int wdSetVisible(Pointer driver, int value);
	
	int wdAddCookie(Pointer driver, WString cookie);
	int wdGetCookies(Pointer driver, PointerByReference cookies);
	int wdSwitchToActiveElement(Pointer driver, PointerByReference element);
	int wdSwitchToFrame(Pointer driver, WString frameName);
	int wdWaitForLoadToComplete(Pointer driver);
	
	int wdFindElementByClassName(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementsByClassName(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementById(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementsById(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementByLinkText(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementsByLinkText(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementByName(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementsByName(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementByXPath(Pointer driver, Pointer element, WString name, PointerByReference result);
	int wdFindElementsByXPath(Pointer wrapper, Pointer element, WString name, PointerByReference result);
	int wdeFindChildrenOfType(Pointer element, WString name, PointerByReference result);
	
	// Element methods
	int wdeClick(Pointer element);
	int wdeGetAttribute(Pointer element, WString string, PointerByReference result);
	int wdeGetValueOfCssProperty(Pointer element, WString name, PointerByReference result);
	int wdeIsEnabled(Pointer element, IntByReference res);
	int wdeIsSelected(Pointer element, IntByReference res);
	int wdeSetSelected(Pointer element);
	int wdeToggle(Pointer element, IntByReference res);
	int wdeGetText(Pointer element, PointerByReference result);
	int wdeGetElementName(Pointer element, PointerByReference result);
	int wdeIsDisplayed(Pointer element, IntByReference res);
	int wdeGetLocation(Pointer element, NativeLongByReference x, NativeLongByReference y);
	int wdeGetSize(Pointer element, NativeLongByReference x, NativeLongByReference y);
	int wdeSendKeys(Pointer element, WString text);
	int wdeClear(Pointer element);
	int wdeSubmit(Pointer elemet);
	int wdeFreeElement(Pointer element);
	
	
	// Break this stuff out
	int clickAt(HWND hwnd, NativeLong x, NativeLong y);
	
    public static class HWND extends PointerType { }
    
    public static class HWNDByReference extends ByReference {
        public HWNDByReference() {
            this(null);
        }
        public HWNDByReference(HWND h) {
            super(Pointer.SIZE);
            setValue(h);
        }
        public void setValue(HWND h) {
            getPointer().setPointer(0, h != null ? h.getPointer() : null);
        }
        public HWND getValue() {
            Pointer p = getPointer().getPointer(0);
            if (p == null)
                return null;
            HWND h = new HWND();
            h.setPointer(p);
            return h;
        }
    }
}
