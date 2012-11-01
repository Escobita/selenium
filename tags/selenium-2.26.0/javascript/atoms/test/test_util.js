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
 * @fileoverview Generic testing utilities.
 */

goog.provide('bot.test');

goog.require('goog.userAgent');


/**
 * @return {boolean} Whether the window under test has focus.
 */
bot.test.isWindowFocused = function() {
  return !goog.userAgent.GECKO || window.document.hasFocus();
};


/**
 * @return {boolean} Whether the current test is Selenium-backed.
 */
bot.test.isSeleniumBacked = function() {
  // Test ('selenium' in window.opener) rather than !!window.opener['selenium']
  // to avoid "attempted to read protected variable" error in Opera.
  return !!window.opener && ('selenium' in window.opener);
};
