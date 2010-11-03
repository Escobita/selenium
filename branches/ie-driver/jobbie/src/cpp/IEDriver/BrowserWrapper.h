#pragma once
#include "BrowserWrapperEvent.h"
#include "BrowserFactory.h"
#include "CommandValues.h"
#include "ErrorCodes.h"
#include "json.h"
#include <string>
#include <iostream>
#include <queue>
#include <rpc.h>
#include <exdispid.h>
#include <exdisp.h>
#include <mshtml.h>

#define SCRIPT_ARGTYPE_STRING 0
#define SCRIPT_ARGTYPE_INT 1
#define SCRIPT_ARGTYPE_DOUBLE 2
#define SCRIPT_ARGTYPE_BOOL 3
#define SCRIPT_ARGTYPE_ELEMENT 4

#define BASE_TEN_BASE 10
#define MAX_DIGITS_OF_NUMBER 22

using namespace std;

class BrowserWrapper :
	public IDispEventSimpleImpl<1, BrowserWrapper, &DIID_DWebBrowserEvents2>

{
public:
	BrowserWrapper(IWebBrowser2* browser, HWND hwnd, BrowserFactory *factory);
	virtual ~BrowserWrapper(void);

	std::wstring m_browserId;
	BrowserFactory *m_factory;
	BrowserWrapperEvent<BrowserWrapper*> NewWindow;
	BrowserWrapperEvent<std::wstring> Quitting;

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

	BEGIN_SINK_MAP(BrowserWrapper)
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_BEFORENAVIGATE2, BeforeNavigate2, BeforeNavigate2Info())
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_DOCUMENTCOMPLETE, DocumentComplete, DocumentCompleteInfo())
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_ONQUIT, OnQuit, NoArgumentsInfo())
		SINK_ENTRY_INFO(1, DIID_DWebBrowserEvents2, DISPID_NEWWINDOW3, NewWindow3, NewWindow3Info())
	END_SINK_MAP()

	STDMETHOD_(void, BeforeNavigate2)(IDispatch * pObject, VARIANT * pvarUrl, VARIANT * pvarFlags,
		VARIANT * pvarTargetFrame, VARIANT * pvarData, VARIANT * pvarHeaders, VARIANT_BOOL * pbCancel);
	STDMETHOD_(void, DocumentComplete)(IDispatch *pDisp,VARIANT *URL);
	STDMETHOD_(void, OnQuit)();
	STDMETHOD_(void, NewWindow3)(IDispatch **ppDisp, VARIANT_BOOL * pbCancel, DWORD dwFlags, BSTR bstrUrlContext, BSTR bstrUrl);

	bool Wait(void);
	void GetDocument(IHTMLDocument2 **ppDoc);
	int ExecuteScript(const std::wstring *script, SAFEARRAY *args, VARIANT *result);
	HWND GetHwnd(void);
	std::wstring GetTitle(void);
	std::wstring GetCookies(void);
	int AddCookie(std::wstring cookie);
	int DeleteCookie(std::wstring cookieName);
	bool m_waitRequired;

	CComPtr<IWebBrowser2> m_pBrowser;
	std::wstring m_pathToFrame;
	std::wstring ConvertVariantToWString(VARIANT *toConvert);

private:
	HWND m_hwnd;
	bool m_navStarted;
	void attachEvents(void);
	void detachEvents(void);
	bool isDocumentNavigating(IHTMLDocument2 *pDoc);
	UINT64 getTime(void);
	bool isHtmlPage(IHTMLDocument2 *pDoc);
	int getElapsedMilliseconds(UINT64 startTime);
	void findCurrentFrameWindow(IHTMLWindow2 **ppWindow);
	void getDefaultContentWindow(IHTMLDocument2 *pDoc, IHTMLWindow2 **ppWindow);
	bool getEvalMethod(IHTMLDocument2* pDoc, DISPID* pEvalId, bool* pAdded);
	void removeScript(IHTMLDocument2* pDoc);
	bool createAnonymousFunction(IDispatch* pScriptEngine, DISPID evalId, const std::wstring *script, VARIANT* pResult);
};
