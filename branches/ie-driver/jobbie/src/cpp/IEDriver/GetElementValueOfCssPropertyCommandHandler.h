#pragma once
#include "BrowserManager.h"

#define BSTR_VALUE(method, cssName)     if (_wcsicmp(cssName, propertyName) == 0) { CComBSTR bstr; method(&bstr); resultStr = (BSTR)bstr; return resultStr;}
#define VARIANT_VALUE(method, cssName)  if (_wcsicmp(cssName, propertyName) == 0) { CComVariant var; method(&var); resultStr = mangleColour(propertyName, pBrowser->ConvertVariantToWString(&var)); return resultStr;}
#define ADD_COLOR(index, name, hex)		this->colourNames2hex[index][0] = name; this->colourNames2hex[index][1] = hex;

class GetElementValueOfCssPropertyCommandHandler :
	public WebDriverCommandHandler
{
public:

	GetElementValueOfCssPropertyCommandHandler(void)
	{
		ADD_COLOR(0, L"aqua", L"#00ffff");
		ADD_COLOR(1, L"black", L"#000000");
		ADD_COLOR(2, L"blue", L"#0000ff");
		ADD_COLOR(3, L"fuchsia", L"#ff00ff");
		ADD_COLOR(4, L"gray", L"#808080");
		ADD_COLOR(5, L"green", L"#008000");
		ADD_COLOR(6, L"lime", L"#00ff00");
		ADD_COLOR(7, L"maroon", L"#800000");
		ADD_COLOR(8, L"navy", L"#000080");
		ADD_COLOR(9, L"olive", L"#808000");
		ADD_COLOR(10, L"purple", L"#800080");
		ADD_COLOR(11, L"red", L"#ff0000");
		ADD_COLOR(12, L"silver", L"#c0c0c0");
		ADD_COLOR(13, L"teal", L"#008080");
		ADD_COLOR(14, L"white", L"#ffffff");
		ADD_COLOR(15, L"yellow", L"#ffff00");
		ADD_COLOR(16, NULL, NULL);
	}

	virtual ~GetElementValueOfCssPropertyCommandHandler(void)
	{
	}

private:
	const wchar_t* colourNames2hex[17][2];

	std::wstring mangleColour(LPCWSTR propertyName, std::wstring toMangle)
	{
		if (wcsstr(propertyName, L"color") == NULL)
			return toMangle;

		// Look for each of the named colours and mangle them.
		for (int i = 0; colourNames2hex[i][0]; i++) {
			if (_wcsicmp(colourNames2hex[i][0], toMangle.c_str()) == 0)
				return colourNames2hex[i][1];
		}

		return toMangle;
	}

	std::wstring GetElementValueOfCssPropertyCommandHandler::GetPropertyValue(BrowserWrapper *pBrowser, IHTMLElement *pElement, LPCWSTR propertyName)
	{
		std::wstring resultStr(L"");
		CComQIPtr<IHTMLElement2> styled(pElement);
		if (!styled) {
			return resultStr;
		}

		CComBSTR name(propertyName);

		CComPtr<IHTMLCurrentStyle> style;
		styled->get_currentStyle(&style);

		/*
		// This is what I'd like to write.

		CComVariant value;
		style->getAttribute(name, 0, &value);
		return variant2wchar(value);
		*/

		// So the way we've done this strikes me as a remarkably poor idea.

		/*
		Not implemented
			background-position
			clip
			column-count
			column-gap
			column-width
			float
			marker-offset
			opacity
			outline-top-width
			outline-right-width
			outline-bottom-width
			outline-left-width
			outline-top-color
			outline-right-color
			outline-bottom-color
			outline-left-color
			outline-top-style
			outline-right-style
			outline-bottom-style
			outline-left-style
			user-focus
			user-select
			user-modify
			user-input
			white-space
			word-spacing
		*/
		BSTR_VALUE(		style->get_backgroundAttachment,		L"background-attachment");
		VARIANT_VALUE(	style->get_backgroundColor,				L"background-color");
		BSTR_VALUE(		style->get_backgroundImage,				L"background-image");
		BSTR_VALUE(		style->get_backgroundRepeat,			L"background-repeat");
		VARIANT_VALUE(	style->get_borderBottomColor,			L"border-bottom-color");
		BSTR_VALUE(		style->get_borderBottomStyle,			L"border-bottom-style");
		VARIANT_VALUE(	style->get_borderBottomWidth,			L"border-bottom-width");
		VARIANT_VALUE(	style->get_borderLeftColor,				L"border-left-color");
		BSTR_VALUE(		style->get_borderLeftStyle,				L"border-left-style");
		VARIANT_VALUE(	style->get_borderLeftWidth,				L"border-left-width");
		VARIANT_VALUE(	style->get_borderRightColor,			L"border-right-color");
		BSTR_VALUE(		style->get_borderRightStyle,			L"border-right-style");
		VARIANT_VALUE(	style->get_borderRightWidth,			L"border-right-width");
		VARIANT_VALUE(	style->get_borderTopColor,				L"border-top-color");
		BSTR_VALUE(		style->get_borderTopStyle,				L"border-top-style");
		VARIANT_VALUE(	style->get_borderTopWidth,				L"border-top-width");
		VARIANT_VALUE(	style->get_bottom,						L"bottom");
		BSTR_VALUE(		style->get_clear,						L"clear");
		VARIANT_VALUE(	style->get_color,						L"color");
		BSTR_VALUE(		style->get_cursor,						L"cursor");
		BSTR_VALUE(		style->get_direction,					L"direction");
		BSTR_VALUE(		style->get_display,						L"display");
		BSTR_VALUE(		style->get_fontFamily,					L"font-family");
		VARIANT_VALUE(	style->get_fontSize,					L"font-size");
		BSTR_VALUE(		style->get_fontStyle,					L"font-style");
		VARIANT_VALUE(	style->get_fontWeight,					L"font-weight");
		VARIANT_VALUE(	style->get_height,						L"height");
		VARIANT_VALUE(	style->get_left,						L"left");
		VARIANT_VALUE(	style->get_letterSpacing,				L"letter-spacing");
		VARIANT_VALUE(	style->get_lineHeight,					L"line-height");
		BSTR_VALUE(		style->get_listStyleImage,				L"list-style-image");
		BSTR_VALUE(		style->get_listStylePosition,			L"list-style-position");
		BSTR_VALUE(		style->get_listStyleType,				L"list-style-type");
		BSTR_VALUE(		style->get_margin, 						L"margin");
		VARIANT_VALUE(	style->get_marginBottom, 				L"margin-bottom");
		VARIANT_VALUE(	style->get_marginRight, 				L"margin-right");
		VARIANT_VALUE(	style->get_marginTop, 					L"margin-top");
		VARIANT_VALUE(	style->get_marginLeft, 					L"margin-left");
		BSTR_VALUE(		style->get_overflow, 					L"overflow");
		BSTR_VALUE(		style->get_padding, 					L"padding");
		VARIANT_VALUE(	style->get_paddingBottom, 				L"padding-bottom");
		VARIANT_VALUE(	style->get_paddingLeft, 				L"padding-left");
		VARIANT_VALUE(	style->get_paddingRight, 				L"padding-right");
		VARIANT_VALUE(	style->get_paddingTop, 					L"padding-top");
		BSTR_VALUE(		style->get_position, 					L"position");
		VARIANT_VALUE(	style->get_right, 						L"right");
		BSTR_VALUE(		style->get_textAlign, 					L"text-align");
		BSTR_VALUE(		style->get_textDecoration, 				L"text-decoration");
		BSTR_VALUE(		style->get_textTransform, 				L"text-transform");
		VARIANT_VALUE(	style->get_top, 						L"top");
		VARIANT_VALUE(	style->get_verticalAlign,				L"vertical-align");
		BSTR_VALUE(		style->get_visibility,					L"visibility");
		VARIANT_VALUE(	style->get_width,						L"width");
		VARIANT_VALUE(	style->get_zIndex,						L"z-index");

		return resultStr;
	}



protected:

	void GetElementValueOfCssPropertyCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (locatorParameters.find("name") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "name";
		}
		else
		{
			int statusCode = SUCCESS;
			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));
			std::wstring name(CA2W(locatorParameters["name"].c_str()));

			BrowserWrapper *pBrowserWrapper;
			manager->GetCurrentBrowser(&pBrowserWrapper);

			ElementWrapper *pElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pElementWrapper);
			if (statusCode == SUCCESS)
			{
				std::wstring value(this->GetPropertyValue(pBrowserWrapper, pElementWrapper->m_pElement, name.c_str()));

				std::string propertyValue(CW2A(value.c_str()));
				response->m_value = propertyValue;
			}
			else
			{
				response->m_value["message"] = "Element is no longer valid";
			}
			response->m_statusCode = statusCode;
		}
	}
};
