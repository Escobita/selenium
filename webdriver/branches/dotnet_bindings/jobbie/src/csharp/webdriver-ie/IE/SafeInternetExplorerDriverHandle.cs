using Microsoft.Win32.SafeHandles;
using System.Runtime.InteropServices;
using System;

namespace OpenQa.Selenium.IE
{
    internal class SafeInternetExplorerDriverHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        internal SafeInternetExplorerDriverHandle()
            : base(true)
        {
        }

        [DllImport("InternetExplorerDriver")]
        public static extern void wdFreeString(IntPtr str);

        [DllImport("InternetExplorerDriver")]
        public static extern Int32 wdStringLength(IntPtr str, ref IntPtr length);

        [DllImport("InternetExplorerDriver")]
        public static extern void wdFreeDriver(IntPtr driver);

        [DllImport("InternetExplorerDriver")]
        public static extern void wdClose(IntPtr driver);

        protected override bool ReleaseHandle()
        {
            wdClose(handle);
            wdFreeDriver(handle);
            // TODO(simonstewart): Are we really always successful?
            return true;
        }


    }
}
