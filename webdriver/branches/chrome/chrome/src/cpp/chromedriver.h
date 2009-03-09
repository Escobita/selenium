#ifndef CHROME_DRIVER_H_
#define CHROME_DRIVER_H_

#include <cstdio>
#include <cstdlib>
#include <windows.h>
#include "errorcodes.h"

#include "base/at_exit.h"
#include "base/message_loop.h"
#include "chrome/test/automation/automation_proxy.h"
#include "chrome/test/automation/browser_proxy.h"
#include "chrome/test/automation/tab_proxy.h"
#include "chrome/test/automation/window_proxy.h"

using namespace std;

class ChromeDriver {
 public:
  ChromeDriver();
  ~ChromeDriver();

  int Launch();
  bool getVisible();
  int setVisible(int visible);
  int close();

  int get(const std::wstring url);
  int back();
  int forward();

  std::wstring getCurrentUrl();
  std::wstring getTitle();
  std::wstring getPageSource();

  // Cookies related.
  std::wstring getCookies();
  int addCookie(const char* cookieString);

  // DOM related.
  std::wstring domGetString(std::wstring operation);
  int domGetInteger(std::wstring operation, int* value);
  int domGetBoolean(std::wstring operation, bool* value);
  int domSetter(std::wstring operation, const std::wstring value);
  int domExecute(std::wstring operation);

 protected:

//    bool ExecuteAndExtractString(const std::wstring& frame_xpath,
//        const std::wstring& jscript, std::wstring* value);
//    bool ExecuteAndExtractBool(const std::wstring& frame_xpath,
//        const std::wstring& jscript, bool* value);
//    bool ExecuteAndExtractInt(const std::wstring& frame_xpath,
//        const std::wstring& jscript, int* value);
//    bool ExecuteAndExtractValue(const std::wstring& frame_xpath,
//        const std::wstring& jscript, Value** value);

 private:

  // Other requirements.
  AutomationProxy* proxy_;
  WindowProxy* window_proxy_;
  BrowserProxy* browser_proxy_;
  TabProxy* tab_proxy_;
  bool is_visible_;

  base::AtExitManager exitManager;
  // MessageLoopForUI ui_loop_;
  MessageLoopForIO io_loop_;

  // Temporary till decided.
  static const int timeout_ = 10000;

private:
  DISALLOW_COPY_AND_ASSIGN(ChromeDriver);
};

#endif  // CHROME_DRIVER_H_
