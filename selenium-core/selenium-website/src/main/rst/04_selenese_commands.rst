.. _chapter04-reference:

"Selenese" Selenium Commands 
=============================

.. Dave: Is this basically a reworking and extension of the existing 'Selenium 
   Reference'? -Dave 1/6/09 3:49 PM

.. Paul: No, we are going to give them lots more.  Basically how to choose the right 
   command to do a specific job.  I can help you with this if you need it.  This 
   like this....when would I use verifyTexPresent vs. verifyText? Also, the 
   command reference has NO examples.  We'll give them examples.  You may need 
   to take sometime of a sample website.  We can help them know how to choose 
   certain commands for certain jobs.
   We can also give them tradeoffs of different commands, and limitations

.. Dave: Sounds good. Perhaps we can use the seleniumhq.org website for our examples?

How to Choose a Selenium Command
--------------------------------

.. Paul: This may not be the best title for the introduction of this section, 
   so Dave, don't let this one mislead you.  You can change this if you need to. 
.. Dave: I like this topic, but like you say it might not be the best title. 
   Perhaps 'Choosing the right Selenium command'
.. Paul: Dave, I could probably write this section.  Would you like me to? 

What to Test?
-------------

.. Paul: So if given a page...Would they test every single item on the page?  
   Not normally.  Would the check the content of each paragraph?  Depends on 
   the paragraph.  Like, paragraphs on a company contact page probably won't 
   change frequently and they may want to test the text itself.  A different 
   page, they may want to test the paragraph exists.  Another page where the 
   content is constantly changed by web-designers, they may only want to test 
   the heading and page title.

.. Dave: So a discussion of checks vs risk. If content is high importance to 
   your scenario then it should be tested, if it is inconsequential or 
   covered by other similar scenarios then either a superficial check is 
   enough or no check at all (again, depending ont he importance of the 
   content).

.. Paul: Yes, all of that.  but also, the user must keep in mind what their 
   test goal is.  What IS it that they REALLY need to check.  I can give a 
   good example from my current experience.....We check page rendering and 
   for broken links, I call these our "UI Tests".   Then we're building "
   smoke tests" for basic functionality that follow the common user scenarios.
   For our UI Tests, our web-designers frequently (with a capital F) change 
   content.  Mostly for testing page rendering I test for structure rather 
   than content.  But 'stable content' such as the corporate content info, 
   copywrite statements, company logo images, etc, I would test the actual 
   content since they don't change frequently and they are highly important 
   for the company image.  We can include these kinds of decisions-tradeoffs 
   in this section.


Assertion or Verification? 
--------------------------

Choosing between **assert** and **verify** comes down to convenience and 
management of failures. There's very little point checking that the first 
paragraph on the page is the correct if your test has already failed when 
checking that the browser is displaying the expected page. If you're not on 
the correct page you'll probably want to abort your test case so that you can 
investigate the cause and fix the issue(s) promptly. On the other hand, you 
may want to check many attributes of a page without aborting the test case on 
the first failure as this will allow you to review all failures on the page 
and take the appopriate action. Effectively an **assert** will fail the test 
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

.. Paul: Dave did you write this above section?  This is exactly what I had 
   in mind also. Nice job!  I couldn't have written it better.

.. Dave: Cool. We must be on the same wavelength!

Locating Elements 
-----------------

For many Selenium commands a target is required. This target identifies an 
element in the content of the web application, and consists of the location 
strategy followed by the location in the format ``locatorType=location``. The 
locator type can be omitted and one of the default strategies will be used 
depending on the initial characters of the location. The various locator 
types are explained below with examples for each.

Locating by identifier
~~~~~~~~~~~~~~~~~~~~~~

This is probably the most common method of locating elements and is the catch-
all default when no recognised locator type is used. With this strategy the 
element with the @id attribute value matching the location will found. If no 
element has a matching @id attribute then the first element with a @name 
attribute matching the location will be found.

.. Paul: Are you illustrating a locator in a Sel command?  I think you are.  
   Let's put it in the context of a command like verifyText or 
   verifyElementPresent.  It needs context.  I was confused at first what 
   this was doing here.

.. Dave:    I agree that this section needs context.

- identifier=loginForm
- identifier=username
- identifier=continue

.. TODO: Colors here!

For instance, your page source could have identifier (ID) and name attributes 
as follows:
           
.. code-block:: html

  <html>
   <body>
    <form id="loginForm">
     <input name="username" type="text" />
     <input name="password" type="password" />
     <input name="continue" type="submit" value="Login" />
     <input name="continue" type="button" value="Continue" />
    </form>
   </body>
  <html>

Locating by id 
~~~~~~~~~~~~~~

More limited than the identifier locator type but also more explicit. Use 
this when you know an element's @id attribute.

- id=loginForm

.. TODO: Colors here!

.. code-block:: html

   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Continue" />
     </form>
    </body>
   <html>


.. Paul: There's an important use of this, and similar locators.  These vs. 
   xpath allow Selenium to test UI elements independent of it's location on 
   the page.  So if the page structure and organization is altered, the test 
   will still pass.  One may, or may not, want to also test whether the page 
   structure changes.  In the case where web-designers frequently alter the 
   page, but it's functionality must be regression tested, testing via ID and 
   NAME attribs, or really via any HTML property becomes very important.

Locating by name 
~~~~~~~~~~~~~~~~

Similar to the identifier locator type when an @id attribute is not found, 
the name locator type will locate the first element with a matching @name 
attribute. If multiple elements have the same value for a name attribute then 
you can use filters to further refine your location strategy. The default 
filter type is value (matching the @value attribute).

.. Paul: I'm indenting your examples and making the Courier New--hope ya don't
   mind! Actually, they look like they're already in Courier front, but I'm 
   adding that explicetly as Google Docs couldn't seem to figure out what the 
   font was

.. Dave: These are just quick examples and I expected them to be refined. 
   Style is fine as you have it.

- name=username
- name=continue
- name=continue Continue
- name=continue value=Continue
- name=continue type=button

.. TODO: Colors here!

.. code-block:: html

   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Continue" />
     </form>
   </body>
   <html>

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

- xpath=/html/body/form[1] - *Absolute path (would break if the HTML was 
  changed only slightly)*
- xpath=//form[1] - *First form element in the HTML*
- xpath=//form[@id='loginForm'] - *The form element with @id of 'loginForm'*
- xpath=//form[input/\@name='username'] - *First form element with an input child
  element with @name of 'username'*
- xpath=//input[@name='username'] - *First input element with @name of 
  'username'*
- xpath=//form[@id='loginForm']/input[1] - *First input child element of the 
  form element with @id of 'loginForm'*
- xpath=//input[@name='continue'][@type='button'] - *Input with @name 'continue'
  and @type of 'button'*
- xpath=//form[@id='loginForm']/input[4] - *Fourth input child element of the 
  form element with @id of 'loginForm'*


.. TODO: Colors here!

.. code-block:: html

   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Continue" />
     </form>
   </body>
   <html>


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

.. Dave: We could have screenshots of using these add-ons or incorporate the 
   short guide here http://seleniumhq.org/projects/core/xpath-help.html or 
   this may be out of scope.

Locating hyperlinks by link text 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This is a simple method of locating a hyperlink in your web page by using the 
text of the link. If two links with the same text are present then the first 
match will be used.

- link=Continue
- link=Cancel

.. TODO: Colors here!

.. code-block:: html

  <html>
   <body>
    <p>Are you sure you want to do this?</p>
    <a href="continue.html">Continue</a> 
    <a href="cancel.html">Cancel</a>
  </body>
  <html>

Locating by DOM  
~~~~~~~~~~~~~~~

The Document Object Model represents a HTML document and can be accessed 
using JavaScript. This location strategy takes JavaScript that evaluates to 
an element on the page, which can be simply the element's location using the 
hierarchical dotted notation.

- dom=document.getElementById('loginForm')
- dom=document.forms['loginForm']
- dom=document.forms[0]
- dom=document.forms[0].username
- dom=document.forms[0].elements['username']
- dom=document.forms[0].elements[0]
- dom=document.forms[0].elements[3]

.. TODO: Colors here!
           
.. code-block:: html

   <html>
    <body>
     <form id="loginForm">
      <input name="username" type="text" />
      <input name="password" type="password" />
      <input name="continue" type="submit" value="Login" />
      <input name="continue" type="button" value="Continue" />
     </form>
   </body>
   <html>


You can use Selenium itself as well as other sites and extensions to explore 
the DOM of your web application. A good reference exists on `W3Schools 
<http://www.w3schools.com/HTMLDOM/dom_reference.asp>`_. 

.. Dave: This topic was written fairly quickly as I feel it's XPath's poor 
   cousin.

Locating by CSS
~~~~~~~~~~~~~~~

.. Santiago: This is a great replace for the slow XPATH locators and it hasn't
   been documented at all (also, there's a los of info around the web) -
   Santiago Suarez Ordoñez 1/6/09 12:20 PM  

.. Dave: I used one of these yesterday and it was really simple, I'll take a 
   look at writing this topic soon. This is the next topic that I'll be 
   working on.

The "AndWait" commands 
----------------------
The difference that any user should see between a command and it's *AndWait*
alternative is that the regular command (e.g. *click*) will do the action and
continue with the following command as fast as it can. While the *AndWait*
alternative (e.g. *clickAndWait*) tells Selenium to **wait** for the page to
load after the action has been done. 

The *andWait* alternative is always used when an action causes the browser to
navigate to another page or reload the present one. 

verifyTextPresent
-----------------

.. Paul: Use this when only when one is concerned about the text itself, that 
   it is present on the page, but it's position on the page is not important 
   for the verification.

verifyElementPresent
--------------------
 
.. Paul: Use this when the presence of the UI element, that is, the HTML tag 
   is what is important to the test.  Use this when the text itself is not 
   relevent. This is also used to verify an img exists, or that a link exists.
   Can also be used to verify items in a dynamic list (like of returned 
   search results) exist on the page.

verifyText
----------
 
.. Paul: Use this when not only the text itself must be checked, but also it's
   position on the page.

 echo 
 ----
 
.. Paul: Useful for debugging a script.  Also very useful for documenting 
   each section of a test and dumping that to an output log.  This can be 
   very useful for identifying bugs when verifications in a script fail.  I 
   can come up with an example if you guys need me to.

 Selenium Variables
 ------------------
 
.. Paul: Do you guys use these?  I've been using them a lot lately.  I can 
   get some examples from our scripts at work and modify them for this 
   section if you need some examples.

 Store Commands
 --------------
 
.. Paul: These are really valiable.  I use them to set constants at the top 
   of my scripts.  Particularly username and password, but also various pre-
   known properties that need to be validated as 'expected results' of a test.
   That approach is also one step away from data-driven testing as these 
   present variable values can then be easily edited in Sel-RC to take values 
   passed by a test app from the command line or a file read into the app.

store 
-----

storeText 
---------

storeElementPresent 
-------------------

storeEval 
---------

Javascript Expression as a Parameter 
------------------------------------

.. Paul: Whoops, we need a section on Patterns for matching text, and 
   particularly should mention RegExp's

Alerts, Popups, and Multiple Windows
------------------------------------

.. Paul: This is an important area, people are constantly asking about this 
   on the forums.

AJAX and waitFor commands
-------------------------

.. Santiago: Nowadays, most of the applications has some AJAX and tends to be 
   a basic requirement for lots of tests, we should cover this topic as soon 
   as we can

Sequence of Evaluation
-----------------------

In what order does Selenium process each of these parameter components?  

.. Paul: There 's some stuff on the Reference about which order the 
   interpreter uses to evaluate locators.  We may need to do some research 
   though.  I haven't though about this one much, other than to be thinking 
   that we should think about it.

Example Test Script
-------------------

.. Paul: My idea here was to have a simple sample web page or two, with a 
   sample script, that demonstrated how to select the right command for 
   different elements of the website.  We would need to create this, any real 
   website would prob be to complex.
