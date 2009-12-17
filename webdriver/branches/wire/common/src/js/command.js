/** @license
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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
 * @fileoverview Contains several classes for handling commands.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

goog.provide('webdriver.Command');
goog.provide('webdriver.CommandName');
goog.provide('webdriver.Response');

goog.require('goog.array');


/**
 * Describes a command to be executed by a
 * {@code webdriver.AbstractCommandProcessor}.
 * @param {webdriver.CommandName} name The name of this command.
 * @constructor
 */
webdriver.Command = function(name) {

  /**
   * A map of named parameters.
   * @type {Object}
   */
  this.parameters = {
    'commandName': name
  };

  /**
   * Callback for when the command processor successfully finishes this command.
   * The result of this function is included in the final result of the command.
   * @type {?function}
   */
  this.onSuccessCallbackFn = null;
  
  /**
   * Callback for when the command processor fails to successfully finish a
   * command. The function should take a single argument, the
   * {@code webdriver.Response} from the command processor. The response may be
   * modified (for example, to turn an expect failure into a success). If the
   * error state is cleared, the {@code onSucessCallbackFn} will still not be
   * called.
   * @type {?function}
   */
  this.onFailureCallbackFn = null;

  /**
   * Callback for when this command is completely finished, which is after the
   * response is set and success/failure callbacks have been run. The function
   * should take a single argument, a reference to this command.
   * @type {?function}
   * @private
   */
  this.onCompleteCallbackFn_ = null;

  /**
   * The response to this command.
   * @type {webdriver.Response}
   */
  this.response = null;

  /**
   * Whether this command was aborted.
   * @type {boolean}
   */
  this.abort = false;
};


/**
 * @return {webdriver.CommandName} The name of this command.
 */
webdriver.Command.prototype.getName = function() {
  return this.parameters['commandName'];
};


/**
 * Adds a named parameter for this command.
 * @param {string} name The name of the parameter.
 * @param {*} value The parameter value.
 * @return {webdriver.Command} A self reference.
 */
webdriver.Command.prototype.addParameter = function(name, value) {
  this.parameters[name] = value;
  return this;
};


/**
 * Set the function to call with the {@code webdriver.Response} when the
 * command processor successfully runs this command. This function is considered
 * part of the command and any errors will cause the command as a whole to fail.
 * @param {function} callbackFn The function to call on success.
 * @param {Object} opt_selfObj The object in whose context to execute the
 *     function.
 */
webdriver.Command.prototype.setSuccessCallback = function(callbackFn,
                                                          opt_selfObj) {
  if (callbackFn) {
    this.onSuccessCallbackFn = goog.bind(callbackFn, opt_selfObj);
  }
  return this;
};


/**
 * Set the function to call with the {@code webdriver.Response} when the
 * command processor encounters an error while executing this command.
 * @param {function} callbackFn The function to call on failure.
 * @param {Object} opt_selfObj The object in whose context to execute the
 *     function.
 */
webdriver.Command.prototype.setFailureCallback = function(callbackFn,
                                                          opt_selfObj) {
  if (callbackFn) {
    this.onFailureCallbackFn = goog.bind(callbackFn, opt_selfObj);
  }
  return this;
};


/**
 * Set the function to call with this command when it is completed.
 * @param {function} callbackFn The function to call on command completion.
 * @param {Object} opt_selfObj The object in whose context to execute the
 *     function.
 */
webdriver.Command.prototype.setCompleteCallback = function(callbackFn,
                                                           opt_selfObj) {
  if (callbackFn) {
    this.onCompleteCallbackFn_ = goog.bind(callbackFn, opt_selfObj);
  }
  return this;
};


/**
 * Set the response for this command. The response may only be set once; any
 * repeat calls will be ignored.
 * @param {webdriver.Response} response The response.
 * @throws If the response was already set.
 */
webdriver.Command.prototype.setResponse = function(response) {
  if (this.response) {
    return;
  }
  this.response = response;

  var sandbox = goog.bind(function(fn) {
    try {
      fn.call(this, this.response);
    } catch (ex) {
      this.response.isFailure = true;
      this.response.errors.push(ex);
    }
  }, this);

  if (!this.response.errors.length) {
    if (this.response.isFailure &&
        goog.isFunction(this.onFailureCallbackFn)) {
      sandbox(this.onFailureCallbackFn);
    } else if (!this.response.isFailure &&
               goog.isFunction(this.onSuccessCallbackFn)) {
      sandbox(this.onSuccessCallbackFn);
    }
  }

  if (this.onCompleteCallbackFn_) {
    this.onCompleteCallbackFn_(this);
  }
};


/**
 * Enumeration of predefined names command names that all command processors
 * will support.
 * @enum {string}
 */
webdriver.CommandName = {
  /* NOTE(jmleyba): This enum should be kept in sync with
   * org.openqa.selenium.remote.DriverCommand
   */

  // Commands executed directly by the JS API. --------------------------------
  FUNCTION: 'function',
  SLEEP: 'sleep',
  WAIT: 'wait',
  PAUSE: 'pause',

  // Commands dispatched to the browser driver. -------------------------------
  NEW_SESSION: 'newSession',
  DELETE_SESSION: 'deleteSession',

  CLOSE: 'close',
  QUIT: 'quit',

  GET: 'get',
  BACK: 'goBack',
  FORWARD: 'goForward',
  REFRESH: 'refresh',

  ADD_COOKIE: 'addCookie',
  GET_COOKIE: 'getCookie',
  GET_ALL_COOKIES: 'getCookies',
  DELETE_COOKIE: 'deleteCookie',
  DELETE_ALL_COOKIES: 'deleteAllCookies',

  FIND_ELEMENT: 'findElement',
  FIND_ELEMENTS: 'findElements',
  FIND_CHILD_ELEMENT: 'findChildElement',
  FIND_CHILD_ELEMENTS: 'findChildElements',

  CLEAR: 'clearElement',
  CLICK: 'clickElement',
  HOVER: 'hoverOverElement',
  SEND_KEYS: 'sendKeysToElement',
  SUBMIT: 'submitElement',
  TOGGLE: 'toggleElement',

  SWITCH_TO_WINDOW: 'switchToWindow',
  SWITCH_TO_FRAME: 'switchToFrame',
  SWITCH_TO_DEFAULT_CONTENT: 'switchToDefaultContent',
  GET_ACTIVE_ELEMENT: 'getActiveElement',

  GET_CURRENT_URL: 'getCurrentUrl',
  GET_PAGE_SOURCE: 'getPageSource',
  GET_TITLE: 'getTitle',

  EXECUTE_SCRIPT: 'executeScript',

  GET_SPEED: 'getSpeed',
  SET_SPEED: 'setSpeed',

  GET_CURRENT_WINDOW_HANDLE: 'getCurrentWindowHandle',
  GET_WINDOW_HANDLES: 'getWindowHandles',
  SET_VISIBLE: 'setVisible',
  GET_VISIBLE: 'getVisible',

  GET_TEXT: 'getElementText',
  GET_VALUE: 'getElementValue',
  GET_TAG_NAME: 'getElementTagName',
  SET_SELECTED: 'setElementSelected',
  DRAG_ELEMENT: 'dragElement',
  IS_SELECTED: 'isElementSelected',
  IS_ENABLED: 'isElementEnabled',
  IS_DISPLAYED: 'isElementDisplayed',
  GET_LOCATION: 'getElementLocation',
  GET_SIZE: 'getElementSize',
  GET_ATTRIBUTE: 'getElementAttribute',
  GET_VALUE_OF_CSS_PROPERTY: 'getElementValueOfCssProperty'
};


/**
 * Encapsulates a response to a {@code webdriver.Command}.
 * @param {boolean} isFailure Whether the command resulted in an error. If
 *     {@code true}, then {@code value} contains the error message.
 * @param {webdriver.Context} context The (potentially new) context resulting
 *     from the command.
 * @param {string} value The value of the response, the meaning of which depends
 *     on the command.
 * @parma {Error} opt_error An error that caused this command to fail
 *     prematurely.
 * @constructor
 */
webdriver.Response = function(isFailure, context, value, opt_error) {
  this.isFailure = isFailure;
  this.context = context;
  this.value = value;
  this.errors = goog.array.slice(arguments, 3);
  this.extraData = {};
};
