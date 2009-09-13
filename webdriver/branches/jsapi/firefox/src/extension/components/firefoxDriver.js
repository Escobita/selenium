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

/**
 * @param {WebDriverServer} server  The creating server.
 * @param {boolean} enableNativeEvents Whether to use native events.
 * @param {nsIDOMWindow} win The window that contains this FirefoxDriver.
 */
function FirefoxDriver(server, enableNativeEvents, win) {
  this.server = server;
  this.mouseSpeed = 1;
  this.enableNativeEvents = enableNativeEvents;

  this.currentX = 0;
  this.currentY = 0;

  /**
   * The window that this instance was created in.
   * @type {nsIDOMWindow}
   */
  this.window = win;

  /**
   * The browser (or tabbrowser) that contains the view of this instance's
   * {@code window}.
   * @type {browser|tabbrowser}
   * @private
   */
  this.browser_ = this.window.getBrowser();
}


/**
 * The content window of the frame that this instance is currently manipulating,
 * or {@code null} for the top most frame.
 * @type {?nsIDOMWindow}
 * @private
 */
FirefoxDriver.prototype.currentFrame_ = null;


FirefoxDriver.prototype.__defineGetter__("id", function() {
  if (!this.id_) {
    this.id_ = this.server.getNextId();
  }

  return this.id_;
});


/**
 * @return {browser|tabbrowser} The browser that contains the view of this
 *     instance's {@code window}.
 */
FirefoxDriver.prototype.getBrowser = function() {
  return this.browser_;
};


/**
 * Retrieves the document that this instance is current manipulating.
 * @return {nsIDOMDocument}
 */
FirefoxDriver.prototype.getDocument_ = function() {
  if (this.currentFrame_) {
    return this.currentFrame_.document;
  }
  return this.browser_.contentDocument;
};


FirefoxDriver.prototype.getCurrentWindowHandle = function(respond) {
  respond.response = this.id;
  respond.send();
};


FirefoxDriver.prototype.get = function(respond, url) {
  var self = this;

  // Check to see if the given url is the same as the current one, but
  // with a different anchor tag.
  var current = this.browser_.contentWindow.location;
  var ioService =
      Utils.getService("@mozilla.org/network/io-service;1", "nsIIOService");
  var currentUri = ioService.newURI(current, "", null);
  var futureUri = ioService.newURI(url, "", currentUri);

  var loadEventExpected = true;

  if (currentUri && futureUri &&
      currentUri.prePath == futureUri.prePath &&
      currentUri.filePath == futureUri.filePath) {
    // Looks like we're at the same url with a ref
    // Being clever and checking the ref was causing me headaches.
    // Brute force for now
    loadEventExpected = futureUri.path.indexOf("#") == -1;
  }

  if (loadEventExpected) {
    new WebLoadingListener(this.browser_, function() {
      // TODO: Rescue the URI and response code from the event
      var responseText = "";
      respond.session.window = self.browser_.contentWindow;
      respond.response = responseText;
      respond.send();
    });
  }

  this.browser_.loadURI(url);

  if (!loadEventExpected) {
    respond.session.window = this.browser_.contentWindow;
    respond.send();
  }
};


FirefoxDriver.prototype.close = function(respond) {
  // Grab all the references we'll need. Once we call close all this might go away
  var wm = Utils.getService(
      "@mozilla.org/appshell/window-mediator;1", "nsIWindowMediator");
  var appService = Utils.getService(
      "@mozilla.org/toolkit/app-startup;1", "nsIAppStartup");
  var forceQuit = Components.interfaces.nsIAppStartup.eForceQuit;

  // Here we go!
  try {
    this.browser_.contentWindow.close();
  } catch(e) {
    dump(e);
  }

  // If we're on a Mac we might have closed all the windows but not quit,
  // so ensure that we do actually quit :)
  var allWindows = wm.getEnumerator("navigator:browser");
  if (!allWindows.hasMoreElements()) {
    appService.quit(forceQuit);
    return;  // The client should catch the fact that the socket suddenly closes
  }

  // If we're still running, return
  respond.send();
};


FirefoxDriver.prototype.executeScript = function(respond, script) {
  var doc = respond.session.window.document;
  var window = doc.defaultView;

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
    var scriptSrc = [
      "var __webdriverFunc = function(){",
      script.shift(),
      "};  __webdriverFunc.apply(window, __webdriverParams);"
    ].join('');

    var convert = script.shift();
    while (convert && convert.length > 0) {
      var t = convert.shift();

      if (t['type'] == "ELEMENT") {
        var element = Utils.getElementAt(t['value'], respond.session);
        t['value'] =
            element.wrappedJSObject ? element.wrappedJSObject : element;
      }

      parameters.push(t['value']);
    }

    var result = runScript(scriptSrc, parameters);

    // Sophisticated.
    if (result && result['tagName']) {
      respond.setField('resultType', "ELEMENT");
      respond.response = Utils.addToKnownElements(result, respond.session);
    } else if (result !== undefined) {
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
  var url = respond.session.window.location;
  if (!url) {
    url = this.browser_.contentWindow.location;
  }
  respond.response = url.href;
  respond.send();
};


FirefoxDriver.prototype.title = function(respond) {
  respond.response = this.browser_.contentTitle;
  respond.send();
};


FirefoxDriver.prototype.getPageSource = function(respond) {
  var doc = respond.session.window.document;
  var source = doc.getElementsByTagName("html")[0].innerHTML;
  respond.response = "<html>" + source + "</html>";
  respond.send();
};


FirefoxDriver.prototype.selectElementUsingXPath = function(respond, xpath) {
  var doc = respond.session.window.document;
  var result = doc.evaluate(xpath, doc, null, Components.interfaces.nsIDOMXPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;

  if (result) {
    respond.response = Utils.addToKnownElements(result, respond.session);
  } else {
    respond.isError = true;
    respond.response = "Unable to locate element using " + xpath;
  }

  respond.send();
};


FirefoxDriver.prototype.selectElementByName = function(respond, name) {
  var doc = respond.session.window.document;

  var elements = doc.getElementsByName(name);
  if (elements.length) {
    respond.response = Utils.addToKnownElements(elements[0], respond.session);
  } else {
    respond.isError = true;
    respond.response = "Unable to locate element with name '" + name + "'";
  }

  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingName = function(respond, name) {
  var doc = respond.session.window.document;

  var elements = doc.getElementsByName(name);
  var response = "";
  for (var i = 0; i < elements.length; i++) {
    var element = elements[i];
    var index = Utils.addToKnownElements(element, respond.session);
    response += index + ",";
  }
  // Strip the trailing comma
  response = response.substring(0, response.length - 1);

  respond.response = response;
  respond.send();
};


FirefoxDriver.prototype.selectElementUsingTagName = function(respond, name) {
  var doc = respond.session.window.document;

  var elements = doc.getElementsByTagName(name);
  if (elements.length) {
    respond.response = Utils.addToKnownElements(elements[0], respond.session);
  } else {
    respond.isError = true;
    respond.response = "Unable to locate element with name '" + name + "'";
  }

  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingTagName = function(respond, name) {
  var doc = respond.session.window.document;

  var elements = doc.getElementsByTagName(name);
  var response = "";
  for (var i = 0; i < elements.length; i++) {
    var element = elements[i];
    var index = Utils.addToKnownElements(element, respond.session);
    response += index + ",";
  }
  // Strip the trailing comma
  response = response.substring(0, response.length - 1);

  respond.response = response;
  respond.send();
};


FirefoxDriver.prototype.selectElementUsingClassName = function(respond, name) {
  var doc = respond.session.window.document;

  if (doc["getElementsByClassName"]) {
    var elements = doc.getElementsByClassName(name);

    if (elements.length) {
      respond.response =
      Utils.addToKnownElements(elements[0], respond.session);
    } else {
      respond.isError = true;
      respond.response =
          "Unable to locate element with class name '" + name + "'";
    }

    respond.send();
  } else {
    this.selectElementUsingXPath(respond,
        "//*[contains(concat(' ',normalize-space(@class),' '),' " + name + " ')]");
  }
};


FirefoxDriver.prototype.selectElementsUsingClassName = function(respond, name) {
  var doc = respond.session.window.document;

  if (doc["getElementsByClassName"]) {
    var result = doc.getElementsByClassName(name);

    var response = "";
    for (var i = 0; i < result.length; i++) {
      var element = result[i];
      var index = Utils.addToKnownElements(element, respond.session);
      response += index + ",";
    }
    // Strip the trailing comma
    response = response.substring(0, response.length - 1);

    respond.response = response;
    respond.send();
  } else {
    this.selectElementsUsingXPath(respond,
        "//*[contains(concat(' ',normalize-space(@class),' '),' " + name + " ')]");
  }
};


FirefoxDriver.prototype.selectElementUsingLink = function(respond, linkText) {
  var doc = respond.session.window.document;
  var allLinks = doc.getElementsByTagName("a");
  var index;
  for (var i = 0; i < allLinks.length && !index; i++) {
    var text = Utils.getText(allLinks[i], true);
    if (linkText == text) {
      index = Utils.addToKnownElements(allLinks[i], respond.session);
    }
  }

  if (index !== undefined) {
    respond.response = index;
  } else {
    respond.isError = true;
    respond.response =
        "Unable to locate element with link text '" + linkText + "'";
  }

  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingLink = function(respond, linkText) {
  var doc = respond.session.window.document;
  var allLinks = doc.getElementsByTagName("a");
  var indices = "";
  for (var i = 0; i < allLinks.length; i++) {
    var text = Utils.getText(allLinks[i], true);
    if (linkText == text) {
      indices += Utils.addToKnownElements(allLinks[i], respond.session) + ",";
    }

  }

  // Strip the trailing comma
  indices = indices.substring(0, indices.length - 1);

  respond.response = indices;
  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingPartialLinkText = function(
    respond, linkText) {
  var doc = respond.session.window.document;
  var allLinks = doc.getElementsByTagName("a");
  var indices = "";
  for (var i = 0; i < allLinks.length; i++) {
    var text = Utils.getText(allLinks[i], true);
    if (text.indexOf(linkText) != -1) {
      indices += Utils.addToKnownElements(allLinks[i], respond.session) + ",";
    }
  }

  respond.response = indices;
  respond.send();
};


FirefoxDriver.prototype.selectElementUsingPartialLinkText = function(
    respond, linkText) {
  var doc = respond.session.window.document;
  var allLinks = doc.getElementsByTagName("a");
  var index;
  for (var i = 0; i < allLinks.length && !index; i++) {
    var text = Utils.getText(allLinks[i], true);
    if (text.indexOf(linkText) != -1) {
      index = Utils.addToKnownElements(allLinks[i], respond.session);
      break;
    }
  }

  if (index !== undefined) {
    respond.response = index;
  } else {
    respond.isError = true;
    respond.response =
        "Unable to locate element with link text contains '" + linkText + "'";
  }

  respond.send();
};


FirefoxDriver.prototype.selectElementById = function(respond, id) {
  var element = respond.session.window.document.getElementById(id);

  if (element) {
    respond.response = Utils.addToKnownElements(element, respond.session);
  } else {
    respond.isError = true;
    respond.response = "Unable to locate element with id '" + id + "'";
  }

  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingId = function(respond, id) {
  var doc = respond.session.window.document;
  var allElements = doc.evaluate("//*[@id='" + id + "']", doc, null,
      Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE,
      null);
  var indices = "";
  var element = allElements.iterateNext();
  while (element) {
    var index = Utils.addToKnownElements(element, respond.session);
    indices += index + ",";
    element = allElements.iterateNext();
  }
  // Strip the trailing comma
  indices = indices.substring(0, indices.length - 1);

  respond.response = indices;
  respond.send();
};


FirefoxDriver.prototype.selectElementsUsingXPath = function(respond, xpath) {
  var doc = respond.session.window.document;
  var result = doc.evaluate(xpath, doc, null,
      Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
  var response = "";
  var element = result.iterateNext();
  while (element) {
    var index = Utils.addToKnownElements(element, respond.session);
    response += index + ",";
    element = result.iterateNext();
  }
  // Strip the trailing comma
  response = response.substring(0, response.length - 1);

  respond.response = response;
  respond.send();
};


FirefoxDriver.prototype.switchToFrame = function(respond, frameId) {
  var stringId = String(frameId);
  var names = stringId.split('.');

  function findFrame(id, rootFrame) {
    // Try a numerical index first.
    var index = Number(id);
    if (!isNaN(index) && rootFrame.frames[index]) {
      return rootFrame.frames[index];
    } else {
      // Fine. Use the name and loop.
      for (var j = 0; j < rootFrame.frames.length; j++) {
        var frame = rootFrame.frames[j];
        if (frame.name == id ||
            (frame.frameElement && frame.frameElement.id == id)) {
          return frame;
        }
      }
    }
    // Nothing found.
    return null;
  }

  var frame = this.browser_.contentWindow;
  for (var i = 0; i < names.length; i++) {
    frame = findFrame(names[i], frame);
    if (!frame) {
      respond.isError = true;
      respond.response = 'Cannot find frame with ID: ' + frameId.toString();
      return respond.send();
    }
  }

  if (!frame) {
    respond.isError = true;
    respond.response = 'Cannot find frame with ID: ' + frameId.toString();
  } else {
    respond.session.window = frame;
  }
  respond.send();
};


FirefoxDriver.prototype.switchToDefaultContent = function(respond) {
  respond.session.window = this.browser_.contentWindow;
  respond.send();
};


FirefoxDriver.prototype.switchToActiveElement = function(respond) {
  var element = Utils.getActiveElement(this.browser_, respond.session);
  respond.response = Utils.addToKnownElements(element, respond.session);
  respond.send();
};


FirefoxDriver.prototype.goBack = function(respond) {
  var browser = this.window.getBrowser();

  if (browser.canGoBack) {
    browser.goBack();
  }

  respond.send();
};


FirefoxDriver.prototype.goForward = function(respond) {
  var browser = this.window.getBrowser();

  if (browser.canGoForward) {
    browser.goForward();
  }

  respond.send();
};


FirefoxDriver.prototype.refresh = function(respond) {
  this.window.getBrowser().contentWindow.location.reload(true);
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

  var currLocation = this.browser_.contentWindow.location;
  if (!cookie.domain) {
    cookie.domain = currLocation.hostname; // + ":" + location.port;
    if (currLocation.port != 80) {
      cookie.domain += ":" + currLocation.port;
    }
  } else {
    var currDomain = currLocation.host;
    if (currLocation.port != 80) {
      currDomain += ":" + currLocation.port;
    }
    // Not quite right, but close enough
    if (currDomain.indexOf(cookie.domain) == -1) {
      respond.isError = true;
      respond.response = "You may only set cookies for the current domain";
      respond.send();
      return;
    }
  }

  var document = respond.session.window.document;
  if (!document || !document.contentType.match(/html/i)) {
    respond.isError = true;
    respond.response = "You may only set cookies on html documents";
    respond.send();
    return;
  }

  var cookieManager =
      Utils.getService("@mozilla.org/cookiemanager;1", "nsICookieManager2");

  // The signature for "add" is different in firefox 3 and 2. We should sniff
  // the browser version and call the right version of the method, but for now
  // we'll use brute-force.
  try {
    cookieManager.add(cookie.domain, cookie.path, cookie.name, cookie.value,
        cookie.secure, false, cookie.expiry);
  } catch(e) {
    cookieManager.add(cookie.domain, cookie.path, cookie.name, cookie.value,
        cookie.secure, false, false, cookie.expiry);
  }

  respond.send();
};


function handleCookies(browser, toCall) {
  var cm = Utils.getService("@mozilla.org/cookiemanager;1", "nsICookieManager");

  var makeStrippedHost = function (aHost) {
    var formattedHost = aHost.charAt(0) == "." ?
                        aHost.substring(1, aHost.length) : aHost;
    formattedHost = formattedHost.substring(0, 4) == "www." ?
                    formattedHost.substring(4, formattedHost.length) :
                    formattedHost;

    return formattedHost;
  };

  var location = browser.contentWindow.location;
  var currentDomain = makeStrippedHost(location.host);
  if (location.port != 80) {
    currentDomain += ":" + location.port;
  }
  var isForCurrentHost = function(aHost) {
    return currentDomain.indexOf(aHost) != -1;
  };

  var currentPath = browser.contentWindow.location.pathname;
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
  handleCookies(this.browser_, function(cookie) {
    var toAdd = cookieToString(cookie);
    toReturn += toAdd + "\n";
  });

  respond.response = toReturn;
  respond.send();
};


// This is damn ugly, but it turns out that just deleting a cookie from the
// document doesn't always do The Right Thing
FirefoxDriver.prototype.deleteCookie = function(respond, cookieString) {
  var cm = Utils.getService("@mozilla.org/cookiemanager;1", "nsICookieManager");
  var toDelete = eval('(' + cookieString + ')');

  handleCookies(this.browser_, function(cookie) {
    if (toDelete.name == cookie.name) {
      cm.remove(cookie.host, cookie.name, cookie.path, false);
    }
  });

  respond.send();
};


FirefoxDriver.prototype.setMouseSpeed = function(respond, speed) {
  this.mouseSpeed = speed;
  respond.send();
};


FirefoxDriver.prototype.getMouseSpeed = function(respond) {
  respond.response = "" + this.mouseSpeed;
  respond.send();
};


FirefoxDriver.prototype.saveScreenshot = function(respond, pngFile) {
  var window = this.browser_.contentWindow;
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
  respond.send();
};
