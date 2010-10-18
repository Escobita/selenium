#include "StdAfx.h"
#include "IEDriverServer.h"

IEDriverServer::IEDriverServer(int port)
{
	DWORD dwThreadId;
	HWND managerHwnd = NULL;
	HANDLE hEvent = ::CreateEvent(NULL, TRUE, FALSE, EVENT_NAME);
	HANDLE hThread = ::CreateThread(NULL, 0, &BrowserManager::ThreadProc, (LPVOID)&managerHwnd, 0, &dwThreadId);
	::WaitForSingleObject(hEvent, INFINITE);
	::CloseHandle(hEvent);
	::CloseHandle(hThread);

	::SendMessage(managerHwnd, WD_INIT, (WPARAM)port, NULL);
	this->m_managerHwnd = managerHwnd;
	this->populateCommandRepository();
}

IEDriverServer::~IEDriverServer(void)
{
}

int IEDriverServer::processRequest(struct mg_connection *conn, const struct mg_request_info *request_info)
{
	int returnCode = NULL;
	std::string httpVerb = request_info->request_method;
	std::wstring requestBody = L"";
	if (httpVerb == "POST")
	{
		std::vector<char> inputBuffer(1024);
		int bytesRead = mg_read(conn, &inputBuffer[0], 1024);
		int outputBufferSize = ::MultiByteToWideChar(CP_UTF8, 0, &inputBuffer[0], -1, NULL, 0);
		vector<TCHAR> outputBuffer(outputBufferSize);
		::MultiByteToWideChar(CP_UTF8, 0, &inputBuffer[0], -1, &outputBuffer[0], outputBufferSize);
		requestBody = &outputBuffer[0];
	}

	if (strcmp(request_info->uri, "/") == 0)
	{
		this->SendWelcomePage(conn, request_info);
		returnCode = 200;
	}
	else
	{
		std::wstring locatorParameters = L"";
		int command = this->lookupCommand(request_info->uri, httpVerb, &locatorParameters);
		if (command == CommandValue::NoCommand)
		{
		}
		else
		{
			// Compile the serialized JSON representation of the command by hand.
			std::wstringstream commandStream;
			commandStream << L"{ \"command\" : " << command;
			commandStream << L", \"locator\" : " << locatorParameters;
			commandStream << L", \"parameters\" : ";
			if (requestBody.length() > 0)
			{
				commandStream << requestBody;
			}
			else
			{
				commandStream << "{}";
			}
			
			commandStream << L" }";
			std::wstring serializedCommand = commandStream.str();
			std::wstring serializedResponse = this->sendCommandToManager(serializedCommand);
			if (serializedCommand.length() > 0)
			{
				WebDriverResponse response(serializedResponse);
				if (response.m_statusCode == 0)
				{
					this->SendHttpOk(conn, request_info, serializedResponse);
					returnCode = 200;
				}
				else if (response.m_statusCode == 303)
				{
					std::string location = response.m_value.asString();
					response.m_statusCode = 0;
					this->SendHttpSeeOther(conn, request_info, location);
					returnCode = 303;
				}
				else
				{
					this->SendHttpInternalError(conn, request_info, serializedResponse);
					returnCode = 500;
				}
			}
		}
	}

	return returnCode;
}

void IEDriverServer::SendWelcomePage(struct mg_connection* connection,
                const struct mg_request_info* request_info)
{
	std::string pageBody(SERVER_DEFAULT_PAGE);
	std::ostringstream out;
	out << "HTTP/1.1 200 OK\r\n"
		<< "Content-Length: " << strlen(pageBody.c_str()) << "\r\n"
		<< "Content-Type: text/html; charset=UTF-8\r\n"
		<< "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
		<< "Accept-Ranges: bytes\r\n"
		<< "Connection: close\r\n\r\n";
	if (strcmp(request_info->request_method, "HEAD") != 0)
	{
		out << pageBody << "\r\n";
	}

	mg_write(connection, out.str().c_str(), out.str().size());
}

// The standard HTTP Status codes are implemented below.  Chrome uses
// OK, See Other, Not Found, Method Not Allowed, and Internal Error.
// Internal Error, HTTP 500, is used as a catch all for any issue
// not covered in the JSON protocol.
void IEDriverServer::SendHttpOk(struct mg_connection* connection,
                const struct mg_request_info* request_info,
				std::wstring body)
{
	std::string narrowBody(CW2A(body.c_str()));
	std::ostringstream out;
	out << "HTTP/1.1 200 OK\r\n"
		<< "Content-Length: " << strlen(narrowBody.c_str()) << "\r\n"
		<< "Content-Type: application/json; charset=UTF-8\r\n"
		<< "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
		<< "Accept-Ranges: bytes\r\n"
		<< "Connection: close\r\n\r\n";
	if (strcmp(request_info->request_method, "HEAD") != 0)
	{
		out << narrowBody << "\r\n";
	}

	mg_write(connection, out.str().c_str(), out.str().size());
}

void IEDriverServer::SendHttpInternalError(struct mg_connection* connection,
                           const struct mg_request_info* request_info,
						   std::wstring body)
{
	std::string narrowBody(CW2A(body.c_str()));
	std::ostringstream out;
	out << "HTTP/1.1 500 Internal Server Error\r\n"
		<< "Content-Length: " << strlen(narrowBody.c_str()) << "\r\n"
		<< "Content-Type: application/json; charset=UTF-8\r\n"
		<< "Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept\r\n"
		<< "Accept-Ranges: bytes\r\n"
		<< "Connection: close\r\n\r\n";
	if (strcmp(request_info->request_method, "HEAD") != 0)
	{
		out << narrowBody << "\r\n";
	}

	mg_write(connection, out.str().c_str(), out.str().size());
}

void IEDriverServer::SendHttpSeeOther(struct mg_connection* connection,
							const struct mg_request_info* request_info,
							std::string location)
{
	std::ostringstream out;
	out << "HTTP/1.1 303 See Other\r\n"
		<< "Location: " << location << "\r\n"
		<< "Content-Type: text/html\r\n"
		<< "Content-Length: 0\r\n\r\n";

	mg_write(connection, out.str().c_str(), out.str().size());
}

std::wstring IEDriverServer::sendCommandToManager(std::wstring serializedCommand)
{
	// Sending a command consists of four actions:
	// 1. Setting the command to be executed
	// 2. Executing the command
	// 3. Waiting for the response to be populated
	// 4. Retrieving the response
	::SendMessage(this->m_managerHwnd, WD_SET_COMMAND, NULL, (LPARAM)serializedCommand.c_str());
	::PostMessage(this->m_managerHwnd, WD_EXEC_COMMAND, NULL, NULL);
	
	int responseLength = (int)::SendMessage(this->m_managerHwnd, WD_GET_RESPONSE_LENGTH, NULL, NULL);
	while (responseLength == 0)
	{
		::Sleep(100);
		responseLength = (int)::SendMessage(this->m_managerHwnd, WD_GET_RESPONSE_LENGTH, NULL, NULL);
	}

	// Must add one to the length to handle the terminating character.
	std::vector<TCHAR> responseBuffer(responseLength + 1);
	::SendMessage(this->m_managerHwnd, WD_GET_RESPONSE, NULL, (LPARAM)&responseBuffer[0]);
	std::wstring serializedResponse(&responseBuffer[0]);
	responseBuffer.clear();
	return serializedResponse;
}

int IEDriverServer::lookupCommand(std::string uri, std::string httpVerb, std::wstring *locator)
{
	int value = CommandValue::NoCommand;
	std::map<std::string, map<std::string, int>>::iterator end = this->m_commandRepository.end();
	for (std::map<std::string, map<std::string, int>>::iterator it = this->m_commandRepository.begin(); it != end; ++it)
	{
		std::vector<std::string> locatorParamNames;
		std::string urlCandidate = (*it).first;
		size_t paramStartPos = urlCandidate.find_first_of(":");
		while (paramStartPos != std::string.npos)
		{
			size_t paramLen = std::string.npos;
			size_t paramEndPos = urlCandidate.find_first_of("/", paramStartPos);
			if (paramEndPos != std::string.npos)
			{
				paramLen = paramEndPos - paramStartPos;
			}

			// Skip the colon
			std::string paramName = urlCandidate.substr(paramStartPos + 1, paramLen - 1);
			locatorParamNames.push_back(paramName);
			urlCandidate.replace(paramStartPos, paramLen, "([^/]+)");
			paramStartPos = urlCandidate.find_first_of(":");
		}

		std::string::const_iterator uriStart = uri.begin();
		std::string::const_iterator uriEnd = uri.end(); 
		std::tr1::regex matcher("^" + urlCandidate + "$");
		std::tr1::match_results<std::string::const_iterator> matches;
		if (std::tr1::regex_search(uriStart, uriEnd, matches, matcher))
		{
			if (it->second.find(httpVerb) != it->second.end())
			{
				value = it->second[httpVerb];
				std::stringstream paramStream;
				paramStream << "{";
				size_t paramCount = locatorParamNames.size();
				for (int i = 0; i < paramCount; i++)
				{
					if (i != 0)
					{
						paramStream << ",";
					}

					paramStream << " \"" << locatorParamNames[i] << "\" : \"" << matches[i + 1] << "\"";
				}

				paramStream << " }";
				std::string param = paramStream.str();
				std::wstring wideParam(param.begin(), param.end());
				locator->append(wideParam);
			}
			break;
		}
	}

	return value;
}

void IEDriverServer::populateCommandRepository()
{
	this->m_commandRepository["/session"]["POST"] = CommandValue::NewSession;
	this->m_commandRepository["/session/:sessionid"]["GET"] = CommandValue::GetSessionCapabilities;
	this->m_commandRepository["/session/:sessionid"]["DELETE"] = CommandValue::Quit;
	this->m_commandRepository["/session/:sessionid/window_handle"]["GET"] = CommandValue::GetCurrentWindowHandle;
	this->m_commandRepository["/session/:sessionid/window_handles"]["GET"] = CommandValue::GetWindowHandles;
	this->m_commandRepository["/session/:sessionid/url"]["GET"] = CommandValue::GetCurrentUrl;
	this->m_commandRepository["/session/:sessionid/url"]["POST"] = CommandValue::Get;
	this->m_commandRepository["/session/:sessionid/forward"]["POST"] = CommandValue::GoForward;
	this->m_commandRepository["/session/:sessionid/back"]["POST"] = CommandValue::GoBack;
	this->m_commandRepository["/session/:sessionid/refresh"]["POST"] = CommandValue::Refresh;
	this->m_commandRepository["/session/:sessionid/speed"]["GET"] = CommandValue::GetSpeed;
	this->m_commandRepository["/session/:sessionid/speed"]["POST"] = CommandValue::SetSpeed;
	this->m_commandRepository["/session/:sessionid/execute"]["POST"] = CommandValue::ExecuteScript;
	this->m_commandRepository["/session/:sessionid/screenshot"]["GET"] = CommandValue::Screenshot;
	this->m_commandRepository["/session/:sessionid/frame"]["POST"] = CommandValue::SwitchToFrame;
	this->m_commandRepository["/session/:sessionid/window"]["POST"] = CommandValue::SwitchToWindow;
	this->m_commandRepository["/session/:sessionid/window"]["DELETE"] = CommandValue::Close;
	this->m_commandRepository["/session/:sessionid/cookie"]["GET"] = CommandValue::GetAllCookies;
	this->m_commandRepository["/session/:sessionid/cookie"]["POST"] = CommandValue::AddCookie;
	this->m_commandRepository["/session/:sessionid/cookie"]["DELETE"] = CommandValue::DeleteAllCookies;
	this->m_commandRepository["/session/:sessionid/cookie/:name"]["DELETE"] = CommandValue::DeleteCookie;
	this->m_commandRepository["/session/:sessionid/source"]["GET"] = CommandValue::GetPageSource;
	this->m_commandRepository["/session/:sessionid/title"]["GET"] = CommandValue::GetTitle;
	this->m_commandRepository["/session/:sessionid/element"]["POST"] = CommandValue::FindElement;
	this->m_commandRepository["/session/:sessionid/elements"]["POST"] = CommandValue::FindElements;
	this->m_commandRepository["/session/:sessionid/timeouts/implicit_wait"]["POST"] = CommandValue::ImplicitlyWait;
	this->m_commandRepository["/session/:sessionid/element/active"]["POST"] = CommandValue::GetActiveElement;
	this->m_commandRepository["/session/:sessionid/element/:id/element"]["POST"] = CommandValue::FindChildElement;
	this->m_commandRepository["/session/:sessionid/element/:id/elements"]["POST"] = CommandValue::FindChildElements;
	this->m_commandRepository["/session/:sessionid/element/:id"]["GET"] = CommandValue::DescribeElement;
	this->m_commandRepository["/session/:sessionid/element/:id/click"]["POST"] = CommandValue::ClickElement;
	this->m_commandRepository["/session/:sessionid/element/:id/text"]["GET"] = CommandValue::GetElementText;
	this->m_commandRepository["/session/:sessionid/element/:id/submit"]["POST"] = CommandValue::SubmitElement;
	this->m_commandRepository["/session/:sessionid/element/:id/value"]["GET"] = CommandValue::GetElementValue;
	this->m_commandRepository["/session/:sessionid/element/:id/value"]["POST"] = CommandValue::SendKeysToElement;
	this->m_commandRepository["/session/:sessionid/element/:id/name"]["GET"] = CommandValue::GetElementTagName;
	this->m_commandRepository["/session/:sessionid/element/:id/clear"]["POST"] = CommandValue::ClearElement;
	this->m_commandRepository["/session/:sessionid/element/:id/selected"]["GET"] = CommandValue::IsElementSelected;
	this->m_commandRepository["/session/:sessionid/element/:id/selected"]["POST"] = CommandValue::SetElementSelected;
	this->m_commandRepository["/session/:sessionid/element/:id/toggle"]["POST"] = CommandValue::ToggleElement;
	this->m_commandRepository["/session/:sessionid/element/:id/enabled"]["GET"] = CommandValue::IsElementEnabled;
	this->m_commandRepository["/session/:sessionid/element/:id/displayed"]["GET"] = CommandValue::IsElementDisplayed;
	this->m_commandRepository["/session/:sessionid/element/:id/location"]["GET"] = CommandValue::GetElementLocation;
	this->m_commandRepository["/session/:sessionid/element/:id/location_in_view"]["GET"] = CommandValue::GetElementLocationOnceScrolledIntoView;
	this->m_commandRepository["/session/:sessionid/element/:id/size"]["GET"] = CommandValue::GetElementSize;
	this->m_commandRepository["/session/:sessionid/element/:id/css/:propertyName"]["GET"] = CommandValue::GetElementValueOfCssProperty;
	this->m_commandRepository["/session/:sessionid/element/:id/attribute/:name"]["GET"] = CommandValue::GetElementAttribute;
	this->m_commandRepository["/session/:sessionid/element/:id/equals/:other"]["GET"] = CommandValue::ElementEquals;
	this->m_commandRepository["/session/:sessionid/element/:id/hover"]["POST"] = CommandValue::HoverOverElement;
	this->m_commandRepository["/session/:sessionid/element/:id/drag"]["POST"] = CommandValue::DragElement;

	/*
	commandDictionary.Add(DriverCommand.DefineDriverMapping, new CommandInfo(CommandInfo.PostCommand, "/config/drivers"));
	commandDictionary.Add(DriverCommand.SetBrowserVisible, new CommandInfo(CommandInfo.PostCommand, "/session/{sessionId}/visible"));
	commandDictionary.Add(DriverCommand.IsBrowserVisible, new CommandInfo(CommandInfo.GetCommand, "/session/{sessionId}/visible"));
	*/
}

void * event_handler(enum mg_event event_raised, 
								struct mg_connection *conn, 
								const struct mg_request_info *request_info)
{
	int returnCode = NULL;
	if (event_raised == MG_NEW_REQUEST)
	{
		returnCode = server->processRequest(conn, request_info);
	}

	return &returnCode;
}

IEDriverServer* StartServer(int port)
{
	char buffer[6];
	_itoa(port, buffer, 10);
	char* options[] = { "listening_ports", buffer, "access_control_list", "-0.0.0.0/0,+127.0.0.1", NULL };
	server = new IEDriverServer(port);
	ctx = mg_start(event_handler, (const char **)options);
	return server;
}

void StopServer(IEDriverServer *server)
{
	::SendMessage(server->m_managerHwnd, WM_CLOSE, NULL, NULL);
	mg_stop(ctx);
}
