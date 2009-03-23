#include <stdio.h>
#include <stdlib.h>
#include <wchar.h>
#include <windows.h>

#ifndef CHROME_ELEMENT_H_
#define CHROME_ELEMENT_H_

class ChromeDriver;

class ChromeElement {
  public:
    enum QueryType {
      BY_ID = 0,
      BY_TAG = 1,
      BY_NAME = 3,
      BY_CLASSNAME = 4,
      BY_XPATH = 5,
      BY_LINK_TEXT = 6,
      BY_LINK_PARTIAL_TEXT = 7
    };

    ChromeElement(ChromeDriver* driver, QueryType type, const std::wstring query) :
      driver_(driver), query_type_(type), org_query_(query), base_script_(L""),
      element_id_(L""), element_type_(L"") {}

    ~ChromeElement();

    int submit();

    int clear();

    int click();

    int setSelected();

    int isEnabled(int* selected);

    int isSelected(int* selected);

    int isDisplayed(int* displayed);

    int toggle(int* toReturn);

    int sendKeys(const std::wstring keys);

    std::wstring getAttribute(const std::wstring name);

    std::wstring getValueOfCssProperty(const std::wstring name);

    std::wstring getText();

    std::wstring getElementName();

    int getLocation(long* left, long* top);

    int getSize(long* width, long* height);

  protected:
    friend class ChromeDriver;
    void setElementIdentifier(
        const std::wstring script, const std::wstring id, const std::wstring type) {
      base_script_ = script;
      element_id_ = id;
      element_type_ = type;
    }

  private:
    ChromeDriver* driver_;
    std::wstring base_script_;
    std::wstring element_id_;
    std::wstring element_type_;
    std::wstring org_query_;
    int query_type_;
};

#endif  // CHROME_ELEMENT_H_
