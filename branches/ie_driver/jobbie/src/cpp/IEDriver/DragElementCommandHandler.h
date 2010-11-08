#ifndef WEBDRIVER_IE_DRAGELEMENTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_DRAGELEMENTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class DragElementCommandHandler : public WebDriverCommandHandler {
public:
	DragElementCommandHandler(void) {
	}

	virtual ~DragElementCommandHandler(void) {
	}

protected:
	void DragElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else if (command_parameters.find("x") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "x";
		} else if (command_parameters.find("y") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "y";
		} else {
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));

			int x = command_parameters["x"].asInt();
			int y = command_parameters["y"].asInt();

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			HWND window_handle = browser_wrapper->GetWindowHandle();

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				element_wrapper->DragBy(window_handle, x, y, manager->speed());
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}
			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_DRAGELEMENTCOMMANDHANDLER_H_
