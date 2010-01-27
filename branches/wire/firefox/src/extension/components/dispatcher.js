/*
 Copyright 2007-2010 WebDriver committers
 Copyright 2007-2010 Google Inc.
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
 * Dispatches commands received by the WebDriver server.
 * @constructor
 */
function Dispatcher() {
  this.resources_ = [];
  this.init_();
}


/**
 * Utility function used to respond to a command that is recognised, but not
 * implemented. Returns a 501.
 * @param {Request} The request to respond to.
 * @param {Response} Class used to send the response.
 */
Dispatcher.notImplemented = function(request, response) {
  response.sendError(Response.NOT_IMPLEMENTED, 'Unsupported command',
      'text/plain');
};


/**
 * Returns a function that translates a WebDriver HTTP request to a legacy
 * nsICommandProcessor.
 * @param {string} name The legacy command name.
 * @return {function(Request, Response)} The translation function.
 * @private
 */
Dispatcher.translateTo = function(name) {
  return function(request, response) {
    var json = {
      'name': name,
      'sessionId': {
        'value': request.getAttribute('sessionId') 
      },
      'parameters': JSON.parse(request.getBody() || '{}')
    };

    // All request attributes, excluding sessionId and parameters also passed
    // the body payload, should be added to the parameters.
    var attributeNames = request.getAttributeNames();
    for (var attrName; attrName = attributeNames.shift();) {
      if (attrName != 'sessionId' && !json['parameters'][attrName]) {
        json['parameters'][attrName] = request.getAttribute(attrName);
      }
    }

    var jsonString = JSON.stringify(json);
    var callback = function(jsonResponseString) {
      var jsonResponse = JSON.parse(jsonResponseString);
      // Going to need more granularity here I think.
      if (jsonResponse.status != 0) {
        response.setStatus(Response.INTERNAL_ERROR);
      }

      response.setContentType('application/json');
      response.setBody(jsonResponseString);
      response.commit();
    };

    // Dispatch the command.
    Components.classes['@googlecode.com/webdriver/command-processor;1'].
        getService(Components.interfaces.nsICommandProcessor).
        execute(jsonString, callback);
  };
};


/**
 * Creates a special handler for translating a request for a new session to a
 * request understood by the legacy nsICommandProcessor.
 */
Dispatcher.translateNewSession = function() {
  return function(request, response) {
    var callback = function(jsonResponseString) {
      var jsonResponse = JSON.parse(jsonResponseString);
      // Going to need more granularity here I think.
      if (jsonResponse.status != 0) {
        response.sendError(Response.INTERNAL_ERROR,
            jsonResponseString, 'application/json');
      } else {
        var url = request.getRequestUrl();
        response.setStatus(Response.SEE_OTHER);
        response.setHeader('Location',
            url.scheme + '://' + url.hostPort + url.path + '/' +
                jsonResponse.value);
        response.commit();
      }
    };

    // Dispatch the command.
    Components.classes['@googlecode.com/webdriver/command-processor;1'].
        getService(Components.interfaces.nsICommandProcessor).
        execute('{"name":"newSession"}', callback);
  };
};


/**
 * Initializes the command bindings for this dispatcher.
 * @private
 */
Dispatcher.prototype.init_ = function() {
  this.bind_('/config/drivers').  // Recognised, but not supported.
      on(Request.Method.POST, Dispatcher.notImplemented);

  this.bind_('/session').
      on(Request.Method.POST, Dispatcher.translateNewSession());

  this.bind_('/session/:sessionId').
      on(Request.Method.GET, Dispatcher.translateTo('getSessionCapabilities')).
      on(Request.Method.DELETE, Dispatcher.translateTo('quit'));

  this.bind_('/session/:sessionId/window_handle').
      on(Request.Method.GET, Dispatcher.translateTo('getCurrentWindowHandle'));
  this.bind_('/session/:sessionId/window_handles').
      on(Request.Method.GET, Dispatcher.translateTo('getWindowHandles'));

  this.bind_('/session/:sessionId/speed').
      on(Request.Method.GET, Dispatcher.translateTo('getSpeed')).
      on(Request.Method.POST, Dispatcher.translateTo('setSpeed'));

  this.bind_('/session/:sessionId/url').
      on(Request.Method.GET, Dispatcher.translateTo('getCurrentUrl')).
      on(Request.Method.POST, Dispatcher.translateTo('get'));

  this.bind_('/session/:sessionId/forward').
      on(Request.Method.POST, Dispatcher.translateTo('goForward'));
  this.bind_('/session/:sessionId/back').
      on(Request.Method.POST, Dispatcher.translateTo('goBack'));
  this.bind_('/session/:sessionId/refresh').
      on(Request.Method.POST, Dispatcher.translateTo('refresh'));

  this.bind_('/session/:sessionId/execute').
      on(Request.Method.POST, Dispatcher.translateTo('executeScript'));

  this.bind_('/session/:sessionId/source').
      on(Request.Method.GET, Dispatcher.translateTo('getPageSource'));
  this.bind_('/session/:sessionId/title').
      on(Request.Method.GET, Dispatcher.translateTo('getTitle'));

  this.bind_('/session/:sessionId/element').
      on(Request.Method.POST, Dispatcher.translateTo('findElement'));
  this.bind_('/session/:sessionId/elements').
      on(Request.Method.POST, Dispatcher.translateTo('findElements'));
  this.bind_('/session/:sessionId/element/active').
      on(Request.Method.POST, Dispatcher.translateTo('getActiveElement'));

  this.bind_('/session/:sessionId/element/:id').
      // TODO: implement
      on(Request.Method.GET, Dispatcher.notImplemented);

  // TODO: drop redundant :using attribute
  this.bind_('/session/:sessionId/element/:id/element/:using').
      on(Request.Method.POST, Dispatcher.translateTo('findChildElement'));
  this.bind_('/session/:sessionId/element/:id/elements/:using').
      on(Request.Method.POST, Dispatcher.translateTo('findChildElements'));

  this.bind_('/session/:sessionId/element/:id/click').
      on(Request.Method.POST, Dispatcher.translateTo('clickElement'));
  this.bind_('/session/:sessionId/element/:id/text').
      on(Request.Method.GET, Dispatcher.translateTo('getElementText'));
  this.bind_('/session/:sessionId/element/:id/submit').
      on(Request.Method.POST, Dispatcher.translateTo('submitElement'));

  this.bind_('/session/:sessionId/element/:id/value').
      on(Request.Method.POST, Dispatcher.translateTo('sendKeysToElement')).
      on(Request.Method.GET, Dispatcher.translateTo('getElementValue'));

  this.bind_('/session/:sessionId/element/:id/name').
      on(Request.Method.GET, Dispatcher.translateTo('getElementTagName'));

  this.bind_('/session/:sessionId/element/:id/clear').
      on(Request.Method.POST, Dispatcher.translateTo('clearElement'));

  this.bind_('/session/:sessionId/element/:id/selected').
      on(Request.Method.GET, Dispatcher.translateTo('isElementSelected')).
      on(Request.Method.POST, Dispatcher.translateTo('setElementSelected'));

  this.bind_('/session/:sessionId/element/:id/enabled').
      on(Request.Method.GET, Dispatcher.translateTo('isElementEnabled'));
  this.bind_('/session/:sessionId/element/:id/displayed').
      on(Request.Method.GET, Dispatcher.translateTo('isElementDisplayed'));

  this.bind_('/session/:sessionId/element/:id/location').
      on(Request.Method.GET, Dispatcher.translateTo('getElementLocation'));
  this.bind_('/session/:sessionId/element/:id/location_in_view').
      on(Request.Method.GET, Dispatcher.translateTo(
          'getElementLocationOnceScrolledIntoView'));

  this.bind_('/session/:sessionId/element/:id/size').
      on(Request.Method.GET, Dispatcher.translateTo('getElementSize'));

  this.bind_('/session/:sessionId/element/:id/css/:propertyName').
      on(Request.Method.GET,
         Dispatcher.translateTo('getElementValueOfCssProperty'));
  this.bind_('/session/:sessionId/element/:id/attribute/:name').
      on(Request.Method.GET, Dispatcher.translateTo('getElementAttribute'));
  // TODO: implement
  this.bind_('/session/:sessionId/element/:id/equals/:other').
      on(Request.Method.GET, Dispatcher.notImplemented);

  this.bind_('/session/:sessionId/element/:id/toggle').
      on(Request.Method.POST, Dispatcher.translateTo('toggleElement'));
  this.bind_('/session/:sessionId/element/:id/hover').
      on(Request.Method.POST, Dispatcher.translateTo('hoverOverElement'));
  this.bind_('/session/:sessionId/element/:id/drag').
      on(Request.Method.POST, Dispatcher.translateTo('dragElement'));

  this.bind_('/session/:sessionId/cookie').
      on(Request.Method.GET, Dispatcher.translateTo('getCookies')).
      on(Request.Method.POST, Dispatcher.translateTo('addCookie')).
      on(Request.Method.DELETE, Dispatcher.translateTo('deleteAllCookies'));

  this.bind_('/session/:sessionId/cookie/:name').
      on(Request.Method.DELETE, Dispatcher.translateTo('deleteCookie'));

  this.bind_('/session/:sessionId/frame').
      on(Request.Method.POST, Dispatcher.translateTo('switchToFrame'));
  this.bind_('/session/:sessionId/frame/:id').  // TODO: zap
      on(Request.Method.POST, Dispatcher.translateTo('switchToFrame'));
  this.bind_('/session/:sessionId/window/:name').  // TODO: zap
      on(Request.Method.POST, Dispatcher.translateTo('switchToWindow'));
  this.bind_('/session/:sessionId/window').  // TODO: zap
      on(Request.Method.DELETE, Dispatcher.translateTo('close'));

  this.bind_('/session/:sessionId/screenshot').
      on(Request.Method.GET, Dispatcher.translateTo('screenshot'));
};


/**
 * Binds a resource to the given path.
 * @param {string} path The resource path.
 * @return {Resource} The bound resource.
 */
Dispatcher.prototype.bind_ = function(path) {
  var resource = new Resource(path);
  this.resources_.push(resource);
  return resource;
};



/**
 * Dispatches a request to the appropriately registered handler.
 * @param {Request} request The request to dispatch.
 * @param {Response} response The request response.
 */
Dispatcher.prototype.dispatch = function(request, response) {
  // We only support one servlet, mapped to /hub/*
  // TODO: be more flexible.
  var path = request.getRequestUrl().path;
  if (path.indexOf('/hub') != 0) {
    response.sendError(Response.NOT_FOUND);
    return;
  }
  request.setServletPath('/hub');
  path = request.getPathInfo();

  var bestMatchResource;
  for (var i = 0; i < this.resources_.length; i++) {
    if (this.resources_[i].isResourceFor(path)) {
      if (!bestMatchResource ||
          bestMatchResource.getNumVariablePathSegments() <
          this.resources_[i].getNumVariablePathSegments()) {
        bestMatchResource = this.resources_[i];
      }
    }
  }

  if (bestMatchResource) {
    try {
      bestMatchResource.setRequestAttributes(request);
      bestMatchResource.handle(request, response);
    } catch (ex) {
      Utils.dump(ex);
      response.sendError(Response.INTERNAL_ERROR, JSON.stringify({
        status: ErrorCode.UNHANDLED_ERROR,
        value: ErrorCode.toJSON(ex)
      }), 'application/json');
    }
  } else {
    response.sendError(Response.NOT_FOUND,
        'Unrecognized command: ' + request.getMethod() + ' ' +
            request.getPathInfo(),
        'text/plain');
  }
};


/**
 * Defines a resource in the WebDriver REST service locatable at the given path.
 * Any path segments prefixed with a ":" indicate that segment is a variable
 * unique to a resource. For example, in the path "/session/:sessionId",
 * ":sessionId" is a variable that can be changed to specify different sessions.
 * @param {!string} path The path that this resource is accessible from.
 */
function Resource(path) {

  /**
   * The request pattern that this resource is located at.
   * @type {!string}
   * @const
   * @private
   */
  this.path_ = path;

  /**
   * The individual path segments for this resource.
   * @type {Array.<string>}
   * @const
   * @private
   */
  this.pathSegments_ = path.split('/');

  /**
   * A map of handler functions, by HTTP method, that can service requests to
   * this resource.
   * @type {!Object}
   * @const
   * @private
   */
  this.handlers_ = {};

  for (var i = 0; i < this.pathSegments_.length; i++) {
    if (this.pathSegments_[i].indexOf(Resource.VARIABLE_PATH_SEGMENT_PREFIX_)) {
      this.numVariablePathSegments_ += 1;
    }
  }
};


/**
 * The number of path segments for this resource that are variables.
 * @type {number}
 * @private
 */
Resource.prototype.numVariablePathSegments_ = 0;


/** @return {string} The path mapped to this resource. */
Resource.prototype.getPath = function() {
  return this.path_;
};


/** @return {number} The number of variable path segments for this resource. */
Resource.prototype.getNumVariablePathSegments = function() {
  return this.numVariablePathSegments_;
};


/**
 * Sets the handler function for this resource when a request is received using
 * the given HTTP method. This function will override any previously set
 * handlers.
 * @param {!Request.Method} httpMethod The request method the function can
 *     handle.
 * @param {function(Request, Response)} handlerFn The function that will handle
 *     all requests for this resource using the given HTTP method.
 * @return {!Resource} A self reference for chained calls.
 */
Resource.prototype.on = function(httpMethod, handlerFn) {
  this.handlers_[httpMethod] = handlerFn;
  return this;
};


/**
 * Determines if this is the resource for the given path.
 * @param {!string} path The resource path to test.
 * @return {boolean} Whether this resource is mapped to the given path.
 */
Resource.prototype.isResourceFor = function(path) {
  var allParts = path.split('/');
  if (this.pathSegments_.length != allParts.length) {
    return false;
  }
  for (var i = 0; i < this.pathSegments_.length; i++) {
    if (this.pathSegments_[i] != allParts[i] &&
        !/^:/.test(this.pathSegments_[i])) {
      return false;
    }
  }
  return true;
};


/**
 * Sets request attributes by the named path variables for this resource. For
 * each named path segment variable for this resource, the value of the
 * corresponding path segment in the request will be stored as the request
 * attribute's value.
 * @param {Request} request The request to update.
 */
Resource.prototype.setRequestAttributes = function(request) {
  var allParts = request.getPathInfo().split('/');
  for (var i = 0; i < this.pathSegments_.length; i++) {
    if (/^:/.test(this.pathSegments_[i])) {
      var decodedValue = decodeURIComponent(allParts[i]);
      request.setAttribute(
          this.pathSegments_[i].replace(/^:/, ''), decodedValue);
    }
  }
};


/**
 * Handles a request to this resource. Behavior is undefined if the request does
 * not map to this resource.
 * @param {Request} request The request to handle.
 * @param {Response} response For sending the response.
 */
Resource.prototype.handle = function(request, response) {
  var handlerFn = this.handlers_[request.getMethod()];
  if (handlerFn) {
    handlerFn(request, response);
  } else {
    response.sendError(Response.METHOD_NOT_ALLOWED,
        'Method "' + request.getMethod() + '" not allowed for command ' +
        '"' + this.path_ + '"');
  }
};
