package org.openqa.selenium.ie;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.WebDriverLibrary.HWNDByReference;
import org.openqa.selenium.internal.InteractionData;
import org.openqa.selenium.internal.Locatable;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public class InternetExplorerElement implements RenderedWebElement, SearchContext, Locatable {
    private final Pointer element;
	private final Pointer parent;
	private final WebDriverLibrary lib;
	private final Helpers helpers;

	// Called from native code
    public InternetExplorerElement(WebDriverLibrary lib, Pointer parent, Pointer element) {
        this.lib = lib;
		this.parent = parent;
		this.element = element;
		
		helpers = new Helpers(lib);
    }

    public void click() {
    	InternetExplorerInteractionData data = (InternetExplorerInteractionData) getLocationOnScreenOnceScrolledIntoView();
    	lib.clickAt(data.getHwnd(), new NativeLong(data.getX()), new NativeLong(data.getY()));
    	lib.wdWaitForLoadToComplete(parent);
    }

    public String getElementName() {
    	PointerByReference result = new PointerByReference();
    	lib.wdeGetElementName(element, result);
    	
    	return helpers.convertToString(result);
    }

    public String getAttribute(String name) {
    	PointerByReference result = new PointerByReference();
    	lib.wdeGetAttribute(element, new WString(name), result);
    	
    	return helpers.convertToString(result);
    }

    public List<WebElement> getChildrenOfType(String tagName) {
    	WString name = new WString(tagName);
		PointerByReference ptr = new PointerByReference();
		if (lib.wdFindElementsByLinkText(parent, element, name, ptr) != 0) {
			throw new RuntimeException("Cannot execute search");
		}
		
		return convertFromWrapperToList(ptr);
    }

    public String getText() {
    	PointerByReference result = new PointerByReference();
    	lib.wdeGetText(element, result);
    	
    	return helpers.convertToString(result);
    }

    public String getValue() {
    	return getAttribute("value");
    }

    public void sendKeys(CharSequence... value) {
    	StringBuilder builder = new StringBuilder();
    	for (CharSequence seq : value) {
    		builder.append(seq);
    	}

    	lib.wdeSendKeys(element, new WString(builder.toString()));
    	lib.wdWaitForLoadToComplete(parent);
    }

    public void clear() {
    	lib.wdeClear(element);
    }

    public boolean isEnabled() {
    	IntByReference res = new IntByReference();
    	lib.wdeIsEnabled(element, res);
    	return res.getValue() == 1;   	
    }

    public boolean isSelected() {
    	IntByReference res = new IntByReference();
    	lib.wdeIsSelected(element, res);
    	return res.getValue() == 1; 
    }

    public void setSelected() {
    	lib.wdeSetSelected(element);
    	lib.wdWaitForLoadToComplete(parent);
    }

    public void submit() {
    	if (lib.wdeSubmit(element) != 0) {
    		throw new RuntimeException("Cannot submit element");
    	}
    	lib.wdWaitForLoadToComplete(parent);
    }

    public boolean toggle() {
    	IntByReference res = new IntByReference();
    	lib.wdeToggle(element, res);
    	lib.wdWaitForLoadToComplete(parent);
    	return res.getValue() == 1; 
    }

    public boolean isDisplayed() {
    	IntByReference res = new IntByReference();
    	lib.wdeIsDisplayed(element, res);
    	return res.getValue() == 1;   	
    }

    public InteractionData getLocationOnScreenOnceScrolledIntoView() {
    	HWNDByReference hwnd = new HWNDByReference();
    	IntByReference x = new IntByReference();
    	IntByReference y = new IntByReference();
    	IntByReference width = new IntByReference();
    	IntByReference height = new IntByReference();
    	if (lib.wdeGetDetailsOnceScrolledOnToScreen(element, hwnd, x, y, width, height) != 0) 
    		return null;
    	
    	return new InternetExplorerInteractionData(hwnd.getValue(), x.getValue(), y.getValue());
    }
    
    public Point getLocation() {
    	NativeLongByReference x = new NativeLongByReference();
    	NativeLongByReference y = new NativeLongByReference();
    	lib.wdeGetLocation(element, x, y);
    	
    	return new Point(x.getValue().intValue(), y.getValue().intValue());
    }

    public Dimension getSize() {
    	NativeLongByReference width = new NativeLongByReference();
    	NativeLongByReference height = new NativeLongByReference();
    	lib.wdeGetSize(element, width, height);
    	
    	return new Dimension(width.getValue().intValue(), height.getValue().intValue());
    }

    public String getValueOfCssProperty(String propertyName) {
    	PointerByReference result = new PointerByReference();
    	lib.wdeGetValueOfCssProperty(element, new WString(propertyName), result);
    	
    	return helpers.convertToString(result);
    }
    
    @Override
    protected void finalize() throws Throwable {
        lib.wdeFreeElement(element);
    }

    public void dragAndDropBy(int moveRightBy, int moveDownBy) {
        throw new UnsupportedOperationException();
    }

    public void dragAndDropOn(RenderedWebElement element) {
        throw new UnsupportedOperationException();
    }

    public WebElement findElement(By by) {
		return new Finder(lib, parent, element).findElement(by);
    }

    public List<WebElement> findElements(By by) {
		return new Finder(lib, parent, element).findElements(by);
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
			elements.add(new InternetExplorerElement(lib, parent, result.getValue()));
		}
		
		return elements;
	}
}
