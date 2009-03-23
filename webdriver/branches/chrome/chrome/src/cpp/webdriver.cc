#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <iostream>

#include "webdriver.h"
#include "chromedriver.h"
#include "chromeelement.h"

#ifdef __cplusplus
extern "C" {
#endif  // __cplusplus

void error(const char* msg) {
  std::cerr << "INFO: " << msg << std::endl;
}

// ----------------------------------------------------------------------------
// String Related implementation.
// ----------------------------------------------------------------------------
int wdStringLength(StringWrapper* str, int* length) {
	*length = -1;
	if (!str || !str->text) {
		error("No string to get length.");
		return !SUCCESS;
	}

	size_t len = wcslen(str->text);
	*length = (int)len + 1;
	return SUCCESS;
}

int wdFreeString(StringWrapper* str) {
  if (!str) return !SUCCESS;
	if (str->text) delete[] str->text;
	delete str;

	return SUCCESS;
}

int wdCopyString(StringWrapper* src, int size, wchar_t* dest) {
	if (!src || !src->text) {
    error("No source string to copy.");
    return !SUCCESS;
	}

	wcscpy_s(dest, size, src->text);
	return SUCCESS;
}

void CopyToStringWrapper(const std::wstring& src, StringWrapper** wrap) {
  size_t length = src.length() + 1;
  *wrap = new StringWrapper();
  (*wrap)->text = new wchar_t[length];
  wcscpy_s((*wrap)->text, length, src.c_str());
}

// ----------------------------------------------------------------------------
// Driver Related implementation.
// ----------------------------------------------------------------------------
int wdNewDriverInstance(WebDriver** ptrDriver) {
  if (*ptrDriver) return !SUCCESS;

  // Allocate instance
  (*ptrDriver = new WebDriver())->instance = new ChromeDriver();

  if ((*ptrDriver)->instance->Launch() == SUCCESS) return SUCCESS;

  // If failed then clean up.
  delete (*ptrDriver)->instance;
  (*ptrDriver)->instance = NULL;
  delete *ptrDriver;
  *ptrDriver = NULL;
  return !SUCCESS;
}

int wdFreeDriverInstance(WebDriver* ptrDriver) {
  if (ptrDriver) {
    delete ptrDriver->instance;
    delete ptrDriver;
    return SUCCESS;
  }
  return !SUCCESS;
}

// ----------------------------------------------------------------------------
// Browser Related implementation.
// ----------------------------------------------------------------------------
int wdGetVisible(WebDriver* ptrDriver, int* visible) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  *visible = ptrDriver->instance->getVisible();
  return SUCCESS;
}

int wdSetVisible(WebDriver* ptrDriver, int value) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->setVisible(value);
}

int wdClose(WebDriver* ptrDriver) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->close();
}

// ----------------------------------------------------------------------------
// Page Related implementation.
// ----------------------------------------------------------------------------
int wdGet(WebDriver* ptrDriver, const wchar_t* url) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->get(url);
}

int wdGoBack(WebDriver* ptrDriver) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->back();
}

int wdGoForward(WebDriver* ptrDriver) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->forward();
}

int wdGetCurrentUrl(WebDriver* ptrDriver, StringWrapper** result) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  const std::wstring orgStr(ptrDriver->instance->getCurrentUrl());
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdGetTitle(WebDriver* ptrDriver, StringWrapper** result) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  const std::wstring orgStr(ptrDriver->instance->getTitle());
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdGetPageSource(WebDriver* ptrDriver, StringWrapper** result) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  const std::wstring orgStr(ptrDriver->instance->getPageSource());
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdSwitchToActiveElement(WebDriver* ptrDriver, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->switchToActiveElement(&returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdSwitchToFrameIndex(WebDriver* ptrDriver, int frame_index) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->switchToFrame(frame_index);
}

int wdSwitchToFrame(WebDriver* ptrDriver, const wchar_t* frame_name) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->switchToFrame(frame_name);
}

int wdSwitchToWindow(WebDriver* ptrDriver, const wchar_t* window_name) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->switchToWindow(window_name);
}

// ----------------------------------------------------------------------------
// Cookie Related implementation.
// ----------------------------------------------------------------------------
int wdGetCookies(WebDriver* ptrDriver, StringWrapper** result) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  const std::wstring orgStr(ptrDriver->instance->getCookies());
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdAddCookie(WebDriver* ptrDriver, const wchar_t* cookie) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  return ptrDriver->instance->addCookie(cookie);
}

// ----------------------------------------------------------------------------
// Element Related implementation.
// ----------------------------------------------------------------------------
int wdFreeElement(WebElement* element) {
  if (!element || !element->element) {
    error("No element to free.");
    return !SUCCESS;
  }

  delete element->element;
  delete element;
  return SUCCESS;
}

int wdFindElementById(
    WebDriver* ptrDriver, const wchar_t* id, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementById(id, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsById(
    WebDriver* ptrDriver, const wchar_t* id, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsById(id);
  return SUCCESS;
}

int wdFindElementByTagName(
    WebDriver* ptrDriver, const wchar_t* tag, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementByTagName(tag, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsByTagName(
    WebDriver* ptrDriver, const wchar_t* tag, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsByTagName(tag);
  return SUCCESS;
}

int wdFindElementByClassName(
    WebDriver* ptrDriver, const wchar_t* cls, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementByClassName(cls, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsByClassName(
    WebDriver* ptrDriver, const wchar_t* cls, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsByClassName(cls);
  return SUCCESS;
}

int wdFindElementByLinkText(
    WebDriver* ptrDriver, const wchar_t* text, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementByLinkText(text, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsByLinkText(
    WebDriver* ptrDriver, const wchar_t* text, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsByLinkText(text);
  return SUCCESS;
}

int wdFindElementByPartialLinkText(
    WebDriver* ptrDriver, const wchar_t* pattern, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementByPartialLinkText(pattern, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsByPartialLinkText(
    WebDriver* ptrDriver, const wchar_t* pattern, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsByPartialLinkText(pattern);
  return SUCCESS;
}

int wdFindElementByName(
    WebDriver* ptrDriver, const wchar_t* name, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementByName(name, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsByName(
    WebDriver* ptrDriver, const wchar_t* name, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsByName(name);
  return SUCCESS;
}

int wdFindElementByXPath(
    WebDriver* ptrDriver, const wchar_t* xpath, WebElement** element) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  ChromeElement* returnElement;
  if (ptrDriver->instance->findElementByXPath(xpath, &returnElement) != SUCCESS) {
    return !SUCCESS;
  }

  (*element = new WebElement())->element = returnElement;
  return SUCCESS;
}

int wdFindElementsByXPath(
    WebDriver* ptrDriver, const wchar_t* xpath, WebElementCollection** collection) {
  if (!ptrDriver || !ptrDriver->instance) {
    error("No driver found.");
    return !SUCCESS;
  }

  (*collection = new WebElementCollection())->elements
      = ptrDriver->instance->findElementsByXPath(xpath);
  return SUCCESS;
}

// ----------------------------------------------------------------------------
// Element Collection Related implementation.
// ----------------------------------------------------------------------------
int wdFreeElementCollection(WebElementCollection* collection) {
  if (!collection || !collection->elements) {
    error("Empty collections to return from.");
    return !SUCCESS;
  }

  std::vector<ChromeElement*>::const_iterator cur = collection->elements->begin();
  std::vector<ChromeElement*>::const_iterator end = collection->elements->end();
  while (cur != end) {
    delete *cur;
    cur++;
  }

	delete collection->elements;
	delete collection;
	return SUCCESS;
}

int wdGetCollectionLength(WebElementCollection* collection, int* length) {
  if (!collection || !collection->elements) {
    error("Empty collections to return from.");
    return !SUCCESS;
  }

	*length = (int) collection->elements->size();
  return SUCCESS;
}

int wdGetElementAtIndex(
    WebElementCollection* collection, int index, WebElement** result) {
  if (!collection || !collection->elements) {
    error("Empty collections to return from.");
    return !SUCCESS;
  }

	std::vector<ChromeElement*>::const_iterator cur = collection->elements->begin();
	cur += index;

  (*result = new WebElement())->element = *cur;
  return SUCCESS;
}

// ----------------------------------------------------------------------------
// Element Operation Related.
// ----------------------------------------------------------------------------
int wdSubmit(WebElement* element) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->submit();
}

int wdClear(WebElement* element) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->clear();
}

int wdClick(WebElement* element) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->click();
}

int wdIsEnabled(WebElement* element, int* selected) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->isEnabled(selected);
}

int wdGetAttribute(WebElement* element, const wchar_t* name, StringWrapper** result) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  const std::wstring orgStr(element->element->getAttribute(name));
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdGetValueOfCssProperty(WebElement* element, const wchar_t* name, StringWrapper** result) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  const std::wstring orgStr(element->element->getValueOfCssProperty(name));
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdIsSelected(WebElement* element, int* selected) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->isSelected(selected);
}

int wdSetSelected(WebElement* element) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->setSelected();
}

int wdToggle(WebElement* element, int* toReturn) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->toggle(toReturn);
}

int wdSendKeys(WebElement* element, const wchar_t* keys) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->sendKeys(keys);
}

int wdIsDisplayed(WebElement* element, int* displayed) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->isDisplayed(displayed);
}

int wdGetText(WebElement* element, StringWrapper** result) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  const std::wstring orgStr(element->element->getText());
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdGetElementName(WebElement* element, StringWrapper** result) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  const std::wstring orgStr(element->element->getElementName());
  CopyToStringWrapper(orgStr, result);
  return SUCCESS;
}

int wdGetLocation(WebElement* element, long* x, long* y) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->getLocation(x, y);
}

int wdGetSize(WebElement* element, long* width, long* height) {
  if (!element || !element->element) {
    error("No element to operate on.");
    return !SUCCESS;
  }

  return element->element->getSize(width, height);
}

#ifdef __cplusplus
}
#endif  // __cplusplus
