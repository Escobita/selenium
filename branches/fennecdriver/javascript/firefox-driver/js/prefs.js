/*
 Copyright 2011 WebDriver committers
 Copyright 2011 Software Freedom Conservancy

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * @fileOverview Utilizing and using xpcom preferences. The contents of this
 * file assume that the execution context will always be a Firefox extension.
 */

goog.provide('fxdriver.prefs');

goog.require('fxdriver.Logger');
goog.require('fxdriver.moz');


/**
 * @type {*}
 * @const
 * @private
 */
fxdriver.prefs.allPrefs_ = fxdriver.moz.getService(
    '@mozilla.org/preferences-service;1', 'nsIPrefBranch');


/**
 * Lookup a particular preference.
 *
 * @param pref {string} The preference to look up.
 * @param opt_default {(string|number|boolean)=} A default value.
 * @return {(string|number|boolean)=} The preference or null.
 */
fxdriver.prefs.read = function(pref, opt_default) {
  var prefs = fxdriver.prefs.allPrefs_;
  var def = opt_default || null;

  if (!prefs.prefHasUserValue(pref)) {
    fxdriver.Logger.dumpn('Preference: ' + pref + ' not set; defaulting to ' + def);
    return def;
  }

  var type = prefs.getPrefType(pref);
  var value = opt_default || null;

  switch (type) {
    case Components.interfaces.nsIPrefBranch.PREF_STRING:
      value = prefs.getCharPref(pref);
      break;

    case Components.interfaces.nsIPrefBranch.PREF_INT:
      value = prefs.getIntPref(pref);
      break;

    case Components.interfaces.nsIPrefBranch.PREF_BOOL:
      value = prefs.getBoolPref(pref);
      break;

    default:
      fxdriver.Logger.dumpn('No match made for pref: ' + pref);
  }

  fxdriver.Logger.dumpn('Preference: ' + pref + ' -> ' + value);

  return value;
};
