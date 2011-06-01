﻿using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using OpenQA.Selenium;

namespace Selenium.Internal.SeleniumEmulation
{
    /// <summary>
    /// Provides a set of methods designed to help selecting options in select lists.
    /// </summary>
    internal class SeleniumSelect
    {
        private string findOption;
        private IWebDriver driver;
        private IWebElement select;

        /// <summary>
        /// Initializes a new instance of the <see cref="SeleniumSelect"/> class.
        /// </summary>
        /// <param name="finder">An <see cref="ElementFinder"/> used in finding options.</param>
        /// <param name="driver">An <see cref="IWebDriver"/> used to drive the browser.</param>
        /// <param name="locator">A locator used to find options.</param>
        public SeleniumSelect(ElementFinder finder, IWebDriver driver, string locator)
        {
            this.driver = driver;

            this.findOption = "return (" + JavaScriptLibrary.GetSeleniumScript("findOption.js") + ").apply(null, arguments)";

            this.select = finder.FindElement(driver, locator);
            if (this.select.TagName.ToLowerInvariant() != "select")
            {
                throw new SeleniumException("Element is not a select element: " + locator);
            }
        }

        /// <summary>
        /// Gets a collection of elements representing all options for the select list.
        /// </summary>
        public ReadOnlyCollection<IWebElement> AllOptions
        {
            get
            {
                return this.select.FindElements(By.TagName("option"));
            }
        }

        /// <summary>
        /// Gets a collection of elements representing all currently selected options for the select list.
        /// </summary>
        public ReadOnlyCollection<IWebElement> SelectedOptions
        {
            get
            {
                List<IWebElement> toReturn = new List<IWebElement>();

                foreach (IWebElement option in this.select.FindElements(By.TagName("option")))
                {
                    if (option.Selected)
                    {
                        toReturn.Add(option);
                    }
                }

                return toReturn.AsReadOnly();
            }
        }

        /// <summary>
        /// Gets a value indicating whether the select list supports multiple selections.
        /// </summary>
        private bool IsMultiple
        {
            get
            {
                string multipleValue = this.select.GetAttribute("multiple");
                bool multiple = multipleValue == "true" || multipleValue == "multiple";
                return multiple;
            }
        }

        /// <summary>
        /// Selects the indicated option.
        /// </summary>
        /// <param name="optionLocator">The locator to use to find the option to select.</param>
        public void SetSelected(string optionLocator)
        {
            if (this.IsMultiple)
            {
                foreach (IWebElement opt in this.select.FindElements(By.TagName("option")))
                {
                    if (opt.Selected)
                    {
                        opt.Toggle();
                    }
                }
            }

            IWebElement option = this.LocateOption(optionLocator);
            if (option != null)
            {
                option.Select();
            }
        }

        /// <summary>
        /// Adds a selection to the currently selected options.
        /// </summary>
        /// <param name="optionLocator">The locator to use to find the option to select.</param>
        public void AddSelection(string optionLocator)
        {
            this.AssertSupportsMultipleSelections();

            IWebElement option = this.LocateOption(optionLocator);
            if (option != null)
            {
                option.Select();
            }
        }

        /// <summary>
        /// Deselects a currently selected option.
        /// </summary>
        /// <param name="optionLocator">The locator to use to find the option to select.</param>
        public void RemoveSelection(string optionLocator)
        {
            this.AssertSupportsMultipleSelections();

            IWebElement option = this.LocateOption(optionLocator);
            if (option != null && option.Selected)
            {
                option.Toggle();
            }
        }

        private IWebElement LocateOption(string optionLocator)
        {
            IWebElement option = null;
            try
            {
                option = ((IJavaScriptExecutor)this.driver).ExecuteScript(this.findOption, this.select, optionLocator) as IWebElement;
            }
            catch (InvalidOperationException)
            {
            }

            return option;
        }

        private void AssertSupportsMultipleSelections()
        {
            if (!this.IsMultiple)
            {
                throw new SeleniumException("You may only add a selection to a select that supports multiple selections");
            }
        }
    }
}
