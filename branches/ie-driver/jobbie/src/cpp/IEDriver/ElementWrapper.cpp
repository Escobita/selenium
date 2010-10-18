#include "StdAfx.h"
#include "ElementWrapper.h"
#include "ErrorCodes.h"

ElementWrapper::ElementWrapper(CComPtr<IHTMLElement> element)
{
	// NOTE: COM should be initialized on this thread, so we
	// could use CoCreateGuid() and StringFromGUID2() instead.
	UUID idGuid;
	RPC_WSTR pszUuid = NULL;
	::UuidCreate(&idGuid);
	::UuidToString(&idGuid, &pszUuid);

	// RPC_WSTR is currently typedef'd in RpcDce.h (pulled in by rpc.h)
	// as unsigned short*. It needs to be typedef'd as wchar_t* 
	wchar_t* pwStr = reinterpret_cast<wchar_t*>(pszUuid);
	this->m_elementId = pwStr;

	::RpcStringFree(&pszUuid);

	this->m_pElement = element;
}

ElementWrapper::~ElementWrapper(void)
{
}

Json::Value ElementWrapper::ConvertToJson()
{
	Json::Value jsonWrapper;
	std::string id(CW2A(this->m_elementId.c_str()));
	jsonWrapper["ELEMENT"] = id;
	return jsonWrapper;
}

int ElementWrapper::IsDisplayed(bool *result)
{
	CComQIPtr<IHTMLInputHiddenElement> hidden(this->m_pElement);
	if (hidden) {
		*result = false;
		return SUCCESS;
	}

	bool displayed;
	int value = this->StyleIndicatesDisplayed(this->m_pElement, &displayed);

	if (value != SUCCESS) {
		return value;
	}

	*result = displayed && this->StyleIndicatesVisible(this->m_pElement);
	return SUCCESS;
}

bool ElementWrapper::IsEnabled()
{
	CComQIPtr<IHTMLElement3> elem3(this->m_pElement);
	if (!elem3) {
		return false;
	}
	VARIANT_BOOL isDisabled;
	elem3->get_disabled(&isDisabled);
	return !isDisabled;
}

int ElementWrapper::GetLocationOnceScrolledIntoView(HWND hwnd, long *x, long *y, long *width, long *height)
{
    CComPtr<IHTMLDOMNode2> node;
	HRESULT hr = this->m_pElement->QueryInterface(&node);

    if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Cannot cast html element to node";
		return ENOSUCHELEMENT;
    }

    bool displayed;
	int result = this->IsDisplayed(&displayed);
	if (result != SUCCESS)
	{
		return result;
	} 

	if (!displayed)
	{
        return EELEMENTNOTDISPLAYED;
    }

    if (!this->IsEnabled())
	{
        return EELEMENTNOTENABLED;
    }

	long top, left, bottom, right = 0;
	result = this->GetLocation(hwnd, &left, &right, &top, &bottom);
	if (result != SUCCESS)
	{
		// Scroll the element into view
		//LOG(DEBUG) << "Will need to scroll element into view";
		HRESULT hr = this->m_pElement->scrollIntoView(CComVariant(VARIANT_TRUE));
		if (FAILED(hr))
		{
			// LOGHR(WARN, hr) << "Cannot scroll element into view";
			return EOBSOLETEELEMENT;
		}

		result = this->GetLocation(hwnd, &left, &right, &top, &bottom);
	}

	if (result != SUCCESS)
	{
		return result;
	}

	long elementWidth = right - left;
	long elementHeight = bottom - top;

    long clickX = left;
	long clickY = top;

	//LOG(DEBUG) << "(x, y, w, h): " << clickX << ", " << clickY << ", " << elementWidth << ", " << elementHeight << endl;

    if (elementHeight == 0 || elementWidth == 0) 
	{
        //LOG(DEBUG) << "Element would not be visible because it lacks height and/or width.";
        return EELEMENTNOTDISPLAYED;
    }

	// This is a little funky.
	//if (ieRelease > 7)
	//{
	//	clickX += 2;
	//	clickY += 2;
	//}

	*x = clickX;
	*y = clickY;
	*width = elementWidth;
	*height = elementHeight;


    CComPtr<IDispatch> ownerDocDispatch;
    hr = node->get_ownerDocument(&ownerDocDispatch);
	if (FAILED(hr))
	{
		//LOG(WARN) << "Unable to locate owning document";
		return ENOSUCHDOCUMENT;
	}
    CComQIPtr<IHTMLDocument3> ownerDoc(ownerDocDispatch);
	if (!ownerDoc)
	{
		//LOG(WARN) << "Found document but it's not the expected type";
		return ENOSUCHDOCUMENT;
	}

    CComPtr<IHTMLElement> docElement;
    hr = ownerDoc->get_documentElement(&docElement);
	if (FAILED(hr))
	{
		//LOG(WARN) << "Unable to locate document element";
		return ENOSUCHDOCUMENT;
	}

    CComQIPtr<IHTMLElement2> e2(docElement);
    if (!e2)
	{
        //LOG(WARN) << "Unable to get underlying html element from the document";
        return EUNHANDLEDERROR;
    }

    CComQIPtr<IHTMLDocument2> doc2(ownerDoc);
	if (!doc2)
	{
		//LOG(WARN) << "Have the owning document, but unable to process";
		return ENOSUCHDOCUMENT;
	}

    long clientLeft, clientTop;
    e2->get_clientLeft(&clientLeft);
    e2->get_clientTop(&clientTop);

    clickX += clientLeft;
    clickY += clientTop;

    // We now know the location of the element within its frame.
    // Where is the frame in relation to the HWND, though?
    // The ieWindow is the ultimate container, without chrome,
    // so if we know its location, we can subtract the screenLeft and screenTop
    // of the window.

    WINDOWINFO winInfo;
    GetWindowInfo(hwnd, &winInfo);
    clickX -= winInfo.rcWindow.left;
    clickY -= winInfo.rcWindow.top;

    CComPtr<IHTMLWindow2> win2;
    hr = doc2->get_parentWindow(&win2);
	if (FAILED(hr))
	{
		//LOG(WARN) << "Cannot obtain parent window";
		return ENOSUCHWINDOW;
	}
    CComQIPtr<IHTMLWindow3> win3(win2);
	if (!win3)
	{
		//LOG(WARN) << "Can't obtain parent window";
		return ENOSUCHWINDOW;
	}
    long screenLeft, screenTop;
    hr = win3->get_screenLeft(&screenLeft);
	if (FAILED(hr))
	{
		//LOG(WARN) << "Unable to determine left corner of window";
		return ENOSUCHWINDOW;
	}
    hr = win3->get_screenTop(&screenTop);
	if (FAILED(hr))
	{
		//LOG(WARN) << "Unable to determine top edge of window";
		return ENOSUCHWINDOW;
	}

    clickX += screenLeft;
    clickY += screenTop;

    *x = clickX;
    *y = clickY;
	return SUCCESS;
}

int ElementWrapper::StyleIndicatesDisplayed(IHTMLElement *element, bool* displayed) 
{
	CComQIPtr<IHTMLElement2> e2(element);
	if (!e2) {
		return EOBSOLETEELEMENT;
	}

	CComPtr<IHTMLCurrentStyle> style;
	CComBSTR display;

	e2->get_currentStyle(&style);
	if(!style)
	{
		return EOBSOLETEELEMENT;
	}
	style->get_display(&display);
	std::wstring displayValue = (BSTR)display;

	if (_wcsicmp(L"none", displayValue.c_str()) == 0) {
		*displayed = false;
		return SUCCESS;
	}

	CComPtr<IHTMLElement> parent;
	element->get_parentElement(&parent);

	if (!parent)
	{
		*displayed = true;
		return SUCCESS;
	}

	// Check that parent has style
	CComQIPtr<IHTMLElement2> parent2(parent);

	CComPtr<IHTMLCurrentStyle> parentStyle;
	parent2->get_currentStyle(&parentStyle);

	if (parentStyle)
	{
		return this->StyleIndicatesDisplayed(parent, displayed);
	}

	return SUCCESS;
}


bool ElementWrapper::StyleIndicatesVisible(IHTMLElement* element) 
{
	CComQIPtr<IHTMLElement2> e2(element);
	if (!e2)
	{
		return false;
	}
	CComPtr<IHTMLCurrentStyle> curr;
	CComBSTR visible;

	e2->get_currentStyle(&curr);
	if(!curr)
	{
		throw std::wstring(L"appears to manipulate obsolete DOM element.");
	}
	curr->get_visibility(&visible);

	std::wstring visibleValue = (BSTR)visible;

	int isVisible = _wcsicmp(L"hidden", visibleValue.c_str());
	if (isVisible == 0)
	{
		return false;
	}

	// If the style attribute was set on this class and contained visibility, then stop
	CComPtr<IHTMLStyle> style;
	element->get_style(&style);
	if (style)
	{
		CComBSTR visibleStyle;
		style->get_visibility(&visibleStyle);
		if (visibleStyle)
		{
			return true;  // because we'd have returned false earlier, otherwise
		}
	}

	CComPtr<IHTMLElement> parent;
	element->get_parentElement(&parent);
	if (parent)
	{
		return this->StyleIndicatesVisible(parent);
	}

	return true;
}

int ElementWrapper::GetLocation(HWND hwnd, long* left, long* right, long* top, long* bottom)
{
	*top, *left, *bottom, *right = 0;

	::Sleep(100);

	// getBoundingClientRect. Note, the docs talk about this possibly being off by 2,2
    // and Jon Resig mentions some problems too. For now, we'll hope for the best
    // http://ejohn.org/blog/getboundingclientrect-is-awesome/

    CComPtr<IHTMLElement2> element2;
	HRESULT hr = this->m_pElement->QueryInterface(&element2);
	if (FAILED(hr)) 
	{
		//LOGHR(WARN, hr) << "Unable to cast element to correct type";
		return EOBSOLETEELEMENT;
	}

    CComPtr<IHTMLRect> rect;
	hr = element2->getBoundingClientRect(&rect);
    if (FAILED(hr)) 
	{
		//LOGHR(WARN, hr) << "Cannot figure out where the element is on screen";
		return EUNHANDLEDERROR;
    }

	long t, b, l, r = 0;

    rect->get_top(&t);
    rect->get_left(&l);
	rect->get_bottom(&b);
    rect->get_right(&r);

	// On versions of IE prior to 8 on Vista, if the element is out of the 
	// viewport this would seem to return 0,0,0,0. IE 8 returns position in 
	// the DOM regardless of whether it's in the browser viewport.

	// Handle the easy case first: does the element have size
	long w = r - l;
	long h = b - t;
	if (w < 0 || h < 0) { return EELEMENTNOTDISPLAYED; }

	// The element has a location, but is it in the viewport?
	// Turns out that the dimensions given (at least on IE 8 on vista)
	// are relative to the view port so get the dimensions of the window
	WINDOWINFO winInfo;
	if (!::GetWindowInfo(hwnd, &winInfo))
	{
		//LOG(WARN) << "Cannot determine size of window";
		return EELEMENTNOTDISPLAYED;
	}
    long winWidth = winInfo.rcClient.right - winInfo.rcClient.left;
    long winHeight = winInfo.rcClient.bottom - winInfo.rcClient.top;

	// Hurrah! Now we know what the visible area of the viewport is
	// Is the element visible in the X axis?
	if (l < 0 || l > winWidth)
	{
		return EELEMENTNOTDISPLAYED;
	}

	// And in the Y?
	if (t < 0 || t > winHeight)
	{
		return EELEMENTNOTDISPLAYED;
	}

	// TODO(simon): we should clip the size returned to the viewport
	*left = l;
	*right = r;
	*top = t;
	*bottom = b;

	return SUCCESS;
}
