#include "webdriver.h"

#ifdef __cplusplus
extern "C" {
#endif  // __cplusplus

// Driver related implemetation.
int wdNewDriverInstance(WebDriver** ptrDriver) {
  if (*ptrDriver)
    return !SUCCESS;

  // Allocate instances
  (*ptrDriver = new WebDriver())->instance = new ChromeDriver();

  // Launch the browser
  if ((*ptrDriver)->instance->Launch() == SUCCESS)
    return SUCCESS;

  // Clean up and fail
  delete (*ptrDriver)->instance;
  (*ptrDriver)->instance = NULL;
  delete *ptrDriver;
  *ptrDriver = NULL;
  return !SUCCESS;
}

int wdClose(WebDriver* driver) {
  if (driver)
     return driver->instance->close();
  return !SUCCESS;
}

int wdFreeDriverInstance(WebDriver* driver) {
  if (driver) {
    delete driver->instance;
    delete driver;
    return SUCCESS;
  }

  return !SUCCESS;
}

// String related implementation.
int wdStringLength(StringWrapper* string, int* length) {
  if (!string) {
    cerr << "No string to get length of" << endl;
    *length = -1;
    return -1;
  }
  if (!string->text) {
    cerr << "No underlying string to get length of" << endl;
    *length = -1;
    return -2;
  }
  size_t len = wcslen(string->text);
  *length = (int) len + 1;

  return SUCCESS;
}

int wdFreeString(StringWrapper* string) {
  if (!string) {
    return  -ENOSTRING;
  }

  if (string->text) delete[] string->text;
  delete string;

  return SUCCESS;
}

int wdCopyString(StringWrapper* source, int size, wchar_t* dest) {
  if (source && source->text) {
    wcscpy_s(dest, size, source->text);
    return SUCCESS;
  }

  //cerr << "Invalid source wrapper" << endl;
  LOG(INFO) << "Invalid source wrapper" << endl;
  return -ENOSTRING;
}

int wdGetVisible(WebDriver* driver, int* visible) {
  if (driver) {
    *visible = driver->instance->getVisible();
    return SUCCESS;
  }
  return !SUCCESS;
}

int wdSetVisible(WebDriver* driver, int value) {
  if (driver && !!driver->instance->setVisible(value))
    return SUCCESS;
  return !SUCCESS;
}

int wdGet(WebDriver* driver, const wchar_t* url) {
  if (driver)
    return driver->instance->get(url);
  return !SUCCESS;
}

int wdGoBack(WebDriver* driver) {
  if (driver)
    return driver->instance->back();
  return !SUCCESS;
}

int wdGoForward(WebDriver* driver) {
  if (driver)
    return driver->instance->forward();
  return !SUCCESS;
}

int wdGetCurrentUrl(WebDriver* driver, StringWrapper** result) {
  if (driver) {
    const std::wstring originalString(driver->instance->getCurrentUrl());

    size_t length = originalString.length() + 1;
    wchar_t* toReturn = new wchar_t[length];
    wcscpy_s(toReturn, length, originalString.c_str());

    *result = new StringWrapper();
    (*result)->text = toReturn;
    return SUCCESS;
  }
  return !SUCCESS;
}

int wdGetTitle(WebDriver* driver, StringWrapper** result) {
  if (driver) {
    const std::wstring originalString(driver->instance->getTitle());

    size_t length = originalString.length() + 1;
    wchar_t* toReturn = new wchar_t[length];
    wcscpy_s(toReturn, length, originalString.c_str());

    *result = new StringWrapper();
    (*result)->text = toReturn;
    return SUCCESS;
  }
  return !SUCCESS;
}

int wdGetPageSource(WebDriver* driver, StringWrapper** result) {
  if (driver) {
    const std::wstring originalString(driver->instance->getPageSource());

    size_t length = originalString.length() + 1;
    wchar_t* toReturn = new wchar_t[length];
    wcscpy_s(toReturn, length, originalString.c_str());

    *result = new StringWrapper();
    (*result)->text = toReturn;
    return SUCCESS;
  }
  return !SUCCESS;
}

// Cookies related
int wdGetCookies(WebDriver* driver, StringWrapper** result) {
  if (driver) {
    const std::wstring originalString(driver->instance->getCookies());

    size_t length = originalString.length() + 1;
    wchar_t* toReturn = new wchar_t[length];
    wcscpy_s(toReturn, length, originalString.c_str());

    *result = new StringWrapper();
    (*result)->text = toReturn;
    return SUCCESS;
  }
  return !SUCCESS;
}

int wdAddCookie(WebDriver* driver, const char* cookie) {
  if (driver)
    return driver->instance->addCookie(cookie);
  return !SUCCESS;
}

int wdDomOperReturnString(WebDriver* driver, const wchar_t* operation, StringWrapper** result) {
  if (driver) {
    const std::wstring originalString(driver->instance->domGetString(operation));

    size_t length = originalString.length() + 1;
    wchar_t* toReturn = new wchar_t[length];
    wcscpy_s(toReturn, length, originalString.c_str());

    *result = new StringWrapper();
    (*result)->text = toReturn;
    return SUCCESS;
  }
  return !SUCCESS;
}

int wdDomOperReturnInteger(WebDriver* driver, const wchar_t* operation, int* wrapper) {
  if (driver)
    return driver->instance->domGetInteger(operation, wrapper);
  return !SUCCESS;
}

int wdDomOperReturnBoolean(WebDriver* driver, const wchar_t* operation, int* wrapper) {
  if (driver) {
    bool result;
    driver->instance->domGetBoolean(operation, &result);
    *wrapper = (result ? 1 : 0);
    return SUCCESS;
  }

  return !SUCCESS;
}

int wdDomOperReturnPoint(WebDriver* driver, const wchar_t* opX, const wchar_t* opY, int* x, int *y) {
  if (driver) {
    driver->instance->domGetInteger(opX, x);
    driver->instance->domGetInteger(opY, y);
    return SUCCESS;
  }

  return !SUCCESS;
}

int wdDomOperSetValue(WebDriver* driver, const wchar_t* operation, const wchar_t* value) {
  if (driver) {
    return driver->instance->domSetter(operation, value);
  }

  return !SUCCESS;
}

int wdDomOperExecute(WebDriver* driver, const wchar_t* operation) {
  if (driver)
    return driver->instance->domExecute(operation);
  return !SUCCESS;
}

#ifdef __cplusplus
}
#endif  // __cplusplus
