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

#ifndef WEBDRIVER_IE_ELEMENTEQUALSCOMMANDHANDLER_H_
#define WEBDRIVER_IE_ELEMENTEQUALSCOMMANDHANDLER_H_

#include "../Browser.h"
#include "../IECommandHandler.h"
#include "../IECommandExecutor.h"

namespace webdriver {

class ElementEqualsCommandHandler : public IECommandHandler {
public:
  ElementEqualsCommandHandler(void) {
  }

  virtual ~ElementEqualsCommandHandler(void) {
  }

protected:
  void ElementEqualsCommandHandler::ExecuteInternal(const IECommandExecutor& executor,
                                                    const LocatorMap& locator_parameters,
                                                    const ParametersMap& command_parameters,
                                                    Response* response) {
    LocatorMap::const_iterator id_parameter_iterator = locator_parameters.find("id");
    LocatorMap::const_iterator other_parameter_iterator = locator_parameters.find("other");
    if (id_parameter_iterator == locator_parameters.end()) {
      response->SetErrorResponse(400, "Missing parameter in URL: id");
      return;
    }
    else if (other_parameter_iterator == locator_parameters.end()) {
      response->SetErrorResponse(400, "Missing parameter in URL: other");
      return;
    } else {
      std::string element_id = id_parameter_iterator->second;
      std::string other_element_id = other_parameter_iterator->second;

      BrowserHandle browser_wrapper;
      int status_code = executor.GetCurrentBrowser(&browser_wrapper);
      if (status_code != SUCCESS) {
        response->SetErrorResponse(status_code, "Unable to get browser");
        return;
      }

      ElementHandle element_wrapper;
      status_code = this->GetElement(executor, element_id, &element_wrapper);
      if (status_code == SUCCESS)
      {
        ElementHandle other_element_wrapper;
        status_code = this->GetElement(executor,
                                       other_element_id,
                                       &other_element_wrapper);
        if (status_code == SUCCESS) {
          response->SetSuccessResponse((element_wrapper->element() == other_element_wrapper->element()));
          return;
        } else {
          response->SetErrorResponse(status_code,
                                     "Element specified by 'other' is no longer valid");
          return;
        }
      } else {
        response->SetErrorResponse(status_code,
                                   "Element specified by 'id' is no longer valid");
        return;
      }
    }

  }
};

} // namespace webdriver

#endif // WEBDRIVER_IE_ELEMENTEQUALSCOMMANDHANDLER_H_
