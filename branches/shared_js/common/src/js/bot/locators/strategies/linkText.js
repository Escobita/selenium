
goog.provide('bot.locators.strategies.linkText');
goog.provide('bot.locators.strategies.partialLinkText');

goog.require('bot');
goog.require('bot.dom');
goog.require('goog.array');
goog.require('goog.dom');

/**
 * Find an element by using the text value of a link
 * @param {Window} win The DOM window to search in.
 * @param {string} target The link text to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 * @private
 */
bot.locators.strategies.linkText.single_ = function(win, target, opt_root, isPartial) {
  if (!target) {
    throw Error('No link text specified');
  }

  var doc = goog.dom.getOwnerDocument(win);
  var root = opt_root || doc;

  var domHelper = new goog.dom.DomHelper(doc);
  var elements = domHelper.getElementsByTagNameAndClass(/*tagName=*/'A');

  return goog.array.find(elements, function(element) {
    var text = bot.dom.getVisibleText(element);
    
    if ((isPartial && text.indexOf(target) != -1) ||
        text == target) {
      return goog.dom.contains(root, element);
    }
  });
};

/**
 * Find many elements by using the value of the link text
 * @param {Window} win The DOM window to search in.
 * @param {string} target The link text to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {goog.array.ArrayLike} All matching elements, or an empty list
 * @private
 */
bot.locators.strategies.linkText.many_ = function(win, target, opt_root, isPartial) {
  if (!target) {
    throw Error('No link text specified');
  }

  var doc = goog.dom.getOwnerDocument(win);
  var root = opt_root || doc;

  var domHelper = new goog.dom.DomHelper(doc);
  var allElements = domHelper.getElementsByTagNameAndClass(/*tagName=*/'A');
  return goog.array.filter(allElements, function(element) {
    var text = bot.dom.getVisibleText(element);
    if ((isPartial && text.indexOf(target) != -1) ||
        text == target) {
      return goog.dom.contains(root, element);
    }
  });
};


/**
 * Find an element by using the text value of a link
 * @param {Window} win The DOM window to search in.
 * @param {string} target The link text to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.linkText.single = function(win, target, opt_root) {
  return bot.locators.strategies.linkText.single_(win, target, opt_root, false);
};


/**
 * Find many elements by using the value of the link text
 * @param {Window} win The DOM window to search in.
 * @param {string} target The link text to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {goog.array.ArrayLike} All matching elements, or an empty list
 */
bot.locators.strategies.linkText.many = function(win, target, opt_root) {
  return bot.locators.strategies.linkText.many_(win, target, opt_root, false);
};


/**
 * Find an element by using part of the text value of a link
 * @param {Window} win The DOM window to search in.
 * @param {string} target The link text to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.partialLinkText.single = function(win, target, opt_root) {
  return bot.locators.strategies.linkText.single_(win, target, opt_root, true);
};


/**
 * Find many elements by using part of the value of the link text
 * @param {Window} win The DOM window to search in.
 * @param {string} target The link text to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {goog.array.ArrayLike} All matching elements, or an empty list
 */
bot.locators.strategies.partialLinkText.many = function(win, target, opt_root) {
  return bot.locators.strategies.linkText.many_(win, target, opt_root, true);
};