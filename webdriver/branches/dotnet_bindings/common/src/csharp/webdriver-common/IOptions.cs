using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    public interface IOptions
    {
        void AddCookie(Cookie cookie);
        Dictionary<String, Cookie> GetCookies();
        void DeleteCookie(Cookie cookie);
        void DeleteCookieNamed(String name);
    }
}
