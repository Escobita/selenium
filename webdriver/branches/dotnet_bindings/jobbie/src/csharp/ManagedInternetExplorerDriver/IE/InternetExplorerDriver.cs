using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace OpenQa.Selenium.IE
{
    public class InternetExplorerDriver : IWebDriver
    {
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
                        throw new Exception("Doomed");
                    }
                    return result.Value;
                
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

        private bool disposed = false;
        private SafeInternetExplorerDriverHandle handle;
    }
}
