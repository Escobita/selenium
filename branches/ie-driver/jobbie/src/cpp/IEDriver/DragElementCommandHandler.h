#pragma once
#include "BrowserManager.h"

class DragElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	DragElementCommandHandler(void)
	{
	}

	virtual ~DragElementCommandHandler(void)
	{
	}

protected:

	void DragElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (commandParameters.find("x") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "x";
		}
		else if (commandParameters.find("y") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "y";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));

			int x = commandParameters["x"].asInt();
			int y = commandParameters["y"].asInt();

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				pElementWrapper->DragBy(hwnd, x, y, manager->GetSpeed());
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}
			response->m_statusCode = statusCode;
		}
	}
};
