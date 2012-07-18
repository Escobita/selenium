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

/**
 * @fileoverview Node externs for WebDriverJS.
 */


/** @type {!Object} */
var exports = {};


/** @type {!Object} */
var process = {};


/** @type {!Object.<string>} */
process.env = {};


/** @type {string} */
var __filename = '';


/**
 * @typedef {{statusCode:number,
 *            headers:!Object.<string>}}
 */
var NodeHttpResponse;


/**
 * @param {string} str
 * @return {{
 *     readFile: function(string, string, Function),
 *     request: function(!Object, function(!NodeHttpResponse))
 *     }}
 */
function require(str) {}


/** @type {!Object} */
var JSON = {};

/**
 * @param {string} value
 * @return {!Object}
 */
JSON.parse = function(value) {};

/**
 * @param {*} value
 * @return {string}
 */
JSON.stringify = function(value) {};
