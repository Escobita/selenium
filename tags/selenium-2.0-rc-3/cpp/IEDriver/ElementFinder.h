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

#ifndef WEBDRIVER_IE_ELEMENTFINDER_H_
#define WEBDRIVER_IE_ELEMENTFINDER_H_

#include <string>
#include <vector>

using namespace std;

namespace webdriver {

// Forward declaration of classes to avoid
// circular include files.
class IESessionWindow;

class ElementFinder {
public:
	ElementFinder();
	virtual ~ElementFinder(void);
	virtual int FindElement(const IESessionWindow& session, ElementHandle parent_wrapper, const std::wstring& mechanism, const std::wstring& criteria, Json::Value* found_element);
	virtual int FindElements(const IESessionWindow& session, ElementHandle parent_wrapper, const std::wstring& mechanism, const std::wstring& criteria, Json::Value* found_elements);

private:
	int FindElementByCssSelector(const IESessionWindow& session, const ElementHandle parent_wrapper, const std::wstring& criteria, Json::Value* found_element);
	int ElementFinder::FindElementsByCssSelector(const IESessionWindow& session, const ElementHandle parent_wrapper, const std::wstring& criteria, Json::Value* found_elements);
	int FindElementByXPath(const IESessionWindow& session, const ElementHandle parent_wrapper, const std::wstring& criteria, Json::Value* found_element);
	int FindElementsByXPath(const IESessionWindow& session, const ElementHandle parent_wrapper, const std::wstring& criteria, Json::Value* found_elements);
	int InjectXPathEngine(BrowserHandle browser_wrapper);
};

} // namespace webdriver

#endif // WEBDRIVER_IE_ELEMENTFINDER_H_
