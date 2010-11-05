#ifndef WEBDRIVER_IE_FINDBYCSSSELECTORELEMENTFINDER_H_
#define WEBDRIVER_IE_FINDBYCSSSELECTORELEMENTFINDER_H_

#include "BrowserManager.h"
#include "sizzle.h"

namespace webdriver {

class FindByCssSelectorElementFinder : public ElementFinder {
public:
	FindByCssSelectorElementFinder(void) {
	}

	virtual ~FindByCssSelectorElementFinder(void) {
	}

protected:
	int FindByCssSelectorElementFinder::FindElementInternal(BrowserWrapper *browser, IHTMLElement *parent_element, std::wstring criteria, IHTMLElement **found_element) {
		int result = ENOSUCHELEMENT;

		std::wstring script(L"(function() { return function(){");
		for (int i = 0; SIZZLE[i]; i++) {
			script += SIZZLE[i];
			script += L"\n";
		}
		script += L"var root = arguments[1] ? arguments[1] : document.documentElement;";
		script += L"if (root['querySelector']) { return root.querySelector(arguments[0]); } ";
		script += L"var results = []; Sizzle(arguments[0], root, results);";
		script += L"return results.length > 0 ? results[0] : null;";
		script += L"};})();";

		SAFEARRAY *args;
		SAFEARRAYBOUND bounds;
		bounds.cElements = 2;
		bounds.lLbound = 0;
		args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

		long index = 0;
		CComVariant selector(criteria.c_str());
		::SafeArrayPutElement(args, &index, &selector);

		++index;
		if (parent_element) {
			VARIANT parent_variant;
			parent_variant.vt = VT_DISPATCH;
			parent_variant.pdispVal = parent_element;
			::SafeArrayPutElement(args, &index, &parent_variant);
		}

		// Call it
		CComVariant query_result;
		result = browser->ExecuteScript(&script, args, &query_result);
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

	int FindByCssSelectorElementFinder::FindElementsInternal(BrowserWrapper *browser, IHTMLElement *parent_element, std::wstring criteria, std::vector<IHTMLElement*> *found_elements)
	{
		int result = ENOSUCHELEMENT;

		std::wstring script(L"(function() { return function(){");
		for (int i = 0; SIZZLE[i]; i++) {
			script += SIZZLE[i];
			script += L"\n";
		}
		script += L"var root = arguments[1] ? arguments[1] : document.documentElement;";
		script += L"if (root['querySelectorAll']) { return root.querySelectorAll(arguments[0]); } ";
		script += L"var results = []; Sizzle(arguments[0], root, results);";
		script += L"return results;";
		script += L"};})();";

		// Call it
		SAFEARRAY *args;
		SAFEARRAYBOUND bounds;
		bounds.cElements = 2;
		bounds.lLbound = 0;
		args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

		long index = 0;
		CComVariant selector(criteria.c_str());
		::SafeArrayPutElement(args, &index, &selector);

		++index;
		if (parent_element) {
			CComVariant parent_variant;
			parent_variant.vt = VT_DISPATCH;
			parent_variant.pdispVal = parent_element;
			::SafeArrayPutElement(args, &index, &parent_variant);
		}

		CComVariant snapshot;
		result = browser->ExecuteScript(&script, args, &snapshot);
		::SafeArrayDestroy(args);

		bounds.cElements = 1;
		SAFEARRAY* length_args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);
		index = 0;
		::SafeArrayPutElement(length_args, &index, &snapshot);
		CComVariant length_variant;
		std::wstring get_element_count_script = L"(function(){return function() {return arguments[0].length;}})();";
		result = browser->ExecuteScript(&get_element_count_script, length_args, &length_variant);
		::SafeArrayDestroy(length_args);

		if (result != SUCCESS) {
			return result;
		}

		if (length_variant.vt != VT_I4) {
			result = EUNEXPECTEDJSERROR;
		}

		long length = length_variant.lVal;

		index = 1;
		for (long i = 0; i < length; i++) {
			SAFEARRAY *get_element_args;
			SAFEARRAYBOUND get_element_bounds;
			get_element_bounds.cElements = 2;
			get_element_bounds.lLbound = 0;
			get_element_args = ::SafeArrayCreate(VT_VARIANT, 1, &get_element_bounds);

			// Cheat
			long get_element_args_index = 0;
			::SafeArrayPutElement(get_element_args, &get_element_args_index, &snapshot);

			++get_element_args_index;
			CComVariant element_index;
			element_index.lVal = i;
			::SafeArrayPutElement(get_element_args, &get_element_args_index, &element_index);

			VARIANT get_element_result;
			std::wstring get_next_element_script = L"(function(){return function() {return arguments[0][arguments[1]];}})();";
			result = browser->ExecuteScript(&get_next_element_script, get_element_args, &get_element_result);
			::SafeArrayDestroy(get_element_args);

			IHTMLElement *found_element = (IHTMLElement *)get_element_result.pdispVal;
			found_elements->push_back(found_element);
		}

		return result;
	}
};

} // namespace webdriver

#endif // WEBDRIVER_IE_FINDBYCSSSELECTORELEMENTFINDER_H_
