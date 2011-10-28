// Copyright 2011 Software Freedom Conservatory
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

#ifndef WEBDRIVER_IE_SENDKEYSTOACTIVEELEMENTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_SENDKEYSTOACTIVEELEMENTCOMMANDHANDLER_H_

#include "../Browser.h"
#include "../IECommandHandler.h"
#include "../IECommandExecutor.h"
#include "interactions.h"

namespace webdriver {

class SendKeysToActiveElementCommandHandler : public IECommandHandler {
 public:
  SendKeysToActiveElementCommandHandler(void) {
  }

  virtual ~SendKeysToActiveElementCommandHandler(void) {
  }

 protected:
  void ExecuteInternal(const IECommandExecutor& executor,
                       const LocatorMap& locator_parameters,
                       const ParametersMap& command_parameters,
                       Response* response) {
    ParametersMap::const_iterator value_parameter_iterator = command_parameters.find("value");
    if (value_parameter_iterator == command_parameters.end()) {
      response->SetErrorResponse(400, "Missing parameter: value");
      return;
    } else {
      std::wstring keys = L"";
      Json::Value key_array = value_parameter_iterator->second;
      for (unsigned int i = 0; i < key_array.size(); ++i ) {
        std::string key(key_array[i].asString());
        keys.append(CA2W(key.c_str(), CP_UTF8));
      }
      BrowserHandle browser_wrapper;
      executor.GetCurrentBrowser(&browser_wrapper);
      HWND window_handle = browser_wrapper->GetWindowHandle();
      sendKeys(window_handle, keys.c_str(), executor.speed());

      response->SetSuccessResponse(Json::Value::null);
    }
  }
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SENDKEYSTOACTIVEELEMENTCOMMANDHANDLER_H_

