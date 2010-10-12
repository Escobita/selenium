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

using namespace std;

extern "C"
{
class BrowserManager
{
public:
	BrowserManager(int port);
	virtual ~BrowserManager(void);
	std::wstring StartManager();
	
	std::wstring m_managerId;
	int m_port;
	bool m_isRunning;
	std::wstring m_currentBrowser;
	std::map<std::wstring, BrowserWrapper> m_trackedBrowsers;
	BrowserFactory *m_factory;

	void NewBrowserEventHandler(BrowserWrapper wrapper);
	void BrowserQuittingEventHandler(std::wstring browserId);
	WebDriverResponse DispatchCommand(WebDriverCommand* command);
	void AddWrapper(BrowserWrapper wrapper);

private:
	static DWORD WINAPI ThreadProc(LPVOID lpParameter);
	void Start(void);
	void PopulateCommandHandlerRepository(void);
	int LaunchBrowser();

	int m_newBrowserEventId;
	int m_browserQuittingEventId;
	std::map<int, WebDriverCommandHandler*> m_commandHandlerRepository;
};
}