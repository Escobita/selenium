#pragma once
#include "BrowserManager.h"

class GetElementLocationCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementLocationCommandHandler(void)
	{
	}

	virtual ~GetElementLocationCommandHandler(void)
	{
	}

protected:

	void GetElementLocationCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				CComQIPtr<IHTMLElement2> element2(pElementWrapper->m_pElement);
				if (!element2)
				{
					statusCode = EUNHANDLEDERROR;
				}
				CComPtr<IHTMLRect> rect;
				element2->getBoundingClientRect(&rect);

				long x, y;
				rect->get_left(&x);
				rect->get_top(&y);

				CComQIPtr<IHTMLDOMNode2> node(element2);
				CComPtr<IDispatch> ownerDocDispatch;
				node->get_ownerDocument(&ownerDocDispatch);
				CComQIPtr<IHTMLDocument3> ownerDoc(ownerDocDispatch);

				CComPtr<IHTMLElement> tempDoc;
				ownerDoc->get_documentElement(&tempDoc);

				CComQIPtr<IHTMLElement2> docElement(tempDoc);
				long left = 0, top = 0;
				docElement->get_scrollLeft(&left);
				docElement->get_scrollTop(&top);

				x += left;
				y += top;

				response->m_value["x"] = x;
				response->m_value["y"] = y;
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}

			response->m_statusCode = statusCode;
		}
	}
};
