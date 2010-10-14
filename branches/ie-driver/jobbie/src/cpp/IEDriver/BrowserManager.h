#pragma once
#include "StdAfx.h"
#include "BrowserWrapper.h"
#include "BrowserFactory.h"
#include "WebDriverCommandHandler.h"
#include <string>
#include <map>
#include <vector>
#include <algorithm>
#include <Objbase.h>

#define WD_INIT WM_APP + 1
#define WD_SET_COMMAND WM_APP + 2
#define WD_EXEC_COMMAND WM_APP + 3
#define WD_GET_RESPONSE_LENGTH WM_APP + 4
#define WD_GET_RESPONSE WM_APP + 5

#define EVENT_NAME L"WD_START_EVENT"

using namespace std;

extern "C"
{
// We use a CWindowImpl (creating a hidden window) here because we
// want to synchronize access to the command handler. For that we
// use SendMessage() most of the time, and SendMessage() requires
// a window handle.
class BrowserManager : public CWindowImpl<BrowserManager>
{
public:
	DECLARE_WND_CLASS(L"WebDriverWndClass")

	BEGIN_MSG_MAP(BrowserManager)
		MESSAGE_HANDLER(WM_CREATE, OnCreate)
		MESSAGE_HANDLER(WM_CLOSE, OnClose)
		MESSAGE_HANDLER(WD_INIT, OnInit)
		MESSAGE_HANDLER(WD_SET_COMMAND, OnSetCommand)
		MESSAGE_HANDLER(WD_EXEC_COMMAND, OnExecCommand)
		MESSAGE_HANDLER(WD_GET_RESPONSE_LENGTH, OnGetResponseLength)
		MESSAGE_HANDLER(WD_GET_RESPONSE, OnGetResponse)
	END_MSG_MAP()

	LRESULT OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnInit(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnSetCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnExecCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnGetResponseLength(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	LRESULT OnGetResponse(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

	std::wstring StartManager();
	
	std::wstring m_managerId;
	int m_port;
	static DWORD WINAPI ThreadProc(LPVOID lpParameter);
	std::wstring m_currentBrowser;
	std::map<std::wstring, BrowserWrapper*> m_trackedBrowsers;
	BrowserFactory *m_factory;
	void AddWrapper(BrowserWrapper* wrapper);

private:
	void NewBrowserEventHandler(BrowserWrapper* wrapper);
	void BrowserQuittingEventHandler(std::wstring browserId);
	void DispatchCommand(void);

	void Start(void);
	void PopulateCommandHandlerRepository(void);
	int LaunchBrowser();

	WebDriverCommand *m_command;
	std::wstring m_serializedResponse;
	int m_newBrowserEventId;
	int m_browserQuittingEventId;
	std::map<int, WebDriverCommandHandler*> m_commandHandlerRepository;
};
}