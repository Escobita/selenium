import unittest
import selenium

class seletest_class(unittest.TestCase):

#    def server_info
#        ['localhost', 3000]
#    def path_to_runner
#    	'selenium/javascript'
#    def driver_port
#    	8080
#    def driver_host
#    	"localhost"
#
    def setUp(self):
	self.seleniumField = selenium.selenium_class()

#    def runTest():
#        selenium = SeleneseInterpreter(10000);

#    def tearDown():

    def open(self, url):
 	print "seletest:open called"
        self.seleniumField.open(url)

    def type(self, arg1, arg2):
        self.seleniumField.type(arg1, arg2)

    def verify_text_present(self, s):
        self.seleniumField.verify_text_present(s)

    def keydown(self, arg1, arg2):
        self.seleniumField.keydown(arg1, arg2)
