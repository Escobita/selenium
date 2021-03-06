﻿using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using OpenQA.Selenium.Internal;

namespace OpenQA.Selenium.Remote
{
    /// <summary>
    /// Defines an interface allowing the user to manipulate cookies on the current page.
    /// </summary>
    internal class RemoteCookieJar : ICookieJar
    {
        private RemoteWebDriver driver;

        /// <summary>
        /// Initializes a new instance of the <see cref="RemoteCookieJar"/> class.
        /// </summary>
        /// <param name="driver">The driver that is currently in use</param>
        public RemoteCookieJar(RemoteWebDriver driver)
        {
            this.driver = driver;
        }

        /// <summary>
        /// Gets all cookies defined for the current page.
        /// </summary>
        public ReadOnlyCollection<Cookie> AllCookies
        {
            get { return this.GetAllCookies(); }
        }

        /// <summary>
        /// Method for creating a cookie in the browser
        /// </summary>
        /// <param name="cookie"><see cref="Cookie"/> that represents a cookie in the browser</param>
        public void AddCookie(Cookie cookie)
        {
            Dictionary<string, object> parameters = new Dictionary<string, object>();
            parameters.Add("cookie", cookie);
            this.driver.InternalExecute(DriverCommand.AddCookie, parameters);
        }

        /// <summary>
        /// Delete the cookie by passing in the name of the cookie
        /// </summary>
        /// <param name="name">The name of the cookie that is in the browser</param>
        public void DeleteCookieNamed(string name)
        {
            Dictionary<string, object> parameters = new Dictionary<string, object>();
            parameters.Add("name", name);
            this.driver.InternalExecute(DriverCommand.DeleteCookie, parameters);
        }

        /// <summary>
        /// Delete a cookie in the browser by passing in a copy of a cookie
        /// </summary>
        /// <param name="cookie">An object that represents a copy of the cookie that needs to be deleted</param>
        public void DeleteCookie(Cookie cookie)
        {
            this.DeleteCookieNamed(cookie.Name);
        }

        /// <summary>
        /// Delete All Cookies that are present in the browser
        /// </summary>
        public void DeleteAllCookies()
        {
            this.driver.InternalExecute(DriverCommand.DeleteAllCookies, null);
        }

        /// <summary>
        /// Method for returning a getting a cookie by name
        /// </summary>
        /// <param name="name">name of the cookie that needs to be returned</param>
        /// <returns>A Cookie from the name</returns>
        public Cookie GetCookieNamed(string name)
        {
            Cookie cookieToReturn = null;
            ReadOnlyCollection<Cookie> allCookies = this.AllCookies;
            foreach (Cookie currentCookie in allCookies)
            {
                if (name.Equals(currentCookie.Name))
                {
                    cookieToReturn = currentCookie;
                    break;
                }
            }

            return cookieToReturn;
        }

        /// <summary>
        /// Method for getting a Collection of Cookies that are present in the browser
        /// </summary>
        /// <returns>ReadOnlyCollection of Cookies in the browser</returns>
        private ReadOnlyCollection<Cookie> GetAllCookies()
        {
            List<Cookie> toReturn = new List<Cookie>();
            object returned = this.driver.InternalExecute(DriverCommand.GetAllCookies, null).Value;

            try
            {
                object[] cookies = returned as object[];
                if (cookies != null)
                {
                    foreach (object rawCookie in cookies)
                    {
                        Dictionary<string, object> cookie = rawCookie as Dictionary<string, object>;
                        if (rawCookie != null)
                        {
                            string name = cookie["name"].ToString();
                            string value = cookie["value"].ToString();

                            string path = "/";
                            if (cookie.ContainsKey("path") && cookie["path"] != null)
                            {
                                path = cookie["path"].ToString();
                            }

                            string domain = string.Empty;
                            if (cookie.ContainsKey("domain") && cookie["domain"] != null)
                            {
                                domain = cookie["domain"].ToString();
                            }

                            DateTime? expires = null;
                            if (cookie.ContainsKey("expiry") && cookie["expiry"] != null)
                            {
                                long seconds = 0;
                                if (long.TryParse(cookie["expiry"].ToString(), out seconds))
                                {
                                    expires = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc).AddSeconds(seconds).ToLocalTime();
                                }
                            }

                            bool secure = bool.Parse(cookie["secure"].ToString());
                            toReturn.Add(new ReturnedCookie(name, value, domain, path, expires, secure, new Uri(this.driver.Url)));
                        }
                    }
                }

                return new ReadOnlyCollection<Cookie>(toReturn);
            }
            catch (Exception e)
            {
                throw new WebDriverException("Unexpected problem getting cookies", e);
            }
        }
    }
}
