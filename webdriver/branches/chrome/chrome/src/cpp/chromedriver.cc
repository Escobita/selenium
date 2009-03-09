#include "chromedriver.h"

#include "base/base_switches.h"
#include "base/command_line.h"
#include "base/path_service.h"
#include "base/process_util.h"

#include "chrome/common/chrome_constants.h"
#include "chrome/common/chrome_paths.h"
#include "chrome/common/chrome_switches.h"
#include "chrome/common/chrome_process_filter.h"

#include "chrome/common/json_value_serializer.h"
#include "chrome/test/automation/automation_constants.h"
#include "chrome/test/automation/automation_messages.h"

std::wstring StringToWString(const std::string& s) {
  std::wstring temp(s.length(), L' ');
  std::copy(s.begin(), s.end(), temp.begin());
  return temp; 
}

ChromeDriver::ChromeDriver() {
  browser_proxy_ = NULL;
  is_visible_ = false;
  proxy_ = new AutomationProxy(timeout_);
  tab_proxy_ = NULL;
  window_proxy_ = NULL;
}

ChromeDriver::~ChromeDriver() {
  close();
  delete browser_proxy_;
  delete window_proxy_;
  delete proxy_;
}

int ChromeDriver::Launch() {
  CommandLine::Init(1, NULL);

  // TODO(noel): use --user-data-dir="profile" when webdriver send anonymous
  // "profile" used for each browser instance (it's a directory).  Have WD
  // send the path to chrome.exe.

  static const std::wstring chrome_executable(L"..\\chrome\\chrome.exe");

  CommandLine command(chrome_executable);
  command.AppendSwitch(switches::kDomAutomationController);
  command.AppendSwitchWithValue(
      switches::kTestingChannelID, proxy_->channel_id());
  base::LaunchApp(command, false, false, NULL);

  if (!proxy_->WaitForInitialLoads())
    return !SUCCESS;
  if (!proxy_->WaitForAppLaunch())
    return !SUCCESS;
  if (!proxy_->WaitForWindowCountToBecome(1, timeout_))
    return !SUCCESS;

  // Get the window before returning, is that the tab window or the
  // browser window?
  window_proxy_ = proxy_->GetActiveWindow();
  proxy_->SetFilteredInet(true);
  return SUCCESS;
}

int ChromeDriver::setVisible(int visible) {
  if (window_proxy_)
    return window_proxy_->SetVisible(is_visible_ = !!visible);
  return !SUCCESS;
}

bool ChromeDriver::getVisible() {
  return is_visible_;
}

int ChromeDriver::close() {
  if (tab_proxy_) {
    tab_proxy_->Close();
    delete tab_proxy_;
    tab_proxy_ = NULL;
    return SUCCESS;
  } else {
    return !SUCCESS;
  }
}

int ChromeDriver::get(const std::wstring url) {
  browser_proxy_ = proxy_->GetBrowserWindow(0);
  tab_proxy_ = browser_proxy_->GetActiveTab();
  tab_proxy_->NavigateToURL(GURL(url.c_str()));
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
  return L"ugly";
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

int ChromeDriver::addCookie(const char* cookieString) {
  if (tab_proxy_) {
    GURL rgurl;
    std::string cookies(cookieString);
    tab_proxy_->GetCurrentURL(&rgurl);
    tab_proxy_->SetCookie(rgurl, cookies);
    return SUCCESS;
  }
  return !SUCCESS;
}

std::wstring ChromeDriver::domGetString(std::wstring operation) {
  if (tab_proxy_) {
    std::wstring jscript;
    SStringPrintf(&jscript, L"window.domAutomationController.send(%ls);", operation.c_str());
    std::wstring rvalue;
    if (tab_proxy_->ExecuteAndExtractString(L"", jscript, &rvalue)) {
      return rvalue;
    }
  }
  return L"";
}

int ChromeDriver::domGetInteger(std::wstring operation, int* value) {
  if (tab_proxy_) {
    std::wstring jscript;
    SStringPrintf(&jscript, L"window.domAutomationController.send(%ls);", operation.c_str());
    if (tab_proxy_->ExecuteAndExtractInt(L"", jscript, value)) {
      return SUCCESS;
    }
  }
  return !SUCCESS;
}

int ChromeDriver::domGetBoolean(std::wstring operation, bool* value) {
  if (tab_proxy_) {
    std::wstring jscript;
    SStringPrintf(&jscript, L"window.domAutomationController.send(%ls);", operation.c_str());
    bool rvalue;
    if (tab_proxy_->ExecuteAndExtractBool(L"", jscript, &rvalue)) {
      *value = rvalue;
      return SUCCESS;
    }
  }
  return !SUCCESS;
}

int ChromeDriver::domSetter(std::wstring operation, const std::wstring value) {
  if (tab_proxy_) {
    std::wstring jscript;
    SStringPrintf(&jscript,
        L"window.domAutomationController.send((%ls = '%ls') && true);", operation.c_str(), value.c_str());
    bool rvalue;
    if (tab_proxy_->ExecuteAndExtractBool(L"", jscript, &rvalue)) {
      return SUCCESS;
    }
  }
  return !SUCCESS;
}

int ChromeDriver::domExecute(std::wstring operation) {
  if (tab_proxy_) {
    std::wstring jscript;
    SStringPrintf(&jscript,
        L"window.domAutomationController.send((new function(d){this.value=true; {%ls}}().value) && true);", operation.c_str());
    bool rvalue;
    if (tab_proxy_->ExecuteAndExtractBool(L"", jscript, &rvalue)) {
      return SUCCESS;
    }
  }
  return !SUCCESS;
}

//bool ChromeDriver::ExecuteAndExtractString(const std::wstring& frame_xpath,
//    const std::wstring& jscript, std::wstring* string_value) {
//
//  Value* root = NULL;
//  bool succeeded = ExecuteAndExtractValue(frame_xpath, jscript, &root);
//  if (!succeeded) return false;
//
//  std::wstring read_value;
//  Value* value = NULL;
//  succeeded = static_cast<ListValue*>(root)->Get(0, &value);
//  if (succeeded) {
//    succeeded = value->GetAsString(&read_value);
//    if (succeeded) {
//      string_value->swap(read_value);
//    }
//  }
//
//  delete root;
//  return succeeded;
//}
//
//bool ChromeDriver::ExecuteAndExtractBool(const std::wstring& frame_xpath,
//                                     const std::wstring& jscript,
//                                     bool* bool_value) {
//  Value* root = NULL;
//  bool succeeded = ExecuteAndExtractValue(frame_xpath, jscript, &root);
//  if (!succeeded)
//    return false;
//
//  bool read_value = false;
//  Value* value = NULL;
//  succeeded = static_cast<ListValue*>(root)->Get(0, &value);
//  if (succeeded) {
//    succeeded = value->GetAsBoolean(&read_value);
//    if (succeeded) {
//      *bool_value = read_value;
//    }
//  }
//
//  delete value;
//  return succeeded;
//}
//
//bool ChromeDriver::ExecuteAndExtractInt(const std::wstring& frame_xpath,
//                                    const std::wstring& jscript,
//                                    int* int_value) {
//  Value* root = NULL;
//  bool succeeded = ExecuteAndExtractValue(frame_xpath, jscript, &root);
//  if (!succeeded)
//    return false;
//
//  int read_value = 0;
//  Value* value = NULL;
//  succeeded = static_cast<ListValue*>(root)->Get(0, &value);
//  if (succeeded) {
//    succeeded = value->GetAsInteger(&read_value);
//    if (succeeded) {
//      *int_value = read_value;
//    }
//  }
//
//  delete value;
//  return succeeded;
//}
//
//bool ChromeDriver::ExecuteAndExtractValue(const std::wstring& frame_xpath,
//    const std::wstring& jscript, Value** value) {
//  if (!tab_proxy_->is_valid()) return false;
//  if (!value) return false;
//
//  std::string json;
//  if (!proxy_->Send(
//        new AutomationMsg_DomOperation(
//            0, tab_proxy_->handle(), frame_xpath, jscript, &json))) {
//    return false;
//  }
//
//  json.insert(0, "[");
//  json.append("]");
//
//  JSONStringValueSerializer deserializer(json);
//  *value = deserializer.Deserialize(NULL);
//  return *value != NULL;
//}
