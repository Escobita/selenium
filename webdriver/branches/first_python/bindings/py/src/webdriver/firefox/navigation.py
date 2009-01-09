from extensionconnection import ExtensionConnection
import webdriver

class Navigation(webdriver.Navigation):
  def __init__(self):
    self._conn = ExtensionConnection()

  def Back(self):
    self._conn.DriverCommand("goBack")

  def Forward(self):
    self._conn.DriverCommand("goForward")

  def To(self, url):
    self._conn.DriverCommand("get", url)
