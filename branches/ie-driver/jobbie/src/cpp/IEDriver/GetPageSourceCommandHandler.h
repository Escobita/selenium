#pragma once
#include "BrowserManager.h"

class GetPageSourceCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetPageSourceCommandHandler(void)
	{
	}

	virtual ~GetPageSourceCommandHandler(void)
	{
	}
protected:

	void GetPageSourceCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);

		CComPtr<IHTMLDocument2> pDoc;
		pWrapper->GetDocument(&pDoc);
		
		CComPtr<IHTMLDocument3> pDoc3;
		CComQIPtr<IHTMLDocument3> pQIDoc(pDoc);
		if (pQIDoc)
		{
			pDoc3 = pQIDoc.Detach();
		}

		if (!pDoc3)
		{
			response->m_value = "";
			return;
		}

		CComPtr<IHTMLElement> pDocElement;
		HRESULT hr = pDoc3->get_documentElement(&pDocElement);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Unable to get document element from page";
			response->m_value = "";
			return;
		}

		CComBSTR html;
		hr = pDocElement->get_outerHTML(&html);
		if (FAILED(hr))
		{
			//LOGHR(WARN, hr) << "Have document element but cannot read source.";
			response->m_value = "";
			return;
		}

		std::string pageSource = CW2A((LPCWSTR)html, CP_UTF8);
		response->m_value = pageSource;
	}
};
