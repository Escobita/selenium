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
 *@fileOverview Ready to inject atoms for handling frames.
 */

goog.provide('webdriver.atoms.inject.frame');

goog.require('bot.frame');
goog.require('bot.inject');
goog.require('bot.inject.cache');


/**
 * Finds a frame by id or name.
 *
 * @param {string} idOrName The frame id or name.
 * @param {{bot.inject.WINDOW_KEY: string}=} opt_root The wrapped window to
 *     perform the search under. Defaults to window.
 * @return {string} A frame element wrapped in a JSON string as defined by
 *     the wire protocol.
 */
webdriver.atoms.inject.frame.findFrameByIdOrName = function(idOrName, opt_root) {
  return bot.inject.executeScript(bot.frame.findFrameByNameOrId,
      [idOrName, opt_root], true);
};


/**
 * @return {string} A string representing the currently active element.
 */
webdriver.atoms.inject.frame.activeElement = function() {
  return bot.inject.executeScript(bot.frame.activeElement, [], true);
};


/**
 * Finds a frame by index.
 *
 * @param {number} index The index of the frame to search for.
 * @param {!Window=} opt_root The window to perform the search under.
 * If not specified window is used as the default.
 * @return {string} A frame element wrapped in a JSON string as defined by
 *     the wire protocol.
 */
webdriver.atoms.inject.frame.findFrameByIndex = function(index, opt_root) {
  return bot.inject.executeScript(bot.frame.findFrameByIndex,
      [index, opt_root], true);
};


/**
 * @return {string} The default content of the current page,
 *     which is the top window.
 */
webdriver.atoms.inject.frame.defaultContent = function() {
  return bot.inject.executeScript(bot.frame.defaultContent,
      [], true);
};


/**
 * @param {bot.inject.ELEMENT_KEY:string} element The element to query.
 * @return {string} The window corresponding to the frame element
 * wrapped in a JSON string as defined by the wire protocol.
 */
webdriver.atoms.inject.frame.getFrameWindow = function(element) {
  return bot.inject.executeScript(bot.frame.getFrameWindow,
      [element], true);
};
