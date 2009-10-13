
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
Selenium-Grid允许把Selenium-RC的解决方案应用到大型测试套件或者需要多环境运行的测试套件。
通过Selenium-Grid，多个Selenium-RC实例可以运行在不同的操作系统和浏览器配置中，
在运行的时候每个实例都会注册到一个中心。
当测试到达这个中心，他们会重定向到可用的Selenium-RC，然后启动浏览器运行测试。
这就可以并行的运行测试，理论上整个测试套件的运行时间就是那个运行时间最长的单个测试的时间。
  
支持的浏览器
------------------

=============  ==================================================  ===========================  =====================
**浏览器**     **Selenium-IDE**                                    **Selenium-RC**              **操作系统**
Firefox 3      1.0 Beta-1 & 1.0 Beta-2: 录制回放测试               启动浏览器，运行测试         Windows, Linux, Mac
Firefox 2      1.0 Beta-1: 录制回放测试                            启动浏览器，运行测试         Windows, Linux, Mac
IE 8                                                   	           开发中                       Windows
IE 7           只能通过Selenium-RC执行测试*                        启动浏览器，运行测试         Windows
Safari 3       只能通过Selenium-RC执行测试                         启动浏览器，运行测试         Mac
Safari 2       只能通过Selenium-RC执行测试                         启动浏览器，运行测试         Mac
Opera 9        只能通过Selenium-RC执行测试                         启动浏览器，运行测试         Windows, Linux, Mac
Opera 8        只能通过Selenium-RC执行测试                         启动浏览器，运行测试         Windows, Linux, Mac 
Google Chrome  只能通过Selenium-RC执行测试(Windows)                启动浏览器，运行测试         Windows
其他           只能通过Selenium-RC执行测试                         可能部分支持**               可适用的
=============  ==================================================  ===========================  =====================

\* 通过Selenium-IDE在Firefox上开发的测试只需通过Selenium-RC命令行就可以在任何其他可支持的浏览器中执行。

** Selenium-RC服务器可以启动任何可执行文件，但浏览器的安全设置的不同，可能有技术上的限制，将限制某些功能。

.. Santi: Should we include Selenium Core in this list???
   How about chrome and mock?? I've noticed they have a browser mod on RC and
   are not included in this list 

.. TODO: Refine this list.
  
灵活性和扩展性
------------------------------
你会发现Selenium有非常高的灵活性。为了满足特殊的测试需求，
有许多种方法可以为Selenium框架添加功能以定制测试自动化。
与私有的自动化工具和其他开源解决方案比较，这或许是Selenium最大的特点。
Selenium-RC支持多种编程和脚本语言，允许测试编写人员把任何逻辑构建到他们的自动化测试中，
也允许测试编写人员使用他们喜欢的编程和脚本语言。

Selenium-IDE允许添加用户定义的“用户插件”以创建额外的根据用户需求定制的命令。
此外，还可能重新配置Selenium-IDE生成Selenium-RC代码的方式。
这使得用户定制产生的代码符合他们自己的测试框架。
最后，Selenium是开放源代码项目，它的代码可以修改，功能提高可以作为贡献提交。

关于本书
---------------
该参考文档既针对Selenium的新用户，又针对一直在使用Selenium并寻求更多知识的用户。
它向新手介绍Selenium测试自动化。我们不假设读者有高深的测试经验。

有经验的Selenium用户也能从该参考中发现价值。它收集了一系列有用的Selenium技巧和最佳实践，
这些都来自众多经验丰富的Selenium质量保证专家的知识。

目前参考的其余章节：

:ref:`Selenium基础 <chapter02-cn-reference>`
    介绍Selenium，描述如何选择最适合你的测试任务的Selenium组件。
    此外，提供Selenium命令和语法的大致描述。
    本节将给你一个关于Selenium如何实现测试自动化的总体的感受，
    并帮助你决定从哪里开始。

:ref:`Selenium-IDE <chapter03-cn-reference>`
    教你如何使用Selenium集成开发环境构建测试用例。
    本章还介绍有用的技巧，使得在解释Selenium测试所捕捉的缺陷的时候，脚本更具可读性的。
    我们还说明如何把脚本“导出”成你选择的编程语言。
    最后，本节会介绍一些配置，便于扩展和定制Selenium-IDE以支持测试用例开发。

:ref:`Selenium命令 <chapter04-cn-reference>`
    详细介绍Selenium最常用的命令。
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
