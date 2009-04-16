using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace OpenQa.Selenium.IE
{
    public class InternetExplorerDriver : IWebDriver
    {

        private bool disposed = false;
        private SafeInternetExplorerDriverHandle handle;

        [DllImport("InternetExplorerDriver")]
        private static extern int wdNewDriverInstance(ref SafeInternetExplorerDriverHandle handle);
        public InternetExplorerDriver()
        {
            handle = new SafeInternetExplorerDriverHandle();
            int result = wdNewDriverInstance(ref handle);
            if (result != 0)
            {
                throw new Exception("Doh!");
            }
        }

        public void Dispose()
        {
            if (!disposed)
            {
                handle.Dispose();
                disposed = true;
            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern void wdGet(SafeHandle handle, string url);
        public void Get(string url)
        {
            if (disposed)
            {
                throw new ObjectDisposedException("handle");
            }
            if (url == null)
            {
                throw new ArgumentNullException("Argument 'url' cannot be null.");
            }
            wdGet(handle, url);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdGetCurrentUrl(SafeHandle handle, ref StringWrapperHandle result);
        public string CurrentUrl
        {
            get
            {
                StringWrapperHandle result = new StringWrapperHandle();


                if (wdGetCurrentUrl(handle, ref result) != 0)
                {
                    throw new Exception("wdGetCurrentUrl Doomed");
                }
                return result.Value;

            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdGetTitle(SafeHandle handle, ref StringWrapperHandle result);
        public string Title
        {
            get
            {
                StringWrapperHandle result = new StringWrapperHandle();


                if (wdGetTitle(handle, ref result) != 0)
                {
                    throw new Exception("wdGetTitle Doomed");
                }
                return result.Value;
            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdSetVisible(SafeHandle handle, int visible);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdGetVisible(SafeHandle handle, ref int visible);
        public bool Visible
        {
            get
            {
                int visible = 0;
                wdGetVisible(handle, ref visible);
                return (visible == 1) ? true : false;
            }

            set
            {
                wdSetVisible(handle, value ? 1 : 0);
            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdFindElementById(SafeHandle driver, ElementWrapper element, [MarshalAs(UnmanagedType.LPWStr)] String id, ref ElementWrapper result);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdFindElementByLinkText(SafeHandle driver, ElementWrapper element, [MarshalAs(UnmanagedType.LPWStr)] String id, ref ElementWrapper result);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdFindElementByName(SafeHandle driver, ElementWrapper element, [MarshalAs(UnmanagedType.LPWStr)] String id, ref ElementWrapper result);
        public IWebElement FindOneElement(By mechanism, string locator)
        {
            ElementWrapper rawElement = new ElementWrapper();
            int result;
            ElementWrapper parent = new ElementWrapper();

            try
            {
                switch (mechanism)
                {
                    case By.Id:
                        result = wdFindElementById(handle, parent, locator, ref rawElement);
                        break;

                    case By.LinkText:
                        result = wdFindElementByLinkText(handle, parent, locator, ref rawElement);
                        break;

                    case By.Name:
                        result = wdFindElementByName(handle, parent, locator, ref rawElement);
                        break;

                    default:
                        throw new ArgumentException("Unrecognised element location mechanism: " + mechanism);
                }

                if (result != 0)
                {
                    throw new Exception("Cannot locate element");
                }
                return new InternetExplorerWebElement(this, rawElement);
            }
            catch (SEHException)
            {
                // Unable to find the element
                return null;
            }
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdWaitForLoadToComplete(SafeInternetExplorerDriverHandle driver);
        internal void WaitForLoadToComplete()
        {
            wdWaitForLoadToComplete(handle);
        }

        public void Close()
        {
            if (!handle.IsClosed)
            {
                handle.Close();
            }
        }

        public void Quit()
        {
            Close();
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdGetPageSource(SafeInternetExplorerDriverHandle driver, ref StringWrapperHandle wrapper);
        public string PageSource
        {
            get
            {
                StringWrapperHandle result = new StringWrapperHandle();
                if (wdGetPageSource(handle, ref result) != 0)
                {
                    throw new Exception("wdGetPageSource Doomed");
                }
                return result.Value;
            }
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdNewScriptArgs(ref IntPtr scriptArgs, int maxLength);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern int wdExecuteScript(SafeInternetExplorerDriverHandle driver, string script, IntPtr scriptArgs, ref IntPtr scriptRes);
        public Object ExecuteScript(String script, params Object[] args)
        {
            
            IntPtr scriptArgsRef = new IntPtr();
            int result = wdNewScriptArgs(ref scriptArgsRef, args.Length);
            //handleErrorCode("Unable to create new script arguments array", result);
            IntPtr scriptArgs = scriptArgsRef;

            try
            {
                //populateArguments(result, scriptArgs, args);

                script = "(function() { return function(){" + script + "};})();";

                IntPtr scriptResultRef = new IntPtr();
                result = wdExecuteScript(handle, script, scriptArgs, ref scriptResultRef);

                //handleErrorCode("Cannot execute script", result);
                //Object toReturn = extractReturnValue(scriptResultRef);
                return null;
            }
            finally
            {
                //wdFreeScriptArgs(scriptArgs);
            }
        }

        public ITargetLocator SwitchTo()
        {
            return new InternetExplorerTargetLocator(handle, this);
        }

        public IOptions Manage()
        {
            return new InternetExplorerOptions(handle, this);
        }

        public INavigation Navigate()
        {
            return new InternetExplorerNavigation(handle, this);
        }

        private class InternetExplorerOptions : IOptions
        {

            private SafeInternetExplorerDriverHandle handle;
            private InternetExplorerDriver driver;

            public InternetExplorerOptions(SafeInternetExplorerDriverHandle handle,
                                           InternetExplorerDriver driver)
            {
                this.handle = handle;
                this.driver = driver;
            }

            [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
            private static extern int wdAddCookie(SafeHandle handle, string cookie);
            public void AddCookie(Cookie cookie)
            {
                //TODO(andre.nogueira): check return value
                int result;
                String cookieString = cookie.ToString();
                result = wdAddCookie(handle, cookieString);
            }

            [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
            private static extern int wdGetCookies(SafeHandle handle, ref StringWrapperHandle cookies);
            //TODO(andre.nogueira): check result
            public Dictionary<String, Cookie> GetCookies()
            {
                String currentUrl = GetCurrentHost();

                StringWrapperHandle wrapper = new StringWrapperHandle();
                int result = wdGetCookies(handle, ref wrapper);

                //handleErrorCode("Unable to extract visible cookies", result);

                Dictionary<String, Cookie> toReturn = new Dictionary<String, Cookie>();
                String allDomainCookies = wrapper.Value;


                String[] cookies =
                    allDomainCookies.Split(new String[] { "; " },
                                           StringSplitOptions.RemoveEmptyEntries);
                foreach (String cookie in cookies)
                {
                    String[] parts = cookie.Split(new String[] { "=" }, StringSplitOptions.RemoveEmptyEntries);
                    if (parts.Length != 2)
                    {
                        continue;
                    }

                    toReturn.Add(parts[0], new Cookie(parts[0], parts[1], currentUrl, ""));
                }

                return toReturn;
            }

            private String GetCurrentHost()
            {
                Uri uri = new Uri(driver.CurrentUrl);
                return uri.Host;
            }

            public void DeleteCookie(Cookie cookie)
            {

                DateTime dateInPast = new DateTime(1);
                AddCookie(new Cookie(cookie.Name, "", cookie.Path, cookie.Domain, dateInPast));
            }

            public void DeleteCookieNamed(String name)
            {
                // TODO(andre.nogueira): Work not completed, still not working
                Cookie cookieToDelete = new Cookie(name, "", ""/*GetCurrentHost()*/, "/");
                String c = cookieToDelete.ToString();

                DeleteCookie(cookieToDelete);
            }
        }

        private class InternetExplorerTargetLocator : ITargetLocator
        {

            InternetExplorerDriver driver;
            SafeInternetExplorerDriverHandle handle;
            public InternetExplorerTargetLocator(SafeInternetExplorerDriverHandle handle,
                InternetExplorerDriver driver)
            {
                this.driver = driver;
                this.handle = handle;
            }

            // TODO(andre.nogueira): Documentation should mention
            // indexes are 0-based, and that there might be problems
            // when frames are named as integers.
            public IWebDriver Frame(int frameIndex)
            {
                return Frame(frameIndex.ToString());
            }

            //TODO(andre.nogueira): Check return values
            [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
            private static extern int wdSwitchToFrame(SafeHandle handle, string frameName);
            public IWebDriver Frame(string frameName)
            {
                wdSwitchToFrame(handle, frameName);
                return driver;
            }

            public IWebDriver Window(string windowName)
            {
                throw new Exception("The method or operation is not implemented.");
            }

            public IWebDriver DefaultContent()
            {
                return Frame("");
            }

            public IWebElement ActiveElement()
            {
                throw new Exception("The method or operation is not implemented.");
            }

        }

        private class InternetExplorerNavigation : INavigation
        {

            SafeInternetExplorerDriverHandle handle;
            InternetExplorerDriver driver;

            public InternetExplorerNavigation(SafeInternetExplorerDriverHandle handle,
                InternetExplorerDriver driver)
            {
                this.handle = handle;
                this.driver = driver;
            }

            [DllImport("InternetExplorerDriver")]
            private static extern int wdGoBack(SafeInternetExplorerDriverHandle driver);
            public void Back()
            {
                //TODO(andre.nogueira): Check return value
                wdGoBack(handle);
            }

            [DllImport("InternetExplorerDriver")]
            private static extern int wdGoForward(SafeInternetExplorerDriverHandle driver);
            public void Forward()
            {
                //TODO(andre.nogueira): Check return value
                wdGoForward(handle);
            }

            public void To(Uri url)
            {
                if (url == null)
                {
                    throw new ArgumentNullException("Argument 'url' cannot be null.");
                }
                String address = url.AbsoluteUri;
                driver.Get(address);
            }

            public void To(string url)
            {
                driver.Get(url);

            }

            public void Refresh()
            {
                throw new Exception("The method or operation is not implemented.");
            }
        }
    }
}
