using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using OpenQa.Selenium.Environment;

namespace OpenQa.Selenium
{
    [TestFixture]
    public class WindowSwitchingTest : DriverTestFixture
    {
        [Test]
        public void ShouldSwitchFocusToANewWindowWhenItIsOpenedAndNotStopFutureOperations()
        {
            driver.Url = xhtmlTestPage;
            String current = driver.GetWindowHandle();
            driver.FindElement(By.LinkText("Open new window")).Click();
            Assert.AreEqual("XHTML Test Page", driver.Title);

            driver.SwitchTo().Window("result");
            Assert.AreEqual("We Arrive Here", driver.Title);
            driver.Url = iframesPage;
            driver.FindElement(By.Id("iframe_page_heading"));

            driver.SwitchTo().Window(current);
            Assert.AreEqual("XHTML Test Page", driver.Title);
        }

        [Test]
        public void ShouldGetBrowserHandles()
        {

            driver.Url = xhtmlTestPage;
            driver.FindElement(By.LinkText("Open new window")).Click();

            string handle1, handle2;
            handle1 = driver.GetWindowHandle();

            driver.SwitchTo().Window("result");
            handle2 = driver.GetWindowHandle();

            List<string> handles = driver.GetWindowHandles();

            // At least the two handles we want should be there.
            Assert.Contains(handle1, handles, "Should have contained current handle");
            Assert.Contains(handle2, handles, "Should have contained result handle");

            // Some (semi-)clean up..
            driver.Url = macbethPage;
            driver.SwitchTo().Window(handle1);
        }

        [Test]
        [IgnoreBrowser(Browser.IE,"Can't close handle and use it afterwards in IE driver")]
        public void CloseShouldCloseCurrentHandleOnly()
        {
            driver.Url = xhtmlTestPage;
            driver.FindElement(By.LinkText("Open new window")).Click();

            string handle1, handle2;
            handle1 = driver.GetWindowHandle();

            driver.SwitchTo().Window("result");
            handle2 = driver.GetWindowHandle();
           
            driver.Close();
            // TODO(andre.nogueira): IE: Safe handles don't allow this, as the
            // handle has already been closed! (Throws exception)
            List<string> handles = driver.GetWindowHandles();

            Assert.IsFalse(handles.Contains(handle2), "Invalid handle still in handle list");
            Assert.IsTrue(handles.Contains(handle1), "Valid handle not in handle list");

            // Clean up after ourselves
            EnvironmentManager.Instance.CreateFreshDriver();
        }

    }
}
