#pragma once
#include "BrowserManager.h"

class GetElementLocationOnceScrolledIntoViewCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementLocationOnceScrolledIntoViewCommandHandler(void)
	{
	}

	virtual ~GetElementLocationOnceScrolledIntoViewCommandHandler(void)
	{
	}

protected:

	void GetElementLocationOnceScrolledIntoViewCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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
				long x, y, width, height;
				statusCode = pElementWrapper->GetLocationOnceScrolledIntoView(hwnd, &x, &y, &width, &height);
				if (statusCode == SUCCESS)
				{
					response->m_value["x"] = x;
					response->m_value["y"] = y;
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
