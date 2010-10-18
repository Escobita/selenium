#include "StdAfx.h"
#include "BrowserManager.h"

ElementFinder::ElementFinder(void)
{
}

ElementFinder::~ElementFinder(void)
{
}

int ElementFinder::FindElement(BrowserManager *pManager, ElementWrapper *pParentWrapper, std::wstring criteria, ElementWrapper **ppFoundElement)
{
	int statusCode(SUCCESS);
	CComPtr<IHTMLElement> pParentElement;
	statusCode = this->getParentElement(pManager, pParentWrapper, &pParentElement);
	if (statusCode == SUCCESS)
	{
		CComPtr<IHTMLElement> pElement;
		statusCode = this->FindElementInternal(pManager, pParentElement, criteria, &pElement);
		if (statusCode == SUCCESS)
		{
			ElementWrapper *wrapper = new ElementWrapper(pElement);
			pManager->m_knownElements[wrapper->m_elementId] = wrapper;
			*ppFoundElement = wrapper;
		}
	}
	return statusCode;
}

int ElementFinder::FindElements(BrowserManager *pManager, ElementWrapper *pParentWrapper, std::wstring criteria, std::vector<ElementWrapper*> *pFoundElements)
{
	int statusCode(SUCCESS);
	CComPtr<IHTMLElement> pParentElement;
	statusCode = this->getParentElement(pManager, pParentWrapper, &pParentElement);
	if (statusCode == SUCCESS)
	{
		std::vector<IHTMLElement*> rawElements;
		statusCode = this->FindElementsInternal(pManager, pParentElement, criteria, &rawElements);
		std::vector<IHTMLElement*>::iterator begin = rawElements.begin();
		std::vector<IHTMLElement*>::iterator end = rawElements.end();
		for (std::vector<IHTMLElement*>::iterator it = begin; it != end; ++it)
		{
			ElementWrapper *wrapper = new ElementWrapper(*it);
			pManager->m_knownElements[wrapper->m_elementId] = wrapper;
			pFoundElements->push_back(wrapper);
		}
	}
	return statusCode;
}

int ElementFinder::FindElementInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
{
	return ENOSUCHELEMENT;
}

int ElementFinder::FindElementsInternal(BrowserManager *pManager, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
{
	return ENOSUCHELEMENT;
}

void ElementFinder::getHtmlDocument3(BrowserManager *pManager, IHTMLDocument3 **ppDoc3)
{
	BrowserWrapper *pBrowser;
	pManager->GetCurrentBrowser(&pBrowser);
	
	CComPtr<IHTMLDocument2> pDoc;
	pBrowser->GetDocument(&pDoc);

	CComQIPtr<IHTMLDocument3> pQIDoc3(pDoc);
	if (pQIDoc3)
	{
		*ppDoc3 = pQIDoc3.Detach();
	}
}

void ElementFinder::extractHtmlDocument3FromDomNode(const IHTMLDOMNode* pExtractionNode, IHTMLDocument3** ppDoc)
{
	CComQIPtr<IHTMLDOMNode2> element(const_cast<IHTMLDOMNode*>(pExtractionNode));

	CComPtr<IDispatch> dispatch;
	element->get_ownerDocument(&dispatch);

	CComQIPtr<IHTMLDocument3> doc(dispatch);
	*ppDoc = doc.Detach();
}

void ElementFinder::extractHtmlDocument2FromDomNode(const IHTMLDOMNode* pExtractionNode, IHTMLDocument2** ppDoc)
{
	CComQIPtr<IHTMLDOMNode2> element(const_cast<IHTMLDOMNode*>(pExtractionNode));

	CComPtr<IDispatch> dispatch;
	element->get_ownerDocument(&dispatch);

	CComQIPtr<IHTMLDocument2> doc(dispatch);
	*ppDoc = doc.Detach();
}

int ElementFinder::getParentElement(BrowserManager *pManager, ElementWrapper *pParentWrapper, IHTMLElement **ppParentElement)
{
	int statusCode(SUCCESS);
	if (pParentWrapper != NULL)
	{
		*ppParentElement = pParentWrapper->m_pElement;
	}
	else
	{
		// No parent element specified, so get the root document
		// element as the parent element.
		CComPtr<IHTMLDocument3> pRootDoc;
		this->getHtmlDocument3(pManager, &pRootDoc);
		if (!pRootDoc) 
		{
			statusCode = ENOSUCHDOCUMENT;
		}
		else
		{
			pRootDoc->get_documentElement(ppParentElement);
		}
	}

	return statusCode;
}

bool ElementFinder::isOrUnder(const IHTMLDOMNode* root, IHTMLElement* child) 
{
	CComQIPtr<IHTMLElement> parent(const_cast<IHTMLDOMNode*>(root));

	if (!parent)
	{
		return true;
	}

	VARIANT_BOOL toReturn;
	HRESULT hr = parent->contains(child, &toReturn);
	if (FAILED(hr))
	{
		// LOGHR(WARN, hr) << "Cannot determine if parent contains child node";
		return false;
	}

	return toReturn == VARIANT_TRUE;
}

bool ElementFinder::isUnder(const IHTMLDOMNode* root, IHTMLElement* child)
{
	CComQIPtr<IHTMLDOMNode> childNode(child);
	return isOrUnder(root, child) && root != childNode;
}

std::wstring ElementFinder::convertVariantToWString(CComVariant toConvert) 
{
	VARTYPE type = toConvert.vt;
	std::wstringstream toReturn;

	switch(type) 
	{
		case VT_BOOL:
			toReturn << (toConvert.boolVal == VARIANT_TRUE ? L"true" : L"false");
			break;

		case VT_BSTR:
			toReturn << (LPCWSTR)toConvert.bstrVal;
			break;

		case VT_I4:
			toReturn << toConvert.lVal;
			break;

		case VT_EMPTY:
			break;

		case VT_NULL:
			// TODO(shs96c): This should really return NULL.
			break;

		// This is lame
		case VT_DISPATCH:
			break;
	}

	return toReturn.str();
}

std::wstring ElementFinder::StripTrailingWhitespace(std::wstring input)
{
	// TODO: make the whitespace finder more comprehensive.
	std::wstring whitespace = L" \t\n\f\v\r";
	if (input.length() == 0)
	{
		return input; 
	}

	size_t pos = input.find_last_not_of(whitespace); 
	if ((pos + 1) == input.length() || pos == std::string::npos)
	{
		return input; 
	}

	return input.substr(0, (pos + 1)); 
}