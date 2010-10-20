#pragma once
#include "BrowserManager.h"

class FindByNameElementFinder :
	public ElementFinder
{
public:

	FindByNameElementFinder(void)
	{
	}

	virtual ~FindByNameElementFinder(void)
	{
	}

protected:
	int FindByNameElementFinder::FindElementInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
	{
		CComQIPtr<IHTMLDOMNode> node(pParentElement);
		if (!node) 
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IHTMLDocument2> doc;
		this->extractHtmlDocument2FromDomNode(node, &doc);
		if (!doc) 
		{
			return ENOSUCHDOCUMENT;
		}

		CComPtr<IHTMLElementCollection> elementCollection;
		CComBSTR name(criteria.c_str());
		if (!SUCCEEDED(doc->get_all(&elementCollection)))
		{
			return ENOSUCHELEMENT;
		}
		
		long elementsLength;
		if (!SUCCEEDED(elementCollection->get_length(&elementsLength)))
		{
			return ENOSUCHELEMENT;
		}

		for (int i = 0; i < elementsLength; i++)
		{
			CComVariant idx;
			idx.vt = VT_I4;
			idx.lVal = i;
			CComVariant zero;
			zero.vt = VT_I4;
			zero.lVal = 0;
			CComPtr<IDispatch> dispatch;
			if (!SUCCEEDED(elementCollection->item(idx, zero, &dispatch)))
			{
				continue;
			}

			CComQIPtr<IHTMLElement> element(dispatch);
			CComBSTR nameText;
			CComVariant value;
			if (!element) {
				continue;
			}
			if (!SUCCEEDED(element->getAttribute(CComBSTR(L"name"), 0, &value)))
			{
				continue;
			}

			if (wcscmp(pBrowser->ConvertVariantToWString(&value).c_str(), criteria.c_str()) == 0 && this->isOrUnder(node, element)) 
			{
				element.CopyTo(ppElement);
				return SUCCESS;
			}
		}

		return ENOSUCHELEMENT;
	}

	int FindByNameElementFinder::FindElementsInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
	{
		CComQIPtr<IHTMLDOMNode> node(pParentElement);
		if (!node) 
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IHTMLDocument2> doc;
		this->extractHtmlDocument2FromDomNode(node, &doc);
		if (!doc) 
		{
			return ENOSUCHDOCUMENT;
		}

		CComPtr<IHTMLElementCollection> elementCollection;
		CComBSTR name(criteria.c_str());
		if (!SUCCEEDED(doc->get_all(&elementCollection)))
		{
			return ENOSUCHELEMENT;
		}
		
		long elementsLength;
		if (!SUCCEEDED(elementCollection->get_length(&elementsLength)))
		{
			return ENOSUCHELEMENT;
		}

		for (int i = 0; i < elementsLength; i++) {
			CComVariant idx;
			idx.vt = VT_I4;
			idx.lVal = i;
			CComVariant zero;
			zero.vt = VT_I4;
			zero.lVal = 0;
			CComPtr<IDispatch> dispatch;
			if (!SUCCEEDED(elementCollection->item(idx, zero, &dispatch)))
			{
				continue;
			}

			CComQIPtr<IHTMLElement> element(dispatch);
			if (!element)
			{
				continue;
			}

			CComBSTR nameText;
			CComVariant value;
			if (!SUCCEEDED(element->getAttribute(CComBSTR(L"name"), 0, &value)))
			{
				continue;
			}

			if (wcscmp(pBrowser->ConvertVariantToWString(&value).c_str(), criteria.c_str()) == 0 && this->isOrUnder(node, element)) 
			{
				IHTMLElement *pDom = NULL;
				element.CopyTo(&pDom);
				pElements->push_back(pDom);
			}
		}
		return SUCCESS;
	}
};
