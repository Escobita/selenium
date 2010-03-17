goog.provide('bot.locators.strategies.xpath');

goog.require('goog.dom');
goog.require('goog.object');
goog.require('goog.string');

// TODO(simon): Add support for browsers with native xpath

/**
 * Find an element by using an xpath expression
 * @param {Window} win The DOM window to search in.
 * @param {string} target The xpath to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.xpath = function(win, target) {
  var doc = win.document;

  if (!goog.isFunction(doc['evaluate'])) {
    throw Error('XPath location is not supported');
  }

  if (!target) {
    throw Error('No xpath specified');
  }

  var element =
      doc.evaluate(target, doc, null, /* FIRST_ORDERED_NODE_TYPE */ 9, null).singleNodeValue;

  return element ? element : null;
};
goog.exportProperty(bot.locators.strategies, 'xpath',
                    bot.locators.strategies.xpath);
