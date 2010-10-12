#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class GetSessionCapabilitiesCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetSessionCapabilitiesCommandHandler(void)
	{
	}

	virtual ~GetSessionCapabilitiesCommandHandler(void)
	{
	}

protected:

	void GetSessionCapabilitiesCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		response->m_value["browserName"] = "internet explorer";
		response->m_value["version"] = "0";
		response->m_value["javascriptEnabled"] = true;
		response->m_value["platform"] = "WINDOWS";
		response->m_value["nativeEvents"] = true;

		//std::string value = commandParameters["value"];
		//std::transform(value.begin(), value.end(), value.begin(), ::toupper);
		//response->m_statusCode = 0;
		//response->m_value["upperValue"] = value;
	}
};
