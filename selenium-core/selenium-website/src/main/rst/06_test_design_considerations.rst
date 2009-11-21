
Test Design Considerations 
==========================

.. _chapter06-reference:

*NOTE: This chapter is currently being developed.  We have some content here already though.  We decided not to hold back on information just because a chapter was not ready.*


Introducing Test Design
-----------------------

In this subsection we describe a few types of different tests you can do with Selenium.  This may not
be new to you, but we provide this as a framework for relating Selenium
test automation to the decisions a quality assurance professional will make when deciding what tests 
to perform, the priority for each of those tests, and whether to automate those tests or not.


What to Test?
-------------

What elements of your application will you test?  Of course, that depends on aspects of your
project--end-user expectations, time allowed for the project, priorities set by the project manager
and so on.  Once the project boundaries are defined though, you the tester will of course 
make many decisions on what aspects of the application to test.

We will define some terms here to help us categorize the types of testing typical for a web-application.
These terms are by no means standard in the industry, although the concepts we present here are typical
for web-applications.

   
Content Tests
~~~~~~~~~~~~~
The simplest type of test for a web-application is to simply test for the existence
of an static, non-changing, element on a particular page.  For instance

- Does each page have it's expected page title?  This can be used to verify your test found an expected page after following a link.
- Does the application's home page contain an image expected to be at the top of the page?  
- Does each page of the website contain a footer area with links to the company contact page,
- privacy policy, and trademarks information?  
- Does each page begin with heading text using the <h1> tag?  And, does each page have the correct text within that header?

You may or may not need content tests.  If your page content is not likely to be affected then it may be more efficient to test page content manually.  If, however, your application will be undergoing platform changes, or files will likely be moved to different locations, content tests may prove valuable.

Link Tests
~~~~~~~~~~
A frequent source of errors for web-sites is broken links and missing pages behind those broken links.  Testing for these involves clicking each link and veryfying the expected page behind that link loads correctly.

*Need to include a description of how to design this test and a simple example.  Should that go in this section or in a separate section?*  


Function Tests
~~~~~~~~~~~~~~
These would be tests of a specific function within your application, requiring some type of user input, and returning some type of results.  Often a function test will involve multiple pages with a form-based input page containing a collection of input fields, Submit and Cancel operations, and one or more response pages.  User input can be via text-input fields, checkboxes, drop-down lists, or anyother other browser-supported input.


Dynamic Content
~~~~~~~~~~~~~~~
Dynamic content is a set of page elements whose identifiers, that is, characteristics used to locate the element, vary with each different instance of the page that contains them.  This is usually on a result page of some given function.  

An example would be a result set of data returned to the user.  Suppose each data result, in, say for example a list of documents, had a unique identifier for each specific document.  So, for a particular search, the search results page returns a data set with one set of documents and their correponding identifiers.  Then, in a different search, the search results page returns a different data set where each document in the result set uses different identifiers.

An example will help.  Dynamic content involves UI elements who identifying properties change each time you open the page 
displaying them.  For example, ......

Dynamic HTML of an object might look as:
           
.. code-block:: html

    <input type="checkbox" value="true" id="addForm:_id74:_id75:0:_id79:0:checkBox" name="addForm:_id74:_id75:0:_id79:0:checkBox"/>

This is HTML snippet for a check box. Its id and name 
(addForm:_id74:_id75:0:_id79:0:checkBox) both are same and both are dynamic 
(they will change the next time you open the application). In this case


Ajax Tests
~~~~~~~~~~ 

Ajax is a technology which supports dynamic real-time UI elements such as animation and RSS feeds.
In AJAX-driven web applications, data is retrieved from the application server with out refreshing 
the page. 

*NOTE - INCLUDE A GOOD DEFINITION OF AJAX OFF THE INTERNET.*

Verifying Results
-----------------

Assert or Verify
~~~~~~~~~~~~~~~~

When should you use an assert command and when should you use a verify command?  This is up to you.
The difference is in what you want to happen when the check fails.  Do you want your test to terminate
or continue and record that the check failed?

Here's the tradeoff. If you use an assert, the test will stop at that point and not run any subsequent checks.  Sometimes, perhaps often, that is what you want.  If the test fails you will immediately know the test did not pass.  Test engines such as TestNG and JUnit have plugins for commonly used development environments (Chap 5) which conveniently flag these tests as failed tests.  The advantage:  you have an immediate visual of whether the checks (those using asserts anyway) passed.
The disadvantage:  when a check does fail, there are other checks which were never performed, so you have no information on their status.

In contrast, verify commands will not terminate the test.  If your test uses only verify commands you are guaranteed (assuming no unexpected exceptions) the test will run to completion whether the checks find defects in the AUT or not.  The disavantage:  you have to do more work to examine your test results.  That is, you won't get feedback from TestNG or JUnit.  Rather, you will need to look at the results of a console printout or a log output by your test application.  And you will need to take the time to look through this output everytime you run your test.  For Java, Logging Selenium (Chap 5) is a convenient logging utility for recording the results of verify commands, however you still need to open the logs and examine the results.  If you are running hundreds of tests, each with it's own log, this will be time-consuming. 

When to *verifyTextPresent*, *verifyElementPresent*, or *verifyText* 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


Choosing a Location Strategy
----------------------------

You know from the Selenese section there are multiple ways of selecting an object on a page.
But what are the tradeoffs of each of these locator types?  Recall we can locate an object using

- the element id
- the element name attribute
- an XPATH statement
- document object model (DOM)

Generally, using an Id locator is more efficient.  It also makes your test code more readable, assuming
the Id used by the AUT's page source is a meaningful one.  Using the name attribute also has similar advantages.  Finally, these also give the best performance.  XPATH statements have been known to be slow
in Internet Explorer due to limations of IE's XPATH processor.
  
Sometimes though, you must use an XPATH locator.  If the page source does not have an ID or name attribute you have no choice but to use a XPATH or DOM locator.  It appears at the time of writing that DOM locators are not commonly used now, and XPATH appears to the preferred choice, possibly because XPATH provide a rich set of possibilities for identifying an object--it is quite flexible.

There is an advantage to using XPATH or DOM that locating via ID or name attributes do not have. With
XPATH and DOM you can locate an object with respect to another object on the page.  For example, if 
there is a link that must occur within the second paragragh within a <div> section, you can use XPATH or DOM to specify this.  With ID and name locators, you can only specify that they occur on the page--somewhere on the page.  If you must test that an image displaying the company logo appears at 
the top of the page within a header section XPATH may be the better locator. 


Identifying Dynamic Objects
~~~~~~~~~~~~~~~~~~~~~~~~~~~

*This section has not been reviewed or edited.*

First, you must understand what a dynamic object is, and to do so, we will contrast that with a static object.  Until now, all the AUT page elements we have been considering have been static objects.  These are objects who's html page source is the same each time the page is loaded in the browser.

The difference between a Static HTML Objects might look as:
           
.. code-block:: html

    <a class="button" id="adminHomeForm" onclick="return oamSubmitForm('adminHomeForm','adminHomeForm:_id38');" href="#">View Archived Allocation Events</a>

This is HTML snippet for a button and its id is "adminHomeForm". This id remains
constant within all instances of this page. That is, when this page is displayed, 
this UI element will always have this identifier.  So, for your test script to click this button you just
have to use the following selenium command.

.. code-block:: java

    selenium.click("adminHomeForm");

These are UI elements who identifying properties change each time you open the page 
displaying them.  For example, ......

Dynamic HTML of an object might look as:
           
.. code-block:: html

    <input type="checkbox" value="true" id="addForm:_id74:_id75:0:_id79:0:checkBox" name="addForm:_id74:_id75:0:_id79:0:checkBox"/>

This is HTML snippet for a check box. Its id and name 
(addForm:_id74:_id75:0:_id79:0:checkBox) both are same and both are dynamic 
(they will change the next time you open the application). In this case
normal object identification would look like:

.. code-block:: java

    selenium.click("addForm:_id74:_id75:0:_id79:0:checkBox);

Given the dynamic nature of id this approach would not work. The best way is 
to capture this id dynamically from the website itself. It can be done as:

.. code-block:: java

   String[] checkboxIds  = selenium.getAllFields(); // Collect all input ids on page.
   if(!GenericValidator.IsBlankOrNull(checkboxIds[i])) // If collected id is not null.
          {
                   // If the id starts with addForm
                   if(checkboxIds[i].indexOf("addForm") > -1) {                       
                       selenium.check(checkboxIds[i]);                    
                   }
           }

.. Santi: I'm not sure if this is a good example... We can just do this by
   using a simple CSS or XPATH locator.
   
.. Tarun: Please elaborate more on css locators.   

This approach will work only if there is one field whose id has got the text 
'addForm' appended to it.

Consider one more example of a Dynamic object. A page with two links having the
same name (one which appears on page) and same html name. Now if href is used 
to click the link, it would always be clicking on first element. Click on second
element link can be achieved as following:

.. code-block:: java

    // Flag for second appearance of link.
    boolean isSecondInstanceLink = false;
    
    // Desired link.
    String editInfo = null;

    // Collect all links.
    String[] links = selenium.getAllLinks();

    // Loop through collected links.
    for(String linkID: links) {

        // If retrieved link is not null
        if(!GenericValidator.isBlankOrNull(linkID))  {

            // Find the inner HTML of link.
            String editTermSectionInfo = selenium.getEval("window.document.getElementById('"+linkID+"').innerHTML");

            // If retrieved link is expected link.
            if(editTermSectionInfo.equalsIgnoreCase("expectedlink")) {

                // If it is second appearance of link then save the link id and break the loop.
                if(isSecondInstanceLink) {
                    editInfo = linkID;
                    break;
                }

            // Set the second appearance of Autumn term link to true as
            isSecondInstanceLink = true;
            }
        }
    }
    
    // Click on link.
    selenium.click(editInfo);
                   


How can I avoid using complex xpath expressions to my test?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If the elements in HTML (button, table, label, etc) have element IDs, 
then one can reliably retrieve all elements without ever resorting
to xpath. These element IDs should be explicitly created by the application.
But non-descriptive element ID (i.e. id_147) tends to cause two problems: 
first, each time the application is deployed, different element ids could be generated. 
Second, a non-specific element id makes it hard for automation testers to keep 
track of and determine which element ids are required for testing.

You might consider trying the `UI-Element`_ extension in this situation.

.. _`UI-Element`: http://wiki.openqa.org/display/SIDE/Contributed+Extensions+and+Formats#ContributedExtensionsandFormats-UIElementLocator

Locator Performance
~~~~~~~~~~~~~~~~~~~

Custom Locators
~~~~~~~~~~~~~~~
  
*This section is not yet developed.*

  
.. Dave: New suggested section. I've been documenting location strategies and 
   it's possible in RC to add new strategies. Maybe an advanced topic but 
   something that isn't documented elsewhere to my knowledge.



Testing Ajax Applications
-------------------------


Waiting for an AJAX Element
~~~~~~~~~~~~~~~~~~~~~~~~~~~
In AJAX-driven web applications, using Selenium's *waitForPageToLoad* wouldn't work as 
the page is not actually 
loaded to refresh the AJAX element. Pausing the test execution for a specified period of time is also not a good 
approach as web element might appear later or earlier than expected leading
to invalid test failures (reported failures that aren't actually failures). 
A better approach would be to wait for a predefined 
period and then continue execution as soon as the element is found.

For instance, consider a page which brings a link (link=ajaxLink) on click of a button 
on page (without refreshing the page)
This could be handled by Selenium using a *for* loop. 

.. code-block:: bash
   
   // Loop initialization.
   for (int second = 0;; second++) {
	
	// If loop is reached 60 seconds then break the loop.
	if (second >= 60) break;
	
	// Search for element "link=ajaxLink" and if available then break loop.
	try { if (selenium.isElementPresent("link=ajaxLink")) break; } catch (Exception e) {}
	
	// Pause for 1 second.
	Thread.sleep(1000);
	
   } 

   
   
UI Mapping
----------

A UI map is a centralized location for an application's UI elements and then the 
test script uses the UI Map for locating elements to be tested.

.. Santi: Yeah, there's a pretty used extension for this (UI-element), it's 
   also very well integrated with selenium IDE.   

A UI map is a repository, that is, a storage location, for all test script objects.  UI maps have several advantages.

- Having centralized location for UI objects instead of having them scattered 
  through out the script.  This makes script maintanence easier and more efficient.
- Cryptic HTML identifiers and names can be given more human-readable increasing the 
  readability of test scripts.

Consider following example (in java) of selenium tests for a website: 

.. code-block:: java

   public void testNew() throws Exception { 
   		selenium.open("http://www.test.com");
   		selenium.type("loginForm:tbUsername", "xxxxxxxx");
   		selenium.click("loginForm:btnLogin");
   		selenium.click("adminHomeForm:_activitynew");
   		selenium.waitForPageToLoad("30000");
   		selenium.click("addEditEventForm:_idcancel");
   		selenium.waitForPageToLoad("30000");
   		selenium.click("adminHomeForm:_activityold");
   		selenium.waitForPageToLoad("30000");
   } 
   
There is hardly any thing comprehensible from script. 
Even the regular users of application would not be able to figure out 
as to what script does. A better script would have been:
   
.. code-block:: java

   public void testNew() throws Exception {
   		selenium.open("http://www.test.com");
   		selenium.type(admin.username, "xxxxxxxx");
   		selenium.click(admin.loginbutton);
   		selenium.click(admin.events.createnewevent);
   		selenium.waitForPageToLoad("30000");
   		selenium.click(admin.events.cancel);
   		selenium.waitForPageToLoad("30000");
   		selenium.click(admin.events.viewoldevents);
   		selenium.waitForPageToLoad("30000");
   }
   
Though again there are no comments provided in the script but it is
more comprehensible because of the keywords used in scripts. (please
beware that UI Map is not a replacement for comments!) A more comprehensible 
script could look like this.
   
.. code-block:: java

   public void testNew() throws Exception {

		// Open app url.
   		selenium.open("http://www.test.com");
   		
   		// Provide admin username.
   		selenium.type(admin.username, "xxxxxxxx");
   		
   		// Click on Login button.
   		selenium.click(admin.loginbutton);
   		
   		// Click on Create New Event button.
   		selenium.click(admin.events.createnewevent);
   		selenium.waitForPageToLoad("30000");
   		
   		// Click on Cancel button.
   		selenium.click(admin.events.cancel);
   		selenium.waitForPageToLoad("30000");
   		
   		// Click on View Old Events button.
   		selenium.click(admin.events.viewoldevents);
   		selenium.waitForPageToLoad("30000");
   }
   
The whole idea is to have a centralized location for objects and using 
comprehensible names for those objects. To achieve this, properties files can 
be used in java. A properties file contains key/value pairs, where each 
key and value are strings.
   
Consider a property file *prop.properties* which has got definition of 
HTML object used above 
   
.. code-block:: java
   
   admin.username = loginForm:tbUsername
   admin.loginbutton = loginForm:btnLogin
   admin.events.createnewevent = adminHomeForm:_activitynew
   admin.events.cancel = addEditEventForm:_idcancel
   admin.events.viewoldevents = adminHomeForm:_activityold
   
Our objects still refer to html objects, but we have introduced a layer 
of abstraction between the test script and UI elements.
Values can be read from the properties file and used in Test Class to implement UI 
Map. For more on Properties files follow this URL_.

.. _URL: http://java.sun.com/docs/books/tutorial/essential/environment/properties.html

Bitmap Comparison
------------------
*This section has not been developed yet.*

.. Tarun: Bitmap comparison is about comparison of two images. This feature 
   is available in commercial web automation tools and helps in UI testing (or
   I guess so)
   Santi: I'm not really sure how this can be achieved using Selenium. The only
   idea that I have right now is calculating the checksum of the image and 
   comparing that with the one of the image that should be present there, like:

   <pseudocode>
     img_url = sel.get_attribute("//img[@src]")
     image = wget(img_url)
     assertEqual(get_md5(image), "MD5SUMEXPECTED12341234KJL234")
   </pseudocode>

   But I've never implemented this before...

.. Santi: Isn't the "Advanced Selenium" chapter better for this topic to be 
   placed on?




Solving Common Web-App Problems 
-------------------------------
*This section has not been developed yet.*

* Handling Login/Logout State 
* Processing a Result Set 



Organizing Your Test Scripts 
----------------------------
*This section has not been developed yet.*


Organizing Your Test Suites 
----------------------------
*This section has not been developed yet.*

Data Driven Testing
~~~~~~~~~~~~~~~~~~~
*This section needs an introduction and it has not been completed yet.*

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

Why would we want a separate file with data in it for our tests?  One 
important method of testing concerns running the same test repetetively with 
differnt data values.  This is called *Data Driven Testing* and is a very 
common testing task.  Test automation tools, Selenium included, generally 
handle this as it's often a common reason for building test automation to 
support manual testing methods.

The Python script above opens a text file.  This file contains a different search
string on each line. The code then saves this in an array of strings, and at last,
it's iterating over the strings array and doing the search and assert on each.

This is a very basic example of what you can do, but the idea is to show you
things that can easily be done with either a programming or scripting 
language when they're difficult or even impossible to do using Selenium-IDE.

Refer to `Selnium RC wiki`_ for examples on reading data from spread sheet or using
data provider capabilities of TestNG with java client driver.

.. _`Selnium RC wiki`: http://wiki.openqa.org/pages/viewpage.action?pageId=21430298


Handling Errors
---------------

*Note: This section is not yet developed.*

Error Reporting
~~~~~~~~~~~~~~~


Recovering From Failure
~~~~~~~~~~~~~~~~~~~~~~~

A quick note though--recognize that your programming language's exception-
handling support can be used for error handling and recovery.

.. TODO: Complete this... Not sure if the scenario that I put is the best example to use
.. Then, what if google.com is down at the moment of our tests? Even if that sounds
   completely impossible. We can create a recovery scenario for that test. We can
   make our tests to wait for a certain amount of time and try again:

.. The idea here is to use a try-catch statement to grab a really unexpected
   error.

*This section has not been developed yet.*

.. Tarun: Here Test attempt is re made against a website which comes up with 
   something unexpected i.e. pop up window or unexpected page etc, I guess 
   for selenium this largely depends on how tests are designed. Say in case 
   of java Try Catch Block might help achieving this.

.. Santi: Isn't the "Advanced Selenium" chapter better for this topic to be 
   placed on?

   
   
Database Validations
~~~~~~~~~~~~~~~~~~~~~

Since you can also do database queries from your favorite programming 
language, assuming you have database support functions, why not using them
for some data validations/retrieval on the Application Under Test?

Consider example of Registration process where in registered email address
is to be retrieved from database. Specific cases of establishing DB connection 
and retrieving data from DB would be:

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
   
This is very simple example of data retrieval from DB in Java.
A more complex test could be to validate that inactive users are not able
to login to application. This wouldn't take too much work from what you've 
already seen.
