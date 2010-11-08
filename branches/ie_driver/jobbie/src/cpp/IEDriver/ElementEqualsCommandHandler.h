#ifndef WEBDRIVER_IE_ELEMENTEQUALSCOMMANDHANDLER_H_
#define WEBDRIVER_IE_ELEMENTEQUALSCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class ElementEqualsCommandHandler : public WebDriverCommandHandler {
public:
	ElementEqualsCommandHandler(void) {
	}

	virtual ~ElementEqualsCommandHandler(void) {
	}

protected:
	void ElementEqualsCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		}
		else if (locator_parameters.find("other") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "other";
		} else {
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));
			std::wstring other_element_id(CA2W(locator_parameters["other"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS)
			{
				ElementWrapper *other_element_wrapper;
				status_code = this->GetElement(manager, other_element_id, &other_element_wrapper);
				if (status_code == SUCCESS) {
					response->m_value = (element_wrapper->element() == other_element_wrapper->element());
				} else {
					response->m_value["message"] = "Element is no longer valid";
				}
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}

			response->set_status_code(status_code);
		}

	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_ELEMENTEQUALSCOMMANDHANDLER_H_
