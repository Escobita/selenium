#pragma once
#include "BrowserManager.h"

class FindElementsCommandHandler :
	public WebDriverCommandHandler
{
public:

	FindElementsCommandHandler(void)
	{
	}

	virtual ~FindElementsCommandHandler(void)
	{
	}

protected:

	void FindElementsCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (commandParameters.find("using") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "using";
		}
		else if (commandParameters.find("value") == commandParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "value";
		}
		else
		{
			std::vector<ElementWrapper *> foundElements;
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str());
			std::wstring value = CA2W(commandParameters["value"].asString().c_str());
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);
			int statusCode = pFinder->FindElements(manager, NULL, value, &foundElements);
			if (statusCode == SUCCESS)
			{
				for (int i = 0; i < foundElements.size(); ++i)
				{
					response->m_value[i] = foundElements[i]->ConvertToJson();
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
