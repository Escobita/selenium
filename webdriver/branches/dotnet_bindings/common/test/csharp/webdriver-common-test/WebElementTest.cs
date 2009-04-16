using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    [TestFixture]
    public class WebElementTest : DriverTestFixture
    {

        [Test]
        public void ShouldToggleElement()
        {
            driver.Get(simpleTestPage);
            IWebElement checkbox = driver.FindOneElement(By.Id, "checkbox1");
            Assert.IsFalse(checkbox.Selected);
            Assert.IsTrue(checkbox.Toggle());
            Assert.IsTrue(checkbox.Selected);
        }

        [Test]
        public void ShouldReturnFalseOnNonToggableElement()
        {
            driver.Get(simpleTestPage);
            IWebElement text = driver.FindOneElement(By.Id, "oneline");
            Assert.IsFalse(text.Toggle());
        }

        [Test]
        [ExpectedException(typeof(Exception))]
        public void ShouldThrowExceptionOnNonExistingElement()
        {
            driver.Get(simpleTestPage);
            driver.FindOneElement(By.Id, "doesnotexist");
        }

    }
}
