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
  // the "attributes" array, but this is only present in IE 8 and above.
  // When running in Firefox's chrome mode, the element needs to be cast to
  // the appropriate XPCOM object for the hack below to work. Try and work
  // round this problem.
  return goog.isDef(element[attributeName]);
};

/**
 * Used to determine whether we should return a boolean value from
 * getAttribute. These are mostly derived from the HTML 5 spec.
 *
 * TODO(simon): Consider using a hash instead of an array
 *
 * @private
 */
bot.dom.booleanAttributes_ = [
  'async',
  'autofocus',
  'autoplay',
  'checked',
  'compact',
  'complete',
  'declare',
  'defaultChecked',
  'defaultSelected',
  'defer',
  'disabled',
  'draggable',
  'ended',
  'formNoValidate',
  'hidden',
  'indeterminate',
  'isContentEditable',
  'isMap',
  'loop',
  'multiple',
  'muted',
  'noHref',
  'noResize',
  'noShade',
  'noWrap',
  'noValidate',
  'open',
  'paused',
  'readOnly',
  'required',
  'reversed',
  'scoped',
  'seamless',
  'seeking',
  'selected',
  'visible',
  'willValidate'
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
  if (!element) { throw Error('Element has not been specified'); }
  if (!attributeName) { throw Error('Attribute name must be set'); }

  var lattr = attributeName.toLowerCase();

  // Handle common boolean values
  if (goog.array.contains(bot.dom.booleanAttributes_, attributeName)) {
    if (!bot.dom.hasAttribute(element, attributeName)) {
      return false;
    }

    var value = element[attributeName];
    return !!(value && value != 'false');
  }

  if (!bot.dom.hasAttribute(element, attributeName)) {
    return null;
  }
  
  // TODO(simon): What's the right thing to do here?
  if ('style' == lattr) {
    var style = element['style'];

    // emulating selenium's behaviour for style attribute on IE
    if (style['cssText']) {
      return style['cssText'];
    } else {
      return '';
    }
  }

  // Commonly looked up attributes that are aliases
  if ('class' == lattr) { attributeName = 'className' }
  if ('readonly' == lattr) { attributeName = 'readOnly' }

  return element[attributeName] === undefined ?
      element.getAttribute(attributeName) : element[attributeName];
};

/**
 * Determines whether an element is what a user would call "selected". This boils
 * down to checking to see if either the "checked" or "selected" attribute is true
 *
 * @param {Node} element The element to use
 */
bot.dom.isSelected = function(element) {
  if (bot.dom.hasAttribute(element, 'checked')) {
    return bot.dom.getAttribute(element, 'checked');
  }
  if (bot.dom.hasAttribute(element, 'selected')) {
    return bot.dom.getAttribute(element, 'selected');
  }

  throw Error('Element has neither checked nor selected attributes');
};


/**
 * Returns the parent element of the given node, or null. This is required
 * because the parent node of an element may not be another element.
 *
 * @param {Element} element The element who's parent is desired.
 * @return {?Element} The parent element, if available, null otherwise.
 */
bot.dom.parentElement = function(element) {
  if (!element || !element.parentNode) {
    return null;
  }

  var elem = element.parentNode;

  while (elem.nodeType != goog.dom.NodeType.ELEMENT &&
         !(elem.nodeType == goog.dom.NodeType.DOCUMENT || elem.nodeType == goog.dom.NodeType.DOCUMENT_FRAGMENT)) {
    elem = elem.parentNode;
  }
  return elem && elem.nodeType == goog.dom.NodeType.ELEMENT ? elem : null;
};


/**
 * Determines if an element is located within the HEAD element.
 *
 * @param {Element} element The element to consider.
 * @return {boolean} Whether the element is a descendant of HEAD.
 */
bot.dom.isInHead = function(element) {
  return !!goog.dom.getAncestor(element, function(node) {
    return node.tagName == goog.dom.TagName.HEAD;
  }, true);
};


/**
 * Determines whether an element is what a user would call "displayed". This
 * means that the element not only has height and width greater than 0px, but
 * also that its visibility is not "hidden" and that it's display property is
 * not "none".
 *
 * @param {Element} element The element to consider.
 * @return {boolean} Whether or not the element would be visible.
 */
bot.dom.isDisplayed = function(element) {
  if (!element) {
    throw new Error('No value given for isDisplayed required parameter.');
  }

  var el = element;
  if (el.nodeType != goog.dom.NodeType.ELEMENT) {
    el = bot.dom.parentElement(element);
  }

  if (!el) {
    throw new Error('Node given does not have a parent element.');
  }

  var doc = goog.dom.getOwnerDocument(el);
  var win = goog.dom.getWindow(doc);

  var style = function(elem, style) {
    if (win['getComputedStyle']) {
      return goog.style.getComputedStyle(elem, style);
    }
    return goog.style.getCascadedStyle(elem, style);
  };

  var visible = function(elem) {
    if (elem.tagName.toLowerCase() == 'input' && elem.type.toLowerCase() == 'hidden') {
      return false;
    }
    return style(elem, 'visibility') != 'hidden';
  };

  var displayed = function(elem) {
    if (style(elem, 'display') == 'none') {
      return false;
    }
    var parent = bot.dom.parentElement(elem);
    return !parent || displayed(parent);
  };

  if (!(visible(el) && displayed(el))) {
    return false;
  }

  var size = goog.style.getSize(el);
  return size.height > 0 && size.width > 0;
};


/**
 * @param {Node} node Node to examine.
 * @returns {boolean} Whether or not the node is a block level element.
 * @private
 */
bot.dom.isBlockLevel_ = function(node) {
  if (node['tagName'] && node.tagName == 'BR')
    return true;

  try {
    // Should we think about getting hold of the current document?
    //        return 'block' == Utils.getStyleProperty(node, 'display');
  } catch (e) {
    return false;
  }
  // TODO(simon): Don't hard code this
  return false;
};


/**
 * Get the text from the current node, appending on to the running value if
 * necessary.
 *
 * @param {Node} node Node to get text from.
 * @param {string} toReturn The value that will ultimately be sent to the user.
 * @param {string} textSoFar The current fragment of text.
 * @return {Array.<string>} A tuple of the ultimate text to return, the current
 *     fragment to append and a space that may be needed.
 * @private
 */
bot.dom.getTextFromNode_ = function(node, toReturn, textSoFar) {
  if (node['tagName'] && node.tagName == 'SCRIPT') {
    return [toReturn, textSoFar];
  } else if (node['tagName'] && node.tagName == 'TITLE') {
    return [textSoFar + node.text, ''];
  }
  var children = node.childNodes;

  for (var i = 0; i < children.length; i++) {
    var child = children[i];

    var bits;
    // Do we need to collapse the text so far?
    if (child['tagName'] && child.tagName == 'PRE') {
      toReturn += bot.dom.collapseWhitespace_(textSoFar);
      textSoFar = '';
      bits = bot.dom.getTextFromNode_(child, toReturn, '', true);
      toReturn += bits[1];
      continue;
    }

    // Or is this just plain text?
    if (child.nodeName == '#text') {
      if (bot.dom.isDisplayed(child)) {
        var textToAdd = child.nodeValue;
        textToAdd = textToAdd.replace(new RegExp(String.fromCharCode(160), 'gm'), ' ');
        textSoFar += textToAdd;
      }
      continue;
    }

    // Treat as another child node.
    bits = bot.dom.getTextFromNode_(child, toReturn, textSoFar, false);
    toReturn = bits[0];
    textSoFar = bits[1];
  }

  if (bot.dom.isBlockLevel_(node)) {
    if (node['tagName'] && node.tagName != 'PRE') {
      toReturn += bot.dom.collapseWhitespace_(textSoFar) + '\n';
      textSoFar = '';
    } else {
      toReturn += '\n';
    }
  }
  return [toReturn, textSoFar];
};


/**
 * @param {string} textSoFar The text so far.
 * @private
 */
bot.dom.collapseWhitespace_ = function(textSoFar) {
  // Handle the special case of IE not counting the nbsp character as
  // whitespace.
  return textSoFar.replace(/[\s\xa0]+/g, ' ');
};


/**
 * @param {string} character The single character to consider.
 * @private
 */
bot.dom.isWhiteSpace_ = function(character) {
  // TODO(simon): I can't remember why I didn't use a pattern.
  return character == '\n' || character == ' ' || character == '\t' ||
         character == '\r' || character == '\xa0';
};


/**
 * Get the user-visible text within an element, normalized as much as possible.
 * The returned text will be stripped of the content of any script tags, and so
 * may not match the value of text returned by innerText et al.
 *
 * @param {Element} element The element to read text from.
 * @returns {string} The user-visible text contained within the element.
 */
bot.dom.getVisibleText = function(element) {
  var bits = bot.dom.getTextFromNode_(element, '', '', element.tagName == 'PRE');
  var text = bits[0] + bot.dom.collapseWhitespace_(bits[1]);
  var start = 0;
  while (start < text.length && bot.dom.isWhiteSpace_(text[start])) {
    ++start;
  }
  var end = text.length;
  while (end > start && bot.dom.isWhiteSpace_(text[end - 1])) {
    --end;
  }
  return text.slice(start, end);
};
