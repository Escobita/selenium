using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;

namespace OpenQa.Selenium
{
    [TestFixture]
    public class ExecutingJavascriptTest : DriverTestFixture
    {

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToExecuteSimpleJavascriptAndReturnAString()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = xhtmlTestPage;

            Object result = ExecuteScript("return document.title;");

            Assert.IsTrue(result is String);
            Assert.AreEqual("XHTML Test Page", result);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToExecuteSimpleJavascriptAndReturnALong()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = xhtmlTestPage;

            Object result = ExecuteScript("return document.title.length;");

            Assert.IsTrue(result is long, result.GetType().Name);
            Assert.AreEqual((long)"XHTML Test Page".Length, (long)result);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToExecuteSimpleJavascriptAndReturnAWebElement()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = xhtmlTestPage;

            Object result = ExecuteScript("return document.getElementById('id1');");

            Assert.IsNotNull(result);
            Assert.IsTrue(result is IWebElement);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToExecuteSimpleJavascriptAndReturnABoolean()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = xhtmlTestPage;

            Object result = ExecuteScript("return true;");

            Assert.IsNotNull(result);
            Assert.IsTrue(result is Boolean);
            Assert.IsTrue((Boolean)result);
        }


        [Category("Javascript")]
        [ExpectedException(typeof(System.Exception))]
        [Test]
        public void ShouldThrowAnExceptionWhenTheJavascriptIsBad()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = xhtmlTestPage;
            ExecuteScript("return squiggle();");
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToCallFunctionsDefinedOnThePage()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;
            ExecuteScript("displayMessage('I like cheese');");
            String text = driver.FindElement(By.Id("result")).Text;

            Assert.AreEqual("I like cheese", text.Trim());
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassAStringAsAnArgument()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript("displayMessage(arguments[0]);", "Hello!");
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("Hello!", text);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassMoreThanOneStringAsArguments()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;
            ExecuteScript("displayMessage(arguments[0] + arguments[1] + arguments[2] + arguments[3]);", "Hello,", " ", "world", "!");

            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("Hello, world!", text);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassABooleanAsAnArgument()
        {

            String function = "displayMessage(arguments[0] ? 'True' : 'False');";

            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript(function, true);
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("True", text);

            ExecuteScript(function, false);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("False", text);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassMoreThanOneBooleanAsArguments()
        {

            String function = "displayMessage((arguments[0] ? 'True' : 'False') + (arguments[1] ? 'True' : 'False'));";

            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript(function, true, true);
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("TrueTrue", text);

            ExecuteScript(function, false, true);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("FalseTrue", text);

            ExecuteScript(function, true, false);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("TrueFalse", text);

            ExecuteScript(function, false, false);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("FalseFalse", text);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassANumberAsAnArgument()
        {
            String function = "displayMessage(arguments[0]);";

            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript(function, 3);
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("3", text);

            ExecuteScript(function, -3);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-3", text);

            ExecuteScript(function, 2147483647);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("2147483647", text);

            ExecuteScript(function, -2147483647);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-2147483647", text);

        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassMoreThanOneNumberAsArguments()
        {
            String function = "displayMessage(arguments[0]+arguments[1]);";

            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript(function, 30, 12);
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("42", text);

            ExecuteScript(function, -30, -12);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-42", text);

            ExecuteScript(function, 2147483646, 1);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("2147483647", text);

            ExecuteScript(function, - 2147483646, -1);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-2147483647", text);

        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassADoubleAsAnArgument()
        {
            String function = "displayMessage(arguments[0]);";

            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript(function, (double)4.2);
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("4.2", text);

            ExecuteScript(function, (double)-4.2);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-4.2", text);

            ExecuteScript(function, (float)4.2);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("4.2", text);

            ExecuteScript(function, (float)-4.2);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-4.2", text);

            ExecuteScript(function, (double)4.0);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("4", text);

            ExecuteScript(function, (double)-4.0);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-4", text);

        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassMoreThanOneDoubleAsArguments()
        {
            String function = "displayMessage(arguments[0]+arguments[1]);";

            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;

            ExecuteScript(function, 30, 12);
            String text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("42", text);

            ExecuteScript(function, -30, -12);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-42", text);

            ExecuteScript(function, 2147483646, 1);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("2147483647", text);

            ExecuteScript(function, -2147483646, -1);
            text = driver.FindElement(By.Id("result")).Text;
            Assert.AreEqual("-2147483647", text);

        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassAWebElementAsArgument()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;
            IWebElement button = driver.FindElement(By.Id("plainButton"));
            String value = (String)ExecuteScript("arguments[0]['flibble'] = arguments[0].getAttribute('id'); return arguments[0]['flibble'];", button);

            Assert.AreEqual("plainButton", value);
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassMoreThanOneWebElementAsArguments()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;
            IWebElement button = driver.FindElement(By.Id("plainButton"));
            IWebElement dynamo = driver.FindElement(By.Id("dynamo"));
            String value = (String)ExecuteScript("arguments[0]['flibble'] = arguments[0].getAttribute('id'); return arguments[0]['flibble'] + arguments[1].innerHTML;", button, dynamo);

            Assert.AreEqual("plainButtonWhat's for dinner?", value);
        }

        [Category("Javascript")]
        [ExpectedException(typeof(ArgumentException))]
        [Test]
        public void ShouldThrowAnExceptionIfAnArgumentIsNotValid()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;
            ExecuteScript("return arguments[0];", new List<IWebElement>());
        }

        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToPassInMixedArguments()
        {
            if (!(driver is IJavascriptExecutor))
                return;

            driver.Url = javascriptPage;
            
            IWebElement dynamo = driver.FindElement(By.Id("dynamo"));
            string result = (string)ExecuteScript("return arguments[0].innerHTML + arguments[1].toString() + arguments[2].toString() + arguments[3] + arguments[4]",
                dynamo,
                42,
                4.2,
                "Hello, World!",
                true);

            Assert.AreEqual("What's for dinner?424.2Hello, World!true", result);

        }

        [IgnoreBrowser(Browser.CHROME, "Frames not implemented")]
        [Category("Javascript")]
        [Test]
        public void ShouldBeAbleToGrabTheBodyOfFrameOnceSwitchedTo()
        {
            driver.Url = richTextPage;

            driver.SwitchTo().Frame("editFrame");
            IWebElement body = (IWebElement)((IJavascriptExecutor)driver).ExecuteScript("return document.body");

            Assert.AreEqual("", body.Text);
        }

        private Object ExecuteScript(String script, params Object[] args)
        {
            return ((IJavascriptExecutor)driver).ExecuteScript(script, args);
        }

    }
}
