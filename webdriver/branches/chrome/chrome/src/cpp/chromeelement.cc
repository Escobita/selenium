#include "chromedriver.h"
#include "chromeelement.h"
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <wchar.h>

ChromeElement::~ChromeElement() {
}

int ChromeElement::submit() {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".submit()");
  return driver_->domGetVoid(jscript);
}

int ChromeElement::clear() {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".clear()");
  return driver_->domGetVoid(jscript);
}

int ChromeElement::click() {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".click()");
  return driver_->domGetVoid(jscript);
}

int ChromeElement::setSelected() {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".selected=true");
  return driver_->domGetVoid(jscript);
}

int ChromeElement::isEnabled(int* selected) {
  return SUCCESS;
}

int ChromeElement::isSelected(int* selected) {
  return SUCCESS;
}

int ChromeElement::isDisplayed(int* displayed) {
  return SUCCESS;
}

int ChromeElement::toggle(int* toReturn) {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".checked=true");
  return driver_->domGetVoid(jscript);
}

int ChromeElement::sendKeys(const std::wstring keys) {
  return SUCCESS;
}

std::wstring ChromeElement::getAttribute(const std::wstring name) {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L"['");
  jscript.append(name.c_str());
  jscript.append(L"']");
  return driver_->domGetString(jscript);
}

std::wstring ChromeElement::getValueOfCssProperty(const std::wstring name) {
  std::wstring jscript = base_script_.c_str();
  if (name == L"className") {
    jscript.append(L".className");
  } else {
    jscript.append(L".style.");
    jscript.append(name);
  }
  return driver_->domGetString(jscript);
}

std::wstring ChromeElement::getText() {
  return L"";
}

std::wstring ChromeElement::getElementName() {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".nodeName");
  return driver_->domGetString(jscript);
}

int ChromeElement::getLocation(long* left, long* top) {
  return SUCCESS;
}

int ChromeElement::getSize(long* width, long* height) {
  return SUCCESS;
}
