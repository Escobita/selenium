﻿using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using System.Text.RegularExpressions;

namespace Selenium.Tests
{
    [TestFixture]
    public class TestComments : SeleniumTestCaseBase
    {
        [Test]
        public void Comments()
        {
            selenium.Open("../tests/html/test_verifications.html?foo=bar");
            Assert.IsTrue(Regex.IsMatch(selenium.GetLocation(), "^[\\s\\S]*/tests/html/test_verifications\\.html[\\s\\S]*$"));
            Assert.AreEqual(selenium.GetValue("theText"), "the text value");
            Assert.AreEqual(selenium.GetValue("theHidden"), "the hidden value");
            Assert.AreEqual(selenium.GetText("theSpan"), "this is the span");
        }
    }
}
