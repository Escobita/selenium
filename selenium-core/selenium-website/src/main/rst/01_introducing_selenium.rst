.. _chapter01-reference:

Introducing Selenium 
====================

.. This is a very rough draft have not proofread it yet. Still, you are 
   welcome to provide comments. 

Test Automation for Web Applications
------------------------------------

Many, perhaps most, software applications now are written as web-
based applications to be run in an Internet browser. The  
effectiveness of testing these applications varies widely among companies and 
organizations. In an era of continuously improving software processes,  
promoting methodologies such as eXtreme programming  (XP) and Agile,  
it can be argued that disciplined testing and quality assurance practices are 
still underdeveloped in many organizations. Software testing is often 
conducted manually. At times, this is effective, however there are 
options to manual testing that many organizations are unaware of, or 
not skilled in.  Developing these options that would greatly improve the 
efficiency of their software development by adding efficiencies to there 
testing. 

Test automation is often the answer. Test automation means using software 
to run tests against other software. In other words, one uses a software 
program to automatically run the tests against the application to be tested.
  
There are many advantages to automating testing. Most are related to 
the repeatability of the tests and the speed that the tests can be executed.
There are a number of commercial and open source tools available for assisting
with the development of test automation. Selenium, is possibly the most 
widely-used Open Source solution. This user's guide will assist both new and experienced Selenium users in 
effective techniques in building test automation for web applications. 

This guide introduces Selenium, teaches it's most widely used features, and provides useful
advice in best practices accumulated from the Selenium community. Many examples are provided. 
Also technical information on the internal structure of Selenium and recommended
uses of Selenium is provided as contributed by a consortium of experienced 
Selenium users. It is our hope that this  guide will get additional new 
users excited about using Selenium for test automation.  We hope this guide
will assist "getting the word out" that quality assurance and software testing
has many options beyond what is currently practiced. We hope this user's 
guide and Selenium itself provide a valuable aid to boosting the reader's 
efficiency in their software testing processes. 

Introducing Selenium 
--------------------

Selenium is a robust set of tools that support rapid development of test 
automation for web-based applications. It is highly extensible, 
allowing the developer of test automation unlimited options in testing logic 
and defect reporting by supporting multiple programming languages. Selenium 
provides a rich set of testing functions, specifically geared to the needs 
of function testing a web application. These functions are highly 
flexible, allowing many options for locating UI elements and comparing 
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
Test automation supports:
  
* Frequent and rapid regression testing 
* Rapid feedback to developers during, and parallel to, the development process 
* Unlimited test-pass iteration 
* Customized reporting of application defects 
* Support for Agile, eXtreme development methodologies 
* Disciplined documentation of testcases
* Finding defects missed by manual testing
  

Selenium Components
-------------------

Selenium is composed of three major tools. Each one has a specific role in 
aiding the development of webapps test automation. 

Selenium-IDE
~~~~~~~~~~~~

The Selenium-IDE is the Integrated Development Environment for building 
Selenium test scripts. It operates as a Firefox plug-in. The Selenium-IDE 
provides an easy to use interface for both developing and running testcases. 
It also can run an entire test suite. The Selenium-IDE has a recording feature
for recording user actions into a script as the user performs them. 
It also has an options menu (right-click) integrated with the Firefox browser 
which allows the user to pick from a list of assertions and verifications 
based on a currently selected UI component and then add the selected test to 
their script. The IDE predicts the assertions and verifications desired by 
the script developer based on the context of the currently selected UI element.
Finally, the IDE allows unlimited editing of the chosen script commands. 

Selenium-RC
~~~~~~~~~~~~

Selenium-RC is the most efficient way of runnings tests. It allows
the test automation developer to use a programming language for maximum 
flexibility and extensibility in developing test logic. For instance, if the 
application under test returns a result set, and if the automated test program
needs to run tests on each element in the result set, the programming languages
iteration support can be used to iterate through the result set, calling 
Selenium commands to run tests on each item. 

Selenium-RC supports the use of programming languages for test logic by 
providing a programming API and library for each of it's 8 supported languages.
This ability to use Selenium-RC to develop a test program also allows the automated 
testing to be integrated with a project's automated build environment. 

Selenium-Grid 
~~~~~~~~~~~~~~

Selenium-Grid extends Selenium-RC's capabilities by supporting the execution 
of automated tests against multiple browsers and multiple machines supporting 
multiple test environments. 
 
  
Supported Browsers
------------------

.. Tarun: Version 1.0 Beta 2 of Selenium list following supported browsers if
   wrong browser string is provided while creating object for Default Selenium.
   I hope these are the supported browsers :-)
   
.. Santi: I'm not sure if giving the string used for selenium instantiation is
   helpful in this content, let's remember that we are in the selenium 
   introduction and probably no one reading this will understand what 
   \*piiexplore or *chrome mean...
   
Following Browsers are supported with Version 1.0 Beta 2 of Selenium RC.

- \*firefoxproxy
- \*safari
- \*safariproxy
- \*iexplore
- \*pifirefox
- \*chrome
- \*firefox2
- \*piiexplore
- \*googlechrome
- \*iehta
- \*firefox3
- \*mock
- \*opera
- \*custom
   
.. TODO: Look this up and get the specific versions off SeleniumHQ.org 
  
Flexibility and Extensibility
------------------------------

You'll find that Selenium is highly flexible.  There are multiple ways in which one can add functionality upong
Selenium's framework to customize test automation for one's specific testing needs.  This is, perhaps, Selenium's strongest
characteristic when compared with proprietary test automation tools and other Open Source solutions.
Selenium-RC support for multiple programming and scripting 
languages allows the test writer to build any logic they need into their 
automated testing and an use a preferred programming or scripting language of their choice. 
  
Selenium-IDE allows for the addition of user-defined "user-extensions" for 
creating additional commands customized to the user's needs. Also, it is possible to re-configure how the 
Selenium-IDE generates its Selenium-RC code. This would allow the user to 
customize the generated code to fit in with their own test framework.
Finally, Selenium is fully an Open Source project where code can be modified and enhancements can be 
submitted for contribution.  

About this Book
---------------

This book targets both new users of Selenium and those who have been using 
Selenium and are seeking additional knowledge. It introduces the novice 
to Selenium test automation. We do not assume the reader has experience in testing beyond the basics.  

The experienced Selenium user will also find this book valuable. It compiles 
in one place a set of useful Selenium techniques and best practices by drawing 
from the knowledge of multiple experience Selenium QA professionals. 

The remaining chapters of the book present:

:ref:`Selenium Basics <chapter02-reference>`
    Introduces Selenium by describing how to select the Selenium component 
    most appropriate for your testing tasks. Also provides a general 
    description of Selenium commands and syntax. This section allows you to 
    get a general feel for how Selenium approaches test automation and
    helps you decide where to begin. 

:ref:`Selenium-IDE <chapter03-reference>`
    Teaches how to build test scripts using the Selenium Integrated Development 
    Environment. This chapter also describes useful techniques for making your 
    scripts more readable when interpreting defects caught by your Selenium tests. 
    We explain how your test script can be 
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
    language. Also, the installation and setup of Selenium-RC is covered here. The various modes,
    or configurations, that Selenium-RC supports are described along with their 
    trade-offs and limitations. Architecture diagrams are provided to help 
    illustrate these points. Also in this section a number of solutions to 
    problems, often difficult for the new user, are described in this chapter. This 
    includes handling Security Certificates, https requests, Pop-ups and the 
    opening of new windows. 

:ref:`Test Design Considerations <chapter06-reference>`
    Presents many useful techniques for using Selenium efficiently. This 
    includes scripting techniques and programming techniques for use with 
    Selenium-RC. We cover examples of source code showing how to report defects 
    in the application under test. We also cover techniques commonly asked about 
    in the user forums such as how to implement data-driven tests (tests where 
    one can vary the data between different test passes).

:ref:`Selenium-Grid <chapter07-reference>`
.. *This chapter is not yet developed.*
  
:ref:`Advanced Selenium <chapter08-reference>`
    Presents a number of advanced topics geared to the experienced Selenium 
    user.
  
:ref:`Getting Help <chapter09-reference>`
    Describes how to be a part of the Selenium community for getting help and 
    exchanging advice. Specifically this section describes the user forums as 
    an avenue for obtaining assistance. 
