.. _第四章:
"Selenese" Selenium 命令集
=============================
Selenium 命令集,通常称作 *selenese*,是运行你的测试的一组命令集合。
这些命令的组合就是 *测试脚本*.
现在我们详细解释一下这些命令,告诉你一些在用Selenium测试web应用的时候用到的例子。


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
再次说明,locaotos将在下部分说明.

verifyText
~~~~~~~~~~
.. TODO mam-p:  Why the parenthetical limitation on locator type below?  The locator could also be name=, id=, identifier=, etc.

Use ``verifyText`` when both the text and its UI element must be tested.
``verifyText`` must use a locator.  If one chooses an *XPath* or *DOM*
locator, one can verify that specific text appears at a specific location on the
page relative to other UI components on the page.

==========   ===================    ===================================================================
verifyText   //table/tr/td/div/p    This is my text and it occurs right after the div inside the table.
==========   ===================    ===================================================================


.. _locators-section:

Locating Elements 
-----------------
For many Selenium commands, a target is required. This target identifies an 
element in the content of the web application, and consists of the location 
strategy followed by the location in the format ``locatorType=location``. The 
locator type can be omitted in many cases.
The various locator types
are explained below with examples for each.

.. Santi: I really liked how this section was taken. But I found that most of
   the locator strategies repeat the same HTML fragment over a over. Couldn't
   we put A example HTML code before starting with each strategie and then use
   that one on all of them?

Default Locators 
~~~~~~~~~~~~~~~~
You can choose to omit the locator type in the following situations:
 
 - Locators starting with "document" will use the DOM locator strategy. 
   See :ref:`locating-by-dom`.

 - Locators starting with "//" will use the XPath locator strategy. 
   See :ref:`locating-by-xpath`.

 - Locators that start with anything other than the above or a valid locator 
   type will default to using the identifier locator strategy. 
   See :ref:`locating-by-identifier`.

.. _locating-by-identifier:

Locating by Identifier
~~~~~~~~~~~~~~~~~~~~~~
This is probably the most common method of locating elements and is the 
catch-all default when no recognised locator type is used. With this strategy,
the first element with the id attribute value matching the location will be used. If
no element has a matching id attribute, then the first element with an name 
attribute matching the location will be used.

For instance, your page source could have id and name attributes 
as follows:
           
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

The following locator strategies would return the elements from the HTML 
snippet above indicated by line number:

- ``identifier=loginForm`` (3)
- ``identifier=username`` (4)
- ``identifier=continue`` (5)
- ``continue`` (5)

Since the ``identifier`` type of locator is the default, the ``identifier=``
in the first three examples above is not necessary.

Locating by Id 
~~~~~~~~~~~~~~
This type of locator is more limited than the identifier locator type, but 
also more explicit. Use this when you know an element's id attribute.

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

Locating by Name 
~~~~~~~~~~~~~~~~
The name locator type will locate the first element with a matching name 
attribute. If multiple elements have the same value for a name attribute, then 
you can use filters to further refine your location strategy. The default 
filter type is value (matching the value attribute).  

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
   types of locators above allow Selenium to test a UI element independent 
   of its location on 
   the page.  So if the page structure and organization is altered, the test 
   will still pass.  One may or may not want to also test whether the page 
   structure changes.  In the case where web designers frequently alter the 
   page, but its functionality must be regression tested, testing via id and 
   name attributes, or really via any HTML property, becomes very important.

.. _locating-by-xpath:

Locating by XPath 
~~~~~~~~~~~~~~~~~
XPath is the language used for locating nodes in an XML document. As HTML can 
be an implementation of XML (XHTML), Selenium users can leverage this powerful 
language to target elements in their web applications. XPath extends beyond (as 
well as supporting) the simple methods of locating by id or name 
attributes, and opens up all sorts of new possibilities such as locating the 
third checkbox on the page.

.. Dave: Is it worth mentioning the varying support of XPath (native in 
   Firefox, using Google AJAXSLT or the new method in IE)? Probably an 
   advanced topic if needed at all..?

One of the main reasons for using XPath is when you don't have a suitable id 
or name attribute for the element you wish to locate. You can use XPath to 
either locate the element in absolute terms (not advised), or relative to an 
element that does have an id or name attribute.  XPath locators can also be
used to specify elements via attributes other than id and name.

Absolute XPaths contain the location of all elements from the root (html) and 
as a result are likely to fail with only the slightest adjustment to the 
application. By finding a nearby element with an id or name attribute (ideally
a parent element) you can locate your target element based on the relationship.
This is much less likely to change and can make your tests more robust.

Since only ``xpath`` locators start with "//", it is not necessary to include
the ``xpath=`` label when specifying an XPath locator.

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

- ``xpath=/html/body/form[1]`` (3) - *Absolute path (would break if the HTML was 
  changed only slightly)*
- ``//form[1]`` (3) - *First form element in the HTML*
- ``xpath=//form[@id='loginForm']`` (3) - *The form element with @id of 'loginForm'*
- ``xpath=//form[input/\@name='username']`` (4) - *First form element with an input child
  element with @name of 'username'*
- ``//input[@name='username']`` (4) - *First input element with @name of 
  'username'*
- ``//form[@id='loginForm']/input[1]`` (4) - *First input child element of the 
  form element with @id of 'loginForm'*
- ``//input[@name='continue'][@type='button']`` (7) - *Input with @name 'continue'
  and @type of 'button'*
- ``//form[@id='loginForm']/input[4]`` (7) - *Fourth input child element of the 
  form element with @id of 'loginForm'*

These examples cover some basics, but in order to learn more, the 
following references are recommended:

* `W3Schools XPath Tutorial <http://www.w3schools.com/Xpath/>`_ 
* `W3C XPath Recommendation <http://www.w3.org/TR/xpath>`_
* `XPath Tutorial 
  <http://www.zvon.org/xxl/XPathTutorial/General/examples.html>`_ 
  - with interactive examples. 

There are also a couple of very useful Firefox Add-ons that can assist in 
discovering the XPath of an element:

* `XPath Checker 
  <https://addons.mozilla.org/en-US/firefox/addon/1095?id=1095>`_ - suggests 
  XPath and can be used to test XPath results. 
* `Firebug <https://addons.mozilla.org/en-US/firefox/addon/1843>`_ -  XPath 
  suggestions are just one of the many powerful features of this very useful add-on.

Locating Hyperlinks by Link Text 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. TODO: mam-p:  Users often are unaware that a link locator is a pattern, 
   not just a string.  So, I think we need an example here that necessitates 
   a link=regexp:pattern locator in the test case.

This is a simple method of locating a hyperlink in your web page by using the 
text of the link. If two links with the same text are present, then the first 
match will be used.

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

.. _locating-by-dom:

Locating by DOM  
~~~~~~~~~~~~~~~

The Document Object Model represents an HTML document and can be accessed 
using JavaScript. This location strategy takes JavaScript that evaluates to 
an element on the page, which can be simply the element's location using the 
hierarchical dotted notation.

Since only ``dom`` locators start with "document", it is not necessary to include
the ``dom=`` label when specifying a dom locator.

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
the DOM of your web application. A good reference exists on `W3Schools
<http://www.w3schools.com/HTMLDOM/dom_reference.asp>`_. 

Locating by CSS
~~~~~~~~~~~~~~~

CSS (Cascading Style Sheets) is a language for describing the rendering of HTML
and XML documents. CSS uses Selectors for binding style properties to elements
in the document. These Selectors can be used by Selenium as another locating 
strategy.

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

For more information about CSS Selectors, the best place to go is `the W3C 
publication <http://www.w3.org/TR/css3-selectors/>`_.  You'll find additional
references there.

.. note:: Most experienced Selenium users recommend CSS as their locating
   strategy of choice as it's considerably faster than XPath and can find the 
   most complicated objects in an intrinsic HTML document.

.. _patterns-section:

Matching Text Patterns
----------------------

Like locators, *patterns* are a type of parameter frequently required by Selenese
commands.  Examples of commands which require patterns are **verifyTextPresent**,
**verifyTitle**, **verifyAlert**, **assertConfirmation**, **verifyText**, and 
**verifyPrompt**.  And as has been mentioned above, link locators can utilize 
a pattern.  Patterns allow one to *describe*, via the use of special characters,
what text is expected rather than having to specify that text exactly.

There are three types of patterns: *globbing*, *regular expressions*, and *exact*.

Globbing Patterns
~~~~~~~~~~~~~~~~~

Most people are familiar with globbing as it is utilized in
filename expansion at a DOS or Unix/Linux command line such as ``ls *.c``.
In this case, globbing is used to display all the files ending with a ``.c`` 
extension that exist in the current directory.  Globbing is fairly limited.  
Only two special characters are supported in the Selenium implementation:

    **\*** which translates to "match anything," i.e., nothing, a single character, or many characters.

    **[ ]** (*character class*) which translates to "match any single character 
    found inside the square brackets." A dash (hyphen) can be used as a shorthand
    to specify a range of characters (which are contiguous in the ASCII character
    set).  A few examples will make the functionality of a character class clear:

    ``[aeiou]`` matches any lowercase vowel

    ``[0-9]`` matches any digit

    ``[a-zA-Z0-9]`` matches any alphanumeric character

In most other contexts, globbing includes a third special character, the **?**.
However, Selenium globbing patterns only support the asterisk and character
class.

To specify a globbing pattern parameter for a Selenese command, one can
prefix the pattern with a **glob:** label.  However, because globbing
patterns are the default, one can also omit the label and specify just the
pattern itself.

Below is an example of two commands that use globbing patterns.  The
actual link text on the page being tested
was "Film/Television Department"; by using a pattern
rather than the exact text, the **click** command will work even if the
link text is changed to "Film & Television Department" or "Film and Television
Department".  The glob pattern's asterisk will match "anything or nothing"
between the word "Film" and the word "Television".

===========   ====================================    ========
click         link=glob:Film*Television Department
verifyTitle   glob:\*Film\*Television\*
===========   ====================================    ========

The actual title of the page reached by clicking on the link was "De Anza Film And
Television Department - Menu".  By using a pattern rather than the exact
text, the ``verifyTitle`` will pass as long as the two words "Film" and "Television" appear
(in that order) anywhere in the page's title.  For example, if 
the page's owner should shorten
the title to just "Film & Television Department," the test would still pass.
Using a pattern for both a link and a simple test that the link worked (such as
the ``verifyTitle`` above does) can greatly reduce the maintenance for such
test cases.

Regular Expression Patterns
~~~~~~~~~~~~~~~~~~~~~~~~~~~

*Regular expression* patterns are the most powerful of the three types
of patterns that Selenese supports.  Regular expressions
are also supported by most high-level programming languages, many text
editors, and a host of tools, including the Linux/Unix command-line
utilities **grep**, **sed**, and **awk**.  In Selenese, regular
expression patterns allow a user to perform many tasks that would
be very difficult otherwise.  For example, suppose your test needed
to ensure that a particular table cell contained nothing but a number.
``regexp: [0-9]+`` is a simple pattern that will match a decimal number of any length.

Whereas Selenese globbing patterns support only the **\*** 
and **[ ]** (character
class) features, Selenese regular expression patterns offer the same
wide array of special characters that exist in JavaScript.  Below 
are a subset of those special characters:

=============     ======================================================================
    PATTERN            MATCH
=============     ======================================================================
   .              any single character
   [ ]            character class: any single character that appears inside the brackets 
   \*             quantifier: 0 or more of the preceding character (or group)
   \+             quantifier: 1 or more of the preceding character (or group)
   ?              quantifier: 0 or 1 of the preceding character (or group)
   {1,5}          quantifier: 1 through 5 of the preceding character (or group)
   \|             alternation: the character/group on the left or the character/group on
                  the right
   ( )            grouping: often used with alternation and/or quantifier
=============     ======================================================================

Regular expression patterns in Selenese need to be prefixed with
either ``regexp:`` or ``regexpi:``.  The former is case-sensitive; the
latter is case-insensitive.

A few examples will help clarify how regular expression patterns can
be used with Selenese commands.  The first one uses what is probably
the most commonly used regular expression pattern--**.\*** ("dot star").  This
two-character sequence can be translated as "0 or more occurrences of
any character" or more simply, "anything or nothing."  It is the
equivalent of the one-character globbing pattern **\*** (a single asterisk).

===========   =======================================    ========
click         link=regexp:Film.*Television Department
verifyTitle   regexp:.\*Film.\*Television.\*
===========   =======================================    ========

The example above is functionally equivalent to the earlier example
that used globbing patterns for this same test.  The only differences
are the prefix (**regexp:** instead of **glob:**) and the "anything
or nothing" pattern (**.\*** instead of just **\***).

The more complex example below tests that the Yahoo!
Weather page for Anchorage, Alaska contains info on the sunrise time:

==================  ===============================================    ========
open                http://weather.yahoo.com/forecast/USAK0012.html
verifyTextPresent   regexp:Sunrise: \*[0-9]{1,2}:[0-9]{2} [ap]m
==================  ===============================================    ========

Let's examine the regular expression above one part at a time:

==============   ====================================================
``Sunrise: *``   The string **Sunrise:** followed by 0 or more spaces
``[0-9]{1,2}``   1 or 2 digits (for the hour of the day)
``:``            The character **:** (no special characters involved)
``[0-9]{2}``     2 digits (for the minutes) followed by a space
``[ap]m``        "a" or "p" followed by "m" (am or pm)
==============   ====================================================

Exact Patterns
~~~~~~~~~~~~~~

The **exact** type of Selenium pattern is of marginal usefulness.
It uses no special characters at all.  So, if one needed to look for
an actual asterisk character (which is special for both globbing and
regular expression patterns), the **exact** pattern would be one way
to do that.  For example, if one wanted to select an item labeled
"Real \*" from a dropdown, the following code might work or it might not.
The asterisk in the ``glob:Real *`` pattern will match anything or nothing.
So, if there was an earlier select option labeled "Real Numbers," it would
be the option selected rather than the "Real \*" option.

===========   ====================================    =============
select        //select                                glob:Real \*
===========   ====================================    =============

In order to ensure that the "Real \*" item would be selected, the ``exact:``
prefix could be used to create an **exact** pattern as shown below:

===========   ====================================    =============
select        //select                                exact:Real \*
===========   ====================================    =============

But the same effect could be achieved via escaping the asterisk in a
regular expression pattern:
 
===========   ====================================    ================
select        //select                                regexp:Real \\\*
===========   ====================================    ================

It's rather unlikely that most testers will ever need to look for
an asterisk or a set of square brackets with characters inside them (the
character class for globbing patterns).  Thus, globbing patterns and
regular expression patterns are sufficient for the vast majority of us.


The "AndWait" Commands 
----------------------

The difference between a command and its *AndWait*
alternative is that the regular command (e.g. *click*) will do the action and
continue with the following command as fast as it can, while the *AndWait*
alternative (e.g. *clickAndWait*) tells Selenium to **wait** for the page to
load after the action has been done. 

.. TODO: mam-p:  I don't believe the following is true, at least in Selenium-
   IDE.  Perhaps it is supposed to be true, but I don't think we should 
   misrepresent the current status. 

The *AndWait* alternative is always used when the action causes the browser to
navigate to another page or reload the present one. 

Be aware, if you use an *AndWait* command for an action that
does not trigger a navigation/refresh, your test will fail. This happens 
because Selenium will reach the *AndWait*'s timeout without seeing any 
navigation or refresh being made, causing Selenium to raise a timeout 
exception.
 

Sequence of Evaluation and Flow Control
---------------------------------------

When a script runs, it simply runs in sequence, one command after another.

Selenese, by itself, does not support condition statements (if-else, etc.) or 
iteration (for, while, etc.). Many useful tests can be conducted without flow 
control. However, for a functional test of dynamic content, possibly involving
multiple pages, programming logic is often needed.

When flow control is needed, there are three options:  

a) Run the script using Selenium-RC and a client library such as Java or
   PHP to utilize the programming language's flow control features.
b) Run a small JavaScript snippet from within the script using the storeEval command.
c) Install the `goto_sel_ide.js <http://51elliot.blogspot.com/2008/02/selenium-ide-goto.html>`_ extension.

Most testers will export the test script into a programming language file that uses the
Selenium-RC API (see the Selenium-IDE chapter).  However, some organizations prefer
to run their scripts from Selenium-IDE whenever possible (such as when they have
many junior-level people running tests for them, or when programming skills are
lacking). If this is your case, consider a JavaScript snippet or the goto_sel_ide.js extension.  

 
Store Commands and Selenium Variables
-------------------------------------
One can use Selenium variables to store constants at the 
beginning of a script.  Also, when combined with a data-driven test design 
(discussed in a later section), Selenium variables can be used to store values 
passed to your test program from the command-line, from another program, or from
a file.
 
The plain *store* command is the most basic of the many store commands and can be used 
to simply store a constant value in a selenium variable.  It takes two 
parameters, the text value to be stored and a selenium variable.  Use the 
standard variable naming conventions of only alphanumeric characters when 
choosing a name for your variable.

.. TODO: mam-p:  Why are we telling them the last sentence above?  Any 
   JavaScript identifiershould be okay, no?

=====   ===============   ========
store   paul@mysite.org   userName               
=====   ===============   ========

Later in your script, you'll want to use the stored value of your 
variable.  To access the value of a variable, enclose the variable in 
curly brackets ({}) and precede it with a dollar sign like this.

==========  =======     ===========
verifyText  //div/p     ${userName}               
==========  =======     ===========

A common use of variables is for storing input for an input field.

====    ========     ===========
type    id=login     ${userName}               
====    ========     ===========

Selenium variables can be used in either the first or second parameter and 
are interpreted by Selenium prior to any other operations performed by the 
command.  A Selenium variable may also be used within a locator expression.

An equivalent store command exists for each verify and assert command.  Here 
are a couple more commonly used store commands.

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



