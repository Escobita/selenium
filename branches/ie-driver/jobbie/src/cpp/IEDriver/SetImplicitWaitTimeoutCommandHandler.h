#pragma once
#include "BrowserManager.h"

class SetImplicitWaitTimeoutCommandHandler :
	public WebDriverCommandHandler
{
public:

	SetImplicitWaitTimeoutCommandHandler(void)
	{
	}

	virtual ~SetImplicitWaitTimeoutCommandHandler(void)
	{
	}

protected:

	void SetImplicitWaitTimeoutCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("ms") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "ms";
		}
		else
		{
			int timeout = commandParameters["ms"].asInt();
			manager->SetImplicitWaitTimeout(timeout);
			response->m_statusCode = SUCCESS;
		}
	}
};
