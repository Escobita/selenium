using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    public interface IWebElement
    {
        string ElementName
        {
            get;
        }

        string Text
        {
            get;
        }

        bool Visible
        {
            get;
        }

        bool Selected
        {
            get;
            set;
        }

        void Clear();
        void SendKeys(string text);

        void Submit();

        void Click();

        string GetAttribute(string attributeName);

        bool Toggle();

        List<IWebElement> FindElements(By by);

        IWebElement FindElement(By by);
    }
}
