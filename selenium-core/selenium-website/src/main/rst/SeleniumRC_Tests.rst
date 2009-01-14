Selenium tests HowTo
====================

:Author: Santiago Suarez Ordoñez
:Copyright: This Documment has been released under 
            Creative Commons licence.

.. contents::

Introduction
-------------

This guide aims to help people writing tests 
to run using the Selenium RC environment built
with eduCommons.

Tests buildout
--------------

Originally, the test environment is not being 
deployed within the production buildout.
To create an instance in which the tests can run
you should run the buildout using the test.cfg 
configuration::

        $ ./bin/buildout -c tests.cfg

One the buildout is complete, a particular test can
be run using zopepy by typing the following::

        $ ./bin/zopepy example_test.py

Creating a test
---------------

Tests guidelines
~~~~~~~~~~~~~~~~

To create a test, there are certain basic rules to 
follow:

#) The test should start Selenium RC server before
   it's start.
#) The test should stop Selenium RC server after it's 
   end.
#) The test should receive the test browser to run the tests
   as a parameter (this is only required for tests that will 
   take part of the main test suite).
#) **Complete this list**

Example test
~~~~~~~~~~~~

The following is an example test, it can be used as the basic
structure for future tests::

        #!/usr/bin/python

        from selenium import selenium
        import sys

        browser = sys.argv[1]
        url = sys.argv[2]
        s = selenium("localhost",4444,browser,url)
