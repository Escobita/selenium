#pragma once
#include "BrowserManager.h"

class FindByTagNameElementFinder :
	public ElementFinder
{
public:

	FindByTagNameElementFinder(void)
	{
	}

	virtual ~FindByTagNameElementFinder(void)
	{
	}

protected:
	int FindElementInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
	{
		// Will use getElementsByTagName to get the elements,
		// so need the root document pointer for an IHTMLDocument3/
		BrowserWrapper *pBrowser;
		pManager->GetCurrentBrowser(&pBrowser);

		CComPtr<IHTMLDocument2> pDoc;
		pBrowser->GetDocument(&pDoc);

		CComPtr<IHTMLDocument3> root_doc;
		pDoc.QueryInterface<IHTMLDocument3>(&root_doc);
		if (!root_doc) 
		{
			return ENOSUCHDOCUMENT;
		}
		
		CComPtr<IHTMLElementCollection> elements;
		if (!SUCCEEDED(root_doc->getElementsByTagName(CComBSTR(criteria.c_str()), &elements)))
		{
			return ENOSUCHELEMENT;
		}

		if (!elements)
		{
			return ENOSUCHELEMENT;
		}

		long length;
		if (!SUCCEEDED(elements->get_length(&length))) 
		{
			return ENOSUCHELEMENT;
		}

		CComQIPtr<IHTMLDOMNode> node(pParentElement);

		for (int i = 0; i < length; i++)
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
				continue;
			}

			CComQIPtr<IHTMLElement> element(dispatch);
			if (!element)
			{
				element;
			}

			// Check to see if the element is contained return if it is
			if (this->isOrUnder(node, pParentElement))
			{
				element.CopyTo(ppElement);
				return SUCCESS;
			}
		}

		return ENOSUCHELEMENT;
	}

	int FindElementsInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
	{
		// Will use getElementsByTagName to get the elements,
		// so need the root document pointer for an IHTMLDocument3
		BrowserWrapper *pBrowser;
		pManager->GetCurrentBrowser(&pBrowser);

		CComPtr<IHTMLDocument2> pDoc;
		pBrowser->GetDocument(&pDoc);

		CComPtr<IHTMLDocument3> root_doc;
		pDoc.QueryInterface<IHTMLDocument3>(&root_doc);
		if (!root_doc) 
		{
			return ENOSUCHDOCUMENT;
		}
		
		CComPtr<IHTMLElementCollection> elements;
		if (!SUCCEEDED(root_doc->getElementsByTagName(CComBSTR(criteria.c_str()), &elements)))
		{
			return ENOSUCHELEMENT;
		}

		if (!elements)
		{
			return ENOSUCHELEMENT;
		}

		long length;
		if (!SUCCEEDED(elements->get_length(&length))) 
		{
			return ENOSUCHELEMENT;
		}

		CComQIPtr<IHTMLDOMNode> node(pParentElement);

		for (int i = 0; i < length; i++)
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
				continue;
			}

			CComQIPtr<IHTMLElement> element(dispatch);
			if (!element)
			{
				continue;
			}

			if (this->isUnder(node, element)) 
			{
				IHTMLElement *pDom = NULL;
				element.CopyTo(&pDom);
				pElements->push_back(pDom);
			}
		}

		return SUCCESS;
	}
};
