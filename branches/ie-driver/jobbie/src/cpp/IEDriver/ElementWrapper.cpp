#include "StdAfx.h"
#include "ElementWrapper.h"

ElementWrapper::ElementWrapper(CComPtr<IHTMLElement> element)
{
	// NOTE: COM should be initialized on this thread, so we
	// could use CoCreateGuid() and StringFromGUID2() instead.
	UUID idGuid;
	RPC_WSTR pszUuid = NULL;
	::UuidCreate(&idGuid);
	::UuidToString(&idGuid, &pszUuid);

	// RPC_WSTR is currently typedef'd in RpcDce.h (pulled in by rpc.h)
	// as unsigned short*. It needs to be typedef'd as wchar_t* 
	wchar_t* pwStr = reinterpret_cast<wchar_t*>(pszUuid);
	this->m_elementId = pwStr;

	::RpcStringFree(&pszUuid);

	this->m_pElement = element;
}

ElementWrapper::~ElementWrapper(void)
{
}

Json::Value ElementWrapper::ConvertToJson()
{
	Json::Value jsonWrapper;
	std::string id(CW2A(this->m_elementId.c_str()));
	jsonWrapper["ELEMENT"] = id;
	return jsonWrapper;
}