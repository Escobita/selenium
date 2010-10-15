#pragma once
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

class GoToUrlCommandHandler :
	public WebDriverCommandHandler
{
public:

	GoToUrlCommandHandler(void)
	{
		this->m_ignorePreExecutionWait = false;
		this->m_ignorePostExecutionWait = false;
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
			while (!pWrapper->m_pendingWait)
			{
				::Sleep(WAIT_TIME_IN_MILLISECONDS);
			}

			pWrapper->m_pathToFrame = L"";
			response->m_statusCode = SUCCESS;
		}
	}
};
