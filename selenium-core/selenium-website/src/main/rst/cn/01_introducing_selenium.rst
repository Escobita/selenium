
.. _chapter01-cn-reference: 

Selenium介绍
====================

自动化或者不自动化？这是个问题！
------------------------------------------------------

自动化是否总是有利的？什么时候人们决定自动化测试用例呢？

自动化测试用例并 **不** 总是有利的。有些时候手动测试可能更为恰当。
例如，如果应用程序的用户界面在不久的将来会有相当大的改变，那么任何自动化将需要重写。
此外，有时根本没有足够的时间来构建测试自动化。短期而言，手动测试可能会更为有效。
如果应用程序有一个十分紧迫的截止日期，目前还没有测试自动化，
并且它的当务之急是在这个时限内完成的测试，那么手动测试是最好的解决办法。

然而，对于提高软件团队测试过程的长期效率，自动化有独特的优势。
测试自动化支持：

* 频繁的回归测试
* 在开发过程中给开发人员快速反馈
* 几乎无限的测试用例执行迭代
* 可定制的应用缺陷报告
* 支持敏捷或极限开发方法
* 纪律的测试用例文档
* 寻找手工测试遗漏的缺陷


网络应用的测试自动化
------------------------------------
多数可能是绝大多数现今的软件都是基于网络的并可用联络浏览器打开的应用程序。
这些应用的测试效果在不同的公司或组织之间有很大的不同。在一个不断改进软件过程的时代，
例如极限编程(XP)和敏捷，可以认为，有纪律的测试和质量保证实践在许多组织中还在不断发展。
软件测试往往以人工方式进行。有时，这是有效的；然而许多组织忽视了手工测试的替代方法，
或者没有技能实施。运用这些替代方法将大大提高软件开发的效率，因为他们增加了测试的效率。

测试自动化往往就是答案。测试自动化是指使用工具对目标应用程序在必要的时候时执行可重复的测试。
  
自动化测试有许多优点，大都和测试的重复性以及测试的执行速度有关。
有许多商业和开源的工具可以辅助测试自动化的开发。Selenium可能是最广泛使用的开源解决方案。
此用户的指南将协助新老Selenium用户学习有效技术，以构建网络应用的测试自动化。

本指南介绍Selenium，教授其使用最广泛的功能，并提供有用的意见，
这些意见来自于Selenium社区积累的最佳实践。指南提供大量实例。
此外，还提供Selenium内部结构的技术信息和Selenium的推荐用法，
这些都是由经验丰富的用户组成的联盟供稿的。
我们希望指南可以增加乐于使用Selenium做自动化测试的新用户。
我们希望指南可以传达这样的信息：质量保证和软件测试有许多超越目前实际的多种选择。
我们希望指南和Selenium本身所提供的宝贵帮助，可以提高读者在软件测试流程中的效率。

Selenium介绍 
--------------------
Selenium是个强大的工具集，支持快速开发网络应用的测试自动化。
Selenium提供了丰富的测试函数集，尤其适合网络应用测试的需要。
这些操作非常灵活，允许多种选择定位UI元素和比较测试的期望结果与应用程序的实际行为。
 
Selenium的主要功能之一就是支持在多种浏览器平台上执行测试。
  
Selenium组件
-------------------
Selenium由三个主要的工具组成。每一个在帮助网络应用测试自动化开发上都有独特的作用。

Selenium-IDE
~~~~~~~~~~~~
Selenium-IDE是构建Selenium测试用例的集成开发环境。
它是一个Firefox插件，为开发和运行单一测试用例或者完整测试套件提供了易用的界面。
Selenium-IDE具有录制功能，能记录用户执行的操作并保存成可复用的脚本用来回放。
它也有一个上下文菜单（右键单击）集成到Firefox浏览器，允许用户在断言和验证列表中选择。
Selenium-IDE还为了测试用例更加精确和便于控制提供了充分的编辑功能。

虽然Selenium-IDE是一个Firefox插件，但是通过使用Selenium-RC并在命令行中指定测试套件名，
由它创建的测试也可以运行在其他的浏览器上。

Selenium-RC (Remote Control)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Selenium-RC允许测试自动化开发人员使用编程语言以最大的灵活性和可扩展性开发测试逻辑。
例如，如果被测程序返回一个结果集，并且如果自动化测试程序需要在结果集的每个元素上运行测试，
编程语言的迭代支持可以用来遍历结果集，在结果集的每一项上调用Selenium命令运行测试。

Selenium-RC为以下每种支持的语言提供API（应用程序接口）和函数库：
HTML，Java，C#，Perl，PHP，Python和Ruby。
使用Selenium-RC通过高级编程语言开发测试用例的能力还使得自动化测试与项目自动化构建环境相集成。


Selenium-Grid 
~~~~~~~~~~~~~~
Selenium-Grid allows the Selenium-RC solution to scale for large test suites 
or test suites that must be run in multiple environments. With Selenium-Grid 
multiple instances of Selenium-RC are running on various operating system and 
browser configurations, each of these when launching register with a hub. 
When tests are sent to the hub they are then redirected to an available 
Selenium-RC, which will launch the browser and run the test. This allows for 
running tests in parallel, with the entire test suite theoretically taking 
only as long to run as the longest individual test.
 
  
Supported Browsers
------------------

=============  ==================================================  ===========================  =====================
**Browser**    **Selenium-IDE**                                    **Selenium-RC**              **Operating Systems**
Firefox 3      1.0 Beta-1 & 1.0 Beta-2: Record and playback tests  Start browser, run tests     Windows, Linux, Mac
Firefox 2      1.0 Beta-1: Record and playback tests               Start browser, run tests     Windows, Linux, Mac
IE 8                                                   			   Under development            Windows
IE 7           Test execution only via Selenium-RC*                Start browser, run tests     Windows
Safari 3       Test execution only via Selenium-RC                 Start browser, run tests     Mac
Safari 2       Test execution only via Selenium-RC                 Start browser, run tests     Mac
Opera 9        Test execution only via Selenium-RC                 Start browser, run tests     Windows, Linux, Mac
Opera 8        Test execution only via Selenium-RC                 Start browser, run tests     Windows, Linux, Mac 
Google Chrome  Test execution only via Selenium-RC(Windows)        Start browser, run tests     Windows
Others         Test execution only via Selenium-RC                 Partial support possible**   As applicable 
=============  ==================================================  ===========================  =====================

\* Tests developed on Firefox via Selenium-IDE can be executed on any other 
supported browser via a simple Selenium-RC command line.

** Selenium-RC server can start any executable, but depending on 
browser security settings, there may be technical limitations that would limit
certain features.

.. Santi: Should we include Selenium Core in this list???
   How about chrome and mock?? I've noticed they have a browser mod on RC and
   are not included in this list 

.. TODO: Refine this list.
  
Flexibility and Extensibility
------------------------------
You'll find that Selenium is highly flexible.  There are multiple ways in which
one can add functionality to Selenium's framework to customize test 
automation for one's specific testing needs. This is, perhaps, Selenium's 
strongest characteristic when compared with proprietary test automation tools
and other open source solutions. Selenium-RC support for multiple programming
and scripting languages allows the test writer to build any logic they need
into their automated testing and to use a preferred programming or scripting
language of one's choice. 
  
Selenium-IDE allows for the addition of user-defined "user-extensions" for 
creating additional commands customized to the user's needs. Also, it is 
possible to re-configure how the Selenium-IDE generates its Selenium-RC code.
This allows users to customize the generated code to fit in with their
own test frameworks. Finally, Selenium is an Open Source project where 
code can be modified and enhancements can be submitted for contribution.

About this Book
---------------
This reference documentation targets both new users of Selenium and those who 
have been using Selenium and are seeking additional knowledge. It introduces 
the novice to Selenium test automation. We do not assume the reader has 
experience in testing beyond the basics.  

The experienced Selenium user will also find this reference valuable. It compiles
in one place a set of useful Selenium techniques and best practices by drawing 
from the knowledge of multiple experienced Selenium QA professionals. 

The remaining chapters of the reference present:

:ref:`Selenium Basics <chapter02-cn-reference>`
    Introduces Selenium by describing how to select the Selenium component 
    most appropriate for your testing tasks. Also provides a general 
    description of Selenium commands and syntax. This section allows you to 
    get a general feel for how Selenium approaches test automation and
    helps you decide where to begin. 

:ref:`Selenium-IDE <chapter03-cn-reference>`
    Teaches how to build test cases using the Selenium Integrated Development 
    Environment. This chapter also describes useful techniques for making your 
    scripts more readable when interpreting defects caught by your Selenium tests. 
    We explain how your test script can be 
    "exported" to the programming language of your choice. Finally, this section 
    describes some configurations available for extending and customizing how 
    the Selenium-IDE supports test case development. 

:ref:`Selenium Commands <chapter04-cn-reference>`
    Describes a subset of the most useful Selenium commands in detail. This 
    chapter shows what types of actions, verifications and 
    assertions can be made against a web application. 

:ref:`Selenium-RC <chapter05-cn-reference>`
    Explains how to develop an automated test program using the Selenium-RC API.
    Many examples are presented in both a programming language and a scripting 
    language. Also, the installation and setup of Selenium-RC is covered here. 
    The various modes, or configurations, that Selenium-RC supports are
    described, along with their trade-offs and limitations. Architecture
    diagrams are provided to help illustrate these points. 
    A number of solutions to problems which are often difficult for the new user, are
    described in this chapter. This includes handling Security Certificates,
    https requests, pop-ups and the opening of new windows. 

:ref:`Test Design Considerations <chapter06-cn-reference>`
    Presents many useful techniques for using Selenium efficiently. This 
    includes scripting techniques and programming techniques for use with 
    Selenium-RC. We cover examples of source code showing how to report defects 
    in the application under test. We also cover techniques commonly asked about 
    in the user forums such as how to implement data-driven tests (tests where 
    one can vary the data between different test passes).

:ref:`Selenium-Grid <chapter07-cn-reference>`
    *This chapter is not yet developed.*
  
:ref:`User extensions <chapter08-cn-reference>`
    Presents all the information required for easily extending Selenium. 
  
..  :ref:`Getting Help <chapter09-cn-reference>`
    Describes how to be a part of the Selenium community for getting help and 
    exchanging advice. Specifically this section describes the user forums as 
    an avenue for obtaining assistance. 

The Documentation Team
----------------------

The Original Authors
~~~~~~~~~~~~~~~~~~~~
* Dave Hunt
* Paul Grandjean
* Santiago Suarez Ordonez
* Tarun Kumar

The original authors who kickstarted this document are listed in alphabetical 
order.  Each of us contributed significantly by taking a leadership role in 
specific areas.  Each chapter originally had a primary author who kicked off 
the intial writing, but in the end, each of us made significant contributions 
to each chapter throughout the project.

Current Authors
~~~~~~~~~~~~~~~
* Mary Ann May-Pumphrey
* Peter Newhook

In addition to the original team members who are still involved (May '09), 
Mary Ann, and Peter have recently made major contributions.  Their reviewing 
and editorial contributions proved invaluable.  Mary Ann is actively writing 
new subsections and has provided editorial assistance throughout the document.
Peter has provided assistance with restructuring our most difficult chapter 
and has provided valuable advice on topics to include. Their enthusiasm and 
dedication has been incredibly helpful.  We hope they continue to be involved.  

Acknowledgements
~~~~~~~~~~~~~~~~
A huge special thanks goes to Patrick Lightbody.  As an administrator of the 
SeleniumHQ website, his support has been invaluable.  Patrick has helped us 
understand the Selenium community--our audience. He also set us up with 
everything we needed on the SeleniumHQ website for developing and releasing 
this user's guide.  His enthusiasm and encouragement definitely helped drive 
this project.  Also thanks goes to Andras Hatvani for his advice on publishing
solutions, and to Amit Kumar for participating in our discussions and for 
assisting with reviewing the document.

And of course, we must *recognize the Selenium Developers*.  They have truly 
designed an amazing tool. Without the vision of the original designers, and 
the continued efforts of the current developers, we would not have such a 
great tool to pass on to you, the reader.
