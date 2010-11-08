#ifndef WEBDRIVER_IE_GETELEMENTLOCATIONCOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETELEMENTLOCATIONCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetElementLocationCommandHandler : public WebDriverCommandHandler {
public:
	GetElementLocationCommandHandler(void) {
	}

	virtual ~GetElementLocationCommandHandler(void) {
	}

protected:
	void GetElementLocationCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		if (locator_parameters.find("id") == locator_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "id";
		} else {
			int status_code = SUCCESS;
			std::wstring element_id(CA2W(locator_parameters["id"].c_str(), CP_UTF8));

			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			HWND window_handle = browser_wrapper->GetWindowHandle();

			ElementWrapper *element_wrapper;
			status_code = this->GetElement(manager, element_id, &element_wrapper);
			if (status_code == SUCCESS) {
				CComQIPtr<IHTMLElement2> element2(element_wrapper->element());
				if (!element2) {
					status_code = EUNHANDLEDERROR;
				}
				CComPtr<IHTMLRect> rect;
				element2->getBoundingClientRect(&rect);

				long x, y;
				rect->get_left(&x);
				rect->get_top(&y);

				CComQIPtr<IHTMLDOMNode2> node(element2);
				CComPtr<IDispatch> owner_document_dispatch;
				node->get_ownerDocument(&owner_document_dispatch);
				CComQIPtr<IHTMLDocument3> owner_doc(owner_document_dispatch);

				CComPtr<IHTMLElement> temp_doc;
				owner_doc->get_documentElement(&temp_doc);

				CComQIPtr<IHTMLElement2> document_element(temp_doc);
				long left = 0, top = 0;
				document_element->get_scrollLeft(&left);
				document_element->get_scrollTop(&top);

				x += left;
				y += top;

				response->m_value["x"] = x;
				response->m_value["y"] = y;
			} else {
				response->m_value["message"] = "Element is no longer valid";
			}

			response->set_status_code(status_code);
		}
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETELEMENTLOCATIONCOMMANDHANDLER_H_
