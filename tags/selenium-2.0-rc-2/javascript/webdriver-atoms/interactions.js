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
 * @fileoverview Atoms-based implementation of the webelement interface
 */

goog.provide('webdriver.interactions');

goog.require('bot.keys');


/**
 * Send keyboard events to the given element.
 *
 * @param {!Element} element The element to use.
 * @param {string} keys The text to simulate typing.
 */
webdriver.interactions.sendKeys = function(element, keys) {
  bot.keys.type(element, keys);
};
