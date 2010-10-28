#pragma once
#include "BrowserManager.h"

class GetTitleCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetTitleCommandHandler(void)
	{
	}

	virtual ~GetTitleCommandHandler(void)
	{
	}
protected:

	void GetTitleCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);
		std::string title(CW2A(pWrapper->GetTitle().c_str()));

		response->m_value = title;
	}
};
