#pragma once
#include "webdrivercommandhandler.h"

class SwitchToWindowCommandHandler :
	public WebDriverCommandHandler
{
public:
	SwitchToWindowCommandHandler(void);
	~SwitchToWindowCommandHandler(void);

protected:
	void ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, std::string> commandParameters, WebDriverResponse * response);
};
