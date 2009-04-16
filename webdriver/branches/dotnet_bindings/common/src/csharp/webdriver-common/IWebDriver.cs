using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    public interface IWebDriver : IDisposable
    {
        string CurrentUrl
        {
            get;
        }

        string Title
        {
            get;
        }

        void Get(string url);

        IWebElement FindOneElement(By mechanism, string locator);

        String PageSource
        {
            get;
        }

        void Close();

        void Quit();

        Object ExecuteScript(String script, params Object[] args);

        IOptions Manage();

        INavigation Navigate();

        ITargetLocator SwitchTo();
    }
}
