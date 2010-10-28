#pragma once
#include "BrowserManager.h"

class GetAllWindowHandlesCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetAllWindowHandlesCommandHandler(void)
	{
	}

	virtual ~GetAllWindowHandlesCommandHandler(void)
	{
	}

protected:

	void GetAllWindowHandlesCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		Json::Value handles;
		std::map<std::wstring, BrowserWrapper*>::iterator end = manager->m_trackedBrowsers.end();
		for (std::map<std::wstring, BrowserWrapper*>::iterator it = manager->m_trackedBrowsers.begin(); it != end; ++it)
		{
			std::string handle(CW2A(it->first.c_str()));
			handles.append(handle);
		}

		response->m_value = handles;
	}
};
