.. _chapter05-reference:

|logo| Selenium-RC
==================

.. |logo| image:: images/selenium-rc-logo.png
   :alt:

Introduction
------------
Selenium-RC is the response for tests that need a little more than just simple
browser actions and a linear execution. Selenium-RC allows the users to use the 
full power of programming languages, creating tests that can do things like read
and write external files, make queries to a Data Base, send emails with test 
reports, practically anything a user can do with a normal application.

Basically, you will need to use Selenium-RC whenever your test requires logic
not supported by running a script from Selenium-IDE. What sort of logic could 
this be? For example, Selenium-IDE does not directly support:

* conditional statements 
* iteration 
* logging and reporting of test results
* error handling, particularly unexpected errors
* database testing
* test case grouping
* re execution of failed tests
* test case dependency
* capture screenshots on test failures

Though these are not supported by selenium inherently all of them can be achieved
by using language specific libraries.

.. note:: It may be possible to add this functionality by the addition of user 
   extensions to Selenium-IDE but most prefer to use Selenium-RC as it's already
   bundled in any programming language.

In `Adding Some Spice to Your Tests`_ section, you'll find some examples that 
demontrate the advantages of using all the power of a real programming language
for your tests.

How Selenium Remote Control works
----------------------------------
Selenium-RC Basic Structure
~~~~~~~~~~~~~~~~~~~~~~~~~~~
Selenium-RC is composed of two parts:

* A server which automatically launches and kills browsers, and acts as a HTTP
  proxy for web requests from them. 
* Client libraries for your favorite programming language, which communicate 
  with the server giving the orders to excecute the tests.

The RC server bundles Selenium Core, and automatically injects it into the 
browser within the Application Under Test.

Here is a simplified representation.... 

.. image:: images/chapt5_img01_Architecture_Diagram_Simple.png
   :align: center

As you can see on the illustration, the client libraries communicate with the
Server directly passing one by one the actions to execute. Then the server gives
this orders to the browser by the use of Selenium-Core javascript commands.

The Server receives commands directly using simple HTTP GET/POST requests;
that means that you can use any programming language that can make HTTP requests
to automate Selenium tests on the browser.

Relationship between Client libs and Selenese
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
.. TODO: Mary Ann pointed out this and I think is very important:
   Info about the individual language APIs for RC being "wrappers" for the
   Selenese commands covered in the chapter.  We need to make clear that
   everyone needs to understand Selenese, but that in order to write a
   Perl/Selenium test (for example), one must also familiarize oneself
   with the Perl/Selenium API.  I recommend that we have a completed
   version of the sketched table below, only with parameter lists added
   for all command cells (including the first row):

.. Selenese    type    click    verifyTextPresent    assertAlert
   Java
   Perl
   C#
   Python
   PHP
   etc.

Installation
-------------
Once you download the whole Selenium-RC zip file from the `downloads page`_ you
will notice that it has lots of sub-folders inside. As you already know from
the `How Selenium Remote Control Works`_, this folders have all the sub-parts
that integrates the RC.

Once you've chosen a language to work with, you'll only need to install the 
server and the client driver you need.

Selenium server installation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
The Selenium-RC server is just a jar file, which doesn't need installation at
all. Just downloading the zip file and extracting the server in the desired
directory should be enough. To start running any tests you've written in any 
programming language, you just have to go to the directory where Selenium-RC
is located and execute the following line in a console:: 

    java -jar selenium-server.jar

Most people like to have a more simplified setup, which can be made by creating
an executable batch file (.bat on windows and .sh on linux) with just the line
writen above. This way, you can make a shortcut to that executable file in your
desktop and just double-click on it anytime you want to wake up the server to 
start your tests.

.. note:: For the server to run you'll need java installed on your computer 
   and propperly setup on the PATH variable to run it from the console.
   You can check that you have java correctly installed by running the following
   on a console::

       java -version

   If you get a version number, your setup ready to start using Selenium-RC.

.. _`downloads page`: http://seleniumhq.org/download/

Java client driver configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Download Selenium-RC from the SeleniumHQ `downloads page`_ 
* Extract the file *selenium-java-client-driver.jar*
* Open your desired java IDE (Eclipse, IntelliJ, Netweaver, etc.)
* Create a new project
* Add to your project classpath the file *selenium-java-client-driver.jar*
* Write your Selenium test in Java
* Run selenium server from console
* Execute your test from the IDE

Python client driver configuration 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Download Selenium-RC from the SeleniumHQ `downloads page`_ 
* Extract the file *selenium.py*
* Write your Selenium test in Python
* Add to your tests path the file *selenium.py*
* Run selenium server from console
* Execute your test from a console or the IDE

.NET client driver configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
.. Someone should resume the whole installation procedure in a 
   list of steps as in previous languages.

Sample Test Scripts
-------------------
If we use the following test recorded with Selenium-IDE as a base:

.. _search example:

=================  ============  ===========
open               /
type               q             selenium rc
clickAndWait       submit
assertTextPresent  Selenium-RC
=================  ============  ===========

.. note:: In the table is not mentioned that the domain to test is 
   http://www.google.com

Here is the test script exported to all the programming languages:

.. container:: toggled

   .. code-block:: c#

        using System;
        using System.Text;
        using System.Text.RegularExpressions;
        using System.Threading;
        using NUnit.Framework;
        using Selenium;

        namespace SeleniumTests
        {
        	[TestFixture]
        	public class NewTest
        	{
        		private ISelenium selenium;
        		private StringBuilder verificationErrors;
        		
        		[SetUp]
        		public void SetupTest()
        		{
        			selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://www.google.com/");
        			selenium.Start();
        			verificationErrors = new StringBuilder();
        		}
        		
        		[TearDown]
        		public void TeardownTest()
        		{
        			try
        			{
        				selenium.Stop();
        			}
        			catch (Exception)
        			{
        				// Ignore errors if unable to close the browser
        			}
        			Assert.AreEqual("", verificationErrors.ToString());
        		}
        		
        		[Test]
        		public void TheNewTest()
        		{
        			selenium.Open("/");
        			selenium.Type("q", "selenium rc");
        			selenium.Click("submit");
        			selenium.WaitForPageToLoad("30000");
        			Assert.IsTrue(selenium.IsTextPresent("Selenium-RC"));
        		}
        	}
        }

.. container:: toggled

   .. code-block:: java

      package com.example.tests;

      import com.thoughtworks.selenium.*;
      import java.util.regex.Pattern;

      public class NewTest extends SeleneseTestCase {
      	public void setUp() throws Exception {
      		setUp("http://www.google.com/", "*firefox");
      	}
            public void testNew() throws Exception {
      	      selenium.open("/");
      	      selenium.type("q", "selenium rc");
      	      selenium.click("submit");
      	      selenium.waitForPageToLoad("30000");
      	      assertTrue(selenium.isTextPresent("Selenium-RC"));
      	}
      }

.. container:: toggled

   .. code-block:: perl

      use strict;
      use warnings;
      use Time::HiRes qw(sleep);
      use Test::WWW::Selenium;
      use Test::More "no_plan";
      use Test::Exception;

      my $sel = Test::WWW::Selenium->new( host => "localhost", 
                                          port => 4444, 
                                          browser => "*firefox", 
                                          browser_url => "http://www.google.com/" );

      $sel->open_ok("/");
      $sel->type_ok("q", "selenium rc");
      $sel->click_ok("submit");
      $sel->wait_for_page_to_load_ok("30000");
      $sel->is_text_present_ok("Selenium-RC");

.. container:: toggled

   .. code-block:: php

      <?php

      require_once 'PHPUnit/Extensions/SeleniumTestCase.php';

      class Example extends PHPUnit_Extensions_SeleniumTestCase
      {
        function setUp()
        {
          $this->setBrowser("*firefox");
          $this->setBrowserUrl("http://www.google.com/");
        }

        function testMyTestCase()
        {
          $this->open("/");
          $this->type("q", "selenium rc");
          $this->click("submit");
          $this->waitForPageToLoad("30000");
          $this->assertTrue($this->isTextPresent("Selenium-RC"));
        }
      }
      ?>

.. container:: toggled

   .. code-block:: python

      from selenium import selenium
      import unittest, time, re

      class NewTest(unittest.TestCase):
          def setUp(self):
              self.verificationErrors = []
              self.selenium = selenium("localhost", 4444, "*firefox",
                      "http://www.google.com/")
              self.selenium.start()
         
          def test_new(self):
              sel = self.selenium
              sel.open("/")
              sel.type("q", "selenium rc")
              sel.click("submit")
              sel.wait_for_page_to_load("30000")
              self.failUnless(sel.is_text_present("Selenium-RC"))
         
          def tearDown(self):
              self.selenium.stop()
              self.assertEqual([], self.verificationErrors)

.. container:: toggled

   .. code-block:: ruby

      require "selenium"
      require "test/unit"

      class NewTest < Test::Unit::TestCase
        def setup
          @verification_errors = []
          if $selenium
            @selenium = $selenium
          else
            @selenium = Selenium::SeleniumDriver.new("localhost", 4444, "*firefox", "http://www.google.com/", 10000);
            @selenium.start
          end
          @selenium.set_context("test_new")
        end

        def teardown
          @selenium.stop unless $selenium
          assert_equal [], @verification_errors
        end

        def test_new
          @selenium.open "/"
          @selenium.type "q", "selenium rc"
          @selenium.click "submit"
          @selenium.wait_for_page_to_load "30000"
          assert @selenium.is_text_present("Selenium-RC")
        end
      end

Now we will analyze the different parts of the tests for you to understand
each statement.

Basic Tests Structure
~~~~~~~~~~~~~~~~~~~~~

Here you will find an explanation of the basic test structure on each 
programming language. This tends to differ from one to another, so you'll find
separate explanations for each of them:

* `C#`_
* Java_
* Perl_
* PHP_ 
* Python_
* Ruby_ 

C#
++

.NET Client Driver works with Microsoft.NET.
It can be used together with any .NET testing framework 
like NUnit or the Visual Studio 2005 Team System.

.. code-block:: c#

	using System;
	using System.Text;
	using System.Text.RegularExpressions;
	using System.Threading;
	using NUnit.Framework;
	using Selenium;
	
	namespace SeleniumTests

	{
	    [TestFixture]

	    public class NewTest

	    {
		private ISelenium selenium;

		private StringBuilder verificationErrors;

		[SetUp]

		public void SetupTest()

		{
		    selenium = new DefaultSelenium("localhost", 4444, "*iehta",
		    "http://www.google.com/");

		    selenium.Start();

		    verificationErrors = new StringBuilder();
		}

		[TearDown]

		public void TeardownTest()
		{
		    try
		    {
			selenium.Stop();
		    }

		    catch (Exception)
		    {
			// Ignore errors if unable to close the browser
		    }

		    Assert.AreEqual("", verificationErrors.ToString());
		}
		[Test]

		public void TheNewTest()
		{
		    // Open Google search engine.		
		    selenium.Open("http://www.google.com/"); 
		    
		    // Assert Title of page.
		    Assert.AreEqual("Google", selenium.GetTitle());
		    
		    // Provide search term as "Selenium OpenQA"
		    selenium.Type("q", "Selenium OpenQA");
		    
		    // Read the keyed search term and assert it.
		    Assert.AreEqual("Selenium OpenQA", selenium.GetValue("q"));
		    
		    // Click on Search button.
		    selenium.Click("btnG");
		    
		    // Wait for page to load.
		    selenium.WaitForPageToLoad("5000");
		    
		    // Assert that "www.openqa.org" is available in search results.
		    Assert.IsTrue(selenium.IsTextPresent("www.openqa.org"));
		    
		    // Assert that page title is - "Selenium OpenQA - Google Search"
		    Assert.AreEqual("Selenium OpenQA - Google Search", 
		    		     selenium.GetTitle());
		}
	    }
	}


Java
++++
For java, we use a wrapper_ of the basic Junit test case. With it, you'll save
many lines of code by just writing the basic part and letting the wrapper do
all the rest.

.. _wrapper: http://release.seleniumhq.org/selenium-remote-control/1.0-beta-2/doc/java/com/thoughtworks/selenium/SeleneseTestCase.html

.. code-block:: java

   package com.example.tests;
   // We specify the package of our tess

   import com.thoughtworks.selenium.*;
   // This is the driver's import, you'll use this for instantiating a
   // browser and make it do what you need.

   import java.util.regex.Pattern;
   // Selenium-IDE add the Pattern module because it's sometimes used for 
   // regex validations. You can remove the module if it's not used in your 
   //script.

   public class NewTest extends SeleneseTestCase {
   // We create our selenium test case

         public void setUp() throws Exception {
   		setUp("http://www.google.com/", "*firefox");
                // We instantiate and start the browser
         }

         public void testNew() throws Exception {
              selenium.open("/");
              selenium.type("q", "selenium rc");
              selenium.click("submit");
              selenium.waitForPageToLoad("30000");
              assertTrue(selenium.isTextPresent("Selenium-RC"));
              // These are the real test steps
        }
   }

Perl
++++

PHP
+++

Python
++++++
We use pyunit testing framework (the unittest module) for our tests, you should
understand how this works to better understand how to write your tests.
To completely understand pyunit, you should read it's `official documentation
<http://docs.python.org/library/unittest.html>`_.

The basic test structure is:

.. code-block:: python

   from selenium import selenium
   # This is the driver's import, you'll use this class for instantiating a
   # browser and make it do what you need.

   import unittest, time, re
   # This are the basic imports added by Selenium-IDE by default.
   # You can remove the modules if they are not used in your script.

   class NewTest(unittest.TestCase):
   # We create our unittest test case

       def setUp(self):
           self.verificationErrors = []
           # This is an empty array where we will store any verification errors
           # we find in our tests

           self.selenium = selenium("localhost", 4444, "*firefox",
                   "http://www.google.com/")
           self.selenium.start()
           # We instantiate and start the browser

       def test_new(self):
           # This is the test code, here you should put the actions you need
           # the browser to do during your test
            
           sel = self.selenium
           # We assign the browser to the variable "sel" (just to save us from 
           # typing "self.selenium" each time we want to call the browser).
            
           sel.open("/")
           sel.type("q", "selenium rc")
           sel.click("submit")
           sel.wait_for_page_to_load("30000")
           self.failUnless(sel.is_text_present("Selenium-RC"))
           # These are the real test steps

       def tearDown(self):
           self.selenium.stop()
           # we close the browser (I'd recommend you to comment this line while
           # you are creating and debugging your tests)

           self.assertEqual([], self.verificationErrors)
           # And make the test fail if we found that any verification errors
           # where found

Ruby
++++

Starting The Browser 
~~~~~~~~~~~~~~~~~~~~~

.. container:: toggled

   .. code-block:: c#

      selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://www.google.com/");
      selenium.Start();

.. container:: toggled

   .. code-block:: java

      setUp("http://www.google.com/", "*firefox");

.. container:: toggled

   .. code-block:: perl

      my $sel = Test::WWW::Selenium->new( host => "localhost", 
                                          port => 4444, 
                                          browser => "*firefox", 
                                          browser_url => "http://www.google.com/" );

.. container:: toggled

   .. code-block:: php

      $this->setBrowser("*firefox");
      $this->setBrowserUrl("http://www.google.com/");

.. container:: toggled

   .. code-block:: python

      self.selenium = selenium("localhost", 4444, "*firefox",
                               "http://www.google.com/")
      self.selenium.start()

.. container:: toggled

   .. code-block:: ruby

      if $selenium
        @selenium = $selenium
      else
        @selenium = Selenium::SeleniumDriver.new("localhost", 4444, "*firefox", "http://www.google.com/", 10000);
        @selenium.start

Each of this sentences is in charge of instantiating a browser (which is just
an object for your code) and assigning the "browser" instance to a variable 
(which will later be used to call methods from the browser, like *open* or 
*type*)

The initial parameters that you should give when you create the browser instance
are: 

host
    This is the ip location where the server is located. Most of the times is
    the same machine than the one where the client is running, so you'll see
    that it's an optional parameter on some clients.
port
    As the host, it determines on which socket is the server listening waiting
    for the client to communicate with him. Again, it can be optional in some
    client drivers.
browser
    The browser in which you want to run the tests. This is a required 
    parameter (I hope you understand why :))
url
    The base url of the application under test. This is also required on all the
    client libs and Selenium-RC needs it before starting the browser due to the
    way the same server is implemented.

Finally, some languages require the browser to be started explicitly by calling
it's *start* method.

Running Commands 
~~~~~~~~~~~~~~~~
Once you have the browser initialized and assigned to a variable (generally
named selenium) you can make it run commands by calling the respective 
methods from the selenium browser. For example, when you call the *type* method
of the selenium object::

    selenium.type("field-id","sting to type")

In backend (by the magic of Selenium-RC), the browser will actually **type** 
using the locator and the string you specified during the method call. So, 
summarizing, what for your code is just a regular object (with methods and 
properties), in backend it's making the real browser do things.

Retrieving and Reporting Results
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Each programming language has it's own testing framework which is used to
run the tests. Everyone of them has it's own way of reporting the results
and you'll surely find third-party libraries specially created for reporting
test results in different formats as HTML or PDF.

Adding Some Spice to Your Tests
-------------------------------
Now you'll understand why you needed Selenium-RC and you just couldn't stay
only with the IDE. We will try to give you some guidance on things that can
only be done using a programming language. The different examples are written
in only one of the languages, the idea is that you understand the concept, be
able to translate it to the language of your choice and upgrade it for your
needs.

Iteration
~~~~~~~~~
Iteration is one of the most common things people needs to do in their tests.
Generally, to repeat a simple search, or saving you from duplicating the same
code several times.

If we take the `search example`_ we've been looking at, it's not so crazy to 
think that we want to check that all the Selenium tools appear on the search
we make. This kind of test could be made doing the following using Selenese:

=================  =============  =============
open               /
type               q              selenium rc
clickAndWait       submit
assertTextPresent  Selenium-RC
type               q              selenium ide
clickAndWait       submit 
assertTextPresent  Selenium-IDE 
type               q              selenium grid
clickAndWait       submit 
assertTextPresent  Selenium-Grid 
=================  =============  =============

As you can see, the code has been triplicated to run the same steps 3 times.
This doesn't look to efficient.

By using a programming language, we can just iterate over a list and do the 
search in the following way. 

**In C#:**   
   
.. code-block:: c#

   // Collection of String values.	
   String[] arr = {"IDE", "RC", "GRID"};	
		
   // Execute For loop for each String in 'arr' array.
   foreach (String s in arr) {
   	sel.open("/");
  	sel.type("q", "selenium " +s);
   	sel.click("submit");
        sel.waitForPageToLoad("30000");
        assertTrue("Expected text: " +s+ " is missing on page."
        , sel.isTextPresent("Selenium-" + s));
   
   }

Conditionals
~~~~~~~~~~~~
Most common errors are encountered while running selenium test are the errors 
which pop up when corresponding element locator is not available on page.
For example, when running the following line:

.. code-block:: java
   
   selenium.type("q", "selenium " +s);
   
If element 'q' happens to be unavailable on page then following exception is
thrown:

.. code-block:: java

   com.thoughtworks.selenium.SeleniumException: ERROR: Element q not found

A better approach would be to first validate if the element is really present
and then take different alternatives in case it is not:

**In Java:**

.. code-block:: java
   
   // If element is available on page then perform type operation.
   if(selenium.isElementPresent("q")) {
       selenium.type("q", "Selenium-RC");			
   } else {
       selenium.open("/")
   }

By just using a simple *if* condition, we can do interesting things. Think of
the possibilities!

Data Driven Testing
~~~~~~~~~~~~~~~~~~~
So, the iteration_ idea seems cool. Let's improve this by allowing the users to
write an external text file from which the script should read the input data,
search and assert it's existence.

.. TODO: The script for this example

As you can see, this task looks really simple being made using a scripting
language while it's impossible to do using Selenium-IDE.

Error Handling
~~~~~~~~~~~~~~

.. The idea here is to use a try-catch statement to grab a really unexpected
   error.

Data Base Validations
~~~~~~~~~~~~~~~~~~~~~
Off course, you can also do Data Base queries in your favorite scripting 
language. Why not using them for some data validations/retrieval on the 
Application Under Test?

Consider example of Registration process where in registered email address
is to be retrieved from database. Specific cases of establishing DB connection 
and retrieving data from DB would be -

**In Java:**

.. code-block:: java

   // Load Microsoft SQL Server JDBC driver.   
   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      
   // Prepare connection url.
   String url = "jdbc:sqlserver://192.168.1.180:1433;DatabaseName=TEST_DB";
   
   // Get connection to DB.
   public static Connection con = 
   DriverManager.getConnection(url, "username", "password");
   
   // Create statement object which would be used in writing DDL and DML 
   // SQL statement.
   public static Statement stmt = con.createStatement();
   
   // Send SQL SELECT statements to the database via the Statement.executeQuery
   // method which returns the requested information as rows of data in a 
   // ResultSet object.
   
   ResultSet result =  stmt.executeQuery
   ("select top 1 email_address from user_register_table");
   
   // Fetch value of "email_address" from "result" object.
   String emailaddress = result.getString("email_address");
   
   // Use the fetched value to login to application.
   selenium.type("userid", emailaddress);
   
This is very simple example of data retrieval from DB in Java.
A more complex test could be to validate that inactive users are not able
to login to application. This wouldn't take too much work from what you've 
already seen.
   
How the Server works
--------------------
.. note:: This topic tries to explain the technical implementation behind 
   Selenium-RC. It's not fundamental for a Selenium user to know this, but 
   could be useful for understanding some of the problems you can find in the
   future.
   
To understand in detail how Selenium-RC Server works  and why it uses proxy injection
and hightened privilege modes you must first understand `the same origin policy`_.
   
The Same Origin Policy
~~~~~~~~~~~~~~~~~~~~~~
The main restriction that Selenium's architecture has faced is the 
Same Origin Policy. This security restriction is applied by every browser
in the market and it's objective is to ensure that a site's content will never
be accessible by a script from other site.

If this were possible, a script placed on any website you open, would 
be able to read information on your bank account if you had the account page
opened on other tab. Which is also called XSS (Cross-site Scripting).

To work under that policy. Selenium-Core (and it's javascript commands that
make all the magic happen) must be placed in the same origin as the Application
Under Test (same URL). This has been the way Selenium-Core was first
used and implemented (by deploying Selenium-Core and the set of tests inside
the application's server), but this was a requirement that not all the projects 
could meet and Selenium Developers had to find an alternative that would allow 
testers to use Selenium to test site where they didn't have the possibility to
deploy their code. 

.. note:: You can find additional information about this topic on wikipedia
   pages about `Same Origin Policy`_ and XSS_. 

.. _Same Origin Policy: http://en.wikipedia.org/wiki/Same_origin_policy
.. _XSS: http://en.wikipedia.org/wiki/Cross-site_scripting

Proxy Injection
~~~~~~~~~~~~~~~
The first method used to skip the `The Same Origin Policy`_ was Proxy Injection.
In this method, the Selenium Server acts as a client-configured [1]_ **HTTP 
proxy** [2]_, that stands in between the browser and the Application Under Test.
After this, it is able to masks the whole AUT under a fictional URL (embedding
Selenium-Core and the set of tests and delivering them as if they were coming
from the same origin). 

.. [1] The proxy is a third person in the middle that passes the ball 
   between the two parts. In this case will act as a "web server" that 
   delivers the AUT to the browser. Being a proxy, gives the capability
   of "lying" about the AUT real URL.  
   
.. [2] The client browser (Firefox, IE, etc) is launched with a 
   configuration profile that has set localhost:4444 as the HTTP proxy, this
   is why any HTTP request that the browser does will pass through Selenium
   server and the response will pass through it and not from the real server.

Here is an architectural diagram. 

.. TODO: Notice: in step 5, the AUT should pass through the HTTPProxy to go to 
   the Browser....

.. image:: images/chapt5_img02_Architecture_Diagram.png
   :align: center

As a test suite starts in your favorite language, the following happens:

1. The client/driver establishes a connection with the selenium-RC server.
2. Selenium-Server launches a browser (or reuses an old one) with an URL that 
   will load Selenium-Core in the web page.
3. Selenium-RC gets the first instruction from the client/driver (via another 
   HTTP request made to the Selenium-RC Server).
4. Selenim-Core acts on that first instruction, typically opening a page of the
   AUT.
5. The server is asked for that page, and it renders in the frame/window 
   reserved for it.
   
.. TODO: I've got to update the image and the steps to include some of the 
   information that is missing (Server response to the libs, AUT passing through 
   the server, etc).
   
Hightened Privileges Browsers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This workflow on this method is very similar to Proxy Injection but the main
difference is that the browsers are launched in a special mode called *Hightened
Privileges*, which allows websites to do things that are not commonly permitted
(as doing XSS_, or filling file upload inputs and pretty useful stuff for 
Selenium). By using this browser modes, Selenium Core is able to directly open
the AUT and read/interact with it's content without having to pass the whole AUT
through the Selenium-RC server.

Here is the architectural diagram. 

.. image:: images/chapt5_img02_Architecture_Diagram.png
   :align: center

As a test suite starts in your favorite language, the following happens:

1. The client/driver establishes a connection with the Selenium-RC server.
2. Selenium-Server launches a browser (or reuses an old one) with an URL that 
   will load Selenium-Core in the web page.
3. Selenium-RC gets the first instruction from the client/driver (via another 
   HTTP request made to the Selenium-RC Server).
4. Selenim-Core acts on that first instruction, typically opening a page of the
   AUT.
5. The server is asked for that page, and it renders in the frame/window 
   reserved for it.
   
.. TODO: I've got to update the image and the steps to include some of the 
   information that is missing (Server response to the libs, etc).
   
.. TODO: Call for review on the Devs list once I finish this topic!!!

Server Command Line options
---------------------------
When the server is launced, some command line options can be used to change the
default behaviour if it is needed.

As you already know, the server is started by running the following:

.. code-block:: bash
 
   $ java -jar selenium-server.jar

If you want to see the list of all the available options, you just have to use
the ``-h`` option:

.. code-block:: bash
 
   $ java -jar selenium-server.jar -h

You'll receive a list of all the options you can use on the server and a brief
explanation on all of them. 
Though, for some of those options, that short overview is not enough, so we've
written an in deep explanation for them.

Multi-Window Mode
~~~~~~~~~~~~~~~~~
Before 1.0, Selenium by default ran the application under test in a subframe 
which looks like this:

.. image:: images/chapt5_img26_single_window_mode.png
   :align: center

Unfortunately, some apps don't run properly in a subframe, preferring to be 
loaded into the top frame of the window. That's why we made the multiWindow 
mode (the new default since Selenium 1.0). Using this you can make your 
application under test run in a separate window rather than in the default 
frame.

.. image:: images/chapt5_img27_multi_window_mode.png
   :align: center

Older versions of Selenium however did not handle this unless you explicitly 
told the server to run in multiwindow mode. For handling multiple windows, 
Selenium 0.9.2 required the Server to be started with the following option:

.. code-block:: bash

   -multiwindow 

In Selenium-RC 1.0 and later if you want to require your testing to run in a
single frame you can explicitly state this to the Selenium Server using the
option:

.. code-block:: bash
 
   -singlewindow 

Personalizing the Firefox Profile used in the tests
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Firefox will not run two instances simultaneously unless you specify a 
separate profile for each instance. Later versions of Selenium-RC run in a 
separate profile automatically, however, if you are using an older version of 
Selenium or if you need to have a special configuration in your running browser
(such as adding an https certificate or having some addons installed), you may 
need to explicitly specify a separate profile. 

Open the Windows Start menu, select "Run", then type and enter one of the 
following:

.. code-block:: bash

   firefox.exe -profilemanager 

.. code-block:: bash

   firefox.exe -P 

Create a new profile using the dialog. When you run the Selenium-RC server, 
tell it to use this new Firefox profile with the server command-line option 
*\-firefoxProfileTemplate* and specify the path to the profile:

.. code-block:: bash

   -firefoxProfileTemplate "path to the profile" 

.. note:: On windows, people tend to have problems with the profiles location.
   Try to start using a simple location like *C:\\seleniumProfile* to make it
   work and then move the profile where you want and try to find it again.

.. warning::  Be sure to put your profile in a separate new folder!!! 
   The Firefox profile manager tool will delete all files in a folder if you 
   delete a profile, regardless of whether they are profile files or not. 
   
More information about firefox profiles in `Mozilla's Knowledge Base`_

.. _Mozilla's KNowledge Base: http://support.mozilla.com/zh-CN/kb/Managing+profiles

.. _html-suite:

Run Selenese tests using -htmlSuite
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
To use the Selenium Server as a proxy, run your tests like this::

   java -jar selenium-server.jar -htmlSuite "*firefox" "http://www.google.com" "c:\absolute\path\to\my\HTMLSuite.html" "c:\absolute\path\to\my\results.html"

That will automatically launch your HTML suite, run all the tests and save a
nice HTML colored report with the results.

.. note::  After this command, the server will start the tests and wait for a
   specified number of seconds for the test to complete; if the test doesn't 
   complete within that amount of time, the command will exit with a non-zero 
   exit code and no results file will be generated.

Note that this command line is very long and very finicky... be careful when 
you type it in. (You can use the -htmlSuite parameter with the ``-port`` and 
``-timeout`` options, but it is incompatible with ``-interactive``; you can't 
do both of those at once.) Also note that it requires you to pass in an HTML 
Selenese suite, not a single test.

.. Selenium-IDE Generated Code
   ---------------------------
   Starting the Browser 
   --------------------
   Specify the Host and Port::
   localhost:4444 
   The Selenium-RC Program's Main() 
   --------------------------------
   Using the Browser While Selenium is Running 
   -------------------------------------------
   You may want to use your browser at the same time that Selenium is also using 
   it. Perhaps you want to run some manual tests while Selenium is running your 
   automated tests and you wish to do this on the same machine. Or perhaps you just
   want to use your Facebook account but Selenium is running in the background. 
   This isn't a problem. 
   
   With Internet Explorer, you can simply start another browser instance and run 
   it in parallel to the IE instance used by Selenium-RC. With Firefox, you can do
   this also, but you must specify a separate profile. 

Troubleshooting 
---------------
.. Santi: must recheck if all the topics here: 
   http://seleniumhq.org/documentation/remote-control/troubleshooting.html
   are covered.

Howto correctly use your Verify commands in Selenium-RC 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you export your tests from Selenium-IDE, you may find yourself getting
empty verify strings from your tests (depending on the programming language
used).

.. Santi: I'll put some info from 
   http://clearspace.openqa.org/message/56908#56908 (we should write an example
   for all the languages...)

Safari and multiWindow mode
~~~~~~~~~~~~~~~~~~~~~~~~~~~
.. Santi: we will have to explain the following:
   http://clearspace.openqa.org/community/selenium/blog/2009/02/24/safari-4-beta#comment-1514
   http://jira.openqa.org/browse/SEL-639

Firefox on Linux 
~~~~~~~~~~~~~~~~
On Unix/Linux, versions of Selenium before 1.0 needed to invoke "firefox-bin" 
directly, so if you are using a previous version, make sure that the real 
executable is on the path. 

On most linux distributions, the real firefox-bin is located on::

   /usr/lib/firefox-x.x.x/ 

Where the x.x.x is the version number you currently have. So, to add that path 
to the user's path. you will have to add the following to your .bashrc file:

.. code-block:: bash

   export PATH="$PATH:/usr/lib/firefox-x.x.x/"


.. This problem is caused because in linux, firefox is executed through a shell
   script (the one located on /usr/bin/firefox), when it comes the time to kill
   the browser Selenium-RC will kill the shell script, leaving the browser 
   running.  Santi: not sure if we should put this here...

If necessary, you can specify the path to firefox-bin directly in your test,
like this::

   "*firefox /usr/lib/firefox-x.x.x/firefox-bin"

IE and the style attributes
~~~~~~~~~~~~~~~~~~~~~~~~~~~
.. Santi: When used in the XPATH, the keys in  @style should be uppercase to 
   work on IE, even if they are lowercase in the source code

.. Paul: Hey Santi, what is this section?  Does this belong inthe Selenese 
   chapter?  That's where we're putting stuff on locators like XPATH.

.. Santi: I put this under the SelRC part, because it's only caused working 
   with IE (and this can only be done using Sel RC)

Unable to Connect to Server 
~~~~~~~~~~~~~~~~~~~~~~~~~~~
When your test program cannot connect to the Selenium Server, an exception 
will be thrown in your test program. It should display this message or a 
similar one::

    "Unable to connect to remote server….Inner Exception Message: No 
    connection could be made because the target machine actively refused it…."
    (using .NET and XP Service Pack 2) 

If you see a message like this, be sure you started the Selenium Server. If 
you did, then there is some problem with the connectivity between the two 
problems. This should not normally happen when your operating system has 
typical networking and TCP/IP settings. If you continue to have trouble, try 
a different computer. 
 
:: 

    (500) Internal Server Error 

This error seems to occur when Selenium-RC cannot load the browser.

::

    500 Internal Server Error 

(using .NET and XP Service Pack 2) 

* Firefox cannot start because the Firefox browser is already open and you did 
  not specify a separate profile. 
* The run mode you're using doesn't match any browser on your machine is this 
  true?  I haven't tried this one as I didn't want to uninstall either of my 
  browsers. 
* you specified the path to the browser explicitly (see above) but the path is 
  incorrect. 

Selenium Starts but Cannot Find the AUT 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If your test program starts Selenium successfully, but the browser window 
cannot display the website you're testing, the most likely cause is your test 
program is not using the correct URL. 

This can easily happen. When Selenium-IDE generates the native language code 
from your script it inserts a dummy URL. It may not (in the .NET-C# format 
this problem exists) use the base URL when it generates the code. You will 
need to explicitly modify the URL in the generated code. 

Firefox refused shutdown while preparing a profile 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This most often occurs when your run your Selenium-RC test program against Firefox,
but you already have a Firefox browser session running, and, you didn't specify
a separate profile when you started the Selenium Server. The error from the 
test program looks like this::

    Error:  java.lang.RuntimeException: Firefox refused shutdown while 
    preparing a profile 

(using .NET and XP Service Pack 2) 

Here's the complete error msg from the server::

    16:20:03.919 INFO - Preparing Firefox profile... 
    16:20:27.822 WARN - GET /selenium-server/driver/?cmd=getNewBrowserSession&1=*fir 
    efox&2=http%3a%2f%2fsage-webapp1.qa.idc.com HTTP/1.1 
    java.lang.RuntimeException: Firefox refused shutdown while preparing a profile 
            at org.openqa.selenium.server.browserlaunchers.FirefoxCustomProfileLaunc 
    her.waitForFullProfileToBeCreated(FirefoxCustomProfileLauncher.java:277) 
    ……………………. 
    Caused by: org.openqa.selenium.server.browserlaunchers.FirefoxCustomProfileLaunc 
    her$FileLockRemainedException: Lock file still present! C:\DOCUME~1\jsvec\LOCALS 
    ~1\Temp\customProfileDir203138\parent.lock 

To resolve this, see the section on `Specifying a Separate Firefox Profile 
<Personalizing the Firefox Profile used in the tests>`_

Handling HTTPS and Security Popups 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Many applications will switch from using HTTP to HTTPS when they need to send 
encrypted information such as passwords or credit card information. This is 
common with many of today's web applications. Selenium-RC supports this. 

To ensure the HTTPS site is genuine, the browser will need a security 
certificate. Otherwise, when the Selenium code is inserted between the 
browser and the application under test, the browser will recognize this as a 
security violation. It will assume some other site is masquerading as your 
application. When this occurs the browser displays security popups, and these 
popups cannot be closed using Selenium-RC. 

When dealing with HTTPS you must use a run mode that supports this and handles
the security certificate for you. You specify the run mode when you test program
initialized Selenium. 

.. TODO: copy my C# code example here. 

In Selenium-RC 1.0 beta 2 and later use \*firefox or \*iexplore for the run 
mode. In earlier versions, including Selenium-RC 1.0 beta 1, use \*chrome or 
\*iehta, for the run mode. Using these run modes, you will not need to install
any special security certificates to prevent your browser's security warning 
popups. 

In Selenium 1.0 beta 2 and later, the run modes \*firefox or \*iexplore are 
recommended. There are additional run modes of \*iexploreproxy and 
\*firefoxproxy. These are provided only for backwards compatibility and 
should not be used unless required by legacy test programs. Their use will 
present limitations with security certificate handling and with the running 
of multiple windows if your application opens additional browser windows. 

In earlier versions of Selenium-RC, \*chrome or \*iehta were the run modes that 
supported HTTPS and the handling of security popups. These were ‘experimental
modes in those versions but as of Selenium-RC 1.0 beta 2, these modes have now 
become stable, and the \*firefox and \*iexplore run modes now translate into 
the \*chrome and \*iehta modes. 

Security Certificates Explained
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Normally, your browser will trust the application you are testing, most 
likely by installing a security certificate which you already own. You can 
check this in your browser's options or internet properties (if you don't 
know your AUT's security certificate as you system administrator or lead 
developer). When Selenium loads your browser it injects code to intercept 
messages between the browser and the server. The browser now thinks 
something is trying to look like your application, but really is not a 
significant security risk. So, it responds by alerting you with popup messages. 

.. Please, can someone verify that I explained certificates correctly?—this is 
   an area I'm not certain I understand well yet. 

To get around this, Selenium-RC, (again when using a run mode that support 
this) will install its own security certificate, temporarily, onto your 
client machine in a place where the browser can access it. This tricks the 
browser into thinking it's accessing a different site from your application 
under test and effectively suppresses the security popups. 

Another method that has been used with earlier versions of Selenium is to 
install the Cybervillians security certificate provided with you selenium 
installation. Most users should no longer need to do this, however, if you are
running Selenium-RC in proxy injection mode, you may need to explicitly install this
security certificate to avoid the security popups. 

Versioning Problems 
~~~~~~~~~~~~~~~~~~~
Make sure your version of Selenium supports the version of your browser. For
example, Selenium-RC 0.92 does not support Firefox 3. At times, you may be lucky
(I was) in that it may still work. But regardless, don't forget to check which
browser versions are supported by the version of Selenium you are using. When in
doubt, use the latest release version of Selenium.

.. Santi: Mary Ann sugested We should also mention about JRE version needed by
   the server

Specifying the Path to a Specific Browser 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
You can specify to Selenium-RC a path to a specific browser. This is useful if 
you have different versions of the same browser, and you wish to use a specific
one. Also, this is used to allow your tests to run against a browser not 
directly supported by Selenium-RC. When specifying the run mode, use the 
\*custom specifier followed by the full path to the browser's executable::

   *custom <path to browser> 
 
For example 
 
.. Paul: Need an example here that works—the one I tried didn't 

