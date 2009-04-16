using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    // TODO(andre.nogueira): Can System.Net.Cookie be used?
    // TODO(andre.nogueira): validate, toString, equals, hashcode..
    // TODO(andre.nogueira): path=/ in Java driver by default
    public class Cookie
    {
        private String name = "";
        private String value = "";
        private String path = "";
        private String domain = "";

        private DateTime expiry = DateTime.MinValue;

        public Cookie(String name, String value, String path, String domain, DateTime expiry)
            : this(name, value, domain, path)
        {
            this.expiry = expiry;
        }

        public Cookie(String name, String value, String domain, String path)
        {
            this.name = name;
            this.value = value;
            this.path = path;
            this.domain = domain;
        }

        public Cookie(String name, String value) : this(name, value, "", "")
        {
            
        }

        public override String ToString()
        {
            return name + "=" + value
                + (expiry.Equals(DateTime.MinValue) ? "" : "; expires=" + expiry.ToLongDateString())
                    + ("".Equals(path) ? "" : "; path=" + path)
                    + ("".Equals(domain) ? "" : "; domain=" + domain);
            //                + (isSecure ? ";secure;" : "");
        }

        public String Name
        {
            get { return name; }
        }

        public String Value
        {
            get { return value; }
        }

        public String Domain
        {
            get { return domain; }
        }
        
        public String Path
        {
            get { return path; }
        }
    }
}
