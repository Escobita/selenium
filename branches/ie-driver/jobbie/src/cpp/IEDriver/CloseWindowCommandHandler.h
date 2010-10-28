#pragma once
#include "BrowserManager.h"

class CloseWindowCommandHandler :
	public WebDriverCommandHandler
{
public:

	CloseWindowCommandHandler(void)
	{
	}

	virtual ~CloseWindowCommandHandler(void)
	{
	}

protected:

	void CloseWindowCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);
		HRESULT hr = pWrapper->m_pBrowser->Quit();
		response->m_statusCode = SUCCESS;
	}
};
