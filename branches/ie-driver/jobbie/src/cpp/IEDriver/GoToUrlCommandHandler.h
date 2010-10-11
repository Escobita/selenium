#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class GoToUrlCommandHandler :
	public WebDriverCommandHandler
{
public:

	GoToUrlCommandHandler(void)
	{
	}

	virtual ~GoToUrlCommandHandler(void)
	{
	}

protected:

	void GoToUrlCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, std::string> commandParameters, WebDriverResponse * response)
	{
		std::string value = commandParameters["value"];
		std::transform(value.begin(), value.end(), value.begin(), ::toupper);
		response->m_statusCode = 0;
		response->m_value = "Received value " + value;

		//if (commandParameters.find("url") == commandParameters.end())
		//{
		//	response->m_statusCode = 400;
		//	response->m_value = "url";
		//}
		//else
		//{
		//	int result = manager->m_trackedBrowsers[manager->m_currentBrowser].GoToUrl(commandParameters["url"]);
		//	response->m_statusCode = result;
		//}
	}
};
