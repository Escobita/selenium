#pragma once
#include "BrowserManager.h"

class ExecuteScriptCommandHandler :
	public WebDriverCommandHandler
{
public:

	ExecuteScriptCommandHandler(void)
	{
	}

	virtual ~ExecuteScriptCommandHandler(void)
	{
	}
protected:
	std::wstring ExecuteScriptCommandHandler::GetScriptResultObjectType(CComVariant* scriptResult)
	{
		CComQIPtr<IHTMLElementCollection> isCol(scriptResult->pdispVal);
		if (isCol) 
		{
			return L"HtmlCollection";
		}

		CComQIPtr<IHTMLElement> isElem(scriptResult->pdispVal);
		if (isElem) 
		{
			return L"HtmlElement";
		}

		// Other possible interfaces: IHTMLFrameBase, IHTMLFrameElement
		// The distinction is not important for now.

		CComPtr<ITypeInfo> typeinfo;
		HRESULT getTypeInfoRes = scriptResult->pdispVal->GetTypeInfo(0, LOCALE_USER_DEFAULT, &typeinfo);
		TYPEATTR* typeAttr;
		CComBSTR name;
		if (SUCCEEDED(getTypeInfoRes) && SUCCEEDED(typeinfo->GetTypeAttr(&typeAttr))
			&& SUCCEEDED(typeinfo->GetDocumentation(-1, &name, 0, 0, 0)))
		{
			// If the name is JScriptTypeInfo then *assume* this is a Javascript array.
			// Note that Javascript can return functions which will have the same
			// type - the only way to be sure is to run some more Javascript code to
			// see if this object has a length attribute. This does not seem necessary
			// now.
			// (For future reference, GUID is {C59C6B12-F6C1-11CF-8835-00A0C911E8B2})
			typeinfo->ReleaseTypeAttr(typeAttr);
			if (name == L"JScriptTypeInfo")
			{
				return L"JavascriptArray";
			}
		}

		return L"Unknown";
	}

	int ExecuteScriptCommandHandler::PopulateArgumentArray(BrowserManager *manager, SAFEARRAY * args, Json::Value jsonArgs)
	{
		int statusCode = SUCCESS;
		for (UINT argIndex = 0; argIndex < jsonArgs.size(); ++argIndex)
		{
			LONG index = (LONG)argIndex;
			Json::Value arg = jsonArgs[argIndex];
			if (arg.isString())
			{
				std::wstring value(CA2W(arg.asString().c_str()));
				CComVariant destStr(value.c_str());
				::SafeArrayPutElement(args, &index, &destStr);
			}
			else if (arg.isInt())
			{
				int intNumber(arg.asInt());
				VARIANT destInt;
				destInt.vt = VT_I4;
				destInt.lVal = (LONG) intNumber;	
				::SafeArrayPutElement(args, &index, &destInt);
			}
			else if (arg.isDouble())
			{
				double dblNumber(arg.asDouble());
				VARIANT destDbl;
				destDbl.vt = VT_R8;
				destDbl.dblVal = dblNumber;	
				::SafeArrayPutElement(args, &index, &destDbl);
			}
			else if (arg.isBool())
			{
				bool boolArg(arg.asBool());
				VARIANT destBool;
				destBool.vt = VT_BOOL;
				destBool.boolVal = boolArg;
				::SafeArrayPutElement(args, &index, &destBool);
			}
			else if (arg.isObject() && arg.isMember("ELEMENT"))
			{
				std::wstring elementId(CA2W(arg["ELEMENT"].asString().c_str()));

				ElementWrapper *pElementWrapper;
				statusCode = this->GetElement(manager, elementId, &pElementWrapper);
				if (statusCode == SUCCESS)
				{
					VARIANT destDisp;
					destDisp.vt = VT_DISPATCH;
					destDisp.pdispVal = pElementWrapper->m_pElement;
					::SafeArrayPutElement(args, &index, &destDisp);
				}
			}
		}

		return statusCode;
	}

	int ConvertScriptResult(CComVariant result, BrowserManager *pManager, Json::Value *value)
	{
		std::string strVal;
		switch (result.vt) {
			case VT_BSTR:
				strVal = CW2A(result.bstrVal);
				*value = strVal;
				break;

			case VT_I4:
			case VT_I8:
				*value = result.lVal;
				break;

			case VT_BOOL:
				*value = result.boolVal == VARIANT_TRUE;
				break;

			case VT_DISPATCH:
				{
					std::wstring itemType = this->GetScriptResultObjectType(&result);
					if (itemType == L"JavascriptArray" || itemType == L"HtmlCollection")
					{
						BrowserWrapper *pBrowser;
						pManager->GetCurrentBrowser(&pBrowser);
						Json::Value resultArray(Json::arrayValue);
						// Prepare an array for the Javascript execution, containing only one
						// element - the original returned array from a JS execution.
						SAFEARRAYBOUND lengthQuery;
						lengthQuery.cElements = 1;
						lengthQuery.lLbound = 0;

						SAFEARRAY* lengthArgs = SafeArrayCreate(VT_VARIANT, 1, &lengthQuery);
						LONG index = 0;
						SafeArrayPutElement(lengthArgs, &index, &result);

						CComVariant lengthVar;
						std::wstring getLengthScript(L"(function(){return function() {return arguments[0].length;}})();");
						int lengthResult = pBrowser->ExecuteScript(
							&getLengthScript,
							lengthArgs, &lengthVar);

						SafeArrayDestroy(lengthArgs);
						// Expect the return type to be an integer. A non-integer means this was
						// not an array after all.
						if (lengthVar.vt != VT_I4) 
						{
							return EUNEXPECTEDJSERROR;
						}

						LONG length = lengthVar.lVal;

						// Prepare an array for the Javascript execution, containing only one
						// element - the original returned array from a JS execution.
						SAFEARRAYBOUND itemQuery;
						itemQuery.cElements = 2;
						itemQuery.lLbound = 0;

						for (LONG i = 0; i < length; ++i)
						{
							SAFEARRAY* itemArgs = SafeArrayCreate(VT_VARIANT, 1, &itemQuery);
							LONG index = 0;
							SafeArrayPutElement(itemArgs, &index, &result);

							CComVariant indexVar;
							indexVar.vt = VT_I4;
							indexVar.lVal = i;	
							index++;
							SafeArrayPutElement(itemArgs, &index, &indexVar);

							CComVariant itemVar;
							std::wstring getArrayItemScript(L"(function(){return function() {return arguments[0][arguments[1]];}})();"); 
							int lengthResult = pBrowser->ExecuteScript(&getArrayItemScript,
								itemArgs, &itemVar);

							SafeArrayDestroy(itemArgs);
							Json::Value arrayItemResult;
							int arrayItemStatus = this->ConvertScriptResult(itemVar, pManager, &arrayItemResult);
							resultArray[i] = arrayItemResult;
						}
						*value = resultArray;
					}
					else
					{
						IHTMLElement *node = (IHTMLElement*) result.pdispVal;
						ElementWrapper *pElement = new ElementWrapper(node);
						pManager->m_knownElements[pElement->m_elementId] = pElement;
						*value = pElement->ConvertToJson();
					}
				}
				break;

			case VT_EMPTY:
				*value = Json::Value::null;
				break;

			case VT_USERDEFINED:
				// TODO: Handle exceptions
				//*type = TYPE_EXCEPTION;
				break;

			case VT_R4:
			case VT_R8:
				*value = result.dblVal;
				break;

			default:
				return EUNKNOWNSCRIPTRESULT;
		}
		return SUCCESS;
	}

	void ExecuteScriptCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("script") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "script";
		}
		else if (commandParameters.find("args") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "args";
		}
		else
		{
			std::wstring scriptBody(CA2W(commandParameters["script"].asString().c_str()));
			wstringstream scriptStream;
			scriptStream << L"(function() { return function(){";
			scriptStream << scriptBody;
			scriptStream << L"};})();";
			const std::wstring script(scriptStream.str());

			Json::Value jsonArgs(commandParameters["args"]);

			SAFEARRAY *args;
			SAFEARRAYBOUND bounds;
			bounds.cElements = jsonArgs.size();
			bounds.lLbound = 0;
			args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

			this->PopulateArgumentArray(manager, args, commandParameters["args"]);

			CComVariant result;
			BrowserWrapper *pWrapper;
			manager->GetCurrentBrowser(&pWrapper);
			int statusCode = pWrapper->ExecuteScript(&script, args, &result);
			::SafeArrayDestroy(args);

			if (statusCode != SUCCESS)
			{
				response->m_statusCode = statusCode;
				response->m_value["message"] = "JavaScript error";
			}
			else
			{
				response->m_statusCode = this->ConvertScriptResult(result, manager, &response->m_value);
			}
		}
	}
};
