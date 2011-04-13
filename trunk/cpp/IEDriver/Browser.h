#ifndef WEBDRIVER_IE_BROWSER_H_
#define WEBDRIVER_IE_BROWSER_H_

#include <exdispid.h>
#include <exdisp.h>
#include <mshtml.h>
#include <iostream>
#include <string>
#include "json.h"
#include "BrowserFactory.h"
#include "ErrorCodes.h"
#include "DocumentHost.h"
#include "messages.h"

#define BASE_TEN_BASE 10
#define MAX_DIGITS_OF_NUMBER 22

using namespace std;

namespace webdriver {

class Browser : public DocumentHost, public IDispEventSimpleImpl<1, Browser, &DIID_DWebBrowserEvents2> {
public:
	Browser(IWebBrowser2* browser, HWND hwnd, HWND session_handle);
	virtual ~Browser(void);

	static inline _ATL_FUNC_INFO* BeforeNavigate2Info() {
		static _ATL_FUNC_INFO kBeforeNavigate2 = { CC_STDCALL, VT_EMPTY, 7,
			{ VT_DISPATCH, VT_VARIANT | VT_BYREF, VT_VARIANT | VT_BYREF, VT_VARIANT | VT_BYREF, VT_VARIANT | VT_BYREF, VT_VARIANT | VT_BYREF, VT_BOOL | VT_BYREF } };
	  return &kBeforeNavigate2;
	}

	static inline _ATL_FUNC_INFO* DocumentCompleteInfo() {
		static _ATL_FUNC_INFO kDocumentComplete = { CC_STDCALL, VT_EMPTY, 2, { VT_DISPATCH, VT_VARIANT|VT_BYREF } };
		return &kDocumentComplete;
	}

	static inline _ATL_FUNC_INFO* NoArgumentsInfo() {
	  static _ATL_FUNC_INFO kNoArguments = { CC_STDCALL, VT_EMPTY, 0 };
	  return &kNoArguments;
	}

	static inline _ATL_FUNC_INFO* NewWindow3Info() {
		static _ATL_FUNC_INFO kNewWindow3 = { CC_STDCALL, VT_EMPTY, 5,
			{ VT_DISPATCH | VT_BYREF, VT_BOOL | VT_BYREF, VT_I4, VT_BSTR, VT_BSTR } };
		return &kNewWindow3;
	}

	BEGIN_SINK_MAP(Browser)
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_BEFORENAVIGATE2, BeforeNavigate2, BeforeNavigate2Info())
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_DOCUMENTCOMPLETE, DocumentComplete, DocumentCompleteInfo())
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_ONQUIT, OnQuit, NoArgumentsInfo())
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_NEWWINDOW3, NewWindow3, NewWindow3Info())
	END_SINK_MAP()

	STDMETHOD_(void, BeforeNavigate2)(IDispatch* pObject, VARIANT* pvarUrl, VARIANT* pvarFlags,
		VARIANT* pvarTargetFrame, VARIANT* pvarData, VARIANT* pvarHeaders, VARIANT_BOOL* pbCancel);
	STDMETHOD_(void, DocumentComplete)(IDispatch* pDisp, VARIANT* URL);
	STDMETHOD_(void, OnQuit)();
	STDMETHOD_(void, NewWindow3)(IDispatch** ppDisp, VARIANT_BOOL* pbCancel, DWORD dwFlags, BSTR bstrUrlContext, BSTR bstrUrl);

	bool Wait(void);
	void Close(void);
	void GetDocument(IHTMLDocument2** doc);
	std::wstring GetWindowName(void);
	std::wstring GetTitle(void);
	HWND GetWindowHandle(void);
	HWND GetTopLevelWindowHandle(void);
	HWND GetActiveDialogWindowHandle(void);

	long GetWidth(void);
	long GetHeight(void);
	void SetWidth(long width);
	void SetHeight(long height);

	int NavigateToUrl(const std::wstring& url);
	int NavigateBack(void);
	int NavigateForward(void);
	int Refresh(void);

	IWebBrowser2* browser(void) { return this->browser_; }

private:
	void AttachEvents(void);
	void DetachEvents(void);
	bool IsDocumentNavigating(IHTMLDocument2* doc);
	bool GetDocumentFromWindow(IHTMLWindow2* window, IHTMLDocument2** doc);
	HWND GetTabWindowHandle(void);
	HWND FindContentWindowHandle(HWND top_level_window);
	void CheckDialogType(HWND dialog_window_handle);

	CComPtr<IWebBrowser2> browser_;
	bool is_navigation_started_;
};

} // namespace webdriver

#endif // WEBDRIVER_IE_BROWSER_H_
