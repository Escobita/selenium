// Copyright 2010 WebDriver committers
// Copyright 2010 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @fileoverview Browser atom for injecting JavaScript into the page under
 * test. There is no point in using this atom directly from JavaScript.
 * Instead, it is intended to be used in its compiled form when injecting
 * script from another language (e.g. C++).
 *
 * TODO(user): Add an example
 *
 */

goog.provide('bot.inject');
goog.provide('bot.inject.cache');

goog.require('bot.Error');
goog.require('bot.ErrorCode');
goog.require('goog.array');
goog.require('goog.dom');
goog.require('goog.dom.NodeType');
goog.require('goog.events');
goog.require('goog.json');
goog.require('goog.object');



/**
 * WebDriver wire protocol definition of a command response.
 * @typedef {{status:bot.ErrorCode, value:*}}
 * @see http://code.google.com/p/selenium/wiki/JsonWireProtocol#Responses
 */
bot.inject.Response;


/**
 * Key used to identify DOM elements in the WebDriver wire protocol.
 * @type {string}
 * @const
 * @see http://code.google.com/p/selenium/wiki/JsonWireProtocol
 */
bot.inject.ELEMENT_KEY = 'ELEMENT';


/**
 * Converts an element to a JSON friendly value so that it can be
 * stringified for transmission to the injector. Values are modified as
 * follows:
 * <ul>
 * <li>booleans, numbers, strings, and null are returned as is</li>
 * <li>undefined values are returned as null</li>
 * <li>functions are returned as a string</li>
 * <li>each element in an array is recursively processed</li>
 * <li>DOM Elements are wrapped in object-literals as dictated by the
 *     WebDriver wire protocol</li>
 * <li>all other objects will be treated as hash-maps, and will be
 *     recursively processed for any string and number key types (all
 *     other key types are discarded as they cannot be converted to JSON).
 * </ul>
 *
 * @param {*} value The value to make JSON friendly.
 * @return {*} The JSON friendly value.
 * @see http://code.google.com/p/selenium/wiki/JsonWireProtocol
 */
bot.inject.wrapValue = function(value) {
  switch (goog.typeOf(value)) {
    case 'string':
    case 'number':
    case 'boolean':
      return value;

    case 'function':
      return value.toString();

    case 'array':
      return goog.array.map((/**@type {goog.array.ArrayLike}*/value),
          bot.inject.wrapValue);

    case 'object':
      // Since {*} expands to {Object|boolean|number|string|undefined}, the
      // JSCompiler complains that it is too broad a type for the remainder of
      // this block where {!Object} is expected. Downcast to prevent generating
      // a ton of compiler warnings.
      value = (/**@type {!Object}*/value);

      // Sniff out DOM elements. We're using duck-typing instead of an
      // instanceof check since the instanceof might not always work
      // (e.g. if the value originated from another Firefox component)
      if (goog.object.containsKey(value, 'nodeType') &&
          (value['nodeType'] == goog.dom.NodeType.ELEMENT
           || value['nodeType'] == goog.dom.NodeType.DOCUMENT)) {
        var ret = {};
        ret[bot.inject.ELEMENT_KEY] =
            bot.inject.cache.addElement((/**@type {!Element}*/value));
        return ret;
      }

      if (goog.isArrayLike(value)) {
        return goog.array.map((/**@type {goog.array.ArrayLike}*/value),
            bot.inject.wrapValue);
      }

      var filtered = goog.object.filter(value, function(val, key) {
        return goog.isNumber(key) || goog.isString(key);
      });
      return goog.object.map(filtered, bot.inject.wrapValue);

    default:  // goog.typeOf(value) == 'undefined' || 'null'
      return null;
  }
};


/**
 * Unwraps any DOM element's encoded in the given {@code value}.
 * @param {*} value The value to unwrap.
 * @param {Document} opt_doc The document whose cache to retrieve wrapped
 *     elements from. Defaults to the current document.
 * @return {*} The unwrapped value.
 * @private
 */
bot.inject.unwrapValue_ = function(value, opt_doc) {
  if (goog.isArray(value)) {
    return goog.array.map((/**@type {goog.array.ArrayLike}*/value),
        function(v) { return bot.inject.unwrapValue_(v, opt_doc); });
  } else if (goog.isObject(value)) {
    return goog.object.containsKey(value, bot.inject.ELEMENT_KEY) ?
        bot.inject.cache.getElement(value[bot.inject.ELEMENT_KEY], opt_doc) :
        goog.object.map(value, function(val) {
          return bot.inject.unwrapValue_(val, opt_doc);
        });
  }
  return value;
};


/**
 * Recompiles {@code fn} in the context of another window so that the
 * correct symbol table is used when the function is executed. This
 * function assumes the {@code fn} can be decompiled to its source using
 * {@code Function.prototype.toString} and that it only refers to symbols
 * defined in the target window's context.
 *
 * @param {!(Function|string)} fn Either the function that shold be
 *     recompiled, or a string defining the body of an anonymous function
 *     that should be compiled in the target window's context.
 * @param {!Window} theWindow The window to recompile the function in.
 * @return {!Function} The recompiled function.
 * @private
 */
bot.inject.recompileFunction_ = function(fn, theWindow) {
  if (goog.isString(fn)) {
    return new theWindow.Function(fn);
  }
  return theWindow == window ? fn : new theWindow.Function(
      'return (' + fn + ').apply(null,arguments);');
};


/**
 * Executes an injected script. This function should never be called from
 * within JavaScript itself. Instead, it is used from an external source that
 * is injecting a script for execution.
 *
 * <p/>For example, in a WebDriver Java test, one might have:
 * <pre><code>
 * Object result = ((JavascriptExecutor) driver).executeScript(
 *     "return arguments[0] + arguments[1];", 1, 2);
 * </code></pre>
 *
 * <p/>Once transmitted to the driver, this command would be injected into the
 * page for evaluation as:
 * <pre><code>
 * bot.inject.executeScript(
 *     function() {return arguments[0] + arguments[1];},
 *     [1, 2]);
 * </code></pre>
 *
 * <p/>The details of how this actually gets injected for evaluation is left
 * as an implementation detail for clients of this library.
 *
 * @param {!(Function|string)} fn Either the function to execute, or a string
 *     defining the body of an anonymous function that should be executed. This
 *     function should only contain references to symbols defined in the context
 *     of the current window.
 * @param {Array.<*>} args An array of wrapped script arguments, as defined by
 *     the WebDriver wire protocol.
 * @param {boolean=} opt_stringify Whether the result should be returned as a
 *     serialized JSON string.
 * @param {!Window=} opt_window The window in whose context the function should
 *     be invoked; defaults to the current window.
 * @return {!(string|bot.inject.Response)} The response object. If
 *     opt_stringify is true, the result will be serialized and returned in
 *     string format.
 */
bot.inject.executeScript = function(fn, args, opt_stringify, opt_window) {
  var win = opt_window || window;
  var ret;
  try {
    fn = bot.inject.recompileFunction_(fn, win);
    var unwrappedArgs = (/**@type {Object}*/bot.inject.unwrapValue_(args,
        win.document));
    ret = bot.inject.wrapResponse_(fn.apply(null, unwrappedArgs));
  } catch (ex) {
    ret = bot.inject.wrapError_(ex);
  }
  return opt_stringify ? goog.json.serialize(ret) : ret;
};


/**
 * Executes an injected script, which is expected to finish asynchronously
 * before the given {@code timeout}. When the script finishes or an error
 * occurs, the given {@code onDone} callback will be invoked. This callback
 * will have a single argument, a {@code bot.inject.Response} object.
 *
 * The script signals its completion by invoking a supplied callback given
 * as its last argument. The callback may be invoked with a single value.
 *
 * The script timeout event will be scheduled with the provided window,
 * ensuring the timeout is synchronized with that window's event queue.
 * Furthermore, asynchronous scripts do not work across new page loads; if an
 * "unload" event is fired on the window while an asynchronous script is
 * pending, the script will be aborted and an error will be returned.
 *
 * Like {@code bot.inject.executeScript}, this function should only be called
 * from an external source. It handles wrapping and unwrapping of input/output
 * values.
 *
 * @param {(function()|string)} fn Either the function to execute, or a string
 *     defining the body of an anonymous function that should be executed.
 * @param {Array.<*>} args An array of wrapped script arguments, as defined by
 *     the WebDriver wire protocol.
 * @param {number} timeout The amount of time, in milliseconds, the script
 *     should be permitted to run; must be non-negative.
 * @param {function(string|bot.inject.Response)} onDone
 *     The function to call when the given {@code fn} invokes its callback,
 *     or when an exception or timeout occurs. This will always be called.
 * @param {boolean=} opt_stringify Whether the result should be returned as a
 *     serialized JSON string.
 * @param {!Window=} opt_window The window to synchronize the script with;
 *     defaults to the current window
 */
bot.inject.executeAsyncScript = function(fn, args, timeout, onDone,
                                         opt_stringify, opt_window) {
  var win = opt_window || window;
  var timeoutId, onunloadKey;
  var responseSent = false;

  function sendResponse(status, value) {
    if (!responseSent) {
      goog.events.unlistenByKey(onunloadKey);
      win.clearTimeout(timeoutId);
      if (status != bot.ErrorCode.SUCCESS) {
        var err = new bot.Error(status, value.message || value + '');
        err.stack = value.stack;
        value = bot.inject.wrapError_(err);
      } else {
        value = bot.inject.wrapResponse_(value);
      }
      onDone(opt_stringify ? goog.json.serialize(value) : value);
      responseSent = true;
    }
  }
  var sendError = goog.partial(sendResponse, bot.ErrorCode.UNKNOWN_ERROR);

  if (win.closed) {
    return sendError('Unable to execute script; the target window is closed.');
  }

  fn = bot.inject.recompileFunction_(fn, win);

  args = bot.inject.unwrapValue_(args, win.document);
  args.push(goog.partial(sendResponse, bot.ErrorCode.SUCCESS));

  onunloadKey = goog.events.listen(win, goog.events.EventType.UNLOAD,
      function() {
        sendResponse(bot.ErrorCode.UNKNOWN_ERROR,
            Error('Detected a page unload event; asynchronous script ' +
                  'execution does not work across page loads.'));
      }, true);

  var startTime = goog.now();
  try {
    fn.apply(win, args);

    // Register our timeout *after* the function has been invoked. This will
    // ensure we don't timeout on a function that invokes its callback after
    // a 0-based timeout.
    timeoutId = win.setTimeout(function() {
      sendResponse(bot.ErrorCode.SCRIPT_TIMEOUT,
                   Error('Timed out waiting for asyncrhonous script result ' +
                         'after ' + (goog.now() - startTime) + ' ms'));
    }, Math.max(0, timeout));
  } catch (ex) {
    sendResponse(ex.code || bot.ErrorCode.UNKNOWN_ERROR, ex);
  }
};


/**
 * Wraps the response to an injected script that executed successfully so it
 * can be JSON-ified for transmission to the process that injected this
 * script.
 * @param {*} value The script result.
 * @return {{status:bot.ErrorCode,value:*}} The wrapped value.
 * @see http://code.google.com/p/selenium/wiki/JsonWireProtocol#Responses
 * @private
 */
bot.inject.wrapResponse_ = function(value) {
  return {
    'status': bot.ErrorCode.SUCCESS,
    'value': bot.inject.wrapValue(value)
  };
};


/**
 * Wraps a JavaScript error in an object-literal so that it can be JSON-ified
 * for transmission to the process that injected this script.
 * @param {Error} err The error to wrap.
 * @return {{status:bot.ErrorCode,value:*}} The wrapped error object.
 * @see http://code.google.com/p/selenium/wiki/JsonWireProtocol#Failed_Commands
 * @private
 */
bot.inject.wrapError_ = function(err) {
  // TODO(user): Parse stackTrace
  return {
    'status': goog.object.containsKey(err, 'code') ?
        err['code'] : bot.ErrorCode.UNKNOWN_ERROR,
    // TODO(user): Parse stackTrace
    'value': {
      'message': err.message
    }
  };
};


/**
 * The property key used to store the element cache on the DOCUMENT node
 * when it is injected into the page. Since compiling each browser atom results
 * in a different symbol table, we must use this known key to access the cache.
 * This ensures the same object is used between injections of different atoms.
 * @type {string}
 * @const
 * @private
 */
bot.inject.cache.CACHE_KEY_ = '$wdc_';


/**
 * The prefix for each key stored in an cache.
 * @type {string}
 * @const
 */
bot.inject.cache.ELEMENT_KEY_PREFIX = ':wdc:';


/**
 * Retrieves the cache object for the given window. Will initialize the cache
 * if it does not yet exist.
 * @param {Document} opt_doc The document whose cache to retrieve. Defaults to
 *     the current document.
 * @return {Object.<string, Element>} The cache object.
 * @private
 */
bot.inject.cache.getCache_ = function(opt_doc) {
  var doc = opt_doc || document;
  var cache = doc[bot.inject.cache.CACHE_KEY_];
  if (!cache) {
    cache = doc[bot.inject.cache.CACHE_KEY_] = {};
    // Store the counter used for generated IDs in the cache so that it gets
    // reset whenever the cache does.
    cache.nextId = goog.now();
  }
  // Sometimes the nextId does not get initialized and returns NaN
  // TODO: Generate UID on the fly instead.
  if (!cache.nextId) {
    cache.nextId = goog.now();
  }
  return cache;
};


/**
 * Adds an element to its ownerDocument's cache.
 * @param {Element} el The element to add.
 * @return {string} The key generated for the cached element.
 */
bot.inject.cache.addElement = function(el) {
  // Check if the element already exists in the cache.
  var cache = bot.inject.cache.getCache_(el.ownerDocument);
  var id = goog.object.findKey(cache, function(value) {
    return value == el;
  });
  if (!id) {
    id = bot.inject.cache.ELEMENT_KEY_PREFIX + cache.nextId++;
    cache[id] = el;
  }
  return id;
};


/**
 * Retrieves an element from the cache. Will verify that the element is
 * still attached to the DOM before returning.
 * @param {string} key The element's key in the cache.
 * @param {Document} opt_doc The document whose cache to retrieve the element
 *     from. Defaults to the current document.
 * @return {Element} The cached element.
 */
bot.inject.cache.getElement = function(key, opt_doc) {
  key = decodeURIComponent(key);
  var doc = opt_doc || document;
  var cache = bot.inject.cache.getCache_(doc);
  if (!goog.object.containsKey(cache, key)) {
    // Throw STALE_ELEMENT_REFERENCE instead of NO_SUCH_ELEMENT since the
    // key may have been defined by a prior document's cache.
    throw new bot.Error(bot.ErrorCode.STALE_ELEMENT_REFERENCE,
        'Element does not exist in cache');
  }

  var el = cache[key];

  // Make sure the element is still attached to the DOM before returning.
  var node = el;
  while (node) {
    if (node == doc.documentElement) {
      return el;
    }
    node = node.parentNode;
  }
  delete cache[key];
  throw new bot.Error(bot.ErrorCode.STALE_ELEMENT_REFERENCE,
      'Element is no longer attached to the DOM');
};

