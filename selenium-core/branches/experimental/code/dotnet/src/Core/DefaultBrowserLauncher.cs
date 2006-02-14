using System.Diagnostics;
using Microsoft.Win32;
using Selenium;
using System;

namespace ThoughtWorks.Selenium.Core
{
	public class DefaultBrowserLauncher : IBrowserLauncher
	{
		protected Process browserProcess;

		public virtual void Launch(string url)
		{
			string arguments;
			string browser;
			GetBrowserPathAndArguments(url, out browser, out arguments);

			ProcessStartInfo info = new ProcessStartInfo();

			info.FileName = browser;
			info.Arguments = arguments;
			info.Verb = "open";
			info.UseShellExecute = false;
			info.WindowStyle = ProcessWindowStyle.Maximized;
			browserProcess = Process.Start(info);

		}

		private static void GetBrowserPathAndArguments(string url, out string browser, out string arguments)
		{
			RegistryKey defaultBrowserKey = Registry.ClassesRoot.OpenSubKey(@"http\shell\open\command");

			string browserPath = (string) defaultBrowserKey.GetValue("");
			if (browserPath.IndexOf("%1") != -1)
			{
				browserPath = browserPath.Replace("%1", url);	
			}
			else
			{
				browserPath = browserPath + " " + url;
			}

            string quotedExeSuffix = "exe\"";
            int browserExeEnd = browserPath.IndexOf(quotedExeSuffix + " ");
            
            if (browserExeEnd==-1)
            {
                browserExeEnd = browserPath.IndexOf(' ');
            }
            else
            {
                browserExeEnd += quotedExeSuffix.Length;
            }
            
            browser = browserPath.Substring(0, browserExeEnd);
			arguments = browserPath.Substring(browserExeEnd + 1);
        }

		public virtual void Close()
		{
			browserProcess.Kill();
			browserProcess.WaitForExit();
		}

		public int ProcessID
		{
			get { return browserProcess.Id; }
		}

	}
}
