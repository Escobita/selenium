#pragma once
#include <string>
#include "json.h"

// Forward declaration of classes to avoid
// circular include files.
class BrowserWrapper;

class ElementWrapper
{
public:
	ElementWrapper(IHTMLElement *element);
	virtual ~ElementWrapper(void);
	std::wstring m_elementId;
	IHTMLElement *m_pElement;
	Json::Value ConvertToJson(void);
	int GetLocationOnceScrolledIntoView(HWND hwnd, long *x, long *y, long *width, long *height);
	int GetAttributeValue(BrowserWrapper *pBrowser, std::wstring attributeName, std::wstring *attributeValue);
	int IsDisplayed(bool *result);
	bool IsEnabled(void);
	bool IsSelected(void);
	bool IsCheckBox(void);
	bool IsRadioButton(void);
	std::wstring GetText(void);
	int Click(HWND containingHwnd);
	int Hover(HWND containingHwnd);
	int DragBy(HWND containingHwnd, int offsetX, int offsetY);
	void FireEvent(IHTMLDOMNode* fireEventOn, LPCWSTR eventName);

private:
	int GetLocation(HWND hwnd, long* left, long* right, long* top, long* bottom);
	bool StyleIndicatesVisible(IHTMLElement* element);
	int StyleIndicatesDisplayed(IHTMLElement *element, bool* displayed);
	void ExtractElementText(std::wstring& toReturn, IHTMLDOMNode* node, bool isPreformatted);
	void CollapsingAppend(std::wstring& s, const std::wstring& s2);
	std::wstring CollapseWhitespace(CComBSTR& comtext);
	bool IsBlockLevel(IHTMLDOMNode *node);
	int IsNodeDisplayed(IHTMLDOMNode *node, bool* result);
	int IsElementDisplayed(IHTMLElement *element, bool* result);
};
