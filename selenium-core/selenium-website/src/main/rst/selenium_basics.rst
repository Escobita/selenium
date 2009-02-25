Selenium Basics 
================

.. contents::

.. This is a rough draft. I have not proofread this yet, although you're 
   still welcome to add your comments. 
   I have removed some of our comments in places where I've incorporated 
   comments into the document. 
   
Getting Started -- Choosing Your Selenium Tool 
-----------------------------------------------

Most people get started with the Selenium IDE. This is what we recommend. It 
is an easy way to get familiar with Selenium commands quickly. You can 
develop your first example script in just a few minutes.  The Selenium-IDE is 
also very easy to install.  See the section on Selenium-IDE for more:

.. TODO: Link the final selenium IDE section here ^
  
You may also run your scripts from the Selenium-IDE. The Selenium-IDE is very 
simple to use and is recommended for less-technical users. It can server as a 
way to develop scripts and run tests without the need for programming skills 
as required by Selenium -RC. The Sel-IDE can serve as an excellent way to 
train junior-level employees in test automation. If one has an understanding 
of how to conduct manual testing of a website they can easily transition to 
using the Selenium-IDE for both, running and developing tests. 

Some testing tasks are too complex though for the Selenium-IDE. When 
programming logic is required, then Selenium-RC must be used. For example, 
any tests requiring iteration, such as testing each element of a variable 
length list or table will require running the script from a programming 
language. Selenium-IDE does not support iteration. In addition, data-driven 
testing, allowing multiple tests by varying the data but using a single 
script which varies the input, is often performed via a test program 
developed with Selenium-RC.  

Finally, Selenium Core is another way of running tests. One can run test 
scripts from a web-browser using the html interface TestRunner.html.  This is 
the original method for running Selenium commands.  It has limitations though.
Similar to Selenium-IDE, it does not support iteration and data-driven 
testing.  

.. note: the Selenium-IDE may now support data-driven testing through a 
   user-extension need to try out the new extension recently made available).

It also cannot switch between http and https protocols.  Since the 
development of Selenium-IDE and Selenium-RC, more and more are using these 
tools with few still using Selenium-Core.  At the time of writing (Feb 09) it 
is still available and may be convenient for some. However, the Selenium 
community is encouraging the use Selenium-IDE and RC and discouraging the use 
of Selenium-Core.  Support for Selenium-Core is becoming less h available and 
it may be deprecated in a future 
release.  

.. Santiago: should this part about Selenium-Core be in the docs?? As I read 
   before, Selenium Core was in a deprecation rute. 

.. Paul: Not sure yet, if we should just give a quick mention to Core or 
   ignore it entirely.  I included just for now but may delete it later.  This 
   is something we should all agree on, perhaps with the Dev's 
   input.  

Introducing Selenium Commands 
------------------------------

Script Syntax 
~~~~~~~~~~~~~~

Selenium Commands -- Selenese
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Selenium provides a rich set of commands for fully testing your web-app 
in virtually any way you may imagine.  This command set is often called 
selenese. These commands essentially create a testing language. 

In selenese, certainly, one can test the existence of UI elements, based 
on their HTML tags, can test for specific content, can test for broken links, 
input fields, selection list, submitting forms, and table data.  In addition 
Selenium commands support testing of window size (is true? must check the 
reference), mouse position, alerts, Ajax functionality, pop up windows, event 
handling, and many other features.  The Command Reference (available at 
SeleniumHQ.org) lists all the available commands. 
  
.. Dave: My understanding was that 'Selenese' was the HTML language for 
   writing tests, which used the core selenium commands in a much more direct 
   way than other languages. If we're reducing references to 'Core' then 
   perhaps this is an attempt to re brand 'Core' commands as 'Selenese' 
   commands, in which case the HTML method of writing tests is simply a quick 
   and direct way of writing tests without the need for an interpreter. Does 
   anyone else have a different understanding of what we mean when we say '
   Selenese'? I can see this topic being a simple introduction to the 
   Selenium Reference, with the HTML method of writing tests covered elsewhere.

.. Paul: Yes, we can present the Selenese separately from the HTML Syntax.  
   As the person that coined 'Selenese' I meant the wire language.  Commands 
   and replies over a plain text connection in the style that the RC drivers 
   have been working with for years.  Since then, it is true that 'Selenese' 
   has been re-appropriated to mean something else (by Dan and Nelson)

.. Dave: This is taken from the command reference - I can try to reword if we 
   feel it's not suitable for newbies, but I think it's a good introduction to 
   the concept of a Selenium command 

A **command** is what tells Selenium what to do. Selenium commands come in 
three 'flavors': **Actions**, **Accessors** and **Assertions**. 

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

.. Paul developed this section, following is a very similar section developed 
   by Dave. 

.. This may belong in the Selenese section.  I was thinking an introduction to 
   script syntax would belong under Basics, however this content is directly 
   related to Selenese.  Let's see how these two sections come together and then 
   decide where this goes. 
 
Selenium commands are simple, they consist of the command and two parameters.  
For example:

==========  ===========  =====
verifyText  //div//a[2]  Login 
==========  ===========  =====

The parameters are not always required.  It depends on the command.  In some 
cases both are required, in others one parameter is required, and still in 
others the command may take no parameters at all.  Here are a couple more 
examples:
  
=================  ===========   =======================
goBackAndWait 
verifyTextPresent                Welcome to My Home Page 
type               id=phone      \(555\) 666-7066 
type               id=address1   ${myVariableAddress} 
=================  ===========   =======================
 
The command reference describes the parameter requirements for each command. 
  
The parameters vary, however they are typically 
  
* a *locator* for identifying a UI element within a page. 
* a *text pattern* for verifying or asserting expected page content 
* a *text pattern* or a selenium variable for entering text in an input field 
  or for selecting an option from an option list.  

Here, we are simply providing an introduction to Selenium Basics including 
the basic features of Selenium scripts.  Locators, text patterns, and 
selenium variables are described in considerable detail in the section on 
Selenium Commands. 
  
Selenium scripts developed in the Selenium-IDE may be stored in an HTML text 
file.  The format uses an HTML table where a row represents a complete 
command and the three column entries represent the command and it's parameters.
An empty column entry indicates that parameter is not used by the command.
For instance::
  
    <tr> 
        <td>verifyText</td> 
        <td>//div//a[2]</td> 
        <td>Login</td> 
    </tr> 

.. Dave added this section below. We'll need to reconcile these two. Hadn't 
   planned on two of us delving into chap 2 at the same time.

The simple Selenese HTML syntax can be used to write tests without knowledge 
of a programming language, and can also be produced by Selenium IDE. With a 
knowledge of the syntax and Selenium IDE you can quickly produce and 
customize your testcases.

The format consists of an HTML table with three columns. The first column is 
used to identify the Selenium command, the second is a target and the final 
column contains a value. The second and third columns may not require values 
depending on the chosen Selenium command, but they should be present. Each 
table row represents a new Selenium command. Here is an example of a test 
that opens a page, asserts the page title and then verifies some content on 
the page::

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

.. Santiago: Shouldn't both attributes of the first 2 rows be in the 2nd 
   column, not the 3rd???
.. Dave: The second column is the target, and as the open and assertTitle don't
   use a target those cells are left empty. These examples will all need 
   testing before the document is ready anyway.
  
Test Suites 
------------

.. Paul: let's show the HTML of a test suite here.  then show how it looks in 
   the IDE section.  

Commonly Used Selenium Commands 
--------------------------------

.. Dave: What are these? My suggestions: open, click, waitForPageToLoad, 
   verifyText, verifyTextPresent, verifyTable, verifyTitle, verifyElementPresent

.. Santiago: I'd add type and waitForElementPresent

Summary 
--------
