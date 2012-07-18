// Copyright 2011 Software Freedom Conservancy
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

#ifndef WEBDRIVER_IE_NEWSESSIONCOMMANDHANDLER_H_
#define WEBDRIVER_IE_NEWSESSIONCOMMANDHANDLER_H_

#include "../Browser.h"
#include "../IECommandHandler.h"
#include "../IECommandExecutor.h"

namespace webdriver {

class NewSessionCommandHandler : public IECommandHandler {
 public:
  NewSessionCommandHandler(void) {
  }

  virtual ~NewSessionCommandHandler(void) {
  }

 protected:
  void ExecuteInternal(const IECommandExecutor& executor,
                       const LocatorMap& locator_parameters,
                       const ParametersMap& command_parameters,
                       Response* response) {
    IECommandExecutor& mutable_executor = const_cast<IECommandExecutor&>(executor);
    ParametersMap::const_iterator it = command_parameters.find("desiredCapabilities");
    if (it != command_parameters.end()) {
      Json::Value ignore_protected_mode_settings = it->second.get("ignoreProtectedModeSettings", false);
      mutable_executor.set_ignore_protected_mode_settings(ignore_protected_mode_settings.asBool());
      Json::Value enable_native_events = it->second.get("nativeEvents", true);
      mutable_executor.set_enable_native_events(enable_native_events.asBool());
      Json::Value initial_url = it->second.get("initialBrowserUrl", "");
      mutable_executor.set_initial_browser_url(initial_url.asString());
      Json::Value scroll_behavior = it->second.get("elementScrollBehavior", 0);
      mutable_executor.set_scroll_behavior(static_cast<ELEMENT_SCROLL_BEHAVIOR>(scroll_behavior.asInt()));
    }
    std::string create_browser_error_message = "";
    int result_code = mutable_executor.CreateNewBrowser(&create_browser_error_message);
    if (result_code != SUCCESS) {
      // The browser was not created successfully, therefore the
      // session must be marked as invalid so the server can
      // properly shut it down.
      mutable_executor.set_is_valid(false);
      response->SetErrorResponse(result_code,
                                 "Unexpected error launching Internet Explorer. " + create_browser_error_message);
      return;
    }
    std::string id = executor.session_id();
    response->SetResponse(303, "/session/" + id);
  }
};

} // namespace webdriver

#endif // WEBDRIVER_IE_NEWSESSIONCOMMANDHANDLER_H_
