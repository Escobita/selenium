#pragma once
#include "BrowserManager.h"

class GetElementSizeCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementSizeCommandHandler(void)
	{
	}

	virtual ~GetElementSizeCommandHandler(void)
	{
	}

protected:

	void GetElementSizeCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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
				bool displayed;
				statusCode = pElementWrapper->IsDisplayed(&displayed);
				if (statusCode == SUCCESS)
				{
					long height, width;
					pElementWrapper->m_pElement->get_offsetHeight(&height);
					pElementWrapper->m_pElement->get_offsetWidth(&width);
					response->m_value["width"] = width;
					response->m_value["height"] = height;
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
