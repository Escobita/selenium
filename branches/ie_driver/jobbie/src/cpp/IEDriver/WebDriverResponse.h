#ifndef WEBDRIVER_IE_WEBDRIVERRESPONSE_H_
#define WEBDRIVER_IE_WEBDRIVERRESPONSE_H_

#include <string>
#include "json.h"

using namespace std;

namespace webdriver {

class WebDriverResponse {
public:
	WebDriverResponse(void);
	WebDriverResponse(std::string session_id);
	virtual ~WebDriverResponse(void);
	std::wstring Serialize(void);
	void Deserialize(std::wstring json);

	int status_code(void) { return this->status_code_; }
	void set_status_code(int value) { this->status_code_ = value; }

	Json::Value m_value;

private:
	int status_code_;
	std::string session_id_;
};

} // namespace webdriver

#endif // WEBDRIVER_IE_WEBDRIVERRESPONSE_H_
