#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class NewSessionCommandHandler :
	public WebDriverCommandHandler
{
public:

	NewSessionCommandHandler(void)
	{
	}

	virtual ~NewSessionCommandHandler(void)
	{
	}

protected:

	void NewSessionCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, std::string> commandParameters, WebDriverResponse * response)
	{
		std::string value = commandParameters["value"];
		std::transform(value.begin(), value.end(), value.begin(), ::toupper);
		response->m_statusCode = 0;
		response->m_value["upperValue"] = value;
	}
};
