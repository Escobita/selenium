#pragma once
#include "StdAfx.h"
#include "BrowserWrapper.h"
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
	BrowserManager(void);
	virtual ~BrowserManager(void);
	std::wstring StartManager();
	
	std::wstring m_managerId;
	bool m_isRunning;
	std::wstring m_currentBrowser;
	std::map<std::wstring, BrowserWrapper> m_trackedBrowsers;

	void NewBrowserEventHandler(BrowserWrapper wrapper);
	void BrowserQuittingEventHandler(std::wstring browserId);

private:
	static DWORD WINAPI ThreadProc(LPVOID lpParameter);
	void Start(void);

	int m_newBrowserEventId;
	int m_browserQuittingEventId;
	WebDriverResponse DispatchCommand(WebDriverCommand* command);
	Json::Value GetCurrentWindowHandle();
	Json::Value GetAllWindowHandles();
	void SwitchToWindow(std::map<std::string, std::string> locator, WebDriverResponse *response);
};
}