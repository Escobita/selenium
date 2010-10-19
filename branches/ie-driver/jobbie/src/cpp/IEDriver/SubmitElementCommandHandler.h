#pragma once
#include "BrowserManager.h"

class SubmitElementCommandHandler :
	public WebDriverCommandHandler
{
public:

	SubmitElementCommandHandler(void)
	{
	}

	virtual ~SubmitElementCommandHandler(void)
	{
	}

private:
	void SubmitElementCommandHandler::FindParentForm(IHTMLElement *pElement, IHTMLFormElement **pform)
	{
		CComQIPtr<IHTMLElement> current(pElement);

		while (current) {
			CComQIPtr<IHTMLFormElement> form(current);
			if (form) {
				*pform = form.Detach();
				return;
			}

			CComPtr<IHTMLElement> temp;
			current->get_parentElement(&temp);
			current = temp;
		}
	}

protected:

	void SubmitElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				CComQIPtr<IHTMLFormElement> form(pElementWrapper->m_pElement);
				if (form) {
					form->submit();
				} else {
					CComQIPtr<IHTMLInputElement> input(pElementWrapper->m_pElement);
					if (input) {
						CComBSTR typeName;
						input->get_type(&typeName);

						std::wstring type((BSTR)typeName);

						if (_wcsicmp(L"submit", type.c_str()) == 0 || _wcsicmp(L"image", type.c_str()) == 0) {
							HWND hwnd = pBrowserWrapper->GetHwnd();
							pElementWrapper->Click(hwnd);
						} else {
							CComPtr<IHTMLFormElement> form2;
							input->get_form(&form2);
							form2->submit();
						}
					} else {
						this->FindParentForm(pElementWrapper->m_pElement, &form);
						if (!form) {
							statusCode = EUNHANDLEDERROR;
							response->m_value = "Unable to find the containing form";
						}
						form->submit();
					}
				}
			}
			response->m_statusCode = statusCode;
		}
	}
};
