.. _chapter05-cn-reference:

|logo| Selenium-RC
==================

.. |logo| image:: ../images/selenium-rc-logo.png
   :alt:

引言
------------
Selenium-RC 为那些稍微复杂，不仅仅是几个简单的浏览器动作和线性执行的测试提供了解决方案。
Selenium-RC 发挥了所有编程语言的优势，可以创建的各种测试，比如：读写外部文件，查询数据库，通过邮件发送报告，以及用户通过应用程序可以做的其它几乎任何事。

当Selenium-IDE 的测试脚本不能满足你的测试需求的时候你会想使用Selenium-RC. 
比如Selenium-IDE不直接支持以下功能:

* 条件语句
* 迭代 
* 测试日志以及测试结果报告
* 错误处理，尤其是意外的错误
* 数据库测试
* 测试用例归类
* 重复执行失败用例
* 测试用例依赖关系
* 测试失败时候截取屏幕图像

尽管Selenium-RC没有直接支持以上所有功能，但是它们可以通过常用的编程技术及其类库来实现。

.. 注:: 尽管使用Selenium-IDE的附加的user extensions 可能可以实现这些测试任务，但是大部分人选择使用Selenium-RC.  因为当面临复杂的测试时候Selenium-RC比Selenium-IDE更加灵活，更强的可扩展性。


在 `给你的测试添加些趣味`_ 章节, 会有一些例子很好的证明了用编程语言写测试脚本的优势。

工作原理
------------
以下介绍的是Selenium-RC 里面各个组件以及其工作原理

RC 组件
~~~~~~~~~~~~~
Selenium-RC 由两部分组成:

* 负责启动和终止浏览器的Selenium Server，同时也充当浏览器请求的 *HTTP* 代理。 
* 各种编程语言的客户端类库，通过传递Selenium命令，客户端就可以驱动Selenium
Server对AUT进行测试。

以下是一个简单的构架图......

.. image:: ../images/chapt5_img01_Architecture_Diagram_Simple.png
   :align: center

这个图表展示了客户端类库与Selenium Server通信并传递每个要执行的Selenium 命令。
然后Selenium server把Selenium命令转化为Selenium-Core JavaScript命令传递给浏览器。 
浏览器用它自己的JavaScript 解析器，执行Selenium 命令, 有效地运行Selenese测试脚本。

Selenium Server
~~~~~~~~~~~~~~~
Selenium Server 从测试程序接收并解析Selenium 命令，然后返回一个测试结果给测试程序。

Selenium Server绑定了Selenium Core, 并且当测试程序使用客户端API启动浏览器的时候，
Selenium Core被自动注入到浏览器里。Selenium-Core 是一个JavaScript程序, 实际上就是一套 JavaScript
功能函数, 它使用浏览器内置的JavaScript解析器来解析和执行Selenese命令。

Selenium Server 用简单的HTTP GET/POST请求的方式从测试程序接收Selenese命令。
这就意味着你可以通过任何支持发送HTTP请求的编程语言来驱动Selenium测试。

客户端类库
~~~~~~~~~~~~~~~~
Selenium-RC支持多种编程语言，为每种不同的编程语言提供了一个不同的客户端类库，
允许用户使用各自喜好的编程语言来编写和运行Selenium命令。
每个客户端类库都是一个程序接口，也可以说是一组在程序里运行Selenium命令的函数。而这些函数和Selenium命令一一对应。

客户端类库通过发送一条Selenese 命令到Selenium Server 来对AUT进行一个特定操作或者测试。
客户端类库也会收到命令运行结果，然后返回给你的测试程序。你的测试程序可以收到这个测试结果，然后报告成功与否，
或者采取错误处理操作如果返回的是一个意外错误信息。 

因此你可以用客户端类库的API编写一些简单的Selenium命令来创建测试程序。
或者，如果你已经有了在Selenium-IDE里创建的Selenese命令，Selenium-IDE可以通过使用Export菜单选项转换成Selenium RC代码。

.. Paul: I added the above text after this comment below was made.  
   The table suggested below may still be helpful.  We can evaluate that later.

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

安装
-------------
从 `downloads page`_ 下载Selenium-RC zip文件后，你会注意到它包含一些子目录，
这些目录包含了在所有支持的编程语言运行Selenium-RC所需的组件。

你只要选中一种编程语言，然后

* 安装Selenium-RC Server.
* 用特定语言的客户端类库建立一个程序工程。

安装 Selenium Server
~~~~~~~~~~~~~~~~~~~~~~~~~~
Selenium server 只是一个jar包 (*selenium-server.jar*), 其实根本不需要安装。
只要下载并解压，然后放在你想放的目录就足够了。
当你写完一些测试脚本之后，你只要进入到Selenium server 所在目录下面，在控制台上执行
以下命令::

    java -jar selenium-server.jar

可能大多数人想要更加快捷的方式。
这个可以通过创建一个包含以上命令的可执行文件（Windows下面的.bat文件或者Linux下面的.sh文件）。
然后在你的桌面创建一个快捷方式。那么你就可以在任意时候双击快捷方式启动Selenium server，开始你的测试。


.. 注:: 启动Selenium server 要求你的电脑必须事先安装好Java,并设置好PATH环境变量。
   你可以在控制台输入以下命令来确认Java是否安装正确::

       java -version

   如果你得到一个版本号（必须1.5或以上），那么你可以开始使用Selenium-RC了。

.. _`downloads page`: http://seleniumhq.org/download/
.. _`NUnit`: http://www.nunit.org/index.php?p=download

Java 客户端驱动配置
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* 从SeleniumHQ `downloads page`_ 下载Selenium-RC  
* 解压提取 *selenium-java-client-driver.jar* 文件
* 打开你的Java IDE (Eclipse, NetBeans, IntelliJ, Netweaver, 等等.)
* 创建一个新的工程
* 把文件 *selenium-java-client-driver.jar* 加到工程的classpath里。
* 从Selenium-IDE, 导出一个java文件，然后加到你的Java工程中去。或者用Java调用selenium-java-client API来写你自己的Selenium 测试脚本。
* 从控制台启动 Selenium server
* 在Java IDE里执行测试

具体的Java 测试工程配置，请参看附录
:ref:`Configuring Selenium-RC With Eclipse <configuring-selenium-RC-eclipse-reference>` 
和
:ref:`Configuring Selenium-RC With Intellij <configuring-selenium-RC-Intellij-reference>`.

Python 客户端驱动配置
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* 从SeleniumHQ `downloads page`_ 下载Selenium-RC  
* 解压提取 *selenium.py* 文件
* 用Python直接写测试脚本或者从Selenium-IDE导出 python文件。
* 把 *selenium.py* 文件加到你测试脚本路径
* 从控制台启动 Selenium server
* 从控制台或者Python IDE执行测试。

具体的Java 客户端驱动配置，请参看附录
:ref:`Python Client Driver Configuration <configuring-selenium-RC-python-reference>`.

.NET 客户端驱动配
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* 从SeleniumHQ `downloads page`_ 下载Selenium-RC  
* 解压缩
* 下载安装 `NUnit`_ （
  注: 你可以把NUnit当作你的测试引擎.  如果你不熟悉NUnit，你可以编写简单的main() 函数来运行测试； 
  不过NUnit 是一个非常不错的测试引擎。）
* 打开你的.Net IDE (Visual Studio, SharpDevelop, MonoDevelop)
* 创建一个动态链接库文件 (.dll)
* 加载以下动态链接库文件: nmock.dll, nunit.core.dll, nunit.
  framework.dll, ThoughtWorks.Selenium.Core.dll, ThoughtWorks.Selenium.
  IntegrationTests.dll and ThoughtWorks.Selenium.UnitTests.dll
* 使用.Net语言 (C#, VB.Net)编写Selenium测试，或者从Selenium-IDE 导出C#文件，然后拷贝代码到刚才创建的动态链接库文件里。
* 从控制台启动 Selenium server
* 从NUnit GUI 或者 NUnit 命令行执行测试。

具体的在Visual Studio里 .NET 客户端驱动配置，请参看附录
:ref:`.NET client driver configuration <configuring-selenium-RC-NET-reference>`. 

从Selenese 到 测试程序
--------------------------
使用Selenium-RC的最重要一步是把Selenese转化为程序代码.  
这同时也是理解Selenium-RC本身的重要一步，尽管同样的Selenese 脚本，在不同的编程语言下显示不同。 
在这个章节，我们会提供一些基于不同语言的例子。

测试脚本实例
~~~~~~~~~~~~~~~~~~
首先，让我们从一个Selenese测试脚本例子开始，假设用Selenium-IDE录制了以下Selenese脚本.

.. _Google search example:

=================  =========================  ===========
open               /
type               q                          selenium rc
clickAndWait       btnG
assertTextPresent  Results * for selenium rc
=================  =========================  ===========

.. note:: 这个例子可以从Google search 页面 http://www.google.com 录制到

Selenese 程序代码
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
下面是通过Selenium-IDE导出的各种编程编程语言的测试脚本. 如果你有面向对象编程语言的基础知识，你查看下面其中一个例子应该就可以理解Selenium是如何由编程语言运行Selenese命令的。点击下面其中一个按钮，查看对应语言的例子。

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
                    selenium.Click("btnG");
                    selenium.WaitForPageToLoad("30000");
                    Assert.IsTrue(selenium.IsTextPresent("Results * for selenium rc"));
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
                selenium.click("btnG");
                selenium.waitForPageToLoad("30000");
                assertTrue(selenium.isTextPresent("Results * for selenium rc"));
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
      $sel->click_ok("btnG");
      $sel->wait_for_page_to_load_ok("30000");
      $sel->is_text_present_ok("Results * for selenium rc");

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
          $this->click("btnG");
          $this->waitForPageToLoad("30000");
          $this->assertTrue($this->isTextPresent("Results * for selenium rc"));
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
              sel.click("btnG")
              sel.wait_for_page_to_load("30000")
              self.failUnless(sel.is_text_present("Results * for selenium rc"))
         
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
          @selenium.click "btnG"
          @selenium.wait_for_page_to_load "30000"
          assert @selenium.is_text_present("Results * for selenium rc")
        end
      end

在接下来的章节，我们来解释怎么用上面生成的代码来创建一个测试程序。

编写测试代码
---------------------
现在，我们将展示所有支持的语言的详细例子。主要有两个步骤，第一，从Selenium-IDE把脚本转化成一种程序语言,也可以对生成的代码略加修改。第二，写一个最简单的main 函数来运行刚才生成的代码。或者，你可以采用一个测试引擎平台比如Java里的JUnit,TestNG, .Net里的NUnit。

这里我们展示不同语言下面的例子。不同语言下的AIPs可能不同，所以你会发现每个都有各自的解释。

* `C#`_
* Java_
* Perl_
* PHP_ 
* Python_
* Ruby_ 

C#
~~

.NET 客户端驱动在Microsoft.NET环境下运行。
它可以和任何 .NET 测试框架，比如NUnit 或者Visual Studio 2005 一起使用。

你可以从转化来的代码里发现，Selenium-IDE 自动默认你将使用NUnit 作为你的测试框架。
代码里包含了*using* 语句来调用NUnit框架，同时使用NUnit的相关属性为每个成员函数标明各自的作用。  

注意，你可能需要把测试类名从"NewTest" 改为你想要的名称。而且，可能需要在以下语句里修改要打开的浏览器的参数::

    selenium = new DefaultSelenium("localhost", 4444, "*iehta", "http://www.google.com/");

生成的代码可能与下面的类似。

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


主程序非常简单。你可以用NUnit来管理测试的执行。或者你可以写一个简单的main()函数来实例化这个测试对象，然后轮流调用SetupTest(), 
TheNewTest(), 和TeardownTest() 这三个函数。

    
Java
~~~~
在Java里, 很多人用JUnit运行测试. 用JUnit来管理运行测试可以帮助你省去很多代码。
很多开发环境比如Eclipse都通过插件直接支持JUnit。如何使用JUnit不包含在本文档内，但是你可以在线找到很多相关资料。 
如果你已经有一个java团队，那么你的开发员会有JUnit的经验。

你可能会想把测试类名“NewTest”重新命名成你想要的名称。同时需要修改打开浏览器参数的语句::

    selenium = new DefaultSelenium("localhost", 4444, "*iehta", "http://www.google.com/");

Selenium-IDE 生成的代码和下面的相似。为了更加明确一点，这个例子上已经手工加了注释上去。

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
我们使用 pyunit 测试框架（单元测试模块）来执行测试。为了更好的理解如何写你的测试，你需要知道这个框架是如何工作的。
如过想全面了解pyunit，请阅读它的 `官方文档 <http://docs.python.org/library/unittest.html>`_ 。

基本测试脚本结构如下:

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

学习 API
----------------
我们之前提到过每个selenium客户端类库提供一个特定语言的程序接口来支持从你测试程序执行Selenese命令。
Selenium-RC API 使用命名惯例，假设你熟悉你选择的编程语言，并且你现在理解Selenese，那么大部分你选定语言的接口会不解自明的。
不过我们这里会解释API的最重要的也可能是不那么显而易见的方面。


启动浏览器 
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

每个例子将会通过分配一个“浏览器实例”给一个程序变量来实例化一个浏览器（这个只是你代码里的一个对象）。
这个浏览器实例然后被用来从浏览器调用方法，例如*open* 或 *type*。

当你创建浏览器实例的时候你必须给出的一些初始参数是：

host
    这个是服务器所在地方的IP地址。大部分时间，这和运行客户端的是同一个机器，因此你可以在有些客户端看到这是一个可选的参数。
port
    和host类似，这决定了服务器监听等待客户端与其通信的Socket端口。同样, 在有些客户端驱动，这个是可选的。
browser
    你想要运行测试的浏览器。这个是必须的参数。（我希望你能理解为什么:)）
url
    待测程序的基准url。这个在所有的客户端类库里也是必须的，并且由于有些服务器的实现方式，Selenium-RC在启动浏览器之前就需要它。

注：有些语言需要明确地调用它的*start*方法来启动浏览器。

运行命令
~~~~~~~~~~~~~~~~
一旦游览器被初始化并且赋值给一个变量（通常命名为"selenium"），你就可以从selenium 浏览器调用各自的方法来让它运行命令了。
比如你从selenium对象调用*type*方法::

    selenium.type("field-id","string to type")

在后台（由于Selenium-RC的魔力）浏览器会用locator和你在调用这个方法的时候指定的字符串，真正地*type**。
因此，总而言之，你的代码仅仅是一个普通的对象（包含方法和属性）。
Selenium Server和内嵌到浏览器的Selenium-Core的后台程序做测试你程序的正真工作。

检索和报告测试结果
--------------------------------
每种编程语言都有自己的测试框架用来运行测试。每一个都有它自己的方式来报告测试结果，并且你会发现一些专门创建的第三方类库用来报告不同格式的测试结果，比如HTML或者PDF格式。


**为Java客户端驱动生成测试报告:**
    

- 如果Selenium测试用例是用JUnit开发的，那么JUnit Report可以被用来生成测试报告。详细信息请参考 `JUnit Report`_ 。

.. _`JUnit Report`: http://ant.apache.org/manual/OptionalTasks/junitreport.html

- 如果Selenium测试用例是用TestNG开发的，那么不需要额外的工作就可以生成测试报告了。TestNG框架会生成一个把测试细节编列成表的HTML格式的报告。
 详细信息请参考 `TestNG Report`_ 。

.. _`TestNG Report`: http://testng.org/doc/documentation-main.html#test-results

- 同时，需要一个很不错的概要报告，可以用TestNG-xslt. 
  TestNG-xslt 报告看起来像这样的。

  .. image:: ../images/chapt5_TestNGxsltReport.png

  详细信息请参考 `TestNG-xslt`_ 。

.. _`TestNG-xslt`: http://code.google.com/p/testng-xslt/

- Logging Selenium 也可以被用来生成Java客户端驱动的报告。  
  Logging Selenium 通过扩展Java客户端驱动增加了记录日志的功能. 
  请参考 `Logging Selenium`_.
    
.. _`Logging Selenium`: http://loggingselenium.sourceforge.net/index.html

**生成Python客户端驱动的测试报告:**

- 当使用Python客户端驱动的时候，那么HTMLTestRunner可以被用来生成一个测试报告。参考 `HTMLTestRunner`_.
    
.. _`HTMLTestRunner`: http://tungwaiyip.info/software/HTMLTestRunner.html

**生成Ruby客户端驱动的测试报告:**

- 如果在Ruby里RSpec框架被用来编写Selenium测试用例，那么它的HTML报告可以被用来生成测试报告。
  详细信息请参考 `RSpec Report`_ .

.. _`RSpec Report`: http://rspec.info/documentation/tools/rake.html

给你的测试添加些趣味
-------------------------------
现在你会理解为什么你需要Selenium-RC，以及为什么你不能完全依靠Selenium-IDE. 
这里，在那些只能通过编程语言才能完成的事情上，我们会提供指导。

你会发现，当你从运行页面上元素的简单测试转换到建立调用多个web页面和各种数据的动态功能性测试，你会需要编程逻辑去验证期望的测试结果。
基本上，Selenium-IDE不支持迭代和条件语句。你会发现你能通过嵌入javascript到你的Selenese参数里面来做一些简单的条件语句，但是迭代是不可能的，
并且很多情况将会需要通过编程语言来完成。另外，你可能需要使用异常处理用来出错恢复。
介于这些原因我们编写了这个章节，让你理解如何让普通的编程技术在你的自动化测试中给你带来巨大的“验证力”

在这个章节的例子是在一种单一的编程语言下编写的--如果你有面向对象编程基础知识，你应该不会有困难在这个章节。

迭代
~~~~~~~~~
迭代是人们在他们的测试中需要用到的最普通的东西之一。比如，你可能会想多次执行一个搜索。 或者，可能为了验证你的测试结果，你需要处理一个从数据库里返回的“一组结果”

如果我们拿我们之前用的相同的 `Google 搜索例子`_ ，检查所有Selenium工具出现在搜索结果里不会是很疯狂的。  
这个测试可以用以下Selenese:

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

代码被作成三份运行了同样的步骤3次。没有正真的软件人员会想这样的完成它，这使得管理代码非常困难。

通过使用编程语言，我们可以在一个列表上迭代，并这样运行搜索。
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

条件语句
~~~~~~~~~~~~~~~~~~~~
当一个期望的元素在页面上不可用的时候，一个普通的运行Selenium测试过程中的问题会出现。
比如，当运行一下行时候：

.. code-block:: java
   
   selenium.type("q", "selenium " +s);
   
如果元素 'q' 在这个页面上刚好不可用，那么一个异常会被抛出：

.. code-block:: java

   com.thoughtworks.selenium.SeleniumException: ERROR: Element q not found

这个会导致你测试中断。一些类型的测试可能想要这样。但是经常当你的测试脚本有许多并发的测试要完成的时候，这不是所被期望的。

一个更好的方法可能是首先确认一下这个元素是否已经真的出现，然后当它没有出现的时候选者一个替代的方法:

**In Java:**

.. code-block:: java
   
   // If element is available on page then perform type operation.
   if(selenium.isElementPresent("q")) {
       selenium.type("q", "Selenium rc");
   } else {
       Reporter.log("Element: " +q+ " is not available on page.")
   }
   
在此处 *Reporter* 是 TestNG测试框架里的API。你可以用构建Selenium测试用例的框架的API来记录异常。
这个方法的好处是能够继续执行测试，即使一些 *比较不* 重要的元素在页面中不可用。
通过仅仅使用一个简单的 *if* 条件，我们可以做一些有趣的事情。想想一下可能性！

数据驱动测试
~~~~~~~~~~~~~~~~~~~
因此，迭代_ 的想法看起来酷。让我们改良它，通过允许用户编写一个外部的文本文件，从那里测试脚本可以读取输入数据，搜索和断言它的存在。

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

为什么我们会为我们的测试，想要一个包含数据的单独文件。测试涉及到的一个重要方法是用不同的数据值各自运行同样的测试。
这被称为 *数据驱动测试*,并且是一个非常普遍的测试任务。自动化测试工具，包括Selenium, 基本上都运用它，因为这常常是一个为了构建测试自动化支持手动测试方法的常见原因。

上面的Python脚本打开一个文本文件。这个文件里每行包含一个不同的搜索字符串。然后代码把这个保存到一个数组里，最后，它在字符串数组上迭代，并各自做搜索和断言。
虽然这是一个非常基础的你能做的例子，但是这个想法是展示用Selenium-IDE很困难或者不可能完成的事，可以简单的通过编程或者脚本语言来完成。

错误处理
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

数据库验证
~~~~~~~~~~~~~~~~~~~~~
以后你也可以用你喜欢的编程语言做数据库查询，如果你有支持数据库的功能，为什么不用他们在被测程序上做一些数据验证和恢复呢？ 
考虑注册过程的例子，注册的email地址是从数据库里取得的。
建立数据连接和从数据库取得数据的具体用例是：

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
   
这是一个Java里非常简单的从数据库里取回数据的例子。
一个更复杂的测试可以是验证未激活的用户是不能登录程序的。从你刚才已经看到的来看，这不会需要很多工作。
   

服务器如何工作
--------------------
.. 注释:: 这个主题尝试解释Selenium-RC背后的技术实现。对Selenium用户来说，这个不是必须要懂的，但能对理解以后你会发现的一些问题有所帮助。

为了详细地理解Selenium-RC服务器如何工作，以及为什么它使用代理注入和提高权限模式，你必须首先理解 `同源策略`_ 。
   
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

.. image:: ../images/chapt5_img02_Architecture_Diagram_1.png
   :align: center

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

.. image:: ../images/chapt5_img02_Architecture_Diagram_2.png
   :align: center

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

Multi-Window Mode
~~~~~~~~~~~~~~~~~
Before 1.0, Selenium by default ran the application under test in a sub frame 
which looks like this:

.. image:: ../images/chapt5_img26_single_window_mode.png
   :align: center

Unfortunately, some apps don't run properly in a sub frame, preferring to be 
loaded into the top frame of the window. That's why we made the multi Window 
mode (the new default since Selenium 1.0). Using this you can make your 
application under test run in a separate window rather than in the default 
frame.

.. image:: ../images/chapt5_img27_multi_window_mode.png
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

.. warning::  Be sure to put your profile in a separate new folder!!! 
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

Unable to Connect to Server 
~~~~~~~~~~~~~~~~~~~~~~~~~~~
When your test program cannot connect to the Selenium Server, an exception 
will be thrown in your test program. It should display this message or a 
similar one::

    "Unable to connect to remote server鈥?Inner Exception Message: No 
    connection could be made because the target machine actively refused it鈥?"
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

* Firefox cannot start because the browser is already open and you did 
  not specify a separate profile. 
* The run mode you're using doesn't match any browser on your machine is this 
  true?  I haven't tried this one as I didn't want to uninstall either of my 
  browsers. 
* you specified the path to the browser explicitly (see above) but the path is 
  incorrect. 

Selenium Cannot Find the AUT 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If your test program starts Selenium successfully, but the browser window 
cannot display the website you're testing, the most likely cause is your test 
program is not using the correct URL. 

This can easily happen. When Selenium-IDE generates the native language code 
from your script it inserts a dummy URL. It may not (in the .NET-C# format 
this problem exists) use the base URL when it generates the code. You will 
need to explicitly modify the URL in the generated code. 

Firefox Refused Shutdown While Preparing a Profile 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This most often occurs when your run your Selenium-RC test program against Firefox,
but you already have a Firefox browser session running and, you didn't specify
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
    鈥︹€︹€︹€︹€︹€︹€︹€? 
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
supported HTTPS and the handling of security popups. These were 鈥榚xperimental
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

.. Please, can someone verify that I explained certificates correctly?鈥攖his is 
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
