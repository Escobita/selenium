#pragma once
#include "BrowserManager.h"

class FindByPartialLinkTextElementFinder :
	public ElementFinder
{
public:

	FindByPartialLinkTextElementFinder(void)
	{
	}

	virtual ~FindByPartialLinkTextElementFinder(void)
	{
	}

protected:
	int FindByPartialLinkTextElementFinder::FindElementInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
	{
		CComQIPtr<IHTMLDOMNode> node(pParentElement);
		CComQIPtr<IHTMLElement2> element2(pParentElement);
		if (!element2 || !node)
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IHTMLElementCollection> elements;
		if (!SUCCEEDED(element2->getElementsByTagName(CComBSTR("A"), &elements)))
		{
			return ENOSUCHELEMENT;
		}
		
		long linksLength;
		if (!SUCCEEDED(elements->get_length(&linksLength)))
		{
			return ENOSUCHELEMENT;
		}

		for (int i = 0; i < linksLength; i++)
		{
			CComVariant idx;
			idx.vt = VT_I4;
			idx.lVal = i;
			CComVariant zero;
			zero.vt = VT_I4;
			zero.lVal = 0;
			CComPtr<IDispatch> dispatch;
			if (!SUCCEEDED(elements->item(idx, zero, &dispatch)))
			{
				// The page is probably reloading, but you never know. Continue looping
				continue;
			}

			CComQIPtr<IHTMLElement> element(dispatch);
			if (!element)
			{
				// Deeply unusual
				continue;
			}

			CComBSTR linkText;
			if (!SUCCEEDED(element->get_innerText(&linkText))) 
			{
				continue;
			}

			std::wstring linkTextString((BSTR)linkText);
			if (wcsstr(linkTextString.c_str(), criteria.c_str()) && this->isOrUnder(node, element)) 
			{
				element.CopyTo(ppElement);
				return SUCCESS;
			}
		}

		return ENOSUCHELEMENT;
	}

	int FindByPartialLinkTextElementFinder::FindElementsInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
	{
		CComQIPtr<IHTMLDOMNode> node(pParentElement);
		CComQIPtr<IHTMLElement2> element2(pParentElement);
		if (!element2 || !node)
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IHTMLElementCollection> elements;
		if (!SUCCEEDED(element2->getElementsByTagName(CComBSTR("A"), &elements)))
		{
			return ENOSUCHELEMENT;
		}
		
		long linksLength;
		if (!SUCCEEDED(elements->get_length(&linksLength)))
		{
			return ENOSUCHELEMENT;
		}

		for (int i = 0; i < linksLength; i++)
		{
			CComVariant idx;
			idx.vt = VT_I4;
			idx.lVal = i;
			CComVariant zero;
			zero.vt = VT_I4;
			zero.lVal = 0;
			CComPtr<IDispatch> dispatch;
			if (!SUCCEEDED(elements->item(idx, zero, &dispatch)))
			{
				return ENOSUCHELEMENT;
			}

			CComQIPtr<IHTMLElement> element(dispatch);
			if (!element)
			{
				continue;
			}

			CComBSTR linkText;
			element->get_innerText(&linkText);

			std::wstring linkTextString((BSTR)linkText);
			if (wcsstr(linkTextString.c_str(), criteria.c_str()) && this->isOrUnder(node, element)) 
			{
				IHTMLElement *pDom = NULL;
				element.CopyTo(&pDom);
				pElements->push_back(pDom);
			}
		}
		return SUCCESS;
	}
};
