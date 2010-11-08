#ifndef WEBDRIVER_IE_GOFORWARDCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GOFORWARDCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GoForwardCommandHandler : public WebDriverCommandHandler {
public:
	GoForwardCommandHandler(void) {
	}

	virtual ~GoForwardCommandHandler(void) {
	}

protected:
	void GoForwardCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);
		HRESULT hr = browser_wrapper->browser()->GoForward();
		response->set_status_code(SUCCESS);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GOFORWARDCOMMANDHANDLER_H_
