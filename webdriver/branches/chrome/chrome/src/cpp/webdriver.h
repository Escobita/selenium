#include <wchar.h>
#include <vector>

#ifndef EXPORT
#define EXPORT __declspec(dllexport)
#endif    // EXPORT definition.

#ifdef __cplusplus
extern "C" {
#endif

class ChromeDriver;
class ChromeElement;

typedef struct StringWrapper {
	wchar_t* text;
} StringWrapper;

typedef struct WebDriver {
  ChromeDriver* instance;
} WebDriver;

typedef struct WebElement {
	ChromeElement* element;
} WebElement;

typedef struct WebElementCollection {
  std::vector<ChromeElement*>* elements;
} WebElementCollection;

// ----------------------------------------------------------------------------
// String Related.
// ----------------------------------------------------------------------------
EXPORT int wdStringLength(StringWrapper* string, int* length);

EXPORT int wdFreeString(StringWrapper* string);

EXPORT int wdCopyString(StringWrapper* source, int length, wchar_t* dest);

// ----------------------------------------------------------------------------
// Driver Related.
// ----------------------------------------------------------------------------
EXPORT int wdNewDriverInstance(WebDriver** ptrDriver);

EXPORT int wdFreeDriverInstance(WebDriver* driver);

// ----------------------------------------------------------------------------
// Browser Related
// ----------------------------------------------------------------------------
EXPORT int wdGetVisible(WebDriver* driver, int* isVisible);

EXPORT int wdSetVisible(WebDriver* driver, int visible);

EXPORT int wdClose(WebDriver* driver);

// ----------------------------------------------------------------------------
// Page Related.
// ----------------------------------------------------------------------------
EXPORT int wdGet(WebDriver* driver, const wchar_t* url);

EXPORT int wdGoBack(WebDriver* driver);

EXPORT int wdGoForward(WebDriver* driver);

EXPORT int wdGetCurrentUrl(WebDriver* driver, StringWrapper** result);

EXPORT int wdGetTitle(WebDriver* driver, StringWrapper** result);

EXPORT int wdGetPageSource(WebDriver* driver, StringWrapper** result);

// ----------------------------------------------------------------------------
// Cookie Related.
// ----------------------------------------------------------------------------
EXPORT int wdGetCookies(WebDriver* driver, StringWrapper** result);

EXPORT int wdAddCookie(WebDriver* driver, const wchar_t* cookie);

// ----------------------------------------------------------------------------
// Element Related.
// ----------------------------------------------------------------------------
EXPORT int wdFreeElement(WebElement* element);

EXPORT int wdFindElementById(
    WebDriver* driver, const wchar_t* id, WebElement** element);

EXPORT int wdFindElementsById(
    WebDriver* driver, const wchar_t* id, WebElementCollection** collection);

EXPORT int wdFindElementByTagName(
    WebDriver* driver, const wchar_t* tag, WebElement** element);

EXPORT int wdFindElementsByTagName(
    WebDriver* driver, const wchar_t* tag, WebElementCollection** collection);

EXPORT int wdFindElementByClassName(
    WebDriver* driver, const wchar_t* cls, WebElement** element);

EXPORT int wdFindElementsByClassName(
    WebDriver* driver, const wchar_t* cls, WebElementCollection** collection);

EXPORT int wdFindElementByLinkText(
    WebDriver* driver, const wchar_t* text, WebElement** element);

EXPORT int wdFindElementsByLinkText(
    WebDriver* driver, const wchar_t* text, WebElementCollection** collection);

EXPORT int wdFindElementByPartialLinkText(
    WebDriver* driver, const wchar_t* pattern, WebElement** element);

EXPORT int wdFindElementsByPartialLinkText(
    WebDriver* driver, const wchar_t* pattern, WebElementCollection** collection);

EXPORT int wdFindElementByName(
    WebDriver* driver, const wchar_t* name, WebElement** element);

EXPORT int wdFindElementsByName(
    WebDriver* driver, const wchar_t* name, WebElementCollection** collection);

EXPORT int wdFindElementByXPath(
    WebDriver* driver, const wchar_t* xpath, WebElement** element);

EXPORT int wdFindElementsByXPath(
    WebDriver* driver, const wchar_t* xpath, WebElementCollection** collection);

// ----------------------------------------------------------------------------
// Element Collection Related.
// ----------------------------------------------------------------------------
EXPORT int wdFreeElementCollection(WebElementCollection* collection);

EXPORT int wdGetCollectionLength(WebElementCollection* collection, int* length);

EXPORT int wdGetElementAtIndex(
    WebElementCollection* collection, int index, WebElement** result);

// ----------------------------------------------------------------------------
// Element Operation Related.
// ----------------------------------------------------------------------------
EXPORT int wdSubmit(WebElement* element);

EXPORT int wdClear(WebElement* element);

EXPORT int wdClick(WebElement* element);

EXPORT int wdIsEnabled(WebElement* element, int* selected);

EXPORT int wdGetAttribute(WebElement* element, const wchar_t* string, StringWrapper** wrapper);

EXPORT int wdGetValueOfCssProperty(WebElement* element, const wchar_t* name, StringWrapper** wrapper);

EXPORT int wdIsSelected(WebElement* element, int* selected);

EXPORT int wdSetSelected(WebElement* element);

EXPORT int wdToggle(WebElement* element, int* toReturn);

EXPORT int wdSendKeys(WebElement* element, const wchar_t* string);

EXPORT int wdIsDisplayed(WebElement* element, int* displayed);

EXPORT int wdGetText(WebElement* element, StringWrapper** wrapper);

EXPORT int wdGetElementName(WebElement* element, StringWrapper** wrapper);

EXPORT int wdGetLocation(WebElement* element, long* x, long* y);

EXPORT int wdGetSize(WebElement* element, long* width, long* height);

#ifdef __cplusplus
}
#endif
