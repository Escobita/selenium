#pragma once
#include "BrowserManager.h"

class GetElementTextCommandHandler :
	public WebDriverCommandHandler
{
public:
	GetElementTextCommandHandler(void)
	{
	}

	~GetElementTextCommandHandler(void)
	{
	}

protected:

	void GetElementTextCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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
				CComBSTR tagName;
				pElementWrapper->m_pElement->get_tagName(&tagName);
				bool isTitle = tagName == L"TITLE";

				if (isTitle)
				{
					text = pBrowserWrapper->GetTitle();
					std::string title(CW2A(text.c_str()));
					response->m_value = title;
				}
				else
				{
					text = pElementWrapper->GetText();
					std::string elementText(CW2A(text.c_str()));
					response->m_value = elementText;
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
