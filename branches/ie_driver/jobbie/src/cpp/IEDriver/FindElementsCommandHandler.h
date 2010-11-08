#ifndef WEBDRIVER_IE_FINDELEMENTSCOMMANDHANDLER_H_
#define WEBDRIVER_IE_FINDELEMENTSCOMMANDHANDLER_H_

#include <ctime>
#include "BrowserManager.h"

namespace webdriver {

class FindElementsCommandHandler : public WebDriverCommandHandler {
public:
	FindElementsCommandHandler(void) {
	}

	virtual ~FindElementsCommandHandler(void) {
	}

protected:
	void FindElementsCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (command_parameters.find("using") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "using";
		} else if (command_parameters.find("value") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "value";
		} else {
			std::vector<ElementWrapper *> found_elements;
			std::wstring mechanism = CA2W(command_parameters["using"].asString().c_str(), CP_UTF8);
			std::wstring value = CA2W(command_parameters["value"].asString().c_str(), CP_UTF8);

			ElementFinder *finder;
			manager->GetElementFinder(mechanism, &finder);

			int timeout(manager->implicit_wait_timeout());
			clock_t end = clock() + (timeout / 1000 * CLOCKS_PER_SEC);
			if (timeout > 0 && timeout < 1000)  {
				end += 1 * CLOCKS_PER_SEC;
			}


			int status_code = SUCCESS;
			do {
				status_code = finder->FindElements(manager, NULL, value, &found_elements);
				if (status_code == SUCCESS && found_elements.size() > 0) {
					break;
				}
			} while (clock() < end);

			if (status_code == SUCCESS) {
				Json::Value element_array(Json::arrayValue);
				for (int i = 0; i < found_elements.size(); ++i) {
					element_array[i] = found_elements[i]->ConvertToJson();
				}

				response->m_value = element_array;
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_FINDELEMENTSCOMMANDHANDLER_H_
