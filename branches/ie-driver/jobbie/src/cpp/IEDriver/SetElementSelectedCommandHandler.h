#ifndef WEBDRIVER_IE_SETELEMENTSELECTEDCOMMANDHANDLER_H_
#define WEBDRIVER_IE_SETELEMENTSELECTEDCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class SetElementSelectedCommandHandler : public WebDriverCommandHandler {
public:
	SetElementSelectedCommandHandler(void) {
	}

	virtual ~SetElementSelectedCommandHandler(void) {
	}

protected:
	void SetElementSelectedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else {
			std::wstring text(L"");
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			HWND window_handle = browser_wrapper->GetWindowHandle();

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				bool currently_selected = element_wrapper->IsSelected();

				if (!element_wrapper->IsEnabled()) {
					status_code = EELEMENTNOTENABLED;
					response->m_value["message"] = "Cannot select disabled element";
				} else {
					bool displayed;
					status_code = element_wrapper->IsDisplayed(&displayed);
					if (status_code != SUCCESS || !displayed) {
						status_code = EELEMENTNOTDISPLAYED;
						response->m_value["message"] = "Cannot select hidden element";
					} else {
						/* TODO(malcolmr): Why not: if (isSelected()) return; ? Do we really need to
						   re-set 'checked=true' for checkbox and do effectively nothing for select?
						   Maybe we should check for disabled elements first? */

						if (element_wrapper->IsCheckBox()) {
							status_code = SUCCESS;
							response->m_value = Json::Value::null;
							if (!element_wrapper->IsSelected()) {
								element_wrapper->Click(window_handle);
								browser_wrapper->set_wait_required(true);
							}

							CComBSTR checked(L"checked");
							CComBSTR is_true(L"true");
							CComVariant is_checked(is_true);
							element_wrapper->element()->setAttribute(checked, is_checked, 0);

							if (currently_selected != element_wrapper->IsSelected()) {
								CComQIPtr<IHTMLDOMNode> check_box_node(element_wrapper->element());
								element_wrapper->FireEvent(check_box_node, L"onchange");
							}
						} else if (element_wrapper->IsRadioButton()) {
							status_code = SUCCESS;
							if (!element_wrapper->IsSelected()) {
								element_wrapper->Click(window_handle);
								browser_wrapper->set_wait_required(true);
							}

							CComBSTR selected(L"selected");
							CComBSTR is_true(L"true");
							CComVariant select(is_true);
							element_wrapper->element()->setAttribute(selected, select, 0);

							if (currently_selected != element_wrapper->IsSelected()) {
								CComQIPtr<IHTMLDOMNode> radio_button_node(element_wrapper->element());
								element_wrapper->FireEvent(radio_button_node, L"onchange");
							}
						} else {
							status_code = SUCCESS;
							CComQIPtr<IHTMLOptionElement> option(element_wrapper->element());
							if (option) {
								option->put_selected(VARIANT_TRUE);

								// Looks like we'll need to fire the event on the select element and not
								// the option. Assume for now that the parent node is a select. Which is dumb.
								CComQIPtr<IHTMLDOMNode> option_node(element_wrapper->element());
								CComPtr<IHTMLDOMNode> parent;
								option_node->get_parentNode(&parent);

								if (currently_selected != element_wrapper->IsSelected()) {
									element_wrapper->FireEvent(parent, L"onchange");
								}
							} else {
								status_code = EELEMENTNOTSELECTED;
								response->m_value["message"] = "Element type not selectable";
							}
						}
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

#endif // WEBDRIVER_IE_SETELEMENTSELECTEDCOMMANDHANDLER_H_
