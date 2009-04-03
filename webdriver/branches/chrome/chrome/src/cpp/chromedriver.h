#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

#include "base/at_exit.h"
#include "base/message_loop.h"

#ifndef SUCCESS
#define SUCCESS 0
#endif    // SUCCESS definition.

#ifndef CHROME_DRIVER_H_
#define CHROME_DRIVER_H_

static const int timeout_ = 4000;
static base::AtExitManager exitManager;
static MessageLoopForUI ui_loop_;

class AutomationProxy;
class BrowserProxy;
class TabProxy;
class WindowProxy;
class ChromeElement;

class ChromeDriver {

  public:
    ChromeDriver();

    ~ChromeDriver();

    int Launch();

    void setProxy(int index, int tabindex);

    // ------------------------------------------------------------------------
    // Browser related.
    // ------------------------------------------------------------------------
    bool getVisible() { return is_visible_; }

    int setVisible(bool visible);

    int close();

    // ----------------------------------------------------------------------------
    // Page Related.
    // ----------------------------------------------------------------------------
    int get(const std::wstring url);

    int back();

    int forward();

    std::wstring getCurrentUrl();

    std::wstring getTitle();

    std::wstring getPageSource();

    int switchToActiveElement(ChromeElement** element);

    int switchToFrame(int index);

    int switchToFrame(const std::wstring name);

    int switchToWindow(const std::wstring name);

    // ----------------------------------------------------------------------------
    // Cookie Related.
    // ----------------------------------------------------------------------------
    std::wstring getCookies();

    int addCookie(const std::wstring cookieString);

    // ----------------------------------------------------------------------------
    // Element Related.
    // ----------------------------------------------------------------------------
    int findElementById(const std::wstring id, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsById(const std::wstring id);

    int findElementByTagName(const std::wstring tag, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsByTagName(const std::wstring tag);

    int findElementByClassName(const std::wstring cls, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsByClassName(const std::wstring cls);

    int findElementByLinkText(const std::wstring text, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsByLinkText(const std::wstring text);

    int findElementByPartialLinkText(const std::wstring pattern, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsByPartialLinkText(const std::wstring pattern);

    int findElementByName(const std::wstring name, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsByName(const std::wstring name);

    int findElementByXPath(const std::wstring xpath, ChromeElement** element);

    std::vector<ChromeElement*>* findElementsByXPath(const std::wstring xpath);

  protected:
    friend class ChromeElement;
    TabProxy* getTabProxy();
    BrowserProxy* getBrowserProxy();
    WindowProxy* getWindowProxy();
    AutomationProxy* getAutomationProxy();

    // default context
    int default_window_index_;
    int default_tab_index_;

    std::wstring domGetString(const std::wstring jscript);
    int domGetInteger(const std::wstring jscript, int* value);
    int domGetBoolean(const std::wstring jscript, bool* value);
    int domGetStringArray(const std::wstring jscript, std::vector<std::wstring>& retValue);
    int domGetIntegerArray(const std::wstring jscript, std::vector<int>& retValue);
    int domGetBooleanArray(const std::wstring jscript, std::vector<bool>& retValue);
    int domGetVoid(const std::wstring jscript);

  private:
    // Copy constructor.
    ChromeDriver(ChromeDriver* other);

    // Internal variables.
    int e_counter_;
    bool is_visible_;
    AutomationProxy* proxy_;
    WindowProxy* window_proxy_;
    BrowserProxy* browser_proxy_;
    TabProxy* tab_proxy_;
    std::wstring current_frame_;
    std::wstring current_frame_src_;

    // Internal members.
    std::wstring getApplicationPath();
    int currentCount() { return ++e_counter_; }
};

#endif  // CHROME_DRIVER_H_
