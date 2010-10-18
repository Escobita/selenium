#pragma once
#include "BrowserManager.h"

class FindByClassNameElementFinder :
	public ElementFinder
{
public:

	FindByClassNameElementFinder(void)
	{
	}

	virtual ~FindByClassNameElementFinder(void)
	{
	}
	int FindElementInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
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
		CComBSTR nameRead;
		if (!SUCCEEDED(enumerator->Next(1, &var, NULL)))
		{
			return ENOSUCHELEMENT;
		}

		const int exactLength = (int) wcslen(criteria.c_str());
		wchar_t *next_token, seps[] = L" ";

		for (CComPtr<IDispatch> disp;
			 disp = V_DISPATCH(&var); 
			 enumerator->Next(1, &var, NULL)) 
		{
			// We are iterating through all the DOM elements
			CComQIPtr<IHTMLElement> curr(disp);
			if (!curr) continue;

			curr->get_className(&nameRead);
			if(!nameRead) continue;

			std::wstring className;
			className = this->StripTrailingWhitespace((BSTR)nameRead);

			for ( wchar_t *token = wcstok_s(&className[0], seps, &next_token);
				  token;
				  token = wcstok_s( NULL, seps, &next_token) )
			{
				__int64 lengthRead = next_token - token;
				if(*next_token!=NULL) lengthRead--;
				if(exactLength != lengthRead) continue;
				if(0 != wcscmp(criteria.c_str(), token)) continue;
				if(!isOrUnder(node, curr)) continue;
				// Woohoo, we found it
				curr.CopyTo(ppElement);
				return SUCCESS;
			}
		}

		return ENOSUCHELEMENT;
	}

	int FindElementsInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
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
		if (!enumerator) {
			return ENOSUCHELEMENT;
		}

		CComVariant var;
		CComBSTR nameRead;
		if (!SUCCEEDED(enumerator->Next(1, &var, NULL)))
		{
		}

		const int exactLength = (int) wcslen(criteria.c_str());
		wchar_t *next_token, seps[] = L" ";

		for (CComPtr<IDispatch> disp;
			 disp = V_DISPATCH(&var); 
			 enumerator->Next(1, &var, NULL)) 
		{
			// We are iterating through all the DOM elements
			CComQIPtr<IHTMLElement> curr(disp);
			if (!curr) continue;

			curr->get_className(&nameRead);
			if(!nameRead) continue;

			std::wstring className;
			className = this->StripTrailingWhitespace((BSTR)nameRead);

			for ( wchar_t *token = wcstok_s(&className[0], seps, &next_token);
				  token;
				  token = wcstok_s( NULL, seps, &next_token) )
			{
				__int64 lengthRead = next_token - token;
				if(*next_token!=NULL) lengthRead--;
				if(exactLength != lengthRead) continue;
				if(0 != wcscmp(criteria.c_str(), token)) continue;
				if(!isOrUnder(node, curr)) continue;
				// Woohoo, we found it
				IHTMLElement *pDom = NULL;
				curr.CopyTo(&pDom);
				pElements->push_back(pDom);
			}
		}
		return SUCCESS;
	}
};
