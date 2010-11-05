#ifndef WEBDRIVER_IE_GETACTIVEELEMENTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETACTIVEELEMENTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetActiveElementCommandHandler : public WebDriverCommandHandler {
public:
	GetActiveElementCommandHandler(void) 	{
	}

	virtual ~GetActiveElementCommandHandler(void) {
	}

protected:
	void GetActiveElementCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		response->set_status_code(SUCCESS);
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);

		CComPtr<IHTMLDocument2> doc;
		browser_wrapper->GetDocument(&doc);
		if (!doc) {
			response->set_status_code(ENOSUCHDOCUMENT);
			response->m_value["message"] = "Document not found";
			return;
		}

		CComPtr<IHTMLElement> element;
		doc->get_activeElement(&element);

		if (!element) {
			// Grab the body instead
			doc->get_body(&element);
		}

		if (element) {
			IHTMLElement* dom_element;
			element.CopyTo(&dom_element);
			ElementWrapper *element_wrapper = new ElementWrapper(dom_element);
			manager->AddManagedElement(element_wrapper);
			response->m_value = element_wrapper->ConvertToJson();
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETACTIVEELEMENTCOMMANDHANDLER_H_
