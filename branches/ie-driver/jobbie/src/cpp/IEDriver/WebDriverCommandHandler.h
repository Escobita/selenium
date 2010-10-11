#pragma once
#include <map>
#include <string>

using namespace std;

// Forward declaration of classes to avoid
// circular include files.
class BrowserManager;
class WebDriverResponse;

class WebDriverCommandHandler
{
public:
	WebDriverCommandHandler(void);
	virtual ~WebDriverCommandHandler(void);
	void Execute(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, std::string> commandParameters, WebDriverResponse * response);

protected:
	bool m_ignorePreExecutionWait;
	bool m_ignorePostExecutionWait;
	virtual void ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, std::string> commandParameters, WebDriverResponse * response);
};
