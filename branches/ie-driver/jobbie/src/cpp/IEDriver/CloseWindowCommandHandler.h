#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class CloseWindowCommandHandler :
	public WebDriverCommandHandler
{
public:

	CloseWindowCommandHandler(void)
	{
		this->m_ignorePreExecutionWait = false;
		this->m_ignorePostExecutionWait = false;
	}

	virtual ~CloseWindowCommandHandler(void)
	{
	}

protected:

	void CloseWindowCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		int result = manager->m_trackedBrowsers[manager->m_currentBrowser]->CloseBrowser();
		response->m_statusCode = result;
	}
};
