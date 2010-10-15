#pragma once
#include <string>
#include "json.h"

class ElementWrapper
{
public:
	ElementWrapper(CComPtr<IHTMLElement> element);
	virtual ~ElementWrapper(void);
	std::wstring m_elementId;
	CComPtr<IHTMLElement> m_pElement;
	Json::Value ConvertToJson();
};
