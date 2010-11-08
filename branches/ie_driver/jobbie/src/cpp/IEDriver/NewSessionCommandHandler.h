#ifndef WEBDRIVER_IE_NEWSESSIONCOMMANDHANDLER_H_
#define WEBDRIVER_IE_NEWSESSIONCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class NewSessionCommandHandler : public WebDriverCommandHandler {
public:
	NewSessionCommandHandler(void) {
	}

	virtual ~NewSessionCommandHandler(void) {
	}

protected:
	void NewSessionCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		manager->CreateNewBrowser();
		response->set_status_code(303);
		std::string id = CW2A(manager->manager_id().c_str(), CP_UTF8);
		response->m_value = "/session/" + id;
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_NEWSESSIONCOMMANDHANDLER_H_
