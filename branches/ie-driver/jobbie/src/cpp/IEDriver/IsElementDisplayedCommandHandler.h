#pragma once
#include "BrowserManager.h"

class IsElementDisplayedCommandHandler :
	public WebDriverCommandHandler
{
public:

	IsElementDisplayedCommandHandler(void)
	{
	}

	virtual ~IsElementDisplayedCommandHandler(void)
	{
	}

protected:

	void IsElementDisplayedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				bool result;
				statusCode = pElementWrapper->IsDisplayed(&result);
				if (statusCode == SUCCESS)
				{
					response->m_value = result;
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
