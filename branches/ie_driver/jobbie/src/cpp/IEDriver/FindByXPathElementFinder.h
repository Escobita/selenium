#ifndef WEBDRIVER_IE_FINDBYXPATHELEMENTFINDER_H_
#define WEBDRIVER_IE_FINDBYXPATHELEMENTFINDER_H_

#include "BrowserManager.h"
#include "jsxpath.h"

namespace webdriver {

class FindByXPathElementFinder : public ElementFinder {
public:
	FindByXPathElementFinder(void) {
	}

	virtual ~FindByXPathElementFinder(void) {
	}

protected:
	int FindByXPathElementFinder::FindElementInternal(BrowserWrapper *browser, IHTMLElement *parent_element, std::wstring criteria, IHTMLElement **found_element) {
		int result = ENOSUCHELEMENT;

		result = this->InjectXPathEngine(browser);
		// TODO(simon): Why does the injecting sometimes fail?
		if (result != SUCCESS) {
			return result;
		}

		// Call it
		std::wstring query;
		if (parent_element) {
			query += L"(function() { return function(){var res = document.__webdriver_evaluate(arguments[0], arguments[1], null, 7, null); return res.snapshotItem(0) ;};})();";
		} else {
			query += L"(function() { return function(){var res = document.__webdriver_evaluate(arguments[0], document, null, 7, null); return res.snapshotLength != 0 ? res.snapshotItem(0) : undefined ;};})();";
		}

		SAFEARRAY *args;
		SAFEARRAYBOUND bounds;
		bounds.cElements = 2;
		bounds.lLbound = 0;
		args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

		long index = 0;
		CComVariant xpath(criteria.c_str());
		::SafeArrayPutElement(args, &index, &xpath);

		++index;
		if (parent_element) {
			VARIANT parentVar;
			parentVar.vt = VT_DISPATCH;
			parentVar.pdispVal = parent_element;
			::SafeArrayPutElement(args, &index, &parentVar);
		}

		CComVariant query_result;
		result = browser->ExecuteScript(&query, args, &query_result);
		::SafeArrayDestroy(args);

		// And be done
		if (result == SUCCESS) {
			if (query_result.vt == VT_EMPTY) {
				result = ENOSUCHELEMENT;
			} else {
				*found_element = (IHTMLElement*) query_result.pdispVal;
				result = SUCCESS;
			}
		}
		return result;
	}

	int FindByXPathElementFinder::FindElementsInternal(BrowserWrapper *browser, IHTMLElement *parent_element, std::wstring criteria, std::vector<IHTMLElement*> *found_elements)
	{
		int result = ENOSUCHELEMENT;

		result = this->InjectXPathEngine(browser);
		// TODO(simon): Why does the injecting sometimes fail?
		if (result != SUCCESS) {
			return result;
		}

		// Call it
		std::wstring query;
		if (parent_element) {
			query += L"(function() { return function() {var res = document.__webdriver_evaluate(arguments[0], arguments[1], null, 7, null); return res;};})();";
		} else {
			query += L"(function() { return function() {var res = document.__webdriver_evaluate(arguments[0], document, null, 7, null); return res;};})();";
		}

		SAFEARRAY *query_args;
		SAFEARRAYBOUND bounds;
		bounds.cElements = 2;
		bounds.lLbound = 0;
		query_args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

		long index = 0;
		CComVariant xpath(criteria.c_str());
		::SafeArrayPutElement(query_args, &index, &xpath);

		++index;
		if (parent_element) {
			CComVariant parent_variant;
			parent_variant.vt = VT_DISPATCH;
			parent_variant.pdispVal = parent_element;
			::SafeArrayPutElement(query_args, &index, &parent_variant);
		}

		CComVariant snapshot;
		result = browser->ExecuteScript(&query, query_args, &snapshot);
		::SafeArrayDestroy(query_args);

		bounds.cElements = 1;
		SAFEARRAY* length_args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);
		index = 0;
		::SafeArrayPutElement(length_args, &index, &snapshot);
		CComVariant length_variant;
		std::wstring getElementCountScript = L"(function(){return function() {return arguments[0].snapshotLength;}})();";
		result = browser->ExecuteScript(&getElementCountScript, length_args, &length_variant);
		::SafeArrayDestroy(length_args);

		if (length_variant.vt != VT_I4) {
			result = EUNEXPECTEDJSERROR;
		}

		long length = length_variant.lVal;

		std::wstring get_next_element_script(L"(function(){return function() {return arguments[0].iterateNext();}})();");
		for (long i = 0; i < length; i++) {
			SAFEARRAYBOUND get_elem_bounds;
			get_elem_bounds.cElements = 2;
			get_elem_bounds.lLbound = 0;

			SAFEARRAY *get_element_args;
			get_element_args = ::SafeArrayCreate(VT_VARIANT, 1, &get_elem_bounds);

			// Cheat
			long get_element_args_index = 0;
			HRESULT hr = ::SafeArrayPutElement(get_element_args, &get_element_args_index, &snapshot);

			++get_element_args_index;
			CComVariant element_index;
			element_index.lVal = i;
			hr = ::SafeArrayPutElement(get_element_args, &get_element_args_index, &element_index);

			CComVariant get_element_result;
			get_element_result.Clear();
			result = browser->ExecuteScript(&get_next_element_script, get_element_args, &get_element_result);
			hr = ::SafeArrayDestroy(get_element_args);

			CComVariant element_value;
			::VariantCopy(&element_value, &get_element_result);
			IHTMLElement *found_element = (IHTMLElement *)element_value.pdispVal;
			found_elements->push_back(found_element);
		}

		return result;
	}

private:
	int FindByXPathElementFinder::InjectXPathEngine(BrowserWrapper *browser_wrapper) 
	{
		// Inject the XPath engine
		std::wstring script;
		for (int i = 0; XPATHJS[i]; i++) {
			script += XPATHJS[i];
		}

		SAFEARRAY *args;
		SAFEARRAYBOUND bounds;
		bounds.cElements = 0;
		bounds.lLbound = 0;
		args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

		CComVariant result;
		int status_code = browser_wrapper->ExecuteScript(&script, args, &result);
		::SafeArrayDestroy(args);

		return status_code;
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE__H_
