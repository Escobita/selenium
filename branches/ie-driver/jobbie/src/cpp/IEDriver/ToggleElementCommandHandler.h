#pragma once
#include "BrowserManager.h"

class ToggleElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	ToggleElementCommandHandler(void)
	{
	}

	virtual ~ToggleElementCommandHandler(void)
	{
	}

protected:

	void ToggleElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
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
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);
			HWND hwnd = pBrowserWrapper->GetHwnd();

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				// It only makes sense to toggle check boxes or options in a multi-select
				CComBSTR tagName;
				HRESULT hr = pElementWrapper->m_pElement->get_tagName(&tagName);
				if (FAILED(hr)) {
					// LOGHR(WARN, hr) << "Unable to get tag name";
					response->m_statusCode = ENOSUCHELEMENT;
					return;
				}

				if ((tagName != L"OPTION") && !pElementWrapper->IsCheckBox()) 
				{
					response->m_statusCode = ENOTIMPLEMENTED;
					return;
				}

				int statusCode = pElementWrapper->Click(hwnd);
				if (statusCode == SUCCESS || statusCode != EELEMENTNOTDISPLAYED) {
					response->m_statusCode = statusCode;
					return;
				} 

				if (tagName == L"OPTION") {
					CComQIPtr<IHTMLOptionElement> option(pElementWrapper->m_pElement);
					if (!option) {
						//LOG(ERROR) << "Cannot convert an element to an option, even though the tag name is right";
						response->m_statusCode = ENOSUCHELEMENT;
						return;
					}

					VARIANT_BOOL selected;
					hr = option->get_selected(&selected);
					if (FAILED(hr)) {
						//LOGHR(WARN, hr) << "Cannot tell whether or not the element is selected";
						response->m_statusCode = ENOSUCHELEMENT;
						return;
					}

					if (selected == VARIANT_TRUE) {
						hr = option->put_selected(VARIANT_FALSE);
					} else {
						hr = option->put_selected(VARIANT_TRUE);
					}
					if (FAILED(hr)) {
						//LOGHR(WARN, hr) << "Failed to set selection";
						response->m_statusCode = EEXPECTEDERROR;
						return;
					}

					//Looks like we'll need to fire the event on the select element and not the option. Assume for now that the parent node is a select. Which is dumb
					CComQIPtr<IHTMLDOMNode> node(pElementWrapper->m_pElement);
					if (!node) {
						//LOG(WARN) << "Current element is not an DOM node";
						response->m_statusCode = ENOSUCHELEMENT;
						return;
					}
					CComPtr<IHTMLDOMNode> parent;
					hr = node->get_parentNode(&parent);
					if (FAILED(hr)) {
						//LOGHR(WARN, hr) << "Cannot get parent node";
						response->m_statusCode = ENOSUCHELEMENT;
						return;
					}

					pElementWrapper->FireEvent(parent, L"onchange");
					statusCode = SUCCESS;
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
