/** @license
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

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
 * @fileoverview Defines a command processor that uses a browser
 * extension/plugin object available to the Javascript on the page.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

goog.provide('webdriver.LocalCommandProcessor');

goog.require('goog.array');
goog.require('goog.json');
goog.require('goog.object');
goog.require('webdriver.AbstractCommandProcessor');
goog.require('webdriver.CommandName');
goog.require('webdriver.Context');
goog.require('webdriver.Response');


/**
 * Command processor that uses a browser extension/plugin exposed to the page
 * for executing WebDriver commands.
 * @constructor
 * @extends {webdriver.AbstractCommandProcessor}
 */
webdriver.LocalCommandProcessor = function() {
  webdriver.AbstractCommandProcessor.call(this);
  // TODO(jmleyba): IE, Chrome, et al. support
  this.cp_ = goog.global['__webDriverCommandProcessor'];
  if (!goog.isDef(this.cp_)) {
    throw new Error(
        'The current browser does not support a LocalCommandProcessor');
  }
};
goog.inherits(webdriver.LocalCommandProcessor,
              webdriver.AbstractCommandProcessor);


/**
 * @override
 */
webdriver.LocalCommandProcessor.prototype.executeDriverCommand = function(
    command, sessionId, context) {
  if (command.name == webdriver.CommandName.SEND_KEYS) {
    command.parameters = [command.parameters.join('')];
  }

  var jsonCommand = {
    'commandName': command.name,
    'context': context.toString(),
    'parameters': command.parameters
  };

  if (command.element) {
    jsonCommand['elementId'] = command.element.getId().getValue();
  }

  webdriver.logging.info(
      'sending:\n' +
      webdriver.logging.describe(jsonCommand, '  '));

  this.cp_.execute(goog.json.serialize(jsonCommand), function(jsonResponse) {
    var rawResponse = goog.json.parse(jsonResponse);
    webdriver.logging.info(
        'receiving:\n' +
        webdriver.logging.describe(rawResponse, '  '));

    var response = new webdriver.Response(
        rawResponse['isError'],
        webdriver.Context.fromString(rawResponse['context']),
        rawResponse['response']);
    response.extraData['resultType'] = rawResponse['resultType'];
    command.setResponse(response);
  });
};
