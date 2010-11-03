#pragma once
#include "BrowserManager.h"

class GetAllCookiesCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetAllCookiesCommandHandler(void)
	{
	}

	virtual ~GetAllCookiesCommandHandler(void)
	{
	}

protected:

	void GetAllCookiesCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		Json::Value responseValue(Json::arrayValue);
		BrowserWrapper *pBrowserWrapper;
		manager->GetCurrentBrowser(&pBrowserWrapper);

		std::wstring cookieString = pBrowserWrapper->GetCookies();
		while (cookieString.size() > 0)
		{
			size_t cookieDelimiterPos = cookieString.find(L"; ");
			std::wstring cookieElement(cookieString.substr(0, cookieDelimiterPos));
			if (cookieDelimiterPos == std::wstring::npos)
			{
				cookieString = L"";
			}
			else
			{
				cookieString = cookieString.substr(cookieDelimiterPos + 2);
			}

			Json::Value cookieValue(this->CreateJsonValueForCookie(cookieElement));
			responseValue.append(cookieValue);
		}

		response->m_value = responseValue;
	}

	Json::Value GetAllCookiesCommandHandler::CreateJsonValueForCookie(std::wstring cookie)
	{
		size_t cookieElementSeparatorPos(cookie.find_first_of(L"="));
		std::string cookieElementName(CW2A(cookie.substr(0, cookieElementSeparatorPos).c_str(), CP_UTF8));
		std::string cookieElementValue(CW2A(cookie.substr(cookieElementSeparatorPos + 1).c_str(), CP_UTF8));
		Json::Value cookieValue;
		cookieValue["name"] = cookieElementName;
		cookieValue["value"] = cookieElementValue;
		cookieValue["secure"] = false;
		return cookieValue;
	}
};
