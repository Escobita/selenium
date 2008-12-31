#include "stdafx.h"
#include "webdriver.h"
#include "finder.h"
#include "InternetExplorerDriver.h"
#include "utils.h"
#include <stdio.h>
#include <string>
#include <vector>

#define END_TRY  catch(std::wstring&) \
	{ \
		return -100; \
	} \
	catch (...) \
	{ \
	safeIO::CoutA("CException caught in dll", true); \
	return -110; }


struct WebDriver {
    InternetExplorerDriver *ie;
};

struct WebElement {
	ElementWrapper *element;
};

struct StringWrapper {
	wchar_t *text;
};

struct ElementCollection {
	std::vector<ElementWrapper*>* elements;
};

InternetExplorerDriver* openIeInstance = NULL;

extern "C"
{

// String manipulation functions
int wdStringLength(StringWrapper* string, int* length)
{
	if (!string) {
		cerr << "No string to get length of" << endl;
		*length = -1;
		return -ENOSTRING;
	}
	if (!string->text) {
		cerr << "No underlying string to get length of" << endl;
		*length = -1;
		return -ENOSTRINGLENGTH;
	}
	size_t len = wcslen(string->text);
	*length = (int) len + 1;

	return SUCCESS;
}

int wdFreeString(StringWrapper* string)
{
	if (!string) {
		return  -ENOSTRING;
	}

	if (string->text) delete[] string->text;
	delete string;
	return SUCCESS;
}

int wdCopyString(StringWrapper* source, int size, wchar_t* dest)
{
	if (!source) {
		cerr << "No source wrapper" << endl;
		return -ENOSTRINGWRAPPER;
	}

	if (!source->text) {
		cerr << "No source text" << endl;
		return -ENOSTRING;
	}

	wcscpy_s(dest, size, source->text);
	return SUCCESS;
}

// Collection manipulation functions
int wdcGetCollectionLength(ElementCollection* collection, int* length)
{
	if (!collection || !collection->elements) return -ENOCOLLECTION;

	*length = (int) collection->elements->size();

	return SUCCESS;
}

int wdcGetElementAtIndex(ElementCollection* collection, int index, WebElement** result)
{
	if (!collection || !collection->elements) return -ENOCOLLECTION;

	std::vector<ElementWrapper*>::const_iterator cur = collection->elements->begin();
	std::vector<ElementWrapper*>::const_iterator end = collection->elements->end();
	cur += index;

	if (cur > end) {
		return -EINDEXOUTOFBOUNDS;
	}

	WebElement* element = new WebElement();
	element->element = *cur;
	*result = element;

	return SUCCESS;
}

// Element manipulation functions
int wdeFreeElement(WebElement* element)
{
	if (!element)
		return -ENOSUCHELEMENT;

	if (element->element) delete element->element;
	delete element;
	return SUCCESS;
}

// Driver manipulation functions
int wdFreeDriver(WebDriver* driver)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;
	driver->ie->close();
    delete driver->ie;
    delete driver;
	return SUCCESS;
}

int wdNewDriverInstance(WebDriver** result)
{
	TRY
	{
	    WebDriver *driver = new WebDriver();
   
		driver->ie = new InternetExplorerDriver();
		driver->ie->setVisible(true);

		if (openIeInstance) 
		{
			openIeInstance->close();
			openIeInstance = NULL;
		}

		openIeInstance = driver->ie;

		*result = driver;

		return SUCCESS;
	}
	END_TRY

	return -ENOSUCHDRIVER;
}

int wdClose(WebDriver* driver)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;
	driver->ie->close();
	openIeInstance = NULL;
	return SUCCESS;
}

int wdGet(WebDriver* driver, wchar_t* url)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;
	driver->ie->get(url);
	return SUCCESS;
}

int wdGoBack(WebDriver* driver)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;
	driver->ie->goBack();
	return SUCCESS;
}

int wdGoForward(WebDriver* driver) 
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;
	driver->ie->goForward();
	return SUCCESS;
}

int wdGetVisible(WebDriver* driver, int* result)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	*result = driver->ie->getVisible() ? 1 : 0;

	return SUCCESS;
}

int wdSetVisible(WebDriver* driver, int value) 
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	driver->ie->setVisible(value != 0);
	cout << "Returning now" << endl;
	return SUCCESS;
}

int wdGetCurrentUrl(WebDriver* driver, StringWrapper** result)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	const std::wstring originalString(driver->ie->getCurrentUrl());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdGetTitle(WebDriver* driver, StringWrapper** result)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	const std::wstring originalString(driver->ie->getTitle());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdGetPageSource(WebDriver* driver, StringWrapper** result)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	const std::wstring originalString(driver->ie->getPageSource());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdGetCookies(WebDriver* driver, StringWrapper** result)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	const std::wstring originalString(driver->ie->getCookies());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdAddCookie(WebDriver* driver, const wchar_t* cookie)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	driver->ie->addCookie(cookie);

	return SUCCESS;
}

int wdSwitchToActiveElement(WebDriver* driver, WebElement** result)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	ElementWrapper* element = driver->ie->getActiveElement();

	if (!element)
		return -ENOSUCHELEMENT;

	WebElement* toReturn = new WebElement();
	toReturn->element = element;
	*result = toReturn;

	return SUCCESS;
}

int wdSwitchToFrame(WebDriver* driver, const wchar_t* path)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	return driver->ie->switchToFrame(path) ? SUCCESS : -ENOSUCHFRAME;
}

int wdWaitForLoadToComplete(WebDriver* driver)
{
	if (!driver || !driver->ie) return -ENOSUCHDRIVER;

	driver->ie->waitForNavigateToFinish();

	return SUCCESS;
}

int wdeGetAttribute(WebElement* element, const wchar_t* name, StringWrapper** result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	const std::wstring originalString(element->element->getAttribute(name));
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdeGetValueOfCssProperty(WebElement* element, const wchar_t* name, StringWrapper** result)
{
	const std::wstring originalString(element->element->getValueOfCssProperty(name));
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdeIsEnabled(WebElement* element, int* result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	*result = element->element->isEnabled() ? 1 : 0;

	return SUCCESS;
}

int wdeIsSelected(WebElement* element, int* result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	*result = element->element->isSelected() ? 1 : 0;

	return SUCCESS;
}

int wdeSetSelected(WebElement* element)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	element->element->setSelected();

	return SUCCESS;
}

int wdeToggle(WebElement* element, int* result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	*result = element->element->toggle() ? 1 : 0;

	return SUCCESS;
}

int wdeGetText(WebElement* element, StringWrapper** result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	const std::wstring originalString(element->element->getText());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdeGetElementName(WebElement* element, StringWrapper** result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	const std::wstring originalString(element->element->getElementName());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());

	StringWrapper* res = new StringWrapper();
	res->text = toReturn;
	
	*result = res;

	return SUCCESS;
}

int wdeIsDisplayed(WebElement* element, int* result)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	*result = element->element->isDisplayed() ? 1 : 0;

	return SUCCESS;
}

int wdeGetLocation(WebElement* element, long* x, long* y)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	element->element->getLocation(x, y);

	return SUCCESS;
}

int wdeGetSize(WebElement* element, long* width, long* height)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	*width = element->element->getWidth();
	*height = element->element->getHeight();

	return SUCCESS;
}

int wdeSendKeys(WebElement* element, const wchar_t* text)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	element->element->sendKeys(text);

	return SUCCESS;
}

int wdeClear(WebElement* element) 
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	element->element->clear();
	return SUCCESS;
}

int wdeSubmit(WebElement* element)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	element->element->submit();
	return SUCCESS;
}

int wdeGetDetailsOnceScrolledOnToScreen(WebElement* element, HWND* hwnd, long* x, long* y, long* width, long* height)
{
	if (!element || !element->element) { return -ENOSUCHELEMENT; }

	element->element->getLocationOnceScrolledIntoView(hwnd, x, y);

	return SUCCESS;
}

int wdFindElementById(WebDriver* driver, WebElement* element, const wchar_t* id, WebElement** result)
{
	if (!driver || !driver->ie) { return -ENOSUCHDRIVER; }

	CComPtr<IHTMLDOMNode> res;
	InternetExplorerDriver* ie = driver->ie;
	CComPtr<IHTMLElement> elem;
	if (element && element->element) {
		elem = element->element->getWrappedElement();
	}

	ElementWrapper* wrapper = NULL;
	try {
		if (ie->selectElementById(elem, id, &wrapper) != SUCCESS)
			return -ENOSUCHELEMENT;
	} catch (std::wstring& ) {
		return -1000;
	}

	if (!wrapper) {
		return -2000;
	}

	WebElement* toReturn = new WebElement();
	toReturn->element = wrapper;

	*result = toReturn;
	return SUCCESS;
}

int wdFindElementsById(WebDriver* driver, WebElement* element, const wchar_t* id, ElementCollection** result) 
{
	return -ENOTIMPLEMENTED;
}

int wdFindElementByName(WebDriver* driver, WebElement* element, const wchar_t* name, WebElement** result)
{
	CComPtr<IHTMLDOMNode> res;
	InternetExplorerDriver* ie = driver->ie;
	CComPtr<IHTMLElement> elem;
	if (element && element->element) {
		elem = element->element->getWrappedElement();
	}

	ElementWrapper* wrapper = NULL;
	try {
		wrapper = ie->selectElementByName(elem, name);
	} catch (std::wstring& ) {
		return -1000;
	}

	if (!wrapper) {
		return -2000;
	}

	WebElement* toReturn = new WebElement();
	toReturn->element = wrapper;

	*result = toReturn;
	return SUCCESS;
}

int wdFindElementsByName(WebDriver* driver, WebElement* element, const wchar_t* name, ElementCollection** result)
{
	return -ENOTIMPLEMENTED;
}

int wdFindElementByClassName(WebDriver* driver, WebElement* element, const wchar_t* className, WebElement** result)
{
	CComPtr<IHTMLDOMNode> res;
	InternetExplorerDriver* ie = driver->ie;
	CComPtr<IHTMLElement> elem;
	if (element && element->element) {
		elem = element->element->getWrappedElement();
	}

	ElementWrapper* wrapper = NULL;
	try {
		wrapper = ie->selectElementByClassName(elem, className);
	} catch (std::wstring& ) {
		return -1000;
	}

	if (!wrapper) {
		return -2000;
	}

	WebElement* toReturn = new WebElement();
	toReturn->element = wrapper;

	*result = toReturn;
	return SUCCESS;
}

int wdFindElementsByClassName(WebDriver* driver, WebElement* element, const wchar_t* className, ElementCollection** result)
{
	return -ENOTIMPLEMENTED;
}

int wdFindElementByLinkText(WebDriver* driver, WebElement* element, const wchar_t* linkText, WebElement** result)
{
	CComPtr<IHTMLDOMNode> res;
	InternetExplorerDriver* ie = driver->ie;
	CComPtr<IHTMLElement> elem;
	if (element && element->element) {
		elem = element->element->getWrappedElement();
	}

	ElementWrapper* wrapper = NULL;
	try {
		wrapper = ie->selectElementByLink(elem, linkText);
	} catch (std::wstring& ) {
		return -1000;
	}

	if (!wrapper) {
		return -2000;
	}

	WebElement* toReturn = new WebElement();
	toReturn->element = wrapper;

	*result = toReturn;
	return SUCCESS;
}

int wdFindElementsByLinkText(WebDriver* driver, WebElement* element, const wchar_t* linkText, ElementCollection** result)
{
	return -ENOTIMPLEMENTED;
}

int wdFindElementByXPath(WebDriver* driver, WebElement* element, const wchar_t* xpath, WebElement** result)
{
	CComPtr<IHTMLDOMNode> res;
	InternetExplorerDriver* ie = driver->ie;
	CComPtr<IHTMLElement> elem;
	if (element && element->element) {
		elem = element->element->getWrappedElement();
	}

	ElementWrapper* wrapper = NULL;
	try {
		wrapper = ie->selectElementByXPath(elem, xpath);
	} catch (std::wstring& ) {
		return -1000;
	}

	if (!wrapper) {
		return -2000;
	}

	WebElement* toReturn = new WebElement();
	toReturn->element = wrapper;

	*result = toReturn;
	return SUCCESS;
}

int wdFindElementsByXPath(WebDriver* driver, WebElement* element, const wchar_t* xpath, ElementCollection** result)
{
	ElementCollection* collection = new ElementCollection();
	collection->elements = driver->ie->selectElementsByXPath(NULL, xpath);

	*result = collection;

	return SUCCESS;
}

int wdeFindChildrenOfType(WebElement* element, const wchar_t* tagName, ElementCollection** result)
{
	ElementCollection* collection = new ElementCollection();
	collection->elements = element->element->getChildrenWithTagName(tagName);

	*result = collection;

	return SUCCESS;
}

}