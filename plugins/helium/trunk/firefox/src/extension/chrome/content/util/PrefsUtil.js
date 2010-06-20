/**
 *  Copyright 2009 - SERLI
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * This class provides useful methods
 * to retrieve and write preferences.
 *
 * @author Kevin Pollet
 */
function PrefsUtil(branche) {
    /**
     * Create the XPCOM preferences component.
     */
    var service = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
    this.prefService = service.getBranch(branche);

    if (typeof PrefsUtil.initialized == "undefined") {

        PrefsUtil.prototype.getChar = function(key) {
            return this.prefService.getCharPref(key);
        };

        PrefsUtil.prototype.getInt = function(key) {
            return this.prefService.getIntPref(key);
        };

        PrefsUtil.prototype.setChar = function(key) {
            return this.prefService.setCharPref(key);
        };

        PrefsUtil.prototype.setInt = function(key) {
            return this.prefService.setIntPref(key);
        };

        PrefsUtil.initialized = true;
    }
}
