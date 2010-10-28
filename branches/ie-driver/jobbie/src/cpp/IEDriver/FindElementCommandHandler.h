#pragma once
#include "BrowserManager.h"

class FindElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	FindElementCommandHandler(void)
	{
	}

	virtual ~FindElementCommandHandler(void)
	{
	}

protected:

	void FindElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("using") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "using";
		}
		else if (commandParameters.find("value") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "value";
		}
		else
		{
			ElementWrapper *pFoundElement;
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str());
			std::wstring value = CA2W(commandParameters["value"].asString().c_str());
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);
			int statusCode = pFinder->FindElement(manager, NULL, value, &pFoundElement);
			if (statusCode == SUCCESS)
			{
				response->m_value = pFoundElement->ConvertToJson();
			}
			else
			{
				response->m_value["message"] = "No element found";
			}

			response->m_statusCode = statusCode;
		}
	}
};
