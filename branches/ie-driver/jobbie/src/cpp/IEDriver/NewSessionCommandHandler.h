#pragma once
#include "BrowserManager.h"

class NewSessionCommandHandler :
	public WebDriverCommandHandler
{
public:

	NewSessionCommandHandler(void)
	{
	}

	virtual ~NewSessionCommandHandler(void)
	{
	}

protected:

	void NewSessionCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		DWORD dwProcId = manager->m_factory->LaunchBrowserProcess(manager->m_port);
		ProcessWindowInfo procWinInfo;
		procWinInfo.dwProcessId = dwProcId;
		procWinInfo.hwndBrowser = NULL;
		procWinInfo.pBrowser = NULL;
		manager->m_factory->AttachToBrowser(&procWinInfo);
		BrowserWrapper *wrapper = new BrowserWrapper(procWinInfo.pBrowser, procWinInfo.hwndBrowser, manager->m_factory);
		manager->AddWrapper(wrapper);
		response->m_statusCode = 303;
		std::string id = CW2A(manager->m_managerId.c_str());
		response->m_value = "/session/" + id;
	}
};
