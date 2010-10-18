#pragma once
#include "BrowserManager.h"
#include "interactions.h"

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
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);

			long x = 0, y = 0, w = 0, h = 0;
			int result = pElementWrapper->GetLocationOnceScrolledIntoView(hwnd, &x, &y, &w, &h);
			if (result != SUCCESS) 
			{
				statusCode = result;
			}

			long clickX = x + (w ? w / 2 : 0);
			long clickY = y + (h ? h / 2 : 0);

			// Create a mouse move, mouse down, mouse up OS event
			LRESULT lresult = mouseMoveTo(hwnd, 10, x, y, clickX, clickY);
			if (result != SUCCESS)
			{
				statusCode = result;
			}
			
			lresult = clickAt(hwnd, clickX, clickY);
			if (result != SUCCESS)
			{
				statusCode = result;
			}

			response->m_statusCode = statusCode;
		}
	}
};
