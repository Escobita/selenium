#ifndef WEBDRIVER_IE_EXECUTESCRIPTCOMMANDHANDLER_H_
#define WEBDRIVER_IE_EXECUTESCRIPTCOMMANDHANDLER_H_

#include "BrowserManager.h"

namespace webdriver {

class ExecuteScriptCommandHandler : public WebDriverCommandHandler {
public:
	ExecuteScriptCommandHandler(void) {
	}

	virtual ~ExecuteScriptCommandHandler(void) {
	}

protected:
	void ExecuteScriptCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locator_parameters, std::map<std::string, Json::Value> command_parameters, WebDriverResponse * response)
	{
		if (command_parameters.find("script") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "script";
		} else if (command_parameters.find("args") == command_parameters.end()) {
			response->set_status_code(400);
			response->m_value = "args";
		} else {
			std::wstring script_body(CA2W(command_parameters["script"].asString().c_str(), CP_UTF8));
			wstringstream script_stream;
			script_stream << L"(function() { return function(){";
			script_stream << script_body;
			script_stream << L"};})();";
			const std::wstring script(script_stream.str());

			Json::Value json_args(command_parameters["args"]);

			SAFEARRAY *args;
			SAFEARRAYBOUND bounds;
			bounds.cElements = json_args.size();
			bounds.lLbound = 0;
			args = ::SafeArrayCreate(VT_VARIANT, 1, &bounds);

			this->PopulateArgumentArray(manager, args, json_args);

			CComVariant result;
			BrowserWrapper *browser_wrapper;
			manager->GetCurrentBrowser(&browser_wrapper);
			int status_code = browser_wrapper->ExecuteScript(&script, args, &result);
			::SafeArrayDestroy(args);

			if (status_code != SUCCESS) {
				response->set_status_code(status_code);
				response->m_value["message"] = "JavaScript error";
			} else {
				response->set_status_code(this->ConvertScriptResult(result, manager, &response->m_value));
			}
		}
	}

private:
	std::wstring ExecuteScriptCommandHandler::GetScriptResultObjectType(CComVariant* script_result) {
		CComQIPtr<IHTMLElementCollection> is_collection(script_result->pdispVal);
		if (is_collection) {
			return L"HtmlCollection";
		}

		CComQIPtr<IHTMLElement> is_element(script_result->pdispVal);
		if (is_element) {
			return L"HtmlElement";
		}

		// Other possible interfaces: IHTMLFrameBase, IHTMLFrameElement
		// The distinction is not important for now.

		CComPtr<ITypeInfo> typeinfo;
		HRESULT get_type_info_result = script_result->pdispVal->GetTypeInfo(0, LOCALE_USER_DEFAULT, &typeinfo);
		TYPEATTR* type_attr;
		CComBSTR name;
		if (SUCCEEDED(get_type_info_result) && SUCCEEDED(typeinfo->GetTypeAttr(&type_attr))
			&& SUCCEEDED(typeinfo->GetDocumentation(-1, &name, 0, 0, 0))) {
			// If the name is JScriptTypeInfo then *assume* this is a Javascript array.
			// Note that Javascript can return functions which will have the same
			// type - the only way to be sure is to run some more Javascript code to
			// see if this object has a length attribute. This does not seem necessary
			// now.
			// (For future reference, GUID is {C59C6B12-F6C1-11CF-8835-00A0C911E8B2})
			typeinfo->ReleaseTypeAttr(type_attr);
			if (name == L"JScriptTypeInfo") {
				return L"JavascriptArray";
			}
		}

		return L"Unknown";
	}

	int ExecuteScriptCommandHandler::PopulateArgumentArray(BrowserManager *manager, SAFEARRAY * args, Json::Value json_args) {
		int status_code = SUCCESS;
		for (UINT arg_index = 0; arg_index < json_args.size(); ++arg_index)
		{
			LONG index = (LONG)arg_index;
			Json::Value arg = json_args[arg_index];
			if (arg.isString()) {
				std::wstring value(CA2W(arg.asString().c_str(), CP_UTF8));
				CComVariant dest_str(value.c_str());
				::SafeArrayPutElement(args, &index, &dest_str);
			} else if (arg.isInt()) {
				int int_number(arg.asInt());
				VARIANT dest_int;
				dest_int.vt = VT_I4;
				dest_int.lVal = (LONG) int_number;	
				::SafeArrayPutElement(args, &index, &dest_int);
			} else if (arg.isDouble()) {
				double dbl_number(arg.asDouble());
				VARIANT dest_dbl;
				dest_dbl.vt = VT_R8;
				dest_dbl.dblVal = dbl_number;	
				::SafeArrayPutElement(args, &index, &dest_dbl);
			} else if (arg.isBool()) {
				bool bool_arg(arg.asBool());
				VARIANT dest_bool;
				dest_bool.vt = VT_BOOL;
				dest_bool.boolVal = bool_arg;
				::SafeArrayPutElement(args, &index, &dest_bool);
			} else if (arg.isObject() && arg.isMember("ELEMENT")) {
				std::wstring element_id(CA2W(arg["ELEMENT"].asString().c_str(), CP_UTF8));

				ElementWrapper *element_wrapper;
				status_code = this->GetElement(manager, element_id, &element_wrapper);
				if (status_code == SUCCESS)
				{
					VARIANT dest_disp;
					dest_disp.vt = VT_DISPATCH;
					dest_disp.pdispVal = element_wrapper->element();
					::SafeArrayPutElement(args, &index, &dest_disp);
				}
			}
		}

		return status_code;
	}

	int ConvertScriptResult(CComVariant result, BrowserManager *manager, Json::Value *value) {
		std::string strVal;
		switch (result.vt) {
			case VT_BSTR:
				strVal = CW2A(result.bstrVal, CP_UTF8);
				*value = strVal;
				break;

			case VT_I4:
			case VT_I8:
				*value = result.lVal;
				break;

			case VT_BOOL:
				*value = result.boolVal == VARIANT_TRUE;
				break;

			case VT_DISPATCH: {
					std::wstring itemType = this->GetScriptResultObjectType(&result);
					if (itemType == L"JavascriptArray" || itemType == L"HtmlCollection") {
						BrowserWrapper *browser_wrapper;
						manager->GetCurrentBrowser(&browser_wrapper);
						Json::Value result_array(Json::arrayValue);
						// Prepare an array for the Javascript execution, containing only one
						// element - the original returned array from a JS execution.
						SAFEARRAYBOUND length_query;
						length_query.cElements = 1;
						length_query.lLbound = 0;

						SAFEARRAY* length_args = SafeArrayCreate(VT_VARIANT, 1, &length_query);
						LONG index = 0;
						SafeArrayPutElement(length_args, &index, &result);

						CComVariant length_variant;
						std::wstring get_length_script(L"(function(){return function() {return arguments[0].length;}})();");
						int length_result = browser_wrapper->ExecuteScript(
							&get_length_script,
							length_args, &length_variant);

						SafeArrayDestroy(length_args);
						// Expect the return type to be an integer. A non-integer means this was
						// not an array after all.
						if (length_variant.vt != VT_I4) {
							return EUNEXPECTEDJSERROR;
						}

						LONG length = length_variant.lVal;

						// Prepare an array for the Javascript execution, containing only one
						// element - the original returned array from a JS execution.
						SAFEARRAYBOUND item_query;
						item_query.cElements = 2;
						item_query.lLbound = 0;

						for (LONG i = 0; i < length; ++i) {
							SAFEARRAY* item_args = SafeArrayCreate(VT_VARIANT, 1, &item_query);
							LONG index = 0;
							::SafeArrayPutElement(item_args, &index, &result);

							CComVariant index_variant;
							index_variant.vt = VT_I4;
							index_variant.lVal = i;	
							index++;
							::SafeArrayPutElement(item_args, &index, &index_variant);

							CComVariant item_variant;
							std::wstring get_array_item_script(L"(function(){return function() {return arguments[0][arguments[1]];}})();"); 
							int lengthResult = browser_wrapper->ExecuteScript(&get_array_item_script,
								item_args, &item_variant);

							::SafeArrayDestroy(item_args);
							Json::Value array_item_result;
							int array_item_status = this->ConvertScriptResult(item_variant, manager, &array_item_result);
							result_array[i] = array_item_result;
						}
						*value = result_array;
					} else {
						IHTMLElement *node = (IHTMLElement*) result.pdispVal;
						ElementWrapper *element_wrapper = new ElementWrapper(node);
						manager->AddManagedElement(element_wrapper);
						*value = element_wrapper->ConvertToJson();
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
};

} // namespace webdriver

#endif // WEBDRIVER_IE_EXECUTESCRIPTCOMMANDHANDLER_H_
