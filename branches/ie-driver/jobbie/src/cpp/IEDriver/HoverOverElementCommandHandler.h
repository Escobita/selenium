#pragma once
#include "BrowserManager.h"

class HoverOverElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	HoverOverElementCommandHandler(void)
	{
	}

	virtual ~HoverOverElementCommandHandler(void)
	{
	}

protected:

	void HoverOverElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				statusCode = pElementWrapper->Hover(hwnd);
			}
			response->m_statusCode = statusCode;
		}
	}
};
