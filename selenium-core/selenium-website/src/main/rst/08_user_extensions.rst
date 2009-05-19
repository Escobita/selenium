
User-Extensions
===============
Introduction
------------------
It can be quite simple to extend Selenium, adding your own actions, assertions and locator-strategies. This is done with JavaScript by adding methods to the Selenium object prototype, and the PageBot object prototype. On startup, Selenium will automatically look through methods on these prototypes, using name patterns to recognize which ones are actions, assertions and locators.
The following examples try to give an indication of how Selenium can be extended with JavaScript.

Actions
-------

All methods on the Selenium prototype beginning with "do" are added as actions. For each action foo there is also an action fooAndWait registered. An action method can take up to two parameters, which will be passed the second and third column values in the test.
Example: Add a "typeRepeated" action to Selenium, which types the text twice into a text box.
        Selenium.prototype.doTypeRepeated = function(locator, text) {
            // All locator-strategies are automatically handled by "findElement"
            var element = this.page().findElement(locator);
        
            // Create the text to type
            var valueToType = text + text;
        
            // Replace the element text with the new text
            this.page().replaceText(element, valueToType);
        };
        
Accessors/Assertions
--------------------

All getFoo and isFoo methods on the Selenium prototype are added as accessors (storeFoo.) For each accessor there is an assertFoo, verifyFooand waitForFoo registered. An assert method can take up to 2 parameters, which will be passed the second and third column values in the test. You can also define your own assertions literally as simple "assert" methods, which will also auto-generate "verify" and "waitFor" commands.
Example: Add a valueRepeated assertion, that makes sure that the element value consists of the supplied text repeated. The 2 commands that would be available in tests would be assertValueRepeated and verifyValueRepeated.
        Selenium.prototype.assertValueRepeated = function(locator, text) {
            // All locator-strategies are automatically handled by "findElement"
            var element = this.page().findElement(locator);
        
            // Create the text to verify
            var expectedValue = text + text;
        
            // Get the actual element value
            var actualValue = element.value;
        
            // Make sure the actual value matches the expected
            Assert.matches(expectedValue, actualValue);
        };
        
Automatic availability of storeFoo, assertFoo, assertNotFoo, waitForFoo and waitForNotFoo for every getFoo
----------------------------------------------------------------------------------------------------------
All getFoo and isFoo methods on the Selenium prototype automatically result in the availability of storeFoo, assertFoo, assertNotFoo, verifyFoo, verifyNotFoo, waitForFoo, and waitForNotFoo commands.
Example, if you add a getTextLength() method, the following commands will automatically be available: storeTextLength, assertTextLength, assertNotTextLength, verifyTextLength, verifyNotTextLength, waitForTextLength, and waitForNotTextLength commands.
        Selenium.prototype.getTextLength = function(locator, text) {
            return this.getText(locator).length;
        };
        
Also note that the assertValueRepeated method described above could have been implemented using isValueRepeated, with the added benefit of also automatically getting assertNotValueRepeated, storeValueRepeated, waitForValueRepeated and waitForNotValueRepeated.

Locator Strategies
------------------

All locateElementByFoo methods on the PageBot prototype are added as locator-strategies. A locator strategy takes 2 parameters, the first being the locator string (minus the prefix), and the second being the document in which to search.
Example: Add a "valuerepeated=" locator, that finds the first element a value attribute equal to the the supplied value repeated.
        // The "inDocument" is a the document you are searching.
        PageBot.prototype.locateElementByValueRepeated = function(text, inDocument) {
            // Create the text to search for
            var expectedValue = text + text;
        
            // Loop through all elements, looking for ones that have 
            // a value === our expected value
            var allElements = inDocument.getElementsByTagName("*");
            for (var i = 0; i < allElements.length; i++) {
                var testElement = allElements[i];
                if (testElement.value && testElement.value === expectedValue) {
                    return testElement;
                }
            }
            return null;
        };

Using User-Extensions With Selenium-IDE
--------------------------------------

User-extensions are very easy to use with the selenium IDE. 
1. Create your user extension and save it as user-extensions.js. While this name isn't technically necessary, it's good practice to keep things consistent.
2. Open Firefox and open Selenium-IDE.
3. Click on Tools > Options
4. In Selenium Core Extensions click on Browse and find the user-extensions.js file. Click on OK.
5. Your user-extension will not yet be loaded, you must close and restart Selenium-IDE.
6. In your empty test, create a new command, your user-extension should now be an uptions in the Commands dropdown.

Using User-Extensions With Selenium RC
--------------------------------------

If you Google "Selenium RC user-extension" ten times you will find ten different approaches to using this feature. Below, is the official Selenium suggested approach

Simple Example
~~~~~~~~~~~~~

Place your user extension in the same directory as your Selenium Server
If you are using client code generated by the Selenium-IDE you will need to make a couple small edits. First, you will need to create an HttpCommandProcessor object with class scope (outside the SetupTest method, just below  "private StringBuilder verificationErrors;")

HttpCommandProcessor proc;

Next, instantiate that HttpCommandProcessor object as you would the DefaultSelenium object.

proc = new HttpCommandProcessor("localhost", 4444, "*iexplore", "http://google.ca/");

Instantiate the DefaultSelenium object using the HttpCommandProcessor object you created.

selenium = new DefaultSelenium(proc);

Within your test code, execute your user-extension by calling it with the DoCommand method of HttpCommandProcessor. This method takes two arguments: a string to identify the user-extension you want to use and string array to pass arguments. For example, to call the sample extension the code would be
string[] inputParams = {"Hello World"};
proc.DoCommand("alertWrapper", inputParams);
Notice that the first letter of your function is lower case, regardless of the capitalization in your user-extension. Selenium automatically does this to keep common JavaScript naming conventions. Because JavaScript is case sensitive, your test will fail if you begin this command with a capital. inputParams is the array of arguments you want to pass to the JavaScript user-extension. In this case there is only one string in the array because there is only one parameter for our user extension, but a longer array will map each index to the corresponding user-extension parameter. Remember that user extensions designed for Selenium-IDE will only take two arguments.

Start the test server using the -userExtensions argument and pass in your user-extensinos.js file. 

	java -jar selenium-server.jar -userExtensions user-extensions.js

