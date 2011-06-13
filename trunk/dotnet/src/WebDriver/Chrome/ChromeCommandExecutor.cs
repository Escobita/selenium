﻿// <copyright file="ChromeCommandExecutor.cs" company="WebDriver Committers">
// Copyright 2007-2011 WebDriver committers
// Copyright 2007-2011 Google Inc.
// Portions copyright 2007 ThoughtWorks, Inc
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
using System.Globalization;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using OpenQA.Selenium.Remote;

namespace OpenQA.Selenium.Chrome
{
    /// <summary>
    /// Provides a mechanism to execute commands on the browser
    /// </summary>
    internal class ChromeCommandExecutor : HttpCommandExecutor
    {
        private ChromeDriverService service;

        /// <summary>
        /// Initializes a new instance of the <see cref="ChromeCommandExecutor"/> class.
        /// </summary>
        /// <param name="driverService">The <see cref="ChromeDriverService"/> that drives the browser.</param>
        /// <param name="commandTimeout">The maximum amount of time to wait for each command.</param>
        public ChromeCommandExecutor(ChromeDriverService driverService, TimeSpan commandTimeout)
            : base(GetDriverServiceUrl(driverService), commandTimeout)
        {
            this.service = driverService;
        }

        /// <summary>
        /// Starts the <see cref="ChromeCommandExecutor"/>.
        /// </summary>
        public void Start()
        {
            this.service.Start();
        }

        /// <summary>
        /// Stops the <see cref="ChromeCommandExecutor"/>.
        /// </summary>
        public void Stop()
        {
            this.service.Dispose();
        }

        private static Uri GetDriverServiceUrl(ChromeDriverService driverService)
        {
            Uri driverUrl = null;
            if (driverService != null)
            {
                driverUrl = driverService.ServiceUrl;
            }

            return driverUrl;
        }
    }
}
