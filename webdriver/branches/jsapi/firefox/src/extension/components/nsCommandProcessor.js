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
 * @fileOverview Contains a Javascript implementation for
 *     nsICommandProcessor.idl. The implemented XPCOM component is exposed to
 *     the content page as a global property so that it can be used from
 *     unpriviledged code.
 */


/**
 * When this component is loaded, load the necessary subscripts.
 */
(function() {
  var scripts = [
    'json2.js',
    'session.js',
    'utils.js'
  ];

  var fileProtocolHandler = Components.
      classes['@mozilla.org/network/protocol;1?name=file'].
      createInstance(Components.interfaces.nsIFileProtocolHandler);
  var loader = Components.classes['@mozilla.org/moz/jssubscript-loader;1'].
      createInstance(Components.interfaces.mozIJSSubScriptLoader);

  for (var script in scripts) {
    var file = __LOCATION__.parent.clone();
    file.append(scripts[script]);

    var fileName = fileProtocolHandler.getURLSpecFromFile(file);
    loader.loadSubScript(fileName);
  }
})();


/**
 * Encapsulates the result of a command to the {@code nsCommandProcessor}.
 * @param {Object} command JSON object describing the command to execute.
 * @param {Session} opt_session The session that sent this command.
 * @constructor
 */
var Response = function(command, opt_session) {
  this.command = command;
  this.session = opt_session;
  this.statusBarLabel_ = null;
  this.callbackFn_ = command.callbackFn;
  this.json_ = {
    commandName: command ? command.commandName : 'Unknown command',
    isError: false,
    response: '',
    elementId: command.elementId
  };
};

Response.prototype = {

  /**
   * Updates the extension status label to indicate we are about to execute a
   * command.
   * @param {window} win The content window that the command will be executed on.
   */
  startCommand: function(win) {
    this.statusBarLabel_ = win.document.getElementById("fxdriver-label");
    if (this.statusBarLabel_) {
      this.statusBarLabel_.style.color = "red";
    }
  },

  /**
   * Sends the encapsulated response to the registered callback.
   */
  send: function() {
    // Indicate that we are no longer executing a command.
    if (this.statusBarLabel_) {
      this.statusBarLabel_.style.color = 'black';
    }
    if (this.callbackFn_) {
      this.callbackFn_(this.json_);
    }
  },

  /**
   * Helper function for reporting internal errors to the client.
   * @param {Error} ex The internal error to report to the client.
   */
  reportError: function(ex) {
    Utils.dump(ex);
    this.response = 'Internal error: ' + JSON.stringify({
      fileName : ex.fileName,
      lineNumber : ex.lineNumber,
      message : ex.message,
      name : ex.name
    });
    this.isError = true;
    this.send();
  },

  setField: function(name, value) { this.json_[name] = value; },
  set commandName(name) { this.json_.commandName = name; },
  get commandName()     { return this.json_.commandName; },
  set elementId(id)     { this.json_.elementId = id; },
  get elementId()       { return this.json_.elementId; },
  set isError(error)    { this.json_.isError = error; },
  get isError()         { return this.json_.isError; },
  set response(res)     { this.json_.response = res; },
  get response()        { return this.json_.response; }
};


/**
 * Handles executing a command from the {@code CommandProcessor} once the window
 * has fully loaded.
 * @param {FirefoxDriver} driver The FirefoxDriver instance to execute the
 *     command with.
 * @param {Object} command JSON object describing the command to execute.
 * @param {Response} response The response object to send the command response
 *     in.
 * @param {Number} opt_sleepDelay The amount of time to wait before attempting
 *     the command again if the window is not ready.
 * @constructor
 */
var DelayedCommand = function(driver, command, response, opt_sleepDelay) {
  this.driver_ = driver;
  this.command_ = command;
  this.response_ = response;
  this.onBlank_ = false;
  this.sleepDelay_ = opt_sleepDelay || DelayedCommand.DEFAULT_SLEEP_DELAY;

  this.loadGroup_ = driver.getBrowser().contentWindow.
      QueryInterface(Components.interfaces.nsIInterfaceRequestor).
      getInterface(Components.interfaces.nsIWebNavigation).
      QueryInterface(Components.interfaces.nsIInterfaceRequestor).
      getInterface(Components.interfaces.nsILoadGroup);
};


/**
 * Default amount of time, in milliseconds, to wait before (re)attempting a
 * {@code DelayedCommand}.
 * @type {Number}
 */
DelayedCommand.DEFAULT_SLEEP_DELAY = 100;


/**
 * Executes the command after the specified delay.
 * @param {Number} ms The delay in milliseconds.
 */
DelayedCommand.prototype.execute = function(ms) {
  var self = this;
  this.driver_.window.setTimeout(function() {
    self.executeInternal_();
  }, ms);
};


/**
 * Attempts to execute the command.  If the window is not ready for the command
 * to execute, will set a timeout to try again.
 * @private
 */
DelayedCommand.prototype.executeInternal_ = function() {
  if (this.loadGroup_.isPending()) {
    return this.execute(this.sleepDelay_);
  } else {
    // Ugh! New windows open on "about:blank" before going to their
    // destination URL. This check attempts to tell the difference between a
    // newly opened window and someone actually wanting to do something on
    // about:blank.
    if (this.driver_.window.location == 'about:blank' && !this.onBlank_) {
      this.onBlank_ = true;
      return this.execute(this.sleepDelay_);
    } else {
      try {
        this.response_.commandName = this.command_.commandName;
        this.driver_[this.command_.commandName](
            this.response_, this.command_.parameters);
      } catch (e) {
        // if (e instanceof StaleElementError) won't work here since
        // StaleElementError is defined in the utils.js subscript which is
        // loaded independently in this component and in the main driver
        // component.
        // TODO(jmleyba): Continue cleaning up the extension and replacing the
        // subscripts with proper components.
        if (e.isStaleElementError) {
          this.response_.isError = true;
          this.response_.response = 'element is obsolete';
          this.response_.send();
        } else {
          Utils.dumpn(
              'Exception caught by driver: ' + this.command_.commandName +
              '(' + this.command_.parameters + ')\n' + e);
          this.response_.reportError(e);
        }
      }
    }
  }
};


/**
 * Class for dispatching WebDriver requests.  Handles window locating commands
 * (e.g. switching, searching, etc.), all other commands are executed with the
 * {@code FirefoxDriver} through reflection.  Note this is a singleton class.
 * @constructor
 */
var nsCommandProcessor = function() {
  /**
   * A wrapped self reference for XPConnect.
   * @type {nsCommandProcessor}
   */
  this.wrappedJSObject = this;

  /**
   * A reference to the nsIWindowMediator service.
   * @type {nsIWindowMediator}
   */
  this.wm = Components.classes['@mozilla.org/appshell/window-mediator;1'].
      getService(Components.interfaces.nsIWindowMediator);

  /**
   * A map of active sessions where they keys are session UUIDs and the values
   * are {@code Session} objects.
   * @type {Object}
   * @private
   */
  this.sessionMap_ = {};
};


/**
 * Flags for the {@code nsIClassInfo} interface.
 * @type {Number}
 */
nsCommandProcessor.prototype.flags =
    Components.interfaces.nsIClassInfo.DOM_OBJECT;


/**
 * Implementaiton language detail for the {@code nsIClassInfo} interface.
 * @type {String}
 */
nsCommandProcessor.prototype.implementationLanguage =
    Components.interfaces.nsIProgrammingLanguage.JAVASCRIPT;


/**
 * Processes a command request for the {@code FirefoxDriver}.
 * @param {nsISupports} wrappedJsonCommand The command to
 *     execute, specified in a JSON object that is wrapped in a nsISupports
 *     instance.
 */
nsCommandProcessor.prototype.execute = function(wrappedJsonCommand) {
  var command = wrappedJsonCommand.wrappedJSObject;
  if (!command) {
    Utils.dumpn('wrappedJsonCommand must have a wrappedJSObject property!');
    throw Components.results.NS_ERROR_FAILURE;
  }

  var sessionId = command.sessionId;
  var response = new Response(command, this.sessionMap_[sessionId]);

  var sessions = [];
  for (var id in this.sessionMap_) {
    sessions.push(id);
  }

  if (!response.session &&
      command.commandName != 'findActiveDriver' &&
      command.commandName != 'quit') {
    response.isError = true;
    response.response =
        'Sessionless command! Sessions must be started with a ' +
        '"findActiveDriver" command';
    Utils.dumpn(response.response +
                '\nKnown sessions:\n' + sessions.join('\n'));
    return response.send();
  }

  // These are used to locate a new driver, and so not having one is a fine
  // thing to do
  if (command.commandName == 'findActiveDriver' ||
      command.commandName == 'switchToWindow' ||
      command.commandName == 'getAllWindowHandles' ||
      command.commandName == 'quit') {
    return this[command.commandName](response, command.parameters);
  }

  var driver = response.session.getDriver();
  if (!driver) {
    response.isError = true;
    response.response = 'Session has no driver: ' + response.session.getId();
    return response.send();
  }

  if (!response.session.window) {
    response.isError = true;
    response.response =
        'The current window for session ' + response.session.getId() +
        ' was closed or no longer exists!';
    return response.send();
  }

  if (typeof driver[command.commandName] != 'function') {
    response.isError = true;
    response.response = 'Unrecognised command: ' + command.commandName;
    return response.send();
  }

  response.startCommand(driver.window);
  new DelayedCommand(driver, command, response).execute(0);
};



/**
 * Changes the context of the caller to the window specified by the first
 * element of the {@code windowId} array.
 * @param {Response} response The response object to send the command response
 *     in.
 * @param {Array.<*>} windowId The parameters sent with the original command.
 *     The first element in the array must be the ID of the window to switch to.
 *     Note all other command parameters are ignored.
 * @param {boolean} opt_isSecondSearch Whether this is the second attempt to
 *     find the window.
 */
nsCommandProcessor.prototype.switchToWindow = function(response, windowId,
                                                       opt_isSecondSearch) {
  var lookFor = windowId[0];
  var matches = function(win, lookFor) {
    return !win.closed &&
           (win.content && win.content.name == lookFor) ||
           // fxdriver is the name of the property the FirefoxDriver is stored
           // in by the WebDriverServer.
           (win.top && win.top.fxdriver && win.top.fxdriver.id == lookFor);
  };

  var windowFound = this.searchWindows_('navigator:browser', function(win) {
    if (matches(win, lookFor)) {
      win.focus();
      if (win.top.fxdriver) {
        response.session.setDriver(win.top.fxdriver);
        response.response = response.session.getId();
      } else {
        response.isError = true;
        response.response = 'No driver found attached to top window!';
      }
      response.send();
      // Found the desired window, stop the search.
      return true;
    }
  });

  // It is possible that the window won't be found on the first attempt. This is
  // typically true for anchors with a target attribute set. This search could
  // execute before the target window has finished loaded, meaning the content
  // window won't have a name or FirefoxDriver instance yet (see matches above).
  // If we don't find the window, set a timeout to try one more time.
  if (!windowFound) {
    if (opt_isSecondSearch) {
      response.isError = true;
      response.response = 'Unable to locate window "' + lookFor + '"';
      response.send();
    } else {
      var self = this;
      this.wm.getMostRecentWindow('navigator:browser').
          setTimeout(function() {
            self.switchToWindow(response, windowId, true);
          }, 500);
    }
  }
};


/**
 * Retrieves a list of all known FirefoxDriver windows.
 * @param {Response} response The response object to send the command response
 *     in.
 */
nsCommandProcessor.prototype.getAllWindowHandles = function(response) {
  var res = [];
  this.searchWindows_('navigator:browser', function(win) {
    if (win.top && win.top.fxdriver) {
      res.push(win.top.fxdriver.id);
    } else if (win.content) {
      res.push(win.content.name);
    } else {
      res.push('');
    }
  });
  response.response = res.join(',');
  response.send();
};


/**
 * Searches over a selection of windows, calling a visitor function on each
 * window found in the search.
 * @param {?string} search_criteria The category of windows to search or
 *     {@code null} to search all windows.
 * @param {function} visitor_fn A visitor function to call with each window. The
 *     function may return true to indicate that the window search should abort
 *     early.
 * @return {boolean} Whether the visitor function short circuited the search.
 */
nsCommandProcessor.prototype.searchWindows_ = function(search_criteria,
                                                       visitor_fn) {
  var allWindows = this.wm.getEnumerator(search_criteria);
  while (allWindows.hasMoreElements()) {
    var win = allWindows.getNext();
    if (visitor_fn(win)) {
      return true;
    }
  }
  return false;
};


/**
 * Locates the most recently used FirefoxDriver window.
 *
 * <p>If this request is not yet associated with a session, a new session will
 * be started.
 *
 * @param {Response} response The response object to send the command response
 *     in.
 */
nsCommandProcessor.prototype.findActiveDriver = function(response) {
  var win = this.wm.getMostRecentWindow("navigator:browser");
  var driver = win.fxdriver;
  if (!driver) {
    response.isError = true;
    response.response = 'No drivers associated with the window';
  } else {
    if (!response.session) {
      response.session = new Session(driver);
      this.sessionMap_[response.session.getId()] = response.session;
    } else {
      response.session.setDriver(driver);
    }
    response.response = response.session.getId();
  }
  response.send();
};


/**
 * Forcefully shuts down the Firefox application.
 */
nsCommandProcessor.prototype.quit = function() {
  Components.classes['@mozilla.org/toolkit/app-startup;1'].
      getService(Components.interfaces.nsIAppStartup).
      quit(Components.interfaces.nsIAppStartup.eForceQuit);
};


/**
 * @see nsIClassInfo.getInterfaces()
 */
nsCommandProcessor.prototype.getInterfaces = function(count) {
  var ifaces = [
    Components.interfaces.nsICommandProcessor,
    Components.interfaces.nsIClassInfo,
    Components.interfaces.nsISupports
  ];
  count.value = ifaces.length;
  return ifaces;
};


/**
 * @see nsIClassInfo.getHelperForLanguage()
 */
nsCommandProcessor.prototype.getHelperForLanguage = function() {
  return null;
};


/**
 * @see nsISupports.QueryInterface()
 */
nsCommandProcessor.prototype.QueryInterface = function (aIID) {
  if (!aIID.equals(Components.interfaces.nsICommandProcessor) &&
      !aIID.equals(Components.interfaces.nsIClassInfo) &&
      !aIID.equals(Components.interfaces.nsISupports)) {
    throw Components.results.NS_ERROR_NO_INTERFACE;
  }
  return this;
};


nsCommandProcessor.CLASS_ID =
    Components.ID('{692e5117-a4a2-4b00-99f7-0685285b4db5}');
nsCommandProcessor.CLASS_NAME = 'Firefox WebDriver CommandProcessor';
nsCommandProcessor.CONTRACT_ID = '@googlecode.com/webdriver/command-processor;1';
nsCommandProcessor.EXPOSED_NAME = '__webDriverCommandProcessor';


/**
 * Factory object for obtaining a reference to the singleton instance of
 * {@code CommandProcessor}.
 */
nsCommandProcessor.Factory = {
  instance_ : null,

  createInstance: function(aOuter, aIID) {
    if (aOuter != null) {
      throw Components.results.NS_ERROR_NO_AGGREGATION;
    }
    if (!this.instance_) {
      this.instance_ = new nsCommandProcessor();
    }
    return this.instance_.QueryInterface(aIID);
  }
};


/**
 * Module definition for registering this XPCOM component.
 */
nsCommandProcessor.Module = {
  firstTime_: true,

  registerSelf: function(aCompMgr, aFileSpec, aLocation, aType) {
    if (this.firstTime_) {
      this.firstTime_ = false;
      throw Components.results.NS_ERROR_FACTORY_REGISTER_AGAIN;
    }
    aCompMgr.QueryInterface(Components.interfaces.nsIComponentRegistrar).
        registerFactoryLocation(
            nsCommandProcessor.CLASS_ID,
            nsCommandProcessor.CLASS_NAME,
            nsCommandProcessor.CONTRACT_ID,
            aFileSpec, aLocation, aType);

    Components.classes['@mozilla.org/categorymanager;1']
        .getService(Components.interfaces.nsICategoryManager)
        .addCategoryEntry(
            'JavaScript global property',
            nsCommandProcessor.EXPOSED_NAME,
            nsCommandProcessor.CONTRACT_ID,
            true,   // Persist this entry
            true);  // Replace existing entry
  },

  unregisterSelf: function(aCompMgr, aLocation) {
    aCompMgr.QueryInterface(Components.interfaces.nsIComponentRegistrar).
        unregisterFactoryLocation(nsCommandProcessor.CLASS_ID, aLocation);
    Components.classes['@mozilla.org/categorymanager;1'].
        getService(Components.interfaces.nsICategoryManager).
        deleteCategoryEntry(
            'JavaScript global property',
            nsCommandProcessor.EXPOSED_NAME,
            nsCommandProcessor.CONTRACT_ID,
            true);
  },

  getClassObject: function(aCompMgr, aCID, aIID) {
    if (!aIID.equals(Components.interfaces.nsIFactory)) {
      throw Components.results.NS_ERROR_NOT_IMPLEMENTED;
    } else if (!aCID.equals(nsCommandProcessor.CLASS_ID)) {
      throw Components.results.NS_ERROR_NO_INTERFACE;
    }
    return nsCommandProcessor.Factory;
  },

  canUnload: function() {
    return true;
  }
};


/**
 * Module initialization.
 */
function NSGetModule() {
  return nsCommandProcessor.Module;
}
