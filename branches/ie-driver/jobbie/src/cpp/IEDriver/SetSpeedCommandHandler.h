#pragma once
#include "BrowserManager.h"

class SetSpeedCommandHandler :
	public WebDriverCommandHandler
{
public:

	SetSpeedCommandHandler(void)
	{
	}

	virtual ~SetSpeedCommandHandler(void)
	{
	}

protected:

	void SetSpeedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("speed") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "speed";
		}
		else
		{
			std::string speed = commandParameters["speed"].asString();
			if (strcmp(speed.c_str(), SPEED_SLOW) == 0)
			{
				manager->SetSpeed(1000);
			}
			else if (strcmp(speed.c_str(), SPEED_MEDIUM) == 0)
			{
				manager->SetSpeed(500);
			}
			else
			{
				manager->SetSpeed(0);
			}
			response->m_statusCode = SUCCESS;
		}
	}
};
