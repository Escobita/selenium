﻿// <copyright file="TouchAction.cs" company="WebDriver Committers">
// Copyright 2007-2012 WebDriver committers
// Copyright 2007-2012 Google Inc.
// Portions copyright 2012 Software Freedom Conservancy
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

namespace OpenQA.Selenium.Interactions.Internal
{
    /// <summary>
    /// Defines an action for keyboard interaction with the browser.
    /// </summary>
    internal class TouchAction : WebDriverAction
    {
        private ITouchScreen touchScreen;

        /// <summary>
        /// Initializes a new instance of the <see cref="TouchAction"/> class.
        /// </summary>
        /// <param name="touchScreen">The <see cref="ITouchScreen"/> to use in performing the action.</param>
        /// <param name="actionTarget">An <see cref="ILocatable"/> object providing the element on which to perform the action.</param>
        protected TouchAction(ITouchScreen touchScreen, ILocatable actionTarget)
            : base(actionTarget)
        {
            this.touchScreen = touchScreen;
        }

        /// <summary>
        /// Gets the touch screen with which to perform the action.
        /// </summary>
        protected ITouchScreen TouchScreen
        {
            get { return this.touchScreen; }
        }

        /// <summary>
        /// Gets the location at which to perform the action.
        /// </summary>
        protected ICoordinates ActionLocation
        {
            get
            {
                if (this.ActionTarget != null)
                {
                    return ActionTarget.Coordinates;
                }

                return null;
            }
        }
    }
}
