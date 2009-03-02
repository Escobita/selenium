.. _chapter01-reference:

Introducing Selenium 
====================

.. This is a very rough draft have not proofread it yet. Still, you are 
   welcome to provide comments. 

Test Automation for Web Applications
------------------------------------

Many, perhaps most, software applications these days are written as web-
based applications to be run in an Internet browser. The efficiency and 
effectiveness of testing these applications varies widely among companies and 
organizations. In the era of continuously improving software processes 
promoting methodologies such as eXtreme programming  (XP) and Agile practices 
it can be argued that disciplined testing and quality assurance practices are 
still underdeveloped in many organizations. Software testing is still often 
conducted manually. At times, this is effective. However there are many 
options to manual testing and many organizations are often not aware of, or 
not skilled in, developing these options that would greatly improve the 
efficiency of their software development by adding efficiencies to there 
testing. 

Test automation is often the answer. Test automation means using software 
to run tests against other software. In other words, one uses a software 
program to automatically run the tests against the application to be tested.
Essentially, what would be conducted manually by an end-user or test 
engineer is built into a program. 
  
There are many advantages to automating testing. Most are related to 
the repeatability of the tests and the speed that the tests can be executed.
There are a number of commercial and open source tools available for assisting
with the development of test automation. Selenium, is possibly the most 
widely-used open source solution. This user's guide is to introduce the new 
Selenium user to Selenium, along with providing advice in best practices 
accumulated from the Selenium community. 

This guide will assist both new and experienced Selenium users in 
effective techniques in building test automation. Many examples are provided. 
Also technical information on the internal structure of Selenium and recommended
uses of Selenium is provided as contributed by a consortium of experienced 
Selenium users. It is our hope that this user guide will get additional new 
users excited about using Selenium to build test automation.  We hope this guide
will assist "getting the word out" that quality assurance and software testing
has many options that often organizations are unaware of. We hope this user's 
guide and Selenium itself provide a valuable aid to boosting the reader's 
efficiency in the software testing processes. 

Introducing Selenium 
--------------------

Selenium is a robust set of tools that support rapid development of test 
automation for web-based applications. Selenium is highly extensible, 
allowing the developer of test automation unlimited option in testing logic 
and defect reporting by supporting multiple programming languages. Selenium 
provides a rich set of testing functions, specifically geared to the needs 
of function testing a web application. These functions also are highly 
extensible, allowing many options for locating UI elements and comparing 
expected test results against actual application behavior. 

To Automate or Not to Automate -- That is a Question!
-----------------------------------------------------

Is automation always an advantage? When should one decide to automate their 
testcases? It is not always advantageous to automate testcases. There are 
times when manual testing may be more appropriate. For instance, if the 
application's user interface will change considerably in the near future, 
then any automation would need to be rewritten. Also, sometimes there simply 
is not enough time to build test automation. For the short-term, manual testing 
is more effective. If an application has a very tight deadline, there is 
currently no test automation available, and it's imperative that the testing 
get done within that time frame, then manual testing is the best solution. 

Test automation has it's place though, and has specific advantages for 
improving the long-term efficiency of a software team's testing processes. 
Test automation supports 
  
* frequent and rapid regression testing 
* rapid feedback to developers during, and parallel to, the development process 
* unlimited test-pass iteration 
* customized reporting of application defects 
* support for Agile, eXtreme development methodologies 
* disciplined documentation of testcases. 
* uncovering the defects. 
  
.. TODO: expand on the points a bit more. 

Selenium Components
-------------------

Selenium is composed of three major components. Each has a specific role in 
aiding the development of web apps test automation. 

Selenium-IDE
~~~~~~~~~~~~

The Selenium-IDE is the integrated development environment for building 
Selenium test scripts. It operates as a Firefox plug-in. The Selenium-IDE 
provides an easy to use interface for both developing and running testcases. 
It also can run an entire test suite. The Selenium-IDE has a recording 
feature for recording user actions into a script as the user performs them. 
It also has an options menu (right-click) integrated with the Firefox browser 
which allows the user to pick from a list of assertions and verifications 
based on a currently selected UI component and then add the selected test to 
their script. The IDE predicts the assertions and verifications desired by 
the script developer based on the context of the currently selected UI element.
Finally, the IDE allows unlimited editing of the chosen script commands. 

Selenium-RC
~~~~~~~~~~~~

Selenium-RC is the primary way most Selenium users run their tests. It allows
the test automation developer to use a programming language for maximum 
flexibility and extensibility in developing test logic. For instance, if the 
application under test returns a result set, and if the automated test program
needs to run tests on each element in the result set, the programming languages
iteration support can be used to iterate through the result set, calling 
Selenium commands to run tests on each result set item. 

.. Dave: I think an example would be a good idea here so that the reader can 
   immediately see the advantage of using Selenium-RC. Maybe some pseudo code.

Selenium-RC supports the use of programming languages for test logic by 
providing a programming API and library for each of it's 8 supported languages.
This ability to use Selenium-RC to develop a test program also allows the automated 
testing to be integrated with a project's automated build environment.

.. TODO: double-check this 


Selenium-Grid 
~~~~~~~~~~~~~~

Selenium-Grid extends Selenium-RC's capabilities by supporting the execution 
of automated tests against multiple browsers and multiple machines supporting 
multiple test environments. 
  
.. TODO: Research this and expand this section. 
  
Supported Browsers
------------------

At the time of writing, the soon-to-be-released Selenium 1.0 supports Internet 
Explorer, Mozilla Firefox, Opera And Safari 

.. TODO: look this up and get the specific versions off SeleniumHQ.org 
  
Flexibility and Extensibility
------------------------------

You'll find that Selenium is highly flexible and easy to extend by adding your 
own functionality. Selenium-RC support for multiple programming and scripting 
languages allows the test writer to build any logic they need into their 
automated testing. 
  
Selenium-IDE allows for the addition of user-defined user-extensions for 
creating additional commands customized to the user's needs. Finally, a future 
goal of the Selenium project is to allow the user to re-configure how the 
Selenium-IDE generates its Selenium-RC code. This would allow the user to 
customize the generated code to fit in with their own customized test framework.
Finally, Selenium is fully an Open Source project where enhancements can be 
submitted for contribution. 

.. Can I make this statement?  If so, how should I reword this?  
  
Example Web-Site Test
~~~~~~~~~~~~~~~~~~~~~

.. To be determined, based on other examples occurring later in the doc. 
   Do we even need a sample website introduced this early?  Possibly not. 
   We can either have a sample website used through the book for illustrations, 
   or just do different examples at each point where an example is needed. 
   We'll figure that out as this doc progresses. 

.. Santiago: Maybe we can create a sample website, where users can practice on tests 
   creation and we can base our examples on. I have a basic AJAX webapp that 
   I developed in PHP for college, it has a CRUD and a contact form. I'll take 
   a look at it and post it for you to see it.
  
About this Book
---------------

This book targets both new users of Selenium and those who have been using 
Selenium and are seeking additional knowledge. It serves to introduce the new 
user to Selenium test automation and to test automation in general. However 
the experienced Selenium user will also find this book valuable. It compiles 
in one place a set of useful Selenium techniques and best practices by drawing 
from the knowledge of multiple experience Selenium QA professionals. 

The remaining chapters of the book present the following:

:ref:`Selenium Basics <chapter02-reference>`
    Introduces Selenium by describing how to select the Selenium component 
    appropriate for your own testing tasks. Also provides a general 
    description of Selenium commands and syntax. This section allows you to 
    get a general feel for how Selenium approaches test automation and aims to 
    help you decide where to start. 

:ref:`Selenium-IDE <chapter03-reference>`
    Explains how to build test scripts using the Selenium Integrated Development 
    Environment. This chapter also describes useful techniques for making your 
    scripts more easily readable when interpreting defaults caught by your 
    automated testing. In this section we explain how your test script can be 
    "exported" to the programming language of your choice. Finally, this section 
    describes some configurations available for extending and customizing how 
    the Selenium-IDE supports script development. 

:ref:`Selenium Commands <chapter04-reference>`
    Describes the Selenium commands in detail providing many examples. This 
    chapter shows the full extent of what types of actions, verifications and 
    assertions can be made against a web application. 

:ref:`Selenium-RC <chapter05-reference>`
    Explains how to develop an automated test program using the Selenium-RC API.
    Many examples are presented in both, a programming language and a scripting 
    language. The installing of Selenium-RC is covered here. The various modes,
    or configurations, that Selenium-RC supports are described along with their 
    trade-offs and limitations. Architecture diagrams are provided to help 
    illustrate these points. Also in this section a number of solutions to 
    problems often difficult for the new user are described in this chapter. This 
    includes handling Security Certificates, https requests, Pop-ups and the 
    opening of new windows. 

:ref:`Test Design Considerations <chapter06-reference>`
    Presents many useful techniques for using Selenium efficiently. This 
    includes scripting techniques and programming techniques for use with 
    Selenium-RC. We cover examples of source code showing how to report defects 
    in the application under test. We also cover techniques commonly asked about 
    in the user forums such as how to implement data-driven tests (tests where 
    one can vary the data between different test passes).
        
.. TODO: Need to look at the TOC and expand this paragraph a little. 

:ref:`Selenium-Grid <chapter07-reference>`
.. TODO: Need content here
  
:ref:`Advanced Selenium <chapter08-reference>`
    Presents a number of advanced topics geared to the experienced Selenium 
    user. 
        
.. TODO: Need to look at the TOC and see what we put here. Add another 
   sentence or two describing this section. 
  
:ref:`Getting Help <chapter09-reference>`
    Describes how to be a part of the Selenium community for getting help and 
    exchanging advice. Specifically this section describes the user forums as 
    an avenue for obtaining assistance. 
