
goog.provide('bot.locators');

goog.require('bot');
goog.require('bot.locators.strategies');
goog.require('goog.array');


/**
 * Find an element by using a selector.
 * 
 * @param {Window} win The DOM window to search in.
 * @param {string} target The selector to search for.
 * @param {Node=} opt_root The optional root node (defaults to document)
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.findElement = function(target, opt_root) {
  var finder_func = bot.locators.strategies.lookupSingle(target, opt_root);
  return finder_func();
};

/**
 * Find all elements matching a selector.
 *
 * @param {Window} win The DOM window to search in.
 * @param {string} target The selector to search for.
 * @param {Node=} opt_root The optional root node (defaults to document)  
 * @return {?goog.array.ArrayLike} All matching elements (may be an empty list)
 */
bot.locators.findElements = function(target, opt_root) {
  var finder_func = bot.locators.strategies.lookupMany(target, opt_root);
  return finder_func();
};
