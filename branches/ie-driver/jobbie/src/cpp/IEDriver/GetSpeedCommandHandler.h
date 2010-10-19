#pragma once
#include "BrowserManager.h"

class GetSpeedCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetSpeedCommandHandler(void)
	{
	}

	virtual ~GetSpeedCommandHandler(void)
	{
	}

protected:

	void GetSpeedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		int speed = manager->GetSpeed();
		switch (speed)
		{
		case 1000:
			response->m_value = "SLOW";
			break;
		case 500:
			response->m_value = "MEDIUM";
			break;
		default:
			response->m_value = "FAST";
			break;
		}
		response->m_statusCode = SUCCESS;
	}
};
