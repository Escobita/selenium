/*
 Copyright 2011 WebDriver committers
 Copyright 2011 Software Freedom Conservancy

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
 * @fileOverview Implements an end-point for the firefox driver that works by
 * creating a hanging GET and channeling commands into the command processor.
 * This is a far lighter implementation than the normal HTTPd approach, and is
 * suitable for use within mobile clients.
 */

goog.provide('XhrEndPoint');

goog.require('bot.ErrorCode');
goog.require('fxdriver.Logger');
goog.require('fxdriver.moz');


var CC = Components.classes;
var CI = Components.interfaces;

/**
 * Create a new XHR end point that connects to the given remote URL.
 */
XhrEndPoint = function() {
  this.wrappedJSObject = this;

  this.QueryInterface = fxdriver.moz.queryInterface(this,
      [CI.nsISupports, CI.wdIClientDispatcher]);

  fxdriver.Logger.dumpn('Loading reference to the command processor');
  this.commandProcessor_ = fxdriver.moz.getService(
      '@googlecode.com/webdriver/command-processor;1', 'nsICommandProcessor');
};

/**
 * @param {nsIURL} url The remote address to connect to.
 */
XhrEndPoint.prototype.connectTo = function(url) {
  fxdriver.Logger.dumpn('Creating XHR end point connecting to ' + url);
  this.homeUrl_ = url.toString();

  this.handleResponse('{status: 0, value: "OK", name: "ping"}');
};

/**
 * {object} The instance of nsICommandProcessor to delegate calls to.
 */
XhrEndPoint.prototype.commandProcessor_ = null;


/**
 * {string} The URL of the server we're chatting with.
 */
XhrEndPoint.prototype.homeUrl_ = '';


/**
 * Handle an incoming response. This will create a new XHR and connect
 * @param jsonResponse
 */
XhrEndPoint.prototype.handleResponse = function(jsonResponse) {
  var xhr = fxdriver.moz.newInstance(
      '@mozilla.org/xmlextras/xmlhttprequest;1', 'nsIXMLHttpRequest');

  xhr.mozBackgroundRequest = true;

  // Open using a POST. On mobile devices on normal mobile networks there's a
  // crazy amount of caching going on. Sometimes even a POST is cached, but at
  // least we should make an effort to avoid this.
  xhr.open('POST', this.homeUrl_, true);

  // Now attach the listeners
  xhr.onload = goog.bind(this.onLoad_, this, xhr);
  xhr.onerror = goog.bind(this.onError_, this, xhr);
  xhr.setRequestHeader('Content-Type', 'application/json');
  // TODO(simon): Ensure that the text we're returning is UTF8

  xhr.send(jsonResponse);
};


/**
 *  Default handler for when the XHR loads.
 *
 * @param {!XMLHttpRequest} xhr The XHR object associated with this call.
 * @param {!object} event The event to examine.
 */
XhrEndPoint.prototype.onLoad_ = function(xhr, event) {
  // TODO(simon): Add error checking. *cough*
  var rawJson = xhr.responseText;

  try {
    this.commandProcessor_.execute(rawJson, this);
  } catch (e) {
    fxdriver.Logger.dumpn('Fatal error detected: ' + e);
    // TODO(simon): respond
  }
};


/**
 * Default handler for when the XHR encounters an error.
 *
 * @param {!XMLHttpRequest} xhr The XHR object associated with this call.
 * @param {!object} event The event to examine.
 */
XhrEndPoint.prototype.onError_ = function(xhr, event) {
  fxdriver.Logger.dumpn('Error detected');
//  this.handleResponse('{status: ' + bot.ErrorCode.UNKNOWN_ERROR + ', sessionId: null}');
};


// And finally, registering
XhrEndPoint.prototype.classDescription = 'A dispatcher that works via XMLHttpRequests';
XhrEndPoint.prototype.contractID = '@googlecode.com/webdriver/xhr-dispatcher;1';
XhrEndPoint.prototype.classID = Components.ID('{c9e3a64a-4804-4b7e-8cb2-69d4c03b4a5e}');

/** @const */ var components = [XhrEndPoint];
var NSGetFactory, NSGetModule;

fxdriver.moz.load('resource://gre/modules/XPCOMUtils.jsm');

if (XPCOMUtils.generateNSGetFactory) {
  NSGetFactory = XPCOMUtils.generateNSGetFactory(components);
} else {
  NSGetModule = XPCOMUtils.generateNSGetModule(components);
}

goog.exportSymbol('XhrEndPoint', XhrEndPoint);
