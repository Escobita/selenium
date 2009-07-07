#include "chromedriver.h"
#include "chromeelement.h"
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <wchar.h>

ChromeElement::~ChromeElement() {
}

void ChromeElement::propagate(ChromeElement* element) {
	element->driver_ = this->driver_;
	element->base_script_ = this->base_script_;
	element->org_query_ = this->org_query_;
	element->query_type_ = this->query_type_;
	element->element_id_ = this->element_id_;
	element->element_type_ = this->element_type_;
}

int ChromeElement::submit() {
  std::wstring jscript;
  driver_->createSubmitScript(jscript, base_script_);
  return driver_->domGetVoid(jscript);
}

int ChromeElement::clear() {
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".clear()");
  return driver_->domGetVoid(jscript);
}

int ChromeElement::click() {
  std::wstring jscript;
  driver_->createClickScript(jscript, base_script_);
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
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".focus()");
  driver_->domGetVoid(jscript);

  driver_->dg(jscript.c_str());
  driver_->dg(keys.c_str());

  ::sendKeys((void *)&window_handle_, keys.c_str(), 0);
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
  std::wstring jscript = base_script_.c_str();
  jscript.append(L".innerText");
  return driver_->domGetString(jscript);
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
