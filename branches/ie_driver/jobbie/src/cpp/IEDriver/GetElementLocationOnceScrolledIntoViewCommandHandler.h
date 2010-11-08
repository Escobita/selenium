#pragma once
#include "BrowserManager.h"

namespace webdriver {

class GetElementLocationOnceScrolledIntoViewCommandHandler : public WebDriverCommandHandler {
public:
	GetElementLocationOnceScrolledIntoViewCommandHandler(void) {
	}

	virtual ~GetElementLocationOnceScrolledIntoViewCommandHandler(void) {
	}

protected:
	void GetElementLocationOnceScrolledIntoViewCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else {
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			HWND window_handle = browser_wrapper->GetWindowHandle();

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				long x, y, width, height;
				status_code = element_wrapper->GetLocationOnceScrolledIntoView(window_handle, &x, &y, &width, &height);
				if (status_code == SUCCESS) {
					response->m_value["x"] = x;
					response->m_value["y"] = y;
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver