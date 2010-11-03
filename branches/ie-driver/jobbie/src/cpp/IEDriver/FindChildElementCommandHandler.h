#pragma once
#include "BrowserManager.h"
#include <ctime>

class FindChildElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	FindChildElementCommandHandler(void)
	{
	}

	virtual ~FindChildElementCommandHandler(void)
	{
	}

protected:

	void FindChildElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (commandParameters.find("using") == commandParameters.end())
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
			int statusCode = SUCCESS;
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str(), CP_UTF8);
			std::wstring value = CA2W(commandParameters["value"].asString().c_str(), CP_UTF8);
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);

			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));

			ElementWrapper *pParentElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pParentElementWrapper);

			if (statusCode == SUCCESS)
			{
				ElementWrapper *pFoundElement;

				int timeout(manager->GetImplicitWaitTimeout());
				clock_t end = clock() + (timeout / 1000 * CLOCKS_PER_SEC);
				if (timeout > 0 && timeout < 1000) 
				{
					end += 1 * CLOCKS_PER_SEC;
				}

				do
				{
					statusCode = pFinder->FindElement(manager, pParentElementWrapper, value, &pFoundElement);
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
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}

			response->m_statusCode = statusCode;
		}
	}
};
