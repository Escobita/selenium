#pragma once
#include "BrowserManager.h"

class SwitchToFrameCommandHandler :
	public WebDriverCommandHandler
{
public:

	SwitchToFrameCommandHandler(void)
	{
	}

	virtual ~SwitchToFrameCommandHandler(void)
	{
	}

protected:

	void SwitchToFrameCommandHandler::ExecuteInternal(BrowserManager *manager, std::map<std::string, std::string> locatorParameters, std::map<std::string, Json::Value> commandParameters, WebDriverResponse * response)
	{
		std::string value = commandParameters["value"].asString();
		std::transform(value.begin(), value.end(), value.begin(), ::toupper);
		response->m_statusCode = 0;
		response->m_value = "Received value " + value;

		//Json::Value frameId = commandParameters["id"];
		//BrowserWrapper *pWrapper;
		//manager->GetCurrentBrowser(&pWrapper);
		//std::wstringstream pathStream;
		//if (frameId.isString())
		//{
		//	pathStream << CA2W(frameId.asString().c_str());
		//}
		//else if(frameId.isIntegral())
		//{
		//	pathStream << frameId.asInt();
		//}

		//pWrapper->m_pathToFrame = pathStream.str();
		//CComPtr<IHTMLDocument2> pDoc;
		//pWrapper->GetDocument(&pDoc);
		//if (!pDoc)
		//{
		//	pWrapper->m_pathToFrame = L"";
		//	response->m_statusCode = ENOSUCHFRAME;
		//}
		//else
		//{
		//	response->m_statusCode = SUCCESS;
		//}
	}
};
