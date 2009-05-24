.. _contributing-reference:

.. Santi: Here we will put additional info about how to start contributing
   to the project, including the forum, Jira, and some documentation about the
   code and the repo structure.
   
   We've talked about this here: http://clearspace.openqa.org/message/60142

Getting Involved with Selenium
==============================
Selenium is certainly a team effort! There are several ways you can help out,
whether you're a programmer, designer, QA engineer, writer, project manager, or
just willing to help. If you're interested in helping, the best way to connect
with us is in the forums, in the Selenium Developers section. We'll work with
you to get you set up to contribute.

You can help by...

Answering Questions in the Forums_
----------------------------------
We get a lot of questions and we have a great community to help answer them.
If you could register and log into the forums_ to answer a question or two, that
would be great! There are often some very basic questions that are easy to 
answer.

Updating our Website
--------------------
We have a lot of out-of-date information that needs to be tidied up on the site!
Help by writing documentation, producing helpful diagrams, re-skinning the
website, or organizing content.

You can get started by using Subversion to check out our content at 
https://svn.openqa.org/svn/selenium-core/selenium-website. To get it running,
you'll need to install a recent version of Java (if you don't have it already)
and `Apache Maven`_, which is used to build the website and run it in a web server
called "Jetty" (Maven will download Jetty when it's needed.) Once you have it
installed, run ``mvn jetty:run`` to start up Maven and Jetty and you'll have a 
website live on your computer at http://localhost:8080/. If you're on Windows,
you may want to make a batch script like this:

.. code-block:: bat

    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_02
    SET PATH=%PATH%;C:\Program Files\apache-maven-2.0.9\bin
    mvn jetty:run

and then save it to the selenium-website folder (Maven uses the pom.xml
configuration file there to set everything up).

Feel free to post questions on the `Selenium Developers`_ section of the forums.

Writing Documentation
---------------------
.. TODO: Update this section with everything we are doing for the new docs

For the most part, our documentation is held in source control, on the wiki,
or generated as part of the build(s). The main documentation is in plain HTML
so use your favorite editor offline to edit the pages. The source for this main
site is  here_ and if you are familiar with Subversion you should be able to
bring it down to your local system for offline editing.

You can also help with the documentation of `Selenium Core`_, `Selenium
Remote Control`_, `Selenium IDE`_, and `Selenium Grid`_ (see above for how to 
set up Maven and Jetty locally to try it out on your own computer).

Bug Reports and Feature Requests
--------------------------------
If you run into a problem with Selenium, feel free to file a bug report about
it on JIRA_, our Bug Tracker. Similarly, if you have an idea for a feature you'd 
like to see in a future version, report that as well.

Also, we would love help going through the bug reports and adding comments (which 
automatically get sent to the reporter) for cases where advice is all that is needed.  
When creating issues in JIRA, the following templates should be used:

Bug template::

    Description
    ===========
    ...
    Steps to reproduce
    ==================
    ...
    Expected
    ========
    ...
    Actual
    ======
    ...

Feature / change request template::

    Description
    ===========
    ...
    Reason
    ======
    ...
    Benefits
    ========
    ...
    Drawbacks
    =========
    ...

.. note:: Although these templates help the clear structuring of an issue, please
   keep in mind that bugs have to be reproducible and feature / change 
   requests have to be understandable! 

If you're a programmer and are skilled in the language in question, then dive
in and see if you can fix the bug (see `Contributing Code to Selenium`_).

.. _JIRA: http://jira.openqa.org/

Contributing Code to Selenium
-----------------------------
Much of the magic behind Selenium is the hard work of programmers with
backgrounds in Javascript, Java, Ruby, PHP, Python, Perl, C#, HTML and other
languages. If you haven't already, you can download the source code from our
Subversion repositories:

* http://svn.seleniumhq.org/svn/selenium-core
* http://svn.seleniumhq.org/svn/selenium-rc
* http://svn.seleniumhq.org/svn/selenium-ide
* http://svn.seleniumhq.org/svn/selenium-grid

We encourage code patches and other contributions - get involved by posting
to the `Selenium Developers`_ section of the forums.

.. note:: Useful information for people willing to code for Selenium-RC can be
   found in the `Developer's Guide`_ at OpenQA's wiki.
   
Shaping the Vision for Selenium
-------------------------------
How could we make Selenium better? What would increase its value to end users
and help it be more accessible and practical? `Contribute to the discussion`_ on
the forums.

.. _forums: http://clearspace.openqa.org/community/selenium
.. _Apache Maven: http://maven.apache.org/
.. _Selenium Developers: http://clearspace.openqa.org/community/selenium/developers
.. _here: https://svn.openqa.org/svn/selenium-core/selenium-website/src/main/webapp
.. _Selenium Core: http://svn.openqa.org/svn/selenium-core/website/src/main/webapp/
.. _Selenium Remote Control: http://svn.openqa.org/svn/selenium-rc/website/src/main/webapp
.. _Selenium IDE: https://svn.openqa.org/svn/selenium-ide/website/src/main/webapp/
.. _Selenium Grid: https://svn.openqa.org/svn/selenium-grid/website/src/main/webapp/
.. _Contribute to the discussion: http://clearspace.openqa.org/thread/14975?tstart=0
.. _Developer's Guide: http://wiki.openqa.org/display/SRC/Developer%27s+Guide
