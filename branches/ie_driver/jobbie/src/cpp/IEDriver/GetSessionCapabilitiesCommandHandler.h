#ifndef WEBDRIVER_IE_GETSESSIONCAPABILITIESCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETSESSIONCAPABILITIESCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetSessionCapabilitiesCommandHandler : public WebDriverCommandHandler {
public:
	GetSessionCapabilitiesCommandHandler(void) {
	}

	virtual ~GetSessionCapabilitiesCommandHandler(void) {
	}

protected:
	void GetSessionCapabilitiesCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		response->m_value["browserName"] = "internet explorer";
		response->m_value["version"] = "0";
		response->m_value["javascriptEnabled"] = true;
		response->m_value["platform"] = "WINDOWS";
		response->m_value["nativeEvents"] = true;
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETSESSIONCAPABILITIESCOMMANDHANDLER_H_
