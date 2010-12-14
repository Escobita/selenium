﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using System.Collections.ObjectModel;

namespace OpenQA.Selenium
{
    [TestFixture]
    public class ExecutingAsyncJavascriptTest : DriverTestFixture
    {
        private IJavaScriptExecutor executor;
        [SetUp]
        public void SetUpEnv()
        {
            if (driver is IJavaScriptExecutor)
            {
                executor = (IJavaScriptExecutor)driver;
            }
            driver.Manage().Timeouts().SetScriptTimeout(TimeSpan.FromMilliseconds(0));
        }

        [Test]
        public void shouldNotTimeoutIfCallbackInvokedImmediately()
        {
            driver.Url = ajaxyPage;
            object result = executor.ExecuteAsyncScript("arguments[arguments.length - 1](123);");
            Assert.IsInstanceOfType(typeof(long), result);
            Assert.AreEqual(123, (long)result);
        }

        [Test]
        public void shouldBeAbleToReturnJavascriptPrimitivesFromAsyncScripts()
        {
            driver.Url = ajaxyPage;
            Assert.IsNull(executor.ExecuteAsyncScript("arguments[arguments.length - 1](null);"));
            Assert.IsNull(executor.ExecuteAsyncScript("arguments[arguments.length - 1]();"));
            Assert.AreEqual(123, (long)executor.ExecuteAsyncScript("arguments[arguments.length - 1](123);"));
            Assert.AreEqual("abc", executor.ExecuteAsyncScript("arguments[arguments.length - 1]('abc');").ToString());
            Assert.IsFalse((bool)executor.ExecuteAsyncScript("arguments[arguments.length - 1](false);"));
            Assert.IsTrue((bool)executor.ExecuteAsyncScript("arguments[arguments.length - 1](true);"));
        }

        [Test]
        public void shouldBeAbleToReturnAnArrayLiteralFromAnAsyncScript()
        {
            driver.Url = ajaxyPage;

            object result = executor.ExecuteAsyncScript("arguments[arguments.length - 1]([]);");
            Assert.IsNotNull(result);
            Assert.IsInstanceOfType(typeof(ReadOnlyCollection<object>), result);
            Assert.AreEqual(0, ((ReadOnlyCollection<object>)result).Count);
        }

        [Test]
        public void shouldBeAbleToReturnAnArrayObjectFromAnAsyncScript()
        {
            driver.Url = ajaxyPage;

            object result = executor.ExecuteAsyncScript("arguments[arguments.length - 1](new Array());");
            Assert.IsNotNull(result);
            Assert.IsInstanceOfType(typeof(ReadOnlyCollection<object>), result);
            Assert.AreEqual(0, ((ReadOnlyCollection<object>)result).Count);
        }

        [Test]
        public void shouldBeAbleToReturnArraysOfPrimitivesFromAsyncScripts()
        {
            driver.Url = ajaxyPage;

            Object result = executor.ExecuteAsyncScript("arguments[arguments.length - 1]([null, 123, 'abc', true, false]);");

            Assert.IsNotNull(result);
            Assert.IsInstanceOfType(typeof(ReadOnlyCollection<object>), result);
            ReadOnlyCollection<object> resultList = result as ReadOnlyCollection<object>;
            Assert.AreEqual(5, resultList.Count);
            Assert.IsNull(resultList[0]);
            Assert.AreEqual(123, (long)resultList[1]);
            Assert.AreEqual("abc", resultList[2].ToString());
            Assert.IsTrue((bool)resultList[3]);
            Assert.IsFalse((bool)resultList[4]);
        }

        [Test]
        public void shouldBeAbleToReturnWebElementsFromAsyncScripts()
        {
            driver.Url = ajaxyPage;

            object result = executor.ExecuteAsyncScript("arguments[arguments.length - 1](document.body);");
            Assert.IsInstanceOfType(typeof(IWebElement), result);
            Assert.AreEqual("body", ((IWebElement)result).TagName.ToLower());
        }

        [Test]
        public void shouldBeAbleToReturnArraysOfWebElementsFromAsyncScripts()
        {
            driver.Url = ajaxyPage;

            object result = executor.ExecuteAsyncScript("arguments[arguments.length - 1]([document.body, document.body]);");
            Assert.IsNotNull(result);
            Assert.IsInstanceOfType(typeof(ReadOnlyCollection<IWebElement>), result);
            ReadOnlyCollection<IWebElement> resultsList = (ReadOnlyCollection<IWebElement>)result;
            Assert.AreEqual(2, resultsList.Count);
            Assert.IsInstanceOfType(typeof(IWebElement), resultsList[0]);
            Assert.IsInstanceOfType(typeof(IWebElement), resultsList[1]);
            Assert.AreEqual("body", ((IWebElement)resultsList[0]).TagName.ToLower());
            Assert.AreEqual(((IWebElement)resultsList[0]), ((IWebElement)resultsList[1]));
        }

        [Test]
        [ExpectedException(typeof(TimeoutException))]
        public void shouldTimeoutIfScriptDoesNotInvokeCallback()
        {
            driver.Url = ajaxyPage;
            executor.ExecuteAsyncScript("return 1 + 2;");
        }

        [Test]
        [ExpectedException(typeof(TimeoutException))]
        public void shouldTimeoutIfScriptDoesNotInvokeCallbackWithAZeroTimeout()
        {
            driver.Url = ajaxyPage;
            executor.ExecuteAsyncScript("window.setTimeout(function() {}, 0);");
        }

        [Test]
        public void shouldNotTimeoutIfScriptCallsbackInsideAZeroTimeout()
        {
            driver.Url = ajaxyPage;
            executor.ExecuteAsyncScript(
                "var callback = arguments[arguments.length - 1];" +
                "window.setTimeout(function() { callback(123); }, 0)");
        }

        [Test]
        [ExpectedException(typeof(TimeoutException))]
        public void shouldTimeoutIfScriptDoesNotInvokeCallbackWithLongTimeout()
        {
            driver.Manage().Timeouts().SetScriptTimeout(TimeSpan.FromMilliseconds(500));
            driver.Url = ajaxyPage;
            executor.ExecuteAsyncScript(
                "var callback = arguments[arguments.length - 1];" +
                "window.setTimeout(callback, 1500);");
        }

        [Test]
        [ExpectedException(typeof(WebDriverException))]
        public void shouldDetectPageLoadsWhileWaitingOnAnAsyncScriptAndReturnAnError()
        {
            driver.Url = ajaxyPage;
            driver.Manage().Timeouts().SetScriptTimeout(TimeSpan.FromMilliseconds(100));
            executor.ExecuteAsyncScript("window.location = '" + dynamicPage + "';");
        }

        [Test]
        [ExpectedException(typeof(WebDriverException))]
        public void shouldCatchErrorsWhenExecutingInitialScript()
        {
            driver.Url = ajaxyPage;
            executor.ExecuteAsyncScript("throw Error('you should catch this!');");
        }

        [Test]
        public void shouldBeAbleToExecuteAsynchronousScripts()
        {
            driver.Url = ajaxyPage;

            IWebElement typer = driver.FindElement(By.Name("typer"));
            typer.SendKeys("bob");
            Assert.AreEqual("bob", typer.Value);

            driver.FindElement(By.Id("red")).Click();
            driver.FindElement(By.Name("submit")).Click();

            Assert.AreEqual(1, getNumDivElements(), "There should only be 1 DIV at this point, which is used for the butter message");

            driver.Manage().Timeouts().SetScriptTimeout(TimeSpan.FromSeconds(10));
            string text = (string)executor.ExecuteAsyncScript(
                "var callback = arguments[arguments.length - 1];"
                + "window.registerListener(arguments[arguments.length - 1]);");
            Assert.AreEqual("bob", text);
            Assert.AreEqual("", typer.Value);

            Assert.AreEqual(2, getNumDivElements(), "There should be 1 DIV (for the butter message) + 1 DIV (for the new label)");
        }

        [Test]
        public void shouldBeAbleToPassMultipleArgumentsToAsyncScripts()
        {
            driver.Url = ajaxyPage;
            long result = (long)executor.ExecuteAsyncScript("arguments[arguments.length - 1](arguments[0] + arguments[1]);", 1, 2);
            Assert.AreEqual(3, result);
        }

        //[Test]
        public void shouldBeAbleToMakeXMLHttpRequestsAndWaitForTheResponse()
        {
            string script =
                "var url = arguments[0];" +
                "var callback = arguments[arguments.length - 1];" +
                // Adapted from http://www.quirksmode.org/js/xmlhttp.html
                "var XMLHttpFactories = [" +
                "  function () {return new XMLHttpRequest()}," +
                "  function () {return new ActiveXObject('Msxml2.XMLHTTP')}," +
                "  function () {return new ActiveXObject('Msxml3.XMLHTTP')}," +
                "  function () {return new ActiveXObject('Microsoft.XMLHTTP')}" +
                "];" +
                "var xhr = false;" +
                "while (!xhr && XMLHttpFactories.length) {" +
                "  try {" +
                "    xhr = XMLHttpFactories.shift().call();" +
                "  } catch (e) {}" +
                "}" +
                "if (!xhr) throw Error('unable to create XHR object');" +
                "xhr.open('GET', url, true);" +
                "xhr.onreadystatechange = function() {" +
                "  if (xhr.readyState == 4) callback(xhr.responseText);" +
                "};" +
                "xhr.send();";

            driver.Url = ajaxyPage;
            driver.Manage().Timeouts().SetScriptTimeout(TimeSpan.FromSeconds(3));
            //string response = (string)executor.ExecuteAsyncScript(script, sleepingPage + "?time=2");
            //Assert.AreEqual("<html><head><title>Done</title></head><body>Slept for 2s</body></html>", response.Trim());
        }

        private long getNumDivElements()
        {
            IJavaScriptExecutor jsExecutor = driver as IJavaScriptExecutor;
            // Selenium does not support "findElements" yet, so we have to do this through a script.
            object d = jsExecutor.ExecuteScript("return document.getElementsByTagName('div').length;");
            return (long)jsExecutor.ExecuteScript("return document.getElementsByTagName('div').length;");
        }
    }
}
