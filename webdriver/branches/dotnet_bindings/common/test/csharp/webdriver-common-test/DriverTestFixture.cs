using NUnit.Core;
using NUnit.Framework;

namespace OpenQa.Selenium
{
    
    public abstract class DriverTestFixture
    {

        /* TODO(andre.nogueira): Add AppServer class so the server name, port name, etc aren't
         * hardcoded */
        public string macbethPage = "http://localhost:2310/web/macbeth.html";
        public string macbethTitle = "Macbeth: Entire Play";

        public string simpleTestPage = "http://localhost:2310/web/simpleTest.html";
        public string simpleTestTitle = "Hello WebDriver";

        public string framesPage = "http://localhost:2310/web/win32frameset.html";

        public string iframesPage = "http://localhost:2310/web/iframes.html";

        public string formsTitle = "We Leave From Here";
        public string formsPage = "http://localhost:2310/web/formPage.html";

        protected IWebDriver driver;

        [TestFixtureSetUp]
        public void SetUp()
        {
            GetFreshDriver();
        }

        protected void GetFreshDriver()
        {
            driver = Environment.Instance.GetDriver();
        }

    }
}
