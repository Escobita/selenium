#include "chromedriver.h"
#include "chromescript.h"
#include "chromeelement.h"

#include <time.h>
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
  current_frame_src_ = L"";
  is_visible_ = true;
  e_counter_ = 0;
  default_window_index_ = 0;
  default_tab_index_ = 0;
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

  setProxy(default_window_index_, default_tab_index_);
  return SUCCESS;
}


void ChromeDriver::setProxy(int index, int tabindex) {
  browser_proxy_ = proxy_->GetBrowserWindow(index);
  tab_proxy_ = browser_proxy_->GetTab(tabindex);
  window_proxy_ = browser_proxy_->GetWindow();
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
    if (current_frame_.length() == 0) {
      GURL rgurl;
      tab_proxy_->GetCurrentURL(&rgurl);
      return StringToWString(rgurl.spec());
    } else {
      return this->current_frame_src_;
    }
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
  // TODO (amitabh)
  return L"";
}


int ChromeDriver::switchToActiveElement(ChromeElement** element) {
  // TODO (amitabh)
  return !SUCCESS;
}

int ChromeDriver::switchToFrame(int index) {
  int count = 0;
  if (domGetInteger(FRAME_COUNT.c_str(), &count) == SUCCESS) {
    std::wstring jscript;
    SStringPrintf(&jscript, FRAME_IDENTIFIER_BY_INDEX.c_str(), index+1); // Note: +1 is to address xpath (1-based index).
    std::vector<std::wstring> framepaths;
    framepaths.clear();
    domGetStringArray(jscript, framepaths);
    if (framepaths.size() > 0) {
      if (this->current_frame_.length() > 0) {
        this->current_frame_ = framepaths.at(0).c_str();
      } else {
        this->current_frame_.append(L"\n");
        this->current_frame_.append(framepaths.at(0).c_str());
      }
      this->current_frame_src_ = framepaths.at(1).c_str();
      return SUCCESS;
    }
  }
  return !SUCCESS;
}

int ChromeDriver::switchToFrame(const std::wstring name) {
  if (name.length() == 0) {
    current_frame_ = L"";
  } else {
  }
  return !SUCCESS;
}

int ChromeDriver::switchToWindow(const std::wstring name) {
  // TODO (amitabh): Support more windows/tabs and then allow switching.
  // Note: Currently it is required only for pop-ups.
  if (name.length() == 0) {
    setProxy(default_window_index_, default_tab_index_);
  } else {
    int counter = 0;
    if (proxy_->GetBrowserWindowCount(&counter)) {
      for (int i = 0; i < counter; i++) {
        BrowserProxy* browser = proxy_->GetBrowserWindow(i);
        int tabindex = 0;
        TabProxy* tab = browser->GetActiveTab();
        browser->GetActiveTabIndex(&tabindex);

        std::wstring windowname;
        tab->GetTabTitle(&windowname);
        if (name == windowname) {
          setProxy(i, tabindex);
          return SUCCESS;
        }
      }
      return !SUCCESS;
    }
  }
  return SUCCESS;
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
  time_t curTime = time(NULL);
  SStringPrintf(&jscript, FINDER_BY_ID.c_str(), id.c_str(), currentCount(), curTime);

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  if (found.size() <= 3) {
    return !SUCCESS;
  } else {
    std::wstring base;
    SStringPrintf(&base, GET_ELEMENT_BY_ID.c_str(), found.at(3).c_str());
    *element = new ChromeElement(this, ChromeElement::BY_ID, id);
    (*element)->setElementIdentifier(base, found.at(3).c_str(), found.at(2).c_str());
	return SUCCESS;
  }
}

std::vector<ChromeElement*>* ChromeDriver::findElementsById(const std::wstring id) {
  std::wstring jscript;
  time_t curTime = time(NULL);
  SStringPrintf(&jscript, FINDER_BY_ID.c_str(), id.c_str(), currentCount(), curTime);

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  std::vector<ChromeElement*>* retList = new std::vector<ChromeElement*>();
  if (found.size() > 3) {
    std::wstring base;
    SStringPrintf(&base, GET_ELEMENT_BY_ID.c_str(), found.at(3).c_str());
	retList->push_back(new ChromeElement(this, ChromeElement::BY_ID, id));
	retList->at(0)->setElementIdentifier(base, found.at(3).c_str(), found.at(2).c_str());
  }
  return retList;
}

int ChromeDriver::findElementByTagName(const std::wstring tag, ChromeElement** element) {
  std::wstring jscript;
  time_t curTime = time(NULL);
  SStringPrintf(&jscript, FINDER_BY_TAGNAME.c_str(), 0, tag.c_str(), currentCount(), curTime);

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  if (found.size() <= 3) {
    return !SUCCESS;
  } else {
	std::wstring eid = found.at(3).c_str();
    std::wstring tagName = found.at(2).c_str();
    std::wstring base;
    SStringPrintf(&base, GET_ELEMENT_BY_NONID.c_str(), tagName.c_str(), eid.c_str());
    *element = new ChromeElement(this, ChromeElement::BY_TAG, tag);
    (*element)->setElementIdentifier(base, found.at(3).c_str(), found.at(2).c_str());
	return SUCCESS;
  }
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByTagName(const std::wstring tag) {
  std::wstring jscript;
  time_t curTime = time(NULL);
  SStringPrintf(&jscript, FINDER_BY_TAGNAME.c_str(), tag.c_str(), 1, currentCount(), curTime);

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  std::vector<ChromeElement*>* retList = new std::vector<ChromeElement*>();
  if (found.size() > 3) {
    wchar_t* endChar = L"";
	this->setElementCounter(wcstol(found.at(0).c_str(), &endChar, 10));
	int total = wcstol(found.at(1).c_str(), &endChar, 10);
	std::wstring tagName = found.at(2).c_str();
	for (int i=3; i<total+3; i++) {
	  std::wstring eid = found.at(i).c_str();
	  std::wstring base;
	  SStringPrintf(&base, GET_ELEMENT_BY_NONID.c_str(), tagName.c_str(), eid.c_str());
	  retList->push_back(new ChromeElement(this, ChromeElement::BY_TAG, tag));
	  retList->at(retList->size() - 1)->setElementIdentifier(base, eid, tagName);
	}
  }
  return retList;
}

int ChromeDriver::findElementByClassName(const std::wstring cls, ChromeElement** element) {
  std::wstring jscript;
  time_t curTime = time(NULL);
  SStringPrintf(&jscript, FINDER_BY_CLASSNAME.c_str(), 0, cls.c_str(), currentCount(), curTime);

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  if (found.size() <= 3) {
    return !SUCCESS;
  } else {
	std::wstring eid = found.at(3).c_str();
    std::wstring tagName = found.at(2).c_str();
    std::wstring base;
	SStringPrintf(&base, GET_ELEMENT_BY_NONID.c_str(), tagName.c_str(), eid.c_str());
    *element = new ChromeElement(this, ChromeElement::BY_CLASSNAME, cls);
    (*element)->setElementIdentifier(base, found.at(3).c_str(), found.at(2).c_str());
	return SUCCESS;
  }
}

std::vector<ChromeElement*>* ChromeDriver::findElementsByClassName(const std::wstring cls) {
  std::wstring jscript;
  time_t curTime = time(NULL);
  SStringPrintf(&jscript, FINDER_BY_CLASSNAME.c_str(), cls.c_str(), 1, currentCount(), curTime);

  std::vector<std::wstring> found;
  domGetStringArray(jscript, found);
  std::vector<ChromeElement*>* retList = new std::vector<ChromeElement*>();
  if (found.size() > 3) {
    wchar_t* endChar = L"";
	this->setElementCounter(wcstol(found.at(0).c_str(), &endChar, 10));
	int total = wcstol(found.at(1).c_str(), &endChar, 10);
	for (int i=2; i<total*2; i=i+2) {
	  std::wstring tagName = found.at(i).c_str();
	  std::wstring eid = found.at(i+1).c_str();
	  std::wstring base;
	  SStringPrintf(&base, GET_ELEMENT_BY_NONID.c_str(), tagName.c_str(), eid.c_str());
	  retList->push_back(new ChromeElement(this, ChromeElement::BY_CLASSNAME, cls));
	  retList->at(retList->size() - 1)->setElementIdentifier(base, eid, tagName);
	}
  }
  return retList;
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
