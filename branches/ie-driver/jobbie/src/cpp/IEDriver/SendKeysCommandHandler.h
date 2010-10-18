#pragma once
#include <ctime>
#include "BrowserManager.h"
#include "interactions.h"

class SendKeysCommandHandler :
	public WebDriverCommandHandler
{
public:

	SendKeysCommandHandler(void)
	{
	}

	virtual ~SendKeysCommandHandler(void)
	{
	}

protected:

	void SendKeysCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (commandParameters.find("value") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "value";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			std::wstring keys(L"");
			Json::Value keyArray(commandParameters["value"]);
			for (int i = 0; i < keyArray.size(); ++i )
			{
				keys.append(CA2W(keyArray[i].asString().c_str()));
			}

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);

			bool displayed;
			statusCode = pElementWrapper->IsDisplayed(&displayed);
			if (statusCode != SUCCESS || !displayed)
			{
				response->m_statusCode = EELEMENTNOTDISPLAYED;
				return;
			}

			if (!pElementWrapper->IsEnabled())
			{
				response->m_statusCode = EELEMENTNOTDISPLAYED;
				return;
			}

			CComQIPtr<IHTMLElement> element(pElementWrapper->m_pElement);
			//checkValidDOM(element);

			//const HWND hWnd = getHwnd();
			//const HWND ieWindow = getIeServerWindow(hWnd);

			//keyboardData keyData;
			//keyData.main = hWnd;  // IE's main window
			//keyData.hwnd = ieWindow;
			//keyData.text = newValue;

			element->scrollIntoView(CComVariant(VARIANT_TRUE));

			//CComQIPtr<IHTMLInputFileElement> file(element);
			//if (file) {
			//	DWORD threadId;
			//	tryTransferEventReleaserToNotifyNavigCompleted(&SC);
			//	keyData.hdl_EventToNotifyWhenNavigationCompleted = m_EventToNotifyWhenNavigationCompleted;
			//	::CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE) setFileValue, (void *) &keyData, 0, &threadId);

			//	element->click();
			//	// We're now blocked until the dialog closes.
			//	return;
			//}

			CComQIPtr<IHTMLElement2> element2(element);
			element2->focus();

			// Check we have focused the element.
			CComPtr<IDispatch> dispatch;
			element->get_document(&dispatch);
			CComQIPtr<IHTMLDocument2> document(dispatch);

			bool hasFocus = false;
			clock_t maxWait = clock() + 1000;
			for (int i = clock(); i < maxWait; i = clock())
			{
				wait(1);
				CComPtr<IHTMLElement> activeElement;
				if (document->get_activeElement(&activeElement) == S_OK)
				{
					CComQIPtr<IHTMLElement2> activeElement2(activeElement);
					if (element2.IsEqualObject(activeElement2))
					{
						hasFocus = true;
						break;
					}
				}
			}

			if (!hasFocus)
			{
				//cerr << "We don't have focus on element." << endl;
			}

			sendKeys(hwnd, keys.c_str(), manager->GetSpeed());

			response->m_statusCode = statusCode;
		}
	}
};
