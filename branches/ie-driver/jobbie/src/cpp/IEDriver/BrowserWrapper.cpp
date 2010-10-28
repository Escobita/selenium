#include "StdAfx.h"
#include "BrowserWrapper.h"
#include <comutil.h>

BrowserWrapper::BrowserWrapper(CComPtr<IWebBrowser2> browser, HWND hwnd, BrowserFactory *factory)
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
	this->m_browserId = pwStr;

	::RpcStringFree(&pszUuid);

	this->m_waitRequired = false;
	this->m_navStarted = false;
	this->m_factory = factory;
	this->m_hwnd = hwnd;
	this->m_pBrowser = browser;
	this->attachEvents();
}

BrowserWrapper::~BrowserWrapper(void)
{
}

void BrowserWrapper::GetDocument(IHTMLDocument2 **ppDoc)
{
	CComPtr<IHTMLWindow2> window;
	this->findCurrentFrameWindow(&window);

	if (window) 
	{
		HRESULT hr = window->get_document(ppDoc);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Cannot get document";
		}
	}
}

int BrowserWrapper::ExecuteScript(const std::wstring *script, SAFEARRAY *args, VARIANT *result)
{
	VariantClear(result);

	CComPtr<IHTMLDocument2> doc;
	this->GetDocument(&doc);
	if (!doc)
	{
		// LOG(WARN) << "Unable to get document reference";
		return EUNEXPECTEDJSERROR;
	}

	CComPtr<IDispatch> scriptEngine;
	HRESULT hr = doc->get_Script(&scriptEngine);
	if (FAILED(hr))
	{
		// LOGHR(WARN, hr) << "Cannot obtain script engine";
		return EUNEXPECTEDJSERROR;
	}

	DISPID evalId;
	bool added;
	bool ok = this->getEvalMethod(doc, &evalId, &added);

	if (!ok)
	{
		// LOG(WARN) << "Unable to locate eval method";
		if (added)
		{ 
			removeScript(doc); 
		}
		return EUNEXPECTEDJSERROR;
	}

	CComVariant tempFunction;
	if (!createAnonymousFunction(scriptEngine, evalId, script, &tempFunction))
	{
		// Debug level since this is normally the point we find out that 
		// a page refresh has occured. *sigh*
		//LOG(DEBUG) << "Cannot create anonymous function: " << _bstr_t(script) << endl;
		if (added)
		{ 
			removeScript(doc); 
		}
		return EUNEXPECTEDJSERROR;
	}

	if (tempFunction.vt != VT_DISPATCH)
	{
		// No return value that we care about
		VariantClear(result);
		result->vt = VT_EMPTY;
		if (added)
		{ 
			removeScript(doc); 
		}
		return SUCCESS;
	}

	// Grab the "call" method out of the returned function
	DISPID callid;
	OLECHAR FAR* szCallMember = L"call";
	hr = tempFunction.pdispVal->GetIDsOfNames(IID_NULL, &szCallMember, 1, LOCALE_USER_DEFAULT, &callid);
	if (FAILED(hr))
	{
		if (added) 
		{ 
			removeScript(doc); 
		}
		//LOGHR(DEBUG, hr) << "Cannot locate call method on anonymous function: " << _bstr_t(script) << endl;
		return EUNEXPECTEDJSERROR;
	}

	DISPPARAMS callParameters = { 0 };
	memset(&callParameters, 0, sizeof callParameters);

	long lower = 0;
	SafeArrayGetLBound(args, 1, &lower);
	long upper = 0;
	SafeArrayGetUBound(args, 1, &upper);
	long nargs = 1 + upper - lower;
	callParameters.cArgs = nargs + 1;

	CComPtr<IHTMLWindow2> win;
	hr = doc->get_parentWindow(&win);
	if (FAILED(hr))
	{
		if (added) 
		{ 
			removeScript(doc); 
		}
		//LOGHR(WARN, hr) << "Cannot get parent window";
		return EUNEXPECTEDJSERROR;
	}
	_variant_t *vargs = new _variant_t[nargs + 1];
	VariantCopy(&(vargs[nargs]), &CComVariant(win));

	long index;
	for (int i = 0; i < nargs; i++)
	{
		index = i;
		CComVariant v;
		SafeArrayGetElement(args, &index, (void*) &v);
		VariantCopy(&(vargs[nargs - 1 - i]), &v);
	}

	callParameters.rgvarg = vargs;

	EXCEPINFO exception;
	memset(&exception, 0, sizeof exception);
	hr = tempFunction.pdispVal->Invoke(callid, IID_NULL, LOCALE_USER_DEFAULT, DISPATCH_METHOD, &callParameters, 
		result,
		&exception, 0);
	if (FAILED(hr))
	{
		CComBSTR errorDescription(exception.bstrDescription);
		if (DISP_E_EXCEPTION == hr) 
		{
			//LOG(INFO) << "Exception message was: " << _bstr_t(exception.bstrDescription);
		}
		else
		{
			//LOGHR(DEBUG, hr) << "Failed to execute: " << _bstr_t(script);
			if (added)
			{ 
				removeScript(doc); 
			}
			return EUNEXPECTEDJSERROR;
		}

		VariantClear(result);
		result->vt = VT_USERDEFINED;
		if (exception.bstrDescription != NULL)
		{
			result->bstrVal = ::SysAllocStringByteLen((char*)exception.bstrDescription, ::SysStringByteLen(exception.bstrDescription));
		}
		else
		{
			result->bstrVal = ::SysAllocStringByteLen(NULL, 0);
		}
		wcout << _bstr_t(exception.bstrDescription) << endl;
	}

	// If the script returned an IHTMLElement, we need to copy it to make it valid.
	if( VT_DISPATCH == result->vt )
	{
		CComQIPtr<IHTMLElement> element(result->pdispVal);
		if(element)
		{
			IHTMLElement* &pDom = * (IHTMLElement**) &(result->pdispVal);
			element.CopyTo(&pDom);
		}
	}

	if (added) 
	{ 
		removeScript(doc); 
	}

	delete[] vargs;

	return SUCCESS;
}

std::wstring BrowserWrapper::GetTitle()
{
	CComPtr<IHTMLDocument2> pDoc;
	GetDocument(&pDoc);

	if (!pDoc) 
	{
		return L"";
	}

	CComBSTR title;
	HRESULT hr = pDoc->get_title(&title);
	if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Unable to get document title";
		return L"";
	}

	std::wstring titleStr = (BSTR)title;
	return titleStr;
}

std::wstring BrowserWrapper::ConvertVariantToWString(VARIANT *toConvert)
{
	VARTYPE type = toConvert->vt;

	switch(type)
	{
		case VT_BOOL:
			return toConvert->boolVal == VARIANT_TRUE ? L"true" : L"false";

		case VT_BSTR:
			if (!toConvert->bstrVal)
			{
				return L"";
			}
			
			return (BSTR)toConvert->bstrVal;
	
		case VT_I4:
			{
				wchar_t *buffer = (wchar_t *)malloc(sizeof(wchar_t) * MAX_DIGITS_OF_NUMBER);
				_i64tow_s(toConvert->lVal, buffer, MAX_DIGITS_OF_NUMBER, BASE_TEN_BASE);
				return buffer;
			}

		case VT_EMPTY:
			return L"";

		case VT_NULL:
			// TODO(shs96c): This should really return NULL.
			return L"";

		// This is lame
		case VT_DISPATCH:
			return L"";
	}
	return L"";
}

bool BrowserWrapper::getEvalMethod(IHTMLDocument2* pDoc, DISPID* pEvalId, bool* pAdded)
{
	CComPtr<IDispatch> scriptEngine;
	pDoc->get_Script(&scriptEngine);

	OLECHAR FAR* evalName = L"eval";
	HRESULT hr = scriptEngine->GetIDsOfNames(IID_NULL, &evalName, 1, LOCALE_USER_DEFAULT, pEvalId);
	if (FAILED(hr)) {
		*pAdded = true;
		// Start the script engine by adding a script tag to the page
		CComPtr<IHTMLElement> scriptTag;
		hr = pDoc->createElement(L"span", &scriptTag);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Failed to create span tag";
		}
		CComBSTR addMe(L"<span id='__webdriver_private_span'>&nbsp;<script defer></script></span>");
		scriptTag->put_innerHTML(addMe);

		CComPtr<IHTMLElement> body;
		pDoc->get_body(&body);
		CComQIPtr<IHTMLDOMNode> node(body);
		CComQIPtr<IHTMLDOMNode> scriptNode(scriptTag);

		CComPtr<IHTMLDOMNode> generatedChild;
		node->appendChild(scriptNode, &generatedChild);

		scriptEngine.Release();
		pDoc->get_Script(&scriptEngine);
		hr = scriptEngine->GetIDsOfNames(IID_NULL, &evalName, 1, LOCALE_USER_DEFAULT, pEvalId);

		if (FAILED(hr))
		{
			removeScript(pDoc);
			return false;
		}
	}

	return true;
}

bool BrowserWrapper::createAnonymousFunction(IDispatch* pScriptEngine, DISPID evalId, const std::wstring *script, VARIANT* pResult)
{
	CComVariant script_variant(script->c_str());
	DISPPARAMS parameters = {0};
	memset(&parameters, 0, sizeof parameters);
	parameters.cArgs      = 1;
	parameters.rgvarg     = &script_variant;
	parameters.cNamedArgs = 0;

	EXCEPINFO exception;
	memset(&exception, 0, sizeof exception);

	HRESULT hr = pScriptEngine->Invoke(evalId, IID_NULL, LOCALE_USER_DEFAULT, DISPATCH_METHOD, &parameters, pResult, &exception, 0);
	if (FAILED(hr)) 
	{
		if (DISP_E_EXCEPTION == hr) 
		{
			// LOGHR(INFO, hr) << "Exception message was: " << _bstr_t(exception.bstrDescription) << ": " << _bstr_t(script);
		} 
		else 
		{
			// LOGHR(DEBUG, hr) << "Failed to compile: " << script;
		}

		if (pResult)
		{
			pResult->vt = VT_USERDEFINED;
			if (exception.bstrDescription != NULL)
			{
				pResult->bstrVal = ::SysAllocStringByteLen((char*)exception.bstrDescription, ::SysStringByteLen(exception.bstrDescription));
			}
			else
			{
				pResult->bstrVal = ::SysAllocStringByteLen(NULL, 0);
			}
		}

		return false;
	}

	return true;
}

void BrowserWrapper::removeScript(IHTMLDocument2 *pDoc)
{
	CComQIPtr<IHTMLDocument3> doc3(pDoc);

	if (!doc3)
	{
		return;
	}

	CComPtr<IHTMLElement> element;
	CComBSTR id(L"__webdriver_private_span");
	HRESULT hr = doc3->getElementById(id, &element);
	if (FAILED(hr))
	{
		// LOGHR(WARN, hr) << "Cannot find the script tag. Bailing.";
		return;
	}

	CComQIPtr<IHTMLDOMNode> elementNode(element);

	if (elementNode)
	{
		CComPtr<IHTMLElement> body;
		hr = pDoc->get_body(&body);
		if (FAILED(hr))
		{
			// LOGHR(WARN, hr) << "Cannot locate body of document";
			return;
		}
		CComQIPtr<IHTMLDOMNode> bodyNode(body);
		if (!bodyNode)
		{
			// LOG(WARN) << "Cannot cast body to a standard html node";
			return;
		}
		CComPtr<IHTMLDOMNode> removed;
		hr = bodyNode->removeChild(elementNode, &removed);
		if (FAILED(hr))
		{
			// LOGHR(DEBUG, hr) << "Cannot remove child node. Shouldn't matter. Bailing";
		}
	}
}

void BrowserWrapper::findCurrentFrameWindow(IHTMLWindow2 **ppWindow)
{
	// Frame location is from _top. This is a good start
	CComPtr<IDispatch> dispatch;
	HRESULT hr = m_pBrowser->get_Document(&dispatch);
	if (FAILED(hr)) 
	{
		//LOGHR(DEBUG, hr) << "Unable to get document";
		return;
	}

	CComPtr<IHTMLDocument2> doc;
	hr = dispatch->QueryInterface(&doc);
	if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Have document but cannot cast";
	}

	// If the current frame path is null or empty, find the default content
	// The default content is either the first frame in a frameset or the body
	// of the current _top doc, even if there are iframes.
	if (0 == wcscmp(L"", this->m_pathToFrame.c_str()))
	{
		this->getDefaultContentWindow(doc, ppWindow);
		if (ppWindow)
		{
			return;
		} 
		else 
		{
			//cerr << "Cannot locate default content." << endl;
			// What can we do here?
			return;
		}
	}

	// Otherwise, tokenize the current frame and loop, finding the
	// child frame in turn
	size_t len = this->m_pathToFrame.length() + 1;
	wchar_t *path = new wchar_t[len];
	wcscpy_s(path, len, this->m_pathToFrame.c_str());
	wchar_t *next_token;
	CComQIPtr<IHTMLWindow2> interimResult;
	for (wchar_t* fragment = wcstok_s(path, L".", &next_token);
		 fragment;
		 fragment = wcstok_s(NULL, L".", &next_token))
	{
		if (!doc)
		{
			// This is seriously Not Good but what can you do?
			break;
		}

		CComQIPtr<IHTMLFramesCollection2> frames;
		doc->get_frames(&frames);

		if (frames == NULL)
		{ 
			// pathToFrame does not match. Exit.
			break;
		}

		long length = 0;
		frames->get_length(&length);
		if (!length)
		{ 
			// pathToFrame does not match. Exit.
			break; 
		} 

		CComBSTR frameName(fragment);
		CComVariant index;
		// Is this fragment a number? If so, the index will be a VT_I4
		int frameIndex = _wtoi(fragment);
		if (frameIndex > 0 || wcscmp(L"0", fragment) == 0)
		{
			index.vt = VT_I4;
			index.lVal = frameIndex;
		} 
		else 
		{
			// Alternatively, it's a name
			frameName.CopyTo(&index);
		}

		// Find the frame
		CComVariant frameHolder;
		hr = frames->item(&index, &frameHolder);

		interimResult.Release();
		if (!FAILED(hr))
		{
			interimResult = frameHolder.pdispVal;
		}

		if (!interimResult)
		{
			// pathToFrame does not match. Exit.
			break; 
		}

		// TODO: Check to see if a collection of frames were returned. Grab the 0th element if there was.

		// Was there only one result? Next time round, please.
		CComQIPtr<IHTMLWindow2> window(interimResult);
		if (!window)
		{ 
			// pathToFrame does not match. Exit.
			break; 
		} 

		doc.Detach();
		window->get_document(&doc);
	}

	if (interimResult)
	{
		*ppWindow = interimResult.Detach();
	}
	delete[] path;
}

void BrowserWrapper::getDefaultContentWindow(IHTMLDocument2 *pDoc, IHTMLWindow2 **ppWindow)
{
	CComQIPtr<IHTMLFramesCollection2> frames;
	HRESULT hr = pDoc->get_frames(&frames);
	if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Unable to get frames from document";
		return;
	}

	if (frames == NULL) 
	{
		hr = pDoc->get_parentWindow(ppWindow);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Unable to get parent window.";
		}
		return;
	}

	long length = 0;
	hr = frames->get_length(&length);
	if (FAILED(hr)) 
	{
		//LOGHR(WARN, hr) << "Cannot determine length of frames";
	}

	if (!length)
	{
		hr = pDoc->get_parentWindow(ppWindow);
		if (FAILED(hr)) 
		{
			//LOGHR(WARN, hr) << "Unable to get parent window.";
		}
		return;
	}

	CComPtr<IHTMLDocument3> doc3;
	hr = pDoc->QueryInterface(&doc3);
	if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Have document, but it's not the right type";
		hr = pDoc->get_parentWindow(ppWindow);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Unable to get parent window.";
		}
		return;
	}

	CComPtr<IHTMLElementCollection> bodyTags;
	CComBSTR bodyTagName(L"BODY");
	hr = doc3->getElementsByTagName(bodyTagName, &bodyTags);
	if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Cannot locate body";
		return;
	}

	long numberOfBodyTags = 0;
	hr = bodyTags->get_length(&numberOfBodyTags);
	if (FAILED(hr))
	{
		//LOGHR(WARN, hr) << "Unable to establish number of tags seen";
	}

	if (numberOfBodyTags)
	{
		// Not in a frameset. Return the current window
		hr = pDoc->get_parentWindow(ppWindow);
		if (FAILED(hr)) 
		{
			//LOGHR(WARN, hr) << "Unable to get parent window.";
		}
		return;
	}

	CComVariant index;
	index.vt = VT_I4;
	index.lVal = 0;

	CComVariant frameHolder;
	hr = frames->item(&index, &frameHolder);
	if (FAILED(hr)) 
	{
		//LOGHR(WARN, hr) << "Unable to get frame at index 0";
	}

	frameHolder.pdispVal->QueryInterface(__uuidof(IHTMLWindow2), (void**) ppWindow);
}

HWND BrowserWrapper::GetHwnd()
{
	if (this->m_hwnd == NULL)
	{
		this->m_hwnd =this->m_factory->GetTabWindowHandle(this->m_pBrowser);
	}

	return this->m_hwnd;
}

void __stdcall BrowserWrapper::BeforeNavigate2(IDispatch * pObject, VARIANT * pvarUrl, VARIANT * pvarFlags, VARIANT * pvarTargetFrame,
VARIANT * pvarData, VARIANT * pvarHeaders, VARIANT_BOOL * pbCancel)
{
	// std::cout << "BeforeNavigate2\r\n";
}

void __stdcall BrowserWrapper::OnQuit()
{
	this->Quitting.raise(this->m_browserId);
}

void __stdcall BrowserWrapper::NewWindow3(IDispatch **ppDisp, VARIANT_BOOL * pbCancel, DWORD dwFlags, BSTR bstrUrlContext, BSTR bstrUrl)
{
	IWebBrowser2 *pBrowser = this->m_factory->CreateBrowser();
	BrowserWrapper *newWindowWrapper = new BrowserWrapper(pBrowser, NULL, this->m_factory);
	*ppDisp = pBrowser;
	this->NewWindow.raise(newWindowWrapper);
}

void __stdcall BrowserWrapper::DocumentComplete(IDispatch *pDisp, VARIANT *URL)
{
	// Flag the browser as navigation having started.
	// std::cout << "DocumentComplete\r\n";
	this->m_navStarted = true;
}

void BrowserWrapper::attachEvents()
{
	CComQIPtr<IDispatch> pDisp(this->m_pBrowser);
	CComPtr<IUnknown> pUnk(pDisp);
	HRESULT hr = this->DispEventAdvise(pUnk);
}

void BrowserWrapper::detachEvents()
{
	CComQIPtr<IDispatch> pDisp(this->m_pBrowser);
	CComPtr<IUnknown> pUnk(pDisp);
	HRESULT hr = this->DispEventUnadvise(pUnk);
}

bool BrowserWrapper::Wait()
{
	bool isNavigating(true);

	//std::cout << "Navigate Events Completed.\r\n";
	this->m_navStarted = false;

	// Navigate events completed. Waiting for browser.Busy != false...
	isNavigating = this->m_navStarted;
	VARIANT_BOOL isBusy(VARIANT_FALSE);
	HRESULT hr = this->m_pBrowser->get_Busy(&isBusy);
	if (isNavigating || FAILED(hr) || isBusy)
	{
		//std::cout << "Browser busy property is true.\r\n";
		return false;
	}

	// Waiting for browser.ReadyState == READYSTATE_COMPLETE...;
	isNavigating = this->m_navStarted;
	READYSTATE readyState;
	hr = this->m_pBrowser->get_ReadyState(&readyState);
	if (isNavigating || FAILED(hr) || readyState != READYSTATE_COMPLETE)
	{
		//std::cout << "readyState is not 'Complete'.\r\n";
		return false;
	}

	// Waiting for document property != null...
	isNavigating = this->m_navStarted;
	CComQIPtr<IDispatch> ppDisp;
	hr = this->m_pBrowser->get_Document(&ppDisp);
	if (isNavigating && FAILED(hr) && !ppDisp)
	{
		//std::cout << "Get Document failed.\r\n";
		return false;
	}

	// Waiting for document to complete...
	CComPtr<IHTMLDocument2> pDoc;
	hr = ppDisp->QueryInterface(&pDoc);
	if (SUCCEEDED(hr))
	{
		isNavigating = this->isDocumentNavigating(pDoc);
	}

	if (!isNavigating)
	{
		this->m_waitRequired = false;
	}

	return !isNavigating;
}

bool BrowserWrapper::isDocumentNavigating(IHTMLDocument2 *pDoc)
{
	bool isNavigating(true);
	// Starting WaitForDocumentComplete()
	isNavigating = this->m_navStarted;
	CComBSTR readyState;
	HRESULT hr = pDoc->get_readyState(&readyState);
	if (FAILED(hr) || isNavigating || _wcsicmp(readyState, L"complete") != 0)
	{
		//std::cout << "readyState is not complete\r\n";
		return true;
	}
	else
	{
		isNavigating = false;
	}

	// document.readyState == complete
	isNavigating = this->m_navStarted;
	CComPtr<IHTMLFramesCollection2> frames;
	hr = pDoc->get_frames(&frames);
	if (isNavigating || FAILED(hr))
	{
		//std::cout << "could not get frames\r\n";
		return true;
	}

	if (frames != NULL)
	{
		long frameCount = 0;
		hr = frames->get_length(&frameCount);

		CComVariant index;
		index.vt = VT_I4;
		for (long i = 0; i < frameCount; ++i)
		{
			// Waiting on each frame
			index.lVal = i;
			CComVariant result;
			hr = frames->item(&index, &result);
			if (FAILED(hr))
			{
				return true;
			}

			CComQIPtr<IHTMLWindow2> window(result.pdispVal);
			if (!window)
			{
				// Frame is not an HTML frame.
				continue;
			}

			CComPtr<IHTMLDocument2> frameDocument;
			hr = window->get_document(&frameDocument);
			if (hr == E_ACCESSDENIED)
			{
				// Cross-domain documents may throw Access Denied. If so,
				// get the document through the IWebBrowser2 interface.
				CComPtr<IWebBrowser2> frameBrowser;
				CComQIPtr<IServiceProvider> pServiceProvider(window);
				hr = pServiceProvider->QueryService(IID_IWebBrowserApp, &frameBrowser);
				if (SUCCEEDED(hr))
				{
					CComQIPtr<IDispatch> frameDocDisp;
					hr = frameBrowser->get_Document(&frameDocDisp);
					hr = frameDocDisp->QueryInterface(&frameDocument);
				}
			}

			isNavigating = this->m_navStarted;
			if (isNavigating)
			{
				break;
			}

			// Recursively call to wait for the frame document to complete
			isNavigating = this->isDocumentNavigating(frameDocument);
			if (isNavigating)
			{
				break;
			}
		}
	}
	return isNavigating;
}

int BrowserWrapper::getElapsedMilliseconds(UINT64 startTime)
{
	UINT64 currentTime = this->getTime();
	return (currentTime - startTime) / 10000;
}

UINT64 BrowserWrapper::getTime() 
{ 
	SYSTEMTIME st; 
	GetSystemTime(&st); 
 
	FILETIME ft; 
	SystemTimeToFileTime(&st, &ft);  // converts to file time format 
	ULARGE_INTEGER ui; 
	ui.LowPart=ft.dwLowDateTime; 
	ui.HighPart=ft.dwHighDateTime; 
 
	return ui.QuadPart; 
} 