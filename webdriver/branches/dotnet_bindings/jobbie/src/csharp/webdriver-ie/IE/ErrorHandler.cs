using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium.IE
{
    public class ErrorHandler
    {
        //TODO(andre.nogueira): Implement the appropriate exceptions and throw them
        public static void VerifyErrorCode(int errorCode, String message)
        {
            switch (errorCode)
            {
                case 0:
                    break; // Nothing to do

                case 7:
                    throw new Exception(message);

                case 8:
                    throw new Exception(message);

                case 9:
                    throw new Exception("You may not perform the requested action");

                case 10:
                    throw new Exception(
                        String.Format("You may not {0} this element. It looks as if the reference is stale. " +
                                      "Did you navigate away from the page with this element on?", message));

                case 11:
                    throw new Exception(
                        String.Format("You may not {0} an element that is not displayed", message));

                case 12:
                    throw new Exception(
                            String.Format("You may not {0} an element that is not enabled", message));

                case 15:
                    throw new Exception(
                            String.Format("The element appears to be unselectable: %s", message));

                case 16:
                    throw new Exception(message + " (no document found)");

                case 21:
                    throw new Exception("The driver reported that the command timed out. There may "
                                                    + "be several reasons for this. Check that the destination"
                                                    + "site is in IE's 'Trusted Sites' (accessed from Tools->"
                                                    + "Internet Options in the 'Security' tab) If it is a "
                                                    + "trusted site, then the request may have taken more than"
                                                    + "a minute to finish.");

                default:
                    throw new Exception(String.Format("{0} ({1})", message, errorCode));
            }
        }

    }
}
