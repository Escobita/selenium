/*
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
 * @fileoverview The heart of the WebDriver JavaScript API.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

goog.provide('webdriver.Event');
goog.provide('webdriver.Event.Type');
goog.provide('webdriver.WebDriver');

goog.require('goog.events');
goog.require('goog.events.Event');
goog.require('goog.events.EventTarget');
goog.require('webdriver.CommandInfo');
goog.require('webdriver.Context');
goog.require('webdriver.Future');
goog.require('webdriver.Response');
goog.require('webdriver.WebElement');


/**
 * Extends {@code goog.events.Event} so arbitrary data can be passed along to
 * event listeners.
 * @param {string} type The event type.
 * @param {*} data The data to send with the event.
 * @param {webdriver.WebDriver} target The {@code webdriver.WebDriver} that
 *     dispatched the event.
 * @extends {goog.events.Event}
 * @constructor
 */
webdriver.Event = function(type, data, target) {
  goog.events.Event.call(this, type, target);

  /**
   * The data associated with this event.
   * @type {*}
   */
  this.data = data;
};
goog.inherits(webdriver.Event, goog.events.Event);


/**
 * The types of events that may be dispatched by a {@code webdriver.WebDriver}
 * instance.
 * @enum {string}
 */
webdriver.Event.Type = {
  IDLE: 'IDLE',
  READY: 'READY',
  PAUSED: 'PAUSED',
  RESUMED: 'RESUMED',
  ERROR: 'ERROR'
};


/**
 * TODO(jmleyba): More documentation.
 * @param {Object} commandProcessor The command processor to use for executing
 *     individual {@code webdriver.Command}s.
 * @constructor
 */
webdriver.WebDriver = function(commandProcessor) {
  goog.events.EventTarget.call(this);

  /**
   * The command processor to use for executing commands.
   * @type {Object}
   * @private
   */
  this.commandProcessor_ = commandProcessor;

  /**
   * This instances current context (window and frame ID).
   * @type {webdriver.Context}
   * @private
   */
  this.context_ = new webdriver.Context();

  /**
   * Whether this instance is ready to execute another command.
   * @type {boolean}
   * @private
   */
  this.isReady_ = true;

  /**
   * Whether this instance is paused. When paused, commands can still be issued,
   * but no commands will be executed.
   * @type {boolean}
   * @private
   */
  this.isPaused_ = false;

  /**
   * Whether this instance has thrown an {@code ERROR} event. Once the driver
   * enter this state, no commands will be executed until the error is cleared.
   * @type {boolean}
   * @private
   */
  this.inError_ = false;

  /**
   * Whether this instance is locked into its current session. Once locked in,
   * any further calls to {@code webdriver.WebDriver.prototype.newSession} will
   * be ignored.
   * @type {boolean}
   * @private
   */
  this.sessionLocked_ = false;

  /**
   * This instance's current session ID.  Set with the
   * {@code webdriver.WebDriver.prototype.newSession} command.
   * @type {?string}
   */
  this.sessionId_ = null;

  /**
   * A queue of pending commands.
   * @type {Array.<webdriver.Command>}
   * @priate
   */
  this.commands_ = [];

  /**
   * The response received by the last executed command.
   * @type {webdriver.Resposne}
   * @private
   */
  this.lastResponse_ = null;

  goog.events.listen(this,
      [webdriver.Event.Type.READY, webdriver.Event.Type.RESUMED],
      goog.bind(this.onReady_, this));
};
goog.inherits(webdriver.WebDriver, goog.events.EventTarget);


/**
 * Enumeration of the supported mouse speeds.
 * @enum {number}
 * @see webdriver.WebDriver.prototype.setMouseSpeed
 * @see webdriver.WebDriver.prototype.getMouseSpeed
 */
webdriver.WebDriver.Speed = {
  SLOW: 1,
  MEDIUM: 10,
  FAST: 100
};


/**
 * Adds a command to the queue.  Commands may be a number (to indicate this
 * instance should pause for the specified amount of time), a function to call
 * (in which case the last response from a {@code webdriver.Command} is passed
 * to the function as its only argument), or a {@code webdriver.Command} to send
 * to the command processor.
 * <p/>
 * This method is considered package-protected.
 * @param {webdriver.Command|number|function} command The command to execute.
 * @param {boolean} opt_addToFront Whether to add the command to the front or
 *     back of the queue.  Defaults to false.
 * @protected
 */
webdriver.WebDriver.prototype.addCommand = function(command, opt_addToFront) {
  if (opt_addToFront) {
    goog.array.splice(this.commands_, 0, 0, command);
  } else {
    this.commands_.push(command);
  }

  if (this.commands_.length == 1 && this.isReady_ && !this.isPaused_) {
    this.dispatchEvent(webdriver.Event.Type.READY);
  }
};


/**
 * @return {boolean} Whether this instance has any pending commands.
 */
webdriver.WebDriver.prototype.hasPendingCommands = function() {
  return this.commands_.length;
};


webdriver.WebDriver.prototype.expectErrorFromPreviousCommand = function(
    opt_msg) {
  var caughtError = false;
  var handleError = goog.bind(function(e) {
    caughtError = true;
    e.stopPropagation();
    this.clearError(/*resumeCommands=*/true);
  }, this);
  var numCommands = this.commands_.length;
  if (!numCommands) {
    throw new Error('No commands to expect an error from!!');
  }

  // Surround the last command with two new commands. The first enables our
  // error listener which cancels any errors. The second verifies that we
  // caught an error. If not, it fails the test.
  goog.array.splice(this.commands_, Math.max(0, numCommands - 1), 0,
      goog.bind(function() {
        goog.events.listen(this, webdriver.Event.Type.ERROR, handleError,
                           /*capture=*/true);
      }, this));
  this.commands_.push(goog.bind(function() {
    // Need to unlisten for error events so the error below doesn't get blocked.
    goog.events.unlisten(this, webdriver.Event.Type.ERROR, handleError,
                         /*capture=*/true);
    if (!caughtError) {
      throw new Error(
          (opt_msg ? (opt_msg + '\n') : '') +
          'Expected an error but none were raised.\n' +
          'Last response was: ' +
          (this.lastResponse_ ? this.lastResponse_.value : 'null'));
    }
  }, this));
};


/**
 * Clears the this instance's error state, if there is one.
 * @param {boolean} opt_resumeCommands Whether to resume command execution when
 *     error state is cleared. If {@code false}, all pending commands will be
 *     aborted and an {@code webdriver.Event.Type.IDLE} event dispatched. If
 *     {@code true}, dispatches a {@code webdriver.Event.Type.READY} event to
 *     resume command execution. Defaults to {@code false}.
 */
webdriver.WebDriver.prototype.clearError = function(opt_resumeCommands) {
  if (this.inError_) {
    this.isReady_ = true;
    this.inError_ = false;
    var eventType;
    if (opt_resumeCommands) {
      eventType = webdriver.Event.Type.READY;
      webdriver.logging.debug(
          'Error state cleared; resuming command execution...');
    } else {
      eventType = webdriver.Event.Type.IDLE;
      this.commands_ = [];
      webdriver.logging.debug(
          'Error state cleared; aborting pending commands...');
    }
    window.setTimeout(goog.bind(this.dispatchEvent, this, eventType), 0);
  }
};


/**
 * Pauses this driver so it will not execute any commands.  Dispatches a
 * {@code webdriver.Event.Type.PAUSED} event.
 */
webdriver.WebDriver.prototype.pause = function() {
  this.isPaused_ = true;
  webdriver.logging.debug('Webdriver paused');
  this.dispatchEvent(webdriver.Event.Type.PAUSED);
};




/**
 * Unpauses this driver so it can execute commands again.  Dispatches a
 * {@code webdriver.Event.Type.RESUMED} event.
 */
webdriver.WebDriver.prototype.resume = function() {
  this.isPaused_ = false;
  webdriver.logging.debug('Webdriver resumed');
  this.dispatchEvent(webdriver.Event.Type.RESUMED);
};


webdriver.WebDriver.updateCommandFutures_ = function(command) {
  function getValue(obj) {
    if (obj instanceof webdriver.Future) {
      return obj.getValue();
    } else if (obj instanceof webdriver.WebDriver.ScriptArgument) {
      obj.value = getValue(obj.value);
    }
    return obj;
  }
  if (goog.isDef(command.elementId)) {
    command.elementId = getValue(command.elementId);
  }
  command.parameters = goog.array.map(command.parameters, function(param) {
    // TODO(jmleyba): Need a better way of detecting this.
    if (goog.isArray(param)) {
      return goog.array.map(param, getValue);
    } else {
      return getValue(param);
    }
  });
};


/**
 * Event handler for whenever this driver is ready to execute a command.
 * @private
 */
webdriver.WebDriver.prototype.onReady_ = function() {
  if (this.isPaused_ || !this.isReady_) {
    webdriver.logging.debug('not ready to execute a command');
    return;
  } else if (this.inError_) {
    // TODO(jmleyba): What's the correct way to do this.
    throw new Error('WebDriver is in an error state.');
  }

  var nextCommand = this.commands_.shift();
  if (nextCommand) {
    this.isReady_ = false;

    if (goog.isNumber(nextCommand)) {
      webdriver.logging.info('Sleeping for ' + nextCommand + 'ms');
      var response = new webdriver.Response(
          new webdriver.Command(this.sessionId_, this.context_),
          false, this.context_, nextCommand);
      window.setTimeout(
          goog.bind(this.handleResponse_, this, response), nextCommand);

    } else if (goog.isFunction(nextCommand)) {
      var command = new webdriver.Command(
          this.sessionId_, this.context_, null, [this.lastResponse_]);
      try {
        var result =
            nextCommand(this.lastResponse_ ? this.lastResponse_ : null);
        this.handleResponse_(new webdriver.Response(
            command, false, this.context_, result));
      } catch (ex) {
        this.handleResponse_(new webdriver.Response(
            command, true, this.context_, ex));
      }

    } else if (nextCommand instanceof webdriver.Command) {
      nextCommand.sessionId = this.sessionId_;
      nextCommand.context = this.context_.toString();

      try {
        webdriver.WebDriver.updateCommandFutures_(nextCommand);
      } catch (ex) {
        this.handleResponse_(new webdriver.Response(
            nextCommand, true, this.context_, ex));
        return;
      }

      this.commandProcessor_.execute(nextCommand,
          goog.bind(this.handleResponse_, this));

    } else {
      this.handleResponse_(new webdriver.Response(
          nextCommand, true, this.context_,
          'Invalid command type: ' + typeof nextCommand));
    }

  } else {
    webdriver.logging.debug('No more commands; driver is going idle');
    this.dispatchEvent(webdriver.Event.Type.IDLE);
  }
};


/**
 * Callback function that processes {@code webdriver.Resposne} objects returned
 * by the command processor.
 * @param {webdriver.Response} response The response to process.
 * @private
 */
webdriver.WebDriver.prototype.handleResponse_ = function(response) {
  webdriver.logging.debug(
      '...received response. Was error? ' + response.isError);
  this.lastResponse_ = response;
  if (response.isError) {
    with(webdriver.logging) { // TODO
      error(describe(response));
    }
    // The errorCallbackFn may be expecting our error.
    if (response.command.errorCallbackFn) {
      try {
        response.command.errorCallbackFn(response);
      } catch (ex) {
        // TODO(jmleyba): Do nothing? Report the error? What?
      }
    }

    if (response.isError) {
      this.inError_ = true;
      this.dispatchEvent(new webdriver.Event(
          webdriver.Event.Type.ERROR, response.value, this));
    } else {
      this.isReady_ = true;
      this.dispatchEvent(webdriver.Event.Type.READY);
    }
    return;
  }

  this.context_ = response.context;
  if (response.command.callbackFn) {
    try {
      response.command.callbackFn(response);
    } catch (ex) {
      this.inError_ = true;
      this.dispatchEvent(
          new webdriver.Event(webdriver.Event.Type.ERROR, ex, this));
      return;
    }
  }

  this.isReady_ = true;
  this.dispatchEvent(webdriver.Event.Type.READY);
};


/**
 * @return {?string} This instance's current session ID or {@code null} if it
 *     does not have one yet.
 */
webdriver.WebDriver.prototype.getSessionId = function() {
  return this.sessionId_;
};


/**
 * @return {webdriver.Context} This instance's current context.
 */
webdriver.WebDriver.prototype.getContext = function() {
  return this.context_;
};


// ----------------------------------------------------------------------------
// Client command functions:
// ----------------------------------------------------------------------------

/**
 * Adds a command to make the driver sleep for the specified amount of time.
 * @param {number} ms The amount of time in milliseconds for the driver to
 *     sleep.
 */
webdriver.WebDriver.prototype.sleep = function(ms) {
  this.addCommand(ms);
};


/**
 * Inserts a function into the command queue for the driver to call. The
 * function will be passed the last {@code webdriver.Response} retrieved from
 * the command processor.
 * @param {function} fn The function to call; should take a single
 *     {@code webdriver.Response} object.
 */
webdriver.WebDriver.prototype.callFunction = function(fn) {
  this.addCommand(fn);
};


/**
 * Adds a command to request a new session ID. This is a no-op if this instance
 * is already locked into another session.
 * @param {boolean} lockSession Whether to lock this instance into the returned
 *     session. Once locked into a session, the driver cannot ask for a new
 *     session (a new instance must be created).
 */
webdriver.WebDriver.prototype.newSession = function(lockSession) {
  if (lockSession) {
    this.addCommand(webdriver.CommandInfo.NEW_SESSION.buildCommand(this, null,
        goog.bind(function(response) {
          this.sessionLocked_ = lockSession;
          if (!(this.commandProcessor_ instanceof
                webdriver.LocalCommandProcessor)) {
            this.sessionId_ = response.value;
          }
          this.context_ = response.context;
        }, this)));
  } else {
    webdriver.logging.warn(
        'Cannot start new session; driver is locked into current session');
  }
};


/**
 * Adds a command to switch to a window with the given name.
 * @param {string} name The name of the window to transfer control to.
 */
webdriver.WebDriver.prototype.switchToWindow = function(name) {
  this.addCommand(webdriver.CommandInfo.SWITCH_TO_WINDOW.buildCommand(
      this, [name], goog.bind(function(response) {
        this.context_ = response.value;
      }, this)));
};


/**
 * Adds a command to switch to a frame in the current window.
 * @param {string} name The name of the window to transfer control to.
 */
webdriver.WebDriver.prototype.switchToFrame = function(name) {
  this.addCommand(webdriver.CommandInfo.SWITCH_TO_FRAME.buildCommand(
      this, [name], goog.bind(function(response) {
        this.context_ = response.context;
      }, this)));
};


/**
 * Adds a command to switch to the top frame in the current window.
 */
webdriver.WebDriver.prototype.switchToDefaultContent = function() {
  this.addCommand(webdriver.CommandInfo.SWITCH_TO_DEFAULT_CONTENT.buildCommand(
      this, [null], goog.bind(function(response) {
        this.context_ = response.context;
      }, this)));
};


/**
 * Adds a command to get the current window handle.
 * @return {webdriver.Future} The current handle wrapped in a Future.
 */
webdriver.WebDriver.prototype.getWindowHandle = function() {
  var handle = new webdriver.Future(this);
  this.addCommand(webdriver.CommandInfo.GET_CURRENT_WINDOW_HANDLE.buildCommand(
      this, null, goog.bind(handle.setValueFromResponse, handle)));
  return handle;
};


/**
 * Adds a command to get all of the current window handles.
 */
webdriver.WebDriver.prototype.getAllWindowHandles = function() {
  this.addCommand(webdriver.CommandInfo.GET_CURRENT_WINDOW_HANDLES.buildCommand(
      this, null,
      function(response) {
        response.value = response.value.split(',');
      }));
};


/**
 * Adds a command to fetch the HTML source of the current page.
 * @return {webdriver.Future} The page source wrapped in a Future.
 */
webdriver.WebDriver.prototype.getPageSource = function() {
  var source = new webdriver.Future(this);
  this.addCommand(webdriver.CommandInfo.GET_PAGE_SOURCE.buildCommand(
      this, null, goog.bind(source.setValueFromResponse, source)));
  return source;
};


/**
 * Adds a command to close the current window.
 */
webdriver.WebDriver.prototype.close = function() {
  this.addCommand(webdriver.CommandInfo.CLOSE.buildCommand(this));
};



webdriver.WebDriver.ScriptArgument = function(type, value) {
  this.type = type;
  this.value = value;
};
/**
 * Helper function for converting an argument to a script into a parameter
 * object to send with the {@code webdriver.Command}.
 * @param {*} arg The value to convert.
 * @return {Object} A JSON object with "type" and "value" properties.
 * @see {webdriver.WebDriver.prototype.executeScript}
 * @private
 */
webdriver.WebDriver.argumentToScriptArgument_ = function(arg) {
  if (arg instanceof webdriver.WebElement) {
    return new webdriver.WebDriver.ScriptArgument('ELEMENT', arg.getId());
  } else if (goog.isBoolean(arg) ||
             goog.isNumber(arg) ||
             goog.isString(arg)) {
    return new webdriver.WebDriver.ScriptArgument(
        goog.typeOf(arg).toUpperCase(), arg);
  } else {
    throw new Error('Invalid script argument type: ' + goog.typeOf(arg));
  }
};


/**
 * Adds a command to execute a JavaScript snippet in the window of the page
 * current under test.
 * @param {string} script The JavaScript snippet to execute.
 * @param {*} var_args The arguments to pass to the script.
 */
webdriver.WebDriver.prototype.executeScript = function(script, var_args) {
  var args = goog.array.map(
      goog.array.slice(arguments, 1),
      webdriver.WebDriver.argumentToScriptArgument_);
  var result = new webdriver.Future(this);
  this.addCommand(webdriver.CommandInfo.EXECUTE_SCRIPT.buildCommand(
      this, [script, args],
      goog.bind(function(response) {
        if (goog.isString(response.value) &&
            webdriver.WebElement.UUID_REGEX.test(response.value)) {
          var id = response.value;
          response.value = new webdriver.WebElement(this);
          response.value.getId().setValue(id);
        }
        result.setValue(response.value);
      }, this)));
  return result;
};


/**
 * Adds a command to fetch the given URL.
 * @param {goog.Uri|string} url The URL to fetch.
 */
webdriver.WebDriver.prototype.get = function(url) {
  this.addCommand(webdriver.CommandInfo.GET.buildCommand(
      this, [url.toString()], goog.bind(function(response) {
    this.context_ = response.context;
  }, this)));
};


webdriver.WebDriver.prototype.back = function() {
  this.addCommand(webdriver.CommandInfo.BACK.buildCommand(this));
};


webdriver.WebDriver.prototype.forward = function() {
  this.addCommand(webdriver.CommandInfo.FORWARD.buildCommand(this));
};


webdriver.WebDriver.prototype.refresh = function() {
  this.addCommand(webdriver.CommandInfo.REFRESH.buildCommand(this));
};


webdriver.WebDriver.prototype.getCurrentUrl = function() {
  return this.executeScript('return window.location.href');
};


/**
 * Adds a command to query for the current page title.
 * @return {webdriver.Future} 
 */
webdriver.WebDriver.prototype.getTitle = function() {
  var title = new webdriver.Future(this);
  this.addCommand(webdriver.CommandInfo.GET_TITLE.buildCommand(
      this, null, goog.bind(title.setValueFromResponse, title)));
  return title;
};


/**
 * Adds a command to find a single element on the current page.
 * @param {Object} by The strategy to use for finding the element.
 * @return {webdriver.WebElement} A WebElement wrapper that can be used to
 *     issue commands against the located element.
 */
webdriver.WebDriver.prototype.findElement = function(by) {
  return webdriver.WebElement.findElement(this, by);
};


/**
 * Adds a command to test if an element can be found on the page.
 * @param {Object} by The strategy to use for finding the element.
 * @return {webdriver.Future} Whether the element was present on the page. The
 *    return value is wrapped in a Future that will be defined when the driver
 *    completes the command.
 */
webdriver.WebDriver.prototype.isElementPresent = function(by) {
  return webdriver.WebElement.isElementPresent(this, by);
};



/**
 * Adds a command to find a multiple element on the current page.
 * @param {Object} by The strategy to use for finding the element.
 */
webdriver.WebDriver.prototype.findElements = function(by) {
  return webdriver.WebElement.findElements(this, by);
};


/**
 * Adds a comman to adjust the speed of the mouse.
 * @param {webdriver.WebDriver.Speed} speed The new speed setting.
 */
webdriver.WebDriver.prototype.setMouseSpeed = function(speed) {
  this.addCommand(
      webdriver.CommandInfo.SET_MOUSE_SPEED.buildCommand(this, [speed]));
};


/**
 * Adds a command to query for the current mouse speed.
 * @return {webdriver.Future} A Future whose value will be set by this driver
 *     when the query command completes.
 */
webdriver.WebDriver.prototype.getMouseSpeed = function() {
  var speed = new webdriver.Future(this);
  this.addCommand(
      webdriver.CommandInfo.GET_MOUSE_SPEED.buildCommand(this, null,
          function(response) {
            response.value = Number(response.value);
            speed.setValue(response.value);
          }));
  return speed;
};
