using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using System.Collections.ObjectModel;

namespace OpenQA.Selenium
{
    [TestFixture]
    public class XPathElementFindingTest : DriverTestFixture
    {
        [Test]
        [ExpectedException(typeof(NoSuchElementException))]
        public void ShouldThrowAnExceptionWhenThereIsNoLinkToClickAndItIsFoundWithXPath()
        {
            driver.Url = xhtmlTestPage;
            driver.FindElement(By.XPath("//a[@id='Not here']"));
        }

        [Test]
        [ExpectedException(typeof(NoSuchElementException))]
        public void ShouldThrowAnExceptionWhenThereIsNoLinkToClick()
        {
            driver.Url = xhtmlTestPage;
            driver.FindElement(By.XPath("//a[@id='Not here']"));
        }

        [Test]
        public void ShouldFindSingleElementByXPath()
        {
            driver.Url = xhtmlTestPage;
            IWebElement element = driver.FindElement(By.XPath("//h1"));
            Assert.AreEqual(element.Text, "XHTML Might Be The Future");
        }

        [Test]
        public void ShouldFindElementsByXPath()
        {
            driver.Url = xhtmlTestPage;
            ReadOnlyCollection<IWebElement> divs = driver.FindElements(By.XPath("//div"));

            Assert.AreEqual(divs.Count, 13);
        }

        [Test]
        public void ShouldBeAbleToFindManyElementsRepeatedlyByXPath()
        {
            driver.Url = xhtmlTestPage;
            String xpathString = "//node()[contains(@id,'id')]";
            Assert.AreEqual(driver.FindElements(By.XPath(xpathString)).Count, 3);

            xpathString = "//node()[contains(@id,'nope')]";
            Assert.AreEqual(driver.FindElements(By.XPath(xpathString)).Count, 0);
        }

        [Test]
        public void ShouldBeAbleToIdentifyElementsByClass()
        {
            driver.Url = xhtmlTestPage;

            String header = driver.FindElement(By.XPath("//h1[@class='header']")).Text;
            Assert.AreEqual(header, "XHTML Might Be The Future");
        }

        [Test]
        public void ShouldBeAbleToSearchForMultipleAttributes()
        {
            driver.Url = formsPage;
            driver.FindElement(By.XPath("//form[@name='optional']/input[@type='submit' and @value='Click!']")).Click();
        }

        [Test]
        public void ShouldLocateElementsWithGivenText()
        {
            driver.Url = xhtmlTestPage;
            driver.FindElement(By.XPath("//a[text()='click me']"));
        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInDriverFindElement()
        {
            driver.Url = formsPage;
            driver.FindElement(By.XPath("this][isnot][valid"));

        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInDriverFindElements()
        {
            driver.Url = formsPage;
            driver.FindElements(By.XPath("this][isnot][valid"));
        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInElementFindElement()
        {
            driver.Url = formsPage;
            IWebElement body = driver.FindElement(By.TagName("body"));
            body.FindElement(By.XPath("this][isnot][valid"));
        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInElementFindElements()
        {
            driver.Url = formsPage;
            IWebElement body = driver.FindElement(By.TagName("body"));
            body.FindElements(By.XPath("this][isnot][valid"));
        }


        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInDriverFindElement()
        {
            driver.Url = formsPage;
            driver.FindElement(By.XPath("count(//input)"));
        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInDriverFindElements()
        {
            driver.Url = formsPage;
            driver.FindElements(By.XPath("count(//input)"));
        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInElementFindElement()
        {
            driver.Url = formsPage;
            IWebElement body = driver.FindElement(By.TagName("body"));
            body.FindElement(By.XPath("count(//input)"));
        }

        [Test]
        [IgnoreBrowser(Browser.Android, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.IPhone, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Opera, "InvalidSelectorException not implemented for driver")]
        [IgnoreBrowser(Browser.Remote, "InvalidSelectorException not implemented for driver")]
        [ExpectedException(typeof(InvalidSelectorException))]
        public void ShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInElementFindElements()
        {
            driver.Url = formsPage;
            IWebElement body = driver.FindElement(By.TagName("body"));
            body.FindElements(By.XPath("count(//input)"));
        }
    }
}

