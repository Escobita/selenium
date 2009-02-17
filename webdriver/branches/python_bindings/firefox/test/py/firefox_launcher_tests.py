import unittest
import logging
from webdriver_firefox.webdriver import FirefoxLauncher
from webdriver_firefox.webdriver import WebDriver


class FirefoxLauncherTests (unittest.TestCase):
    def setUp(self):
        self.firefox = FirefoxLauncher()

    def testLaunchAndCloseBrowser(self):
         self.firefox.LaunchBrowser()
         self.webdriver = WebDriver()
         self.webdriver.quit()

    def testDoubleClose(self):
        self.firefox.LaunchBrowser()
        self.webdriver = WebDriver()
        self.webdriver.close()
        self.webdriver.close()
        self.webdriver.quit()

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    unittest.main()
