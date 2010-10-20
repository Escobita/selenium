#pragma once
#include "BrowserManager.h"
#include "sizzle.h"

class FindByCssSelectorElementFinder :
	public ElementFinder
{
public:

	FindByCssSelectorElementFinder(void)
	{
	}

	virtual ~FindByCssSelectorElementFinder(void)
	{
	}
protected:

	int FindByCssSelectorElementFinder::FindElementInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
	{
		// TODO: create timeout
		//clock_t end = endAt(driver);
		int result = ENOSUCHELEMENT;

		//do {
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
			if (pParentElement)
			{
				VARIANT parentVar;
				parentVar.vt = VT_DISPATCH;
				parentVar.pdispVal = pParentElement;
				::SafeArrayPutElement(args, &index, &parentVar);
			}

			// Call it
			CComVariant queryResult;
			result = pBrowser->ExecuteScript(&script, args, &queryResult);
			::SafeArrayDestroy(args);

			// And be done
			if (result == SUCCESS) {
				if (queryResult.vt == VT_EMPTY)
				{
					result = ENOSUCHELEMENT;
					// continue;
				}
				else
				{
					*ppElement = (IHTMLElement*) queryResult.pdispVal;
					result = SUCCESS;
				}
				int type = 0;
			}
			return result;

		//} while (clock() < end);

		return SUCCESS;
	}
	int FindByCssSelectorElementFinder::FindElementsInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
	{
		// TODO: create timeout
		//clock_t end = endAt(driver);
		int result = ENOSUCHELEMENT;

		//do {
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
			CComVariant xpath(criteria.c_str());
			::SafeArrayPutElement(args, &index, &xpath);

			++index;
			if (pParentElement)
			{
				CComVariant parentVar;
				parentVar.vt = VT_DISPATCH;
				parentVar.pdispVal = pParentElement;
				::SafeArrayPutElement(args, &index, &parentVar);
			}

			CComVariant snapshot;
			result = pBrowser->ExecuteScript(&script, args, &snapshot);
			::SafeArrayDestroy(args);

			bounds.cElements = 1;
			SAFEARRAY* lengthArgs = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);
			index = 0;
			::SafeArrayPutElement(lengthArgs, &index, &snapshot);
			CComVariant lengthVar;
			std::wstring getElementCountScript = L"(function(){return function() {return arguments[0].length;}})();";
			result = pBrowser->ExecuteScript(&getElementCountScript, lengthArgs, &lengthVar);
			::SafeArrayDestroy(lengthArgs);

			if (result != SUCCESS) {
				// continue;
			}

			if (lengthVar.vt != VT_I4) {
				result = EUNEXPECTEDJSERROR;
				// continue;
			}

			long length = lengthVar.lVal;

			index = 1;
			for (long i = 0; i < length; i++) {
				SAFEARRAY *getElemArgs;
				SAFEARRAYBOUND getElemBounds;
				getElemBounds.cElements = 2;
				getElemBounds.lLbound = 0;
				getElemArgs = ::SafeArrayCreate(VT_VARIANT, 1, &getElemBounds);

				// Cheat
				long getElemArgsIndex = 0;
				::SafeArrayPutElement(getElemArgs, &getElemArgsIndex, &snapshot);

				++getElemArgsIndex;
				long elementIndex = i;
				::SafeArrayPutElement(getElemArgs, &getElemArgsIndex, &elementIndex);

				VARIANT getElemRes;
				std::wstring getNextElementScript = L"(function(){return function() {return arguments[0][arguments[1]];}})();";
				result = pBrowser->ExecuteScript(&getNextElementScript, getElemArgs, &getElemRes);
				::SafeArrayDestroy(getElemArgs);

				IHTMLElement *foundElement = (IHTMLElement *)getElemRes.pdispVal;
				pElements->push_back(foundElement);
			}

			return result;

		//} while (clock() < end);

		//return result;
	}
};
