using System;
using System.Threading;
using NUnit.Framework;
using Selenium;
using ThoughtWorks.Selenium.Core;

namespace ThoughtWorks.Selenium.IntegrationTests
{
	[TestFixture]
	public class SeleniumIntegrationTest
	{
		private ISelenium selenium;

		[SetUp]
		public void SetupTest()
		{
			HttpCommandProcessor processor = new HttpCommandProcessor("http://localhost:8180/selenium/driver/");
			IBrowserLauncher launcher = new ManualPromptUserLauncher();
			selenium = new DefaultSelenium(processor, launcher);
			selenium.Start();
		}

		[TearDown]
		public void TeardownTest()
		{
			try
			{
				selenium.Stop();
			}
			catch (Exception)
			{
				// Ignore errors if unable to close the browser
			}
		}

		[Test]
		public void IISIntegrationTest()
		{
			selenium.Open("http://www.irian.at/myfaces-sandbox/inputSuggestAjax.jsf");
			selenium.VerifyTextPresent("suggest");
			String elementID = "_idJsp0:_idJsp3";
			selenium.Type(elementID, "foo");
			// DGF On Mozilla a keyPress is needed, and types a letter.
			// On IE6, a keyDown is needed, and no letter is typed. :-p
			// NS On firefox, keyPress needed, no letter typed.
        
			bool isIE = selenium.GetEvalBool("isIE");
			bool isFirefox = selenium.GetEvalBool("isFirefox");
			bool isNetscape = selenium.GetEvalBool("isNetscape");
			String verificationText = null;
			if (isIE) 
			{
				selenium.KeyDown(elementID, 120);
			} 
			else 
			{
				selenium.KeyPress(elementID, 120);
			}
			if (isNetscape) 
			{
				verificationText = "foox1";
			} 
			else if (isIE || isFirefox) 
			{
				verificationText = "foo1";
			}
			else 
			{
				throw new Exception("which browser is this?");
			}
			Thread.Sleep(2000);
			selenium.VerifyTextPresent(verificationText);
		}
	}
}