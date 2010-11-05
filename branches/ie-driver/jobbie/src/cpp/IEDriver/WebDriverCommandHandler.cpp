#include "StdAfx.h"
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

namespace webdriver {

WebDriverCommandHandler::WebDriverCommandHandler(void) {
}

WebDriverCommandHandler::~WebDriverCommandHandler(void) {
}

void WebDriverCommandHandler::Execute(BrowserManager *manager, std::map<std::string,std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse *response) {
	this->ExecuteInternal(manager, locator_parameters, command_parameters, response);
}

void WebDriverCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string,std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse *response) {
}

int WebDriverCommandHandler::GetElement(BrowserManager *manager, std::wstring element_id, ElementWrapper **element_wrapper) {
	int statusCode = EOBSOLETEELEMENT;
	ElementWrapper *candidate_wrapper;
	int result = manager->GetManagedElement(element_id, &candidate_wrapper);
	if (result != SUCCESS) {
		statusCode = 404;
	} else {
		// Verify that the element is still valid by walking up the
		// DOM tree until we find no parent or the html tag
		CComPtr<IHTMLElement> parent(candidate_wrapper->element());
		while (parent) {
			CComQIPtr<IHTMLHtmlElement> html(parent);
			if (html) {
				statusCode = SUCCESS;
				*element_wrapper = candidate_wrapper;
				break;
			}

			CComPtr<IHTMLElement> next;
			HRESULT hr = parent->get_parentElement(&next);
			parent = next;
		}
	}

	return statusCode;
}

} // namespace webdriver