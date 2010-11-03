#include "StdAfx.h"
#include "WebDriverResponse.h"

WebDriverResponse::WebDriverResponse(void) : m_statusCode(0)
{
}

WebDriverResponse::WebDriverResponse(std::wstring json)
{
	Json::Value responseObject;
	Json::Reader reader;
	std::string input(CW2A(json.c_str(), CP_UTF8));
	reader.parse(input, responseObject);
	this->m_statusCode = responseObject["status"].asInt();
	this->m_sessionId = responseObject["sessionId"].asString();
	this->m_value = responseObject["value"];
}

WebDriverResponse::~WebDriverResponse(void)
{
}

std::wstring WebDriverResponse::Serialize(void)
{
	Json::Value jsonObject;
	jsonObject["status"] = m_statusCode;
	jsonObject["sessionId"] = m_sessionId;
	jsonObject["value"] = m_value;
	Json::FastWriter writer;
	std::string output(writer.write(jsonObject));
	std::wstring response(CA2W(output.c_str(), CP_UTF8));
	return response;
}