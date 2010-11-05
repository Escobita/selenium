#ifndef WEBDRIVER_IE_GETELEMENTVALUECOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETELEMENTVALUECOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetElementValueCommandHandler : public WebDriverCommandHandler {
public:
	GetElementValueCommandHandler(void) {
	}

	virtual ~GetElementValueCommandHandler(void) {
	}

protected:
	void GetElementValueCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
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
				CComVariant value_variant;
				status_code = element_wrapper->GetAttributeValue(browser_wrapper, L"value", &value_variant);
				if (status_code == SUCCESS) {
					std::wstring value(browser_wrapper->ConvertVariantToWString(&value_variant));
					std::string value_str(CW2A(value.c_str(), CP_UTF8));
					response->m_value = value_str;
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETELEMENTVALUECOMMANDHANDLER_H_
