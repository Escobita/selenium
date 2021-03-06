using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Globalization;
using System.Security.Permissions;

namespace OpenQA.Selenium.IE
{
    // TODO(andre.nogueira): StringCollection, ElementCollection and StringWrapperHandle should be consistent among them
    [SecurityPermission(SecurityAction.LinkDemand, Flags = SecurityPermissionFlag.UnmanagedCode)]
    class StringCollection : IDisposable
    {
        private SafeStringCollectionHandle handle;

        public StringCollection(SafeStringCollectionHandle elementCollectionHandle)
        {
            handle = elementCollectionHandle;
        }

        public List<string> ToList()
        {
            int elementCount = 0;
            WebDriverResult result = NativeMethods.wdcGetStringCollectionLength(handle, ref elementCount);
            if (result != WebDriverResult.Success)
            {
                Dispose();
                throw new WebDriverException(string.Format(CultureInfo.InvariantCulture, "Cannot extract strings from collection: {0}", result));
            }

            List<string> toReturn = new List<String>();
            for (int i = 0; i < elementCount; i++)
            {
                SafeStringWrapperHandle stringHandle = new SafeStringWrapperHandle();
                result = NativeMethods.wdcGetStringAtIndex(handle, i, ref stringHandle);
                if (result != WebDriverResult.Success)
                {
                    stringHandle.Dispose();
                    Dispose();
                    throw new WebDriverException(string.Format(CultureInfo.InvariantCulture, "Cannot extract string from collection at index: {0} ({1})", i, result));
                }
                using (StringWrapper wrapper = new StringWrapper(stringHandle))
                {
                    toReturn.Add(wrapper.Value);
                }
            }
            //TODO(andre.nogueira): from the java code (elementcollection.java)... "Free memory from the collection"
            //Dispose();
            return toReturn;
        }

        #region IDisposable Members

        public void Dispose()
        {
            handle.Dispose();
            GC.SuppressFinalize(this);
        }

        #endregion
    }
}
