#ifndef WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_
#define WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_

#include "Session.h"

namespace webdriver {

class SwitchToFrameCommandHandler : public WebDriverCommandHandler {
public:
	SwitchToFrameCommandHandler(void) {
	}

	virtual ~SwitchToFrameCommandHandler(void) {
	}

protected:
	void SwitchToFrameCommandHandler::ExecuteInternal(Session* session, const LocatorMap& locator_parameters, const ParametersMap& command_parameters, WebDriverResponse * response) {
		Json::Value frame_id = Json::Value::null;
		ParametersMap::const_iterator it = command_parameters.find("id");
		// TODO: When issue 1133 is fixed, the else block in the following code
		// should be uncommented.
		if (it != command_parameters.end()) {
			frame_id = it->second;
		//} else {
		//	response->SetErrorResponse(400, "Missing parameter: id");
		//	return;
		}
		BrowserHandle browser_wrapper;
		int status_code = session->GetCurrentBrowser(&browser_wrapper);
		if (status_code != SUCCESS) {
			response->SetErrorResponse(status_code, "Unable to get browser");
			return;
		}

		if (frame_id.isNull()) {
			status_code = browser_wrapper->SetFocusedFrameByElement(NULL);
		} else if (frame_id.isObject()) {
			Json::Value element_id = frame_id.get("ELEMENT", Json::Value::null);
			if (element_id.isNull()) {
				status_code = ENOSUCHFRAME;
			} else {
				std::wstring frame_element_id(CA2W(element_id.asString().c_str(), CP_UTF8));

				ElementHandle frame_element_wrapper;
				status_code = this->GetElement(session, frame_element_id, &frame_element_wrapper);
				if (status_code == SUCCESS) {
					status_code = browser_wrapper->SetFocusedFrameByElement(frame_element_wrapper->element());
				}
			}
		} else if (frame_id.isString()) {
			std::wstring frame_name(CA2W(frame_id.asString().c_str(), CP_UTF8));
			status_code = browser_wrapper->SetFocusedFrameByName(frame_name);
		} else if(frame_id.isIntegral()) {
			int frame_index(frame_id.asInt());
			status_code = browser_wrapper->SetFocusedFrameByIndex(frame_index);
		}

		if (status_code != SUCCESS) {
			response->SetErrorResponse(status_code, "No frame found");
		} else {
			response->SetResponse(SUCCESS, Json::Value::null);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SWITCHTOFRAMECOMMANDHANDLER_H_
