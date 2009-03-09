#ifndef WEB_DRIVER_H_
#define WEB_DRIVER_H_

#include <wchar.h>
#include "chromedriver.h"

#pragma once
#define EXPORT __declspec(dllexport)

#ifdef __cplusplus
extern "C" {
#endif

typedef struct WebDriver WebDriver;
struct WebDriver {
  ChromeDriver *instance;
};

typedef struct StringWrapper StringWrapper;
struct StringWrapper {
  wchar_t *text;
};

typedef struct WebElement WebElement;
struct WebElement {
  wchar_t* element;
  //ElementWrapper *element;
};

// ----------------------------------------------------------------------------
// Driver Related.
// ----------------------------------------------------------------------------
EXPORT int wdNewDriverInstance(WebDriver** ptrDriver);
EXPORT int wdClose(WebDriver* driver);
EXPORT int wdFreeDriverInstance(WebDriver* driver);

// ----------------------------------------------------------------------------
// String related Related.
// ----------------------------------------------------------------------------
EXPORT int wdStringLength(StringWrapper* string, int* length);
EXPORT int wdFreeString(StringWrapper* string);
EXPORT int wdCopyString(StringWrapper* source, int length, wchar_t* dest);


// ----------------------------------------------------------------------------
// Automation Related.
// ----------------------------------------------------------------------------
// Browser related
EXPORT int wdGetVisible(WebDriver* driver, int* isVisible);
EXPORT int wdSetVisible(WebDriver* driver, int visible);

// Page Related.
EXPORT int wdGet(WebDriver* driver, const wchar_t* url);
EXPORT int wdGoBack(WebDriver* driver);
EXPORT int wdGoForward(WebDriver* driver);
EXPORT int wdGetCurrentUrl(WebDriver* driver, StringWrapper** result);
EXPORT int wdGetTitle(WebDriver* driver, StringWrapper** result);
EXPORT int wdGetPageSource(WebDriver* driver, StringWrapper** result);

// Cookie related.
EXPORT int wdGetCookies(WebDriver* driver, StringWrapper** result);
EXPORT int wdAddCookie(WebDriver* driver, const char* cookie);

// DOM Related.
EXPORT int wdDomOperReturnString(WebDriver* driver, const wchar_t* operation, StringWrapper** wrapper);
EXPORT int wdDomOperReturnInteger(WebDriver* driver, const wchar_t* operation, int* wrapper);
EXPORT int wdDomOperReturnBoolean(WebDriver* driver, const wchar_t* operation, int* wrapper);
EXPORT int wdDomOperReturnPoint(WebDriver* driver, const wchar_t* opX, const wchar_t* opY, int* x, int *y);
EXPORT int wdDomOperSetValue(WebDriver* driver, const wchar_t* operation, const wchar_t* value);
EXPORT int wdDomOperExecute(WebDriver* driver, const wchar_t* operation);

// Element Collection Related.
//EXPORT int wdcGetCollectionLength(ElementCollection* collection, int* length);
//EXPORT int wdcGetElementAtIndex(ElementCollection* collection, int index, WebElement** result);

#ifdef __cplusplus
}
#endif

#endif  // WEB_DRIVER_H_
