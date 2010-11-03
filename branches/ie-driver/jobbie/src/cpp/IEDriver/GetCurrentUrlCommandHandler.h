#pragma once
#include "BrowserManager.h"

class GetCurrentUrlCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetCurrentUrlCommandHandler(void)
	{
	}

	virtual ~GetCurrentUrlCommandHandler(void)
	{
	}
protected:

	void GetCurrentUrlCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);

		CComPtr<IHTMLDocument2> pDoc;
		pWrapper->GetDocument(&pDoc);

		if (!pDoc) 
		{
			response->m_value = "";
			return;
		}

		CComBSTR url;
		HRESULT hr = pDoc->get_URL(&url);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Unable to get current URL";
			response->m_value = "";
			return;
		}

		std::string urlStr = CW2A((LPCWSTR)url, CP_UTF8);
		response->m_value = urlStr;
	}
};
