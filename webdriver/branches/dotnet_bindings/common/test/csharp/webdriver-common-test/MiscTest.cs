using NUnit.Framework;

namespace OpenQa.Selenium
{
    [TestFixture]
    [Category("Driver")]
    public class MiscTest : DriverTestFixture
    {

        [Test]
        public void ShouldNotBlowUpExecutingNonExistingScript()
        {
            driver.Get(macbethPage);
            driver.ExecuteScript("doesNotExist();");
        }

        [Test]
        public void ShouldExecuteScript()
        {
            driver.Get(formsPage);
            driver.ExecuteScript("changePageDotNetServer();");
            //TODO(andre.nogueira): This should be removed after ExecuteScript is finished.
            //Currently it does not get the return value, so it does not wait for the script to finish.
            System.Threading.Thread.Sleep(1000);
            Assert.AreEqual("Foo 3", driver.Title);
        }

        [Test]
        public void ShouldReturnPageSource()
        {
            string pageSource;
            driver.Get(macbethPage);
            pageSource = driver.PageSource;
            Assert.That(pageSource.StartsWith("<HTML><HEAD><TITLE>Macbeth: Entire Play</TITLE>"));
            Assert.That(pageSource.Contains("I have lost my hopes."));
            Assert.That(pageSource.EndsWith("</HTML>"));
        }

        [Test]
        public void ShouldReturnTitle()
        {
            driver.Get(macbethPage);
            Assert.AreEqual(driver.Title, macbethTitle);
        }

        [Test]
        public void ShouldReturnCurrentUrl()
        {
            driver.Get(macbethPage);
            Assert.AreEqual(driver.CurrentUrl, macbethPage);
        }

        //TODO: Move these to the IE-only tests.
        [Test]
        [IgnoreBrowser(Browser.ALL)]
        public void ShouldMakeWindowVisible()
        {
            //driver.Visible = true;
            //Assert.IsTrue(driver.Visible);
        }

        [Test]
        [IgnoreBrowser(Browser.ALL)]
        public void ShouldMakeWindowNotVisible()
        {
            //driver.Visible = false;
            //Assert.IsFalse(driver.Visible);
            //driver.Visible = true;
        }

    }
}
