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
 * @fileoverview Helper function to determine which HTML5 features are
 * supported by browsers..
 */

goog.provide('bot.html5');

goog.require('bot');
goog.require('bot.Error');
goog.require('bot.ErrorCode');
goog.require('bot.userAgent');
goog.require('goog.userAgent');


/**
 * Identifier for supported HTML5 API in Webdriver.
 *
 * @enum {string}
 */
bot.html5.API = {
  APPCACHE: 'appcache',
  BROWSER_CONNECTION: 'browser_connection',
  DATABASE: 'database',
  GEOLOCATION: 'location',
  LOCAL_STORAGE: 'local_storage',
  SESSION_STORAGE: 'session_storage',
  VIDEO: 'video',
  AUDIO: 'audio',
  CANVAS: 'canvas'
};


/**
 * True if the current browser is IE8.
 *
 * @private
 * @type {boolean}
 * @const
 */
bot.html5.IS_IE8_ = goog.userAgent.IE &&
    bot.userAgent.isVersion(8) && !bot.userAgent.isVersion(9);


/**
 * True if the current browser is Safari 4.
 *
 * @private
 * @type {boolean}
 * @const
 */
bot.html5.IS_SAFARI4_ = goog.userAgent.SAFARI &&
    bot.userAgent.isVersion(4) && !bot.userAgent.isVersion(5);


/**
 * True if the current browser is Safari 5 on Windows.
 *
 * @private
 * @type {boolean}
 * @const
 */
bot.html5.IS_SAFARI5_WINDOWS_ = goog.userAgent.WINDOWS &&
    goog.userAgent.SAFARI && bot.userAgent.isVersion(5) &&
    !bot.userAgent.isVersion(6);


/**
 * Checks if the browser supports an HTML5 feature.
 *
 * @param {bot.html5.API} api HTML5 API identifier.
 * @param {!Window=} opt_window The window to be accessed;
 *     defaults to the main window.
 * @return {boolean} Whether the browser supports the feature.
 */
bot.html5.isSupported = function(api, opt_window) {
  var win = opt_window || bot.getWindow();

  switch (api) {
    case bot.html5.API.APPCACHE:
      // IE8 does not support application cache, though the APIs exist.
      if (bot.html5.IS_IE8_) {
        return false;
      }
      return goog.isDefAndNotNull(win.applicationCache);

    case bot.html5.API.BROWSER_CONNECTION:
      return goog.isDefAndNotNull(win.navigator) &&
          goog.isDefAndNotNull(win.navigator.onLine);

    case bot.html5.API.DATABASE:
      // Safari4 database API does not allow writes.
      if (bot.html5.IS_SAFARI4_) {
        return false;
      }
      return goog.isDefAndNotNull(win.openDatabase);

    case bot.html5.API.GEOLOCATION:
      // Safari 5 on Windows does not support geolocation, see:
      // https://discussions.apple.com/thread/3547900
      if (bot.html5.IS_SAFARI5_WINDOWS_) {
        return false;
      }
      return goog.isDefAndNotNull(win.navigator) &&
          goog.isDefAndNotNull(win.navigator.geolocation);

    case bot.html5.API.LOCAL_STORAGE:
      // IE8 does not support local storage, though the APIs exist.
      if (bot.html5.IS_IE8_) {
        return false;
      }
      return goog.isDefAndNotNull(win.localStorage);

    case bot.html5.API.SESSION_STORAGE:
      // IE8 does not support session storage, though the APIs exist.
      if (bot.html5.IS_IE8_) {
        return false;
      }
      return goog.isDefAndNotNull(win.sessionStorage) &&
          // To avoid browsers that only support this API partically
          // like some versions of FF.
          goog.isDefAndNotNull(win.sessionStorage.clear);

    default:
      throw new bot.Error(bot.ErrorCode.UNKNOWN_ERROR,
          'Unsupported API identifier provided as parameter');
  }
};
