#!/usr/bin/python

from webdriver_common_tests import api_examples
import webdriver_firefox
from webdriver_firefox.webdriver import WebDriver

if __name__ == "__main__":
    api_examples.run_tests(webdriver_firefox)

