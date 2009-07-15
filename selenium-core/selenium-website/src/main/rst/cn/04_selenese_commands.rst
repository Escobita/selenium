"Selenese" Selenium 命令集
=============================

.. _第四章:

Selenium 命令集,通常称作 *selenese*,是运行你的测试的一组命令集合。
这些命令的序列就是 *测试脚本*.
现在我们详细解释一下这些命令,告诉你一些在用Selenium测试web应用的精选例子。


验证页面元素
------------------------
验证页面上的 *UI元素* 或许是做自动化测试最常用到的功能.
Selenese 允许多种方式验证UI元素。
理解这些不同的方法是很重要的，因为这些方法定义了你真正要测试的。

例如：

a) 一个元素是否出现在页面中的某个地方？
b) 某文本是否出现在页面中某个地方？
c) 某文本是否出现在页面上特定的位置?

例如,测试一个文本的头，文本以及在这页的顶部位置或许对你的测试来说是恰当的.然而，
如果你测试的是在主页存在的一个图片,网站设计者经常改变这个图片在这个也页面上的位置，
如果你只是想测试 *一个图片*（这个图片文件）存在 *页面上的某个位置*
   
   
判定还是验证?
~~~~~~~~~~~~~~~~~~~~~~~~~~
选择使用 **assert** 还是 **verify** 取决于方便性和对case失败的管理方式.
如果测试本章节页是正确的那页，而且当检查浏览器中显示的是你想要的那页的测试 
已经不通过了，这种情况这两个方法差别是很小的。如果不是正确的那页
你或许想终止这个测试， 
及时地找出原因并解决其中问题.另外,
你可能想检查页面上的很多属性，当第一个执行不通过，而不需要终止这个测试用例 
同时允许你看到所有这个页面上执行不通过的情况，然后采取适当的措施。 
使用 **assert** 将此测试用例执行不通过并终止. 
而 **verify** 将继续执行这个测试用例到结束，并告知这个测试用例不通过. 

使用这个功能的最好办法是给你的测试命令集分组，然后按组执行一个 **assert** 紧跟着
一个或多个 **verify** 命令集
例如:

============    ==========  ============
open            /download/      
assertTitle     Downloads       
verifyText      //h2        Downloads       
assertTable     1.2.1       Selenium IDE    
verifyTable     1.2.2       June 3, 2008    
verifyTable     1.2.3       1.0 beta 2      
============    ==========  ============

上面的例子中，首先打开一个页面，然后 **asserts** 这个当前页面已经加载通过比较题目是期望的值
只有这个通过，
接下来的命令 **verify** 在特定的位置某个位置存在.
这个测试用例接着 ***assert** 第一个表中第一列第二行包含期望的数值,
只有这个通过
那行中其它的方格将用 **verified**


verifyTextPresent
~~~~~~~~~~~~~~~~~
命令 ``verifyTextPresent`` 用来验证*在页面上某地方存在一个特定文本*
它包含一个参数--要验证的文本pattern.
例如:

=================   ==================   ============
verifyTextPresent   Marketing Analysis 
=================   ==================   ============

Selenium将寻找然后验证 "Marketing Analysis" 本文出现在当前要测也页面的某个地方.
当你想验证某个页面上存在只存在某个文本时用``verifyTextPresent``
当你需要测试这个文本存在页面中哪里时，不要用这个命令.

verifyElementPresent
~~~~~~~~~~~~~~~~~~~~
当你需要测试某个特定UI元素存在时用这个命令.
而不是验证这个元素的内容.这个验证不去检查文本，
而是检查HTML标签.一个通常的应用是一个检查一个图片是否存在.

====================   ==================   ============
verifyElementPresent   //div/p/img               
====================   ==================   ============
   
这个命令验证一个图片，<img>标签是否在某个特定页面存在
这个标签在一个<div>标签和<p>标签后面.
开头第一个参数是一个 *locator* 告诉Selenese命令怎么去找这个元素.
Locators将在下部分说明.

``verifyElementPresent`` 能用来检查在页面中任何HTML 标签是否存在，
链接,段落,<div>分割等等.
下面是更多的例子.

====================   ==============================   ============
verifyElementPresent   //div/p 
verifyElementPresent   //div/a               
verifyElementPresent   id=Login
verifyElementPresent   link=Go to Marketing Research               
verifyElementPresent   //a[2]
verifyElementPresent   //head/title
====================   ==============================   ============

这些例子说明了要测的各种UI元素的方法.
再次说明,locators将在下部分说明.

verifyText
~~~~~~~~~~
.. TODO mam-p:  Why the parenthetical limitation on locator type below?  The locator could also be name=, id=, identifier=, etc.

当文本和它的UI元素都必须要测试时，使用``verifyText``.
``verifyText`` 必须使用一个locator,如果你选择一个*XPath* 或者 *DOM* 的locator,
你可以验证在页面上某个特定位置有某个特定文本，相对于这个页面上其它的UI组件.


==========   ===================    ===================================================================
verifyText   //table/tr/td/div/p    This is my text and it occurs right after the div inside the table.
==========   ===================    ===================================================================


.. _locators-section:

Locating Elements 
定位元素
-----------------
对许多Selenium命令，一个目标是必须的.这个目标标示在web应用内容中的一个元素,
这个目标包含这样的位置格式:``locatorType=location`` 策略.
locator类型在许多情况下可以忽略.
不同的locator类型将在以下例子中以此讲述.

.. Santi: I really liked how this section was taken. But I found that most of
   the locator strategies repeat the same HTML fragment over a over. Couldn't
   we put A example HTML code before starting with each strategie and then use
   that one on all of them?

默认locator
~~~~~~~~~~~~~~~~
在以下情况下你可以选择忽略locator类型:
 
 - locator以 "document"开头, 将使用DOM locator策略.
   See :ref:`locating-by-dom`.
   参见 :说明:`通过dom定位`.

 - locator 以 "//" 开头,将使用 XPath locator策略.
   参见 : 说明:`通过xpath定位`.

 - locator以除了以上的其它或者正确的locator类型开始,
   将默认为用identifier locator 策略. 
   参见 : 说明:`identifier通过定位`.

.. _通过identifier定位:

通过identifier定位:
~~~~~~~~~~~~~~~~~~~~~~
这或许是最常用的定位元素的方法,也是当没有被识别的locator类型使用时，默认使用的方法
使用这种方法,第一个id属性值的相配位置的元素将被使用.
如果没有相配的id属性,第一个name属性值的相配位置的元素将被使用.

例如, 页面可能有如下id和name属性:
           
.. code-block:: html
  :linenos:

  <html>
   <body>
    <form id="loginForm">
     <input name="username" type="text" />
     <input name="password" type="password" />
     <input name="continue" type="submit" value="Login" />
    </form>
   </body>
  <html>

接下来的locator策略将从以上HTML片段按行号返回元素:

- ``identifier=loginForm`` (3)
- ``identifier=username`` (4)
- ``identifier=continue`` (5)
- ``continue`` (5)

因为 ``identifier`` locator类型是默认的,  ``identifier=`` 在前三个例子中 
是不必要的.

通过Id定位
~~~~~~~~~~~~~~
这种locator类型仅局限在identifier locator类型,
很明显当你知道一个元素的id属性时使用它.

.. code-block:: html
  :linenos:
  
   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Clear" />
     </form>
    </body>
   <html>

- ``id=loginForm`` (3)

通过Name定位
~~~~~~~~~~~~~~~~
name locator类型将定位与name属性相配的第一个 元素.
如果一个name属性对应多个有相同值的元素,那么你可以用过滤器去再定义你的定位策略.
默认的过滤器类型是vale(与value属性相配).

.. code-block:: html
  :linenos:
  
   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Clear" />
     </form>
   </body>
   <html>

- ``name=username`` (4)
- ``name=continue value=Clear`` (7)
- ``name=continue Clear`` (7)
- ``name=continue type=button`` (7)

.. note:: Unlike some types of XPath and DOM locators, the three
.. 注释:: 不像XPah和DOM locator类型,以上三种locator类型允许Selenium测试在
   types of locators above allow Selenium to test a UI element independent 
   页面上位置独立的UI元素.所以如果页面架构和组织方式改变了,这个测试
   将仍然通过.不管你是希望还是不希望页面结构变化，你都要进行测试.
   web设计者频繁改变页面的情况,但它的功能必须迭代测试.
   测试通过id和name属性或者通过任何HTML属性，变得非常重要.

.. _locating-by-xpath:
.. _通过xpath定位:

通过XPath定位
~~~~~~~~~~~~~~~~~
XPath是在XML文档中定位节点的语言.
HTML可以作为XML(XHTML)的一个实现,Selenium用户可以借助这个有用的语言在web应用程序中
去寻找元素.XPath扩展了通过id或者name属性定位的简单方法,它展示了所有像定位页面上第三个
复选框之类元素的新的可能.

.. Dave: 是否有必要指出对XPath支持的不同(本地Firefox,用Google AJAXSLT或者在IE的新方法)?
   可能是即使需要的话也是一个高级话题，

用XPath的一个主要原因是对于你想定位的元素没有一个合适的id或者name属性.
你可以使用XPath去定位元素用绝对路径(不推荐),或者相对于某个有id或name属性的元素
XPath locator 还可以用在通过不是id和name属性的其它属性定位元素.

绝对路径的XPath 包含所有从根(html)的所有元素,最终可以因为一个应用程序细微地方的调整而失败.
通过寻找一个相近的有id或者name属性的元素(一个父元素最好了),你可以定位你的目标元素基于它们的关系

这些是很少变化的，可以使得你的测试更智能.

因为只有 ``xpath`` locator 以 "//"开头,当表明一个XPath locator的时候加 ``xpath`` 标签
是没有必要的.

.. code-block:: html
  :linenos:
  
   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Clear" />
     </form>
   </body>
   <html>

.. TODO: mam-p:  Is the fourth example below correct?
.. TODO: mam-p:下面第四个例子正确吗?
- ``xpath=/html/body/form[1]`` (3) - *绝对路径 (如果HTML有细微改变的时候将中断)*
- ``//form[1]`` (3) - *在HTML中的第一个form元素*
- ``xpath=//form[@id='loginForm']`` (3) - *id属性值是 'oginForm'的form元素*
- ``xpath=//form[input/\@name='username']`` (4) - *有一个子元素的name属性值是username的input字段的第一个form元素*
- ``//input[@name='username']`` (4) - *name属性值是username的input元素*
- ``//form[@id='loginForm']/input[1]`` (4) - *id属性值为loginFomr的form元素中第一个子input字段*
- ``//input[@name='continue'][@type='button']`` (7) - *name属性值为continue,type属性值为button的Input元素*
- ``//form[@id='loginForm']/input[4]`` (7) - *id属性值为loginForm'的form元素的第四个input*

这些例子包括了一些基础,为了学习更多,推荐以下教程:

* `W3Schools XPath Tutorial <http://www.w3schools.com/Xpath/>`_ 
* `W3C XPath Recommendation <http://www.w3.org/TR/xpath>`_
* `XPath Tutorial 
  <http://www.zvon.org/xxl/XPathTutorial/General/examples.html>`_ 
  - 有交互的例子. 

也有很多有用的firefox插件来协助我们发现元素的XPath:

* `XPath Checker 
  <https://addons.mozilla.org/en-US/firefox/addon/1095?id=1095>`_ - XPath建议,
  能用来测试XPath结果. 
* `Firebug <https://addons.mozilla.org/en-US/firefox/addon/1843>`_ -  XPath 
  建议是这个非常有用插件的许多功能中的一个.

通过链接文本定位超链接
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. TODO: mam-p:  Users often are unaware that a link locator is a pattern, 
   not just a string.  So, I think we need an example here that necessitates 
   a link=regexp:pattern locator in the test case.

用链接的文本定位web页面中的超链接，这是一个简单的方法.
如果两个链接在页面上是相同的文本,则第一个匹配的将被使用.

.. code-block:: html
  :linenos:

  <html>
   <body>
    <p>Are you sure you want to do this?</p>
    <a href="continue.html">Continue</a> 
    <a href="cancel.html">Cancel</a>
  </body>
  <html>

- ``link=Continue`` (4)
- ``link=Cancel`` (5)

.. _通过dom定位:

通过DOM定位
~~~~~~~~~~~~~~~

文档对象模型表示能用JavaScript访问的HTML文档.
这种定位策略使用JavaScript计算给页面上的元素.
它用层级点符号定位元素的位置

因为只有 ``dom`` locator 以  "document" 开头,因此当用dom locator时没有必要包含 ``dom=`` 
标签.

.. code-block:: html
  :linenos:

   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Clear" />
     </form>
   </body>
   <html>

- ``dom=document.getElementById('loginForm')`` (3)
- ``dom=document.forms['loginForm']`` (3)
- ``dom=document.forms[0]`` (3)
- ``document.forms[0].username`` (4)
- ``document.forms[0].elements['username']`` (4)
- ``document.forms[0].elements[0]`` (4)
- ``document.forms[0].elements[3]`` (7)

You can use Selenium itself as well as other sites and extensions to explore
你可以用Selenium或者其它的站点和扩展来捕获web应用程序的DOM.
在 `W3Schools
<http://www.w3schools.com/HTMLDOM/dom_reference.asp>`_  有一个不错的教程. 

通过CSS定位
~~~~~~~~~~~~~~~

CSS (Cascading Style Sheets) 是一种描述HTML和XML文档外观的语言.
CSS使用给文档中的元素加样式属性的选择器.这些选择器被Selenium用作另一种定位策略.

.. code-block:: html
  :linenos:

   <html>
    <body>
     <form id="loginForm">
      <input class="required" name="username" type="text" />
      <input class="required passfield" name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Clear" />
     </form>
   </body>
   <html>

- ``css=form#loginForm (3)``
- ``css=input[name="username"]`` (4)
- ``css=input.required[type="text"]`` (4)
- ``css=input.passfield (5)``
- ``css=#loginForm input[type="button"]`` (4)
- ``css=#loginForm input:nth-child(2)`` (5)

要想获得更多关于CSS选择器的信息,最好的地方是去 `the W3C
publication <http://www.w3.org/TR/css3-selectors/>`_.  你将获得更多参考信息.

.. note:: Most experienced Selenium users recommend CSS as their locating
   strategy of choice as it's considerably faster than XPath and can find the 
   most complicated objects in an intrinsic HTML document.
.. 注释:: 有经验的Selenium用户建议使用CSS作为他们定位策略的一种选择,因为它比
   XPath快速,而且在HTML文档中能找到很多复杂的对象.

.. _模式部分:      

文本模式匹配
----------------------

和定位器类似, *模式*  是Selenese命令中经常用到的一种参数类型.
需要模式的命令有:**verifyTextPresent**, **verifyTitle**, **verifyAlert**, 
**assertConfirmation**, **verifyText** 和 **verifyPrompt**.  
上面提及的链接locator也可以使用模式.模式允许 *描述* ,通过使用特定的字符,
期望的文本内容不必与指定文字完全相同.
有三种类型的模式: *文件名替换*, *正则表达式* 和 *完全*.

文件名替换模式
~~~~~~~~~~~~~~~~~

很多人熟悉文件名替换，因为它用在DOS或者Unix/Linux命令行的文件名扩展,像
``ls *.c`` 命令.
在这种情况下,文件名替换被用来显示所有以 ``.c`` 扩展的所有在当前目录下的文件.
文件名替换用处非常有限.
只有两个特殊字符在Selenium实现中被支持:

    **\*** 翻译为 "匹配所有," 如下,空,单个字符,或者多个字符.
    **[ ]** (*字符类*) 翻译为 "匹配在方括号里面的任何单个字符.",
    破折号(连字符)可以作为一种简写方式,来指定一个范围内的字符(这些字符在ASCII字符集中连续),
    以下几个例子可以清楚的说明字符类的功能.

    ``[aeiou]`` 匹配任意小写的vowel字符中的一个

    ``[0-9]`` 匹配任意一个数字

    ``[a-zA-Z0-9]`` 匹配任意字母和数字字符

在很多其它环境中,文件名替换包括第三个特殊字符 **?**.
但Selenium 文件名替换只支持星号和字符类.

在Selenese命令中指定文件名替换模式参数,需要加一个 **glob:** 标签前缀.
但是，因为文件名替换模式是默认的,所以你也可以省略掉这个标签而特指模式本身.

以下例子是用文件名替换模式的两个命令.实际的在页面上被测的链接文本是 
"Film/Television Department";通过使用一个模式而不是完全的文本, **click**
命令将起作用，即使这个文本变为 "Film & Television Department" 或者
"Film and Television Department".
文件名替换模式的星号将匹配在字 "Film" 和字 "Television"之间的 "任意的或空的" .

===========   ====================================    ========
click         link=glob:Film*Television Department
verifyTitle   glob:\*Film\*Television\*
===========   ====================================    ========

通过点击链接 "De Anza Film And Television Department - Menu"得到页面标题.
通过一个模式而不是完全的文本,只要两个字"Film" 和 "Television"在页面标题的任何位置,
命令 ``verifyTitle`` 将会通过.例如,当这页的标题缩短为"Film & Television Department,"
这个测试仍然通过.用一个模式用于链接和测试链接是否工作的简单的测试
(正如上面 ``verifyTitle`` 做的) 能有效的减少这类测试的维护成本.

正则表达式
~~~~~~~~~~~~~~~~~~~~~~~~~~~

*正则表达* 模式是Selenese支持的三类模式中功能最强的.
正则表达式在大多高级编程语言中支持,许多文本编辑器以及很多工具,包括
Linux/Unix的命令行工具 **grep**, **sed**和**awk** 也支持.
在Selenese中,正则表达式允许用户完成许多非常复杂的任务.
例如,假设你的测试需要测试确保一个特定表格内只包含数字.
``regexp: [0-9]+`` 是一个匹配任何长度数字的简短模式.

Selenese的文件名替换模式只支持 **\*** 和 **[ ]** (字符类)功能.
Selenese正则表达式提供在JavaScript存在的特定字符集范围.
以下是这些特殊字符集的一个子集.

=============     ======================================================================
    PATTERN            MATCH
=============     ======================================================================
   .              任意单字符
   [ ]            字符类: 位于括号内的任意字符 
   \*             量词: 0个或多个前面的字符(或组)
   \+             量词: 1个或多个前面的字符(或组)
   ?              量词: 0个或1个前面的字符(或组)
   {1,5}          量词: 1个到5个前面的字符(或组)
   \|             可选: 左边字符/组或右边字符/组
   ( )            分组：往往交替使用和/或量词
=============     ======================================================================

正则表达式在Selenese中需要以 ``regexp:`` 或 ``regexpi:`` 作前缀.  
前面这个是区分大小写的;后面这个不区分大小写.

很多例子将帮助你清晰地来了解正则表达式在Selenese是如何使用的.
第一个或许是最经常用到的正则表达式--**.\***("星号").
这两个字符序列翻译为 "0个或者多个字符"或者更简单的 "有或者没有"
它和一个字符的文件名替换模式 **\** (单个星号).

===========   =======================================    ========
click         link=regexp:Film.*Television Department
verifyTitle   regexp:.\*Film.\*Television.\*
===========   =======================================    ========

上面这个例子和之前用文件名替换模式用于同一个测试的实现的功能相同.
唯一的不同是前缀(**regexp:** 而不是 **glob:**) 和 "有或者没有"模式(
**.\*** 而不是 **\***).

下面这些更复杂些的测试例子是yahoo! 天气页导航, Alaska日出的信息:

==================  ===============================================    ========
open                http://weather.yahoo.com/forecast/USAK0012.html
verifyTextPresent   regexp:Sunrise: \*[0-9]{1,2}:[0-9]{2} [ap]m
==================  ===============================================    ========

让我们测试以上正则表达式一个时间:

==============   ====================================================
``Sunrise: *``   字符串 **Sunrise:** 后一个或者多个空格
``[0-9]{1,2}``   1个或者2个数字(一天中的小时)
``:``            字符 **:** (不涉及特殊字符)
``[0-9]{2}``     2个数字(分钟) 跟着1个空格
``[ap]m``        "a" 或者 "p" 跟着1个 "m" (am 或者 pm)
==============   ====================================================

完整模式
~~~~~~~~~~~~~~

Selenium 模式的 **完整** 类型用处比较有限.
它完全没有特殊字符.所以,如果想找一个真实的星号字符(这个字符对文件名替换和正则表达式
来说是特殊的), **完整** 模式是一个办法.例如,如果想选择在下拉框中的含有 "Real \*" 
的一个标签,下面的代码将可以或者不可以.在 ``glob:Real *`` 模式中的星号将匹配所有或者没有.
所以,如果在前面有个"Real Numbers,"标签的选择项.它将选择这个而不是 "Real \*"项.

===========   ====================================    =============
select        //select                                glob:Real \*
===========   ====================================    =============

为了确保"Real \*"项被选中, ``exact:`` 前缀将被用来产生一个 **完整** 模式如下:

===========   ====================================    =============
select        //select                                exact:Real \*
===========   ====================================    =============

但通过在正则表达式对星号转义,也可以达到同样的效果:
 
===========   ====================================    ================
select        //select                                regexp:Real \\\*
===========   ====================================    ================

大多数的测试员很少在字符集内部(文件名替换模式中的字符类)找一个星号或者一组方括号.
因此,文件名替换模式和正则表达式模式对我们大多是人来说足够了.


"AndWait" 命令集

----------------------

一个命令和它的 *AndWait* 可选项的不同在于:一个普通的命令(例如, *click*) 将执行这
个动作，然后接着执行接下来的命令,而 *AndWait* 可选项(例如, *clickAndWait*) 将
告诉Selenium 执行这个命令后 **等待** 页面加载.

.. TODO: mam-p:  I don't believe the following is true, at least in Selenium-
   IDE.  Perhaps it is supposed to be true, but I don't think we should 
   misrepresent the current status. 

*AndWait* 可选项经常用在当这个动作导致浏览器导航到另个页面或者重新加载当前这个页面. 

当心，如果你用一个 *AndWait* 命令执行不是执行一个导航/刷新动作,你的这个测试将失败.
这是因为Selenium将 等待 *AndWait* 的时间去看任何导航或者刷新执行,导致Selenium抛出一个
超时异常.
 

计算序列和流程控制
---------------------------------------

当一个脚步执行的时候,这个脚本是依次执行的,一个命令接着另一个命令.

Selenese本身并不支持条件语句(if-else等) 或者循环语句(for, while等). 
许多有用的测试可以在没有流程控制的情况下执行.
然而,对一个动态内容的功能性测试来说，可能涉及多个页面，编程逻辑的情况是经常需要的.

当流程控制需要的时候,有三种选择:  

a) 用Selenium-RC执行脚本,一个客户端库象Java或者PHP去使用编程语言的流程控制功能.
b) 在脚本中用StoreEval命令,运行一个小的JavaScript片段.
c) 安装 `goto_sel_ide.js <http://51elliot.blogspot.com/2008/02/selenium-ide-goto.html>`_ 扩展.

大多数的测试员将导出测试脚本为一种使用Selenium-RC API(见Selenium-IDE章节)的编程语言文件.
然而,有些组织更乐于尽可能的从Selenium-IDE运行他们的脚本(比如,当他们运行这些脚本的人
是初级水平的或者缺乏编程经验的),如果你是这种情况,可以考虑JavaScript片段或者goto_sel_ide.js扩展.  

 
Store命令集和Selenium 变量
-------------------------------------
可以用Selenium变量存储常量在脚本的开始.
当连接一个data-driven设计的时候(后面的部分会讨论),Selenium 变量可以用来存储值,
这些值是你的测试测试程序通过命令行,或者另个程序，或者通过一个文件传入的. 
 
简单的 *store* 命令是许多存储命令中最基本的,它能用来在Selenium变量中存储一个常量值. 
它需要两个参数,要存储的文本值和selenium变量.   用标准的变量命名规则,即用数字和字母来命名你的
变量.

.. TODO: mam-p:  Why are we telling them the last sentence above?  Any 
   JavaScript identifiershould be okay, no?

=====   ===============   ========
store   paul@mysite.org   userName               
=====   ===============   ========

在你后面的脚本中,你或许想用你存储值的变量.或者变量的值，将这个变量用括号 ({})括在中间,
前面加一个美元符号标识.

==========  =======     ===========
verifyText  //div/p     ${userName}               
==========  =======     ===========

一个通常的变量用法是存储input字段的输入值.

====    ========     ===========
type    id=login     ${userName}               
====    ========     ===========

Selenium变量可以用在第一个或者第二个参数中,它被Selenium编译,优先于这个命令中任何其他行为.
Selenium变量还可以被用在一个locator表达式中.

等价的存储命令存在于每个验证和判定中.  以下是经常用到的存储命令.

storeElementPresent 
~~~~~~~~~~~~~~~~~~~
This corresponds to verifyElementPresent.  It simply stores a boolean value--"true" 
or "false"--depending on whether the UI element is found.

storeText 
~~~~~~~~~
StoreText corresponds to verifyText.  It uses a locater to identify specific 
page text.  The text, if found, is stored in the variable.  StoreText can be 
used to extract text from the page being tested.

storeEval 
~~~~~~~~~
This command takes a script as its 
first parameter.  Embedding JavaScript within Selenese is covered in the next section.
StoreEval allows the test to store the result of running the script in a variable.


JavaScript and Selenese Parameters
----------------------------------
JavaScript can be used with two types of Selenese parameters--**script**
and non-script (usually expressions).  In most cases, you'll want to access 
and/or manipulate a test case variable inside the JavaScript snippet used as 
a Selenese parameter.  All variables created in your test case are stored in 
a JavaScript *associative array*.  An associative array has string indexes 
rather than sequential numeric indexes.  The associative array containing 
your test case's variables is named **storedVars**.  Whenever you wish to 
access or manipulate a variable within a JavaScript snippet, you must refer 
to it as **storedVars['yourVariableName']**.

JavaScript Usage with Script Parameters  
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Several Selenese commands specify a **script** parameter including
**assertEval**, **verifyEval**, **storeEval**, and **waitForEval**.
These parameters require no special syntax.  A Selenium-IDE
user would simply place a snippet of JavaScript code into
the  appropriate field, normally the **Target** field (because
a **script** parameter is normally the first or only parameter).

The example below illustrates how a JavaScript snippet
can be used to perform a simple numerical calculation:

===============    ============================================   ===========
store              10                                             hits
storeXpathCount    //blockquote                                   blockquotes
storeEval          storedVars['hits']-storedVars['blockquotes']   paragraphs
===============    ============================================   ===========

This next example illustrates how a JavaScript snippet can include calls to 
methods, in this case the JavaScript String object's ``toUpperCase`` method 
and ``toLowerCase`` method.  

===============    ============================================   ===========
store              Edith Wharton                                  name
storeEval          storedVars['name'].toUpperCase()               uc
storeEval          storedVars['name'].toLowerCase()               lc
===============    ============================================   ===========

JavaScript Usage with Non-Script Parameters  
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

JavaScript can also be used to help generate values for parameters, even
when the parameter is not specified to be of type **script**.  
However, in this case, special syntax is required--the JavaScript
snippet must be enclosed inside curly braces and preceded by the
label ``javascript``, as in ``javascript {*yourCodeHere*}``.
Below is an example in which the ``type`` command's second parameter 
``value`` is generated via JavaScript code using this special syntax:

===============    ============================================   ===========
store              league of nations                              searchString
type               q                                              javascript{storedVars['searchString'].toUpperCase()}
===============    ============================================   ===========

*echo* - The Selenese Print Command
------------------------------------
Selenese has a simple command that allows you to print text to your test's 
output.  This is useful for providing informational progress notes in your 
test which display on the console as your test is running.  These notes also can be 
used to provide context within your test result reports, which can be useful 
for finding where a defect exists on a page in the event your test finds a 
problem.  Finally, echo statements can be used to print the contents of 
Selenium variables.

=====   ========================   ========
echo    Testing page footer now.    
echo    Username is ${userName}                 
=====   ========================   ========


Alerts, Popups, and Multiple Windows
------------------------------------
*This section is not yet developed.*

.. Paul: This is an important area, people are constantly asking about this 
   on the forums.

AJAX and waitFor Commands
-------------------------

*This section is not yet developed.*

Many applications use AJAX for dynamic and animated functionality making 
testing of AJAX behavior often a basic testing requirement.

*This section is not yet developed.*



