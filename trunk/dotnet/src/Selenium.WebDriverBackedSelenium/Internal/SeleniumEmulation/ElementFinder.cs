using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using OpenQA.Selenium;

namespace Selenium.Internal.SeleniumEmulation
{
    /// <summary>
    /// Provides methods for finding elements.
    /// </summary>
    internal class ElementFinder
    {
        private string findElement;
        private Dictionary<string, string> lookupStrategies = new Dictionary<string, string>();

        /// <summary>
        /// Initializes a new instance of the <see cref="ElementFinder"/> class.
        /// </summary>
        public ElementFinder()
        {
            string rawScript = JavaScriptLibrary.GetSeleniumScript("findElement.js");
            this.findElement = "return (" + rawScript + ")(arguments[0]);";

            string linkTextLocator = "return (" + JavaScriptLibrary.GetSeleniumScript("linkLocator.js") + ").call(null, arguments[0], document)";

            this.AddStrategy("link", linkTextLocator);
        }

        /// <summary>
        /// Finds an element.
        /// </summary>
        /// <param name="driver">The <see cref="IWebDriver"/> to use in finding the elements.</param>
        /// <param name="locator">The locator string describing how to find the element.</param>
        /// <returns>An <see cref="IWebElement"/> described by the locator.</returns>
        /// <exception cref="SeleniumException">There is no element matching the locator.</exception>
        internal IWebElement FindElement(IWebDriver driver, string locator)
        {
            IJavaScriptExecutor executor = driver as IJavaScriptExecutor;
            IWebElement result;
            string strategy = this.FindStrategy(locator);
            if (!string.IsNullOrEmpty(strategy))
            {
                string actualLocator = locator.Substring(locator.IndexOf('=') + 1);

                // TODO(simon): Recurse into child documents
                try
                {
                    result = executor.ExecuteScript(strategy, actualLocator) as IWebElement;

                    if (result == null)
                    {
                        throw new SeleniumException("Element " + locator + " not found");
                    }

                    return result;
                }
                catch (WebDriverException)
                {
                    throw new SeleniumException("Element " + locator + " not found");
                }
            }

            try
            {
                result = FindElementDirectly(driver, locator);
                if (result != null)
                {
                    return result;
                }

                return executor.ExecuteScript(this.findElement, locator) as IWebElement;
            }
            catch (WebDriverException)
            {
                throw new SeleniumException("Element " + locator + " not found");
            }
            catch (InvalidOperationException)
            {
                throw new SeleniumException("Element " + locator + " not found");
            }
        }

        /// <summary>
        /// Gets the strategy used to find elements.
        /// </summary>
        /// <param name="locator">The locator string that defines the strategy.</param>
        /// <returns>A string used in finding elements.</returns>
        internal string FindStrategy(string locator)
        {
            string strategy = string.Empty;
            int index = locator.IndexOf('=');
            if (index == -1)
            {
                return null;
            }

            string strategyName = locator.Substring(0, index);
            if (!this.lookupStrategies.TryGetValue(strategyName, out strategy))
            {
                return null;
            }

            return strategy;
        }

        /// <summary>
        /// Adds a strategy to the dictionary of known lookup strategies.
        /// </summary>
        /// <param name="strategyName">The name used to identify the lookup strategy.</param>
        /// <param name="strategy">The string used in finding elements.</param>
        internal void AddStrategy(string strategyName, string strategy)
        {
            this.lookupStrategies.Add(strategyName, strategy);
        }

        private static IWebElement FindElementDirectly(IWebDriver driver, string locator)
        {
            if (locator.StartsWith("xpath=", StringComparison.Ordinal))
            {
                return driver.FindElement(By.XPath(locator.Substring("xpath=".Length)));
            }

            if (locator.StartsWith("//", StringComparison.Ordinal))
            {
                return driver.FindElement(By.XPath(locator));
            }

            if (locator.StartsWith("css=", StringComparison.Ordinal))
            {
                return driver.FindElement(By.CssSelector(locator.Substring("css=".Length)));
            }

            return null;
        }
    }
}
