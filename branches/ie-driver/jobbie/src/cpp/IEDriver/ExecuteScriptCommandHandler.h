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
				destDbl.lVal = (LONG) dblNumber;	
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

	int ConvertScriptResult(CComVariant result, Json::Value *value)
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
				// TODO: handle arrays and elements.
				//LPCWSTR itemType = driver->ie->getScriptResultType(&(result->result));
				//std::string itemTypeStr;
				//cw2string(itemType, itemTypeStr);

				//LOG(DEBUG) << "Got type: " << itemTypeStr;
				//// If it's a Javascript array or an HTML Collection - type 8 will
				//// indicate the driver that this is ultimately an array.
				//if ((itemTypeStr == "JavascriptArray") ||
				//	(itemTypeStr == "HtmlCollection")) {
				//*type = TYPE_ARRAY;
				//} else {
				//*type = TYPE_ELEMENT;
				//}
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
			}
			else
			{
				response->m_statusCode = this->ConvertScriptResult(result, &response->m_value);
			}
		}
	}
};
