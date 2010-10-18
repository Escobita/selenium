#include "StdAfx.h"
#include "BrowserManager.h"
#include "FindByClassNameElementFinder.h"
#include "FindByIdElementFinder.h"
#include "FindByLinkTextElementFinder.h"
#include "FindByNameElementFinder.h"
#include "FindByPartialLinkTextElementFinder.h"
#include "FindByTagNameElementFinder.h"
#include "CloseWindowCommandHandler.h"
#include "ExecuteScriptCommandHandler.h"
#include "FindElementCommandHandler.h"
#include "FindElementsCommandHandler.h"
#include "GetAllWindowHandlesCommandHandler.h"
#include "GetCurrentUrlCommandHandler.h"
#include "GetCurrentWindowHandleCommandHandler.h"
#include "GetElementTagNameCommandHandler.h"
#include "GetSessionCapabilitiesCommandHandler.h"
#include "GetPageSourceCommandHandler.h"
#include "GetTitleCommandHandler.h"
#include "GoToUrlCommandHandler.h"
#include "NewSessionCommandHandler.h"
#include "SwitchToFrameCommandHandler.h"
#include "SwitchToWindowCommandHandler.h"
#include "QuitCommandHandler.h"

LRESULT BrowserManager::OnInit(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	// If we wanted to be a little more clever, we could create a struct 
	// containing the HWND and the port number and pass them into the
	// ThreadProc via lpParameter and avoid this message handler altogether.
	this->m_port = (int)wParam;
	return 0;
}

LRESULT BrowserManager::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	// NOTE: COM should be initialized on this thread, so we
	// could use CoCreateGuid() and StringFromGUID2() instead.
	UUID idGuid;
	RPC_WSTR pszUuid = NULL;
	::UuidCreate(&idGuid);
	::UuidToString(&idGuid, &pszUuid);

	// RPC_WSTR is currently typedef'd in RpcDce.h (pulled in by rpc.h)
	// as unsigned short*. It needs to be typedef'd as wchar_t* 
	wchar_t* pwStr = reinterpret_cast<wchar_t*>(pszUuid);
	this->m_managerId = pwStr;

	::RpcStringFree(&pszUuid);
	this->SetWindowText(this->m_managerId.c_str());

	this->PopulateCommandHandlerRepository();
	this->PopulateElementFinderRepository();
	this->m_currentBrowser = L"";
	this->m_factory = new BrowserFactory;
	this->m_command = new WebDriverCommand;
	this->m_serializedResponse = L"";
	return 0;
}

LRESULT BrowserManager::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	this->DestroyWindow();
	return 0;
}

LRESULT BrowserManager::OnSetCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	LPCTSTR lpRawCommand = (LPCTSTR)lParam;
	std::wstring jsonCommand(lpRawCommand);

	// JsonCpp only understands narrow strings, so we have to convert.
	std::string convertedCommand(CW2A(jsonCommand.c_str()));
	this->m_command->populate(convertedCommand);
	return 0;
}

LRESULT BrowserManager::OnExecCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	this->DispatchCommand();
	return 0;
}

LRESULT BrowserManager::OnGetResponseLength(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	return this->m_serializedResponse.size();
}

LRESULT BrowserManager::OnGetResponse(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	LPWSTR str = (LPWSTR)lParam;
	this->m_serializedResponse.copy(str, this->m_serializedResponse.size());

	// Reset the serialized response for the next command.
	this->m_serializedResponse = L"";
	return 0;
}

DWORD WINAPI BrowserManager::ThreadProc(LPVOID lpParameter)
{
	// Optional TODO: Create a struct to pass in via lpParameter
	// instead of just a pointer to an HWND. That way, we could
	// pass the mongoose server port via a single call, rather than
	// having to send an init message after the window is created.
	HWND *paramHwnd = (HWND *)lpParameter;
	DWORD error = 0;
	HRESULT hr = ::CoInitializeEx(NULL, COINIT_APARTMENTTHREADED);
	BrowserManager manager;
	HWND managerHwnd = manager.Create(HWND_MESSAGE, CWindow::rcDefault);
	if (managerHwnd == NULL)
	{
		error = ::GetLastError();
	}

	// Return the HWND back through lpParameter, and signal that the
	// window is ready for messages.
	*paramHwnd = managerHwnd;
	HANDLE hEvent = ::OpenEvent(EVENT_ALL_ACCESS, FALSE, EVENT_NAME);
	::SetEvent(hEvent);
	::CloseHandle(hEvent);

    // Run the message loop
	MSG msg;
	while (::GetMessage(&msg, NULL, 0, 0) > 0)
	{
		::TranslateMessage(&msg);
		::DispatchMessage(&msg);
	}

	::CoUninitialize();
	return 0;
}

void BrowserManager::DispatchCommand()
{
	WebDriverResponse response;
	if (this->m_commandHandlerRepository.find(this->m_command->m_commandValue) == this->m_commandHandlerRepository.end())
	{
		response.m_statusCode = 501;
	}
	else
	{
		this->m_commandHandlerRepository[this->m_command->m_commandValue]->Execute(this, this->m_command->m_locatorParameters, this->m_command->m_commandParameters, &response);
	}

	this->m_serializedResponse = response.serialize();
}

int BrowserManager::GetCurrentBrowser(BrowserWrapper **ppWrapper)
{
	*ppWrapper = this->m_trackedBrowsers[this->m_currentBrowser];
	return SUCCESS;
}

void BrowserManager::AddWrapper(BrowserWrapper *wrapper)
{
	this->m_trackedBrowsers[wrapper->m_browserId] = wrapper;
	
	this->m_newBrowserEventId = wrapper->NewWindow.attach(this, &BrowserManager::NewBrowserEventHandler);
	this->m_browserQuittingEventId = wrapper->Quitting.attach(this, &BrowserManager::BrowserQuittingEventHandler);
	if (this->m_currentBrowser == L"")
	{
		this->m_currentBrowser = wrapper->m_browserId;
	}
}

void BrowserManager::NewBrowserEventHandler(BrowserWrapper *wrapper)
{
	if (this->m_trackedBrowsers.find(wrapper->m_browserId) == this->m_trackedBrowsers.end())
	{
		this->AddWrapper(wrapper);
	}
}

void BrowserManager::BrowserQuittingEventHandler(std::wstring browserId)
{
	if (this->m_trackedBrowsers.find(browserId) != this->m_trackedBrowsers.end())
	{
		this->m_trackedBrowsers[browserId]->NewWindow.detach(this->m_newBrowserEventId);
		this->m_trackedBrowsers[browserId]->Quitting.detach(this->m_browserQuittingEventId);
		this->m_trackedBrowsers.erase(browserId);
	}
}

void BrowserManager::PopulateElementFinderRepository(void)
{
	this->m_elementFinders[L"id"] = new FindByIdElementFinder;
	this->m_elementFinders[L"name"] = new FindByNameElementFinder;
	this->m_elementFinders[L"tag name"] = new FindByTagNameElementFinder;
	this->m_elementFinders[L"link text"] = new FindByLinkTextElementFinder;
	this->m_elementFinders[L"partial link text"] = new FindByPartialLinkTextElementFinder;
	this->m_elementFinders[L"class name"] = new FindByClassNameElementFinder;
}

void BrowserManager::PopulateCommandHandlerRepository()
{
	this->m_commandHandlerRepository[CommandValue::NoCommand] = new WebDriverCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetCurrentWindowHandle] = new GetCurrentWindowHandleCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetWindowHandles] = new GetAllWindowHandlesCommandHandler;
	this->m_commandHandlerRepository[CommandValue::SwitchToWindow] = new SwitchToWindowCommandHandler;
	this->m_commandHandlerRepository[CommandValue::SwitchToFrame] = new SwitchToFrameCommandHandler;
	this->m_commandHandlerRepository[CommandValue::Get] = new GoToUrlCommandHandler;
	this->m_commandHandlerRepository[CommandValue::NewSession] = new NewSessionCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetSessionCapabilities] = new GetSessionCapabilitiesCommandHandler;
	this->m_commandHandlerRepository[CommandValue::Close] = new CloseWindowCommandHandler;
	this->m_commandHandlerRepository[CommandValue::Quit] = new QuitCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetTitle] = new GetTitleCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetPageSource] = new GetPageSourceCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetCurrentUrl] = new GetCurrentUrlCommandHandler;
	this->m_commandHandlerRepository[CommandValue::ExecuteScript] = new ExecuteScriptCommandHandler;
	this->m_commandHandlerRepository[CommandValue::FindElement] = new FindElementCommandHandler;
	this->m_commandHandlerRepository[CommandValue::FindElements] = new FindElementsCommandHandler;
	this->m_commandHandlerRepository[CommandValue::GetElementTagName] = new GetElementTagNameCommandHandler;
}