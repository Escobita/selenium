#ifndef WEBDRIVER_IE_GOTOURLCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GOTOURLCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GoToUrlCommandHandler : public WebDriverCommandHandler {
public:
	GoToUrlCommandHandler(void) {
	}

	virtual ~GoToUrlCommandHandler(void) {
	}

protected:
	void GoToUrlCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (command_parameters.find("url") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "url";
		} else {
			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			std::string url = command_parameters["url"].asString();
			CComVariant url_variant(url.c_str());
			CComVariant dummy;

			HRESULT hr = browser_wrapper->browser()->Navigate2(&url_variant, &dummy, &dummy, &dummy, &dummy);
			browser_wrapper->set_wait_required(true);

			browser_wrapper->set_path_to_frame(L"");
			response->set_status_code(SUCCESS);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GOTOURLCOMMANDHANDLER_H_
