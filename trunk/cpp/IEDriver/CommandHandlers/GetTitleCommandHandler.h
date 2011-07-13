// Copyright 2011 WebDriver committers
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#ifndef WEBDRIVER_IE_GETTITLECOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETTITLECOMMANDHANDLER_H_

#include "../Browser.h"
#include "../IECommandHandler.h"
#include "../IECommandExecutor.h"

namespace webdriver {

class GetTitleCommandHandler : public IECommandHandler {
public:
	GetTitleCommandHandler(void) {
	}

	virtual ~GetTitleCommandHandler(void) {
	}

protected:
	void GetTitleCommandHandler::ExecuteInternal(const IECommandExecutor& executor, const LocatorMap& locator_parameters, const ParametersMap& command_parameters, Response * response) {
		BrowserHandle browser_wrapper;
		int status_code = executor.GetCurrentBrowser(&browser_wrapper);
		if (status_code != SUCCESS) {
			response->SetErrorResponse(status_code, "Unable to get browser");
			return;
		}
		std::string title = CW2A(browser_wrapper->GetTitle().c_str(), CP_UTF8);

		response->SetSuccessResponse(title);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETTITLECOMMANDHANDLER_H_
