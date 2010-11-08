#ifndef WEBDRIVER_IE_GETELEMENTATTRIBUTECOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETELEMENTATTRIBUTECOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetElementAttributeCommandHandler : public WebDriverCommandHandler {
public:
	GetElementAttributeCommandHandler(void) {
	}

	virtual ~GetElementAttributeCommandHandler(void) {
	}

protected:
	void GetElementAttributeCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else if (locator_parameters.find("name") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "name";
		} else {
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));
			std::wstring name(CA2W(locator_parameters["name"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				CComVariant value_variant;
				status_code = element_wrapper->GetAttributeValue(browser_wrapper, name, &value_variant);
				if (value_variant.vt != VT_EMPTY && value_variant.vt != VT_NULL) {
					std::wstring value(browser_wrapper->ConvertVariantToWString(&value_variant));
					std::string value_str(CW2A(value.c_str(), CP_UTF8));
					response->m_value = value_str;
				} else {
					response->m_value = Json::Value::null;
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}
			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETELEMENTATTRIBUTECOMMANDHANDLER_H_
