using NUnit.Core;
using NUnit.Core.Extensibility;
using OpenQa.Selenium;
using System.Reflection;

namespace OpenQa.Selenium
{
	[NUnitAddin(Description="Ignores a given test on a given browser")]
	public class IgnoredBrowserTestDecorator : ITestDecorator, IAddin
	{
		private static readonly string IgnoreAttributeType = "OpenQa.Selenium.IgnoreBrowserAttribute";

		public bool Install(IExtensionHost host)
		{
			IExtensionPoint decorators = host.GetExtensionPoint( "TestDecorators" );
			if ( decorators == null )
				return false;
				
			decorators.Install( this );
			return true;
		}

		public Test Decorate(Test test, MemberInfo member)
		{
			if ( member == null )
				return test;

			TestCase testCase = test as TestCase;
			if ( testCase == null )
				return test;

			System.Attribute[] ignoreAttr = 
                Reflect.GetAttributes( member, IgnoreAttributeType, true );

			if ( ignoreAttr == null )
				return test;

            // A test case might be ignored in more than one browser
            foreach (System.Attribute attr in ignoreAttr)
            {
                // TODO(andre.nogueira): Check if a reason has been entered
                // in the annotation, and if so include it in IgnoreReason.
                object propVal = Reflect.GetPropertyValue(attr, "Value",
                    BindingFlags.Public | BindingFlags.Instance);

                if (propVal == null)
                    return test;

                Browser browser = (Browser)propVal;

                if (browser.Equals(Environment.Instance.Browser) ||
                    browser.Equals(Browser.ALL))
                {   
                    testCase.RunState = RunState.Ignored;
                    testCase.IgnoreReason = "Ignoring browser " +
                        Environment.Instance.Browser.ToString() + ".";
                    
                    return testCase;
                }
            }

            return test;
            
		}
	}
}
