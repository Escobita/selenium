goog.provide('bot.locators.strategies.className');

goog.require('goog.dom');
goog.require('goog.string');


/**
 * Find an element by its class name.
 * @param {Window} win The DOM window to search in.
 * @param {string} target The class name to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.by_className = function(win, target) {
  if (!target) {
    throw Error('No class name specified');
  }

  target = goog.string.trim(target);
  if (target.split(/\s+/).length > 1) {
    throw Error('Compound class names not permitted');
  }

  var domHelper = new goog.dom.DomHelper(win.document);
  var elements = domHelper.getElementsByTagNameAndClass(
      /*tagName=*/'*', /*className=*/target);
  if (elements.length > 0) {
    return elements[0];
  }
  return null;
};
goog.exportProperty(bot.locators.strategies, 'by_className',
                    bot.locators.strategies.by_className);