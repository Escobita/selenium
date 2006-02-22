import unittest
import seletest
import selenium

seleniumHost = 'localhost'
seleniumPort = str(8080)

def chooseSeleniumServer(host, port):
	seletest_class.seleniumHost = host
	seletest_class.seleniumPort = str(port)


class seletest_class(unittest.TestCase):

    def setUp(self):
	print "Using selenium server at " + seletest.seleniumHost + ":" + seletest.seleniumPort
	self.seleniumField = selenium.selenium_class()

    def open(self, url):
 	print "seletest:open called"
        self.seleniumField.open(url)

    def type(self, arg1, arg2):
        self.seleniumField.type(arg1, arg2)

    def verify_text_present(self, s):
        self.seleniumField.verify_text_present(s)

    def keydown(self, arg1, arg2):
        self.seleniumField.keydown(arg1, arg2)
