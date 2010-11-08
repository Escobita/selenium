#ifndef WEBDRIVER_IE_GETSPEEDCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETSPEEDCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetSpeedCommandHandler : public WebDriverCommandHandler {
public:
	GetSpeedCommandHandler(void) {
	}

	virtual ~GetSpeedCommandHandler(void) {
	}

protected:
	void GetSpeedCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		int speed = manager->speed();
		switch (speed) {
		  case 1000:
			response->m_value = "SLOW";
			break;
		  case 500:
			response->m_value = "MEDIUM";
			break;
		  default:
			response->m_value = "FAST";
			break;
		}
		response->set_status_code(SUCCESS);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE__H_
