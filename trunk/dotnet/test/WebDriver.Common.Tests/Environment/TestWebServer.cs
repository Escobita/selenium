﻿using System;
using System.IO;
using System.Net;
using System.Diagnostics;

namespace OpenQA.Selenium.Environment
{
    public class TestWebServer
    {
        private Process webserverProcess;

        private string standaloneTestJar = @"build\java\client\test\org\openqa\selenium\tests-standalone.jar";
        private string webserverClassName = "org.openqa.selenium.environment.webserver.Jetty7AppServer";
        private string projectRootPath;

        public TestWebServer(string projectRoot)
        {
            projectRootPath = projectRoot;
        }

        public void Start()
        {
            if (webserverProcess == null || webserverProcess.HasExited)
            {
                if (!File.Exists(standaloneTestJar))
                {
                    throw new FileNotFoundException(
                        string.Format(
                            "Standalone test jar at {0} didn't exist - please build it using something like {1}",
                            standaloneTestJar,
                            "go //java/client/test/org/openqa/selenium:tests:uber"));
                }

                webserverProcess = new Process();
                webserverProcess.StartInfo.FileName = "java.exe";
                webserverProcess.StartInfo.Arguments = "-cp " + standaloneTestJar + " " + webserverClassName;
                webserverProcess.StartInfo.WorkingDirectory = projectRootPath;
                webserverProcess.Start();
                DateTime timeout = DateTime.Now.Add(TimeSpan.FromSeconds(30));
                bool isRunning = false;
                while (!isRunning && DateTime.Now < timeout)
                {
                    // Poll until the webserver is correctly serving pages.
                    HttpWebRequest request = WebRequest.Create(EnvironmentManager.Instance.UrlBuilder.LocalWhereIs("simpleTest.html")) as HttpWebRequest;
                    try
                    {
                        HttpWebResponse response = request.GetResponse() as HttpWebResponse;
                        if (response.StatusCode == HttpStatusCode.OK)
                        {
                            isRunning = true;
                        }
                    }
                    catch (WebException)
                    {
                    }
                }

                if (!isRunning)
                {
                    throw new TimeoutException("Could not start the test web server in 15 seconds");
                }
            }
        }

        public void Stop()
        {
            HttpWebRequest request = WebRequest.Create(EnvironmentManager.Instance.UrlBuilder.LocalWhereIs("quitquitquit")) as HttpWebRequest;
            try
            {
                request.GetResponse();
            }
            catch (WebException)
            {
            }

            if (webserverProcess != null)
            {
                webserverProcess.WaitForExit(10000);
                if (!webserverProcess.HasExited)
                {
                    webserverProcess.Kill();
                }

                webserverProcess.Dispose();
                webserverProcess = null;
            }
        }
    }
}
