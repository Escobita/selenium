.. _chapter02-cn-reference:

Selenium基础 
============

从这里开始 -- 选择你的Selenium工具
-----------------------------------------------
许多人都是从Selenium-IDE开始的。这也是我们推荐的。
它是一种熟悉Selenium命令的简易方式。
Selenium-IDE还非常容易安装。
具体参考 :ref:`chapter on Selenium-IDE <chapter03-cn-reference>` 

你还可以从Selenium-IDE运行你的脚本。它使用方便，适合技术不高的用户。
这个IDE允许开发和运行测试，但并不需要Selenium-RC所需的编程技能。
Selenium-IDE为训练测试自动化的初级员工提供了杰出的方式。
如果有人了解如何实施网站的手工测试，他就可以非常容易地过渡到使用Selenium-IDE运行和开发测试。

有些测试任务对Selenium-IDE来说非常复杂。
当需要编程逻辑的时候，就必须使用Selenium-RC。
例如任何需要迭代的测试，像检查变长列表中的每个元素，就需要编程语言运行脚本。
Selenium-IDE不支持迭代和条件语句。

最后，Selenium-Core是运行测试的另一种方式。通过网页浏览器，测试脚本可以使用HTML接口 *TestRunner.html* 运行。这是运行Selenium命令的最初的方法。但它有局限。和Selenium-IDE类似，它不支持迭代。

Selenium-Core还不能在http协议和https协议之间切换。
因为有了Selnium-IDE和Selenium-RC的开发，更多人使用它们而不是Selenium-Core。
到本文档写作之时（09年四月），它还仍然可用，并且可能对一些人来说可能非常适合。
但是Selenium社区鼓励使用Selenium-IDE和RC，不鼓励使用Selenium-Core。
对Selenium-Core的支持会越来越少，并且可能在将来的发布中弃用。


Selenium命令介绍
----------------

Selenium命令 -- Selenese
~~~~~~~~~~~~~~~~~~~~~~~~
Selenium提供了丰富的命令集，支持任何你可以想像的方式全面测试你的网络应用。
这个命令集常被称为 *selenese* 。本质上这些命令创造了一种测试语言。

用Selenium，你可以根据UI元素的HTML标记测试他们是否存在，
测试具体的内容，测试坏链接、输入域、选择列表选项、提交的表单和表格数据以及其他的元素。
此外，Selenium命令支持测试窗口大小、鼠标位置、提示、Ajax功能、弹出窗口、事件处理和许多其他网络应用的功能。
命令参考（在SeleniumHQ.org可用）列出了所有可用的命令。

*命令* 就是告诉Selenium该做什么。
Selenium命令包含三种“口味”： **动作** ， **访问器** 和 **断言** 。
 
* **动作** 通常是控制应用程序状态的命令。
  他们所做的事情就像“单击此链接”和“请选择该选项”那样。
  如果动作失败了，或者产生错误，当前测试的执行就会停止。

  许多命令可以和"AndWait"后缀一起调用，例如"clickAndWait"。
  这个后缀告诉Selenium，当前动作将导致浏览器访问服务器，
  Selenium应该等待新的网页加载。

* **访问器** 检查应用程序的状态并把结果保存在变量中，例如"storeTitle"。
  他们也常用来自动产生断言。


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
  
.. Peter: setTimeout doesn't yet exist in this document. I'll assume it's 
   going in the Commonly Used Selenium Commands section. Is there somewhere
   else this should link to?

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
selenium variables, and the commands themselves are described in considerable
detail in the section on Selenium Commands. 
  
Selenium scripts that will be run from Selenium-IDE may be stored in an HTML
text file format. This consists of an HTML table with three columns. The first
column is used to identify the Selenium command, the second is a target and the
final column contains a value. The second and third columns may not require
values depending on the chosen Selenium command, but they should be present.
Each table row represents a new Selenium command. Here is an example of a test
that opens a page, asserts the page title and then verifies some content on the
page:
           
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

The Selenese HTML syntax can be used to write and run tests without requiring 
knowledge of a programming language.  With a basic knowledge of selenese and 
Selenium-IDE you can quickly produce and run testcases.
   
Test Suites 
------------
A test suite is a collection of tests.  Often one will run all the tests in a
test suite as one continuous batch-job.  

When using Selenium-IDE, test suites also can be defined using a simple HTML 
file.  The syntax again is simple.  An HTML table defines a list of tests where
each row defines the filesystem path to each test.  An example tells it all.

.. code-block:: html

      <html> 
      <head> 
      <title>Test Suite Function Tests - Priority 1</title> 
      </head> 
      <body> 
      <table> 
        <tr><td><b>Suite Of Tests</b></td></tr> 
        <tr><td><a href="./Login.html">Login</a></td></tr> 
        <tr><td><a href="./SearchValues.html">Test Searching for Values</a></td></tr> 
        <tr><td><a href="./SaveValues.html">Test Save</a></td></tr> 
      </table> 
      </body> 
      </html>  

A file similar to this would allow running the tests all at once, one after
another, from the Selenium-IDE.

Test suites can also be maintained when using Selenium-RC.  This is done via
programming and can be done a number of ways.  Commonly Junit is used to
maintain a test suite if one is using Selenium-RC with Java.  Additionally, if
C# is the chosen language, Nunit could be employed.  If using an interpreted 
language like Python with Selenium-RC than some simple programming would be
involved in setting up a test suite.  Since the whole reason for using Sel-RC
is to make use of programming logic for your testing this usually isn't a
problem.

Commonly Used Selenium Commands 
--------------------------------
To conclude our introduction of Selenium, we'll show you a few typical Selenium
commands.  These are probably the most commonly used commands for building test.

open
   opens a page using a URL.
click/clickAndWait
   performs a click operation, and optionally waits for a new page to load.
verifyTitle/assertTitle
   verifies an expected page title.
verifyTextPresent
   verifies expected text is somewhere on the page.
verifyElementPresent
   verifies an expected UI element, as defined by it's HTML tag, is present on
   the page.
verifyText
   verifies expected text and it's corresponding HTML tag are present on the page.
verifyTable
   verifies a table's expected contents.
waitForPageToLoad
   pauses execution until an expected new page loads.  Called automatically when 
   clickAndWait is used.
waitForElementPresent
   pauses execution until an expected UI element, as defined by its HTML tag,
   in present on the page. 


Summary 
--------
Now that you've seen an introduction to Selenium, you're ready to start writing
your first scripts.  We recommend beginning with the Selenium IDE and its
context-sensitive, right-click, menu.  This will allow you to get familiar with
the most common Selenium commands quickly, and you can have a simple script
done in just a minute or two.  Chapter 3 gets you started and then guides you
through all the features of the Selenium-IDE.
