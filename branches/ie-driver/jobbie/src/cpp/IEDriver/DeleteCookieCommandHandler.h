#pragma once
#include "BrowserManager.h"

class DeleteCookieCommandHandler :
	public WebDriverCommandHandler
{
public:

	DeleteCookieCommandHandler(void)
	{
	}

	virtual ~DeleteCookieCommandHandler(void)
	{
	}

protected:

	void DeleteCookieCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("name") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "name";
		}

		std::wstring cookieName(CA2W(locatorParameters["name"].c_str(), CP_UTF8));
		BrowserWrapper *pBrowserWrapper;
		manager->GetCurrentBrowser(&pBrowserWrapper);
		int statusCode = pBrowserWrapper->DeleteCookie(cookieName);
		if (statusCode != SUCCESS)
		{
			response->m_value["message"] = "Unable to delete cookie";
		}

		response->m_statusCode = statusCode;
	}
};
