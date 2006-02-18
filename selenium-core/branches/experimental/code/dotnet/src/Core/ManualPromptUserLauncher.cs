using System;
using Selenium;

namespace ThoughtWorks.Selenium.Core
{
	/// <summary>
	/// Manually prompt the user to start a browser from the command line
	/// </summary>
	public class ManualPromptUserLauncher : IBrowserLauncher
	{
		public ManualPromptUserLauncher()
		{
		}

		public virtual void Launch(string url)
		{
			System.Console.WriteLine("Hello!  This test run is now waiting for you to manually bring up a browser for testing:\nFrom this browser, request:\n" + url);
		}

		public virtual void Close()
		{
		}
	}

	
}
