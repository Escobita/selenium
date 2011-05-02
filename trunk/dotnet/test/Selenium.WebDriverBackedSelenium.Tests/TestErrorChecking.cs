﻿using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using System.Threading;

namespace Selenium.Tests
{
    [TestFixture]
    public class TestErrorChecking : SeleniumTestCaseBase
    {
        [Test]
        public void ShouldAllowErrorChecking()
        {
		selenium.Open("../tests/html/test_click_page1.html");
		//  These tests should all fail, as they are checking the error checking commands. 
		try { Assert.AreEqual(selenium.GetText("link"), "Click here for next page"); Assert.Fail("expected failure"); } catch (Exception e) {}
		try { Console.WriteLine("foo"); Assert.Fail("expected failure"); } catch (Exception e) {}
		try { Assert.AreEqual(selenium.GetText("link"), "foo"); Assert.Fail("expected failure"); } catch (Exception e) {}
		try { Assert.AreEqual(selenium.GetText("link"), "Click here for next page"); Assert.Fail("expected failure"); } catch (Exception e) {}
		try { Assert.AreEqual(selenium.GetText("link"), "foo"); Assert.Fail("expected failure"); } catch (Exception e) {}
		try { Assert.AreEqual(selenium.GetText("notAlink"), "foo"); Assert.Fail("expected failure"); } catch (Exception e) {}
        }
    }
}
