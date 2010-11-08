#ifndef WEBDRIVER_IE_GETELEMENTTEXTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETELEMENTTEXTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetElementTextCommandHandler : public WebDriverCommandHandler {
public:
	GetElementTextCommandHandler(void) {
	}

	~GetElementTextCommandHandler(void) {
	}

protected:
	void GetElementTextCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
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
				CComBSTR tag_name;
				element_wrapper->element()->get_tagName(&tag_name);
				bool is_title = tag_name == L"TITLE";

				if (is_title) {
					text = browser_wrapper->GetTitle();
					std::string title(CW2A(text.c_str(), CP_UTF8));
					response->m_value = title;
				} else {
					text = element_wrapper->GetText();
					std::string element_text(CW2A(text.c_str(), CP_UTF8));
					response->m_value = element_text;
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETELEMENTTEXTCOMMANDHANDLER_H_
