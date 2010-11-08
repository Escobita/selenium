#include "StdAfx.h"
#include "WebDriverResponse.h"

namespace webdriver {

WebDriverResponse::WebDriverResponse(void) : status_code_(0), session_id_("") {
}

WebDriverResponse::WebDriverResponse(std::string session_id) {
	this->session_id_ = session_id;
	this->status_code_ = 0;
}

WebDriverResponse::WebDriverResponse(std::wstring json) {
	Json::Value responseObject;
	Json::Reader reader;
	std::string input(CW2A(json.c_str(), CP_UTF8));
	reader.parse(input, responseObject);
	this->status_code_ = responseObject["status"].asInt();
	this->session_id_ = responseObject["sessionId"].asString();
	this->m_value = responseObject["value"];
}

WebDriverResponse::~WebDriverResponse(void) {
}

std::wstring WebDriverResponse::Serialize(void) {
	Json::Value jsonObject;
	jsonObject["status"] = this->status_code_;
	jsonObject["sessionId"] = this->session_id_;
	jsonObject["value"] = this->m_value;
	Json::FastWriter writer;
	std::string output(writer.write(jsonObject));
	std::wstring response(CA2W(output.c_str(), CP_UTF8));
	return response;
}

} // namespace webdriver