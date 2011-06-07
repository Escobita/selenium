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

#ifndef WEBDRIVER_IE_GETSESSIONCAPABILITIESCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETSESSIONCAPABILITIESCOMMANDHANDLER_H_

#include "Session.h"

namespace webdriver {

class GetSessionCapabilitiesCommandHandler : public CommandHandler {
public:
	GetSessionCapabilitiesCommandHandler(void) {
	}

	virtual ~GetSessionCapabilitiesCommandHandler(void) {
	}

protected:
	void GetSessionCapabilitiesCommandHandler::ExecuteInternal(const IESessionWindow& session, const LocatorMap& locator_parameters, const ParametersMap& command_parameters, Response * response) {
		// ASSUMPTION: Version string will never be larger than 2 characters
		// (+1 for the null terminator).
		int version = session.browser_version();
		char buffer[3];
		_itoa_s(version, buffer, 3, 10);
		std::string version_string = buffer;

		Json::Value capabilities;
		capabilities["browserName"] = "internet explorer";
		capabilities["version"] = version_string;
		capabilities["javascriptEnabled"] = true;
		capabilities["platform"] = "WINDOWS";
		capabilities["nativeEvents"] = true;
		capabilities["cssSelectorsEnabled"] = true;
		capabilities["takesScreenshot"] = true;
		response->SetResponse(SUCCESS, capabilities);
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETSESSIONCAPABILITIESCOMMANDHANDLER_H_
