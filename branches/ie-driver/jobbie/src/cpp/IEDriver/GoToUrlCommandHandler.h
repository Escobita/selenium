#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class GoToUrlCommandHandler :
	public WebDriverCommandHandler
{
public:

	GoToUrlCommandHandler(void)
	{
		this->m_ignorePreExecutionWait = false;
		this->m_ignorePostExecutionWait = false;
	}

	virtual ~GoToUrlCommandHandler(void)
	{
	}

protected:

	void GoToUrlCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("url") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "url";
		}
		else
		{
			int result = manager->m_trackedBrowsers[manager->m_currentBrowser]->GoToUrl(commandParameters["url"].asString());
			response->m_statusCode = result;
		}
	}
};
