#pragma once
#include "BrowserManager.h"

class GetElementAttributeCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementAttributeCommandHandler(void)
	{
	}

	virtual ~GetElementAttributeCommandHandler(void)
	{
	}

protected:

	void GetElementAttributeCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (locatorParameters.find("name") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "name";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));
			std::wstring name(CA2W(locatorParameters["name"].c_str(), CP_UTF8));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				CComVariant valueVariant;
				statusCode = pElementWrapper->GetAttributeValue(pBrowserWrapper, name, &valueVariant);
				if (valueVariant.vt != VT_EMPTY && valueVariant.vt != VT_NULL)
				{
					std::wstring value(pBrowserWrapper->ConvertVariantToWString(&valueVariant));
					std::string valueStr(CW2A(value.c_str(), CP_UTF8));
					response->m_value = valueStr;
				}
				else
				{
					response->m_value = Json::Value::null;
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
