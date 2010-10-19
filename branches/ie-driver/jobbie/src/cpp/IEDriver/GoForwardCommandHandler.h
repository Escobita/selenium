#pragma once
#include "BrowserManager.h"

class GoForwardCommandHandler :
	public WebDriverCommandHandler
{
public:

	GoForwardCommandHandler(void)
	{
	}

	virtual ~GoForwardCommandHandler(void)
	{
	}

protected:

	void GoForwardCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);
		HRESULT hr = pWrapper->m_pBrowser->GoForward();
		response->m_statusCode = SUCCESS;
	}
};
