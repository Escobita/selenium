/**
 * 
 */
package org.openqa.selenium.ie;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

class Helpers {
	private final WebDriverLibrary lib;
	
	public Helpers(WebDriverLibrary lib) {
		this.lib = lib;
	}

	public String convertToString(PointerByReference ptr) {
		Pointer string = ptr.getValue();
		IntByReference length = new IntByReference();
		if (lib.wdStringLength(string, length) != 0)
			throw new RuntimeException("Cannot determine length of string");
		char[] rawString = new char[length.getValue()];
		if (lib.wdCopyString(string, length.getValue(), rawString) != 0) 
			throw new RuntimeException("Cannot copy string from native data to Java string");
		
		String toReturn = Native.toString(rawString);
		lib.wdFreeString(string);
		
		return toReturn;
	}
}