
goog.provide('bot.locators.strategies.id');

/**
 * Find an element by using the value of the ID attribute
 * @param {Window} win The DOM window to search in.
 * @param {string} target The id to search for.
 * @return {?Element} The first matching element found in the DOM, or null if no
 *     such element could be found.
 */
bot.locators.strategies.id = function(win, target) {
  var doc = win.document;

  var e = doc.getElementById(target);
  if (!e) {
    return null;
  }

  // On IE getting by ID returns the first match by id _or_ name.
  if (e.getAttribute('id') == target) {
    return e;
  }
                                                                                                         
  var elements = doc.getElementsByTagName('*');

  var length = elements.length;
  for (var i = 0; i < length; i++) {
    var element = elements[i];

    // Lifted from selenium's original finding.
    if (element.tagName.toLowerCase() == 'form') {
      if (element.attributes['id'].nodeValue == target) {
        return element;
      }
    } else if (element.getAttribute('id') == target) {
      return element;
    }
  }

  return null;
};
