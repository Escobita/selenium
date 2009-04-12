.. _chapter02-reference:

Selenium Basics 
================

.. This is a rough draft. I have not proofread this yet, although you're 
   still welcome to add your comments. 
   I have removed some of our comments in places where I've incorporated 
   comments into the document. 
   
Getting Started -- Choosing Your Selenium Tool 
-----------------------------------------------

Most people get started with Selenium-IDE. This is what we recommend. It's 
an easy way to get familiar with Selenium commands quickly. You can develop
your first script in just a few minutes. Selenium-IDE is also very easy
to install. See the :ref:`section on Selenium-IDE <chapter03-reference>` for 
specifics.
  
You may also run your scripts from the Selenium-IDE. It's  
simple to use and is recommended for less-technical users. The IDE allows 
developing and running tests without the need for programming skills as required by Selenium -RC. The Sel-IDE can serve as an excellent way to 
train junior-level employees in test automation. If one has an understanding 
of how to conduct manual testing of a website they can easily transition to 
using the Selenium-IDE for both, running and developing tests. 

Some testing tasks are too complex though for the Selenium-IDE. When 
programming logic is required Selenium-RC must be used. For example, 
any tests requiring iteration, such as testing each element of a variable 
length list requires running the script from a programming 
language. Selenium-IDE does not support iteration or condition statements. In addition, data-driven 
testing, allowing multiple tests by varying the data using a single 
script with varied input, is often done via a test program 
developed with Selenium-RC.  *NOTE:  a "user-extension" has recently become available for supporting data-driven testing.  At the time of writing the authors have not tried to use this with Sel-IDE.*

Finally, Selenium-Core is another way of running tests. One can run test 
scripts from a web-browser using the HTML interface *TestRunner.html*. This is 
the original method for running Selenium commands. It has limitations though.
Similar to Selenium-IDE, it does not support iteration and data-driven 
testing.

Selenium-Core also cannot switch between http and https protocols. Since the 
development of Selenium-IDE and Selenium-RC, more are using these 
tools rather than Selenium-Core. At the time of writing (April 09) it 
is still available and may be convenient for some. However, the Selenium 
community is encouraging the use Selenium-IDE and RC and discouraging the use 
of Selenium-Core. Support for Selenium-Core is becoming less available and 
it may even be deprecated in a future release. 

Introducing Selenium Commands 
------------------------------

Selenium Commands -- Selenese
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Selenium provides a rich set of commands for fully testing your web-app 
in virtually any way you may imagine. The command set is often called 
*selenese*. These commands essentially create a testing language. 

In selenese, one can test the existence of UI elements based 
on their HTML tags, test for specific content, test for broken links, 
input fields, selection list options, submitting forms, and table data among other things. In addition 
Selenium commands support testing of window size, mouse position, alerts, Ajax functionality, pop up windows, event 
handling, and many other web-application features. The Command Reference (available at 
SeleniumHQ.org) lists all the available commands. 

A *command* is what tells Selenium what to do. Selenium commands come in 
three "flavors": **Actions**, **Accessors** and **Assertions**. 

* **Actions** are commands that generally manipulate the state of the 
  application. They do things like "click this link" and "select that option". 
  If an Action fails, or has an error, the execution of the current test is 
  stopped. 

  Many Actions can be called with the "AndWait" suffix, e.g. "clickAndWait". 
  This suffix tells Selenium that the action will cause the browser to make a 
  call to the server, and that Selenium should wait for a new page to load. 

* **Accessors** examine the state of the application and store the results in 
  variables, e.g. "storeTitle". They are also used to automatically generate 
  Assertions. 

* **Assertions** are like Accessors, but they verify that the state of the 
  application conforms to what is expected. Examples include "make sure the 
  page title is X" and "verify that this checkbox is checked". 

  All Selenium Assertions can be used in 3 modes: "assert", "verify", and "
  waitFor". For example, you can "assertText", "verifyText" and "waitForText". 
  When an "assert" fails, the test is aborted. When a "verify" fails, the test 
  will continue execution, logging the failure. This allows a single "assert" 
  to ensure that the application is on the correct page, followed by a bunch of 
  "verify" assertions to test form field values, labels, etc. 

  "waitFor" commands wait for some condition to become true (which can be 
  useful for testing Ajax applications). They will succeed immediately if the 
  condition is already true. However, they will fail and halt the test if the 
  condition does not become true within the current timeout setting (see the 
  setTimeout action below). 

Script Syntax 
~~~~~~~~~~~~~~
 
Selenium commands are simple, they consist of the command and two parameters. 
For example:

==========  ===========  =====
verifyText  //div//a[2]  Login 
==========  ===========  =====

The parameters are not always required. It depends on the command. In some 
cases both are required, in others one parameter is required, and still in 
others the command may take no parameters at all. Here are a couple more 
examples:
  
=================  ===========   =======================
goBackAndWait 
verifyTextPresent                Welcome to My Home Page 
type               id=phone      \(555\) 666-7066 
type               id=address1   ${myVariableAddress} 
=================  ===========   =======================
 
The command reference describes the parameter requirements for each command. 
  
Parameters vary, however they are typically 
  
* a *locator* for identifying a UI element within a page. 
* a *text pattern* for verifying or asserting expected page content 
* a *text pattern* or a selenium variable for entering text in an input field 
  or for selecting an option from an option list. 

Locators, text patterns, 
selenium variables, and the commands themselves are described in considerable detail in the section on Selenium Commands. 
  
Selenium scripts that will be run from Selenium-IDE may be stored in an HTML text file format. This consists of an HTML table with three columns. The first column is used to identify the Selenium command, the second is a target and the final column contains a value. The second and third columns may not require values depending on the chosen Selenium command, but they should be present. Each table row represents a new Selenium command. Here is an example of a test that opens a page, asserts the page title and then verifies some content on the page:
           
.. code-block:: html

   <table>
       <tr><td>open</td><td></td><td>/download/</td></tr>
       <tr><td>assertTitle</td><td></td><td>Downloads</td></tr>
       <tr><td>verifyText</td><td>//h2</td><td>Downloads</td></tr>
   </table>

Rendered as a table in a browser this would look like the following:

===========  ====  ==========
open               /download/
assertTitle        Downloads
verifyText   //h2  Downloads
===========  ====  ==========

The Selenese HTML syntax can be used to write and run tests without requiring knowledge of a programming language.  With a basic knowledge of selenese and Selenium-IDE you can quickly produce and run testcases.
   
Test Suites 
------------

.. Paul: let's show the HTML of a test suite here. then show how it looks in 
   the IDE section. 

Commonly Used Selenium Commands 
--------------------------------

.. Dave: What are these? My suggestions: open, click, waitForPageToLoad, 
   verifyText, verifyTextPresent, verifyTable, verifyTitle, verifyElementPresent

.. Santiago: I'd add type and waitForElementPresent

Summary 
--------
