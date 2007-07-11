#include "StdAfx.h"
#include "IeWrapper.h"
#include "utils.h"
#include <exdispid.h>
#include <iostream>
#include <jni.h>
#include <comutil.h>
#include <comdef.h>
#include <stdlib.h>
#include <string>

#include "atlbase.h"
#include "atlstr.h"

using namespace std;

long invokeCount = 0;
long queryCount = 0;

IeWrapper::IeWrapper()
{
	if (!SUCCEEDED(CoCreateInstance(CLSID_InternetExplorer, NULL, CLSCTX_LOCAL_SERVER, IID_IWebBrowser2, (void**)&ie))) 
	{
		throw "Cannot create InternetExplorer instance";
	}

	currentFrame = 0;

///	sink = new IeEventSink(ie);
}

IeWrapper::~IeWrapper()
{
//	delete sink;	
}

bool IeWrapper::getVisible()
{
	VARIANT_BOOL visible;
	ie->get_Visible(&visible);
	return visible == VARIANT_TRUE;
}

void IeWrapper::setVisible(bool isVisible) 
{
	if (isVisible)
		ie->put_Visible(VARIANT_TRUE);
	else 
		ie->put_Visible(VARIANT_FALSE);
}

const wchar_t* IeWrapper::getCurrentUrl() 
{
	CComQIPtr<IHTMLDocument2, &__uuidof(IHTMLDocument2)> doc = getDocument();
	CComBSTR url;
	doc->get_URL(&url);

	return bstr2wchar(url);
}

const wchar_t* IeWrapper::getTitle() 
{
	CComBSTR title;
	IHTMLDocument2 *doc = getDocument();
	doc->get_title(&title);
	doc->Release();

	return bstr2wchar(title);
}

void IeWrapper::get(const wchar_t *url)
{
	CComVariant spec(url);
	CComVariant dummy;

	ie->Navigate2(&spec, &dummy, &dummy, &dummy, &dummy);
	currentFrame = 0;
	waitForNavigateToFinish();
}

ElementWrapper* IeWrapper::selectElementById(const wchar_t *elementId) 
{
	IHTMLDocument3 *doc = getDocument3();
	IHTMLElement* element = NULL;
	BSTR id = SysAllocString(elementId);
	doc->getElementById(id, &element);
	doc->Release();
	SysFreeString(id);
	
	if (element != NULL) {
		IHTMLDOMNode* node = NULL;
		element->QueryInterface(__uuidof(IHTMLDOMNode), (void **)&node);
		element->Release();
		ElementWrapper* toReturn = new ElementWrapper(this, node);
		node->Release();
		return toReturn;
	}

	throw "Cannot find element";
}

ElementWrapper* IeWrapper::selectElementByLink(const wchar_t *elementLink)
{
	IHTMLDocument2 *doc = getDocument();
	IHTMLElementCollection* linkCollection;
	doc->get_links(&linkCollection);
	doc->Release();

	long linksLength;
	linkCollection->get_length(&linksLength);

	for (int i = 0; i < linksLength; i++) {
		VARIANT idx;
		idx.vt = VT_I4;
		idx.lVal = i;
		IDispatch* dispatch;
		VARIANT zero;
		zero.vt = VT_I4;
		zero.lVal = 0;
		linkCollection->item(idx, zero, &dispatch);
		VariantClear(&idx);
		VariantClear(&zero);

		IHTMLElement* element;
		dispatch->QueryInterface(__uuidof(IHTMLElement), (void**)&element);
		dispatch->Release();

		BSTR linkText;
		element->get_innerText(&linkText);

		const wchar_t *converted = bstr2wchar(linkText);
		SysFreeString(linkText);

		if (wcscmp(elementLink, converted) == 0) {
			delete converted;
			IHTMLDOMNode* linkNode;
			element->QueryInterface(__uuidof(IHTMLDOMNode), (void**)&linkNode);
			element->Release();
			linkCollection->Release();
			ElementWrapper* toReturn = new ElementWrapper(this, linkNode);
			linkNode->Release();
			return toReturn;
		}
		delete converted;
		element->Release();
	}
	linkCollection->Release();
    throw "Cannot find element";
}

void IeWrapper::waitForNavigateToFinish() 
{
	VARIANT_BOOL busy;
	ie->get_Busy(&busy);
	while (busy == VARIANT_TRUE) {
		Sleep(100);
		ie->get_Busy(&busy);
	}

	READYSTATE readyState;
	ie->get_ReadyState(&readyState);
	while (readyState != READYSTATE_COMPLETE) {
		Sleep(50);
		ie->get_ReadyState(&readyState);
	}

	IHTMLDocument2* doc = getDocument();
	waitForDocumentToComplete(doc);

	IHTMLFramesCollection2* frames = NULL;
	doc->get_frames(&frames);

	if (frames != NULL) {
		long framesLength = 0;
		frames->get_length(&framesLength);

		VARIANT index;
		VariantInit(&index);
		index.vt = VT_I4;

		for (long i = 0; i < framesLength; i++) {
			index.lVal = i;
			VARIANT result;
			frames->item(&index, &result);

			IHTMLWindow2* window;
			result.pdispVal->QueryInterface(__uuidof(IHTMLWindow2), (void**)&window);

			IHTMLDocument2* frameDoc;
			window->get_document(&frameDoc);

			waitForDocumentToComplete(frameDoc);

			frameDoc->Release();
			window->Release();
			VariantClear(&result);
		}

		VariantClear(&index);
		frames->Release();
	}

	doc->Release();
}

void IeWrapper::waitForDocumentToComplete(IHTMLDocument2* doc)
{
	BSTR state;
	doc->get_readyState(&state);
	wchar_t* currentState = bstr2wchar(state);

	while (wcscmp(L"complete", currentState) != 0) {
		Sleep(50);
		SysFreeString(state);
		delete currentState;
		doc->get_readyState(&state);
		currentState = bstr2wchar(state);
	}

	SysFreeString(state);
	delete currentState;
}

void IeWrapper::switchToFrame(int frameIndex) 
{
	currentFrame = frameIndex;
}

IHTMLDocument2* IeWrapper::getDocument() 
{
	CComPtr<IDispatch> dispatch = NULL;
	ie->get_Document(&dispatch);
	IHTMLDocument2* doc = NULL;
	dispatch->QueryInterface(__uuidof(IHTMLDocument2), (void**)&doc);

	CComQIPtr<IHTMLFramesCollection2> frames;
	doc->get_frames(&frames);

	long length = 0;
	frames->get_length(&length);
	
	if (!length) {
		return doc;
	}

	doc->Release();

	VARIANT index;
	VariantInit(&index);
	index.vt = VT_I4;
	index.lVal = currentFrame;
	VARIANT result;
	VariantInit(&result);
	frames->item(&index, &result);

	VariantClear(&index);
	CComQIPtr<IHTMLWindow2, &__uuidof(IHTMLWindow2)> win;
	win = result.pdispVal;
	VariantClear(&result);

	win->get_document(&doc);
	return doc;
}

IHTMLDocument3* IeWrapper::getDocument3() 
{
	IHTMLDocument2* doc2 = getDocument();
	IHTMLDocument3* toReturn;
	doc2->QueryInterface(__uuidof(IHTMLDocument3), (void**)&toReturn);
	doc2->Release();
	return toReturn;
}

IeEventSink::IeEventSink(IWebBrowser2* ie) 
{
	this->ie = ie;
	this->ie->AddRef();

	HRESULT hr = AtlAdvise(this->ie, (IUnknown*) this, DIID_DWebBrowserEvents2, &eventSinkCookie);
/*
	IConnectionPointContainer* pCPContainer;
 
        // Step 1: Get a pointer to the connection point container
        HRESULT hr = ie->QueryInterface(IID_IConnectionPointContainer, 
                                           (void**)&pCPContainer);
        if (SUCCEEDED(hr))
        {
           // m_pConnectionPoint is defined like this:
           IConnectionPoint* m_pConnectionPoint;
 
           // Step 2: Find the connection point
           hr = pCPContainer->FindConnectionPoint(
                         DIID_DWebBrowserEvents2, &m_pConnectionPoint);
           if (SUCCEEDED(hr))
           {
              // Step 3: Advise
              hr = m_pConnectionPoint->Advise(this, &eventSinkCookie);
              if (FAILED(hr))
              {
                 cout <<  "Failed to Advise" << endl;
			  }
           }
 
           pCPContainer->Release();
        }
		*/
}

IeEventSink::~IeEventSink() 
{
	AtlUnadvise(ie, DIID_DWebBrowserEvents2, eventSinkCookie);
}

// IUnknown methods
STDMETHODIMP IeEventSink::QueryInterface(REFIID interfaceId, void **pointerToObj)
{
	queryCount++;
	cout << "Querying interface: " << queryCount << endl;
    if (interfaceId == IID_IUnknown)
    {
        *pointerToObj = (IUnknown *)this;
        return S_OK;
    }
    else if (interfaceId == IID_IDispatch)
    {
        *pointerToObj = (IDispatch *)this;
        return S_OK;
    }

	*pointerToObj = NULL;
    return E_NOINTERFACE;
    
}

STDMETHODIMP_(ULONG) IeEventSink::AddRef()
{
//	cout << "AddRef" << endl;
    return 1;
}

STDMETHODIMP_(ULONG) IeEventSink::Release()
{
//	cout << "Release" << endl;
    return 1;
}


// IDispatch methods
STDMETHODIMP IeEventSink::Invoke(DISPID dispidMember,
                                     REFIID riid,
                                     LCID lcid, WORD wFlags,
                                     DISPPARAMS* pDispParams,
                                     VARIANT* pvarResult,
                                     EXCEPINFO*  pExcepInfo,
                                     UINT* puArgErr)
{
	invokeCount++;
	cout << "Invoking: " << invokeCount << endl;

	if (!pDispParams)
		return E_INVALIDARG;

	switch (dispidMember) {
		case DISPID_PROGRESSCHANGE:
			break;

		case DISPID_BEFORENAVIGATE2:
			cout << "Before navigate" << endl;
			break;

		case DISPID_NAVIGATECOMPLETE2:
			cout << "Navigation complete" << endl;

		case DISPID_NEWWINDOW2:
			cout << "New window event detected" << endl;
			// Check the argument's type
			/*
			if (pDispParams->rgvarg[0].vt == (VT_BYREF|VT_VARIANT)) {
				CComVariant varURL(*pDispParams->rgvarg[0].pvarVal);
				varURL.ChangeType(VT_BSTR);

			char str[100];   // Not the best way to do this.
			}
			*/
			break;    

		default:
			break;
	}

	return S_OK;
}

STDMETHODIMP IeEventSink::GetIDsOfNames(REFIID    riid,
                                                 LPOLESTR *names,
                                                 UINT      numNames,
                                                 LCID      localeContextId,
                                                 DISPID *  dispatchIds)
{
    return E_NOTIMPL;
}

STDMETHODIMP IeEventSink::GetTypeInfoCount(UINT* pctinfo)
{
    return E_NOTIMPL;
}

STDMETHODIMP IeEventSink::GetTypeInfo(UINT        typeInfoId,
                                               LCID        localeContextId,
                                               ITypeInfo** pointerToTypeInfo)
{
    return E_NOTIMPL;
}
