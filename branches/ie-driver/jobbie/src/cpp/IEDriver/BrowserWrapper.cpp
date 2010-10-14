#include "StdAfx.h"
#include "BrowserWrapper.h"

BrowserWrapper::BrowserWrapper(CComPtr<IWebBrowser2> browser)
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

	this->m_pBrowser = browser;
	this->AttachEvents();
}

BrowserWrapper::~BrowserWrapper(void)
{
}

int BrowserWrapper::GoToUrl(std::string url)
{
	CComVariant pVarUrl(url.c_str());
	CComVariant dummy;

	HRESULT hr = m_pBrowser->Navigate2(&pVarUrl, &dummy, &dummy, &dummy, &dummy);
	while (!this->m_pendingWait)
	{
		::Sleep(WAIT_TIME_IN_MILLISECONDS);
	}

	return SUCCESS;
}

int BrowserWrapper::CloseBrowser(void)
{
	HRESULT hr = m_pBrowser->Quit();
	return SUCCESS;
}

void __stdcall BrowserWrapper::BeforeNavigate2(IDispatch * pObject, VARIANT * pvarUrl, VARIANT * pvarFlags, VARIANT * pvarTargetFrame,
VARIANT * pvarData, VARIANT * pvarHeaders, VARIANT_BOOL * pbCancel)
{
	std::cout << "BeforeNavigate2\r\n";
	//if (this->m_pNavDisp.p == NULL)
	//{
	//	this->m_navStarted = true;
	//	this->m_pNavDisp.Attach(pObject);
	//	if (!this->m_pendingWait)
	//	{
	//		this->m_pendingWait = true;
	//	}
	//}
}

void __stdcall BrowserWrapper::OnQuit()
{
	std::cout << "OnQuit\r\n";
}

void __stdcall BrowserWrapper::NewWindow3(IDispatch * pDisp, VARIANT_BOOL * pbCancel, DWORD dwFlags, BSTR bstrUrlContext, BSTR bstrUrl)
{
	std::cout << "NewWindow\r\n";
}

void __stdcall BrowserWrapper::DocumentComplete(IDispatch *pDisp, VARIANT *URL)
{
	std::cout << "DocumentComplete\r\n";
	//if (this->m_pNavDisp.p != NULL && this->m_pNavDisp.IsEqualObject(pDisp))
	//{
	//	this->m_pNavDisp.Detach();
	//}
}

void BrowserWrapper::AttachEvents()
{
	CComQIPtr<IDispatch> pDisp(this->m_pBrowser);
	CComPtr<IUnknown> pUnk(pDisp);
	HRESULT hr = this->DispEventAdvise(pUnk);
}

void BrowserWrapper::DetachEvents()
{
	CComQIPtr<IDispatch> pDisp(this->m_pBrowser);
	CComPtr<IUnknown> pUnk(pDisp);
	HRESULT hr = this->DispEventUnadvise(pUnk);
}

std::string BrowserWrapper::getWindowName()
{
	CComPtr<IDispatch> dispatch;
	HRESULT hr = m_pBrowser->get_Document(&dispatch);
	if (FAILED(hr)) {
		return "";
	}

	CComQIPtr<IHTMLDocument2> doc(dispatch);
	if (!doc) {
		return "";
	}

	CComPtr<IHTMLWindow2> window;
	hr = doc->get_parentWindow(&window);
	if (FAILED(hr)) {
		return "";
	}

	CComBSTR windowName;
	window->get_name(&windowName);
	std::string name = CW2A(BSTR(windowName));
	return name;
}


void BrowserWrapper::Wait()
{
	int errorCode = SUCCESS;
	UINT64 waitStartTime = this->getTime();
	while (this->m_pendingWait && errorCode == SUCCESS)
	{
		errorCode = this->WaitInternal(waitStartTime);
	}

	// If we did not successfully complete our wait
	// (for example, due to a timeout) reset the pending
	// wait flag.
	if (errorCode != SUCCESS)
	{
		this->m_pendingWait = false;
	}
}

int BrowserWrapper::WaitInternal(UINT64 waitStartTime)
{
	this->m_pendingWait = false;
	return SUCCESS;
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