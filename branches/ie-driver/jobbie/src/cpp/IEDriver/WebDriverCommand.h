#pragma once
#include "json.h"
#include <map>

using namespace std;

class WebDriverCommand
{
public:
	WebDriverCommand();
	virtual ~WebDriverCommand(void);
	void populate(std::string jsonCommand);
	int m_commandValue;
	std::map<std::string, std::string> m_locatorParameters;
	std::map<std::string, std::string> m_commandParameters;
};
