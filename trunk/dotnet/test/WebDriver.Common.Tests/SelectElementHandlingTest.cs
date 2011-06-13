using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using System.Collections.ObjectModel;

namespace OpenQA.Selenium
{
    [TestFixture]
    public class SelectElementHandlingTest : DriverTestFixture
    {
        [Test]
        public void ShouldBePossibleToDeselectASingleOptionFromASelectWhichAllowsMultipleChoices()
        {
            driver.Url = formsPage;

            IWebElement multiSelect = driver.FindElement(By.Id("multi"));
            ReadOnlyCollection<IWebElement> options = multiSelect.FindElements(By.TagName("option"));

            IWebElement option = options[0];
            Assert.IsTrue(option.Selected);
            option.Click();
            Assert.IsFalse(option.Selected);
            option.Click();
            Assert.IsTrue(option.Selected);

            option = options[2];
            Assert.IsTrue(option.Selected);
        }

        [Test]
        [IgnoreBrowser(Browser.IE, "IE driver allows deselecting option from a single-select selectlist")]
        [ExpectedException(typeof(InvalidElementStateException))]
        public void ShouldNotBeAbleToDeselectAnOptionFromANormalSelect()
        {
            driver.Url = formsPage;

            IWebElement select = driver.FindElement(By.XPath("//select[@name='selectomatic']"));
            ReadOnlyCollection<IWebElement> options = select.FindElements(By.TagName("option"));
            IWebElement option = options[0];
            option.Click();
        }

        [Test]
        public void ShouldBeAbleToChangeTheSelectedOptionInASelect()
        {
            driver.Url = formsPage;
            IWebElement selectBox = driver.FindElement(By.XPath("//select[@name='selectomatic']"));
            ReadOnlyCollection<IWebElement> options = selectBox.FindElements(By.TagName("option"));
            IWebElement one = options[0];
            IWebElement two = options[1];
            Assert.IsTrue(one.Selected);
            Assert.IsFalse(two.Selected);

            two.Click();
            Assert.IsFalse(one.Selected);
            Assert.IsTrue(two.Selected);
        }

        [Test]
        public void ShouldBeAbleToSelectMoreThanOneOptionFromASelectWhichAllowsMultipleChoices()
        {
            driver.Url = formsPage;

            IWebElement multiSelect = driver.FindElement(By.Id("multi"));
            ReadOnlyCollection<IWebElement> options = multiSelect.FindElements(By.TagName("option"));
            foreach (IWebElement option in options)
            {
                if (!option.Selected)
                {
                    option.Click();
                }
            }

            for (int i = 0; i < options.Count; i++)
            {
                IWebElement option = options[i];
                Assert.IsTrue(option.Selected, "Option at index is not selected but should be: " + i.ToString());
            }
        }
    }
}
