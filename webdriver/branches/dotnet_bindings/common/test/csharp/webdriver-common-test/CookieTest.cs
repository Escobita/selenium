using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    [TestFixture]
    //TODO(andre.nogueira): refactor so we don't contact the driver for each check. (place Manage() and GetCookies() in a var)s
    public class CookieTest : DriverTestFixture
    {
        [Test]
        public void ShouldAddCookie()
        {
            driver.Get(macbethPage);
            Cookie cookie = new Cookie("cookie", "monster");
            driver.Manage().AddCookie(cookie);
            Assert.That(driver.Manage().GetCookies().ContainsKey(cookie.Name),
                "Cookie was not added successfully");
        }

        [Test]
        public void ShouldDeleteCookie()
        {
            driver.Get(macbethPage);
            Cookie cookieToDelete = new Cookie("chocolate", "rain");
            Cookie cookieToKeep = new Cookie("canIHaz", "Cheeseburguer");
            driver.Manage().AddCookie(cookieToDelete);
            driver.Manage().AddCookie(cookieToKeep);
            Dictionary<String, Cookie> cookies = driver.Manage().GetCookies();
            driver.Manage().DeleteCookie(cookieToDelete);
            cookies = driver.Manage().GetCookies();
            Assert.IsFalse(cookies.ContainsKey(cookieToDelete.Name),
                "Cookie was not deleted successfully");
            Assert.IsTrue(cookies.ContainsKey(cookieToKeep.Name),
                "Valid cookie was not returned");
        }

        [Test]
        // TODO(andre.nogueira): Work not completed, still not working
        public void ShouldDeleteCookieNamed()
        {
            driver.Get(macbethPage);
            Cookie cookieToDelete = new Cookie("answer", "42");
            Cookie cookieToKeep = new Cookie("question", "dunno");
            driver.Manage().AddCookie(cookieToDelete);
            driver.Manage().AddCookie(cookieToKeep);
            driver.Manage().DeleteCookieNamed(cookieToDelete.Name);
            Dictionary<String, Cookie> cookies = driver.Manage().GetCookies();
            Assert.IsTrue(cookies.ContainsKey(cookieToKeep.Name),
                "Valid cookie was not returned");
            Cookie deletedCookie;
            cookies.TryGetValue(cookieToDelete.Name, out deletedCookie);
            Assert.IsFalse(cookies.ContainsKey(cookieToDelete.Name),
                "Cookie was not deleted successfully: " + deletedCookie.ToString());
        }
    }
}
