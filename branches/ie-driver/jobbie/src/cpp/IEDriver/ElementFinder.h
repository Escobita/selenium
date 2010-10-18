#pragma once
#include <string>
#include <vector>

using namespace std;

// Forward declaration of classes to avoid
// circular include files.
class BrowserManager;

class ElementFinder
{
public:
	ElementFinder(void);
	virtual ~ElementFinder(void);
	int FindElement(BrowserManager *pManager, ElementWrapper *pParentWrapper, std::wstring criteria, ElementWrapper **ppFoundElement);
	int FindElements(BrowserManager *pManager, ElementWrapper *pParentWrapper, std::wstring criteria, std::vector<ElementWrapper*> *pFoundElements);

protected:
	virtual int FindElementInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement);
	virtual int FindElementsInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements);
	void getHtmlDocument3(BrowserManager *pManager, IHTMLDocument3 **ppDoc3);
	void extractHtmlDocument2FromDomNode(const IHTMLDOMNode* pExtractionNode, IHTMLDocument2** ppDoc);
	void extractHtmlDocument3FromDomNode(const IHTMLDOMNode* pExtractionNode, IHTMLDocument3** ppDoc);
	bool isOrUnder(const IHTMLDOMNode* root, IHTMLElement* child);
	bool isUnder(const IHTMLDOMNode* root, IHTMLElement* child);
	std::wstring convertVariantToWString(CComVariant toConvert);
	std::wstring StripTrailingWhitespace(std::wstring input);

private:
	int getParentElement(BrowserManager *pManager, ElementWrapper *pParentElementWrapper, IHTMLElement **ppParentElement);
};
