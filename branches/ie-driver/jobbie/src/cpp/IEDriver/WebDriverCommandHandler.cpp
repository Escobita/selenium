#include "StdAfx.h"
#include "WebDriverCommandHandler.h"

WebDriverCommandHandler::WebDriverCommandHandler(void)
{
}

WebDriverCommandHandler::~WebDriverCommandHandler(void)
{
}

void WebDriverCommandHandler::Execute(BrowserManager *manager, std::map<std::string,std::string> locatorParameters, std::map<std::string,std::string> commandParameters, WebDriverResponse *response)
{
	if (!this->m_ignorePreExecutionWait)
	{
		manager->m_trackedBrowsers[manager->m_currentBrowser].Wait();
	}

	this->ExecuteInternal(manager, locatorParameters, commandParameters, response);

	if (!this->m_ignorePostExecutionWait)
	{
		manager->m_trackedBrowsers[manager->m_currentBrowser].Wait();
	}
}

void WebDriverCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string,std::string> locatorParameters, std::map<std::string,std::string> commandParameters, WebDriverResponse *response)
{
}