#pragma once
#include "BrowserManager.h"

class GoBackCommandHandler :
	public WebDriverCommandHandler
{
public:

	GoBackCommandHandler(void)
	{
	}

	virtual ~GoBackCommandHandler(void)
	{
	}

protected:

	void GoBackCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);
		HRESULT hr = pWrapper->m_pBrowser->GoBack();
		response->m_statusCode = SUCCESS;
	}
};
