#include "StdAfx.h"
#include "BrowserManager.h"

BrowserManager::BrowserManager(void)
{
	// NOTE: Use UuidCreate here to avoid unnecessarily initializing COM
	// on this thread. It will be initialized on the named pipe worker
	// thread.
	UUID idGuid;
	RPC_WSTR pszUuid = NULL;
	::UuidCreate(&idGuid);
	::UuidToString(&idGuid, &pszUuid);

	// RPC_WSTR is currently typedef'd in RpcDce.h (pulled in by rpc.h)
	// as unsigned short*. It needs to be typedef'd as wchar_t* 
	wchar_t* pwStr = reinterpret_cast<wchar_t*>( pszUuid );
	m_managerId = pwStr;

	::RpcStringFree(&pszUuid);

	this->m_currentBrowser = L"";
}

BrowserManager::~BrowserManager(void)
{
}

DWORD WINAPI BrowserManager::ThreadProc(LPVOID lpParameter)
{
	BrowserManager* pManager = reinterpret_cast<BrowserManager*>(lpParameter);
	pManager->Start();
	return 0;
}

void BrowserManager::Start(void)
{
	CoInitializeEx(NULL, COINIT_APARTMENTTHREADED);
	std::basic_string<TCHAR> pipeName = L"\\\\.\\pipe\\" + this->m_managerId;
	HANDLE hPipe = ::CreateNamedPipe(pipeName.c_str(), 
								PIPE_ACCESS_DUPLEX, 
								PIPE_TYPE_MESSAGE | PIPE_READMODE_MESSAGE | PIPE_WAIT, 
								PIPE_UNLIMITED_INSTANCES,
								1024,
								1024,
								0,
								NULL);

	if (hPipe == INVALID_HANDLE_VALUE)
	{
		DWORD dwError = ::GetLastError();
	}

	this->m_isRunning = true;
	WebDriverCommand* browserCommand = new WebDriverCommand();
	while (browserCommand->m_commandValue != CommandValue::Quit)
	{
		DWORD errCode = 0;
		BOOL result = ::ConnectNamedPipe(hPipe, NULL);
		vector<CHAR> inputBuffer(1024);
		DWORD bytesRead = 0;

		::ReadFile(hPipe, &inputBuffer[0], 1024, &bytesRead, NULL);
		int bytesRequired = ::MultiByteToWideChar(CP_UTF8, 0, &inputBuffer[0], -1, NULL, 0);
		vector<TCHAR> outputBuffer(bytesRequired);
		bytesRequired = ::MultiByteToWideChar(CP_UTF8, 0, &inputBuffer[0], -1, &outputBuffer[0], bytesRequired);

		std::wstring jsoncommand = &outputBuffer[0];
		std::string narrowCmd(CW2A(jsoncommand.c_str()));
		browserCommand->populate(narrowCmd);

		WebDriverResponse response = DispatchCommand(browserCommand);
		std::wstring serializedResponse = response.serialize();

		int bytesWritten = ::WideCharToMultiByte(CP_UTF8, 0, serializedResponse.c_str(), -1, NULL, 0, NULL, NULL);
		vector<CHAR> convertBuffer(bytesWritten);
		bytesWritten = ::WideCharToMultiByte(CP_UTF8, 0, serializedResponse.c_str(), -1, &convertBuffer[0], bytesWritten, NULL, NULL);
		if (bytesWritten == 0)
		{
			errCode = ::GetLastError();
		}

		::WriteFile(hPipe, &convertBuffer[0], bytesWritten, &bytesRead, NULL);
		::FlushFileBuffers(hPipe);
		::DisconnectNamedPipe(hPipe);
	}

	this->m_isRunning = false;
	::CloseHandle(hPipe);
	CoUninitialize();
}

WebDriverResponse BrowserManager::DispatchCommand(WebDriverCommand* command)
{
	WebDriverResponse response;
	std::string value = command->m_commandParameters["value"];
	switch (command->m_commandValue)
	{
		case CommandValue::NewSession:
			std::transform(value.begin(), value.end(), value.begin(), ::toupper);
			response.m_statusCode = 0;
			response.m_value["upperValue"] = value;
			break;

		case CommandValue::GetCurrentWindowHandle:
			response.m_value = this->GetCurrentWindowHandle();
			break;

		case CommandValue::GetWindowHandles:
			response.m_value = this->GetAllWindowHandles();
			break;

		case CommandValue::SwitchToWindow:
			this->SwitchToWindow(command->m_locatorParameters, &response);
			break;

		case CommandValue::Quit:
			break;

		case CommandValue::GetSpeed:
			break;

		case CommandValue::SetSpeed:
			break;

		case CommandValue::ImplicitlyWait:
			break;

		case CommandValue::Get:
			std::transform(value.begin(), value.end(), value.begin(), ::toupper);
			response.m_statusCode = 0;
			response.m_value = "Received value " + value;
			break;

		default:
			this->m_trackedBrowsers[this->m_currentBrowser].runCommand(*command, &response);
			break;
	}

	return response;
}

Json::Value BrowserManager::GetCurrentWindowHandle()
{
	std::string currentHandle(CW2A(this->m_currentBrowser.c_str()));
	Json::Value value(currentHandle);
	return value;
}

Json::Value BrowserManager::GetAllWindowHandles()
{
	Json::Value handles;
	std::map<std::wstring, BrowserWrapper>::iterator end = this->m_trackedBrowsers.end();
	for (std::map<std::wstring, BrowserWrapper>::iterator it = this->m_trackedBrowsers.begin(); it != end; ++it)
	{
		std::string handle(CW2A(it->first.c_str()));
		handles.append(handle);
	}

	return handles;
}

void BrowserManager::SwitchToWindow(std::map<std::string, std::string> locator, WebDriverResponse *response)
{
	if (locator.find("name") == locator.end())
	{
		response->m_statusCode = 400;
		response->m_value = "name";
	}
	else
	{
		std::wstring foundBrowserHandle = L"";
		std::string desiredName = locator["name"];
		std::map<std::wstring, BrowserWrapper>::iterator end = this->m_trackedBrowsers.end();
		for (std::map<std::wstring, BrowserWrapper>::iterator it = this->m_trackedBrowsers.begin(); it != end; ++it)
		{
			std::string browserName = it->second.getWindowName();
			if (browserName == desiredName)
			{
				foundBrowserHandle = it->first;
				break;
			}

			std::string browserHandle = CW2A(it->first.c_str());
			if (browserHandle == browserName)
			{
				foundBrowserHandle = it->first;
				break;
			}
		}

		if (foundBrowserHandle == L"")
		{
			response->m_statusCode = ENOSUCHWINDOW;
		}
		else
		{
			WebDriverCommand waitCommand;
			waitCommand.m_commandValue = CommandValue::NoCommand;
			this->m_trackedBrowsers[this->m_currentBrowser].runCommand(waitCommand, NULL);
			this->m_currentBrowser = foundBrowserHandle;
			this->m_trackedBrowsers[this->m_currentBrowser].runCommand(waitCommand, NULL);
		}
	}
}

void BrowserManager::NewBrowserEventHandler(BrowserWrapper wrapper)
{
	if (this->m_trackedBrowsers.find(wrapper.m_browserId) == this->m_trackedBrowsers.end())
	{
		this->m_trackedBrowsers[wrapper.m_browserId] = wrapper;
		this->m_newBrowserEventId = wrapper.NewWindow.attach(this, &BrowserManager::NewBrowserEventHandler);
		this->m_browserQuittingEventId = wrapper.Quitting.attach(this, &BrowserManager::BrowserQuittingEventHandler);
		if (this->m_currentBrowser == L"")
		{
			this->m_currentBrowser = wrapper.m_browserId;
		}
	}
}

void BrowserManager::BrowserQuittingEventHandler(std::wstring browserId)
{
	if (this->m_trackedBrowsers.find(browserId) != this->m_trackedBrowsers.end())
	{
		this->m_trackedBrowsers[browserId].NewWindow.detach(this->m_newBrowserEventId);
		this->m_trackedBrowsers[browserId].Quitting.detach(this->m_browserQuittingEventId);
		this->m_trackedBrowsers.erase(browserId);
	}
}

std::wstring BrowserManager::StartManager()
{
	DWORD dwThreadId;
	HANDLE hThread = ::CreateThread(NULL, 0, &BrowserManager::ThreadProc, (LPVOID)this, 0, &dwThreadId);
	::CloseHandle(hThread);
	return this->m_managerId;
}
