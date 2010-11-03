#pragma once
#include "BrowserManager.h"
#include <ctime>

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
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str(), CP_UTF8);
			std::wstring value = CA2W(commandParameters["value"].asString().c_str(), CP_UTF8);
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);

			int timeout(manager->GetImplicitWaitTimeout());
			clock_t end = clock() + (timeout / 1000 * CLOCKS_PER_SEC);
			if (timeout > 0 && timeout < 1000) 
			{
				end += 1 * CLOCKS_PER_SEC;
			}


			int statusCode = SUCCESS;
			do
			{
				statusCode = pFinder->FindElements(manager, NULL, value, &foundElements);
				if (statusCode == SUCCESS && foundElements.size() > 0)
				{
					break;
				}
			}
			while (clock() < end);

			if (statusCode == SUCCESS)
			{
				Json::Value elementArray(Json::arrayValue);
				for (int i = 0; i < foundElements.size(); ++i)
				{
					elementArray[i] = foundElements[i]->ConvertToJson();
				}

				response->m_value = elementArray;
			}

			response->m_statusCode = statusCode;
		}
	}
};
