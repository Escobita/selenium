using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Text;
using OpenQA.Selenium.Internal;
using OpenQA.Selenium.Remote;
using System.Runtime.InteropServices;

namespace OpenQA.Selenium.IE
{
    public class InternetExplorerDriver : RemoteWebDriver, IFindsByCssSelector
    {
        private const int ServerPort = 5555;

        [DllImport("IEDriver.dll")]
        private static extern IntPtr StartServer(int port);

        [DllImport("IEDriver.dll")]
        private static extern void StopServer(IntPtr server);

        private IntPtr nativeServer;

        public InternetExplorerDriver()
            : base(new Uri("http://localhost:" + ServerPort.ToString()), DesiredCapabilities.InternetExplorer())
        {
        }

        protected override void StartClient()
        {
            nativeServer = StartServer(ServerPort);
        }

        protected override void StopClient()
        {
            StopServer(nativeServer);
        }

        #region IFindsByCssSelector Members

        public IWebElement FindElementByCssSelector(string cssSelector)
        {
            return FindElement("css selector", cssSelector);
        }

        public ReadOnlyCollection<IWebElement> FindElementsByCssSelector(string cssSelector)
        {
            return FindElements("css selector", cssSelector);
        }

        #endregion
    }
}
