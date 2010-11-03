#pragma once
#include "BrowserManager.h"
#include <ctime>

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
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str(), CP_UTF8);
			std::wstring value = CA2W(commandParameters["value"].asString().c_str(), CP_UTF8);
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);

			int timeout(manager->GetImplicitWaitTimeout());
			clock_t end = clock() + (timeout / 1000 * CLOCKS_PER_SEC);
			if (timeout > 0 && timeout < 1000) 
			{
				end += 1 * CLOCKS_PER_SEC;
			}


			int statusCode = SUCCESS;
			do
			{
				statusCode = pFinder->FindElement(manager, NULL, value, &pFoundElement);
				if (statusCode == SUCCESS)
				{
					break;
				}
			}
			while (clock() < end);
			
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
