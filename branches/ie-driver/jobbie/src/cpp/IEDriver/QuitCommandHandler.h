#pragma once
#include "BrowserManager.h"

class QuitCommandHandler :
	public WebDriverCommandHandler
{
public:

	QuitCommandHandler(void)
	{
	}

	virtual ~QuitCommandHandler(void)
	{
	}

protected:

	void QuitCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		std::vector<std::wstring> trackedBrowserHandles;
		std::map<std::wstring, BrowserWrapper*>::iterator end = manager->m_trackedBrowsers.end();
		for (std::map<std::wstring, BrowserWrapper*>::iterator it = manager->m_trackedBrowsers.begin(); it != end; ++it)
		{
			trackedBrowserHandles.push_back(it->first);
		}

		std::vector<std::wstring>::iterator handleEnd = trackedBrowserHandles.end();
		for (std::vector<std::wstring>::iterator handleIt = trackedBrowserHandles.begin(); handleIt != handleEnd; ++handleIt)
		{
			BrowserWrapper *pWrapper(manager->m_trackedBrowsers[*handleIt]);
			HRESULT hr = pWrapper->m_pBrowser->Quit();
			if (FAILED(hr))
			{
				cout << "Quit failed: " << hr << "\r\n";
			}
		}

		response->m_statusCode = SUCCESS;
	}
};
