
goog.provide('bot.locators');

goog.require('bot');
goog.require('bot.locators.strategies');

/**
 * Find an element by using a selector.
 * 
 * @param {Window} win The DOM window to search in.
 * @param {string} target The selector to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.findElement = function(target) {
  var finder_func = bot.locators.strategies.lookupSingle(target);
  return finder_func();
};

bot.locators.findElements = function(target) {
  var finder_func = bot.locators.strategies.lookupMany(target);
  return finder_func();
};
