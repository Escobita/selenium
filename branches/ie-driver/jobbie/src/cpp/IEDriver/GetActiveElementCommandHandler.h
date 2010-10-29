#pragma once
#include "BrowserManager.h"

class GetActiveElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetActiveElementCommandHandler(void)
	{
	}

	virtual ~GetActiveElementCommandHandler(void)
	{
	}

protected:
	void GetActiveElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		response->m_statusCode = SUCCESS;
		BrowserWrapper *pWrapper;
		manager->GetCurrentBrowser(&pWrapper);

		IHTMLElement* pDom;

		CComPtr<IHTMLDocument2> doc;
		pWrapper->GetDocument(&doc);
		if (!doc) {
			response->m_statusCode = ENOSUCHDOCUMENT;
			response->m_value["message"] = "Document not found";
			return;
		}

		CComPtr<IHTMLElement> element;
		doc->get_activeElement(&element);

		if (!element) {
			// Grab the body instead
			doc->get_body(&element);
		}

		if (element)
		{
			element.CopyTo(&pDom);
			ElementWrapper *pElementWrapper = new ElementWrapper(pDom);
			manager->m_knownElements[pElementWrapper->m_elementId] = pElementWrapper;
			response->m_value = pElementWrapper->ConvertToJson();
		}
	}
};
