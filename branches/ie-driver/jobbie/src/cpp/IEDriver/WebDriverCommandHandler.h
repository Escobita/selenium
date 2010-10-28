#pragma once
#include "json.h"
#include <map>
#include <string>

using namespace std;

// Forward declaration of classes to avoid
// circular include files.
class BrowserManager;
class ElementWrapper;
class WebDriverResponse;

class WebDriverCommandHandler
{
public:
	WebDriverCommandHandler(void);
	virtual ~WebDriverCommandHandler(void);
	void Execute(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response);

protected:
	virtual void ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response);
	int GetElement(BrowserManager *manager, std::wstring elementId, ElementWrapper **ppElementWrapper);
};
