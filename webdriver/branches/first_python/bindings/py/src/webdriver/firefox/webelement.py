from extensionconnection import ExtensionConnection
import webdriver

class WebElement(object):
  """Represents an HTML element. Generally, all interesting operations to do with
  interacting with a page will be performed through this interface."""

  def __init__(self, parent, id):
    self.parent = parent
    self.conn = ExtensionConnection()
    self.id = id

  def GetText(self):
    """Gets the inner text of the element."""
    return self._command("getElementText")

  def Click(self):
    """Clicks the element."""
    self._command("click")

  def Submit(self):
    """Submits a form."""
    self._command("submitElement")

  def GetValue(self):
    """Gets the value of the element's value attribute."""
    return self._command("getElementValue")

  def Clear(self):
    """Clears the text if it's a text entry element."""
    self._command("clear")

  def GetAttribute(self, name):
    """Gets the attribute value."""
    return self._command("getElementAttribute", name)

  def Toggle(self):
    """Toggles the element state."""
    self._command("toggleElement")

  def IsSelected(self):
    """Whether the element is selected."""
    return self._command("getElementSelected")

  def SetSelected(self):
    """Selects an elmeent."""
    self._command("setElementSelected")

  def IsEnabled(self):
    """Whether the element is enabled."""
    if self.GetAttribute("disabled"):
      return False
    else:
      # The "disabled" attribute may not exist
      return True

  def FindElementsByXPath(self, xpath):
    """Finds elements within the elements by xpath."""
    resp = self._command("findElementsByXPath", xpath)
    elems = []
    for elemId in resp.split(","):
      elem = WebElement(self.parent, elemId)
      elems.append(elem)
    return elems

  def SendKeys(self, keys_characters):
    """Simulates typing into the element."""
    self._command("sendKeys", keys_characters)

  def _command(self, _cmd, *args):
    return self.conn.ElementCommand(_cmd, self.id, *args)
