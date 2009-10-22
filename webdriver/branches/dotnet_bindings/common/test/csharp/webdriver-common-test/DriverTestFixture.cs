using NUnit.Framework;
using OpenQa.Selenium.Environment;

namespace OpenQa.Selenium
{
    
    public abstract class DriverTestFixture
    {

        public string macbethPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("macbeth.html");
        public string macbethTitle = "Macbeth: Entire Play";

        public string simpleTestPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("simpleTest.html");
        public string simpleTestTitle = "Hello WebDriver";

        public string framesPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("win32frameset.html");
        public string framesTitle = "This page has frames";

        public string iframesPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("iframes.html");
        public string iframesTitle = "This page has iframes";

        public string formsPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("formPage.html");
        public string formsTitle = "We Leave From Here";

        public string javascriptPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("javascriptPage.html");

        public string resultPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("resultPage.html");

        public string nestedElementsPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("nestedElements.html");

        public string xhtmlTestPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("xhtmlTest.html");

        public string richTextPage = EnvironmentManager.Instance.UrlBuilder.WhereIs("rich_text.html");

        protected IWebDriver driver;

        [TestFixtureSetUp]
        public void SetUp()
        {
            driver = EnvironmentManager.Instance.GetCurrentDriver();
        }

        [TestFixtureTearDown]
        public void TearDown()
        {
            driver = EnvironmentManager.Instance.CreateFreshDriver();
        }
        
        /*
         *  Exists because a given test might require a fresh driver
         */
        protected void CreateFreshDriver()
        {
            driver = EnvironmentManager.Instance.CreateFreshDriver();
        }

    }
}
