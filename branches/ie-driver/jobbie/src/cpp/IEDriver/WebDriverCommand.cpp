#include "StdAfx.h"
#include "WebDriverCommand.h"

WebDriverCommand::WebDriverCommand()
{
	this->m_commandValue = 0;
}

WebDriverCommand::~WebDriverCommand(void)
{
}

void WebDriverCommand::populate(std::string jsonCommand)
{
	// Clear the existing maps.
	this->m_commandParameters.clear();
	this->m_locatorParameters.clear();
	
	Json::Value root;
	Json::Reader reader;
	BOOL successfulParse = reader.parse(jsonCommand, root);
	if (!successfulParse)
	{
		// report to the user the failure and their locations in the document.
		std::cout  << "Failed to parse configuration\n"
				   << reader.getFormatedErrorMessages();
	}

	Json::Value cmdVal = root.get("command", "0");
	m_commandValue = root.get("command", 0).asInt();
	if (m_commandValue != 0)
	{
		Json::Value locatorParamObject = root["locator"];
		Json::Value::iterator end = locatorParamObject.end();
		for (Json::Value::iterator it = locatorParamObject.begin(); it != end; ++it)
		{
			std::string key = it.key().asString();
			std::string value = locatorParamObject[key].asString();
			this->m_locatorParameters[key] = value;
		}

		Json::Value commandParamObject = root["parameters"];
		end = commandParamObject.end();
		for (Json::Value::iterator it = commandParamObject.begin(); it != end; ++it)
		{
			std::string key = it.key().asString();
			std::string value = commandParamObject[key].asString();
			this->m_commandParameters[key] = value;
		}
	}
}

