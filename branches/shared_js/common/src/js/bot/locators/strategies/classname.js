goog.provide('bot.locators.strategies.className');

goog.require('goog.array');
goog.require('goog.dom');
goog.require('goog.string');


bot.locators.strategies.className.filter_ = function(element, root, doc) {
  return root == doc || goog.dom.contains(root, element);
};

/**
 * Find an element by its class name.
 * @param {Window} win The DOM window to search in.
 * @param {string} target The class name to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.className.single = function(win, target, opt_root) {
  if (!target) {
    throw Error('No class name specified');
  }

  target = goog.string.trim(target);
  if (target.split(/\s+/).length > 1) {
    throw Error('Compound class names not permitted');
  }

  var doc = goog.dom.getOwnerDocument(win);
  var root = opt_root || doc;

  var domHelper = new goog.dom.DomHelper(doc);
  var elements = domHelper.getElementsByTagNameAndClass(
      /*tagName=*/'*', /*className=*/target);
  return goog.array.find(elements, function(element) {
    return bot.locators.strategies.className.filter_(element, root, doc);
  });
};

/**
 * Find an element by its class name.
 * @param {Window} win The DOM window to search in.
 * @param {string} target The class name to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {goog.array.ArrayLike} All matching elements, or an empty list
 */
bot.locators.strategies.className.many = function(win, target, opt_root) {
  if (!target) {
    throw Error('No class name specified');
  }

  target = goog.string.trim(target);
  if (target.split(/\s+/).length > 1) {
    throw Error('Compound class names not permitted');
  }

  var doc = goog.dom.getOwnerDocument(win);
  var root = opt_root || doc;

  var domHelper = new goog.dom.DomHelper(doc);
  var allElements = domHelper.getElementsByTagNameAndClass(
      /*tagName=*/'*', /*className=*/target);
  return goog.array.filter(allElements, function(element) {
    return bot.locators.strategies.className.filter_(element, root, doc);
  });
};