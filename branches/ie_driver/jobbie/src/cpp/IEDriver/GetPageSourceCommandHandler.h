#ifndef WEBDRIVER_IE_GETPAGESOURCECOMMANDHANDLER_H_
#define WEBDRIVER_IE_GETPAGESOURCECOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class GetPageSourceCommandHandler : public WebDriverCommandHandler {
public:
	GetPageSourceCommandHandler(void) {
	}

	virtual ~GetPageSourceCommandHandler(void) {
	}

protected:
	void GetPageSourceCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response) {
		BrowserWrapper *browser_wrapper;
		manager->GetCurrentBrowser(&browser_wrapper);

		CComPtr<IHTMLDocument2> doc;
		browser_wrapper->GetDocument(&doc);
		
		CComPtr<IHTMLDocument3> doc3;
		CComQIPtr<IHTMLDocument3> doc_qi_pointer(doc);
		if (doc_qi_pointer) {
			doc3 = doc_qi_pointer.Detach();
		}

		if (!doc3) {
			response->m_value = "";
			return;
		}

		CComPtr<IHTMLElement> document_element;
		HRESULT hr = doc3->get_documentElement(&document_element);
		if (FAILED(hr)) {
			//LOGHR(WARN, hr) << "Unable to get document element from page";
			response->m_value = "";
			return;
		}

		CComBSTR html;
		hr = document_element->get_outerHTML(&html);
		if (FAILED(hr)) {
			//LOGHR(WARN, hr) << "Have document element but cannot read source.";
			response->m_value = "";
			return;
		}

		std::string page_source = CW2A((LPCWSTR)html, CP_UTF8);
		response->m_value = page_source;
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_GETPAGESOURCECOMMANDHANDLER_H_
