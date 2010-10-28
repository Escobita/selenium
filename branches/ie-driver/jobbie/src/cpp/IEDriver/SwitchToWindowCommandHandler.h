#pragma once
#include "BrowserManager.h"

class SwitchToWindowCommandHandler :
	public WebDriverCommandHandler
{
public:

	SwitchToWindowCommandHandler(void)
	{
	}

	virtual ~SwitchToWindowCommandHandler(void)
	{
	}

protected:
	std::string GetWindowName(IWebBrowser2* browser)
	{
		CComPtr<IDispatch> dispatch;
		HRESULT hr = browser->get_Document(&dispatch);
		if (FAILED(hr)) {
			return false;
		}
		CComQIPtr<IHTMLDocument2> doc(dispatch);
		if (!doc) {
			return false;
		}

		CComPtr<IHTMLWindow2> window;
		hr = doc->get_parentWindow(&window);
		if (FAILED(hr)) {
			return false;
		}

		std::string name("");
		CComBSTR windowName;
		hr = window->get_name(&windowName);
		if (windowName)
		{
			name = CW2A((BSTR)windowName);
		}
		return name;
	}

	void ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("name") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "name";
		}
		else
		{
			std::wstring foundBrowserHandle = L"";
			std::string desiredName = commandParameters["name"].asString();
			std::map<std::wstring, BrowserWrapper*>::iterator end = manager->m_trackedBrowsers.end();
			for (std::map<std::wstring, BrowserWrapper*>::iterator it = manager->m_trackedBrowsers.begin(); it != end; ++it)
			{
				std::string browserName = this->GetWindowName(it->second->m_pBrowser);
				if (browserName == desiredName)
				{
					foundBrowserHandle = it->first;
					break;
				}

				std::string browserHandle = CW2A(it->first.c_str());
				if (browserHandle == desiredName)
				{
					foundBrowserHandle = it->first;
					break;
				}
			}

			if (foundBrowserHandle == L"")
			{
				response->m_statusCode = ENOSUCHWINDOW;
			}
			else
			{
				// Reset the path to the focused frame before switching window context.
				BrowserWrapper *pWrapper;
				manager->GetCurrentBrowser(&pWrapper);
				pWrapper->m_pathToFrame = L"";

				manager->m_currentBrowser = foundBrowserHandle;
			}
		}
	}
};
