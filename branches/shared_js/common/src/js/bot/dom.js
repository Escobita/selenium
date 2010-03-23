goog.provide('bot.dom');

goog.require('goog.array');
goog.require('goog.style');

/**
 * Determines whether or not the element has an attribute of the given name,
 * regardless of the value of the attribute.
 *
 * @param {Node} element The element to use
 * @param {string} attributeName The name of the attribute
 * @return {boolean} True if the attribute is present, false otherwise.
 */
bot.dom.hasAttribute = function(element, attributeName) {
  if (goog.isFunction(element['hasAttribute'])) {

    // But if might be an element property....
    if (element.hasAttribute(attributeName)) {
      return true;
    }
  }

  // Handle the case where we lack the hasAttribute method. Normally we'd use
  // the "attributes" array, but this is only present in IE 8 and above. Fall
  // back to simulating the method. Clumsily, but as MS suggest.
  for (var i in element) {
    if (attributeName == i) {
      return true;
    }
  }

  return false;
};

// Used to determine whether we should return a boolean value from getAttribute
bot.dom.booleanAttributes_ = [
  'checked',
  'disabled',
  'readOnly',
  'selected'
];

/**
 * Get the value of the given attribute of the element. This method will
 * endeavour to return consistent values between browsers. For example, boolean
 * values for attributes such as "selected" or "checked" will always be
 * returned as "true" or "false".
 *
 * @param {Node} element The element to use
 * @param {string} attributeName The name of the attribute to return
 * @return {*} The value of the node or "null" if entirely missing
 */
bot.dom.getAttribute = function(element, attributeName) {
  var lattr = attributeName.toLowerCase();

  // TODO(simon): What's the right thing to do here?
  if ('style' == lattr) { return ''; }

  // Commonly looked up attributes that are aliases
  if ('class' == lattr) { attributeName = 'className' }
  if ('readonly' == lattr) { attributeName = 'readOnly' }

  if (!bot.dom.hasAttribute(element, attributeName)) {
    return null;
  }

  var value = element[attributeName] === undefined ?
      element.getAttribute(attributeName) : element[attributeName];

  // Handle common boolean values
  if (goog.array.contains(bot.dom.booleanAttributes_, attributeName)) {
    value = (value != '' && value != 'false' && value != false);
  }

  return value;
};

// TODO(simon): I strongly believe that this function lies
bot.dom.getLocation = function(element) {
  // Position is only relative to the viewport.
  var position = goog.style.getClientPosition(element);

  var scroll = goog.dom.getDomHelper(element).scrollCoord;
  position.x += scroll.x;
  position.y += scroll.y;

  return position;
};