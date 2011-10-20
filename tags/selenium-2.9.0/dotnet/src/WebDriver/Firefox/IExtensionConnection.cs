﻿// <copyright file="IExtensionConnection.cs" company="WebDriver Committers">
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
using System.Text;
using OpenQA.Selenium.Remote;

namespace OpenQA.Selenium.Firefox
{
    /// <summary>
    /// Defines the interface by which a driver connects to the WebDriver extension.
    /// </summary>
    internal interface IExtensionConnection : ICommandExecutor
    {
        /// <summary>
        /// Starts the connection to the extension.
        /// </summary>
        void Start();

        /// <summary>
        /// Closes the connection to the extension.
        /// </summary>
        void Quit();
    }
}
