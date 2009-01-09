import re
import sys

from navigation import *
from options import *
from targetlocator import *
from webelement import *

# import the base webdriver package
import webdriver

class WebDriver(webdriver.WebDriver):
  def __init__(self):
    self.conn = ExtensionConnection()

  def Get(self, url):
    self.conn.DriverCommand("get", url)

  def GetCurrentUrl(self):
    return self.conn.DriverCommand("getCurrentUrl")

  def GetTitle(self):
    return self.conn.DriverCommand("title")

  def GetVisible(self):
    return True

  def SetVisible(self, visible):
    pass

  def FindElementByXPath(self, xpath):
    elemId = self.conn.DriverCommand("selectElementUsingXPath", xpath)
    elem = WebElement(self, elemId)
    return elem
  
  def FindElementByLinkText(self, link):
    elemId = self.conn.DriverCommand("selectElementUsingLink", link)
    elem = WebElement(self, elemId)
    return elem

  def FindElementById(self, id):
    return self.FindElementByXPath("//*[@id=\"%s\"]" % id)

  def FindElementByName(self, name):
    return self.FindElementByXPath("//*[@name=\"%s\"]" % name)

  def FindElementsByXPath(self, xpath):
    elemIds = self.conn.DriverCommand("selectElementsUsingXPath", xpath)
    elems = []
    if len(elemIds):
      for elemId in elemIds.split(","):
        elem = WebElement(self, elemId)
        elems.append(elem)
    return elems

  def GetPageSource(self):
    return self.conn.DriverCommand("getPageSource")
  
  def Close(self):
    self.conn.DriverCommand("close")

  def Quit(self):
    self.conn.DriverCommand("quit")

  def SwitchTo(self):
    return TargetLocator()

  def Navigate(self):
    return Navigation()

  def Manage(self):
    return Options()
