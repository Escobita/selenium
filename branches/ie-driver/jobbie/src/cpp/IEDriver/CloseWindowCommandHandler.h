#ifndef WEBDRIVER_IE_CLOSEWINDOWCOMMANDHANDLER_H_
#define WEBDRIVER_IE_CLOSEWINDOWCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class CloseWindowCommandHandler : public WebDriverCommandHandler {
public:
	CloseWindowCommandHandler(void) {
	}

	virtual ~CloseWindowCommandHandler(void) {
	}

protected:
	void CloseWindowCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response)
	{
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);
		HRESULT hr = browser_wrapper->browser()->Quit();
		response->set_status_code(SUCCESS);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_CLOSEWINDOWCOMMANDHANDLER_H_
