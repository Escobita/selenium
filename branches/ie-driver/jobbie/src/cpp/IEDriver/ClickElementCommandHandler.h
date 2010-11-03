#pragma once
#include "BrowserManager.h"

class ClickElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	ClickElementCommandHandler(void)
	{
	}

	virtual ~ClickElementCommandHandler(void)
	{
	}

protected:

	void ClickElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				statusCode = pElementWrapper->Click(hwnd);
				pBrowserWrapper->m_waitRequired = true;
				if (statusCode != SUCCESS)
				{
					response->m_value["message"] = "Cannot click on element";
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
