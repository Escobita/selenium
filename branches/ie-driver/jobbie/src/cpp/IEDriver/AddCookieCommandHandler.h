#pragma once
#include "BrowserManager.h"

class AddCookieCommandHandler :
	public WebDriverCommandHandler
{
public:

	AddCookieCommandHandler(void)
	{
	}

	virtual ~AddCookieCommandHandler(void)
	{
	}

protected:

	void AddCookieCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("cookie") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "cookie";
		}

		Json::Value cookieValue = commandParameters["cookie"];
		std::string cookieString(cookieValue["name"].asString() + "=" + cookieValue["value"].asString() + "; ");
		cookieValue.removeMember("name");
		cookieValue.removeMember("value");

		bool isSecure(cookieValue["secure"].asBool());
		if (isSecure)
		{
			cookieString += "secure; ";
		}
		cookieValue.removeMember("secure");

		Json::Value::iterator it = cookieValue.begin();
		for (; it != cookieValue.end(); ++it)
		{
			std::string key = it.key().asString();
			std::string value = cookieValue[key].asString();
			if (value != "")
			{
				cookieString += key + "=" + cookieValue[key].asString() + "; ";
			}
		}


		BrowserWrapper *pBrowserWrapper;
		manager->GetCurrentBrowser(&pBrowserWrapper);

		std::wstring cookie(CA2W(cookieString.c_str(), CP_UTF8));
		int statusCode = pBrowserWrapper->AddCookie(cookie);
		if (statusCode != SUCCESS)
		{
			response->m_value["message"] = L"Unable to add cookie to page";
		}

		response->m_statusCode = statusCode;
	}
};
