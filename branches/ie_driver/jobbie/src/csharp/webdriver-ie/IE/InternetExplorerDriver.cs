using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Net;
using System.Net.Sockets;
using System.Runtime.InteropServices;
using System.Text;
using OpenQA.Selenium.Internal;
using OpenQA.Selenium.Remote;

namespace OpenQA.Selenium.IE
{
    public class InternetExplorerDriver : RemoteWebDriver, IFindsByCssSelector, ITakesScreenshot
    {
        private static int port = FindFreePort();

        public InternetExplorerDriver()
            : base(new Uri("http://localhost:" + port.ToString()), DesiredCapabilities.InternetExplorer())
        {
        }

        protected override void StartClient()
        {
            NativeDriverLibrary.Instance.StartServer(port);
        }

        protected override void StopClient()
        {
            NativeDriverLibrary.Instance.StopServer();
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

        /// <summary>
        /// Creates a <see cref="RemoteWebElement"/> with the specified ID.
        /// </summary>
        /// <param name="elementId">The ID of this element.</param>
        /// <returns>A <see cref="RemoteWebElement"/> with the specified ID. For the FirefoxDriver this will be a <see cref="FirefoxWebElement"/>.</returns>
        protected override RemoteWebElement CreateElement(string elementId)
        {
            return new InternetExplorerWebElement(this, elementId);
        }

        #endregion

        #region ITakesScreenshot Members

        public Screenshot GetScreenshot()
        {
            // Get the screenshot as base64.
            Response screenshotResponse = Execute(DriverCommand.Screenshot, null);
            string base64 = screenshotResponse.Value.ToString();

            // ... and convert it.
            return new Screenshot(base64);
        }

        #endregion

        private static int FindFreePort()
        {
            // Locate a free port on the local machine by binding a socket to
            // an IPEndPoint using IPAddress.Any and port 0. The socket will
            // select a free port.
            Socket portSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            IPEndPoint socketEndPoint = new IPEndPoint(IPAddress.Any, 0);
            portSocket.Bind(socketEndPoint);
            socketEndPoint = (IPEndPoint)portSocket.LocalEndPoint;
            int listeningPort = socketEndPoint.Port;
            portSocket.Close();
            return listeningPort;
        }
    }
}
