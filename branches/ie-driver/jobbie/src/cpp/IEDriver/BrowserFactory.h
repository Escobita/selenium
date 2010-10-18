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
	IWebBrowser2 *pBrowser;
};

class BrowserFactory
{
public:
	BrowserFactory(void);
	virtual ~BrowserFactory(void);

	DWORD LaunchBrowserProcess(int port);
	IWebBrowser2* CreateBrowser();
	void AttachToBrowser(ProcessWindowInfo *procWinInfo);
	HWND GetTabWindowHandle(IWebBrowser2* pBrowser);

	static BOOL CALLBACK FindBrowserWindow(HWND hwnd, LPARAM param);
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
