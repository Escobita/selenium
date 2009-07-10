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
 * @fileoverview Defines standard strategies for locating an element on the page
 * under test.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

goog.provide('webdriver.By');

goog.require('webdriver.CommandInfo');


/**
 * Class for defining different strategies for locating an element on the page
 * under test.  Each instance is considered immutable. This class should not be
 * instantiated directly; use one of the static factory methods instead.
 * @param {string} type The name of the locator type.
 * @param {string} target The target value to search for.
 * @constructor
 */
webdriver.By = function(type, target) {
  this.type = type;
  this.target = target;
};


/**
 * @return {webdriver.By} A locator for finding an element by its ID.
 * @static
 */
webdriver.By.id = function(id) {
  return new webdriver.By('id', id);
};


/**
 * @return {webdriver.By} A locator for finding an {@code A} element by its
 *     text.
 * @static
 */
webdriver.By.linkText = function(text) {
  return new webdriver.By('linkText', text);
};


/**
 * @return {webdriver.By} A locator for finding an element by the value of its
 *     name attribute.
 * @static
 */
webdriver.By.name = function(name) {
  return new webdriver.By('name', name);
};


/**
 * @return {webdriver.By} A locator for finding an element by its class name.
 * @static
 */
webdriver.By.className = function(name) {
  return new webdriver.By('className', name);
};


/**
 * @return {webdriver.By} A locator for finding an {@code A} element by a
 *     partial match of its text.
 * @static
 */
webdriver.By.partialLinkText = function(text) {
  return new webdriver.By('partialLinkText', text);
};


/**
 * @return {webdriver.By} A locator for finding an element by its tag name.
 * @static
 */
webdriver.By.tagName = function(name) {
  return new webdriver.By('tagName', name);
};


/**
 * @return {webdriver.By} A locator for finding an element by XPath expression.
 * @static
 */
webdriver.By.xpath = function(xpath) {
  return new webdriver.By('xpath', xpath);
};
