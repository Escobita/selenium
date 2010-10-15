#pragma once
#include "BrowserManager.h"

class NewSessionCommandHandler :
	public WebDriverCommandHandler
{
public:

	NewSessionCommandHandler(void)
	{
		this->m_ignorePreExecutionWait = true;
		this->m_ignorePostExecutionWait = false;
	}

	virtual ~NewSessionCommandHandler(void)
	{
	}

protected:

	void NewSessionCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		DWORD dwProcId = manager->m_factory->LaunchBrowserProcess(manager->m_port);
		CComPtr<IWebBrowser2> pBrowser = manager->m_factory->AttachToBrowser(dwProcId);
		BrowserWrapper *wrapper = new BrowserWrapper(pBrowser);
		manager->AddWrapper(wrapper);
		response->m_statusCode = 303;
		std::string id = CW2A(manager->m_managerId.c_str());
		response->m_value = "/session/" + id;
	}
};
