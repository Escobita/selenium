#!/usr/bin/python

import logging
import re
import time
import sys
import unittest
from webdriver.firefox.webdriver import WebDriver
from webdriver.firefox.webserver import SimpleWebServer
from webdriver.firefox.firefoxlauncher import FirefoxLauncher

class ApiExampleTest (unittest.TestCase):
    def setUp(self):
	self.driver = WebDriver()

    def tearDown(self):
	pass

    def testGetTitle(self):
	self._loadSimplePage()
	title = self.driver.get_title()
	self.assertEquals("Hello WebDriver", title)

    def testGetCurrentUrl(self):
	self._loadSimplePage()
	url = self.driver.get_current_url()
	self.assertEquals("http://localhost:8000/simpleTest.html", url)

    def testFindElementsByXPath(self):
	self._loadSimplePage()
	elem = self.driver.find_element_by_xpath("//h1")
	self.assertEquals("Heading", elem.get_text())

    def testFindElementsByXpath(self):
	self._loadPage("xhtmlTest")
	elems = self.driver.find_elements_by_xpath("//option")
	self.assertEquals(4, len(elems))
	self.assertEquals("saab", elems[1].get_attribute("value"))

    def testFindElementsByName(self):
	self._loadPage("xhtmlTest")
	elem = self.driver.find_element_by_name("x")
	self.assertEquals("Named element", elem.get_text())

    def testShouldBeAbleToEnterDataIntoFormFields(self):
	self._loadPage("xhtmlTest")
	elem = self.driver.find_element_by_xpath("//form[@name='someForm']/input[@id='username']")
	elem.clear()
	elem.send_keys("some text")
	elem = self.driver.find_element_by_xpath("//form[@name='someForm']/input[@id='username']")
	self.assertEquals("some text", elem.get_value())

    def testSwitchToWindow(self):
	title_1 = "XHTML Test Page"
	title_2 = "We Arrive Here"
	self._loadPage("xhtmlTest")
	self.driver.find_element_by_link_text("Open new window").click()
	self.assertEquals(title_1, self.driver.get_title())
	try:
	    self.driver.SwitchToWindow("result")
	except:
	  # This may fail because the window is not loading fast enough, so try again
	  time.sleep(1)
	  self.driver.switch_to_window("result")
	self.assertEquals(title_2, self.driver.get_title())

    def testSwitchToFrameByIndex(self):
	self._loadPage("frameset")
	self.driver.switch_to_frame(2)
	self.driver.switch_to_frame(0)
	self.driver.switch_to_frame(2)
	checkbox = self.driver.find_element_by_id("checky")
	checkbox.toggle()
	checkbox.submit()
  
    def testSwitchFrameByName(self):
        self._loadPage("frameset")
        self.driver.switch_to_frame("third");
        checkbox = self.driver.find_element_by_id("checky")
        checkbox.toggle()
        checkbox.submit()

    def testGetPageSource(self):
        self._loadSimplePage()
        source = self.driver.get_page_source()
        self.assertTrue(len(re.findall(r'<html>.*</html>', source, re.DOTALL)) > 0)

    def testIsEnabled(self):
        self._loadPage("formPage")
        elem = self.driver.find_element_by_xpath("//input[@id='working']")
        self.assertTrue(elem.is_enabled())
        elem = self.driver.find_element_by_xpath("//input[@id='notWorking']")
        self.assertFalse(elem.is_enabled())

    def testIsSelectedAndToggle(self):
        self._loadPage("formPage")
        elem = self.driver.find_element_by_id("multi")
        option_elems = elem.find_elements_by_xpath("option")
        self.assertTrue(option_elems[0].is_selected())
        option_elems[0].toggle()
        self.assertFalse(option_elems[0].is_selected())
        option_elems[0].toggle()
        self.assertTrue(option_elems[0].is_selected())
        self.assertTrue(option_elems[2].is_selected())

    def testNavigate(self):
        self._loadPage("formPage")
        self.driver.find_element_by_id("imageButton").submit()
        self.assertEquals("We Arrive Here", self.driver.get_title())
        self.driver.back()
        self.assertEquals("We Leave From Here", self.driver.get_title())
        self.driver.forward()
        self.assertEquals("We Arrive Here", self.driver.get_title())

    def testGetAttribute(self):
        self._loadPage("xhtmlTest")
        elem = self.driver.find_element_by_id("select_volvo")
        self.assertEquals("xx", elem.get_attribute("tag"))

    def testGetImplicitAttribute(self):
        self._loadPage("xhtmlTest")
        elem = self.driver.find_element_by_id("select_saab")
        self.assertEquals(1, elem.get_attribute("index"))

    def _loadSimplePage(self):
        self.driver.get("http://localhost:8000/simpleTest.html")

    def _loadPage(self, name):
        self.driver.get("http://localhost:8000/%s.html" % name)

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    webserver = SimpleWebServer()
    webserver.start()
    firefox = FirefoxLauncher()
    firefox.LaunchBrowser()
  
    try:
        testLoader = unittest.TestLoader()
        testRunner = unittest.TextTestRunner()
        testRunner.run(testLoader.loadTestsFromTestCase(ApiExampleTest))
        driver = WebDriver()
        driver.quit()
    finally:
        webserver.stop()
