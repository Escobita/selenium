#pragma once
#include "BrowserManager.h"

class ClearElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	ClearElementCommandHandler(void)
	{
	}

	virtual ~ClearElementCommandHandler(void)
	{
	}

protected:

	void ClearElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			std::wstring text(L"");
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
				if (!element2) {
					statusCode = EUNHANDLEDERROR;
					response->m_value = "Cannot cast element to IHTMLElement2";
				}
				else
				{
					CComQIPtr<IHTMLTextAreaElement> textArea(pElementWrapper->m_pElement);
					CComQIPtr<IHTMLInputElement> inputElement(pElementWrapper->m_pElement);
					CComBSTR v;
					if (textArea) {
						textArea->get_value(&v);
					}
					if (inputElement) {
						inputElement->get_value(&v);
					}
					bool fireChange = v.Length() > 0;

					element2->focus();

					if (textArea) textArea->put_value(L"");
					if (inputElement) inputElement->put_value(L"");
					
					if (fireChange) {
						CComQIPtr<IHTMLDOMNode> node(pElementWrapper->m_pElement);
						pElementWrapper->FireEvent(node, L"onchange");
					}

					element2->blur();

					LRESULT lr;
					::SendMessageTimeoutW(hwnd, WM_SETTEXT, 0, (LPARAM) L"", SMTO_ABORTIFHUNG, 3000, (PDWORD_PTR)&lr);
				}
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}

			response->m_statusCode = statusCode;
		}
	}
};
