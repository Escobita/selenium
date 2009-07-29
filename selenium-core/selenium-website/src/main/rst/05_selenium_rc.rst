Selenium-RC
===========

.. _chapter05-reference:

Introduction
------------
Selenium-RC is the solution for tests that need more than  simple
browser actions and linear execution. Selenium-RC uses the 
full power of programming languages to create more complex tests like reading
and writing files, querying a database, emailing test 
results.

You'll want to use Selenium-RC whenever your test requires logic
not supported by Selenium-IDE. What logic could 
this be? For example, Selenium-IDE does not directly support:

* condition statements 
* iteration 
* logging and reporting of test results
* error handling, particularly unexpected errors
* database testing
* test case grouping
* re-execution of failed tests
* test case dependency
* screenshot capture of test failures

Although these tasks are not supported by Selenium directly, all of them can be achieved
by using programming techniques with a language-specific Selenium-RC client library.

.. Note: It may be possible to perform these testing tasks by adding user 
   extensions to Selenium-IDE.  Most prefer to use Selenium-RC.  Selenium-RC
   is considerably more flexible and extensible when it comes to complex testing
   problems.

In the `Adding Some Spice to Your Tests`_ section, you'll find examples that 
demonstrate the advantages of using a programming language
for your tests.

How Selenium-RC Works
---------------------
First, we will describe how the components of Selenium-RC operate and the role each plays in running 
your test scripts.

RC Components
~~~~~~~~~~~~~
Selenium-RC components are:

* The Selenium Server which launches and kills browsers, interprets and runs the Selenese commands passed from the test program, and acts as an *HTTP proxy*, intercepting and verifying HTTP messages passed between the browser and the AUT.
* Client libraries which provide the interface between each programming language and the Selenium-RC Server.

Here is a simplified architecture diagram.... 

.. image:: images/chapt5_img01_Architecture_Diagram_Simple.png
   :class: align-center

The diagram shows the client libraries communicate with the
Server passing each Selenium command for execution. Then the server passes the 
Selenium command to the browser using Selenium-Core JavaScript commands.  The 
browser, using its JavaScript interpreter, executes the Selenium command.  This
runs the Selenese action or verification you specified in your test script.

Selenium Server
~~~~~~~~~~~~~~~
Selenium Server receives Selenium commands from your test program,
interprets them, and reports back to your program the results of
running those tests.

The RC server bundles Selenium Core and  automatically injects
it into the browser.  This occurs when your test program opens the
browser (using a client library API function).
Selenium-Core is a JavaScript program, actually a set of JavaScript
functions which interprets and executes Selenese commands using the
browser's built-in JavaScript interpreter.

The Server receives the Selenese commands from your test program
using simple HTTP GET/POST requests. This means you can use any
programming language that can send HTTP requests to automate
Selenium tests on the browser.

Client Libraries
~~~~~~~~~~~~~~~~
The client libraries provide the programming support that allows you to
run Selenium commands from a program of your own design.  There is a 
different client library for each supported language.  A Selenium client 
library provides a programming interface (API), i.e., a set of functions,
which run Selenium commands from your own program. Within each interface,
there is a programming function that supports each Selenese command.

The client library takes a Selenese command and passes it to the Selenium Server
for processing a specific action or test against the application under test 
(AUT).  The client library
also receives the result of that command and passes it back to your program.
Your program can receive the result and store it into a program variable and
reporting it as a success or failure, 
or possibly taking corrective action if it was an unexpected error. 

So to create a test program, you simply write a program that runs 
a set of Selenium commands using a client library API.  And, optionally, if 
you already have a Selenese test script created in the Selenium-IDE, you can 
*generate the Selenium-RC code*. The Selenium-IDE can translate (using its 
Export menu item) its Selenium commands into a client-driver's API function 
calls.  See the Selenium-IDE chapter for specifics on exporting RC code from 
Selenium-IDE.

.. Paul: I added the above text after this comment below was made.  I don't
   quite understand the vision behind this suggested table.  I do agree with
   the suggestion to emphasize learning the API and making clear that it 
   wrappers Selenese.  Actually, does it wrapper Selenese or pass along 
   Selenese.

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
After downloading the Selenium-RC zip file from the `downloads page`_, you'll
notice it has several sub-folders. These folders have all the components you 
need for using Selenium-RC with the programming language of your choice.

Once you've chosen a language to work with, you simply need to:

* Install the Selenium-RC Server.
* Set up a programming project using a language specific client driver.

Installing Selenium Server
~~~~~~~~~~~~~~~~~~~~~~~~~~
The Selenium-RC server is simply a Java *jar* file (*selenium-server.jar*), which doesn't
require any special installation. Just downloading the zip file and extracting the 
server in the desired directory is suffiient. 

Running Selenium Server
~~~~~~~~~~~~~~~~~~~~~~~
Before starting any tests you must start the server.  Go to the directory
where Selenium-RC's server is located and run the following from a command-line 
console.

    java -jar selenium-server.jar

This can be simplified by creating
a batch or shell executable file (.bat on Windows and .sh on Linux) containing the command
above. Then make a shortcut to that executable on your
desktop and simply double-click the icon to start the server.

For the server to run you'll need Java installed 
and the PATH environment variable correctly configured to run it from the console.
You can check that you have Java correctly installed by running the following
on a console::

       java -version

If you get a version number (which needs to be 1.5 or later), you're ready to start using Selenium-RC.

.. _`downloads page`: http://seleniumhq.org/download/
.. _`NUnit`: http://www.nunit.org/index.php?p=download

Using the Java Client Driver
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Download Selenium-RC from the SeleniumHQ `downloads page`_.
* Extract the file *selenium-java-client-driver.jar*.
* Open your desired Java IDE (Eclipse, NetBeans, IntelliJ, Netweaver, etc.)
* Create a new project.
* Add the selenium-java-client-driver.jar files to your project as references.
* Add to your project classpath the file *selenium-java-client-driver.jar*.
* From Selenium-IDE, export a script to a Java file and include it in your Java.
  project, or write your Selenium test in Java using the selenium-java-client API.
  The API is presented later in this chapter.  You can either use JUnit, or TestNg
  to run your test, or you can write your own simple main() program.  These concepts are
  explained later in this section.
* Run Selenium server from the console.
* Execute your test from the Java IDE or from the command-line.

For details on Java test project configuration, see the Appendix sections
:ref:`Configuring Selenium-RC With Eclipse <configuring-selenium-RC-eclipse-reference>` 
and 
:ref:`Configuring Selenium-RC With Intellij <configuring-selenium-RC-Intellij-reference>`.

Using the Python Client Driver 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Download Selenium-RC from the SeleniumHQ `downloads page`_ 
* Extract the file *selenium.py*
* Either write your Selenium test in Python or export
  a script from Selenium-IDE to a python file.
* Add to your test's path the file *selenium.py*
* Run Selenium server from the console
* Execute your test from a console or your Python IDE 

For details on Python client driver configuration, see the appendix 
:ref:`Python Client Driver Configuration <configuring-selenium-RC-python-reference>`.

Using the .NET Client Driver
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Download Selenium-RC from the SeleniumHQ `downloads page`_
* Extract the folder
* Download and install `NUnit`_ (
  Note:  You can use NUnit as your test engine.  If you're not familiar yet with 
  NUnit, you can also write a simple main() function to run your tests; 
  however NUnit is very useful as a test engine.)
* Open your desired .Net IDE (Visual Studio, SharpDevelop, MonoDevelop)
* Create a class library (.dll)
* Add references to the following DLLs: nmock.dll, nunit.core.dll, nunit.
  framework.dll, ThoughtWorks.Selenium.Core.dll, ThoughtWorks.Selenium.
  IntegrationTests.dll and ThoughtWorks.Selenium.UnitTests.dll
* Write your Selenium test in a .Net language (C#, VB.Net), or export
  a script from Selenium-IDE to a C# file and copy this code into the class file 
  you just created.
* Write your own simple main() program or you can include NUnit in your project 
  for running your test.  These concepts are explained later in this chapter.
* Run Selenium server from console
* Run your test either from the IDE, from the NUnit GUI or from the command line

For specific details on .NET client driver configuration with Visual Studio, see the appendix 
:ref:`.NET client driver configuration <configuring-selenium-RC-NET-reference>`. 

From Selenese to a Program
--------------------------
The primary task for using Selenium-RC is to convert your Selenese into a programming 
language.  In this section, we provide several different 
language-specific examples.

Sample Test Script
~~~~~~~~~~~~~~~~~~
Let's start with an example Selenese test script.  Imagine recording
the following test with Selenium-IDE.

.. _Google search example:

=================  =========================  ===========
open               /
type               q                          selenium rc
clickAndWait       btnG
assertTextPresent  Results * for selenium rc
=================  =========================  ===========

Note: This example would work with the Google search page http://www.google.com

Selenese as Programming Code
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Here is the test script exported (via Selenium-IDE) to each of the supported
programming languages.  If you have at least basic knowledge of an object-
oriented programming language, you will understand how Selenium 
runs Selenese commands by reading one of these 
examples.  To see an example in a specific language, select one of these buttons.

.. container:: toggled

   **In C#**:

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
                    selenium.Click("btnG");
                    selenium.WaitForPageToLoad("30000");
                    Assert.IsTrue(selenium.IsTextPresent("Results * for selenium rc"));
                }
            }
        }

.. container:: toggled

   **In Java**:

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
                selenium.click("btnG");
                selenium.waitForPageToLoad("30000");
                assertTrue(selenium.isTextPresent("Results * for selenium rc"));
          }
      }

.. container:: toggled

   **In Perl**:

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
      $sel->click_ok("btnG");
      $sel->wait_for_page_to_load_ok("30000");
      $sel->is_text_present_ok("Results * for selenium rc");

.. container:: toggled

   **In PHP**:

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
          $this->click("btnG");
          $this->waitForPageToLoad("30000");
          $this->assertTrue($this->isTextPresent("Results * for selenium rc"));
        }
      }
      ?>

.. container:: toggled

   **in Python**:

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
              sel.click("btnG")
              sel.wait_for_page_to_load("30000")
              self.failUnless(sel.is_text_present("Results * for selenium rc"))
         
          def tearDown(self):
              self.selenium.stop()
              self.assertEqual([], self.verificationErrors)

.. container:: toggled

   **in Ruby**:

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
          @selenium.click "btnG"
          @selenium.wait_for_page_to_load "30000"
          assert @selenium.is_text_present("Results * for selenium rc")
        end
      end

In the next section we'll explain how to build a test program using the generated code.

Programming Your Test
---------------------
Now we'll illustrate how to program your own tests using examples in each of the
supported programming languages.
There are essemtially two tasks.  
* Generate your script into a programming 
language from Selenium-IDE, optionally modifying the result.  
* And two, write a very simple main program that executes the generated code.  
Optionally, you can adopt a test engine platform like JUnit or TestNG for Java, 
or NUnit for .NET if you are using one of those languages.

Here, we show language-specific examples.  The language-specific APIs tend to 
differ from one to another, so you'll find a separate explanation for each.  

* `C#`_
* Java_
* Perl_
* PHP_ 
* Python_
* Ruby_ 

C#
~~

The .NET Client Driver works with Microsoft.NET.
It can be used together with any .NET testing framework 
like NUnit or the Visual Studio 2005 Team System.

Selenium-IDE automatically assumes you will use NUnit as your testing framework.
You can see this in the generated code below.  It includes the *using* statement
for the NUnit framework along with corresponding NUnit attributes identifying 
the role for each generated member function of the test class.  

Note that you will probably have to rename the test class from "NewTest" to 
something of your own choosing.  Also, you will need to change the browser-open
parameters in the statement::

    selenium = new DefaultSelenium("localhost", 4444, "*iehta", "http://www.google.com/");

The generated code will look similar to this.

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

The main program is very simple.  You can allow NUnit to manage the execution 
of your tests.  Or alternatively, you can write a simple main() program that 
instantiates the test object and runs each of the three methods, SetupTest(), 
TheNewTest(), and TeardownTest() in turn.

    
Java
~~~~
For Java, most people use Junit as the test engine.  With it, you'll save
many lines of code by allowing Junit to manage the execution of your tests.
Some development environments like Eclipse have direct support for JUnit via 
plug-ins which makes it even easier. Teaching JUnit is beyond the scope of 
this document however materials may be found online and there are publications
available.  If you are already a "java-shop" chances are your developers will 
already have experience with JUnit.

You will probably want to rename the test class from "NewTest" to something 
of your own choosing.  Also, you will need to change the browser-open 
parameters in the statement::

    selenium = new DefaultSelenium("localhost", 4444, "*iehta", "http://www.google.com/");

The Selenium-IDE generated code will look something like this.  This example 
has coments added manually for additional clarity.

.. _wrapper: http://release.seleniumhq.org/selenium-remote-control/1.0-beta-2/doc/java/com/thoughtworks/selenium/SeleneseTestCase.html

.. code-block:: java

   package com.example.tests;
   // We specify the package of our tess

   import com.thoughtworks.selenium.*;
   // This is the driver's import. You'll use this for instantiating a
   // browser and making it do what you need.

   import java.util.regex.Pattern;
   // Selenium-IDE add the Pattern module because it's sometimes used for 
   // regex validations. You can remove the module if it's not used in your 
   // script.

   public class NewTest extends SeleneseTestCase {
   // We create our Selenium test case

         public void setUp() throws Exception {
           setUp("http://www.google.com/", "*firefox");
                // We instantiate and start the browser
         }

         public void testNew() throws Exception {
              selenium.open("/");
              selenium.type("q", "selenium rc");
              selenium.click("btnG");
              selenium.waitForPageToLoad("30000");
              assertTrue(selenium.isTextPresent("Results * for selenium rc"));
              // These are the real test steps
        }
   }

Perl
~~~~

*Note: This section is not yet developed.*

PHP
~~~

*Note: This section is not yet developed.*

Python
~~~~~~
We use pyunit testing framework (the unit test module) for our tests. You should
understand how this works to better understand how to write your tests.
To completely understand pyunit, you should read its `official documentation
<http://docs.python.org/library/unittest.html>`_.

The basic test structure is:

.. code-block:: python

   from selenium import selenium
   # This is the driver's import.  You'll use this class for instantiating a
   # browser and making it do what you need.

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
           # This is the test code.  Here you should put the actions you need
           # the browser to do during your test.
            
           sel = self.selenium
           # We assign the browser to the variable "sel" (just to save us from 
           # typing "self.selenium" each time we want to call the browser).
            
           sel.open("/")
           sel.type("q", "selenium rc")
           sel.click("btnG")
           sel.wait_for_page_to_load("30000")
           self.failUnless(sel.is_text_present("Results * for selenium rc"))
           # These are the real test steps

       def tearDown(self):
           self.selenium.stop()
           # we close the browser (I'd recommend you to comment this line while
           # you are creating and debugging your tests)

           self.assertEqual([], self.verificationErrors)
           # And make the test fail if we found that any verification errors
           # were found

Ruby
~~~~

*Note: This section is not yet developed.*

Learning the API
----------------
The Selenium-RC API uses naming conventions 
that, assuming you understand Selenese, much of the interface  
will be self-explanatory. Here, however, we explain the most critical and 
possibly less obvious, aspects of the API.

Starting the Browser 
~~~~~~~~~~~~~~~~~~~~

.. container:: toggled

   **In C#**:

   .. code-block:: c#

      selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://www.google.com/");
      selenium.Start();

.. container:: toggled

   **In Java**:

   .. code-block:: java

      setUp("http://www.google.com/", "*firefox");

.. container:: toggled

   **In Perl**:

   .. code-block:: perl

      my $sel = Test::WWW::Selenium->new( host => "localhost", 
                                          port => 4444, 
                                          browser => "*firefox", 
                                          browser_url => "http://www.google.com/" );

.. container:: toggled

   **In PHP**:

   .. code-block:: php

      $this->setBrowser("*firefox");
      $this->setBrowserUrl("http://www.google.com/");

.. container:: toggled

   **In Python**:

   .. code-block:: python

      self.selenium = selenium("localhost", 4444, "*firefox",
                               "http://www.google.com/")
      self.selenium.start()

.. container:: toggled

   **In Ruby**:

   .. code-block:: ruby

      if $selenium
        @selenium = $selenium
      else
        @selenium = Selenium::SeleniumDriver.new("localhost", 4444, "*firefox", "http://www.google.com/", 10000);
        @selenium.start

Each of these examples opens the browser and represents that browser 
by assigning a "browser instance" to a program variable.  This 
browser variable is then used to call methods from the browser. 
These methods execute the Selenium commands, i.e. like *open* or *type* or the *verify* 
commands.

The parameters required when creating the browser instance
are: 

**host**
    Specifies the IP address of the computer where the server is located. Usually, this is
    the same machine as where the client is running, so in this case *localhost* is passed.  In some clients this is an optional parameter.
	
**port**
    Specifies the TCP/IP socket where the server is listening waiting
    for the client to establish a connection.  This also is optional in some
    client drivers.
	
**browser**
    The browser in which you want to run the tests. This is a required 
    parameter.
	
**url**
    The base url of the application under test. This is required by all the
    client libs and is integral information for starting up the browser-proxy-AUT communication.

Note that some of the client libraries require the browser to be started explicitly by calling
its *start()* method.

Running Commands 
~~~~~~~~~~~~~~~~
Once you have the browser initialized and assigned to a variable (generally
named "selenium") you can make it run Selenese commands by calling the respective 
methods from the browser variable. For example, to call the *type* method
of the selenium object::

    selenium.type("field-id","string to type")

In the background the browser will actually perform a *type* operation, 
essentially identical to a user typing input into the browser, by  
using the locator and the string you specified during the method call.

Retrieving and Reporting Results
--------------------------------
Each programming language has its own testing framework which is used to
run the tests. Every one of them has its own way of reporting the results
and you'll find third-party libraries specially created for reporting
test results in different formats such as HTML or PDF.

**Generating Test Reports for Java client driver:**
    

- If Selenium Test cases are developed using JUnit then JUnit Report can be used
  to generate test reports. Refer to `JUnit Report`_ for specifics.

.. _`JUnit Report`: http://ant.apache.org/manual/OptionalTasks/junitreport.html

- If Selenium Test cases are developed using TestNG then no external task 
  is required to generate test reports. The TestNG framework generates an 
  HTML report which list details of tests. See `TestNG Report`_ for more.

.. _`TestNG Report`: http://testng.org/doc/documentation-main.html#test-results

- ReportNG is a HTML reporting plug-in for the TestNG framework. 
  It is intended as a replacement for the default TestNG HTML report. 
  ReportNG provides a simple, colour-coded view of the test results. 
  See `ReportNG`_ for more. 
  
.. _`ReportNG`: https://reportng.dev.java.net/  

- Also, for a very nice summary report try using TestNG-xslt. 
  TestNG-xslt Report looks like this.

  .. image:: images/chapt5_TestNGxsltReport.png

  See `TestNG-xslt`_ for more.

.. _`TestNG-xslt`: http://code.google.com/p/testng-xslt/

- Logging Selenium can also be used to generate reports for the Java client driver.  
  Logging Selenium extends the Java client driver to add logging ability. 
  Please refer to `Logging Selenium`_.
    
.. _`Logging Selenium`: http://loggingselenium.sourceforge.net/index.html

**Generating Test Reports for Python Client driver:**

- When using Python Client Driver then HTMLTestRunner can be used to
  generate a Test Report. See `HTMLTestRunner`_.
    
.. _`HTMLTestRunner`: http://tungwaiyip.info/software/HTMLTestRunner.html

**Generating Test Reports for Ruby Client driver:**

- If RSpec framework is used for writing Selenium Test Cases in Ruby
  then its HTML report can be used to generate test report.
  Refer to `RSpec Report`_ for more.

.. _`RSpec Report`: http://rspec.info/documentation/tools/rake.html

.. note:: If you are interested in a language independent log of what's going
   on, take a look at `Selenium RC Logging`_

Adding Some Spice to Your Tests
-------------------------------
Now you'll understand why you needed Selenium-RC and you just couldn't stay
strictly with Selenium-IDE. We will give you guidance here on things that can
only be done using a programming language.

You find, as you transition from running simple tests of page elements, to 
building tests of dynamic functionality involving multiple web-pages and 
varying data that you will require programming logic for verifying expected 
test results.  Basically, the Selenium-IDE does not support iteration and 
condition statements.  You will find you can do some simple condition 
statements by embedding javascript in your Selenese parameters, however 
iteration is impossible, and many conditions simply will need to be done in a 
programming language.  In addition, you may need to use exception-handling for
error recovery.  For these reasons and others, we have written this section
to give you ideas on how to leverage common programming techniques to
give you greater 'verification power' in your automated testing.

The examples in this section are written
in a single programming language--the idea being that you understand the concept and be
able to translate it to the language of your choice.  If you have some basic knowledge
of object-oriented programming you shouldn't have difficulty making use of this section.

Iteration
~~~~~~~~~
Iteration is one of the most common things people need to do in their tests.
For example, you may want to to execute a search multiple times.  Or, perhaps for
verifying your test results you need to process a "result set" returned from a database.

If we take the same `Google search example`_ we've been using, it's not so crazy to 
check that all the Selenium tools appear in the search
results. This test could use the Selenese:

=================  ===========================  =============
open               /
type               q                            selenium rc
clickAndWait       btnG
assertTextPresent  Results * for selenium rc
type               q                            selenium ide
clickAndWait       btnG 
assertTextPresent  Results * for selenium ide
type               q                            selenium grid
clickAndWait       btnG 
assertTextPresent  Results * for selenium grid
=================  ===========================  =============

The code has been triplicated to run the same steps 3 times.  No half-way
decent software person would want to do it this way, it makes
managing the code much more difficult.

By using a programming language, we can iterate over a list and run 
the search this way. 

**In C#:**   
   
.. code-block:: c#

   // Collection of String values.
   String[] arr = {"ide", "rc", "grid"};    
        
   // Execute For loop for each String in 'arr' array.
   foreach (String s in arr) {
       sel.open("/");
       sel.type("q", "selenium " +s);
       sel.click("btnG");
       sel.waitForPageToLoad("30000");
       assertTrue("Expected text: " +s+ " is missing on page."
       , sel.isTextPresent("Results * for selenium " + s));
    }

Condition Statements
~~~~~~~~~~~~~~~~~~~~
A common problem encountered while running Selenium tests occurs when an 
expected element is not available on page.  For example, when running the 
following line:

.. code-block:: java
   
   selenium.type("q", "selenium " +s);
   
If element 'q' happens to be unavailable on the page then an exception is
thrown:

.. code-block:: java

   com.thoughtworks.selenium.SeleniumException: ERROR: Element q not found

This can cause your test to abort.  Some types of tests may want that.  But
often that is not desireable as your test script has many other subsequent tests
to perform.

A better approach would be to first validate if the element is really present
and then take alternatives when it it is not:

**In Java:**

.. code-block:: java
   
   // If element is available on page then perform type operation.
   if(selenium.isElementPresent("q")) {
       selenium.type("q", "Selenium rc");
   } else {
       Reporter.log("Element: " +q+ " is not available on page.")
   }
   
Herein *Reporter* is API in TestNG framework. One can log exceptions using 
the API of framework on which Sel Test Cases are built. Advantage of this 
approach is to be able to continue with test execution even if *less* 
important elements are not available on page.

By just using a simple *if* condition, we can do interesting things. Think of
the possibilities!

Data Driven Testing
~~~~~~~~~~~~~~~~~~~
So, the iteration_ idea seems cool. Let's improve this by allowing the users to
write an external text file from which the script should read the input data,
search and assert its existence.

**In Python:**

.. code-block:: python

   # Collection of String values
   source = open("input_file.txt", "r")
   values = source.readlines()
   source.close()
   # Execute For loop for each String in the values array
   for search in values:
       sel.open("/")
       sel.type("q", search)
       sel.click("btnG")
       sel.waitForPageToLoad("30000")
       self.failUnless(sel.is_text_present("Results * for " + search))

Why would we want a separate file with data in it for our tests?  One 
important method of testing concerns running the same test repetetively with 
differnt data values.  This is called *Data Driven Testing* and is a very 
common testing task.  Test automation tools, Selenium included, generally 
handle this as it's often a common reason for building test automation to 
support manual testing methods.

The Python script above opens a text file.  This file contains a different search
string on each line. The code then saves this in an array of strings, and at last,
it's iterating over the strings array and doing the search and assert on each.

This is a very basic example of what you can do, but the idea is to show you
things that can easily be done with either a programming or scripting 
language when they're difficult or even impossible to do using Selenium-IDE.

Refer to `Selnium RC wiki`_ for examples on reading data from spread sheet or using
data provider capabilities of TestNG with java client driver.

.. _`Selnium RC wiki`: http://wiki.openqa.org/pages/viewpage.action?pageId=21430298

Error Handling
~~~~~~~~~~~~~~

*Note: This section is not yet developed.*

A quick note though--recognize that your programming language's exception-
handling support can be used for error handling and recovery.

.. TODO: Complete this... Not sure if the scenario that I put is the best example to use
.. Then, what if google.com is down at the moment of our tests? Even if that sounds
   completely impossible. We can create a recovery scenario for that test. We can
   make our tests to wait for a certain amount of time and try again:

.. The idea here is to use a try-catch statement to grab a really unexpected
   error.

Database Validations
~~~~~~~~~~~~~~~~~~~~~

Since you can also do database queries from your favorite programming 
language, assuming you have database support functions, why not using them
for some data validations/retrieval on the Application Under Test?

Consider example of Registration process where in registered email address
is to be retrieved from database. Specific cases of establishing DB connection 
and retrieving data from DB would be:

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

Executing java script 
~~~~~~~~~~~~~~~~~~~~~

java script comes very handy in exercising application which is not directly supported
by selenium. **getEval** method of selenium API can be used to execute java script from
selenium RC. 

Consider an application having check boxes with no static identifiers. 
In this case one could evaluate js from selenium RC to get ids of all 
check boxes and then exercise them. 

.. code-block:: java
   
   public static String[] getAllCheckboxIds () { 
		String script = "var inputId  = new Array();";// Create array in java script.
		script += "var cnt = 0;"; // Counter for check box ids.  
		script += "var inputFields  = new Array();"; // Create array in java script.
		script += "inputFields = window.document.getElementsByTagName('input');"; // Collect input elements.
		script += "for(var i=0; i<inputFields.length; i++) {"; // Loop through the collected elements.
		script += "if(inputFields[i].id !=null " +
		"&& inputFields[i].id !='undefined' " +
		"&& inputFields[i].getAttribute('type') == 'checkbox') {"; // If input field is of type check box and input id is not null.
		script += "inputId[cnt]=inputFields[i].id ;" + // Save check box id to inputId array.
		"cnt++;" + // increment the counter.
		"}" + // end of if.
		"}"; // end of for.
		script += "inputId.toString();" ;// Convert array in to string.			
		String[] checkboxIds = selenium.getEval(script).split(","); // Split the string.
		return checkboxIds;
    }

To count number of images on a page:

.. code-block:: java
   
   selenium.getEval("window.document.images.length;");
	
Remember to use window object in case of dom expressions as by default selenium
window is referred and not the test window.
   
How the Server Works
--------------------
.. note:: This topic tries to explain the technical implementation behind 
   Selenium-RC. It's not fundamental for a Selenium user to know this, but 
   could be useful for understanding some of the problems you can find in the
   future.
   
To understand in detail how Selenium-RC Server works  and why it uses proxy injection
and heightened privilege modes you must first understand `the same origin policy`_.
   
The Same Origin Policy
~~~~~~~~~~~~~~~~~~~~~~
The main restriction that Selenium's architecture has faced is the 
Same Origin Policy. This security restriction is applied by every browser
in the market and its objective is to ensure that a site's content will never
be accessible by a script from other site.

If this were possible, a script placed on any website you open, would 
be able to read information on your bank account if you had the account page
opened on other tab. Which is also called XSS (Cross-site Scripting).

To work under that policy. Selenium-Core (and its JavaScript commands that
make all the magic happen) must be placed in the same origin as the Application
Under Test (same URL). This has been the way Selenium-Core was first
used and implemented (by deploying Selenium-Core and the set of tests inside
the application's server), but this was a requirement that not all the projects 
could meet and Selenium Developers had to find an alternative that would allow 
testers to use Selenium to test site where they didn't have the possibility to
deploy their code. 

.. note:: You can find additional information about this topic on Wikipedia
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

.. image:: images/chapt5_img02_Architecture_Diagram_1.png
   :class: align-center

As a test suite starts in your favorite language, the following happens:

1. The client/driver establishes a connection with the selenium-RC server.
2. Selenium-RC server launches a browser (or reuses an old one) with an URL 
   that will load Selenium-Core in the web page.
3. Selenium-Core gets the first instruction from the client/driver (via another 
   HTTP request made to the Selenium-RC Server).
4. Selenium-Core acts on that first instruction, typically opening a page of the
   AUT.
5. The browser receives the open request and asks for the website's content to
   the Selenium-RC server (set as the HTTP proxy for the browser to use).
6. Selenium-RC server communicates with the Web server asking for the page and once
   it receives it, it sends the page to the browser masking the origin to look
   like the page comes from the same server as Selenium-Core (this allows 
   Selenium-Core to comply with the Same Origin Policy).
7. The browser receives the web page and renders it in the frame/window reserved
   for it.
   
Heightened Privileges Browsers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This workflow on this method is very similar to Proxy Injection but the main
difference is that the browsers are launched in a special mode called *Heightened
Privileges*, which allows websites to do things that are not commonly permitted
(as doing XSS_, or filling file upload inputs and pretty useful stuff for 
Selenium). By using these browser modes, Selenium Core is able to directly open
the AUT and read/interact with its content without having to pass the whole AUT
through the Selenium-RC server.

Here is the architectural diagram. 

.. image:: images/chapt5_img02_Architecture_Diagram_2.png
   :class: align-center

As a test suite starts in your favorite language, the following happens:

1. The client/driver establishes a connection with the selenium-RC server.
2. Selenium-RC server launches a browser (or reuses an old one) with an URL 
   that will load Selenium-Core in the web page.
3. Selenium-Core gets the first instruction from the client/driver (via another 
   HTTP request made to the Selenium-RC Server).
4. Selenium-Core acts on that first instruction, typically opening a page of the
   AUT.
5. The browser receives the open request and asks the Web Server for the page.
   Once the browser receives the web page, renders it in the frame/window reserved
   for it.
   
Server Command Line options
---------------------------
When the server is launched, some command line options can be used to change the
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


Proxy Configuration
~~~~~~~~~~~~~~~~~~~
If one is behind HTTP proxy which requires authentication then one should 
set up http.proxyHost, http.proxyPort, http.proxyUser and http.proxyPassword
like this - 

.. code-block:: bash

   $ java -jar selenium-server.jar -Dhttp.proxyHost=proxy.com -Dhttp.proxyPort=8080 -Dhttp.proxyUser=username -Dhttp.proxyPassword=password


Multi-Window Mode
~~~~~~~~~~~~~~~~~
Before 1.0, Selenium by default ran the application under test in a sub frame 
which looks like this:

.. image:: images/chapt5_img26_single_window_mode.png
   :class: align-center

Unfortunately, some apps don't run properly in a sub frame, preferring to be 
loaded into the top frame of the window. That's why we made the multi Window 
mode (the new default since Selenium 1.0). Using this you can make your 
application under test run in a separate window rather than in the default 
frame.

.. image:: images/chapt5_img27_multi_window_mode.png
   :class: align-center

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

Specifying the Firefox Profile
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. TODO: Better describe how Selenium handles Firefox profiles (it creates,
   uses and then deletes sandbox profiles unless you specify special ones)
   
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

.. warning::  Be sure to put your profile in a new folder separate from the default!!! 
   The Firefox profile manager tool will delete all files in a folder if you 
   delete a profile, regardless of whether they are profile files or not. 
   
More information about Firefox profiles in `Mozilla's Knowledge Base`_

.. _Mozilla's KNowledge Base: http://support.mozilla.com/zh-CN/kb/Managing+profiles

.. _html-suite:

Run Selenese Tests Directly from the Server Using -htmlSuite
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

Selenium RC Logging
~~~~~~~~~~~~~~~~~~~

Server-Side Logs
++++++++++++++++
When launching selenium server then **-log** option can be used to direct 
lots of valuable debugging information to a text file.

.. code-block:: bash

   java -jar selenium-server.jar -log selenium.log
   
This log file is more verbose than the standard console logs (it includes DEBUG 
level logging messages). The log file also includes the logger name, and the ID
number of the thread that logged the message. For example:   

.. code-block:: bash

   20:44:25 DEBUG [12] org.openqa.selenium.server.SeleniumDriverResourceHandler - 
   Browser 465828/:top frame1 posted START NEW
   
Format is 

.. code-block:: bash

   TIMESTAMP(HH:mm:ss) LEVEL [THREAD] LOGGER - MESSAGE
   
This message may be multiline.

Browser-Side Logs
+++++++++++++++++
JavaScript on the browser side (Selenium Core) also logs important messages; 
in many cases, these can be more useful to the end-user than the regular Selenium 
Server logs. To access browser-side logs, one will need to pass the **-browserSideLog**
argument to the Selenium Server, like this:

.. code-block:: bash

   java -jar selenium-server.jar -browserSideLog
   
**-browserSideLog** can also be combined with the **-log** argument, to log 
browserSideLogs (as well as all other DEBUG level logging messages) to a file.

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
When first getting started with Selenium-RC there's a few potential problems
that are commonly encountered.  We present them along with their solutions here.

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
components. This should not normally happen when your operating system has 
typical networking and TCP/IP settings. If you continue to have trouble, try 
a different computer.  

You can also use common networking tools like *ping*, *telnet*, *ipconfig/ifconfig*
(on windows), etc to ensure you first have a valid network connection.  
Also, if you're trying to 
connect to the Selenium Server on a remote machine try running it locally first and
verifying you can get a connection using "localhost" as your connection parameter.  
 
Unable to Load the Browser 
~~~~~~~~~~~~~~~~~~~~~~~~~~

Ok, not a very friendly error, sorry!  But if the Selenium Server cannot load the browser 
you will probably see this error.
 
:: 

    (500) Internal Server Error 

This error seems to occur when Selenium-RC cannot load the browser.

::

    500 Internal Server Error 

(using .NET and XP Service Pack 2) 

This could be caused by

* Firefox (prior to Selenium 1.0) cannot start because the browser is already open and you did 
  not specify a separate profile. 
* The run mode you're using doesn't match any browser on your machine.  Check the parameters you 
  passed to Selenium when you program opens the browser. 
* You specified the path to the browser explicitly (using "\*custom"--see above) but the path is 
  incorrect. 

Selenium Cannot Find the AUT 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If your test program starts the browser successfully, but the browser doesn't
display the website you're testing, the most likely cause is your test 
program is not using the correct URL. 

This can easily happen. When you use Selenium-IDE to export you script,
it inserts a dummy URL. You must manually change the URL to the correct one
for your application to be tested. 

Firefox Refused Shutdown While Preparing a Profile 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This most often occurs when your run your Selenium-RC test program against Firefox,
but you already have a Firefox browser session running and, you didn't specify
a separate profile when you started the Selenium Server. The error from the 
test program looks like this::

    Error:  java.lang.RuntimeException: Firefox refused shutdown while 
    preparing a profile 

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
install the Cybervillians security certificate provided with your Selenium 
installation. Most users should no longer need to do this however, if you are
running Selenium-RC in proxy injection mode, you may need to explicitly install this
security certificate to avoid the security popup. 

Versioning Problems 
~~~~~~~~~~~~~~~~~~~
Make sure your version of Selenium supports the version of your browser. For
example, Selenium-RC 0.92 does not support Firefox 3. At times, you may be lucky
(I was) in that it may still work. But regardless, don't forget to check which
browser versions are supported by the version of Selenium you are using. When in
doubt, use the latest release version of Selenium.

.. Santi: Mary Ann suggested We should also mention about JRE version needed by
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
 
.. TODO:  we need to add an example here.


Error message: "(Unsupported major.minor version 49.0)" while starting server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This message indicates that one is not using the correct version of Java. 
The Selenium Server requires Java 1.5 or higher; this error appears when 
one uses Java 1.4 or backward version instead of 1.5. 

Try running this from the command line:

.. code-block:: bash

   java -version

You should see a brief message telling you what version of Java is installed,
like this:

.. code-block:: bash

   java version "1.5.0_07"
   Java(TM) 2 Runtime Environment, Standard Edition (build 1.5.0_07-b03)
   Java HotSpot(TM) Client VM (build 1.5.0_07-b03, mixed mode)

If you see a lower version number instead, you may need to install a newer 
version of the JRE, or you may need to add it to your PATH environment variable.

I get a 404 error when running the getNewBrowserSession command!
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you're getting a 404 error while attempting to open a page on 
"http://www.google.com/selenium-server/", then it must be because the Selenium
Server was not correctly configured as a proxy. "selenium-server" directory 
doesn't really exist on google.com; it only appears to exist when the proxy is 
properly configured. Proxy Configuration highly depends on way browser is 
launched with \*firefox, \*iexplore, \*opera, or \*custom.

    * \*iexplore: If browser is launched using \*iexplore, there are a number
      of likely possibilities.Selenium Server attempts to configure the global
      proxy settings in Internet Options control panel; one needs to make 
      sure that those are correctly configured when Selenium Server launches 
      the browser. Try looking at your Internet Options control panel. 
      Click on the "Connections" tab and click on "LAN Settings". 
      
          - If you need to use a proxy to access the application you want to test,
            you'll need to start Selenium Server with "-Dhttp.proxyHost"; 
            see the `Proxy Configuration`_ for more details.
          - You may also try configuring your proxy manually and then launching
            browser with \*custom, or with \*iehta browser launcher.
            
    * \*custom: When using \*custom, it's up to you to configure the proxy correctly
      (manually). If you forgot to do this, or if you configured the proxy incorrectly, 
      you'll get a 404 error. Double-check that you've configured your proxy 
      settings correctly. 
      One way to check whether one has configured proxy correctly is to attempt 
      to intentionally configure browser incorrectly. Try configuring browser 
      to use the wrong proxy server hostname, or the wrong port. 
      If one had successfully configured browser's proxy settings incorrectly, 
      then browser will be unable to connect to the Internet, which is one way 
      to make sure that one is adjusting the relevant settings. 
      
    * others (\*firefox, \*opera): For the others browsers, we automatically hard-code
      the proxy for you, and don't know of any known issues with this functionality.
      If you're encountering 404 errors in the tutorial while using these browser 
      launchers, post them to user forums.
      
Why am I getting a permission denied error?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
The most common reason for this error is that your session is attempting to violate
the same-origin policy by crossing domain boundaries (e.g., accesses a page from 
http://domain1 and then accesses a page from http://domain2) or switching protocols 
(moving from http://domainX to https://domainX).
This error can also sometimes occur when JavaScript attempts to look at objects 
which are not yet available (before the page has completely loaded), or tries to 
look at objects which are no longer available (after the page has started 
to be unloaded). This kind of pitfall is most typically encountered with AJAX pages
which are working with sections of a page or subframes that load and/or reload 
independently of the larger page. For this category of problem, it is usual
that the error is intermittent. Often it is impossible to reproduce the problem 
with a debugger running because the trouble stems from race conditions which 
settle down when the debugger's overhead is added to the system.
This is covered in some detail in the tutorial. Make sure you read the section 
about the `The Same Origin Policy`_, `Proxy Injection`_ carefully. 


Executing tests with different configurations of Browser
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Normally Selenium RC automatically configures browser, but if one launches 
browser using "\*custom" as browser launcher, one can force Selenium RC
to launch the browser as-is, without changing configuration. 
(Note that this is also the way one launches other browsers that Selenium RC 
doesn't yet explicitly support.)

For example, one can launch Firefox with a custom configuration like this:

.. code-block:: bash

   cmd=getNewBrowserSession&1=*custom c:\Program Files\Mozilla Firefox\firefox.exe&2=http://www.google.com

Note that when launching the browser in this way, one will have to manually 
configure browser to use the Selenium Server as a proxy. (Normally this just 
means opening your browser preferences and specifying "localhost:4444" as 
an HTTP proxy, but instructions for this can differ radically from browser to 
browser, so consult your browser's documentation for details.)

Beware that Mozilla browsers can be a little fidgety about how they start and stop. 
One may need to set the MOZ_NO_REMOTE environment variable to make Mozilla browsers 
behave a little more predictably. Unix users should avoid launching browser using 
a shell script; always prefer to use the binary executable (e.g. firefox-bin) directly.

How to block pop up windows?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
There are several kinds of "pop-ups" that one can get during a Selenium test;
each of them need to be addressed differently.

    * HTTP basic authentication dialogs: These dialogs prompt for a 
      username/password to login to the site. To login to a site that requires 
      HTTP basic authentication, use a username and password in the URL, as 
      described in `RFC 1738`_, like this: open("http://myusername:myuserpassword@myexample.com/blah/blah/blah").
      
.. _`RFC 1738`: http://tools.ietf.org/html/rfc1738#section-3.1

    * SSL certificate warnings: Selenium RC automatically attempts to spoof SSL 
      certificates when it is enabled as a proxy; see more details about this 
      in the tutorial section on HTTPS. If your browser is configured correctly,
      you should never see SSL certificate warnings, but you may need to 
      configure your browser to trust our dangerous "CyberVillains" SSL certificate 
      authority. (The tutorial explains how to do this.)

    * modal JavaScript alert/confirmation/prompt dialogs: Selenium tries to conceal
      those dialogs from you (by replacing window.alert, window.confirm and 
      window.prompt) so they won't stop the execution of your page. If you're 
      actually seeing an alert pop-up, it's probably because it fired during 
      the page load process, which is usually too early for us to protect the page.
      
      
On Linux, why isn't my Firefox browser session closing?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~      
On Unix/Linux one needs to invoke "firefox-bin" directly, so make sure that
executable is on the path. If one is  forced to execute Firefox through a 
shell script, when it comes time to kill the browser Selenium RC will kill
the shell script, leaving the browser running.   
If necessary, one can specify the path to firefox-bin directly, like this:
      
.. code-block:: bash      
      
   cmd=getNewBrowserSession&1=*firefox /usr/local/firefox/firefox-bin&2=http://www.google.com

Firefox \*chrome doesn't work with custom profile
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Check Firefox profile folder -> prefs.js -> user_pref("browser.startup.page", 0);
Comment this line like this: "//user_pref("browser.startup.page", 0);" and try again.

How can I avoid using complex xpath expressions to my test?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If the elements in HTML (button, table, label, etc) have element IDs, 
then one can reliably retrieve all elements without ever resorting
to xpath. These element IDs should be explicitly created by the application.
But non-descriptive element ID (i.e. id_147) tends to cause two problems: 
first, each time the application is deployed, different element ids could be generated. 
Second, a non-specific element id makes it hard for automation testers to keep 
track of and determine which element ids are required for testing.

You might consider trying the `UI-Element`_ extension in this situation.

.. _`UI-Element`: http://wiki.openqa.org/display/SIDE/Contributed+Extensions+and+Formats#ContributedExtensionsandFormats-UIElementLocator


Is it ok to load a custom pop-up as the parent page is loading (i.e., before the parent page's javascript window.onload() function runs)?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
No. Selenium relies on interceptors to determine window names as they are being loaded.
These interceptors work best in catching new windows if the windows are loaded AFTER 
the onload() function. Selenium may not recognize windows loaded before the onload function.


How can I wait for an element in AJAX driven application?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
In AJAX driven web applications; data is retrieved from server with out refreshing 
the page. Usage of *waitForPageToLoad* would not work as page is not actually 
loaded. Pausing the test execution for certain period of time is also not a good 
approach as web element might appear late or earlier than stipulated period; leading
to test failures. A better approach would be to wait for element for predefined 
period and then continue execution as soon as element is found.

For ex. Consider a page which brings a link (link=ajaxLink) on click of a button 
on page (with out refreshing the page)
This could be handled employing a for loop in selenium. 

.. code-block:: bash
   
   // Loop initialization.
   for (int second = 0;; second++) {
	
	// If loop is reached 60 seconds then break the loop.
	if (second >= 60) break;
	
	// Search for element "link=ajaxLink" and if available then break loop.
	try { if (selenium.isElementPresent("link=ajaxLink")) break; } catch (Exception e) {}
	
	// Pause for 1 second.
	Thread.sleep(1000);
	
   } 

   
   .. Santi: must recheck if all the topics here: 
   http://seleniumhq.org/documentation/remote-control/troubleshooting.html
   are covered.

Problems With Verify Commands 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you export your tests from Selenium-IDE, you may find yourself getting
empty verify strings from your tests (depending on the programming language
used).

*Note: This section is not yet developed.*

.. Santi: I'll put some info from 
   http://clearspace.openqa.org/message/56908#56908 (we should write an example
   for all the languages...)

.. Paul:  Are we sure this is still a problem?  I've never encountered it.

.. I'll investigate into this, I only use python and using that client it's failing

Safari and MultiWindow Mode
~~~~~~~~~~~~~~~~~~~~~~~~~~~

*Note: This section is not yet developed.*

.. Santi: we will have to explain the following:
   http://clearspace.openqa.org/community/selenium/blog/2009/02/24/safari-4-beta#comment-1514
   http://jira.openqa.org/browse/SEL-639

Firefox on Linux 
~~~~~~~~~~~~~~~~
On Unix/Linux, versions of Selenium before 1.0 needed to invoke "firefox-bin" 
directly, so if you are using a previous version, make sure that the real 
executable is on the path. 

On most Linux distributions, the real *firefox-bin* is located on::

   /usr/lib/firefox-x.x.x/ 

Where the x.x.x is the version number you currently have. So, to add that path 
to the user's path. you will have to add the following to your .bashrc file:

.. code-block:: bash

   export PATH="$PATH:/usr/lib/firefox-x.x.x/"

.. This problem is caused because in linux, Firefox is executed through a shell
   script (the one located on /usr/bin/firefox), when it comes the time to kill
   the browser Selenium-RC will kill the shell script, leaving the browser 
   running.  Santi: not sure if we should put this here...

If necessary, you can specify the path to firefox-bin directly in your test,
like this::

   "*firefox /usr/lib/firefox-x.x.x/firefox-bin"

IE and Style Attributes
~~~~~~~~~~~~~~~~~~~~~~~
If you are running your tests on Internet Explorer and you are trying to locate
elements using their `style` attribute, you're definitely in trouble.
Probably a locator like this::

    //td[@style="background-color:yellow"]

Would perfectly work in Firefox, Opera or Safari but it won't work on IE. 
That's because the keys in  `@style` are interpreted as uppercase once the page
is parsed by IE. So, even if the source code is in lowercase, you should use::

    //td[@style="BACKGROUND-COLOR:yellow"]

This is a problem if your test is intended to work on multiple browsers, but
you can easily code your test to detect the situation and try the alternative
locator that only works in IE.
   

Where should I go if I have questions about Selenium RC that aren't answered in this FAQ?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Try our `user forums`_

.. _`user forums`: http://seleniumhq.org/support/
