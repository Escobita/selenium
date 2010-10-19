#pragma once
#include "BrowserManager.h"

class IsElementSelectedCommandHandler :
	public WebDriverCommandHandler
{
public:

	IsElementSelectedCommandHandler(void)
	{
	}

	virtual ~IsElementSelectedCommandHandler(void)
	{
	}

protected:

	void IsElementSelectedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			std::wstring text(L"");
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				std::wstring value;
				statusCode = pElementWrapper->GetAttributeValue(pBrowserWrapper, L"selected", &value);
				if (statusCode == SUCCESS)
				{
					bool selected = wcscmp(L"true", value.c_str()) == 0 ? 1 : 0;
					response->m_value = selected;
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
