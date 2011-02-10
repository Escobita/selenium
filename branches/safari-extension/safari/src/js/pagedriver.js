goog.provide('selenium.safari.PageDriver');

goog.require('bot.locators');

/**
 * The injected WebDriver object ("PageDriver").
 * Controls the page where it is injected into.
 *
 * @constructor
 */
selenium.safari.PageDriver = function() {
  /**
   * An array to store all elements ever found by findElement methods.
   * @type {Array}
   * @private
   */
  this.elementStore_ = new Array();
};

/**
 * Initializes the PageDriver.
 * @private
 */
selenium.safari.PageDriver.prototype.init = function() {
  document.write("Hello! PageDriver has been initialized.<br/>");
};

/**
 * Find an element from the root.
 * @param {!Object} locator The locator strategy with which to find the elements.
 * @return {?number} The index of the found element in the elementStore, or null if not found.
 */
selenium.safari.PageDriver.prototype.findElement = function(locator) {
  var element = bot.locators.findElement(locator);
  if (element) {
    return this.elementStore_.push(element);
  } else {
    return null;
  }
};
