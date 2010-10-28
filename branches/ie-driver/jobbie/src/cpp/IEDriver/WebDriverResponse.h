#pragma once
#include <string>
#include "json.h"

using namespace std;

class WebDriverResponse
{
public:
	WebDriverResponse(void);
	WebDriverResponse(std::wstring json);
	virtual ~WebDriverResponse(void);
	int m_statusCode;
	std::string m_sessionId;
	Json::Value m_value;
	std::wstring Serialize(void);
};
