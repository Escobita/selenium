#ifndef WEBDRIVER_IE_GETCURRENTURLCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETCURRENTURLCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetCurrentUrlCommandHandler : public WebDriverCommandHandler {
public:
	GetCurrentUrlCommandHandler(void) {
	}

	virtual ~GetCurrentUrlCommandHandler(void) {
	}

protected:
	void GetCurrentUrlCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);

		CComPtr<IHTMLDocument2> doc;
		browser_wrapper->GetDocument(&doc);

		if (!doc) {
			response->m_value = "";
			return;
		}

		CComBSTR url;
		HRESULT hr = doc->get_URL(&url);
		if (FAILED(hr)) {
			//LOGHR(WARN, hr) << "Unable to get current URL";
			response->m_value = "";
			return;
		}

		std::string url_str = CW2A((LPCWSTR)url, CP_UTF8);
		response->m_value = url_str;
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETCURRENTURLCOMMANDHANDLER_H_
