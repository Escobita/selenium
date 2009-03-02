.. _chapter06-reference:

Test Design Considerations
==========================

Introducing Test Design Options
-------------------------------

Web Page Content -- Static vs. Dynamic Pages
--------------------------------------------

.. Tarun: Any one Please go through description below for Static vs Dynamic 
   and suggest improvement or any thing I have misunderstated.

.. Note: 
    This topic is covered as - Object identification for Static content and 
    Object identification for Dynamic contents. The examples described here 
    are specific to Java and should not be very different to implement.


Object Identification for Static HTML Objects       
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Static HTML Objects might look as:
           
.. code-block:: html

    <a class="button" id="adminHomeForm" onclick="return oamSubmitForm('adminHomeForm','adminHomeForm:_id38');" href="#">View Archived Allocation Events</a>

This is HTML snippet for a button and its id is "adminHomeForm". This id 
remains constant with the all occurrences of page. Hence to click this button 
use following selenium command:

.. code-block:: java
   :linenos:

    selenium.click("adminHomeForm");

Object identification with Dynamic HTML Objects
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dynamic HTML of an object might look as:
           
.. code-block:: html

<input type="checkbox" class="checkbox" style="margin-right: 10px;" value="true" id="addForm:_id74:_id75:0:_id79:0:checkBox" name="addForm:_id74:_id75:0:_id79:0:checkBox"/>

This is HTML snippet for a check box. Its id and name 
(addForm:_id74:_id75:0:_id79:0:checkBox) both are same and dynamic. Now in 
this case normal object identification like:

.. code-block:: java
   :linenos:

    selenium.click("addForm:_id74:_id75:0:_id79:0:checkBox);

would not work. Best way to capture this id dynamically from website it self. 
This can be as following:

.. code-block:: java
   :linenos:

   String[] checkboxIds  = selenium.getAllFields(); // Collect all input ids on page.
   if(GenericValidator.IsBlankOrNull(checkboxIds[i])) // If collected id is not null.
          {
                   // If id is of desired check box.
                   if(checkboxIds[i].indexOf("addForm") > -1) {                       
                       selenium.check(checkboxIds[i]);                    
                   }
           }

This approach will work only if there is one field whose id has got text 
'addForm' appended to it.

Consider one more examples of Dynamic object. A page with two links having the
same name (one which appears on page) and same html name. Now if href is used 
to click link it would always be clicking on first element. Click on second 
element link can be achieved as following:

.. code-block:: java
   :linenos:

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

    selenium.click(editInfo );
                   




Add Location Strategies
-----------------------
  
.. Dave: New suggested section. I've been documenting location strategies and 
   it's possible in RC to add new strategies. Maybe an advanced topic but 
   something that isn't documented elsewhere to my knowledge.

UI Mapping with Selenium
-------------------------

.. Tarun: My understanding of UI map is to have centralized location for 
   elements and test script uses the UI Map to locate elements.
   Paul: Do we know how this is used in Selenium?
   Santi: Yeah, there's a pretty used extension for this (UI-element), it's 
   also very well integrated with selenium IDE.
   Dave: I'd like to look into writing some documentation here.

Bitmap Comparison
------------------

.. Tarun: Bitmap comparison is about comparison of two images. This feature 
   is available in commercial web automation tools and helps in UI testing (or
   I guess so)

Recovery From Failure
---------------------

.. Tarun: Here Test attempt is re made against a website which comes up with 
   something unexpected i.e. pop up window or unexpected page etc, I guess 
   for selenium this largely depends on how tests are designed. Say in case 
   of java Try Catch Block might help achieving this.

Types of Tests 
--------------

* Page Rendering Tests 
* Forms Tests 
* Specific Function Tests - For example, if a .jsp is called from used to retrieve data based on parameter input. 
* User Scenario Test - A Multiple Page Functional Test 

Solving Common Web-App Problems 
-------------------------------

* Assert vs Verify (or possibly put this under script development) 
* Judgement calls, when to *verifyTextPresent*, *verifyElementPresent*, or 
  *verifyText*. 
* Handling Login/Logout State 
* Processing a Result Set 

Interpreting Test Results
-------------------------

.. Tarun: This topic and followed ones seem more general to me and probably 
   can be kept under 'Test Design Considerations'. Or may be we could branch 
   off a new index for it.

Organizing Your Test Scripts 
----------------------------

Organizing Your Test Suites 
----------------------------

Example Test Suites 
-------------------
