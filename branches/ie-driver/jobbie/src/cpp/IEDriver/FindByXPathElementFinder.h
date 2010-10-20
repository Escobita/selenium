#pragma once
#include "BrowserManager.h"
#include "jsxpath.h"

class FindByXPathElementFinder :
	public ElementFinder
{
public:
	FindByXPathElementFinder(void)
	{
	}

	virtual ~FindByXPathElementFinder(void)
	{
	}

private:
	int FindByXPathElementFinder::InjectXPathEngine(BrowserWrapper *pBrowser) 
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
		int statusCode = pBrowser->ExecuteScript(&script, args, &result);
		::SafeArrayDestroy(args);

		return statusCode;
	}

protected:

	int FindByXPathElementFinder::FindElementInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, IHTMLElement **ppElement)
	{
		// TODO: create timeout
		//clock_t end = endAt(driver);
		int result = ENOSUCHELEMENT;

		//do {
			result = this->InjectXPathEngine(pBrowser);
			// TODO(simon): Why does the injecting sometimes fail?
			/*
			if (result != SUCCESS) {
				return result;
			}
			*/

			// Call it
			std::wstring query;
			if (pParentElement) {
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
			if (pParentElement)
			{
				VARIANT parentVar;
				parentVar.vt = VT_DISPATCH;
				parentVar.pdispVal = pParentElement;
				::SafeArrayPutElement(args, &index, &parentVar);
			}

			CComVariant queryResult;
			result = pBrowser->ExecuteScript(&query, args, &queryResult);
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
	int FindByXPathElementFinder::FindElementsInternal(BrowserWrapper *pBrowser, IHTMLElement *pParentElement, std::wstring criteria, std::vector<IHTMLElement*> *pElements)
	{
		// TODO: create timeout
		//clock_t end = endAt(driver);
		int result = ENOSUCHELEMENT;

		//do {
			result = this->InjectXPathEngine(pBrowser);
			// TODO(simon): Why does the injecting sometimes fail?
			/*
			if (result != SUCCESS) {
				return result;
			}
			*/

			// Call it
			std::wstring query;
			if (pParentElement) {
				query += L"(function() { return function() {var res = document.__webdriver_evaluate(arguments[0], arguments[1], null, 7, null); return res;};})();";
			} else {
				query += L"(function() { return function() {var res = document.__webdriver_evaluate(arguments[0], document, null, 7, null); return res;};})();";
			}

			SAFEARRAY *queryArgs;
			SAFEARRAYBOUND bounds;
			bounds.cElements = 2;
			bounds.lLbound = 0;
			queryArgs = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

			long index = 0;
			CComVariant xpath(criteria.c_str());
			::SafeArrayPutElement(queryArgs, &index, &xpath);

			++index;
			if (pParentElement)
			{
				CComVariant parentVar;
				parentVar.vt = VT_DISPATCH;
				parentVar.pdispVal = pParentElement;
				::SafeArrayPutElement(queryArgs, &index, &parentVar);
			}

			CComVariant snapshot;
			result = pBrowser->ExecuteScript(&query, queryArgs, &snapshot);
			::SafeArrayDestroy(queryArgs);

			bounds.cElements = 1;
			SAFEARRAY* lengthArgs = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);
			index = 0;
			::SafeArrayPutElement(lengthArgs, &index, &snapshot);
			CComVariant lengthVar;
			std::wstring getElementCountScript = L"(function(){return function() {return arguments[0].snapshotLength;}})();";
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
				std::wstring getNextElementScript = L"(function(){return function() {return arguments[0].iterateNext();}})();";
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
