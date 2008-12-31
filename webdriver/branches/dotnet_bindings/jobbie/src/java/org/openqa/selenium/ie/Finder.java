package org.openqa.selenium.ie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByXPath;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// Kept package level deliberately.

class Finder implements SearchContext,
	FindsById, FindsByClassName, FindsByLinkText, FindsByName, FindsByXPath {

	private final WebDriverLibrary lib;
	private final Pointer wrapper;
	private final Pointer element;
	
	public Finder(WebDriverLibrary lib, Pointer wrapper, Pointer element) {
		this.lib = lib;
		this.wrapper = wrapper;
		this.element = element;
	}
	
	public WebElement findElementById(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementById(wrapper, element, name, ptr) == 0) {
			if (ptr.getValue() == null) {
				throw new NoSuchElementException("Cannot find element with id " + using);
			}
			return new InternetExplorerElement(lib, wrapper, ptr.getValue());
		}
		
		throw new RuntimeException("Cannot execute search");
	}

	public List<WebElement> findElementsById(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementsById(wrapper, element, name, ptr) != 0) {
			throw new RuntimeException("Cannot execute search");
		}
		
		return convertFromWrapperToList(ptr);
	}		
	
	public WebElement findElementByName(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementByName(wrapper, element, name, ptr) == 0) {
			if (ptr.getValue() == null) {
				throw new NoSuchElementException("Cannot find element with name " + using);
			}
			return new InternetExplorerElement(lib, wrapper, ptr.getValue());
		}
		
		throw new RuntimeException("Cannot execute search");
	}
	
	public List<WebElement> findElementsByName(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementsByName(wrapper, element, name, ptr) != 0) {
			throw new RuntimeException("Cannot execute search");
		}
		
		return convertFromWrapperToList(ptr);
	}		

	public WebElement findElementByClassName(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementByClassName(wrapper, element, name, ptr) == 0) {
			if (ptr.getValue() == null) {
				throw new NoSuchElementException("Cannot find element with id " + using);
			}
			return new InternetExplorerElement(lib, wrapper, ptr.getValue());
		}
		
		throw new RuntimeException("Cannot execute search");
	}
	
	public List<WebElement> findElementsByClassName(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementsByClassName(wrapper, element, name, ptr) != 0) {
			throw new RuntimeException("Cannot execute search");
		}
		
		return convertFromWrapperToList(ptr);
	}		

    public WebElement findElementByLinkText(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementByLinkText(wrapper, element, name, ptr) == 0) {
			if (ptr.getValue() == null) {
				throw new NoSuchElementException("Cannot find element with id " + using);
			}
			return new InternetExplorerElement(lib, wrapper, ptr.getValue());
		}
		
		throw new RuntimeException("Cannot execute search");

	}
	
	public List<WebElement> findElementsByLinkText(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementsByLinkText(wrapper, element, name, ptr) != 0) {
			throw new RuntimeException("Cannot execute search");
		}
		
		return convertFromWrapperToList(ptr);
	}		

	public WebElement findElementByPartialLinkText(String using) {
		throw new UnsupportedOperationException();
	}

	public List<WebElement> findElementsByPartialLinkText(String using) {
		throw new UnsupportedOperationException();
	}
	
    public WebElement findElementByXPath(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementByXPath(wrapper, element, name, ptr) == 0) {
			if (ptr.getValue() == null) {
				throw new NoSuchElementException("Cannot find element with id " + using);
			}
			return new InternetExplorerElement(lib, wrapper, ptr.getValue());
		}
		
		throw new RuntimeException("Cannot execute search");
	}
	
	public List<WebElement> findElementsByXPath(String using) {
		WString name = new WString(using);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementsByXPath(wrapper, element, name, ptr) != 0) {
			throw new RuntimeException("Cannot execute search");
		}
		
		return convertFromWrapperToList(ptr);
	}		
    
	public WebElement findElement(By by) {
		return by.findElement(this);
	}
	public List<WebElement> findElements(By by) {
		return by.findElements(this);
	}
	
	private List<WebElement> convertFromWrapperToList(PointerByReference ptr) {
		if (ptr.getValue() == null) {
			return Collections.emptyList();
		}
		
		Pointer collection = ptr.getValue();
		IntByReference sizeRef = new IntByReference();
		lib.wdcGetCollectionLength(collection, sizeRef);
		int length = sizeRef.getValue();

		System.out.println(length);
		
		ArrayList<WebElement> elements = new ArrayList<WebElement>();
		for (int i = 0; i < length; i++) {
			PointerByReference result = new PointerByReference();
			lib.wdcGetElementAtIndex(collection, i, result);
			elements.add(new InternetExplorerElement(lib, wrapper, result.getValue()));
		}
		
		return elements;
	}
}
