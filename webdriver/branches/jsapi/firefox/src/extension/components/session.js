/**
 * @fileoverview Encapsulates the information for an active session with the
 * FirefoxDriver extension.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

// TODO(jmleyba): This should be made a component so it doesn't have to be
// loaded as a subscript.

/**
 * Encapsulates the information describing a session communicating with this
 * driver.
 * @param {FirefoxDriver} driver The FirefoxDriver currently being used by this
 *     session.
 * @constructor
 */
var Session = function(driver) {
  /**
   * This session's UUID.
   * @type {string}
   * @private
   */
  this.uuid_ = Session.getNextSessionId();

  this.setDriver(driver);
};


/**
 * Generates the next session UUID.
 * @return {string} The string representation of the next session UUID.
 */
Session.getNextSessionId = function() {
  return Components.
      classes["@mozilla.org/uuid-generator;1"].
      getService(Components.interfaces.nsIUUIDGenerator).
      generateUUID().toString();
};


/**
 * The current driver for this session.
 * @type {?FirefoxDriver}
 * @private
 */
Session.prototype.driver_ = null;


/**
 * The content window this session is currently focused on. This window should
 * always be contained within the session's driver's browser.
 *
 * <p>We store the current window in the session instead of in the FirefoxDriver
 * since multiple sessions could be manipulating the same page. The only
 * scenario in which this should be done is for running WebDriverJS tests; a
 * JVM will open the browser to the WebDriverJS test page and then poll for the
 * test result. Meanwhile, the test page will have sessions of its own for
 * WebDriverJS.
 * 
 * @type {?nsIDOMWindow}
 * @private
 */
Session.prototype.window_ = null;


/**
 * Getter for this Session's ID.
 * @return {string} The session UUID as a string.
 */
Session.prototype.getId = function() {
  return this.uuid_;
};


/**
 * Sets the current driver for this session; updates the current window to the
 * contentWindow of the driver's browser.
 * @param {FirefoxDriver} driver The new driver to use for this session.
 */
Session.prototype.setDriver = function(driver) {
  this.driver_ = driver;
  this.window = this.driver_.getBrowser().contentWindow;
};


/**
 * @return {FirefoxDriver} The current driver for this session.
 */
Session.prototype.getDriver = function() {
  return this.driver_;
};


/**
 * @return {nsIDOMWindow} The content window this session is currently focused
 *     on.
 */
Session.prototype.__defineGetter__('window', function() {
  return this.window_;
});


/**
 * Sets the content window for this session to focus on. If the page contains a
 * FRAMESET, the content window will be adjusted to select the first frame in
 * the set. This is assuming the topmost window contains nothing but the
 * frameset (standard use of frameset).
 * @type {nsIDOMWindow} The content window to focus on. 
 */
Session.prototype.__defineSetter__('window', function(win) {
  this.window_ = win;
  var frames = this.window_.frames;
  if (frames && frames.length && 'FRAME' == frames[0].frameElement.tagName) {
    this.window_ = frames[0];
  }
});

