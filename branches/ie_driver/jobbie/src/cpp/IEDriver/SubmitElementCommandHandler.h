#ifndef WEBDRIVER_IE_SUBMITELEMENTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_SUBMITELEMENTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class SubmitElementCommandHandler : public WebDriverCommandHandler {
public:
	SubmitElementCommandHandler(void) {
	}

	virtual ~SubmitElementCommandHandler(void) {
	}

private:
	void SubmitElementCommandHandler::FindParentForm(IHTMLElement *element, IHTMLFormElement **form_element) {
		CComQIPtr<IHTMLElement> current(element);

		while (current) {
			CComQIPtr<IHTMLFormElement> form(current);
			if (form) {
				*form_element = form.Detach();
				return;
			}

			CComPtr<IHTMLElement> temp;
			current->get_parentElement(&temp);
			current = temp;
		}
	}

protected:
	void SubmitElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else {
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				CComQIPtr<IHTMLFormElement> form(element_wrapper->element());
				if (form) {
					form->submit();
				} else {
					CComQIPtr<IHTMLInputElement> input(element_wrapper->element());
					if (input) {
						CComBSTR type_name;
						input->get_type(&type_name);

						std::wstring type((BSTR)type_name);

						if (_wcsicmp(L"submit", type.c_str()) == 0 || _wcsicmp(L"image", type.c_str()) == 0) {
							HWND hwnd = browser_wrapper->GetWindowHandle();
							element_wrapper->Click(hwnd);
							browser_wrapper->set_wait_required(true);
						} else {
							CComPtr<IHTMLFormElement> form2;
							input->get_form(&form2);
							form2->submit();
						}
					} else {
						this->FindParentForm(element_wrapper->element(), &form);
						if (!form) {
							status_code = EUNHANDLEDERROR;
							response->m_value = "Unable to find the containing form";
						}
						form->submit();
					}
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}
			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SUBMITELEMENTCOMMANDHANDLER_H_
