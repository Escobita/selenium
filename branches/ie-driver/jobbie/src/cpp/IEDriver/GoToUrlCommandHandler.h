#pragma once
#include "BrowserManager.h"

class GoToUrlCommandHandler :
	public WebDriverCommandHandler
{
public:

	GoToUrlCommandHandler(void)
	{
	}

	virtual ~GoToUrlCommandHandler(void)
	{
	}

protected:

	void GoToUrlCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("url") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "url";
		}
		else
		{
			BrowserWrapper *pWrapper;
			manager->GetCurrentBrowser(&pWrapper);
			std::string url = commandParameters["url"].asString();
			CComVariant pVarUrl(url.c_str());
			CComVariant dummy;

			HRESULT hr = pWrapper->m_pBrowser->Navigate2(&pVarUrl, &dummy, &dummy, &dummy, &dummy);
			pWrapper->m_waitRequired = true;

			pWrapper->m_pathToFrame = L"";
			response->m_statusCode = SUCCESS;
		}
	}
};
