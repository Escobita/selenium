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

#ifndef WEBDRIVER_IE_GETELEMENTTEXTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETELEMENTTEXTCOMMANDHANDLER_H_

#include "Session.h"
#include "Generated/atoms.h"

namespace webdriver {

class GetElementTextCommandHandler : public CommandHandler {
public:
	GetElementTextCommandHandler(void) {
	}

	~GetElementTextCommandHandler(void) {
	}

protected:
	void GetElementTextCommandHandler::ExecuteInternal(const IESessionWindow& session, const LocatorMap& locator_parameters, const ParametersMap& command_parameters, Response * response) {
		LocatorMap::const_iterator id_parameter_iterator = locator_parameters.find("id");
		if (id_parameter_iterator == locator_parameters.end()) {
			response->SetErrorResponse(400, "Missing parameter in URL: id");
			return;
		} else {
			std::wstring element_id = CA2W(id_parameter_iterator->second.c_str(), CP_UTF8);

			BrowserHandle browser_wrapper;
			int status_code = session.GetCurrentBrowser(&browser_wrapper);
			if (status_code != SUCCESS) {
				response->SetErrorResponse(status_code, "Unable to get browser");
				return;
			}

			ElementHandle element_wrapper;
			status_code = this->GetElement(session, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				// The atom is just the definition of an anonymous
				// function: "function() {...}"; Wrap it in another function so we can
				// invoke it with our arguments without polluting the current namespace.
				std::wstring script_source = L"(function() { return (";
				script_source += atoms::GET_TEXT;
				script_source += L")})();";

				CComPtr<IHTMLDocument2> doc;
				browser_wrapper->GetDocument(&doc);
				Script script_wrapper(doc, script_source, 1);
				script_wrapper.AddArgument(element_wrapper->element());
				status_code = script_wrapper.Execute();

				CComVariant text_variant;
				if (status_code == SUCCESS) {
					HRESULT hr = ::VariantCopy(&text_variant, &script_wrapper.result());
				}

				if (status_code == SUCCESS) {
					std::wstring text = this->ConvertVariantToWString(&text_variant);
					std::string text_str = CW2A(text.c_str(), CP_UTF8);
					response->SetResponse(SUCCESS, text_str);
					return;
				} else {
					response->SetErrorResponse(status_code, "Unable to get element text");
					return;
				}
			} else {
				response->SetErrorResponse(status_code, "Element is no longer valid");
				return;
			}
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETELEMENTTEXTCOMMANDHANDLER_H_
