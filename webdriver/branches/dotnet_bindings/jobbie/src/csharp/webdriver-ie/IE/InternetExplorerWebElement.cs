using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Text;

namespace OpenQa.Selenium.IE
{
    class InternetExplorerWebElement : IWebElement, ISearchContext
    {

        private ElementWrapper wrapper;
        private InternetExplorerDriver driver;

        public InternetExplorerWebElement(InternetExplorerDriver driver, ElementWrapper wrapper)
        {
            this.driver = driver;
            this.wrapper = wrapper;
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdeGetText(ElementWrapper wrapper, ref StringWrapperHandle result);
        public string Text
        {
            get 
            {
                StringWrapperHandle result = new StringWrapperHandle();
                wdeGetText(wrapper, ref result);
                return result.Value;
            }
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdeGetTagName(ElementWrapper wrapper, ref StringWrapperHandle result);
        public string TagName
        {
            get
            {
                StringWrapperHandle result = new StringWrapperHandle();
                wdeGetTagName(wrapper, ref result);
                return result.Value;
            }
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdeIsDisplayed(ElementWrapper handle, ref int displayed);
        public bool Displayed
        {
            get 
            {
                int displayed = 0;
                wdeIsDisplayed(wrapper, ref displayed);
                return (displayed == 1);
            }
        }
	
        [DllImport("InternetExplorerDriver")]
        private static extern int wdeClear(ElementWrapper handle);
        public void Clear()
        {
            wdeClear(wrapper);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdeSendKeys(ElementWrapper wrapper, [MarshalAs(UnmanagedType.LPWStr)] string text);
        public void SendKeys(string text)
        {
            wdeSendKeys(wrapper, text);            
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdeSubmit(ElementWrapper wrapper);
        public void Submit()
        {
            wdeSubmit(wrapper);
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdeClick(ElementWrapper wrapper);
        public void Click()
        {
            wdeClick(wrapper);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdeGetAttribute(ElementWrapper wrapper, [MarshalAs(UnmanagedType.LPWStr)] string attributeName, ref StringWrapperHandle result);
        public string GetAttribute(string attributeName)
        {
            StringWrapperHandle result = new StringWrapperHandle();
            wdeGetAttribute(wrapper, attributeName, ref result);
            return result.Value;
        }

        public string Value
        {
            get { return GetAttribute("value"); }
        }


        [DllImport("InternetExplorerDriver")]
        private static extern int wdeIsSelected(ElementWrapper handle, ref int selected);
        [DllImport("InternetExplorerDriver")]
        private static extern int wdeSetSelected(ElementWrapper handle);
        public bool Selected
        {
            get
            {
                int selected = 0;
                int result = wdeIsSelected(wrapper, ref selected);
                ErrorHandler.VerifyErrorCode(result, "Checking if element is selected");
                return (selected == 1);
            }
            set
            {
                int result = wdeSetSelected(wrapper);
                ErrorHandler.VerifyErrorCode(result, "(Un)selecting element");
            }
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdeToggle(ElementWrapper handle, ref int toggled);
        public bool Toggle()
        {
            int toggled = 0;
            int result = wdeToggle(wrapper, ref toggled);
            ErrorHandler.VerifyErrorCode(result, "Toggling element");
            return (toggled == 1);
        }

        public List<IWebElement> FindElements(By by)
        {
            return by.FindElements(new Finder(driver, wrapper));
        }

        public IWebElement FindElement(By by)
        {
            return by.FindElement(new Finder(driver, wrapper));
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdAddElementScriptArg(IntPtr scriptArgs, ElementWrapper handle);
        public int AddToScriptArgs(IntPtr scriptArgs)
        {
            return wdAddElementScriptArg(scriptArgs, wrapper);
        }

    }
}
