
goog.provide('bot.locators.strategies.id');

bot.locators.strategies.by_id = function(win, target) {
  var doc = win.document;

  var e = doc.getElementById(target);
  if (!e) {
    return undefined;
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
      if (element.attributes['id'].nodeValue == identifier) {
        return element;
      }
    }
    else if (element.getAttribute('id') == identifier) {
      return element;
    }
  }

  return undefined;
};
