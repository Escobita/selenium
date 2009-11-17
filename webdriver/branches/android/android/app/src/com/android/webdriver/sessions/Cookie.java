package com.android.webdriver.sessions;

public class Cookie {
   public Cookie(String domain, String name, String value) {
     mDomain = domain;
     mName = name;
     mValue = value;
   }
   
   public String getDomain() {
     return mDomain;
   }
   
   public String getName() {
     return mName;
   }
   
   public String getValue() {
     return mValue;
   }
   
   String mDomain, mName, mValue;
}
