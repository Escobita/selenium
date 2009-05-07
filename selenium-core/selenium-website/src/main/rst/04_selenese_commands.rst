.. _chapter04-reference:

"Selenese" Selenium Commands 
=============================
Selenium commands, often called *selenese*, is the set of commands that runs 
your tests.  A sequence of these commands is a *test script*.  Here we explain 
those commands in detail, and we present the many choices you have in testing 
your web-application when using Selenium.


Verifying Page Elements
------------------------
Verifying *UI elements* on a web-page is probably the most common feature of 
your automated tests.  Selenese allows multiple ways of checking for UI 
elements.  It is important that you understand these different methods because
these methods define what you are actually testing.

For example, will you test for?

a) an element is present somewhere on the page.
b) specific text is somewhere on the page.
c) specific text is at a specific location on the page.

So, for example, if you are testing a text heading, the text, and it's position
at the top of the page, is probably relevant for your test.  If, however, you 
are testing for the existence of an image on the home page, and the 
web-designers frequently change the specific image file along with it's position
on the page, then you only want to test that *an image* (as opposed to the 
specific image file) exists *somewhere on the page*.
   
   
Assertion or Verification? 
~~~~~~~~~~~~~~~~~~~~~~~~~~
Choosing between **assert** and **verify** comes down to convenience and 
management of failures. There's very little point checking that the first 
paragraph on the page is the correct if your test has already failed when 
checking that the browser is displaying the expected page. If you're not on 
the correct page you'll probably want to abort your test case so that you can 
investigate the cause and fix the issue(s) promptly. On the other hand, you 
may want to check many attributes of a page without aborting the test case on 
the first failure as this will allow you to review all failures on the page 
and take the appropriate action. Effectively an **assert** will fail the test 
and abort the current test case, whereas a **verify** will fail the test and 
continue to run the testcase. 

The best use of this feature is to logically group your test commands, and 
start each group with an **assert** followed by one or more **verify** test 
commands. An example follows:

============    =====   ============
open                    /download/      
assertTitle             Downloads       
verifyText      //h2    Downloads       
assertTable     1.2.1   Selenium IDE    
verifyTable     1.2.2   June 3, 2008    
verifyTable     1.2.3   1.0 beta 2      
============    =====   ============

The above example first opens a page and then **asserts** that the correct page 
is loaded by comparing the title with the expected value, only if this passes 
will the following command run and **verify** that the text is present in the 
expected location. The test case then **asserts** the first column in the second
row of the first table meets the expected value, and only if this passed with 
the remaining cells in that row be **verified**.


verifyTextPresent
~~~~~~~~~~~~~~~~~
The command ``verifyTextPresent`` is used to verify *specific text exists 
somewhere on the page*.  It takes a single argument--the text pattern to be 
verified.  For example.

=================   ==================   ============
verifyTextPresent   Marketing Forcasts               
=================   ==================   ============

This would cause Selenium to search for, and verify, that the text string
"Marketing Analysis" appears somewhere on the page currently being tested. Use
``verifyTextPresent`` when only when interested in only the text 
itself and only that it is present on the page.  Do not use this when you also need to test 
where it occurs on the page. 

verifyElementPresent
~~~~~~~~~~~~~~~~~~~~
Use this command when you must test for the presence of a specific UI 
element, rather then it's content.  This verification does not check the text
, it checks for the HTML tag.  For example, one common use is to check for an 
image. 

====================   ==================   ============
verifyElementPresent   //div/p/img               
====================   ==================   ============
   
This command verifies that an image, specified by the existance of an <img> 
HTML tag is present on the page, and that it follows a <div> tag and a <p> tag.
The second parameter is a *locator* for telling the Selenese command how to 
find the element.  Locators are explained in next section.  

``verifyElementPresent`` can be used to check the existence of any HTML tag 
within the page. One can check existence of links, paragraphs, divisions 
<div>, etc.  Here's a couple more examples.  

====================   ==============================   ============
verifyElementPresent   //div/p 
verifyElementPresent   //div/a               
verifyElementPresent   id=Login
verifyElementPresent   link=Go to Marketing Research               
verifyElementPresent   //a[2]
verifyElementPresent   //head/title
====================   ==============================   ============

These examples illustrate the variety of ways a UI element may be tested.  
Again, locators are explained in the next section.

verifyText
~~~~~~~~~~
 
Use ``verifyText`` when both the text and it's UI element must be tested.
``verifyText`` must use a locator.  Depending on the locator used (an *xpath* or DOM
locator) one can verify that specific text appears at a specific location on the
page relative to other UI components on the page which contain it.

==========   ===================    ===================================================================
verifyText   //table/tr/td/div/p 	This is my text and it occurs right after the div inside the table.
==========   ===================    ===================================================================


.. _locators-section:

Locating Elements 
-----------------
For many Selenium commands a target is required. This target identifies an 
element in the content of the web application, and consists of the location 
strategy followed by the location in the format ``locatorType=location``. The 
locator type can be omitted and one of the default strategies will be used 
depending on the initial characters of the location. The various locator types
are explained below with examples for each.

.. Santi: I really liked how this section was taken. But I found that most of
   the locator strategies repeat the same HTML fragment over a over. Couldn't
   we put A example HTML code before starting with each strategie and then use
   that one on all of them?

Default locators 
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

Locating by identifier
~~~~~~~~~~~~~~~~~~~~~~

This is probably the most common method of locating elements and is the 
catch-all default when no recognised locator type is used. With this strategy
the element with the @id attribute value matching the location will be found. If
no element has a matching @id attribute then the first element with a @name 
attribute matching the location will be found.

For instance, your page source could have identifier (ID) and name attributes 
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

Locating by id 
~~~~~~~~~~~~~~

More limited than the identifier locator type but also more explicit. Use 
this when you know an element's @id attribute.

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

.. note:: There's an important use of this, and similar locators.  These vs. 
   xpath allow Selenium to test UI elements independent of it's location on 
   the page.  So if the page structure and organization is altered, the test 
   will still pass.  One may, or may not, want to also test whether the page 
   structure changes.  In the case where web-designers frequently alter the 
   page, but it's functionality must be regression tested, testing via ID and 
   name attributes, or really via any HTML property becomes very important.

Locating by name 
~~~~~~~~~~~~~~~~

Similar to the identifier locator type when an @id attribute is not found, 
the name locator type will locate the first element with a matching @name 
attribute. If multiple elements have the same value for a name attribute then 
you can use filters to further refine your location strategy. The default 
filter type is value (matching the @value attribute).

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
- ``name=continue Clear`` (7)
- ``name=continue value=Clear`` (7)
- ``name=continue type=button`` (7)

.. _locating-by-xpath:

Locating by XPath 
~~~~~~~~~~~~~~~~~

XPath is the language used for locating nodes in an XML document. As HTML can 
be an implementation of XML (XHTML) Selenium users can leverage this powerful 
language to target elements in their web applications. XPath extends beyond (
as well as supporting) the simple methods of locating by @id or @name 
attributes, and opens up all sorts of new possibilities such as locating the 
third checkbox on the page or similar. 

.. Dave: Is it worth mentioning the varying support of XPath (native in 
   Firefox, using Google AJAXSLT or the new method in IE)? Probably an 
   advanced topic if needed at all..?

One of the main reasons for using XPath is when you don't have a suitable @id 
or @name attribute for the element you wish to locate. You can use XPath to 
either locate the element in absolute terms (not advised), or relative to an 
element that does have an @id or @name attribute.

Absolute XPaths contain the location of all elements from the root (html) and 
as a result are likely to fail with only the slightest adjustment to the 
application. By finding a nearby element with an @id or @name attribute (ideally
a parent element) you can locate your target element based on the relationship.
This is much less likely to change and can make your tests more robust.

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

- ``xpath=/html/body/form[1]`` (3) - *Absolute path (would break if the HTML was 
  changed only slightly)*
- ``xpath=//form[1]`` (3) - *First form element in the HTML*
- ``xpath=//form[@id='loginForm']`` (3) - *The form element with @id of 'loginForm'*
- ``xpath=//form[input/\@name='username']`` (4) - *First form element with an input child
  element with @name of 'username'*
- ``xpath=//input[@name='username']`` (4) - *First input element with @name of 
  'username'*
- ``xpath=//form[@id='loginForm']/input[1]`` (4) - *First input child element of the 
  form element with @id of 'loginForm'*
- ``xpath=//input[@name='continue'][@type='button']`` (7) - *Input with @name 'continue'
  and @type of 'button'*
- ``xpath=//form[@id='loginForm']/input[4]`` (7) - *Fourth input child element of the 
  form element with @id of 'loginForm'*

These examples cover some basics, but in order to really take advantage the 
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
* `Firebug <https://addons.mozilla.org/en-US/firefox/addon/1843>`_ - very 
  useful, XPath suggestions are just one of the many powerful features of 
  this add-on.

Locating hyperlinks by link text 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This is a simple method of locating a hyperlink in your web page by using the 
text of the link. If two links with the same text are present then the first 
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

The Document Object Model represents a HTML document and can be accessed 
using JavaScript. This location strategy takes JavaScript that evaluates to 
an element on the page, which can be simply the element's location using the 
hierarchical dotted notation.

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
- ``dom=document.forms[0].username`` (4)
- ``dom=document.forms[0].elements['username']`` (4)
- ``dom=document.forms[0].elements[0]`` (4)
- ``dom=document.forms[0].elements[3]`` (7)

You can use Selenium itself as well as other sites and extensions to explore
the DOM of your web application. A good reference exists on `W3Schools
<http://www.w3schools.com/HTMLDOM/dom_reference.asp>`_. 

Locating by CSS
~~~~~~~~~~~~~~~

CSS (Cascading Style Sheets) is a language for describing the rendering of HTML
and XML documents. CSS uses Selectors for binding style properties to elements
in the document. This Selectors can be used by Selenium as another locating 
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
publication <http://www.w3.org/TR/css3-selectors/>`_ you'll find additional
references there.

.. note:: Most experimented Selenium users recommend CSS as their locating
   strategy of choice as it's considerably faster than xpath and can find the 
   most complicated objects in an intrinsic HTML document.

Order of Locators Evaluation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
*This section still needs to be developed.  Please refer to the Selenium 
Command Reference on the SeleniumHq.org website.*

Matching Text Patterns
----------------------
Another topic, almost as important as locating UI elements, is understanding 
how to match text patterns on the page.  There are multiple ways this can be done.

.. regexp: vs. glob: vs. exact: patterns

*This section is not yet developed.  Please refer to the Selenium Reference 
at www.SeleniumHQ.org*

 
The "AndWait" commands 
----------------------
The difference between a command and it's *AndWait*
alternative is that the regular command (e.g. *click*) will do the action and
continue with the following command as fast as it can. While the *AndWait*
alternative (e.g. *clickAndWait*) tells Selenium to **wait** for the page to
load after the action has been done. 

The *andWait* alternative is always used when the action causes the browser to
navigate to another page or reload the present one. 

Be aware, if you use an *AndWait* command for an action that
does not trigger a navigation/refresh, your test will fail. This happens 
because Selenium will reach the *AndWait*'s timeout without seeing any 
navigation or refresh being made, this is why Selenium raises a timeout 
exception.
 

Sequence of Evaluation and Flow Control
---------------------------------------

When a script runs, it simply runs in sequence, one command after another.

Selenese, by itself, does not handle condition statements (if-else, etc.) or 
iteration (for, while, etc.). Many useful tests can be conducted without flow 
control, however for a functional test of dynamic content, possibly involving
multiple pages, programming logic is often needed.

When flow control is needed there are two options.  

	a) run the script using Sel-RC to take advantage of a programming language.
	b) run a small Javascript snippet from within the script using the storeEval command.

Most will export the test script into a programming language file that uses the
Selenium-RC API (see the Selenium-IDE chapter).  However, some organizations prefer
to run their scripts from Selenium-IDE whenever possible (such as when they have
many junior-level people running tests for them, or when programming skills are
lacking). If this is your case, consider a Javascript snippet.  However, this 
will not handle iteration. So, for example, if your test needs to iterate 
through a variable-lenght result-set of values, you will need Selenium-RC.

 
Store Commands and Selenium Variables
-------------------------------------
For instance, one can use Selenium variables to store constants at the 
beginning of a script.  Also, when combined with a data-driven test design 
(discussed in a later section) Selenium variables can be used to store values 
passed to your test program from the command-line or from another program.
 
The *store* is used to the most basic of the store commands and can be used 
to simply store a constant value in a selenium variable.  It takes two 
parameters, the text value to be stored and a selenium variable.  Use the 
standard variable naming conventions of only alphanumeric characters when 
choosing a name for your variable.

=====   ===============   ========
store   paul@mysite.org	  userName               
=====   ===============   ========

Later in your script of course you'll want to use the stored values of your 
variable.  To cause a variable to return it's value enclose the variable in 
curly brackets ({}) and precede it with a dollar sign like this.

==========  =======     ===========
verifyText  //div/p     ${userName}               
==========  =======     ===========

A common use of variables is for storing input for an input field.

====    ========     ===========
type	id=login	 ${userName}               
====    ========     ===========

Selenium variables can be used in either the first or second parameter and are interpreted by Selenium prior to any other operations performed by the command.  A Selenium variable may also even be used from within a locator expression.

An equivalent store command corresponds to each verify command and assert command.  Here are a couple more commonly used store commands.

storeElementPresent 
~~~~~~~~~~~~~~~~~~~
This corresponds to verifyElementPresent.  It simply stores a boolean value, 
"true" or "false" depending on whether the UI element is found.

storeText 
~~~~~~~~~
StoreText corresponds to verifyText.  It uses a locater to identify specific 
page text.  The text, if found, is stored in the variable.  StoreText can be 
used to extract text from the page being tested.


storeEval 
~~~~~~~~~
This command takes an expression, generally a javascript expression as its 
first parameter.  Embedding javascript within Selenese is in the next section.
StoreEval allows the test to store the expression's result within a variable.


Javascript Expressions as a Parameter 
-------------------------------------
*This section is not yet developed.*


*echo* - The Selenese Print Command
------------------------------------
Selenese has a simple command that allows you to print text to your test's 
output.  This is useful for providing informational progress notes in your 
test which display on the console as your test is running.  They also can be 
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

AJAX and waitFor commands
-------------------------
Many applications use AJAX for dynamic and animated functionality making 
testing of Ajax behavior often a basic testing requirement.

*This section is not yet developed.*
