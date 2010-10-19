#pragma once
#include "BrowserManager.h"

class FindChildElementsCommandHandler :
	public WebDriverCommandHandler
{
public:

	FindChildElementsCommandHandler(void)
	{
	}

	virtual ~FindChildElementsCommandHandler(void)
	{
	}

protected:

	void FindChildElementsCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		if (locatorParameters.find("id") == locatorParameters.end())
		{
			response->m_statusCode = 400;
			response->m_value = "id";
		}
		else if (commandParameters.find("using") == commandParameters.end())
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
			int statusCode = SUCCESS;
			std::wstring mechanism = CA2W(commandParameters["using"].asString().c_str());
			std::wstring value = CA2W(commandParameters["value"].asString().c_str());
			ElementFinder *pFinder(manager->m_elementFinders[mechanism]);

			std::wstring elementId(CA2W(locatorParameters["id"].c_str()));

			ElementWrapper *pParentElementWrapper;
			statusCode = this->GetElement(manager, elementId, &pParentElementWrapper);

			if (statusCode == SUCCESS)
			{
				std::vector<ElementWrapper *> foundElements;
				int statusCode = pFinder->FindElements(manager, pParentElementWrapper, value, &foundElements);
				if (statusCode == SUCCESS)
				{
					for (int i = 0; i < foundElements.size(); ++i)
					{
						response->m_value[i] = foundElements[i]->ConvertToJson();
					}
				}
			}

			response->m_statusCode = statusCode;
		}
	}
};
