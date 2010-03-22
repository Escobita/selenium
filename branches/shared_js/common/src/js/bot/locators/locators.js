
goog.provide('bot.locators');

goog.require('bot');
goog.require('bot.locators.strategies');

/**
 * Find an element by using a selector. The format of the sel
 * @param {Window} win The DOM window to search in.
 * @param {string} target The selector to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.findElement = function(target) {
  var finder_func = bot.locators.strategies.lookup(target);
  return finder_func();
};

