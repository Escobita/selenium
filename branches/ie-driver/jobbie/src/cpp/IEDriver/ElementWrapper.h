#pragma once
#include <string>
#include "json.h"

class ElementWrapper
{
public:
	ElementWrapper(CComPtr<IHTMLElement> element);
	virtual ~ElementWrapper(void);
	std::wstring m_elementId;
	CComPtr<IHTMLElement> m_pElement;
	Json::Value ConvertToJson(void);
	int GetLocationOnceScrolledIntoView(HWND hwnd, long *x, long *y, long *width, long *height);
	int IsDisplayed(bool *result);
	bool IsEnabled();
	int GetLocation(HWND hwnd, long* left, long* right, long* top, long* bottom);

private:
	bool StyleIndicatesVisible(IHTMLElement* element);
	int StyleIndicatesDisplayed(IHTMLElement *element, bool* displayed);

};
