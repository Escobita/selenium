#include "StdAfx.h"
#include "WebDriverCommandHandler.h"
#include "BrowserManager.h"

WebDriverCommandHandler::WebDriverCommandHandler(void)
{
}

WebDriverCommandHandler::~WebDriverCommandHandler(void)
{
}

void WebDriverCommandHandler::Execute(BrowserManager *manager, std::map<std::string,std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse *response)
{
	this->ExecuteInternal(manager, locatorParameters, commandParameters, response);
}

void WebDriverCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string,std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse *response)
{
}

int WebDriverCommandHandler::GetElement(BrowserManager *manager, std::wstring elementId, ElementWrapper **ppElementWrapper)
{
	int statusCode = EOBSOLETEELEMENT;
	if (manager->m_knownElements.find(elementId) == manager->m_knownElements.end())
	{
		statusCode = 404;
	}
	else
	{
		ElementWrapper *pWrapper = manager->m_knownElements[elementId];

		// Verify that the element is still valid by walking up the
		// DOM tree until we find no parent or the html tag
		CComPtr<IHTMLElement> parent(pWrapper->m_pElement);
		while (parent)
		{
			CComQIPtr<IHTMLHtmlElement> html(parent);
			if (html)
			{
				statusCode = SUCCESS;
				*ppElementWrapper = pWrapper;
				break;
			}

			CComPtr<IHTMLElement> next;
			HRESULT hr = parent->get_parentElement(&next);
			parent = next;
		}
	}

	return statusCode;
}
