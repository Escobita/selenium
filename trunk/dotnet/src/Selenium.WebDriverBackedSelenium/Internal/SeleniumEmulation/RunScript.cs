﻿using System;
using System.Collections.Generic;
using System.Text;
using OpenQA.Selenium;

namespace Selenium.Internal.SeleniumEmulation
{
    /// <summary>
    /// Defines the command for the runScript keyword.
    /// </summary>
    internal class RunScript : SeleneseCommand
    {
        private IScriptMutator mutator;

        public RunScript(IScriptMutator mutator)
        {
            this.mutator = mutator;
        }
        /// <summary>
        /// Handles the command.
        /// </summary>
        /// <param name="driver">The driver used to execute the command.</param>
        /// <param name="locator">The first parameter to the command.</param>
        /// <param name="value">The second parameter to the command.</param>
        /// <returns>The result of the command.</returns>
        protected override object HandleSeleneseCommand(IWebDriver driver, string locator, string value)
        {
            StringBuilder builder = new StringBuilder();
            mutator.Mutate(locator, builder);
            ((IJavaScriptExecutor)driver).ExecuteScript(builder.ToString());
            return null;
        }
    }
}
