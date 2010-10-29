#pragma once
#include "BrowserManager.h"

class GetElementValueCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementValueCommandHandler(void)
	{
	}

	virtual ~GetElementValueCommandHandler(void)
	{
	}

protected:

	void GetElementValueCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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
				CComVariant valueVariant;
				statusCode = pElementWrapper->GetAttributeValue(pBrowserWrapper, L"value", &valueVariant);
				if (statusCode == SUCCESS)
				{
					std::wstring value(pBrowserWrapper->ConvertVariantToWString(&valueVariant));
					std::string valueStr(CW2A(value.c_str()));
					response->m_value = valueStr;
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
