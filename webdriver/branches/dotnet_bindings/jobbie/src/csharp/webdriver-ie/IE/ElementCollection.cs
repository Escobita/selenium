using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;

namespace OpenQa.Selenium.IE
{
    class ElementCollection
    {
        SafeInternetExplorerDriverHandle driverHandle;
        InternetExplorerDriver driver;
        IntPtr elements;

        public ElementCollection(InternetExplorerDriver driver, SafeInternetExplorerDriverHandle driverHandle, IntPtr elements)
        {
            this.driver = driver;
            this.driverHandle = driverHandle;
            this.elements = elements;
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdcGetElementCollectionLength(IntPtr elementCollection, ref int count);
        [DllImport("InternetExplorerDriver")]
        private static extern int wdcGetElementAtIndex(IntPtr elementCollection, int index, ref ElementWrapper result);
        public List<IWebElement> ToList()
        {
            List<IWebElement> toReturn = new List<IWebElement>();
            int nelements = 0;
            wdcGetElementCollectionLength(elements, ref nelements);
            for (int i = 0; i < nelements; i++)
            {
                ElementWrapper wrapper = new ElementWrapper();
                int result = wdcGetElementAtIndex(elements, i, ref wrapper);
                //TODO(andre.nogueira): I don't like this very much... Maybe add a ErrorHandler.IsError or something?
                try
                {
                    ErrorHandler.VerifyErrorCode(result, "");
                } 
                catch (Exception)
                {
                    freeElements(elements);
                    //TODO(andre.nogueira): More suitable exception
                    throw new Exception("Could not retrieve element " + i + " from element collection");
                }
                toReturn.Add(new InternetExplorerWebElement(driver, wrapper));
                
            }
            //TODO(andre.nogueira): from the java code (elementcollection.java)... "Free memory from the collection"
            freeCollection(elements);
            return toReturn;
        }

        [DllImport("InternetExplorerDriver")]
        private static extern int wdFreeElementCollection(IntPtr elementCollection, int index);
        private void freeElements(IntPtr rawElements)
        {
            wdFreeElementCollection(rawElements, 1);
        }

        private void freeCollection(IntPtr rawElements)
        {
            wdFreeElementCollection(rawElements, 0);
        }

    }
}
