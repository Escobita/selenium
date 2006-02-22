import httplib

class selenium_class:
    def do_command(self, verb, arg1='', arg2=''):
	conn = httplib.HTTPConnection("localhost", 8081)
	commandString = '/selenium-driver/driver?commandRequest=|' + verb + '|' + str(arg1) + '|' + str(arg2) + '|'
	print 'do_command(' + verb + ',' + str(arg1) + ',' + str(arg2) + ') -> ' + commandString + '\n'
	conn.request("GET", commandString)

	response = conn.getresponse()
	print response.status, response.reason
	data = response.read()
        result = response.reason
        print "RESULT: " + str(result) + "\n\n"
        if None != result:
	  resultStr = result
          if "OK" != result:
            if "PASSED" != result:
              raise Exception, result
        result

    def open(self, url):
 	self.do_command("open", url)

    def type(self, arg1, arg2):
        self.do_command("type", arg1, arg2)

    def verify_text_present(self, s):
        self.do_command("verify_text_present", s)

    def keydown(self, arg1, arg2):
        self.do_command("keydown", arg1, arg2)
