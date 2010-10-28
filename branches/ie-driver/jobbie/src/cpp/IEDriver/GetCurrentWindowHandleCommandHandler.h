#pragma once
#include "BrowserManager.h"

class GetCurrentWindowHandleCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetCurrentWindowHandleCommandHandler(void)
	{
	}

	virtual ~GetCurrentWindowHandleCommandHandler(void)
	{
	}

protected:

	void ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		std::string currentHandle(CW2A(manager->m_currentBrowser.c_str()));
		Json::Value value(currentHandle);
		response->m_value = value;
	}
};
