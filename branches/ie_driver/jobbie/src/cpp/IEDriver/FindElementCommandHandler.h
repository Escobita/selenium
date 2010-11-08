#ifndef WEBDRIVER_IE_FINDELEMENTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_FINDELEMENTCOMMANDHANDLER_H_

#include <ctime>
#include "BrowserManager.h"

namespace webdriver {

class FindElementCommandHandler : public WebDriverCommandHandler {
public:
	FindElementCommandHandler(void) {
	}

	virtual ~FindElementCommandHandler(void) {
	}

protected:
	void FindElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (command_parameters.find("using") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "using";
		} else if (command_parameters.find("value") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "value";
		} else {
			ElementWrapper *found_element;
			std::wstring mechanism = CA2W(command_parameters["using"].asString().c_str(), CP_UTF8);
			std::wstring value = CA2W(command_parameters["value"].asString().c_str(), CP_UTF8);

			ElementFinder *finder;
			manager->GetElementFinder(mechanism, &finder);

			int timeout(manager->implicit_wait_timeout());
			clock_t end = clock() + (timeout / 1000 * CLOCKS_PER_SEC);
			if (timeout > 0 && timeout < 1000) {
				end += 1 * CLOCKS_PER_SEC;
			}

			int status_code = SUCCESS;
			do {
				status_code = finder->FindElement(manager, NULL, value, &found_element);
				if (status_code == SUCCESS) {
					break;
				}
			} while (clock() < end);
			
			if (status_code == SUCCESS) {
				response->m_value = found_element->ConvertToJson();
			} else 			{
				response->m_value["message"] = "Unable to find element with " + command_parameters["using"].asString() + " " + command_parameters["value"].asString();
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_FINDELEMENTCOMMANDHANDLER_H_
