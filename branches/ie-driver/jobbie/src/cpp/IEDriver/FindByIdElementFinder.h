#pragma once
#include "BrowserManager.h"

class FindByIdElementFinder :
	public ElementFinder
{
public:

	FindByIdElementFinder(void)
	{
	}

	virtual ~FindByIdElementFinder(void)
	{
	}

protected:
	int FindElementInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
	{
		CComQIPtr<IHTMLDOMNode> node(pParentElement);
		if (!node) 
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IHTMLDocument3> doc;
		this->extractHtmlDocument3FromDomNode(node, &doc);

		if (!doc) 
		{
			return ENOSUCHDOCUMENT;
		}
	 
		CComPtr<IHTMLElement> element;
		CComBSTR id(criteria.c_str());
		if (!SUCCEEDED(doc->getElementById(id, &element)))
		{
			return ENOSUCHELEMENT;
		}

		if(NULL == element)
		{
			return ENOSUCHELEMENT;
		}
		
		CComVariant value;
		if (!SUCCEEDED(element->getAttribute(CComBSTR(L"id"), 0, &value)))
		{
			return ENOSUCHELEMENT;
		}

		if (wcscmp(pBrowser->ConvertVariantToWString(&value).c_str(), criteria.c_str()) == 0) 
		{
			if (this->isOrUnder(node, element))
			{
				element.CopyTo(ppElement);
				return SUCCESS;
			}
		}

		CComQIPtr<IHTMLDocument2> doc2(doc);
		if (!doc2) 
		{
			return ENOSUCHDOCUMENT;
		}

		CComPtr<IHTMLElementCollection> allNodes;
		if (!SUCCEEDED(doc2->get_all(&allNodes)))
		{
			return ENOSUCHELEMENT;
		}

		long length = 0;
		CComPtr<IUnknown> unknown;
		if (!SUCCEEDED(allNodes->get__newEnum(&unknown)))
		{
			return ENOSUCHELEMENT;
		}

		CComQIPtr<IEnumVARIANT> enumerator(unknown);
		if (!enumerator) 
		{
			return ENOSUCHELEMENT;
		}

		CComVariant var;
		if (!SUCCEEDED(enumerator->Next(1, &var, NULL)))
		{
			return ENOSUCHELEMENT;
		}

		for (CComPtr<IDispatch> disp;
			 disp = V_DISPATCH(&var); 
			 enumerator->Next(1, &var, NULL)) 
		{
			CComQIPtr<IHTMLElement> curr(disp);
			if (curr)
			{
				CComVariant value;
				if (!SUCCEEDED(curr->getAttribute(CComBSTR(L"id"), 0, &value))) 
				{
					continue;
				}
				if (wcscmp(pBrowser->ConvertVariantToWString(&value).c_str(), criteria.c_str())==0) 
				{
					if (this->isOrUnder(node, curr)) 
					{
						curr.CopyTo(ppElement);
						return SUCCESS;
					}
				}
			}
		}	

		return ENOSUCHELEMENT;
	}

	int FindElementsInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
	{
		CComQIPtr<IHTMLDOMNode> node(pParentElement);
		if (!node) 
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IHTMLDocument2> doc2;
		this->extractHtmlDocument2FromDomNode(node, &doc2);

		if (!doc2) 
		{
			return ENOSUCHDOCUMENT;
		}

		CComPtr<IHTMLElementCollection> allNodes;
		if (!SUCCEEDED(doc2->get_all(&allNodes)))
		{
			return ENOSUCHELEMENT;
		}

		CComPtr<IUnknown> unknown;
		if (!SUCCEEDED(allNodes->get__newEnum(&unknown)))
		{
			return ENOSUCHELEMENT;
		}

		CComQIPtr<IEnumVARIANT> enumerator(unknown);
		if (!enumerator)
		{
			return ENOSUCHELEMENT;
		}

		CComVariant var;
		enumerator->Next(1, &var, NULL);

		for (CComPtr<IDispatch> disp;
			 disp = V_DISPATCH(&var); 
			 enumerator->Next(1, &var, NULL)) 
		{ 
			// We are iterating through all the DOM elements
			CComQIPtr<IHTMLElement> curr(disp);
			if (!curr) 
			{
				continue;
			}

			CComVariant value;
			if (!SUCCEEDED(curr->getAttribute(CComBSTR(L"id"), 0, &value)))
			{
				continue;
			}

			if (wcscmp(pBrowser->ConvertVariantToWString(&value).c_str(), criteria.c_str()) == 0 && this->isOrUnder(node, curr)) 
			{
				IHTMLElement *pDom = NULL;
				curr.CopyTo(&pDom);
				pElements->push_back(pDom);
			}
		}

		return SUCCESS;
	}
};
