
goog.provide('bot.locators.strategies.name');

goog.require('bot.dom');
goog.require('goog.array');
goog.require('goog.dom');

bot.locators.strategies.name.filter_ = function(target, element, root, doc) {
  if (bot.dom.getAttribute(element, 'name') == target) {
    return root == doc || goog.dom.contains(root, element);
  }
  return false;
};


/**
 * Find an element by the value of the name attribute
 *
 * @param {Window} win The DOM window to search in.
 * @param {string} target The id to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.name.single = function(win, target, opt_root) {
  var doc = goog.dom.getOwnerDocument(win);
  var root = opt_root || doc;

  if (doc['getElementsByName']) {
    var results = doc.getElementsByName(target);

    var length = results.length;
    for (var i = 0; i < length; i++) {
      if (results[i].getAttribute('name') === target) {
        if (root != doc && !goog.dom.contains(root, results[i])) {
          continue;
        }
        return results[i];
      }
    }
  }

  // TODO(simon): potential bug here: what if there's a named element before
  //     one in the form? For now, choose speedier location
  // On some browsers (notably IE) "getElementsByName" only returns elements
  // that are in a form. If we get here, it's possible a matching element is in
  // the DOM, but not in a form. Hunt for it.

  var allElements = doc.getElementsByTagName('*');

  return goog.array.find(allElements, function(element) {
    return bot.locators.strategies.name.filter_(target, element, root, doc);
  })
};

/**
 * Find all elements by the value of the name attribute
 *
 * @param {Window} win The DOM window to search in.
 * @param {string} target The id to search for.
 * @param {Node=} opt_root The node from which to start the search
 * @return {goog.array.ArrayLike} All matching elements, or an empty list
 */
bot.locators.strategies.name.many = function(win, target, opt_root) {
  var doc = goog.dom.getOwnerDocument(win);
  var root = opt_root || doc;

  var allElements = doc.getElementsByTagName('*');

  return goog.array.filter(allElements, function(element) {
    return bot.locators.strategies.name.filter_(target, element, root, doc);
  });
};
