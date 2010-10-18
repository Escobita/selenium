#pragma once
#include "BrowserManager.h"

class GetElementTagNameCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementTagNameCommandHandler(void)
	{
	}

	virtual ~GetElementTagNameCommandHandler(void)
	{
	}

protected:

	void GetElementTagNameCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));
			ElementWrapper *pElementWrapper;
			int statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				CComBSTR temp;
				pElementWrapper->m_pElement->get_tagName(&temp);
				std::wstring tagName((BSTR)temp);
				std::transform(tagName.begin(), tagName.end(), tagName.begin(), tolower);
				std::string returnValue(CW2A(tagName.c_str()));
				response->m_value = returnValue;
			}

			response->m_statusCode = statusCode;
		}
	}
};
