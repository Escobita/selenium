#pragma once
#include <string>
#include <vector>
#include <sstream>
#include <exdispid.h>
#include <exdisp.h>
#include <shlguid.h>
#include <mshtml.h>
#include <iepmapi.h>
#include <sddl.h>
#include <oleacc.h>

using namespace std;

struct ProcessWindowInfo
{
	DWORD dwProcessId;
	HWND hwndBrowser;
};

class BrowserFactory
{
public:
	BrowserFactory(void);
	virtual ~BrowserFactory(void);

	DWORD LaunchBrowserProcess(int port);
	CComPtr<IWebBrowser2> CreateBrowser();
	CComPtr<IWebBrowser2> AttachToBrowser(int processId);

	static BOOL CALLBACK FindTopLevelWindows(HWND hwnd, LPARAM param);
	static BOOL CALLBACK FindChildWindowForProcess(HWND hwnd, LPARAM arg);

private:
	int m_ieMajorVersion;
	int m_windowsMajorVersion;
	std::wstring m_ieExecutableLocation;

	void SetThreadIntegrityLevel(void);
	void ResetThreadIntegrityLevel(void);

	void GetExecutableLocation(void);
	void GetIEVersion(void);
	void GetOSVersion(void);
};
