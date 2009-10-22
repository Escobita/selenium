using System;
using System.Collections.Generic;
using System.Text;
using OpenQa.Selenium.Internal;

namespace OpenQa.Selenium
{
    public class By
    {

        delegate IWebElement findElementDelegate(ISearchContext context);
        delegate List<IWebElement> findElementsDelegate(ISearchContext context);

        findElementDelegate findElement;
        findElementsDelegate findElements;

        public static By Id(string id) {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsById)context).FindElementById(id);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsById)context).FindElementsById(id);
            };
            return by;
        }

        public static By LinkText(string linkText) {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsByLinkText)context).FindElementByLinkText(linkText);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsByLinkText)context).FindElementsByLinkText(linkText);
            };
            return by;
        }

        public static By Name(string name)
        {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsByName)context).FindElementByName(name);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsByName)context).FindElementsByName(name);
            };
            return by;
        }

        public static By XPath(string xpath)
        {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsByXPath)context).FindElementByXPath(xpath);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsByXPath)context).FindElementsByXPath(xpath);
            };
            return by;
        }

        public static By ClassName(string className)
        {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsByClassName)context).FindElementByClassName(className);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsByClassName)context).FindElementsByClassName(className);
            };
            return by;
        }

        public static By PartialLinkText(string partialLinkText)
        {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsByPartialLinkText)context).FindElementByPartialLinkText(partialLinkText);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsByPartialLinkText)context).FindElementsByPartialLinkText(partialLinkText);
            };
            return by;
        }

        public static By TagName(string tagName)
        {
            By by = new By();
            by.findElement = delegate(ISearchContext context)
            {
                return ((IFindsByTagName)context).FindElementByTagName(tagName);
            };
            by.findElements = delegate(ISearchContext context)
            {
                return ((IFindsByTagName)context).FindElementsByTagName(tagName);
            };
            return by;
        }

        //TODO(andre.nogueira) should accept Context - accepting IFindsById only for quick prototyping
        public IWebElement FindElement(ISearchContext context)
        {
            return findElement(context);
        }

        public List<IWebElement> FindElements(ISearchContext context)
        {
            return findElements(context);
        }

    }
}
