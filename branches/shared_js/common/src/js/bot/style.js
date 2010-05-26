goog.provide('bot.style');

goog.require('bot');
goog.require('goog.style');

/**
 * Pick the first available value from the element's computed, cascaded or
 * inline style.
 *
 * @param {Element} elem Element to get the style value from.
 * @param {string} styleName The name of the value to look up.
 * @return {string} The value of the style, or undefined.
 */
bot.style.getBestStyle = function(elem, styleName) {
  return goog.style.getComputedStyle(elem, styleName) ||
    goog.style.getCascadedStyle(elem, styleName) ||
    elem.style[styleName];
};
