// Copyright 2012 Software Freedom Conservancy. All Rights Reserved.
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

goog.provide('remote.ui.Event');
goog.provide('remote.ui.Event.Type');

goog.require('goog.events.Event');


/**
 * UI event with associated data.
 * @param {string} type The type of event.
 * @param {goog.events.EventTarget} target The event target.
 * @param {*=} data The data associated with this event.
 * @constructor
 * @extends {goog.events.Event}
 */
remote.ui.Event = function(type, target, data) {
  goog.base(this, type, target);

  /**
   * The data associated with this event.
   * @type {*}
   */
  this.data = data;
};
goog.inherits(remote.ui.Event, goog.events.Event);


/**
* Types of events used by the {@code remote.ui} package.
* @enum {string}
*/
remote.ui.Event.Type = {
  CREATE: 'create',
  DELETE: 'delete',
  REFRESH: 'refresh',
  SCREENSHOT: 'screenshot'
};
