import re
import sys

from navigation import *
from options import *
from targetlocator import *
from webelement import *

# import the base webdriver package
import webdriver

class WebDriver(object):
   """The main interface to use for testing, which represents an idealised web browser."""
  def __init__(self):
    self._conn = ExtensionConnection()

  def Get(self, url):
    """Loads a web page in the current browser."""
    self._conn.DriverCommand("get", url)

  def GetCurrentUrl(self):
    """Gets the current url."""
    return self._conn.DriverCommand("getCurrentUrl")

  def GetTitle(self):
    """Gets the title of the current page."""
    return self._conn.DriverCommand("title")

  def FindElementByXPath(self, xpath):
    """Finds an element by xpath."""
    elemId = self._conn.DriverCommand("selectElementUsingXPath", xpath)
    elem = WebElement(self, elemId)
    return elem
  
  def FindElementByLinkText(self, link):
    """Finds an element by its link text.

    Returns None if the element is not a link.
    """
    elemId = self._conn.DriverCommand("selectElementUsingLink", link)
    elem = WebElement(self, elemId)
    return elem

  def FindElementById(self, id):
    """Finds an element by its id."""
    return self.FindElementByXPath("//*[@id=\"%s\"]" % id)

  def FindElementByName(self, name):
    """Finds and element by its name."""
    return self.FindElementByXPath("//*[@name=\"%s\"]" % name)

  def FindElementsByXPath(self, xpath):
    """Finds all the elements for the given xpath query."""
    elemIds = self._conn.DriverCommand("selectElementsUsingXPath", xpath)
    elems = []
    if len(elemIds):
      for elemId in elemIds.split(","):
        elem = WebElement(self, elemId)
        elems.append(elem)
    return elems

  def GetPageSource(self):
    """Gets the page source."""
    return self._conn.DriverCommand("getPageSource")
  
  def Close(self):
    """Closes the current window, quit the browser if it's the last window open."""
    self._conn.DriverCommand("close")

  def Quit(self):
    """Quits the driver and close every associated window."""
    self._conn.DriverCommand("quit")

  def SwitchToWindow(self, windowName):
    """Switches focus to a window."""
    resp = self._conn.DriverCommand("switchToWindow", windowName)
    if not resp or "No window found" in resp:
      raise exceptions.InvalidSwitchToTargetException("Window %s not found" % windowName)
    self._conn.context = resp

  def SwitchToFrame(self, indexOrName):
    """Switches focus to a frame by index or name."""
    resp = self._conn.DriverCommand("switchToFrame", str(indexOrName))

  def Back(self):
    """Goes back in browser history."""
    self._conn.DriverCommand("goBack")

  def Forward(self):
    """Goes forward in browser history."""
    self._conn.DriverCommand("goForward")
