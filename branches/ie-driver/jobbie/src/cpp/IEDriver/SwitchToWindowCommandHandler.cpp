#include "StdAfx.h"
#include "SwitchToWindowCommandHandler.h"

SwitchToWindowCommandHandler::SwitchToWindowCommandHandler(void)
{
}

SwitchToWindowCommandHandler::~SwitchToWindowCommandHandler(void)
{
}

void SwitchToWindowCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, std::string> commandParameters, WebDriverResponse * response)
{
	if (locatorParameters.find("name") == locatorParameters.end())
	{
		response->m_statusCode = 400;
		response->m_value = "name";
	}
	else
	{
		std::wstring foundBrowserHandle = L"";
		std::string desiredName = locatorParameters["name"];
		std::map<std::wstring, BrowserWrapper>::iterator end = manager->m_trackedBrowsers.end();
		for (std::map<std::wstring, BrowserWrapper>::iterator it = manager->m_trackedBrowsers.begin(); it != end; ++it)
		{
			std::string browserName = it->second.getWindowName();
			if (browserName == desiredName)
			{
				foundBrowserHandle = it->first;
				break;
			}

			std::string browserHandle = CW2A(it->first.c_str());
			if (browserHandle == desiredName)
			{
				foundBrowserHandle = it->first;
				break;
			}
		}

		if (foundBrowserHandle == L"")
		{
			response->m_statusCode = ENOSUCHWINDOW;
		}
		else
		{
			WebDriverCommand waitCommand;
			waitCommand.m_commandValue = CommandValue::NoCommand;
			manager->m_trackedBrowsers[manager->m_currentBrowser].runCommand(waitCommand, NULL);
			manager->m_currentBrowser = foundBrowserHandle;
			manager->m_trackedBrowsers[manager->m_currentBrowser].runCommand(waitCommand, NULL);
		}
	}
}
