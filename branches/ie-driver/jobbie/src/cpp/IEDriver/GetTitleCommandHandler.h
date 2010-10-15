#pragma once
#include "BrowserManager.h"

class GetTitleCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetTitleCommandHandler(void)
	{
		this->m_ignorePreExecutionWait = false;
		this->m_ignorePostExecutionWait = false;
	}

	virtual ~GetTitleCommandHandler(void)
	{
	}
protected:

	void GetTitleCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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

		CComBSTR title;
		HRESULT hr = pDoc->get_title(&title);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Unable to get document title";
			response->m_value = "";
			return;
		}

		std::string titleStr = CW2A((LPCWSTR)title);
		response->m_value = titleStr;
	}
};
