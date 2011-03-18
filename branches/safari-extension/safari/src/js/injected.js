goog.provide('selenium.safari.Injected');

goog.require('goog.debug.Console');
goog.require('goog.debug.Logger');


/**
 * The injected WebDriver object, does the wiring.
 * @constructor
 */
selenium.safari.Injected = function() {
  /**
   * An array to store all elements ever found by findElement methods.
   * @type {Array}
   * @private
   */
  this.elementStore_ = new Array();

  /**
   * The bindings for the command handling mechanism.
   */
  this.bindings_ = {};


  this.logger_ = goog.debug.Logger.getLogger('selenium.safari.Injected');
  this.logger_.setLevel(goog.debug.Logger.Level.FINEST);

  this.logConsole_ = new goog.debug.Console();
  this.logConsole_.setCapturing(true);
};


selenium.safari.Injected.prototype.onMessageReceived_ = function(event) {
  this.logger_.info("Received " + JSON.stringify(event));

  var target = this.bindings_[event.message.command];
  if (target) {
    target(event.message);
  } else {
    // Send back error
  }
};


selenium.safari.Injected.prototype.sendResponse_ = function(status, payload) {
  safari.self.tab.dispatchMessage('sendResponse', {status: status, payload: payload});
};


// WebDriver implementation methods.


selenium.safari.Injected.prototype.initBindings_ = function() {
  this.bindings_.executeScript = goog.bind(this.executeScript_, this);
};


selenium.safari.Injected.prototype.executeScript_ = function(data) {
  this.logger_.info('Executing script');

  // TODO(kurniady): implement element wrapping and response code
  // TODO(kurniady): make this hop into the page's JS context.
  eval(data.script);

  this.sendResponse_(0, null);
};


/**
 * Initializes the Injected WebDriver.
 */
selenium.safari.Injected.prototype.init = function() {
  this.initBindings_();
  safari.self.addEventListener('message', goog.bind(this.onMessageReceived_, this), false);
};


var injected = new selenium.safari.Injected();
injected.init();
