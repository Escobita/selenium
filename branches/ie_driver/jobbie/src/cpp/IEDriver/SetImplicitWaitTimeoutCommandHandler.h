#ifndef WEBDRIVER_IE_SETIMPLICITWAITTIMEOUTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_SETIMPLICITWAITTIMEOUTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class SetImplicitWaitTimeoutCommandHandler : public WebDriverCommandHandler {
public:
	SetImplicitWaitTimeoutCommandHandler(void) {
	}

	virtual ~SetImplicitWaitTimeoutCommandHandler(void) {
	}

protected:
	void SetImplicitWaitTimeoutCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (command_parameters.find("ms") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "ms";
		} else {
			int timeout = command_parameters["ms"].asInt();
			manager->set_implicit_wait_timeout(timeout);
			response->set_status_code(SUCCESS);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SETIMPLICITWAITTIMEOUTCOMMANDHANDLER_H_
