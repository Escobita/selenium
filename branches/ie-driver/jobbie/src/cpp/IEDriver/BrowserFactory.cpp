#include "StdAfx.h"
#include "BrowserFactory.h"

BrowserFactory::BrowserFactory(void)
{
	this->GetExecutableLocation();
	this->GetIEVersion();
	this->GetOSVersion();
}

BrowserFactory::~BrowserFactory(void)
{
}

DWORD BrowserFactory::LaunchBrowserProcess(int port)
{
	DWORD processId = NULL;
	STARTUPINFO startInfo;
	PROCESS_INFORMATION procInfo;

	wstringstream urlStream;
	urlStream << L"http://localhost:" << port << L"/";
	std::wstring initialUrl(urlStream.str());

	// If we are running IE 6 or earlier, or are on XP or earlier...
	if (this->m_ieMajorVersion < 7 || this->m_windowsMajorVersion < 6)
	{
		::CreateProcess(this->m_ieExecutableLocation.c_str(), &initialUrl[0], NULL, NULL, FALSE, 0, NULL, NULL, &startInfo, &procInfo);
	}
	else
	{
		::IELaunchURL(initialUrl.c_str(), &procInfo, NULL);
	}

	processId = procInfo.dwProcessId;
	::CloseHandle(procInfo.hThread);
	::CloseHandle(procInfo.hProcess);

	return processId;
}

void BrowserFactory::AttachToBrowser(ProcessWindowInfo *procWinInfo)
{
	while (procWinInfo->hwndBrowser == NULL)
	{
		// TODO: create a timeout for this. We shouldn't need it, since
		// we got a valid process ID, but we should bulletproof it.
		::EnumWindows(&BrowserFactory::FindBrowserWindow, (LPARAM)procWinInfo);
		if (procWinInfo->hwndBrowser == NULL)
		{
			::Sleep(250);
		}
	}

	if (procWinInfo->hwndBrowser != NULL)
	{
		// Explicitly load MSAA so we know if it's installed
		HINSTANCE hInst = ::LoadLibrary(_T("OLEACC.DLL"));
		if (hInst)
		{
			CComPtr<IHTMLDocument2> spDoc;
			LRESULT lRes;
			UINT nMsg = ::RegisterWindowMessage(_T("WM_HTML_GETOBJECT"));
			::SendMessageTimeout(procWinInfo->hwndBrowser, nMsg, 0L, 0L, SMTO_ABORTIFHUNG, 1000, (PDWORD_PTR)&lRes);

			LPFNOBJECTFROMLRESULT pfObjectFromLresult =  reinterpret_cast<LPFNOBJECTFROMLRESULT>(::GetProcAddress(hInst, "ObjectFromLresult"));
			if (pfObjectFromLresult != NULL)
			{
				HRESULT hr;
				hr = (*pfObjectFromLresult)(lRes, IID_IHTMLDocument2, 0, reinterpret_cast<void **>(&spDoc));
				if (SUCCEEDED(hr))
				{
				   CComPtr<IHTMLWindow2> window;
				   hr = spDoc->get_parentWindow(&window);
				   if (SUCCEEDED(hr))
				   {
						// http://support.microsoft.com/kb/257717
						CComQIPtr<IServiceProvider> provider(window);
						if (provider)
						{
							CComPtr<IServiceProvider> childProvider;
							hr = provider->QueryService(SID_STopLevelBrowser, IID_IServiceProvider, reinterpret_cast<void **>(&childProvider));
							if (SUCCEEDED(hr))
							{
								IWebBrowser2* browser;
								hr = childProvider->QueryService(SID_SWebBrowserApp, IID_IWebBrowser2, reinterpret_cast<void **>(&browser));
								if (SUCCEEDED(hr))
								{
									procWinInfo->pBrowser = browser;
								}
							}
						}
				   }
				}
			}
			::FreeLibrary(hInst);
		}
	} // else Active Accessibility is not installed
}

IWebBrowser2* BrowserFactory::CreateBrowser()
{
	// TODO: Error and exception handling and return value checking.
	IWebBrowser2 *pBrowser;
	this->SetThreadIntegrityLevel();
	DWORD context = CLSCTX_LOCAL_SERVER;
	if (this->m_ieMajorVersion == 7 && this->m_windowsMajorVersion == 6)
	{
		// ONLY for IE 7 on Windows Vista. XP and below do not have Protected Mode;
		// Windows 7 shipped with IE8.
		context = context | CLSCTX_ENABLE_CLOAKING;
	}

	//pBrowser.CoCreateInstance(CLSID_InternetExplorer, NULL, context);
	::CoCreateInstance(CLSID_InternetExplorer, NULL, context, IID_IWebBrowser2, (void**)&pBrowser);
	pBrowser->put_Visible(VARIANT_TRUE);
	this->ResetThreadIntegrityLevel();
	return pBrowser;
}


void BrowserFactory::SetThreadIntegrityLevel()
{
	// TODO: Error handling and return value checking.
	HANDLE hProcToken = NULL;
	HANDLE hProc = ::GetCurrentProcess();
	BOOL result = ::OpenProcessToken(hProc, TOKEN_DUPLICATE, &hProcToken);

	HANDLE hThreadToken = NULL;
	result = ::DuplicateTokenEx(
		hProcToken, 
		TOKEN_QUERY | TOKEN_IMPERSONATE | TOKEN_ADJUST_DEFAULT,
		NULL, 
		SecurityImpersonation,
		TokenImpersonation,
		&hThreadToken);

	PSID pSid = NULL;
	result = ::ConvertStringSidToSid(SDDL_ML_LOW, &pSid);

	TOKEN_MANDATORY_LABEL tml;
	tml.Label.Attributes = SE_GROUP_INTEGRITY | SE_GROUP_INTEGRITY_ENABLED;
	tml.Label.Sid = pSid;

	result = ::SetTokenInformation(hThreadToken, TokenIntegrityLevel, &tml, sizeof(tml) + ::GetLengthSid(pSid));
	::LocalFree(pSid);

	HANDLE hThread = ::GetCurrentThread();
	result = ::SetThreadToken(&hThread, hThreadToken);
	result = ::ImpersonateLoggedOnUser(hThreadToken);

	result = ::CloseHandle(hThreadToken);
	result = ::CloseHandle(hProcToken);
}

void BrowserFactory::ResetThreadIntegrityLevel()
{
	::RevertToSelf();
}

HWND BrowserFactory::GetTabWindowHandle(IWebBrowser2 *pBrowser)
{
	ProcessWindowInfo procWinInfo;
	procWinInfo.pBrowser = pBrowser;
	procWinInfo.hwndBrowser = NULL;

	HWND hwnd = NULL;
	CComQIPtr<IServiceProvider> pServiceProvider;
	HRESULT hr = pBrowser->QueryInterface(IID_IServiceProvider, reinterpret_cast<void **>(&pServiceProvider));
	if (SUCCEEDED(hr))
	{
		CComPtr<IOleWindow> pWindow;
		hr = pServiceProvider->QueryService(SID_SShellBrowser, IID_IOleWindow, reinterpret_cast<void **>(&pWindow));
		if (SUCCEEDED(hr))
		{
			// This gets the TabWindowClass window in IE 7 and 8,
			// and the top-level window frame in IE 6. The window
			// we need is the InternetExplorer_Server window.
			pWindow->GetWindow(&hwnd);

			DWORD dwProcessId;
			::GetWindowThreadProcessId(hwnd, &dwProcessId);
			procWinInfo.dwProcessId = dwProcessId;

			::EnumChildWindows(hwnd, &BrowserFactory::FindChildWindowForProcess, (LPARAM)&procWinInfo);
			hwnd = procWinInfo.hwndBrowser;
		}
	}

	return hwnd;
}

BOOL CALLBACK BrowserFactory::FindBrowserWindow(HWND hwnd, LPARAM arg)
{
	// Could this be an IE instance?
	// 8 == "IeFrame\0"
	// 21 == "Shell DocObject View\0";
	char name[21];
	if (GetClassNameA(hwnd, name, 21) == 0)
	{
		// No match found. Skip
		return TRUE;
	}
	
	if (strcmp("IEFrame", name) != 0 && strcmp("Shell DocObject View", name) != 0)
	{
		return TRUE;
	}

	return EnumChildWindows(hwnd, FindChildWindowForProcess, arg);
}

BOOL CALLBACK BrowserFactory::FindChildWindowForProcess(HWND hwnd, LPARAM arg)
{
	ProcessWindowInfo *procWinInfo = (ProcessWindowInfo *)arg;

	// Could this be an Internet Explorer Server window?
	// 25 == "Internet Explorer_Server\0"
	char name[25];
	if (GetClassNameA(hwnd, name, 25) == 0)
	{
		// No match found. Skip
		return TRUE;
	}
	
	if (strcmp("Internet Explorer_Server", name) != 0)
	{
		return TRUE;
	}
	else
	{
		DWORD dwProcessId = NULL;
		::GetWindowThreadProcessId(hwnd, &dwProcessId);
		if (procWinInfo->dwProcessId == dwProcessId)
		{
			// Once we've found the first Internet Explorer_Server window
			// for the process we want, we can stop.
			procWinInfo->hwndBrowser = hwnd;
			return FALSE;
		}
	}

	return TRUE;
}

void BrowserFactory::GetExecutableLocation()
{
	std::wstring classIdKey = L"SOFTWARE\\Classes\\InternetExplorer.Application\\CLSID";

	// TODO: error checking.
	DWORD cbRequired;
	HKEY hkeyClassId;
	::RegOpenKeyEx(HKEY_LOCAL_MACHINE, classIdKey.c_str(), 0, KEY_QUERY_VALUE, &hkeyClassId);
	::RegQueryValueEx(hkeyClassId, NULL, NULL, NULL, NULL, &cbRequired);
	std::vector<TCHAR> classIdBuffer(cbRequired);
	::RegQueryValueEx(hkeyClassId, NULL, NULL, NULL, (LPBYTE)&classIdBuffer[0], &cbRequired);
	::RegCloseKey(hkeyClassId);
	std::wstring classId = &classIdBuffer[0];

	std::wstring locationKey = L"SOFTWARE\\Classes\\CLSID\\" + classId + L"\\LocalServer32";

	HKEY hkeyLocation;
	::RegOpenKeyEx(HKEY_LOCAL_MACHINE, locationKey.c_str(), 0, KEY_QUERY_VALUE, &hkeyLocation);
	::RegQueryValueEx(hkeyLocation, NULL, NULL, NULL, NULL, &cbRequired);
	std::vector<TCHAR> locationBuffer(cbRequired);
	::RegQueryValueEx(hkeyLocation, NULL, NULL, NULL, (LPBYTE)&locationBuffer[0], &cbRequired);
	::RegCloseKey(hkeyLocation);
	this->m_ieExecutableLocation = &locationBuffer[0];
	if (this->m_ieExecutableLocation.substr(0, 1) == L"\"")
	{
		this->m_ieExecutableLocation.erase(0, 1);
		this->m_ieExecutableLocation.erase(this->m_ieExecutableLocation.length() - 1, 1);
	}
}

void BrowserFactory::GetIEVersion()
{
	struct LANGANDCODEPAGE {
		WORD language;
		WORD code_page;
		} *lpTranslate;

	DWORD dummy;
	DWORD length = ::GetFileVersionInfoSize(this->m_ieExecutableLocation.c_str(), &dummy);
	std::vector<BYTE> versionBuffer(length);
	::GetFileVersionInfo(this->m_ieExecutableLocation.c_str(), dummy, length, &versionBuffer[0]);

	UINT page_count;
	BOOL query_result = ::VerQueryValue(&versionBuffer[0], L"\\VarFileInfo\\Translation", (LPVOID*) &lpTranslate, &page_count);
    
	wchar_t sub_block[MAX_PATH];
    _snwprintf_s(sub_block, MAX_PATH, MAX_PATH,
                 L"\\StringFileInfo\\%04x%04x\\FileVersion", lpTranslate->language, lpTranslate->code_page);
    LPVOID value = NULL;
    UINT size;
    query_result = ::VerQueryValue(&versionBuffer[0], sub_block, &value, &size);
	std::wstring ieVersion;
	ieVersion.assign(static_cast<wchar_t*>(value));
	std::wstringstream versionStream(ieVersion);
	versionStream >> this->m_ieMajorVersion;
}

void BrowserFactory::GetOSVersion()
{
	OSVERSIONINFO osVersion;
	osVersion.dwOSVersionInfoSize = sizeof(OSVERSIONINFO);
	::GetVersionEx(&osVersion);
	this->m_windowsMajorVersion = osVersion.dwMajorVersion;
}