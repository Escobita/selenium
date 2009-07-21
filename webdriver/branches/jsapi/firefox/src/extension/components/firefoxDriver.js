/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.
Portions copyright 2007 ThoughtWorks, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

function FirefoxDriver(server) {
  this.server = server;
  this.context = new Context();
  this.mouseSpeed = 1;

  this.currentX = 0;
  this.currentY = 0;
}

FirefoxDriver.prototype.__defineGetter__("id", function() {
  if (!this.id_) {
    this.id_ = this.server.getNextId();
  }

  return this.id_;
});

FirefoxDriver.prototype.getCurrentWindowHandle = function(respond) {
  respond.response = this.id;
  respond.send();
};

FirefoxDriver.prototype.get = function(respond, url) {
    var self = this;
    this.context.frameId = "?";

    // Check to see if the given url is the same as the current one, but
    // with a different anchor tag.
    var current = Utils.getBrowser(this.context).contentWindow.location;
    var ioService = Utils.getService("@mozilla.org/network/io-service;1", "nsIIOService");
    var currentUri = ioService.newURI(current, "", null);
    var futureUri = ioService.newURI(url, "", currentUri);

    var loadEventExpected = true;

    if (currentUri && futureUri &&
        currentUri.prePath == futureUri.prePath &&
        currentUri.filePath == futureUri.filePath) {
        // Looks like we're at the same url with a ref
        // Being clever and checking the ref was causing me headaches. Brute force for now
        loadEventExpected = futureUri.path.indexOf("#") == -1;
    }

    if (loadEventExpected) {
        new WebLoadingListener(this, function() {
            // TODO: Rescue the URI and response code from the event
            var responseText = "";
            respond.context = self.context;
            respond.response = responseText;
            respond.send();
        });
    }

    Utils.getBrowser(this.context).loadURI(url);

    if (!loadEventExpected) {
        respond.context = self.context;
        respond.send();
    }
};

FirefoxDriver.prototype.close = function(respond) {
    // Grab all the references we'll need. Once we call close all this might go away
    var wm = Utils.getService("@mozilla.org/appshell/window-mediator;1", "nsIWindowMediator");
    var appService = Utils.getService("@mozilla.org/toolkit/app-startup;1", "nsIAppStartup");
    var forceQuit = Components.interfaces.nsIAppStartup.eForceQuit;

    // Here we go!
    try {
        var browser = Utils.getBrowser(this.context);
        browser.contentWindow.close();
    } catch(e) {
        dump(e);
    }

    // If we're on a Mac we might have closed all the windows but not quit, so ensure that we do actually quit :)
    var allWindows = wm.getEnumerator("navigator:browser");
    if (!allWindows.hasMoreElements()) {
        appService.quit(forceQuit);
        return;  // The client should catch the fact that the socket suddenly closes
    }

    // If we're still running, return
    respond.context = this.context;
    respond.send();
};

FirefoxDriver.prototype.executeScript = function(respond, script) {
  var context = this.context;
  var window = Utils.getDocument(this.context).defaultView;

  var parameters = new Array();
  var runScript;
  // Pre 2.0.0.15
  if (window['alert'] && !window.wrappedJSObject) {
    runScript = function(scriptSrc) {
      return window.eval(scriptSrc);
    };
  } else {
    runScript = function(scriptSrc) {
      window = window.wrappedJSObject;
      var sandbox = new Components.utils.Sandbox(window);
      sandbox.window = window;
      sandbox.__webdriverParams = parameters;
      sandbox.document = window.document;
      sandbox.unsafeWindow = window;
      sandbox.__proto__ = window;

      return Components.utils.evalInSandbox(scriptSrc, sandbox);
    };
  }

  try {
    var scriptSrc = "var __webdriverFunc = function(){" + script.shift() + "};  __webdriverFunc.apply(window, __webdriverParams);";

    var convert = script.shift();
    while (convert && convert.length > 0) {
      var t = convert.shift();

      if (t['type'] == "ELEMENT") {
        var element = Utils.getElementAt(t['value'], context);
        t['value'] = element.wrappedJSObject ? element.wrappedJSObject : element;
      }

      parameters.push(t['value']);
    }

    var result = runScript(scriptSrc, parameters);

    // Sophisticated.
    if (result && result['tagName']) {
      respond.setField('resultType', "ELEMENT");
      respond.response = Utils.addToKnownElements(result, this.context);
    } else if (result) {
      respond.setField('resultType', "OTHER");
      respond.response = result;
    } else {
      respond.setField('resultType', "NULL");
    }

  } catch (e) {
    respond.isError = true;
    respond.response = e;
  }
  respond.send();
};

FirefoxDriver.prototype.getCurrentUrl = function(respond) {
    respond.context = this.context;
    var url = Utils.getDocument(this.context).defaultView.location;
    if (!url) {
      url = Utils.getBrowser(this.context).contentWindow.location;
    }
    respond.response = "" + url;
    respond.send();
};

FirefoxDriver.prototype.title = function(respond) {
    var browser = Utils.getBrowser(this.context);
    respond.context = this.context;
    respond.response = browser.contentTitle;
    respond.send();
};

FirefoxDriver.prototype.getPageSource = function(respond) {
    var source = Utils.getDocument(this.context).getElementsByTagName("html")[0].innerHTML;

    respond.context = this.context;
    respond.response = "<html>" + source + "</html>";
    respond.send();
};


FirefoxDriver.prototype.selectElementUsingXPath = function(respond, xpath) {
    var doc = Utils.getDocument(this.context);
    var result = doc.evaluate(xpath, doc, null, Components.interfaces.nsIDOMXPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;

    respond.context = this.context;

    if (result) {
        respond.response = Utils.addToKnownElements(result, this.context);
    } else {
        respond.isError = true;
        respond.response = "Unable to locate element using " + xpath;
    }

    respond.send();
};

FirefoxDriver.prototype.selectElementByName = function(respond, name) {
  var doc = Utils.getDocument(this.context);

  var elements = doc.getElementsByName(name);
  if (elements.length) {
    respond.response = Utils.addToKnownElements(elements[0], this.context);
  } else {
    respond.isError = true;
    respond.response = "Unable to locate element with name '" + name + "'";
  }

  respond.send();
};

FirefoxDriver.prototype.selectElementsUsingName = function(respond, name) {
  var doc = Utils.getDocument(this.context);

  var elements = doc.getElementsByName(name);
  var response = "";
  for (var i = 0; i < elements.length; i++) {
    var element = elements[i];
    var index = Utils.addToKnownElements(element, this.context);
    response += index + ",";
  }
      // Strip the trailing comma
  response = response.substring(0, response.length - 1);

  respond.context = this.context;
  respond.response = response;
  respond.send();
};

FirefoxDriver.prototype.selectElementUsingTagName = function(respond, name) {
	var doc = Utils.getDocument(this.context);

	var elements = doc.getElementsByTagName(name);
	if (elements.length) {
		respond.response = Utils.addToKnownElements(elements[0], this.context);
	} else {
		respond.isError = true;
		respond.response = "Unable to locate element with name '" + name + "'";
	}

	respond.send();
};

FirefoxDriver.prototype.selectElementsUsingTagName = function(respond, name) {
	var doc = Utils.getDocument(this.context);

	var elements = doc.getElementsByTagName(name);
	var response = "";
	for ( var i = 0; i < elements.length; i++) {
		var element = elements[i];
		var index = Utils.addToKnownElements(element, this.context);
		response += index + ",";
	}
	// Strip the trailing comma
	response = response.substring(0, response.length - 1);

	respond.context = this.context;
	respond.response = response;
	respond.send();
};

FirefoxDriver.prototype.selectElementUsingClassName = function(respond, name) {
    var doc = Utils.getDocument(this.context);

    if (doc["getElementsByClassName"]) {
      var elements = doc.getElementsByClassName(name);
      respond.context = this.context;

      if (elements.length) {
        respond.response = Utils.addToKnownElements(elements[0], this.context);
      } else {
        respond.isError = true;
        respond.response = "Unable to locate element with class name '" + name + "'";
      }

      respond.send();
    } else {
      this.selectElementUsingXPath(respond, "//*[contains(concat(' ',normalize-space(@class),' '),' " + name + " ')]");
    }
};

FirefoxDriver.prototype.selectElementsUsingClassName = function(respond, name) {
    var doc = Utils.getDocument(this.context);

    if (doc["getElementsByClassName"]) {
      var result = doc.getElementsByClassName(name);

      var response = "";
      for (var i = 0; i < result.length; i++) {
          var element = result[i];
          var index = Utils.addToKnownElements(element, this.context);
          response += index + ",";
      }
      // Strip the trailing comma
      response = response.substring(0, response.length - 1);

      respond.context = this.context;
      respond.response = response;
      respond.send();
    } else {
      this.selectElementsUsingXPath(respond, "//*[contains(concat(' ',normalize-space(@class),' '),' " + name + " ')]");
    }
};

FirefoxDriver.prototype.selectElementUsingLink = function(respond, linkText) {
    var allLinks = Utils.getDocument(this.context).getElementsByTagName("A");
    var index;
    for (var i = 0; i < allLinks.length && !index; i++) {
        var text = Utils.getText(allLinks[i], true);
        if (linkText == text) {
            index = Utils.addToKnownElements(allLinks[i], this.context);
        }
    }

    respond.context = this.context;

    if (index !== undefined) {
        respond.response = index;
    } else {
        respond.isError = true;
        respond.response = "Unable to locate element with link text '" + linkText + "'";
    }

    respond.send();
};

FirefoxDriver.prototype.selectElementsUsingLink = function(respond, linkText) {
  var allLinks = Utils.getDocument(this.context).getElementsByTagName("A");
  var indices = "";
  for (var i = 0; i < allLinks.length; i++) {
    var text = Utils.getText(allLinks[i], true);
    if (linkText == text) {
      indices += Utils.addToKnownElements(allLinks[i], this.context) + ",";
    }

  }

  // Strip the trailing comma
  indices = indices.substring(0, indices.length - 1);

  respond.context = this.context;
  respond.response = indices;
  respond.send();
};

FirefoxDriver.prototype.selectElementsUsingPartialLinkText = function(respond, linkText) {
    var allLinks = Utils.getDocument(this.context).getElementsByTagName("A");
    var indices = "";
    for (var i = 0; i < allLinks.length; i++) {
        var text = Utils.getText(allLinks[i], true);
        if (text.indexOf(linkText) != -1) {
            indices += Utils.addToKnownElements(allLinks[i], this.context) + ",";
        }
    }

    respond.context = this.context;
    respond.response = indices;
    respond.send();
};

FirefoxDriver.prototype.selectElementUsingPartialLinkText = function(respond, linkText) {
    var allLinks = Utils.getDocument(this.context).getElementsByTagName("A");
    var index;
    for (var i = 0; i < allLinks.length && !index; i++) {
        var text = Utils.getText(allLinks[i], true);
        if (text.indexOf(linkText) != -1) {
            index = Utils.addToKnownElements(allLinks[i], this.context);
            break;
        }
    }

    respond.context = this.context;

    if (index !== undefined) {
        respond.response = index;
    } else {
        respond.isError = true;
        respond.response = "Unable to locate element with link text contains '" + linkText + "'";
    }

    respond.send();
};

FirefoxDriver.prototype.selectElementById = function(respond, id) {
    var doc = Utils.getDocument(this.context);
    var element = doc.getElementById(id);

    respond.context = this.context;

    if (element) {
        respond.response = Utils.addToKnownElements(element, this.context);
    } else {
        respond.isError = true;
        respond.response = "Unable to locate element with id '" + id + "'";
    }

    respond.send();
};

FirefoxDriver.prototype.selectElementsUsingId = function(respond, id) {
  var doc = Utils.getDocument(this.context);
  var allElements = doc.evaluate("//*[@id='" + id + "']", doc, null,
      Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE,
      null);
  var indices = "";
  var element = allElements.iterateNext();
  while (element) {
    var index = Utils.addToKnownElements(element, this.context);
    indices += index + ",";
    element = allElements.iterateNext();
  }
  // Strip the trailing comma
  indices = indices.substring(0, indices.length - 1);

  respond.context = this.context;
  respond.response = indices;
  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingXPath = function(respond, xpath) {
    var doc = Utils.getDocument(this.context);
    var result = doc.evaluate(xpath, doc, null, Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
    var response = "";
    var element = result.iterateNext();
    while (element) {
        var index = Utils.addToKnownElements(element, this.context);
        response += index + ",";
        element = result.iterateNext();
    }
    // Strip the trailing comma
    response = response.substring(0, response.length - 1);

    respond.context = this.context;
    respond.response = response;
    respond.send();
};

FirefoxDriver.prototype.switchToFrame = function(respond, frameId) {
    var browser = Utils.getBrowser(this.context);
    var frameDoc = Utils.findDocumentInFrame(browser, frameId[0]);

    if (frameDoc) {
        this.context = new Context(this.context.windowId, frameId[0]);
        respond.context = this.context.toString();
        respond.send();
    } else {
        respond.isError = true;
        respond.response = "Cannot find frame with id: " + frameId.toString();
        respond.send();
    }
};

FirefoxDriver.prototype.switchToDefaultContent = function(respond) {
    this.context.frameId = "?";
    respond.context = this.context.toString();
    respond.send();
};

FirefoxDriver.prototype.switchToActiveElement = function(respond) {
  var element = Utils.getActiveElement(this.context);

  respond.response = Utils.addToKnownElements(element, this.context);
  respond.send();
};


FirefoxDriver.prototype.goBack = function(respond) {
    var browser = Utils.getBrowser(this.context);

    if (browser.canGoBack) {
      browser.goBack();
    }

    respond.context = this.context;
    respond.send();
};

FirefoxDriver.prototype.goForward = function(respond) {
    var browser = Utils.getBrowser(this.context);

    if (browser.canGoForward) {
      browser.goForward();
    }

    respond.context = this.context;
    respond.send();
};

FirefoxDriver.prototype.refresh = function(respond) {
    var browser = Utils.getBrowser(this.context);
    browser.contentWindow.location.reload(true);

    respond.send();
};

FirefoxDriver.prototype.addCookie = function(respond, cookieString) {
    var cookie;
    cookie = eval('(' + cookieString[0] + ')');

    if (cookie.expiry) {
        cookie.expiry = new Date(cookie.expiry);
    } else {
        var date = new Date();
        date.setYear(2030);
        cookie.expiry = date;
    }

    cookie.expiry = cookie.expiry.getTime() / 1000; // Stored in seconds

    if (!cookie.domain) {
      var location = Utils.getBrowser(this.context).contentWindow.location;
      cookie.domain = location.hostname; // + ":" + location.port;
      if (location.port != 80) {
        cookie.domain += ":" + location.port;
      }
    } else {
      var currLocation = Utils.getBrowser(this.context).contentWindow.location;
      var currDomain = currLocation.host;
      if (currLocation.port != 80) { currDomain += ":" + currLocation.port; }
      if (currDomain.indexOf(cookie.domain) == -1) {  // Not quite right, but close enough
        respond.isError = true;
        respond.response = "You may only set cookies for the current domain";
        respond.send();
        return;
      }
    }

    var document = Utils.getDocument(this.context);
    if (!document || !document.contentType.match(/html/i)) {
      respond.isError = true;
      respond.response = "You may only set cookies on html documents";
      respond.send();
      return;
    }

    var cookieManager = Utils.getService("@mozilla.org/cookiemanager;1", "nsICookieManager2");

    // The signature for "add" is different in firefox 3 and 2. We should sniff the browser version and call the right
    // version of the method, but for now we'll use brute-force.
    try {
      cookieManager.add(cookie.domain, cookie.path, cookie.name, cookie.value, cookie.secure, false, cookie.expiry);
    } catch(e) {
      cookieManager.add(cookie.domain, cookie.path, cookie.name, cookie.value, cookie.secure, false, false, cookie.expiry);
    }

    respond.context = this.context;
    respond.send();
};

function handleCookies(context, toCall) {
  var cm = Utils.getService("@mozilla.org/cookiemanager;1", "nsICookieManager");

  var makeStrippedHost = function (aHost) {
    var formattedHost = aHost.charAt(0) == "." ? aHost.substring(1, aHost.length) : aHost;
    formattedHost = formattedHost.substring(0, 4) == "www." ? formattedHost.substring(4, formattedHost.length) : formattedHost;

    return formattedHost;
  };

  var location = Utils.getBrowser(context).contentWindow.location;
  var currentDomain = makeStrippedHost(location.host);
  if (location.port != 80) {
    currentDomain += ":" + location.port;
  }
  var isForCurrentHost = function(aHost) {
    return currentDomain.indexOf(aHost) != -1;
  };

  var currentPath = Utils.getBrowser(context).contentWindow.location.pathname;
  if (!currentPath) currentPath = "/";
  var isForCurrentPath = function(aPath) {
    return currentPath.indexOf(aPath) != -1;
  };

  var e = cm.enumerator;
  while (e.hasMoreElements()) {
    var cookie = e.getNext();
     if (cookie && cookie instanceof Components.interfaces.nsICookie) {
       var strippedHost = makeStrippedHost(cookie.host);

       if (isForCurrentHost(strippedHost) && isForCurrentPath(cookie.path)) {
         toCall(cookie);
      }
    }
  }
}

FirefoxDriver.prototype.getCookie = function(respond) {
  var cookieToString = function(c) {
    return c.name + "=" + c.value + ";" + "domain=" + c.host + ";"
        + "path=" + c.path + ";" + "expires=" + c.expires + ";"
        + (c.isSecure ? "secure ;" : "");
  };

  var toReturn = "";
  handleCookies(this.context, function(cookie) {
    var toAdd = cookieToString(cookie);
    toReturn += toAdd + "\n";
  });

  respond.response = toReturn;
  respond.send();
};

// This is damn ugly, but it turns out that just deleting a cookie from the document
// doesn't always do The Right Thing
FirefoxDriver.prototype.deleteCookie = function(respond, cookieString) {
    var cm = Utils.getService("@mozilla.org/cookiemanager;1", "nsICookieManager");
    var toDelete = eval('(' + cookieString + ')');

    handleCookies(this.context,  function(cookie) {
      if (toDelete.name == cookie.name) {
        cm.remove(cookie.host, cookie.name, cookie.path, false);
      }
    });

    respond.context = this.context;
    respond.send();
};


FirefoxDriver.prototype.setMouseSpeed = function(respond, speed) {
    this.mouseSpeed = speed;
    respond.context = this.context;
    respond.send();
};

FirefoxDriver.prototype.getMouseSpeed = function(respond) {
    respond.context = this.context;
    respond.response = "" + this.mouseSpeed;
    respond.send();
};

FirefoxDriver.prototype.saveScreenshot = function(respond, pngFile) {
    var window = Utils.getBrowser(this.context).contentWindow;
    try {
        var canvas = Screenshooter.grab(window);
        try {
            Screenshooter.save(canvas, pngFile);
        } catch(e) {
            respond.isError = true;
            respond.response = 'Could not save screenshot to ' + pngFile + ' - ' + e;
        }
    } catch(e) {
        respond.isError = true;
        respond.response = 'Could not take screenshot of current page - ' + e;
    }
    respond.context = this.context;
    respond.send();
};
