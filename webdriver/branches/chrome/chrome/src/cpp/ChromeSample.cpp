// ChromeSample.cpp : Defines the entry point for the console application.

#include "webdriver.h"

static void error(const wchar_t* error) {
  wprintf(error);
  exit(1);
}

int main(int argc, wchar_t* argv[]) {
  wprintf(L"foo bar");
  WebDriver* driver = NULL;
  if (wdNewDriverInstance(&driver))
    error(L"failed to launch browser\n");
  if (wdGet(driver, L"http://www.google.com"))
    error(L"failed to navigate\n");
  ::Sleep(5000);
  wdClose(driver);
  wdFreeDriverInstance(driver);
  return 0;
}
