import re
import sys

from navigation import *
from options import *
from targetlocator import *
from webelement import *

# import the base webdriver package
import webdriver

class WebDriver(object):
  def __init__(self):
    self._conn = ExtensionConnection()

  def Get(self, url):
    self._conn.DriverCommand("get", url)

  def GetCurrentUrl(self):
    return self._conn.DriverCommand("getCurrentUrl")

  def GetTitle(self):
    return self._conn.DriverCommand("title")

  def GetVisible(self):
    return True

  def SetVisible(self, visible):
    pass

  def FindElementByXPath(self, xpath):
    elemId = self._conn.DriverCommand("selectElementUsingXPath", xpath)
    elem = WebElement(self, elemId)
    return elem
  
  def FindElementByLinkText(self, link):
    elemId = self._conn.DriverCommand("selectElementUsingLink", link)
    elem = WebElement(self, elemId)
    return elem

  def FindElementById(self, id):
    return self.FindElementByXPath("//*[@id=\"%s\"]" % id)

  def FindElementByName(self, name):
    return self.FindElementByXPath("//*[@name=\"%s\"]" % name)

  def FindElementsByXPath(self, xpath):
    elemIds = self._conn.DriverCommand("selectElementsUsingXPath", xpath)
    elems = []
    if len(elemIds):
      for elemId in elemIds.split(","):
        elem = WebElement(self, elemId)
        elems.append(elem)
    return elems

  def GetPageSource(self):
    return self._conn.DriverCommand("getPageSource")
  
  def Close(self):
    self._conn.DriverCommand("close")

  def Quit(self):
    self._conn.DriverCommand("quit")

  def SwitchToWindow(self, windowName):
    resp = self._conn.DriverCommand("switchToWindow", windowName)
    if not resp or "No window found" in resp:
      raise exceptions.InvalidSwitchToTargetException("Window %s not found" % windowName)
    self._conn.context = resp

  def SwitchToFrameByIndex(self, index):
    resp = self._conn.DriverCommand("switchToFrame", str(index))

  def SwitchToFrameByName(self, frameName):
    resp = self._conn.DriverCommand("switchToFrame", frameName)

  def Back(self):
    self._conn.DriverCommand("goBack")

  def Forward(self):
    self._conn.DriverCommand("goForward")

  def To(self, url):
    self._conn.DriverCommand("get", url)
