#ifndef WEBDRIVER_IE_GOBACKCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GOBACKCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GoBackCommandHandler : public WebDriverCommandHandler {
public:
	GoBackCommandHandler(void) {
	}

	virtual ~GoBackCommandHandler(void) {
	}

protected:
	void GoBackCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);
		HRESULT hr = browser_wrapper->browser()->GoBack();
		response->set_status_code(SUCCESS);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GOBACKCOMMANDHANDLER_H_
