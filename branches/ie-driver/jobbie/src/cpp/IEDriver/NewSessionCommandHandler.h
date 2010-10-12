#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class NewSessionCommandHandler :
	public WebDriverCommandHandler
{
public:

	NewSessionCommandHandler(void)
	{
		this->m_ignorePreExecutionWait = true;
	}

	virtual ~NewSessionCommandHandler(void)
	{
	}

protected:

	void NewSessionCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		DWORD dwProcId = manager->m_factory->LaunchBrowserProcess(manager->m_port);
		CComPtr<IWebBrowser2> pBrowser = manager->m_factory->AttachToBrowser(dwProcId);
		BrowserWrapper wrapper(pBrowser);
		manager->AddWrapper(wrapper);
		response->m_statusCode = 303;
		std::string id = CW2A(manager->m_managerId.c_str());
		response->m_value = "/session/" + id;

		//std::string value = commandParameters["value"];
		//std::transform(value.begin(), value.end(), value.begin(), ::toupper);
		//response->m_statusCode = 0;
		//response->m_value["upperValue"] = value;
	}
};
