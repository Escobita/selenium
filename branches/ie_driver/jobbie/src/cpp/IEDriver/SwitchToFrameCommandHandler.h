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
		if (command_parameters.find("id") == command_parameters.end()) {
			response->SetErrorResponse(400, "Missing parameter: id");
			return;
		} else {
			Json::Value frame_id = command_parameters["id"];
			BrowserWrapper *browser_wrapper;
			int status_code = manager->GetCurrentBrowser(&browser_wrapper);
			if (status_code != SUCCESS) {
				response->SetErrorResponse(status_code, "Unable to get browser");
				return;
			}

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
				response->SetErrorResponse(ENOSUCHFRAME, "No frame found");
				return;
			} else {
				response->SetResponse(SUCCESS, Json::Value::null);
				return;
			}
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_
