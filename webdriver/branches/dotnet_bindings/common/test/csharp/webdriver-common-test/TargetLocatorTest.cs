using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;

namespace OpenQa.Selenium
{
    [TestFixture]
    public class TargetLocatorTest : DriverTestFixture
    {

        [Test]
        //TODO(andre.nogueira): Currently crashes when getting CurrentURL
        public void ShouldContinueWorkingAfterSwitchingToInvalidFrame()
        {
            driver.Get(framesPage);
            driver.SwitchTo().Frame(10);
            Assert.AreEqual(framesPage, driver.CurrentUrl);
        }

        [Test]
        public void ShouldAcceptInvalidFrameIndexes()
        {
            driver.Get(framesPage);
            driver.SwitchTo().Frame(10);
            driver.SwitchTo().Frame(-1);
        }

        [Test]
        public void ShouldAcceptInvalidFrameNames()
        {
            driver.Get(framesPage);
            driver.SwitchTo().Frame("");
            driver.SwitchTo().Frame("æ©ñµøöíúüþ®éåä²³");
            driver.SwitchTo().Frame(null);
        }

        [Test]
        public void ShouldSwitchToDefaultContentInFramesPage()
        {
            driver.Get(framesPage);
            driver.SwitchTo().Frame(1);
            driver.SwitchTo().DefaultContent();
            Assert.AreEqual("Foo 1", driver.Title);
        }

        [Test]
        public void ShouldSwitchToDefaultContentInIframesPage()
        {
            driver.Get(iframesPage);
            driver.SwitchTo().Frame(1);
            driver.SwitchTo().DefaultContent();
            Assert.AreEqual(iframesPage, driver.CurrentUrl);
        }

        [Test]
        public void ShouldSwitchToIframeByName()
        {
            driver.Get(iframesPage);
            driver.SwitchTo().Frame("iframe1");
            Assert.AreEqual(formsTitle, driver.Title);
        }

        [Test]
        public void ShouldSwitchToIframeByIndex()
        {
            driver.Get(iframesPage);
            driver.SwitchTo().Frame(0);
            Assert.AreEqual(formsTitle, driver.Title);
        }

        [Test]
        public void ShouldSwitchToFrameByName() 
        {
            driver.Get(framesPage);
            driver.SwitchTo().Frame("first");
            Assert.AreEqual("Foo 1", driver.Title);

            driver.Get(framesPage);
            driver.SwitchTo().Frame("second");
            Assert.AreEqual("Foo 2", driver.Title);
        }

        [Test]
        public void ShouldSwitchToFrameByIndex()
        {
            driver.Get(framesPage);
            driver.SwitchTo().Frame(0);
            Assert.AreEqual("Foo 1", driver.Title);

            driver.Get(framesPage);
            driver.SwitchTo().Frame(1);
            Assert.AreEqual("Foo 2", driver.Title);
        }

    }
}
