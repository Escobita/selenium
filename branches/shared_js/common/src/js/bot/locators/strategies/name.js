
goog.provide('bot.locators.strategies.name');

goog.require('bot.dom');
goog.require('goog.array');

/**
 * Find an element by the value of the name attribute
 *
 * @param {Window} win The DOM window to search in.
 * @param {string} target The id to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.name = function(win, target) {
  var doc = win.document;

  if (doc['getElementsByName']) {
    var results = doc.getElementsByName(target);

    var length = results.length;
    for (var i = 0; i < length; i++) {
      if (results[i].getAttribute('name') === target) {
        return results[i];
      }
    }
  }

  // On some browsers (notably IE) "getElementsByName" only returns elements
  // that are in a form. If we get here, it's possible a matching element is in
  // the DOM, but not in a form. Hunt for it.

  var allElements = doc.getElementsByTagName('*');

  return goog.array.find(allElements, function(element) {
    return bot.dom.getAttribute(element, 'name') == target;
  })
};
