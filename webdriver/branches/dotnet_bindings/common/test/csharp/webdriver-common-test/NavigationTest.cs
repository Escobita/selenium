using System;
using NUnit.Framework;

namespace OpenQa.Selenium
{

    [TestFixture]
    public class NavigationTest : DriverTestFixture
    {

        [Test]
        public void ShouldNotHaveProblemNavigatingWithNoPagesBrowsed()
        {
            GetFreshDriver();
            INavigation navigation;
            navigation = driver.Navigate();
            navigation.Back();
            navigation.Forward();
        }

        [Test]
        public void ShouldGoBackAndForward()
        {
            INavigation navigation;
            navigation = driver.Navigate();

            driver.Get(macbethPage);
            driver.Get(simpleTestPage);
            
            navigation.Back();
            Assert.AreEqual(driver.Title, macbethTitle);

            navigation.Forward();
            Assert.AreEqual(driver.Title, simpleTestTitle);
        }

        [Test]
        [IgnoreBrowser(Browser.IE, "Crashes driver")]
        public void ShouldAcceptInvalidUrlsUsingStrings()
        {
            INavigation navigation;
            navigation = driver.Navigate();

            navigation.To("isidsji30342όϊώ®ιεµρ©ζ");
            navigation.To((string)null);
            navigation.To("");
        }

        [Test]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ShouldAcceptInvalidUrlsUsingUris()
        {
            INavigation navigation;
            navigation = driver.Navigate();
            navigation.To((Uri)null);
            // new Uri("") and new Uri("isidsji30342όϊώ®ιεµρ©ζ") 
            // throw an exception, so we needn't worry about them.
        }

        [Test]
        public void ShouldGoToUrlUsingString()
        {
            INavigation navigation;
            navigation = driver.Navigate();

            navigation.To(macbethPage);
            Assert.AreEqual(driver.Title, macbethTitle);

            // We go to two pages to ensure that the browser wasn't
            // already at the desired page through a previous test.
            navigation.To(simpleTestPage);
            Assert.AreEqual(driver.Title, simpleTestTitle);
        }

        [Test]
        public void ShouldGoToUrlUsingUri()
        {
            Uri macBeth = new Uri(macbethPage);
            Uri simpleTest = new Uri(simpleTestPage);
            INavigation navigation;
            navigation = driver.Navigate();

            navigation.To(macBeth);
            Assert.AreEqual(driver.Title, macbethTitle);

            // We go to two pages to ensure that the browser wasn't
            // already at the desired page through a previous test.
            navigation.To(simpleTest);
            Assert.AreEqual(driver.Title, simpleTestTitle);
        }

        [Test]
        [IgnoreBrowser(Browser.ALL)]
        public void ShouldRefreshPage()
        {
        }

    }
}
