goog.provide('bot.locators.strategies.xpath');

goog.require('goog.dom');
goog.require('goog.object');
goog.require('goog.string');

// TODO(simon): Add support for browsers without native xpath

/**
 * Find an element by using an xpath expression
 * @param {Window} win The DOM window to search in.
 * @param {string} target The xpath to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.xpath.single = function(win, target) {
  var doc = goog.dom.getOwnerDocument(win);

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

/**
 * Find an element by using an xpath expression
 * @param {Window} win The DOM window to search in.
 * @param {string} target The xpath to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.xpath.many = function(win, target) {
  var doc = goog.dom.getOwnerDocument(win);

  if (!goog.isFunction(doc['evaluate'])) {
    throw Error('XPath location is not supported');
  }

  if (!target) {
    throw Error('No xpath specified');
  }

  var nodes = doc.evaluate(target, doc, null, /* ORDERED_NODE_ITERATOR_TYPE */ 5, null);

  var elements = [];
  var e = nodes.iterateNext();
  while (e) {
    elements.push(e);
    e = nodes.iterateNext();
  }
  return elements;
};
