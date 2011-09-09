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
 * @fileoverview Atoms for frame handling.
 *
 */


goog.provide('bot.frame');

goog.require('bot.locators');
goog.require('goog.array');
goog.require('goog.dom');


/**
 * @return {!Window} The top window.
 */
bot.frame.defaultContent = function() {
  return bot.getWindow().top;
};


/**
 * @return {!Element} The currently active element.
 */
bot.frame.activeElement = function() {
  return document.activeElement || document.body;
};


/**
 * Returns a reference to the window object corresponding to the given element.
 * Note that the element must be a frame or an iframe.
 *
 * @param {!Element} element The iframe or frame element.
 * @return {Window} The window reference for the given iframe or frame element.
 */
bot.frame.getFrameWindow = function(element) {
  if (/^i?frame$/i.test(element.tagName)) {
    var frame = /** @type {HTMLFrameElement|HTMLIFrameElement} */ element;
    return goog.dom.getFrameContentWindow(frame);
  }
  throw new bot.Error(bot.ErrorCode.NO_SUCH_FRAME,
      "The given element isn't a frame or an iframe.");
};


/**
 * Looks for a frame by its name or id (preferring name over id)
 * under the given root. If no frame was found, we look for an
 * iframe by name or id.
 *
 * @param {string} nameOrId The frame's name or the frame's id.
 * @param {!Window=} opt_root The window to perform the search under.
 *     Defaults to {@code bot.getWindow()}.
 * @return {Window} The window if found, null otherwise.
 */
bot.frame.findFrameByNameOrId = function(nameOrId, opt_root) {
  var domWindow = opt_root || bot.getWindow();

  // Lookup frame by name
  var frame = domWindow.frames[nameOrId];
  if (frame) {
    // This is needed because Safari 4 returns
    // an HTMLFrameElement instead of a Window object.
    if (frame.document) {
      return frame;
    } else {
      return goog.dom.getFrameContentWindow(frame);
    }
  }

  // Lookup frame by id
  var isFrame = function(element) {
    return bot.dom.isElement(element, goog.dom.TagName.FRAME) ||
        bot.dom.isElement(element, goog.dom.TagName.IFRAME);
  }
  var elements = bot.locators.findElements({id: nameOrId},
      domWindow.document);
  for (var i = 0; i < elements.length; i++) {
    if (isFrame(elements[i])) {
      return goog.dom.getFrameContentWindow(elements[i]);
    }
  }
  return null;
};


/**
 * Looks for a frame by its index under the given root.
 *
 * @param {number} index The frame's index.
 * @param {!Window=} opt_root The window to perform
 *     the search under. Defaults to {@code bot.getWindow()}.
 * @return {Window} The frame if found, null otherwise.
 */
bot.frame.findFrameByIndex = function(index, opt_root) {
  var domWindow = opt_root || bot.getWindow();
  return domWindow.frames[index] || null;
};

/**
 * Gets the index of a frame in the given window. Note that the element must
 * be a frame or an iframe.
 *
 * @param {Element} element The iframe or frame element.
 * @param {!Window=} opt_root The window to perform
 *     the search under. Defaults to {@code bot.getWindow()}.
 * @return {number} The index of the frame if found, null otherwise.
 */
bot.frame.getFrameIndex = function(element, opt_root) {
  var domWindow = opt_root || bot.getWindow();

  if (element.nodeType != 1 || !/^i?frame$/i.test(element.tagName)) {
    return null;
  }
  for (var i = 0; i < domWindow.frames.length; i++) {
    if (element.contentWindow == domWindow.frames[i]) {
      return i;
    }
  }
  return null;
};
