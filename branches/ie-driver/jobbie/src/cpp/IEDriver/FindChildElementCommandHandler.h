#pragma once
#include "BrowserManager.h"

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
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str());
			std::wstring value = CA2W(commandParameters["value"].asString().c_str());
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);

			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			ElementWrapper *pParentElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pParentElementWrapper);

			if (statusCode == SUCCESS)
			{
				ElementWrapper *pFoundElement;
				int statusCode = pFinder->FindElement(manager, pParentElementWrapper, value, &pFoundElement);
				if (statusCode == SUCCESS)
				{
					response->m_value = pFoundElement->ConvertToJson();
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
