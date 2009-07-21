/*
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
 * @fileoverview A class for working with elements on the page under test.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

goog.provide('webdriver.WebElement');

goog.require('goog.array');
goog.require('goog.math.Coordinate');
goog.require('goog.math.Size');
goog.require('webdriver.CommandInfo');
goog.require('webdriver.Future');

/**
 * TODO(jmleyba): Beefier documentation please
 * Usage:
 * <code>
 * var driver = webdriver.createLocalWebDriver();
 * driver.init();
 * driver.switchToWindow('test_window');
 * driver.get('http://www.google.com');
 * var element = driver.findElement(webdriver.By.name('q'));
 * element.sendKeys('webdriver');
 * element = driver.findElement(webdriver.By.name('btnG'));
 * element.click();
 * </code>
 * @param {webdriver.WebDriver} driver The WebDriver instance that will
 *     actually execute commands.
 * @constructor
 */
webdriver.WebElement = function(driver) {

  /**
   * The WebDriver instance to issue commands to.
   * @type {webdriver.WebDriver}
   * @private
   */
  this.driver_ = driver;

  /**
   * The UUID used by WebDriver to identify this element on the page. The ID is
   * wrapped in a webdriver.Future instance so it can be determined
   * asynchronously.
   * @type {webdriver.Future}
   * @private
   */
  this.elementId_ = new webdriver.Future(this.driver_);
};


/**
 * Regular expression for a UUID.
 * @type {RegExp}
 */
webdriver.WebElement.UUID_REGEX =
    /^{[\da-z]{8}-[\da-z]{4}-[\da-z]{4}-[\da-z]{4}-[\da-z]{12}}$/i;


webdriver.WebElement.findElementLocatorToCommandInfo_ = function(by) {
  var commandMap = {
    id: webdriver.CommandInfo.FIND_ELEMENT_BY_ID,
    className: webdriver.CommandInfo.FIND_ELEMENT_BY_CLASS_NAME,
    name: webdriver.CommandInfo.FIND_ELEMENT_BY_NAME,
    linkText: webdriver.CommandInfo.FIND_ELEMENT_BY_LINK_TEXT,
    partialLinkText: webdriver.CommandInfo.FIND_ELEMENT_BY_PARTIAL_LINK_TEXT,
    tagName: webdriver.CommandInfo.FIND_ELEMENT_BY_TAG_NAME,
    xpath: webdriver.CommandInfo.FIND_ELEMENT_BY_XPATH
  };

  var commandInfo = commandMap[by.type];
  if (goog.isDef(commandInfo)) {
    if (by.type == 'className') {
      by.target = goog.string.trim(by.target);
      if (by.target.search(/\s/) >= 0) {
        throw new Error('Compound class names are not allowed for searches');
      }
    }
    return commandInfo;
  }
  throw new Error('Undefined locator type: ' + by.type);
};


/**
 * Adds a command to the given {@code webdriver.WebDriver} instance to find an
 * element on the page.
 * @param {webdriver.WebDriver} driver The driver to perform the search with.
 * @param {webdriver.By} by The strategy to use for finding the element.
 * @return {webdriver.WebElement} A WebElement that can be used to issue
 *     commands on the found element.  The element's ID will be set
 *     asynchronously once the driver successfully finds the element.
 */
webdriver.WebElement.findElement = function(driver, by) {
  var commandInfo = webdriver.WebElement.findElementLocatorToCommandInfo_(by);
  var webElement = new webdriver.WebElement(driver);
  var webElementId = webElement.getId();
  driver.addCommand(commandInfo.buildCommand(
      driver, [by.target],
      goog.bind(webElementId.setValueFromResponse, webElementId)));
  return webElement;
};


/**
 * Adds a command to the given {@code webdriver.WebDriver} instance to test if
 * an element is present on the page.
 * @param {webdriver.WebDriver} driver The driver to perform the search with.
 * @param {webdriver.By} by The strategy to use for finding the element.
 * @return {webdriver.Future} A future whose value will be set when the driver
 *     completes the search; value will be {@code true} if the element was
 *     found, false otherwise.
 */
webdriver.WebElement.isElementPresent = function(driver, by) {
  var commandInfo = webdriver.WebElement.findElementLocatorToCommandInfo_(by);
  var isPresent = new webdriver.Future(driver);
  driver.addCommand(commandInfo.buildCommand(
      driver, [by.target],
      // If returns without an error, element is present
      function(response) {
        response.value = true;
        isPresent.setValue(true);
      },
      // If returns with an error, element is not present (clear the error!)
      function(response) {
        response.isError = false;
        response.value = false;
        isPresent.setValue(false);
      }));
  return isPresent;
};




/**
 * Adds a command to the given {@code webdriver.WebDriver} instance to find
 * multiple elements on the page.
 * @param {webdriver.WebDriver} driver The driver to perform the search with.
 * @param {webdriver.By} by The strategy to use for finding the elements.
 */
webdriver.WebElement.findElements = function(driver, by) {
  var commandMap = {
    id: webdriver.CommandInfo.FIND_ELEMENTS_BY_ID,
    className: webdriver.CommandInfo.FIND_ELEMENTS_BY_CLASS_NAME,
    name: webdriver.CommandInfo.FIND_ELEMENTS_BY_NAME,
    linkText: webdriver.CommandInfo.FIND_ELEMENTS_BY_LINK_TEXT,
    partialLinkText: webdriver.CommandInfo.FIND_ELEMENTS_BY_PARTIAL_LINK_TEXT,
    tagName: webdriver.CommandInfo.FIND_ELEMENTS_BY_TAG_NAME,
    xpath: webdriver.CommandInfo.FIND_ELEMENTS_BY_XPATH
  };

  var commandInfo = commandMap[by.type];
  if (!goog.isDef(commandInfo)) {
    throw new Error('Undefined locator type');
  }

  driver.addCommand(commandInfo.buildCommand(driver, [by.target],
      function(response) {
        var ids = response.value.split(',');
        var elements = [];
        for (var i = 0, id; id = ids[i]; i++) {
          var element = new webdriver.WebElement(driver);
          element.getId().setValue(id);
          elements.push(element);
        }
        response.value = elements;
      }));
};


webdriver.WebElement.prototype.isElementPresent = function(findBy) {
  var isPresent = new webdriver.Future(this.driver_);
  var foundCallbackFn = function(response) {
    response.value = !!response.value;
    isPresent.setValue(response.value);
  };

  var commandInfo;
  switch (findBy.type) {
    case 'id':
      commandInfo = webdriver.CommandInfo.FIND_ELEMENT_USING_ELEMENT_BY_ID;
      break;
    case 'tagName':
      commandInfo = webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH;
      findBy.target = './/' + findBy.target;
      break;
    case 'linkText':
      commandInfo = webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH;
      findBy.target = ".//a[text()='" + findBy.target + "']";
      break;
    case 'name':
      commandInfo = webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH;
      findBy.target = ".//*[@name='" + findBy.target + "']";
      break;
    case 'partialLinkText':
      commandInfo = webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH;
      findBy.target = ".//a[contains(text(),'" + findBy.target + "')]";
      break;
    case 'xpath':
      commandInfo = webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH;
      break;
    case 'className':
      commandInfo =
          webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_CLASS_NAME;
      foundCallbackFn = function(response) {
        response.value = !!response.value.split(',').length;
        isPresent.setValue(response.value);
      };
      break;
  }

  if (!goog.isDef(commandInfo)) {
    throw new Error('Unsupported locator type: ' + findBy.type);
  }
  this.addCommand_(commandInfo, [findBy.target], foundCallbackFn,
      function(response) {
        response.isError = false;
        response.value = false;
        isPresent.setValue(false);
      });
  return isPresent;
};


/**
 * Adds a command to search for a single element on the page, restricting the
 * search to the descendants of the element represented by this instance.
 * @param {webdriver.By} by The strategy to use for finding the elements.
 * @return {webdriver.WebElement} A WebElement that can be used to issue
 *     commands on the found element.  The element's ID will be set
 *     asynchronously once the element is successfully located.
 */
webdriver.WebElement.prototype.findElement = function(by) {
  var commandInfo, xpath;
  var webElement = new webdriver.WebElement(this.driver_);
  if (by.type == 'id') {
    commandInfo = webdriver.CommandInfo.FIND_ELEMENT_USING_ELEMENT_BY_ID;
    this.addCommand_(commandInfo, [by.target],
        function(response) {
          // TODO(jmleyba): FF extension needs to report error, not return -1
          // That is what it does for finding from the root anyway...
          if (response.value == '-1') {
            throw new Error('Element not found');
          }
          webElement.getId().setValueFromResponse(response);
        });
  } else if (by.type == 'className') {
    this.addCommand_(
        webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_CLASS_NAME,
        [by.target],
        goog.bind(function(response) {
          // TODO(jmleyba): Is this the correct way to handle this?
          response.value = response.value.split(',')[0];
          this.getId().setValue(response.value);
        }, webElement));
  } else if (by.type == 'partialLinkText') {
    this.addCommand_(
        webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_PARTIAL_LINK_TEXT,
        [by.target],
        goog.bind(function(response) {
          // TODO(jmleyba): Is this the correct way to handle this?
          response.value = response.value.split(',')[0];
          this.getId().setValue(response.value);
        }, webElement));
  } else if (by.type == 'tagName') {
    xpath = './/' + by.target;
  } else if (by.type == 'linkText') {
    xpath = ".//a[text()='" + by.target + "']";
  } else if (by.type == 'name') {
    xpath = ".//*[@name='" + by.target + "']";
  } else if (by.type == 'className') {
  } else if (by.type == 'partialLinkText') {
    xpath = ".//a[contains(text(),'" + by.target + "')]";
  } else if (by.type == 'xpath') {
    xpath = by.target;
  }

  if (xpath) {
    this.addCommand_(
        webdriver.CommandInfo.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH, [xpath],
        goog.bind(function(response) {
          // TODO(jmleyba): Is this the correct way to handle this?
          if (!response.value) {
            throw new Error('No element found: ' +
                webdriver.logging.describe(response));
          }
          response.value = response.value.split(',')[0];
          this.getId().setValue(response.value);
        }, webElement));
  }

  return webElement;
};


/**
 * Adds a command to search for multiple elements on the page, restricting the
 * search to the descendants of hte element represented by this instance.
 * @param {webdriver.By} by The strategy to use for finding the elements.
 */
webdriver.WebElement.prototype.findElements = function(by) {
  if (by.type == 'id') {
    by = webdriver.By.xpath(".//*[@id='" + by.target + "']");
  } else if (by.type == 'name') {
    by = webdriver.By.xpath(".//*[@name='" + by.target + "']");
  }

  var ci = webdriver.CommandInfo;
  var commandMap = {
    'className': ci.FIND_ELEMENTS_USING_ELEMENT_BY_CLASS_NAME,
    'linkText': ci.FIND_ELEMENTS_USING_ELEMENT_BY_LINK_TEXT,
    'partialLinkText': ci.FIND_ELEMENTS_USING_ELEMENT_BY_PARTIAL_LINK_TEXT,
    'tagName': ci.FIND_ELEMENTS_USING_ELEMENT_BY_TAG_NAME,
    'xpath': ci.FIND_ELEMENTS_USING_ELEMENT_BY_XPATH
  };

  var commandInfo = commandMap[by.type];
  if (!goog.isDef(commandInfo)) {
    throw new Error('Undefined locator type: ' + by.type);
  }

  this.addCommand_(commandInfo, [by.target], goog.bind(function(response) {
    var ids = response.value.split(',');
    var elements = [];
    for (var i = 0, id; id = ids[i]; i++) {
      var element = new webdriver.WebElement(this.driver_);
      element.getId().setValue(id);
      elements.push(element);
    }
    response.value = elements;
  }, this));
};


/**
 * @return {webdriver.WebDriver} The driver that this element delegates commands
 *     to.
 */
webdriver.WebElement.prototype.getDriver = function() {
  return this.driver_;
};


/**
 * @return {webdriver.Futur} The UUID of this element wrapped in a Future.
 */
webdriver.WebElement.prototype.getId = function() {
  return this.elementId_;
};


/**
 * Helper function for building commands that execute against the element
 * represented by this instance.
 * @param {webdriver.CommandInfo} commandInfo Describes the command to add.
 * @param {Array.<*>} opt_parameters Array of arguments to send with the
 *     command; defaults to an empty array.
 * @param {function} opt_callbackFn Function to call with the response to the
 *     command.
 * @param {function} opt_errorCallbackFn Function to call with the response when
 *     the response is an error.
 * @param {boolean} opt_addToFront Whether this command should be added to the
 *     front or back of the driver's command queue; defaults to {@code false}.
 * @private
 */
webdriver.WebElement.prototype.addCommand_ = function(commandInfo,
                                                      opt_parameters,
                                                      opt_callbackFn,
                                                      opt_errorCallbackFn,
                                                      opt_addToFront) {
  var command = commandInfo.buildCommand(
      this.driver_, opt_parameters, opt_callbackFn, opt_errorCallbackFn);
  command.elementId = this.getId();
  this.driver_.addCommand(command, opt_addToFront);
};


/**
 * Adds a command to click on this element.
 */
webdriver.WebElement.prototype.click = function() {
  this.addCommand_(webdriver.CommandInfo.CLICK_ELEMENT);
};


/**
 * Adds a command to type the given key sequence on this element.
 * @param {string} var_args The strings to type.  All arguments will be joined
 *     into a single sequence (var_args is permitted for convenience).
 */
webdriver.WebElement.prototype.sendKeys = function(var_args) {
  this.addCommand_(webdriver.CommandInfo.SEND_KEYS,
                   [goog.array.slice(arguments, 0).join('')]);
};

/**
 * Queries for the tag/node name of this element.
 */
webdriver.WebElement.prototype.getElementName = function() {
  var name = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_NAME, null,
      goog.bind(name.setValueFromResponse, name));
  return name;
};


/**
 * Queries for the specified attribute.
 * @param {string} attributeName The name of the attribute to query.
 */
webdriver.WebElement.prototype.getAttribute = function(attributeName) {
  var value = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_ATTRIBUTE,
      [attributeName], goog.bind(value.setValueFromResponse, value),
      // If there is an error b/c the attribute was not found, set value to null
      function (response) {
        // TODO(jmleyba): This error message needs to be consistent for all
        // drivers.
        if (response.value == 'No match') {
          response.isError = false;
          response.value = null;
          value.setValue(null);
        }
      });
  return value;
};


webdriver.WebElement.prototype.getValue = function() {
  var value = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_VALUE, null,
      goog.bind(value.setValueFromResponse, value));
  return value;
};


webdriver.WebElement.prototype.getText = function() {
  var text = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_TEXT, null,
      goog.bind(text.setValueFromResponse, text));
  return text;
};


webdriver.WebElement.prototype.setSelected = function() {
  this.addCommand_(webdriver.CommandInfo.SET_ELEMENT_SELECTED);
};


webdriver.WebElement.prototype.clear = function() {
  this.addCommand_(webdriver.CommandInfo.CLEAR_ELEMENT);
};


webdriver.WebElement.prototype.getSize = function() {
  var size = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_SIZE, null,
      goog.bind(function(response) {
        var wh = response.value.replace(/\s/g, '').split(',');
        response.value = new goog.math.Size(wh[0], wh[1]);
        size.setValue(response.value);
      }, this));
  return size;
};


webdriver.WebElement.createCoordinatesFromResponse_ = function(future,
                                                               response) {
  var xy = response.value.replace(/\s/g, '').split(',');
  response.value = new goog.math.Coordinate(xy[0], xy[1]);
  future.setValue(response.value);
};


webdriver.WebElement.prototype.getLocation = function() {
  var currentLocation = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_LOCATION, null,
      goog.bind(webdriver.WebElement.createCoordinatesFromResponse_, null,
          currentLocation));
  return currentLocation;
};


webdriver.WebElement.prototype.dragAndDropBy = function(x, y) {
  var newLocation = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.DRAG_ELEMENT, [x, y],
      goog.bind(webdriver.WebElement.createCoordinatesFromResponse_, null,
          newLocation));
  return newLocation;
};


webdriver.WebElement.prototype.dragAndDropTo = function(webElement) {
  if (this.driver_ != webElement.driver_) {
    throw new Error(
        'WebElements created by different drivers cannot coordinate');
  }

  var toLocation = webElement.getLocation();
  var thisLocation = this.getLocation();
  var newLocation = new webdriver.Future(this.driver_);
  this.driver_.callFunction(goog.bind(function() {
    var delta = goog.math.Coordinate.difference(
        toLocation.getValue(), thisLocation.getValue());
    this.addCommand_(webdriver.CommandInfo.DRAG_ELEMENT, [delta.x, delta.y],
        goog.bind(webdriver.WebElement.createCoordinatesFromResponse_, null,
            newLocation), null, true);
  }, this));
  return newLocation;
};


webdriver.WebElement.prototype.isEnabled = function() {
  var futureValue = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_ATTRIBUTE, ['disabled'],
      function(response) {
        response.value = !!!response.value;
        futureValue.setValue(response.value);
      });
  return futureValue;
};


/** @private */
webdriver.WebElement.prototype.isCheckedOrSelected_ = function(opt_future,
                                                               opt_addToFront) {
  var value = opt_future ||  new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.GET_ELEMENT_NAME, null,
      goog.bind(function(response) {
        var attribute = response.value == 'input' ? 'checked' : 'selected';
        this.addCommand_(
            webdriver.CommandInfo.GET_ELEMENT_ATTRIBUTE, [attribute],
            function(response) {
              response.value = !!response.value;
              value.setValue(response.value);
            }, null, true);
      }, this), null, opt_addToFront);
  return value;
};



webdriver.WebElement.prototype.isSelected = function() {
  return this.isCheckedOrSelected_();
};


webdriver.WebElement.prototype.isChecked = function() {
  return this.isCheckedOrSelected_();
};


webdriver.WebElement.prototype.toggle = function() {
  var toggleResult = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.TOGGLE_ELEMENT, null,
      goog.bind(function() {
        this.isCheckedOrSelected_(toggleResult, true);
      }, this));
  return toggleResult;
};


webdriver.WebElement.prototype.submit = function() {
  this.addCommand_(webdriver.CommandInfo.SUBMIT_ELEMENT);
};


webdriver.WebElement.prototype.clear = function() {
  this.addCommand_(webdriver.CommandInfo.CLEAR_ELEMENT);
};


webdriver.WebElement.prototype.isDisplayed = function() {
  var futureValue = new webdriver.Future(this.driver_);
  this.addCommand_(webdriver.CommandInfo.IS_ELEMENT_DISPLAYED, null,
      function(response) {
        // TODO(jmleyba): FF extension should not be returning a string here...
        if (goog.isString(response.value)) {
          futureValue.setValue(response.value == 'true');
        } else {
          futureValue.setValue(response.value);
        }
      });
  return futureValue;
};


webdriver.WebElement.prototype.getOuterHtml = function() {
  return this.driver_.executeScript(
      ['var element = arguments[0];',
       'if ("outerHTML" in element) {',
       '  return element.outerHTML;',
       '} else {',
       '  var div = document.createElement("div");',
       '  div.appendChild(element.cloneNode(true));',
       '  return div.innerHTML;',
       '}'].join(''), this);
};


webdriver.WebElement.prototype.getInnerHtml = function() {
  return this.driver_.executeScript('return arguments[0].innerHTML', this);
};

