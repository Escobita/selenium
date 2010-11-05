#ifndef WEBDRIVER_IE_ADDCOOKIECOMMANDHANDLER_H_
#define WEBDRIVER_IE_ADDCOOKIECOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class AddCookieCommandHandler : public WebDriverCommandHandler {
public:
	AddCookieCommandHandler(void) {
	}

	virtual ~AddCookieCommandHandler(void) {
	}

protected:
	void AddCookieCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response)
	{
		if (command_parameters.find("cookie") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "cookie";
		}

		Json::Value cookie_value = command_parameters["cookie"];
		std::string cookie_string(cookie_value["name"].asString() + "=" + cookie_value["value"].asString() + "; ");
		cookie_value.removeMember("name");
		cookie_value.removeMember("value");

		bool is_secure(cookie_value["secure"].asBool());
		if (is_secure) {
			cookie_string += "secure; ";
		}
		cookie_value.removeMember("secure");

		Json::Value::iterator it = cookie_value.begin();
		for (; it != cookie_value.end(); ++it) {
			std::string key = it.key().asString();
			std::string value = cookie_value[key].asString();
			if (value != "") {
				cookie_string += key + "=" + cookie_value[key].asString() + "; ";
			}
		}

		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);

		std::wstring cookie(CA2W(cookie_string.c_str(), CP_UTF8));
		int status_code = browser_wrapper->AddCookie(cookie);
		if (status_code != SUCCESS) {
			response->m_value["message"] = L"Unable to add cookie to page";
		}

		response->set_status_code(status_code);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_ADDCOOKIECOMMANDHANDLER_H_