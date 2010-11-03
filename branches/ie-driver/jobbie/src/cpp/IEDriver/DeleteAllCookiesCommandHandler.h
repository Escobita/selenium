#pragma once
#include "BrowserManager.h"

class DeleteAllCookiesCommandHandler :
	public WebDriverCommandHandler
{
public:

	DeleteAllCookiesCommandHandler(void)
	{
	}

	virtual ~DeleteAllCookiesCommandHandler(void)
	{
	}

protected:

	void DeleteAllCookiesCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
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

			std::wstring cookieName(this->GetCookieName(cookieElement));
			pBrowserWrapper->DeleteCookie(cookieName);
		}
	}

	std::wstring DeleteAllCookiesCommandHandler::GetCookieName(std::wstring cookie)
	{
		size_t cookieSeparatorPos(cookie.find_first_of(L"="));
		std::wstring cookieName(cookie.substr(0, cookieSeparatorPos));
		return cookieName;
	}
};
