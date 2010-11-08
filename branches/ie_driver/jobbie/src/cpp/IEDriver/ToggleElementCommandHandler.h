#ifndef WEBDRIVER_IE_TOGGLEELEMENTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_TOGGLEELEMENTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class ToggleElementCommandHandler : public WebDriverCommandHandler {
public:
	ToggleElementCommandHandler(void) {
	}

	virtual ~ToggleElementCommandHandler(void) {
	}

protected:
	void ToggleElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else {
			std::wstring text(L"");
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			HWND hwnd = browser_wrapper->GetWindowHandle();

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				// It only makes sense to toggle check boxes or options in a multi-select
				CComBSTR tag_name;
				HRESULT hr = element_wrapper->element()->get_tagName(&tag_name);
				if (FAILED(hr)) {
					// LOGHR(WARN, hr) << "Unable to get tag name";
					response->set_status_code(ENOSUCHELEMENT);
					return;
				}

				if ((tag_name != L"OPTION") && !element_wrapper->IsCheckBox())  {
					response->set_status_code(ENOTIMPLEMENTED);
					response->m_value["message"] = "cannot toggle element that is not an option or check box";
					return;
				}

				status_code = element_wrapper->Click(hwnd);
				browser_wrapper->set_wait_required(true);
				if (status_code == SUCCESS || status_code != EELEMENTNOTDISPLAYED) {
					response->set_status_code(status_code);
					if (status_code == SUCCESS) {
						response->m_value = element_wrapper->IsSelected();
					} else {
						response->m_value["message"] = "cannot toggle element";
					}
					return;
				} 

				if (tag_name == L"OPTION") {
					CComQIPtr<IHTMLOptionElement> option(element_wrapper->element());
					if (!option) {
						//LOG(ERROR) << "Cannot convert an element to an option, even though the tag name is right";
						response->set_status_code(ENOSUCHELEMENT);
						return;
					}

					VARIANT_BOOL selected;
					hr = option->get_selected(&selected);
					if (FAILED(hr)) {
						//LOGHR(WARN, hr) << "Cannot tell whether or not the element is selected";
						response->set_status_code(ENOSUCHELEMENT);
						return;
					}

					if (selected == VARIANT_TRUE) {
						hr = option->put_selected(VARIANT_FALSE);
					} else {
						hr = option->put_selected(VARIANT_TRUE);
					}

					if (FAILED(hr)) {
						//LOGHR(WARN, hr) << "Failed to set selection";
						response->set_status_code(EEXPECTEDERROR);
						return;
					}

					//Looks like we'll need to fire the event on the select element and not the option. Assume for now that the parent node is a select. Which is dumb
					CComQIPtr<IHTMLDOMNode> node(element_wrapper->element());
					if (!node) {
						//LOG(WARN) << "Current element is not an DOM node";
						response->set_status_code(ENOSUCHELEMENT);
						return;
					}
					CComPtr<IHTMLDOMNode> parent;
					hr = node->get_parentNode(&parent);
					if (FAILED(hr)) {
						//LOGHR(WARN, hr) << "Cannot get parent node";
						response->set_status_code(ENOSUCHELEMENT);
						return;
					}

					element_wrapper->FireEvent(parent, L"onchange");
					status_code = SUCCESS;
					response->m_value = element_wrapper->IsSelected();
				} else {
					// Element is not an OPTION element, and it's not visible.
					response->set_status_code(status_code);
					response->m_value["message"] = "cannot toggle invisible element";
					return;
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_TOGGLEELEMENTCOMMANDHANDLER_H_
