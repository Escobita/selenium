from extensionconnection import ExtensionConnection
import webdriver
import exceptions

class TargetLocator(webdriver.TargetLocator):
  
  def __init__(self):
    self._conn = ExtensionConnection()
 
  def Window(self, windowName):
    resp = self._conn.DriverCommand("switchToWindow", windowName)
    if not resp or "No window found" in resp:
      raise exceptions.InvalidSwitchToTargetException("Window %s not found" % windowName)
    self._conn.context = resp

  def FrameByIndex(self, index):
    resp = self._conn.DriverCommand("switchToFrame", str(index))

  def FrameByName(self, frameName):
    resp = self._conn.DriverCommand("switchToFrame", frameName)
