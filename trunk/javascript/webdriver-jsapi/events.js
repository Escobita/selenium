// Copyright 2011 WebDriver committers
// Copyright 2011 Google Inc.
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
 * @fileoverview A light weight event system modeled after Node's EventEmitter.
 */

goog.provide('webdriver.EventEmitter');


/**
 * Object that can emit events for others to listen for. This is used instead
 * of Closure's event system because it is much more light weight. The API is
 * based on Node's EventEmitters.
 * @constructor
 * @export
 */
webdriver.EventEmitter = function() {
  /**
   * Map of events to registered listeners.
   * @type {!Object.<string, !Array.<{fn:!Function,oneshot:boolean}>>}
   * @private
   */
  this.events_ = {};
};


/**
 * Fires an event and calls all listeners.
 * @param {string} type The type of event to emit.
 * @param {...*} var_args Any arguments to pass to each listener.
 * @export
 */
webdriver.EventEmitter.prototype.emit = function(type, var_args) {
  var args = Array.prototype.slice.call(arguments, 1);
  var listeners = this.events_[type];
  if (!listeners) {
    return;
  }
  var i = 0;
  while (i < listeners.length) {
    listeners[i].fn.apply(null, args);
    if (listeners[i].oneshot) {
      listeners.splice(i, 1);
    } else {
      i += 1;
    }
  }
};


/**
 * Returns a mutable list of listeners for a specific type of event.
 * @param {string} type The type of event to retrieve the listeners for.
 * @return {!Array.<!Function>} The registered listeners for the given event
 *     type.
 */
webdriver.EventEmitter.prototype.listeners = function(type) {
  var listeners = this.events_[type];
  if (!listeners) {
    listeners = this.events_[type] = [];
  }
  return listeners;
};


/**
 * Registers a listener.
 * @param {string} type The type of event to listen for.
 * @param {!Function} listener The function to invoke when the event is fired.
 * @param {boolean=} opt_oneshot Whether the listener should be removed after
 *    the first event is fired.
 * @return {!webdriver.EventEmitter} A self reference.
 * @export
 */
webdriver.EventEmitter.prototype.addListener = function(type, listener,
                                                        opt_oneshot) {
  var listeners = this.listeners(type);
  var n = listeners.length;
  for (var i = 0; i < n; ++i) {
    if (listeners[i] == listener) {
      return this;
    }
  }

  listeners.push({
    fn: listener,
    oneshot: !!opt_oneshot
  });
  return this;
};


/**
 * Registers a one-time listener which will be called only the first time an
 * event is emitted, afterwhich it will be removed.
 * @param {string} type The type of event to listen for.
 * @param {!Function} listener The function to invoke when the event is fired.
 * @return {!webdriver.EventEmitter} A self reference.
 * @export
 */
webdriver.EventEmitter.prototype.once = function(type, listener) {
  return this.addListener(type, listener, true);
};


/**
 * An alias for {@code #addListener()}.
 * @param {string} type The type of event to listen for.
 * @param {!Function} listener The function to invoke when the event is fired.
 * @return {!webdriver.EventEmitter} A self reference.
 * @export
 */
webdriver.EventEmitter.prototype.on =
    webdriver.EventEmitter.prototype.addListener;


/**
 * Removes a previously registered event listener.
 * @param {string} type The type of event to unregister.
 * @param {!Function} listener The handler function to remove.
 * @return {!webdriver.EventEmitter} A self reference.
 * @export
 */
webdriver.EventEmitter.prototype.removeListener = function(type, listener) {
  var listeners = this.events_[type];
  if (listeners) {
    var n = listeners.length;
    for (var i = 0; i < n; ++i) {
      if (listeners[i].fn == listener) {
        listeners.splice(i, 1);
        return this;
      }
    }
  }
  return this;
};


/**
 * Removes all listeners for a specific type of event. If no event is
 * specified, all listeners across all types will be removed.
 * @param {string=} type The type of event to remove listeners from.
 * @return {!webdriver.EventEmitter} A self reference.
 * @export
 */
webdriver.EventEmitter.prototype.removeAllListeners = function(type) {
  goog.isDef(type) ? delete this.events_[type] : this.events_ = {};
  return this;
};
