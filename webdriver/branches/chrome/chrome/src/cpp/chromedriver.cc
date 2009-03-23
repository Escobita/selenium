#include "chromedriver.h"
#include "chromescript.h"
#include "chromeelement.h"

#include <wchar.h>

#include "base/base_switches.h"
#include "base/command_line.h"
#include "base/path_service.h"
#include "base/process_util.h"

#if defined(OS_WIN)
#include "chrome/common/chrome_constants.h"
#include "chrome/common/chrome_paths.h"
#include "chrome/common/chrome_switches.h"
#include "chrome/common/chrome_process_filter.h"
#include "chrome/common/json_value_serializer.h"
#endif

#include "chrome/test/automation/automation_constants.h"
#include "chrome/test/automation/automation_messages.h"
#include "chrome/test/automation/automation_proxy.h"
#include "chrome/test/automation/browser_proxy.h"
#include "chrome/test/automation/window_proxy.h"
#include "chrome/test/automation/tab_proxy.h"

void dg(const std::wstring msg) {
  std::wcout << L"INFO: " << msg << std::endl;
}

std::wstring StringToWString(const std::string& s) {
  std::wstring temp(s.length(),L' ');
  std::copy(s.begin(), s.end(), temp.begin());
  return temp; 
}

std::string WStringToString(const std::wstring& ws) {
  int length = ws.length();
  char* sbuf = new char[(length * 2)+2];
  memset(sbuf, 0x00, length+2);
  WideCharToMultiByte(CP_ACP, 0, ws.c_str(), length, sbuf, (length*2)+2, 0, 0);
  return sbuf;
}

ChromeDriver::ChromeDriver() {
  proxy_ = new AutomationProxy(timeout_);
  current_frame_ = L"";
  is_visible_ = true;
  e_counter_ = 0;
}

ChromeDriver::~ChromeDriver() {
}

int ChromeDriver::Launch() {
  BrowserProcessFilter filter(L"");
  int processCount = base::GetProcessCount(
      chrome::kBrowserProcessExecutableName, &filter);
  if (processCount == 0) {
    CommandLine command_(getApplicationPath());
    command_.AppendSwitch(switches::kDomAutomationController);
    command_.AppendSwitchWithValue(switches::kTestingChannelID, proxy_->channel_id());
    base::LaunchApp(command_, false, false, NULL);

    if (!proxy_->WaitForInitialLoads()) {
      return !SUCCESS;
    }

    if (!proxy_->WaitForAppLaunch()) {
      return !SUCCESS;
    }

    if (!proxy_->WaitForWindowCountToBecome(1, 6000)) {
      return !SUCCESS;
    }
    proxy_->SetFilteredInet(true);
  }

  setWindowProxy(proxy_->GetActiveWindow());
  return SUCCESS;
}

int ChromeDriver::setVisible(bool visible) {
  if (window_proxy_) {
    is_visible_ = visible;
    return window_proxy_->SetVisible(visible);
  }
  return !SUCCESS;
}

int ChromeDriver::close() {
  if (tab_proxy_) {
    tab_proxy_->Close();
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::get(const std::wstring url) {
  browser_proxy_ = proxy_->GetBrowserWindow(0);
  tab_proxy_ = browser_proxy_->GetActiveTab();
  GURL gurl(url.c_str());
  tab_proxy_->NavigateToURL(gurl);
  return SUCCESS;
}

int ChromeDriver::back() {
  if (tab_proxy_) {
    tab_proxy_->GoBack();
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::forward() {
  if (tab_proxy_) {
    tab_proxy_->GoForward();
    return SUCCESS;
  }
  return !SUCCESS;
}

std::wstring ChromeDriver::getCurrentUrl() {
  if (tab_proxy_) {
    GURL rgurl;
    tab_proxy_->GetCurrentURL(&rgurl);
    return StringToWString(rgurl.spec());
  }
  return L"";
}

std::wstring ChromeDriver::getTitle() {
  if (tab_proxy_) {
    std::wstring title;
    tab_proxy_->GetTabTitle(&title);
    return title;
  }
  return L"";
}

std::wstring ChromeDriver::getPageSource() {
  return L"";
}


int ChromeDriver::switchToActiveElement(ChromeElement** element) {
  return !SUCCESS;
}

int ChromeDriver::switchToFrame(int index) {
  return !SUCCESS;
}

int ChromeDriver::switchToFrame(const std::wstring name) {
  return !SUCCESS;
}

int ChromeDriver::switchToWindow(const std::wstring name) {
  return !SUCCESS;
}

std::wstring ChromeDriver::getCookies() {
  if (tab_proxy_) {
    GURL rgurl;
    std::string cookies;
    tab_proxy_->GetCurrentURL(&rgurl);
    tab_proxy_->GetCookies(rgurl, &cookies);
    return StringToWString(cookies);
  }
  return L"";
}

int ChromeDriver::addCookie(const std::wstring cookieString) {
  if (tab_proxy_) {
    GURL rgurl;
    std::string cookies = WStringToString(cookieString);
    tab_proxy_->GetCurrentURL(&rgurl);
    tab_proxy_->SetCookie(rgurl, cookies);
    return SUCCESS;
  }
  return !SUCCESS;
}

// ----------------------------------------------------------------------------
// Element Related.
// ----------------------------------------------------------------------------
int ChromeDriver::findElementById(const std::wstring id, ChromeElement** element) {
  std::wstring jscript;
  SStringPrintf(&jscript, FINDER_BY_ID.c_str(), id.c_str(), currentCount());

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  if (found.size() != 2) {
    return !SUCCESS;
  } else {
    std::wstring base;
    SStringPrintf(&base, GET_ELEMENT_BY_ID.c_str(), found.at(0).c_str());
    *element = new ChromeElement(this, ChromeElement::BY_ID, id);
    (*element)->setElementIdentifier(base, id, found.at(1));
  }
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsById(const std::wstring id) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

int ChromeDriver::findElementByTagName(const std::wstring tag, ChromeElement** element) {
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByTagName(const std::wstring tag) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

int ChromeDriver::findElementByClassName(const std::wstring cls, ChromeElement** element) {
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByClassName(const std::wstring cls) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

int ChromeDriver::findElementByLinkText(const std::wstring text, ChromeElement** element) {
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByLinkText(const std::wstring text) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

int ChromeDriver::findElementByPartialLinkText(const std::wstring pattern, ChromeElement** element) {
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByPartialLinkText(const std::wstring pattern) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

int ChromeDriver::findElementByName(const std::wstring name, ChromeElement** element) {
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByName(const std::wstring name) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

int ChromeDriver::findElementByXPath(const std::wstring xpath, ChromeElement** element) {
  return SUCCESS;
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByXPath(const std::wstring xpath) {
  std::vector<ChromeElement*>* toReturn = new std::vector<ChromeElement*>();
  return toReturn;
}

// ----------------------------------------------------------------------------
// Internal members implementation.
// ----------------------------------------------------------------------------
std::wstring ChromeDriver::getApplicationPath() {
  return L"..\\chrome\\chrome.exe";
}

std::wstring getRunnableScript(int type, const std::wstring jscript) {
  std::wstring domOper(L"window.domAutomationController.send(");
  domOper.append(type == FUNCTION_TYPE_RETURN ? ANON_RETURN : ANON_NORETURN);
  domOper.append(L");");

  std::wstring runScript;
  SStringPrintf(&runScript, domOper.c_str(), jscript.c_str());
  return runScript;
}

std::wstring ChromeDriver::domGetString(const std::wstring jscript) {
  if (!tab_proxy_) return L"";
  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_RETURN, jscript);
  std::wstring retValue;
  if (tab_proxy_->ExecuteAndExtractString(
        current_frame_.c_str(), runScript, &retValue)) {
    return retValue;
  }
  return L"";
}

int ChromeDriver::domGetInteger(const std::wstring jscript, int* value) {
  if (!tab_proxy_) return !SUCCESS;
  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_RETURN, jscript);
  if (tab_proxy_->ExecuteAndExtractInt(
        current_frame_.c_str(), runScript, value)) {
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::domGetBoolean(const std::wstring jscript, bool* value) {
  if (!tab_proxy_) return !SUCCESS;
  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_RETURN, jscript);
  if (tab_proxy_->ExecuteAndExtractBool(
        current_frame_.c_str(), runScript, value)) {
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::domGetStringArray(const std::wstring jscript, std::vector<std::wstring>& retValue) {
  if (!tab_proxy_) return !SUCCESS;

  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_RETURN, jscript);
  std::wstring retString;
  if (tab_proxy_->ExecuteAndExtractString(current_frame_.c_str(), runScript, &retString)) {
    std::string::size_type lastPos = retString.find_first_not_of(L",", 0);
    std::string::size_type pos     = retString.find_first_of(L",", lastPos);
    while (std::string::npos != pos || std::string::npos != lastPos) {
      retValue.push_back(retString.substr(lastPos, pos - lastPos));
      lastPos = retString.find_first_not_of(L",", pos);
      pos = retString.find_first_of(L",", lastPos);
    }
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::domGetIntegerArray(const std::wstring jscript, std::vector<int>& retValue) {
  if (!tab_proxy_) return !SUCCESS;

  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_RETURN, jscript);
  std::wstring retString;
  if (tab_proxy_->ExecuteAndExtractString(current_frame_.c_str(), runScript, &retString)) {
    std::string::size_type lastPos = retString.find_first_not_of(L",", 0);
    std::string::size_type pos     = retString.find_first_of(L",", lastPos);
    wchar_t* endChar = L"";
    while (std::string::npos != pos || std::string::npos != lastPos) {
      int retInt = (int)wcstol(retString.substr(lastPos, pos - lastPos).c_str(), &endChar, 10);
      retValue.push_back(retInt);
      lastPos = retString.find_first_not_of(L",", pos);
      pos = retString.find_first_of(L",", lastPos);
    }
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::domGetBooleanArray(const std::wstring jscript, std::vector<bool>& retValue) {
  if (!tab_proxy_) return !SUCCESS;

  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_RETURN, jscript);
  std::wstring retString;
  if (tab_proxy_->ExecuteAndExtractString(current_frame_.c_str(), runScript, &retString)) {
    std::string::size_type lastPos = retString.find_first_not_of(L",", 0);
    std::string::size_type pos     = retString.find_first_of(L",", lastPos);
    wchar_t* endChar = L"";
    while (std::string::npos != pos || std::string::npos != lastPos) {
      int retInt = (int)wcstol(retString.substr(lastPos, pos - lastPos).c_str(), &endChar, 10);
      retValue.push_back(retInt == 1);
      lastPos = retString.find_first_not_of(L",", pos);
      pos = retString.find_first_of(L",", lastPos);
    }
    return SUCCESS;
  }
  return !SUCCESS;
}

int ChromeDriver::domGetVoid(const std::wstring jscript) {
  if (!tab_proxy_) return !SUCCESS;
  std::wstring runScript = getRunnableScript(FUNCTION_TYPE_NORETURN, jscript);
  bool value;
  if (tab_proxy_->ExecuteAndExtractBool(current_frame_.c_str(), runScript, &value)
      || value) {
    return SUCCESS;
  }
  return !SUCCESS;
}
