﻿// <copyright file="DriverService.cs" company="WebDriver Committers">
// Copyright 2007-2011 WebDriver committers
// Copyright 2007-2011 Google Inc.
// Portions copyright 2011 Software Freedom Conservancy
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// </copyright>

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Security.Permissions;
using System.Text;

namespace OpenQA.Selenium
{
    /// <summary>
    /// Exposes the service provided by a native WebDriver server executable.
    /// </summary>
    public abstract class DriverService : IDisposable
    {
        private string driverServicePath;
        private int driverServicePort;
        private Process driverServiceProcess;
        private Uri serviceUrl;

        /// <summary>
        /// Initializes a new instance of the DriverService class.
        /// </summary>
        /// <param name="executable">The full path to the ChromeDriver executable.</param>
        /// <param name="port">The port on which the ChromeDriver executable should listen.</param>
        protected DriverService(string executable, int port)
        {
            this.driverServicePath = executable;
            this.driverServicePort = port;
            this.serviceUrl = new Uri(string.Format(CultureInfo.InvariantCulture, "http://localhost:{0}", port));
        }

        /// <summary>
        /// Gets the Uri of the service.
        /// </summary>
        public Uri ServiceUrl
        {
            get { return this.serviceUrl; }
        }

        /// <summary>
        /// Gets a value indicating whether the service is running.
        /// </summary>
        public bool IsRunning
        {
            [SecurityPermission(SecurityAction.Demand)]
            get { return this.driverServiceProcess != null && !this.driverServiceProcess.HasExited; }
        }

        /// <summary>
        /// Gets the executable file name of the driver service.
        /// </summary>
        protected abstract string DriverServiceExecutableName
        {
            get;
        }

        #region IDisposable Members
        /// <summary>
        /// Releases all resources associated with this <see cref="DriverService"/>.
        /// </summary>
        public void Dispose()
        {
            this.Dispose(true);
            GC.SuppressFinalize(this);
        }
        #endregion

        /// <summary>
        /// Starts the DriverService.
        /// </summary>
        [SecurityPermission(SecurityAction.Demand)]
        public void Start()
        {
            this.driverServiceProcess = new Process();
            this.driverServiceProcess.StartInfo.FileName = this.driverServicePath;
            this.driverServiceProcess.StartInfo.Arguments = string.Format(CultureInfo.InvariantCulture, "-port={0}", this.driverServicePort);
            this.driverServiceProcess.StartInfo.UseShellExecute = false;
            this.driverServiceProcess.Start();
            DateTime timeout = DateTime.Now.Add(TimeSpan.FromSeconds(20));
            Uri serviceHealthUri = new Uri(this.serviceUrl, new Uri("status", UriKind.Relative));
            HttpWebRequest request = HttpWebRequest.Create(serviceHealthUri) as HttpWebRequest;
            bool processStarted = false;
            while (!processStarted && DateTime.Now < timeout)
            {
                try
                {
                    request.GetResponse();
                    processStarted = true;
                }
                catch (WebException)
                {
                }
            }
        }

        /// <summary>
        /// Finds a random, free port to be listened on by the DriverService
        /// </summary>
        /// <returns>A random, free port to be listened on by the DriverService</returns>
        protected static int FindFreePort()
        {
            // Locate a free port on the local machine by binding a socket to
            // an IPEndPoint using IPAddress.Any and port 0. The socket will
            // select a free port.
            int listeningPort = 0;
            using (Socket portSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp))
            {
                IPEndPoint socketEndPoint = new IPEndPoint(IPAddress.Any, 0);
                portSocket.Bind(socketEndPoint);
                socketEndPoint = (IPEndPoint)portSocket.LocalEndPoint;
                listeningPort = socketEndPoint.Port;
            }

            return listeningPort;
        }

        /// <summary>
        /// Releases all resources associated with this <see cref="DriverService"/>.
        /// </summary>
        /// <param name="disposing"><see langword="true"/> if the Dispose method was explicitly called; otherwise, <see langword="false"/>.</param>
        protected virtual void Dispose(bool disposing)
        {
            if (disposing)
            {
                this.Stop();
            }
        }

        /// <summary>
        /// Stops the ChromeDriverService.
        /// </summary>
        [SecurityPermission(SecurityAction.Demand)]
        private void Stop()
        {
            if (this.driverServiceProcess != null && !this.driverServiceProcess.HasExited)
            {
                Uri shutdownUrl = new Uri(this.serviceUrl, "/shutdown");
                DateTime timeout = DateTime.Now.Add(TimeSpan.FromSeconds(3));
                HttpWebRequest request = HttpWebRequest.Create(shutdownUrl) as HttpWebRequest;
                bool processStopped = false;
                while (!processStopped && DateTime.Now < timeout)
                {
                    try
                    {
                        request.GetResponse();
                    }
                    catch (WebException)
                    {
                        processStopped = true;
                    }
                }

                this.driverServiceProcess.WaitForExit();
                this.driverServiceProcess.Dispose();
                this.driverServiceProcess = null;
            }
        }
    }
}
