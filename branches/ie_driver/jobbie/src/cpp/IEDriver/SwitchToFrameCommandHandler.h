#ifndef WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_
#define WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class SwitchToFrameCommandHandler : public WebDriverCommandHandler {
public:
	SwitchToFrameCommandHandler(void) {
	}

	virtual ~SwitchToFrameCommandHandler(void) {
	}

protected:
	void SwitchToFrameCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		Json::Value frame_id = command_parameters["id"];
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);
		std::wstringstream path_stream;
		if (frame_id.isString()) {
			std::wstring path(CA2W(frame_id.asString().c_str(), CP_UTF8)); 
			path_stream << path;
		} else if(frame_id.isIntegral()) {
			path_stream << frame_id.asInt();
		}

		browser_wrapper->set_path_to_frame(path_stream.str());
		CComPtr<IHTMLDocument2> doc;
		browser_wrapper->GetDocument(&doc);
		if (!doc) {
			browser_wrapper->set_path_to_frame(L"");
			response->set_status_code(ENOSUCHFRAME);
			response->m_value["message"] = "No frame found";
		} else {
			response->set_status_code(SUCCESS);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_
