#!/usr/bin/python

from webdriver_common_tests import api_examples
import webdriver_remote
from webdriver_remote.webdriver import WebDriver

if __name__ == "__main__":
    #TODO: start remote server
    api_examples.run_tests(webdriver_remote)
    #shut down remote server
