// <copyright file="SelectElement.cs" company="WebDriver Committers">
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
using System.Text;

namespace OpenQA.Selenium.Support.UI
{
    /// <summary>
    /// Provides a convenience method for manipulating selections of options in an HTML select element.
    /// </summary>
    public class SelectElement
    {
        private readonly IWebElement element;

        /// <summary>
        /// Initializes a new instance of the SelectElement class.
        /// </summary>
        /// <param name="element">The element to be wrapped</param>
        /// <exception cref="ArgumentNullException">Thrown when the <see cref="IWebElement"/> object is <see langword="null"/></exception>
        /// <exception cref="UnexpectedTagNameException">Thrown when the element wrapped is not a &lt;select&gt; element.</exception>
        public SelectElement(IWebElement element)
        {
            if (element == null)
            {
                throw new ArgumentNullException("element", "element cannot be null");
            }

            if (string.IsNullOrEmpty(element.TagName) || string.Compare(element.TagName, "select", StringComparison.OrdinalIgnoreCase) != 0)
            {
                throw new UnexpectedTagNameException("select", element.TagName);
            }

            this.element = element;

            // let check if it's a multiple
            string attribute = element.GetAttribute("multiple");
            this.IsMultiple = attribute != null && attribute.ToLowerInvariant() != "false";
        }

        /// <summary>
        /// Gets a value indicating whether the parent element supports multiple selections.
        /// </summary>
        public bool IsMultiple { get; private set; }

        /// <summary>
        /// Gets the list of options for the select element.
        /// </summary>
        public IList<IWebElement> Options
        {
            get
            {
                return this.element.FindElements(By.TagName("option"));
            }
        }

        /// <summary>
        /// Gets the selected item within the select element.
        /// </summary>
        /// <remarks>If more than one item is selected this will return the first item.</remarks>
        /// <exception cref="NoSuchElementException">Thrown if no option is selected.</exception>
        public IWebElement SelectedOption
        {
            get
            {
                foreach (IWebElement option in this.Options)
                {
                    if (option.Selected)
                    {
                        return option;
                    }
                }

                throw new NoSuchElementException("No option is selected");
            }
        }

        /// <summary>
        /// Gets all of the selected options within the select element.
        /// </summary>
        public IList<IWebElement> AllSelectedOptions
        {
            get
            {
                List<IWebElement> returnValue = new List<IWebElement>();
                foreach (IWebElement option in this.Options)
                {
                    if (option.Selected)
                    {
                        returnValue.Add(option);
                    }
                }

                return returnValue;
            }
        }

        /// <summary>
        /// Select all options by the text displayed.
        /// </summary>
        /// <param name="text">The text of the option to be selected. If an exact match is not found,
        /// this method will perform a substring match.</param>
        /// <remarks>When given "Bar" this method would select an option like:
        /// <para>
        /// &lt;option value="foo"&gt;Bar&lt;/option&gt;
        /// </para>
        /// </remarks>
        /// <exception cref="NoSuchElementException">Thrown if there is no element with the given text present.</exception>
        public void SelectByText(string text)
        {
            // try to find the option via XPATH ...
            IList<IWebElement> options = element.FindElements(By.XPath(".//option[. = " + this.EscapeQuotes(text) + "]"));

            bool matched = false;
            foreach (IWebElement option in options)
            {
                this.SetSelected(option);
                if (!this.IsMultiple)
                {
                    return;
                }

                matched = true;
            }

            if (options.Count == 0 && text.Contains(" "))
            {
                string substringWithoutSpace = this.GetLongestSubstringWithoutSpace(text);
                IList<IWebElement> candidates;
                if (substringWithoutSpace == string.Empty)
                {
                    // hmm, text is either empty or contains only spaces - get all options ...
                    candidates = element.FindElements(By.TagName("option"));
                }
                else
                {
                    // get candidates via XPATH ...
                    candidates = element.FindElements(By.XPath(".//option[contains(., " + this.EscapeQuotes(substringWithoutSpace) + ")]"));
                }

                foreach (IWebElement option in candidates)
                {
                    if (text == option.Text)
                    {
                        this.SetSelected(option);
                        if (!this.IsMultiple) { return; }
                        matched = true;
                    }
                }
            }

            if (!matched)
            {
                throw new NoSuchElementException("Cannot locate element with text: " + text);
            }
        }

        /// <summary>
        /// Select an option by the value.
        /// </summary>
        /// <param name="value">The value of the option to be selected.</param>
        /// <remarks>When given "foo" this method will select an option like:
        /// <para>
        /// &lt;option value="foo"&gt;Bar&lt;/option&gt;
        /// </para>
        /// </remarks>
        /// <exception cref="NoSuchElementException">Thrown when no element with the specified value is found.</exception>
        public void SelectByValue(string value)
        {
            StringBuilder builder = new StringBuilder(".//option[@value = ");
            builder.Append(this.EscapeQuotes(value));
            builder.Append("]");
            IList<IWebElement> options = element.FindElements(By.XPath(builder.ToString()));

            bool matched = false;
            foreach (IWebElement option in options)
            {
                this.SetSelected(option);
                if (!this.IsMultiple)
                {
                    return;
                }

                matched = true;
            }

            if (!matched)
            {
                throw new NoSuchElementException("Cannot locate option with value: " + value);
            }
        }

        /// <summary>
        /// Select the option by the index, as determined by the "index" attribute of the element.
        /// </summary>
        /// <param name="index">The value of the index attribute of the option to be selected.</param>
        /// <exception cref="NoSuchElementException">Thrown when no element exists with the specified index attribute.</exception>
        public void SelectByIndex(int index)
        {
            string match = index.ToString(CultureInfo.InvariantCulture);

            bool matched = false;
            foreach (IWebElement option in this.Options)
            {
                if (option.GetAttribute("index") == match)
                {
                    this.SetSelected(option);
                    if (!this.IsMultiple)
                    {
                        return;
                    }

                    matched = true;
                }
            }

            if (!matched)
            {
                throw new NoSuchElementException("Cannot locate option with index: " + index);
            }
        }

        /// <summary>
        /// Clear all selected entries. This is only valid when the SELECT supports multiple selections.
        /// </summary>
        /// <exception cref="WebDriverException">Thrown when attempting to deselect all options from a SELECT 
        /// that does not support multiple selections.</exception>
        public void DeselectAll()
        {
            if (!this.IsMultiple)
            {
                throw new WebDriverException("You may only deselect all options if multi-select is supported");
            }

            foreach (IWebElement webElement in this.Options)
            {
                if (webElement.Selected)
                {
                    webElement.Click();
                }
            }
        }

        /// <summary>
        /// Deselect the option by the text displayed.
        /// </summary>
        /// <param name="text">The text of the option to be deselected.</param>
        /// <remarks>When given "Bar" this method would deselect an option like:
        /// <para>
        /// &lt;option value="foo"&gt;Bar&lt;/option&gt;
        /// </para>
        /// </remarks>
        public void DeselectByText(string text)
        {
            StringBuilder builder = new StringBuilder(".//option[. = ");
            builder.Append(this.EscapeQuotes(text));
            builder.Append("]");
            IList<IWebElement> options = element.FindElements(By.XPath(builder.ToString()));
            foreach (IWebElement option in options)
            {
                if (option.Selected)
                {
                    option.Click();
                }
            }
        }

        /// <summary>
        /// Deselect the option having value matching the specified text.
        /// </summary>
        /// <param name="value">The value of the option to deselect.</param>
        /// <remarks>When given "foo" this method will deselect an option like:
        /// <para>
        /// &lt;option value="foo"&gt;Bar&lt;/option&gt;
        /// </para>
        /// </remarks>
        public void DeselectByValue(string value)
        {
            StringBuilder builder = new StringBuilder(".//option[@value = ");
            builder.Append(this.EscapeQuotes(value));
            builder.Append("]");
            IList<IWebElement> options = element.FindElements(By.XPath(builder.ToString()));
            foreach (IWebElement option in options)
            {
                if (option.Selected)
                {
                    option.Click();
                }
            }
        }

        /// <summary>
        /// Deselect the option by the index, as determined by the "index" attribute of the element.
        /// </summary>
        /// <param name="index">The value of the index attribute of the option to deselect.</param>
        public void DeselectByIndex(int index)
        {
            string match = index.ToString(CultureInfo.InvariantCulture);
            foreach (IWebElement option in this.Options)
            {
                if (match == option.GetAttribute("index") && option.Selected)
                {
                    option.Click();
                }
            }
        }

        private string EscapeQuotes(string toEscape)
        {
            // Convert strings with both quotes and ticks into: foo'"bar -> concat("foo'", '"', "bar")
            if (toEscape.IndexOf("\"") > -1 && toEscape.IndexOf("'") > -1)
            {
                bool quoteIsLast = false;
                if (toEscape.IndexOf("\"") == toEscape.Length - 1)
                {
                    quoteIsLast = true;
                }

                string[] substrings = toEscape.Split('\"');

                StringBuilder quoted = new StringBuilder("concat(");
                for (int i = 0; i < substrings.Length; i++)
                {
                    quoted.Append("\"").Append(substrings[i]).Append("\"");
                    if (i == substrings.Length - 1)
                    {
                        if (quoteIsLast)
                        {
                            quoted.Append(", '\"')");
                        }
                        else
                        {
                            quoted.Append(")");
                        }
                    }
                    else
                    {
                        quoted.Append(", '\"', ");
                    }
                }
                return quoted.ToString();
            }

            // Escape string with just a quote into being single quoted: f"oo -> 'f"oo'
            if (toEscape.IndexOf("\"") > -1)
            {
                return string.Format("'{0}'", toEscape);
            }

            // Otherwise return the quoted string
            return string.Format("\"{0}\"", toEscape);
        }

        private void SetSelected(IWebElement option)
        {
            if (!option.Selected)
            {
                option.Click();
            }
        }

        private string GetLongestSubstringWithoutSpace(string s)
        {
            string result = string.Empty;
            string[] substrings = s.Split(' ');
            foreach (string substring in substrings)
            {
                if (substring.Length > result.Length)
                {
                    result = substring;
                }
            }
            return result;
        }
    }
}
