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
 * Wraps a {@code nsIServerSocket} for listening for commands to the
 * FirefoxDriver.
 * @constructor
 * @extends {nsIServerSocketListener}
 */
function WebDriverServer() {
  this.wrappedJSObject = this;
  this.serverSocket = Components.
      classes["@mozilla.org/network/server-socket;1"].
      createInstance(Components.interfaces.nsIServerSocket);
  this.generator = Utils.getService("@mozilla.org/uuid-generator;1", "nsIUUIDGenerator");
  this.enableNativeEvents = null;
}


/**
 * The default port the WebDriverServer will listen on.
 * @type {number}
 */
WebDriverServer.DEFAULT_PORT = 7055;


/**
 * Name of the profile preference that defines which port to listen on.
 * @type {string}
 */
WebDriverServer.PORT_PREFERENCE = 'webdriver_firefox_port';


/**
 * Name of the profile preference that defines whether to use native events.
 * @type {string}
 */
WebDriverServer.NATIVE_EVENT_PREFERENCE = 'webdriver_enable_native_events';


/**
 * Creates a new FirefoxDriver for the given {@code window}. The driver will
 * be accessible as the {@code fxdriver} property.
 * @param {nsIDOMWIndow} window The window to attach the new driver to.
 * @return {FirefoxDriver} The new driver instance.
 */
WebDriverServer.prototype.newDriver = function(window) {
  if (null == this.useNativeEvents) {
    var prefs = Components.
      classes["@mozilla.org/preferences-service;1"].
      getService(Components.interfaces.nsIPrefBranch);
    if (!prefs.prefHasUserValue(WebDriverServer.NATIVE_EVENT_PREFERENCE)) {
      Utils.dumpn('webdriver_enable_native_events not set; defaulting to true');
    }
    this.enableNativeEvents =
        prefs.prefHasUserValue(WebDriverServer.NATIVE_EVENT_PREFERENCE) ?
        prefs.getBoolPref(WebDriverServer.NATIVE_EVENT_PREFERENCE) : false;
    Utils.dumpn('Enable native events: ' + this.enableNativeEvents);
  }
  window.fxdriver = new FirefoxDriver(this, this.enableNativeEvents);
  // Yuck. But it allows us to refer to it later.
  window.fxdriver.window = window;

  return window.fxdriver;
};


/**
 * Returns a random UUID.
 * @return {string} The random UUID as a string.
 */
WebDriverServer.prototype.getNextId = function() {
  return this.generator.generateUUID().toString();
};


/**
 * Called when the wrapped {@code nsIServerSocket} has accepted a new
 * connection. Creates a new {@code SocketListener} to handle the actual
 * socket I/O.
 * @param {nsIServerSocket} socket The server socket.
 * @param {nsISocketTransport} transport The connected socket transport.
 * @see {nsIServerSocketListener#onSocketAccepted}
 */
WebDriverServer.prototype.onSocketAccepted = function(socket, transport) {
  try {
    var socketListener = new SocketListener(transport);
  } catch(e) {
    dump(e);
  }
};


/**
 * Starts listening on the specified port. The port is chosed in the
 * following order:
 * - The argument passed to this function.
 * - The value of the {@code WebDriverServer.PORT_PREFERENCE}
 * - {@code WebDriverServer.DEFAULT_PORT}.
 * @param {number} opt_port The port to listen on.
 */
WebDriverServer.prototype.startListening = function(opt_port) {
  var port = opt_port;
  if (!port) {
    var prefs = Components.
      classes["@mozilla.org/preferences-service;1"].
      getService(Components.interfaces.nsIPrefBranch);
    port = prefs.prefHasUserValue(WebDriverServer.PORT_PREFERENCE) ?
           prefs.getIntPref(WebDriverServer.PORT_PREFERENCE) :
           WebDriverServer.DEFAULT_PORT;
  }

  if (!this.isListening) {
    this.serverSocket.init(port, true, -1);
    this.serverSocket.asyncListen(this);
    this.isListening = true;
  }
};


/**
 * Called when the server socket is closed.
 * @param {nsIServerSocket} socket The closed socket.
 * @param {nsresult} status Indicates why the connection was closed.
 * @see {nsIServerSocketListener#onStopListening}
 */
WebDriverServer.prototype.onStopListening = function(socket, status) {
};


/**
 * @see {nsISupports#QueryInterface} 
 */
WebDriverServer.prototype.QueryInterface = function(aIID) {
  if (!aIID.equals(nsISupports))
    throw Components.results.NS_ERROR_NO_INTERFACE;
  return this;
};


WebDriverServer.prototype.createInstance = function(ignore1, ignore2, ignore3) {
  var port = WebDriverServer.readPort();
  this.startListening(port);
};
