import seletest
import unittest
import time

class ExampleTest(seletest.seletest_class):
    def test_something(self):
	print "selenium_example.py"
        self.open("/inputSuggestAjax.jsf")
	self.verify_text_present("suggest")
	self.type("_id0:_id3", "foo")
	self.keydown("_id0:_id3", 120)
	time.sleep(2)
	self.verify_text_present("foo1")

suite = unittest.makeSuite(ExampleTest)
unittest.TextTestRunner(verbosity=2).run(suite)
