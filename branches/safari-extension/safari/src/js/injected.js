goog.provide('selenium.safari.Injected');

goog.require('selenium.safari.PageDriver');

/**
 * The injected WebDriver object, does the wiring.
 * @param {selenium.safari.PageDriver} pagedriver The encapsulated PageDriver.
 * @constructor
 */
selenium.safari.Injected = function(pageDriver) {
  /**
   * A reference to the encapsulated PageDriver.
   * @type {selenium.safari.PageDriver}
   * @private
   */
  this.pageDriver_ = pageDriver;
};

/**
 * Initializes the PageDriver.
 */
selenium.safari.Injected.prototype.init = function() {
  this.pageDriver_.init();
  document.write('Injected WebDriver has been initialized.<br/>')
};

var injected = new selenium.safari.Injected(new selenium.safari.PageDriver());
injected.init();
