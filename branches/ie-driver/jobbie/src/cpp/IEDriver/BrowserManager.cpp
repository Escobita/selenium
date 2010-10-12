#include "StdAfx.h"
#include "BrowserManager.h"
#include "CloseWindowCommandHandler.h"
#include "GetAllWindowHandlesCommandHandler.h"
#include "GetCurrentWindowHandleCommandHandler.h"
#include "GetSessionCapabilitiesCommandHandler.h"
#include "GoToUrlCommandHandler.h"
#include "NewSessionCommandHandler.h"
#include "SwitchToWindowCommandHandler.h"
#include "QuitCommandHandler.h"

BrowserManager::BrowserManager(int port)
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
	wchar_t* pwStr = reinterpret_cast<wchar_t*>(pszUuid);
	m_managerId = pwStr;

	::RpcStringFree(&pszUuid);

	this->PopulateCommandHandlerRepository();
	this->m_currentBrowser = L"";
	this->m_port = port;
	this->m_factory = new BrowserFactory;
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
	if (this->m_commandHandlerRepository.find(command->m_commandValue) == this->m_commandHandlerRepository.end())
	{
		response.m_statusCode = 501;
	}
	else
	{
		this->m_commandHandlerRepository[command->m_commandValue]->Execute(this, command->m_locatorParameters, command->m_commandParameters, &response);
	}

	return response;
}

void BrowserManager::AddWrapper(BrowserWrapper wrapper)
{
	this->m_trackedBrowsers[wrapper.m_browserId] = wrapper;
	this->m_newBrowserEventId = wrapper.NewWindow.attach(this, &BrowserManager::NewBrowserEventHandler);
	this->m_browserQuittingEventId = wrapper.Quitting.attach(this, &BrowserManager::BrowserQuittingEventHandler);
	if (this->m_currentBrowser == L"")
	{
		this->m_currentBrowser = wrapper.m_browserId;
	}
}

void BrowserManager::NewBrowserEventHandler(BrowserWrapper wrapper)
{
	if (this->m_trackedBrowsers.find(wrapper.m_browserId) == this->m_trackedBrowsers.end())
	{
		this->AddWrapper(wrapper);
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

void BrowserManager::PopulateCommandHandlerRepository()
{
	this->m_commandHandlerRepository[CommandValue::NoCommand] = new WebDriverCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetCurrentWindowHandle] = new GetCurrentWindowHandleCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetWindowHandles] = new GetAllWindowHandlesCommandHandler;
	this->m_commandHandlerRepository[CommandValue::SwitchToWindow] = new SwitchToWindowCommandHandler;
	this->m_commandHandlerRepository[CommandValue::Get] = new GoToUrlCommandHandler;
	this->m_commandHandlerRepository[CommandValue::NewSession] = new NewSessionCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetSessionCapabilities] = new GetSessionCapabilitiesCommandHandler;
	this->m_commandHandlerRepository[CommandValue::Close] = new CloseWindowCommandHandler;
	this->m_commandHandlerRepository[CommandValue::Quit] = new QuitCommandHandler;
}