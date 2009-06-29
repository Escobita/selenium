.. Santi: I'm not sure about this whole chapter. It looks like most of the content
   should be placed on the "Advanced Selenium" chapter instead of here. Maybe we 
   can merge both chapters, it'd keep advanced topics where anyone will expect.

.. Paul: This chapter is about all the tradeoffs a person must make when 
   building their tests.  All the decisions they will have to make.  How to 
   choose a test for a given testing situation.  There's a lot that could go here.

Test Design Considerations 
==========================

.. _chapter06-reference:

*NOTE: This chapter is not finished.  We have some content here already though.  We decided not to hold back on information just because a chapter was not ready.*



Introducing Test Design Options
-------------------------------
*This section is not yet developed.*


Web Page Content -- Static vs. Dynamic Pages
--------------------------------------------
*This section is not yet developed.*

.. Tarun: Any one Please go through description below for Static vs Dynamic 
   and suggest improvement or any thing I have misunderstated.
   
.. Paul:  Actually, I've got some vision for this section, in presenting the 
   different decisions and types of testing a person will need to consider.  
   There's, I think, a sizable sub-section in here.  No time to think about 
   it now though.  I'll also need to read what you guys wrote on Object 
   Identification as that will influence my ideas on this topic.

This topic is explained as - Object identification for Static content and 
Object identification for Dynamic contents. The examples described here 
are specific to Java and must have equivalent in the language of your choice.


Object Identification for Static HTML Objects       
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*This section has not been reviewed or edited.*

Static HTML Objects might look as:
           
.. code-block:: html

    <a class="button" id="adminHomeForm" onclick="return oamSubmitForm('adminHomeForm','adminHomeForm:_id38');" href="#">View Archived Allocation Events</a>

This is HTML snippet for a button and its id is "adminHomeForm". This id remains
constant within the all occurrences of page. Hence to click this button you just
have to use the following selenium command:

.. code-block:: java

    selenium.click("adminHomeForm");

Object identification with Dynamic HTML Objects
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*This section has not been reviewed or edited.*

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
   if(GenericValidator.IsBlankOrNull(checkboxIds[i])) // If collected id is not null.
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

    // Stores reference for second appearance of link.
    boolean isSecondInstanceLink = false;

    // Collect all links.
    String[] links = selenium.getAllLinks();

    // Loop through collected links.
    for(String linkID: links) {

        // If retrieved link is not null
        if(!GenericValidator.isBlankOrNull(linkID))  {

            // Find the inner HTML of collected links.
            editTermSectionInfo = selenium.getEval("window.document.getElementById('"+linkID+"').innerHTML");

            // If retrieved link is expected link.
            if(editTermSectionInfo.equalsIgnoreCase("expectedlink")) {

                // If it is second appearance of link then save the link id.
                if(isSecondInstanceAutumnLink) {
                    editInfo = linkID;
                }

            // Set the second appearance of Autumn term link to true as
            isSecondInstanceLink = true;
            }
        }
    }
    
    // Click on collected element.
    selenium.click(editInfo);
                   




Location Strategies
-----------------------
  
*This section is not yet developed.*

  
.. Dave: New suggested section. I've been documenting location strategies and 
   it's possible in RC to add new strategies. Maybe an advanced topic but 
   something that isn't documented elsewhere to my knowledge.

UI Mapping with Selenium
-------------------------
*This section has not been reviewed or edited.*


.. Tarun: My understanding of UI map is to have centralized location for 
   elements and test script uses the UI Map to locate elements.
   Paul: Do we know how this is used in Selenium?
   Santi: Yeah, there's a pretty used extension for this (UI-element), it's 
   also very well integrated with selenium IDE.
   Dave: I'd like to look into writing some documentation here.

.. Santi: Isn't the "Advanced Selenium" chapter better for this topic to be 
   placed on?
   

UI Map is a repository for all Objects of test scripts.
Advantages of using UI Maps are:

- Having centralized location for UI objects instead of having them scattered 
  through out the script.
- Centralized location of objects makes maintenance of Tests easy.
- Arcane HTML ids and names can be given comprehensible names and it increases 
  readability of scripts.

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
beware that UI Map is not replacement of comments) So a more comprehensible 
script would look as following -
   
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
   
Herein whole idea is to have centralized location for objects and using 
comprehensible names for objects. To achieve this properties files can 
be used in java. Properties file contains key/value pairs, where in 
key and value both are String values.
   
Consider a property file *prop.properties* which has got definition of 
HTML object used above 
   
.. code-block:: java
   
   admin.username = loginForm:tbUsername
   admin.loginbutton = loginForm:btnLogin
   admin.events.createnewevent = adminHomeForm:_activitynew
   admin.events.cancel = addEditEventForm:_idcancel
   admin.events.viewoldevents = adminHomeForm:_activityold
   
Hence still our objects refer to html objects but we have introduced a layer 
of abstraction between test script and UI elements.
Values can be read from properties file and used in Test Class to implement UI 
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


Recovery From Failure
---------------------
*This section has not been developed yet.*

.. Tarun: Here Test attempt is re made against a website which comes up with 
   something unexpected i.e. pop up window or unexpected page etc, I guess 
   for selenium this largely depends on how tests are designed. Say in case 
   of java Try Catch Block might help achieving this.

.. Santi: Isn't the "Advanced Selenium" chapter better for this topic to be 
   placed on?

Types of Tests 
--------------
.. Paul:  I have a lot of vision for this section.  And it is 'introductory' 
   and probably will go towards the beginning of this chapter.  I've got some 
   ideas here based on how we organized our tests at my work.

*This section has not been developed yet.*


* Page Rendering Tests 
* Forms Tests 
* Specific Function Tests - For example, if a .jsp is called from used to 
  retrieve data based on parameter input. 
* User Scenario Test - A Multiple Page Functional Test 

Solving Common Web-App Problems 
-------------------------------
*This section has not been developed yet.*

.. Paul:  This sub-section was my idea.  We may have some duplication here 
   with Chapter 4.  But there's also some ideas here that are more appropriate
   for this chapter.  I can take the lead on this sub-section and then have 
   you guys add to it.  Unless, of course, this list is already sparking 
   ideas in you.  If so, go ahead and add them here before I add mine and we'll reconcile.

* Assert vs Verify (or possibly put this under script development) 
* Judgement calls, when to *verifyTextPresent*, *verifyElementPresent*, or 
  *verifyText*. 
* Handling Login/Logout State 
* Processing a Result Set 

Interpreting Test Results
-------------------------
*This section has not been developed yet.*

.. Tarun: This topic and followed ones seem more general to me and probably 
   can be kept under 'Test Design Considerations'. Or may be we could branch 
   off a new index for it.

Organizing Your Test Scripts 
----------------------------
*This section has not been developed yet.*


Organizing Your Test Suites 
----------------------------
*This section has not been developed yet.*


Example Test Suites 
-------------------
.. Paul:  This probably is not a separate section, but a sub-sub-section of '
   Organizing Your Test Suites'.  And really?  do we want to give them a 
   whole test suite example?  We may be able to handle this just fine with small
   snippets.

*This section has not been developed yet.*
