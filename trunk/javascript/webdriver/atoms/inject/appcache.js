// Copyright 2012 WebDriver committers
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
 *@fileOverview Ready to inject atoms for handling application cache.
 */

goog.provide('webdriver.atoms.inject.storage.appcache');

goog.require('bot.inject');
goog.require('webdriver.atoms.storage.appcache');


/**
 * Gets the status of the application cache.
 *
 * @return {number} The status of the application cache.
 */
webdriver.atoms.inject.storage.appcache.getStatus = function() {
  return bot.inject.executeScript(webdriver.atoms.storage.appcache.getStatus,
      [], true);
};


