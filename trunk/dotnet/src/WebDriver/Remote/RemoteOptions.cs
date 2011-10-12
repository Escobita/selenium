﻿// <copyright file="RemoteOptions.cs" company="WebDriver Committers">
// Copyright 2007-2011 WebDriver committers
// Copyright 2007-2011 Google Inc.
// Portions copyright 2011 Software Freedom Conservatory
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
using System.Collections.ObjectModel;
using System.Text;
using OpenQA.Selenium.Internal;

namespace OpenQA.Selenium.Remote
{
    /// <summary>
    /// Provides a mechanism for setting options needed for the driver during the test.
    /// </summary>
    internal class RemoteOptions : IOptions
    {
        private RemoteWebDriver driver;

        /// <summary>
        /// Initializes a new instance of the RemoteOptions class
        /// </summary>
        /// <param name="driver">Instance of the driver currently in use</param>
        public RemoteOptions(RemoteWebDriver driver)
        {
            this.driver = driver;
        }

        #region IOptions
        /// <summary>
        /// Gets an object allowing the user to manipulate cookies on the page.
        /// </summary>
        public ICookieJar Cookies
        {
            get { return new RemoteCookieJar(this.driver); }
        }

        /// <summary>
        /// Provides access to the timeouts defined for this driver.
        /// </summary>
        /// <returns>An object implementing the <see cref="ITimeouts"/> interface.</returns>
        public ITimeouts Timeouts()
        {
            return new RemoteTimeouts(this.driver);
        }
        #endregion
    }
}
