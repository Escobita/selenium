#ifndef WEBDRIVER_IE_DELETECOOKIECOMMANDHANDLER_H_
#define WEBDRIVER_IE_DELETECOOKIECOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class DeleteCookieCommandHandler : public WebDriverCommandHandler {
public:
	DeleteCookieCommandHandler(void) {
	}

	virtual ~DeleteCookieCommandHandler(void) {
	}

protected:
	void DeleteCookieCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("name") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "name";
		}

		std::wstring cookie_name(CA2W(locator_parameters["name"].c_str(), CP_UTF8));
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);
		int status_code = browser_wrapper->DeleteCookie(cookie_name);
		if (status_code != SUCCESS) {
			response->m_value["message"] = "Unable to delete cookie";
		}

		response->set_status_code(status_code);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_DELETECOOKIECOMMANDHANDLER_H_
