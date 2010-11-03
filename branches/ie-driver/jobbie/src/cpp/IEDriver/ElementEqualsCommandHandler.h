#pragma once
#include "BrowserManager.h"

class ElementEqualsCommandHandler :
	public WebDriverCommandHandler
{
public:

	ElementEqualsCommandHandler(void)
	{
	}

	virtual ~ElementEqualsCommandHandler(void)
	{
	}

protected:

	void ElementEqualsCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (locatorParameters.find("other") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "other";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));
			std::wstring otherElementId(CA2W(locatorParameters["other"].c_str(), CP_UTF8));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				ElementWrapper *pOtherElementWrapper;
				statusCode = this->GetElement(manager, otherElementId, &pOtherElementWrapper);
				if (statusCode == SUCCESS)
				{
					response->m_value = (pElementWrapper->m_pElement == pOtherElementWrapper->m_pElement);
				}
				else
				{
					response->m_value["message"] = "Element is no longer valid";
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
