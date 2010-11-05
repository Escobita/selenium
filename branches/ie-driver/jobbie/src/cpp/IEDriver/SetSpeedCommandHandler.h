#ifndef WEBDRIVER_IE_SETSPEEDCOMMANDHANDLER_H_
#define WEBDRIVER_IE_SETSPEEDCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class SetSpeedCommandHandler : public WebDriverCommandHandler {
public:
	SetSpeedCommandHandler(void) {
	}

	virtual ~SetSpeedCommandHandler(void) {
	}

protected:
	void SetSpeedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (command_parameters.find("speed") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "speed";
		} else {
			std::string speed = command_parameters["speed"].asString();
			if (strcmp(speed.c_str(), SPEED_SLOW) == 0) {
				manager->set_speed(1000);
			} else if (strcmp(speed.c_str(), SPEED_MEDIUM) == 0) {
				manager->set_speed(500);
			} else {
				manager->set_speed(0);
			}
			response->set_status_code(SUCCESS);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SETSPEEDCOMMANDHANDLER_H_
