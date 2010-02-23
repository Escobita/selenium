#!/usr/bin/python
# Copyright 2008-2009 WebDriver committers
# Copyright 2008-2009 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


import logging
import os
import socket
import subprocess
import time
from webdriver.common_tests import api_examples
from webdriver.remote.webdriver import WebDriver

SERVER_ADDR = "localhost"
DEFAULT_PORT = 4444

if __name__ == "__main__":
    _socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_proc = None
    try:
        _socket.connect((SERVER_ADDR, DEFAULT_PORT))
        print ("The remote driver server is already running or something else"
               "is using port %d, continuing..." % DEFAULT_PORT)
    except:
        print ("Starting the remote driver server")
        server_proc = subprocess.Popen(
            "java -jar selenium-server-standalone.jar",
            shell=True)
        time.sleep(5)
        print "Server should be online"
    try:
        url = "http://%s:%d/wd/hub" % (SERVER_ADDR, DEFAULT_PORT)
        api_examples.run_tests(WebDriver(url, "firefox", "ANY"))
    finally:
        try:
            os.kill(server_proc.pid, 9)
        except:
            pass
