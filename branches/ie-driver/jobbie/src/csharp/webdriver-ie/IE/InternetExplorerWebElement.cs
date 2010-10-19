using System;
using System.Collections.ObjectModel;
using System.Drawing;
using System.Globalization;
using OpenQA.Selenium.Internal;
using OpenQA.Selenium.Remote;

namespace OpenQA.Selenium.IE
{
    /// <summary>
    /// InternetExplorerWebElement allows you to have access to specific items that are found on the page.
    /// </summary>
    /// <seealso cref="IRenderedWebElement"/>
    /// <seealso cref="ILocatable"/>
    /// <example>
    /// <code>
    /// [Test]
    /// public void TestGoogle()
    /// {
    ///     driver = new InternetExplorerDriver();
    ///     InternetExplorerWebElement elem = driver.FindElement(By.Name("q"));
    ///     elem.SendKeys("Cheese please!");
    /// }
    /// </code>
    /// </example>
    public class InternetExplorerWebElement : RenderedRemoteWebElement
    {
        InternetExplorerWebElement(InternetExplorerDriver parent, string id)
            : base(parent, id)
        {
        }
    }
}
