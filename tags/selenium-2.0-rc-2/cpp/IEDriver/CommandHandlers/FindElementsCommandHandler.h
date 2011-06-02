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

#ifndef WEBDRIVER_IE_FINDELEMENTSCOMMANDHANDLER_H_
#define WEBDRIVER_IE_FINDELEMENTSCOMMANDHANDLER_H_

#include <ctime>
#include "Session.h"

namespace webdriver {

class FindElementsCommandHandler : public CommandHandler {
public:
	FindElementsCommandHandler(void) {
	}

	virtual ~FindElementsCommandHandler(void) {
	}

protected:
	void FindElementsCommandHandler::ExecuteInternal(const Session& session, const LocatorMap& locator_parameters, const ParametersMap& command_parameters, Response * response) {
		ParametersMap::const_iterator using_parameter_iterator = command_parameters.find("using");
		ParametersMap::const_iterator value_parameter_iterator = command_parameters.find("value");
		if (using_parameter_iterator == command_parameters.end()) {
			response->SetErrorResponse(400, "Missing parameter: using");
			return;
		} else if (value_parameter_iterator == command_parameters.end()) {
			response->SetErrorResponse(400, "Missing parameter: value");
			return;
		} else {
			std::wstring mechanism = CA2W(using_parameter_iterator->second.asString().c_str(), CP_UTF8);
			std::wstring value = CA2W(value_parameter_iterator->second.asString().c_str(), CP_UTF8);

			std::wstring mechanism_translation;
			int status_code = session.GetElementFindMethod(mechanism, &mechanism_translation);
			if (status_code != SUCCESS) {
				response->SetErrorResponse(status_code, "Unknown finder mechanism: " + using_parameter_iterator->second.asString());
				return;
			}

			int timeout = session.implicit_wait_timeout();
			clock_t end = clock() + (timeout / 1000 * CLOCKS_PER_SEC);
			if (timeout > 0 && timeout < 1000)  {
				end += 1 * CLOCKS_PER_SEC;
			}

			Json::Value found_elements(Json::arrayValue);
			do {
				status_code = session.LocateElements(ElementHandle(), mechanism_translation, value, &found_elements);
				if (status_code == SUCCESS && found_elements.size() > 0) {
					break;
				} else {
					// Release the thread so that the browser doesn't starve.
					::Sleep(FIND_ELEMENT_WAIT_TIME_IN_MILLISECONDS);
				}
			} while (clock() < end);

			if (status_code == SUCCESS) {
				response->SetResponse(SUCCESS, found_elements);
				return;
			}
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_FINDELEMENTSCOMMANDHANDLER_H_
