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
 * @fileoverview Wrapping execute script to use a serialized window object.
 *
 */

goog.provide('webdriver.inject');

goog.require('bot.inject');
goog.require('bot.inject.cache');

/**
 * Wrapper to allow passing a seliazed window object to executeScript.
 *
 * @param {!(string|function)} fn The function to execute.
 * @param {Array.<*>} args Array of arguments to pass to fn.
 * @param {!{bot.inject.WINDOW_KEY:string}=} opt_window The serialized window
 *     object to be read from the cache.
 * @return {!(string|bot.inject.Response)} The response object. If
 *     opt_stringify is true, the result will be serialized and returned in
 *     string format.
 */
webdriver.inject.executeScript = function(fn, args, opt_window) {
  return bot.inject.executeScript(
      fn, args, true, webdriver.inject.getWindow_(opt_window));
};

/**
 *
 * @param {!(string|function)} fn The function to execute.
 * @param {Array.<*>} args Array of arguments to pass to fn.
 * @param {int} timeout The timeout to wait up to in millis.
 * @param {!{bot.inject.WINDOW_KEY:string}=} opt_window The serialized window
 * object to be read from the cache.
 * @return {!(string|bot.inject.Response)} The response object. If
 *     opt_stringify is true, the result will be serialized and returned in
 *     string format.
 */
webdriver.inject.executeAsyncScript =
    function(fn, args, timeout, onDone, opt_window) {
  return bot.inject.executeScript(fn, args, timeout, onDone, true,
          webdriver.inject.getWindow_(opt_window));
};


/**
 * Get the window to use.
 *
 * @param {!Window=} opt_window An optional window to use.
 * @return {!Window} A reference to a window.
 * @private
 */
webdriver.inject.getWindow_ = function(opt_window) {
  var win;
  if (opt_window) {
    win = bot.inject.cache.getElement(opt_window['WINDOW']);
  } else {
    win = window;
  }
  return win;
};
