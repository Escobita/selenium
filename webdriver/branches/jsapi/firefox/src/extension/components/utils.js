/*
 Copyright 2007-2009 WebDriver committers
 Copyright 2007-2009 Google Inc.
 Portions copyright 2007 ThoughtWorks, Inc

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

function StaleElementError() {
  this.isStaleElementError = true;
}


function Utils() {
}


Utils.getUniqueId = function() {
  if (!Utils._generator) {
    Utils._generator =
    Utils.getService("@mozilla.org/uuid-generator;1", "nsIUUIDGenerator");
  }
  return Utils._generator.generateUUID().toString();
};


Utils.newInstance = function(className, interfaceName) {
  var clazz = Components.classes[className];

  if (!clazz)
    return undefined;

  var iface = Components.interfaces[interfaceName];
  return clazz.createInstance(iface);
};


Utils.getService = function(className, serviceName) {
  var clazz = Components.classes[className];
  if (clazz == undefined) {
    throw new Exception();
  }

  return clazz.getService(Components.interfaces[serviceName]);
};


Utils.getServer = function() {
  var handle =
      Utils.newInstance("@googlecode.com/webdriver/fxdriver;1", "nsISupports");
  return handle.wrappedJSObject;
};


Utils.getActiveElement = function(browser, session) {
  var doc = session.window.document;

  var element;
  if (doc["activeElement"]) {
    element = doc.activeElement;
  } else {
    var commandDispatcher = browser.ownerDocument.commandDispatcher;
    element = commandDispatcher.focusedElement;
    if (element && element.ownerDocument != doc) {
      element = null;
    }
  }

  // Default to the body
  if (!element) {
    element = doc.body;
  }

  return element;
};


function getTextFromNode(node, toReturn, textSoFar) {
  if (node['tagName'] && node.tagName == "SCRIPT") {
    return [toReturn, textSoFar];
  }
  var children = node.childNodes;

  var bits;
  for (var i = 0; i < children.length; i++) {
    var child = children[i];

    // Do we need to collapse the text so far?
    if (child["tagName"] && child.tagName == "PRE") {
      toReturn += collapseWhitespace(textSoFar);
      textSoFar = "";
      bits = getTextFromNode(child, toReturn, "", true);
      toReturn += bits[1];
      continue;
    }

    // Or is this just plain text?
    if (child.nodeName == "#text") {
      if (Utils.isDisplayed(child)) {
        var textToAdd = child.nodeValue;
        textToAdd =
            textToAdd.replace(new RegExp(String.fromCharCode(160), "gm"), " ");
        textSoFar += textToAdd;
      }
      continue;
    }

    // Treat as another child node.
    bits = getTextFromNode(child, toReturn, textSoFar, false);
    toReturn = bits[0];
    textSoFar = bits[1];
  }

  if (isBlockLevel(node)) {
    if (node["tagName"] && node.tagName != "PRE") {
      toReturn += collapseWhitespace(textSoFar) + "\n";
      textSoFar = "";
    } else {
      toReturn += "\n";
    }
  }
  return [toReturn, textSoFar];
}


function isBlockLevel(node) {
  if (node["tagName"] && node.tagName == "BR")
    return true;

  try {
    // Should we think about getting hold of the current document?
    return "block" == Utils.getStyleProperty(node, "display");
  } catch (e) {
    return false;
  }
}


Utils.isInHead = function(element) {
  while (element) {
    if (element.tagName && element.tagName.toLowerCase() == "head") {
      return true;
    }
    try {
      element = element.parentNode;
    } catch (e) {
      // Fine. the DOM has dispeared from underneath us
      return false;
    }
  }

  return false;
};


Utils.isDisplayed = function(element) {
  // Ensure that we're dealing with an element.
  var el = element;
  while (el.nodeType != 1 && !(el.nodeType >= 9 && el.nodeType <= 11)) {
    el = el.parentNode;
  }

  if (!el) {
    return false;
  }

  // Hidden input elements are, by definition, never displayed
  if (el.tagName == "input" && el.type == "hidden") {
    return false;
  }

  var box = Utils.getLocationOnceScrolledIntoView(el);
  // Elements with zero width or height are never displayed
  if (box.width == 0 || box.height == 0) {
    return false;
  }

  var visibility = Utils.getStyleProperty(el, "visibility");

  var _isDisplayed = function(e) {
    var display = e.ownerDocument.defaultView.getComputedStyle(e, null).
        getPropertyValue("display");
    if (display == "none") return display;
    if (e && e.parentNode && e.parentNode.style) {
      return _isDisplayed(e.parentNode);
    }
    return undefined;
  };

  var displayed = _isDisplayed(el);

  return displayed != "none" && visibility != "hidden";
};


/**
 * Gets the computed style of a DOM {@code element}. If the computed style is
 * inherited from the element's parent, the parent will be queried for its
 * style value. If the style value is an RGB color string, it will be converted
 * to hex ("#rrggbb").
 * @param {Element} element The DOM element whose computed style to retrieve.
 * @param {string} propertyName The name of the CSS style proeprty to get.
 * @return {string} The computed style as a string.
 */
Utils.getStyleProperty = function(element, propertyName) {
  if (!element) {
    return undefined;
  }

  var value = element.ownerDocument.defaultView.getComputedStyle(element, null).
      getPropertyValue(propertyName);

  if ('inherit' == value && element.parentNode.style) {
    value = Utils.getStyleProperty(element.parentNode, propertyName);
  }

  // Convert colours to hex if possible
  var raw = /rgb\((\d{1,3}),\s*(\d{1,3}),\s*(\d{1,3})\)/.exec(value);
  if (raw) {
    var hex = (Number(raw[1]) << 16) +
              (Number(raw[2]) << 8) +
              (Number(raw[3]));
    hex = (hex & 0x00ffffff) | 0x1000000;
    value = '#' + hex.toString(16).substring(1);
  }

  return value;
};


function collapseWhitespace(textSoFar) {
  return textSoFar.replace(/\s+/g, " ");
}


function getPreformattedText(node) {
  var textToAdd = "";
  return getTextFromNode(node, "", textToAdd, true)[1];
}


function isWhiteSpace(character) {
  return character == '\n' || character == ' ' || character == '\t' ||
         character == '\r';
}


Utils.getText = function(element) {
  var bits = getTextFromNode(element, "", "", element.tagName == "PRE");
  var text = bits[0] + collapseWhitespace(bits[1]);
  var start = 0;
  while (start < text.length && isWhiteSpace(text[start])) {
    ++start;
  }
  var end = text.length;
  while (end > start && isWhiteSpace(text[end - 1])) {
    --end;
  }
  return text.slice(start, end);
};


Utils.addToKnownElements = function(element, session) {
  var doc = session.window.document;
  if (!doc.fxdriver_elements) {
    doc.fxdriver_elements = {};
  }

  var id = Utils.getUniqueId();
  doc.fxdriver_elements[id] = element;

  return id;
};


Utils.getElementAt = function(index, session) {
  var doc = session.window.document;
  var e = doc.fxdriver_elements ? doc.fxdriver_elements[index] : undefined;
  if (e) {
    // Is this a stale reference?
    var parent = e;
    while (parent && parent != e.ownerDocument.documentElement) {
      parent = parent.parentNode;
    }

    if (parent !== e.ownerDocument.documentElement) {
      // Remove from the cache
      delete doc.fxdriver_elements[index];

      throw new StaleElementError();
    }
  } else {
    throw new StaleElementError();
  }

  return e;
};


Utils.currentDocument = function(session) {
  if (session) {
    return session.window.document;
  } else {
    return document;
  }
};


Utils.platform = function(session) {
  if (!this.userAgentPlatformLowercase) {
    this.userAgentPlatformLowercase =
        session.window.navigator.platform.toLowerCase();
  }

  return this.userAgentPlatformLowercase;
};


Utils.shiftCount = 0;


Utils.getNativeEvents = function() {
  try {
    const cid = "@openqa.org/nativeevents;1";
    var obj = Components.classes[cid].createInstance();
    return obj.QueryInterface(Components.interfaces.nsINativeEvents);
  } catch(e) {
    // Unable to retrieve native events. No biggie, because we fall back to
    // synthesis later
    return undefined;
  }
};


Utils.getNodeForNativeEvents = function(element) {
  try {
    // This stuff changes between releases.
    // Do as much up-front work in JS as possible
    var retrieval = Utils.newInstance(
        "@mozilla.org/accessibleRetrieval;1", "nsIAccessibleRetrieval");
    var accessible = retrieval.getAccessibleFor(element.ownerDocument);
    var accessibleDoc =
        accessible.QueryInterface(Components.interfaces.nsIAccessibleDocument);
    return accessibleDoc.QueryInterface(Components.interfaces.nsISupports);
  } catch(e) {
    // Unable to retrieve the accessible doc
    return undefined;
  }
};


Utils.type = function(element, text, opt_useNativeEvents) {

  // For consistency between native and synthesized events, convert common
  // escape sequences to their Key enum aliases.
  text = text.replace(new RegExp('\b', 'g'), '\uE003').   // DOM_VK_BACK_SPACE
      replace(/\t/g, '\uE004').                           // DOM_VK_TAB
      replace(/(\r\n|\n|\r)/g, '\uE006');                 // DOM_VK_RETURN

  // Special-case file input elements. This is ugly, but should be okay
  if (element.tagName == "INPUT") {
    var inputtype = element.getAttribute("type");
    if (inputtype && inputtype.toLowerCase() == "file") {
      element.value = text;
      return;
    }
  }

  var obj = Utils.getNativeEvents();
  var node = Utils.getNodeForNativeEvents(element);
  const thmgr_cls = Components.classes["@mozilla.org/thread-manager;1"];

  if (opt_useNativeEvents && obj && node && thmgr_cls) {
    // Now do the native thing.
    obj.sendKeys(node, text);

    var hasEvents = {};
    do {
      // This sleep is needed so that Firefox on Linux will manage to process
      // all of the keyboard events before returning control to the caller
      // code (otherwise the caller may not find all of the keystrokes it
      // has entered).

      var threadmgr =
          thmgr_cls.getService(Components.interfaces.nsIThreadManager);
      var thread = threadmgr.currentThread;
      var done = false;
      var the_window = element.ownerDocument.defaultView;
      the_window.setTimeout(function() {
        done = true;
      }, 500);
      while (!done) thread.processNextEvent(true);

      obj.hasUnhandledEvents(node, hasEvents);
    } while (hasEvents.value == true);

    thread.processNextEvent(true);
    return;
  }

  Utils.dumpn("Doing sendKeys in a non-native way...")
  var controlKey = false;
  var shiftKey = false;
  var altKey = false;
  var metaKey = false;

  Utils.shiftCount = 0;

  var upper = text.toUpperCase();

  for (var i = 0; i < text.length; i++) {
    var c = text.charAt(i);

    // NULL key: reset modifier key states, and continue

    if (c == '\uE000') {
      if (controlKey) {
        var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_CONTROL;
        Utils.keyEvent(element, "keyup", kCode, 0,
            controlKey = false, shiftKey, altKey, metaKey);
      }

      if (shiftKey) {
        var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SHIFT;
        Utils.keyEvent(element, "keyup", kCode, 0,
            controlKey, shiftKey = false, altKey, metaKey);
      }

      if (altKey) {
        var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_ALT;
        Utils.keyEvent(element, "keyup", kCode, 0,
            controlKey, shiftKey, altKey = false, metaKey);
      }

      if (metaKey) {
        var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_META;
        Utils.keyEvent(element, "keyup", kCode, 0,
            controlKey, shiftKey, altKey, metaKey = false);
      }

      continue;
    }

    // otherwise decode keyCode, charCode, modifiers ...

    var modifierEvent = "";
    var charCode = 0;
    var keyCode = 0;

    if (c == '\uE001') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_CANCEL;
    } else if (c == '\uE002') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_HELP;
    } else if (c == '\uE003') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_BACK_SPACE;
    } else if (c == '\uE004') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_TAB;
    } else if (c == '\uE005') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_CLEAR;
    } else if (c == '\uE006') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_RETURN;
    } else if (c == '\uE007') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_ENTER;
    } else if (c == '\uE008') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SHIFT;
      shiftKey = !shiftKey;
      modifierEvent = shiftKey ? "keydown" : "keyup";
    } else if (c == '\uE009') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_CONTROL;
      controlKey = !controlKey;
      modifierEvent = controlKey ? "keydown" : "keyup";
    } else if (c == '\uE00A') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_ALT;
      altKey = !altKey;
      modifierEvent = altKey ? "keydown" : "keyup";
    } else if (c == '\uE03D') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_META;
      metaKey = !metaKey;
      modifierEvent = metaKey ? "keydown" : "keyup";
    } else if (c == '\uE00B') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_PAUSE;
    } else if (c == '\uE00C') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_ESCAPE;
    } else if (c == '\uE00D') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SPACE;
      keyCode = charCode = ' '.charCodeAt(0);  // printable
    } else if (c == '\uE00E') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_PAGE_UP;
    } else if (c == '\uE00F') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_PAGE_DOWN;
    } else if (c == '\uE010') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_END;
    } else if (c == '\uE011') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_HOME;
    } else if (c == '\uE012') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_LEFT;
    } else if (c == '\uE013') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_UP;
    } else if (c == '\uE014') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_RIGHT;
    } else if (c == '\uE015') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_DOWN;
    } else if (c == '\uE016') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_INSERT;
    } else if (c == '\uE017') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_DELETE;
    } else if (c == '\uE018') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SEMICOLON;
      charCode = ';'.charCodeAt(0);
    } else if (c == '\uE019') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_EQUALS;
      charCode = '='.charCodeAt(0);
    } else if (c == '\uE01A') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD0;
      charCode = '0'.charCodeAt(0);
    } else if (c == '\uE01B') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD1;
      charCode = '1'.charCodeAt(0);
    } else if (c == '\uE01C') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD2;
      charCode = '2'.charCodeAt(0);
    } else if (c == '\uE01D') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD3;
      charCode = '3'.charCodeAt(0);
    } else if (c == '\uE01E') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD4;
      charCode = '4'.charCodeAt(0);
    } else if (c == '\uE01F') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD5;
      charCode = '5'.charCodeAt(0);
    } else if (c == '\uE020') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD6;
      charCode = '6'.charCodeAt(0);
    } else if (c == '\uE021') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD7;
      charCode = '7'.charCodeAt(0);
    } else if (c == '\uE022') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD8;
      charCode = '8'.charCodeAt(0);
    } else if (c == '\uE023') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_NUMPAD9;
      charCode = '9'.charCodeAt(0);
    } else if (c == '\uE024') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_MULTIPLY;
      charCode = '*'.charCodeAt(0);
    } else if (c == '\uE025') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_ADD;
      charCode = '+'.charCodeAt(0);
    } else if (c == '\uE026') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SEPARATOR;
      charCode = ','.charCodeAt(0);
    } else if (c == '\uE027') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SUBTRACT;
      charCode = '-'.charCodeAt(0);
    } else if (c == '\uE028') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_DECIMAL;
      charCode = '.'.charCodeAt(0);
    } else if (c == '\uE029') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_DIVIDE;
      charCode = '/'.charCodeAt(0);
    } else if (c == '\uE031') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F1;
    } else if (c == '\uE032') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F2;
    } else if (c == '\uE033') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F3;
    } else if (c == '\uE034') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F4;
    } else if (c == '\uE035') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F5;
    } else if (c == '\uE036') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F6;
    } else if (c == '\uE037') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F7;
    } else if (c == '\uE038') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F8;
    } else if (c == '\uE039') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F9;
    } else if (c == '\uE03A') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F10;
    } else if (c == '\uE03B') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F11;
    } else if (c == '\uE03C') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_F12;
    } else if (c == ',' || c == '<') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_COMMA;
      charCode = c.charCodeAt(0);
    } else if (c == '.' || c == '>') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_PERIOD;
      charCode = c.charCodeAt(0);
    } else if (c == '/' || c == '?') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SLASH;
      charCode = text.charCodeAt(i);
    } else if (c == '`' || c == '~') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_BACK_QUOTE;
      charCode = c.charCodeAt(0);
    } else if (c == '{' || c == '[') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_OPEN_BRACKET;
      charCode = c.charCodeAt(0);
    } else if (c == '\\' || c == '|') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_BACK_SLASH;
      charCode = c.charCodeAt(0);
    } else if (c == '}' || c == ']') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_CLOSE_BRACKET;
      charCode = c.charCodeAt(0);
    } else if (c == '\'' || c == '"') {
      keyCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_QUOTE;
      charCode = c.charCodeAt(0);
    } else {
      keyCode = upper.charCodeAt(i);
      charCode = text.charCodeAt(i);
    }

    // generate modifier key event if needed, and continue

    if (modifierEvent) {
      Utils.keyEvent(element, modifierEvent, keyCode, 0,
          controlKey, shiftKey, altKey, metaKey);
      continue;
    }

    // otherwise, shift down if needed

    var needsShift = false;
    if (charCode) {
      needsShift = /[A-Z\!\$\^\*\(\)\+\{\}\:\?\|~@#%&_"<>]/.test(c);
    }

    if (needsShift && !shiftKey) {
      var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SHIFT;
      Utils.keyEvent(element, "keydown", kCode, 0,
          controlKey, true, altKey, metaKey);
      Utils.shiftCount += 1;
    }

    // generate key[down/press/up] for key

    var pressCode = keyCode;
    if (charCode >= 32 && charCode < 127) {
      pressCode = 0;
      if (!needsShift && shiftKey && charCode > 32) {
        // If typing a lowercase character key and the shiftKey is down, the
        // charCode should be mapped to the shifted key value. This assumes
        // a default 104 international keyboard layout.
        if (charCode >= 97 && charCode <= 122) {
          charCode = charCode + 65 - 97;  // [a-z] -> [A-Z]
        } else {
          var mapFrom = '`1234567890-=[]\\;\',./';
          var mapTo = '~!@#$%^&*()_+{}|:"<>?';

          var value = String.fromCharCode(charCode).
              replace(/([\[\\\.])/g, '\\$1');
          var index = mapFrom.search(value);
          if (index >= 0) {
            charCode = mapTo.charCodeAt(index);
          }
        }
      }
    }

    var accepted =
        Utils.keyEvent(element, "keydown", keyCode, 0,
            controlKey, needsShift || shiftKey, altKey, metaKey);

    Utils.keyEvent(element, "keypress", pressCode, charCode,
        controlKey, needsShift || shiftKey, altKey, metaKey, !accepted);

    Utils.keyEvent(element, "keyup", keyCode, 0,
        controlKey, needsShift || shiftKey, altKey, metaKey);

    // shift up if needed

    if (needsShift && !shiftKey) {
      var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SHIFT;
      Utils.keyEvent(element, "keyup", kCode, 0,
          controlKey, false, altKey, metaKey);
    }
  }

  // exit cleanup: keyup active modifier keys

  if (controlKey) {
    var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_CONTROL;
    Utils.keyEvent(element, "keyup", kCode, 0,
        controlKey = false, shiftKey, altKey, metaKey);
  }

  if (shiftKey) {
    var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_SHIFT;
    Utils.keyEvent(element, "keyup", kCode, 0,
        controlKey, shiftKey = false, altKey, metaKey);
  }

  if (altKey) {
    var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_ALT;
    Utils.keyEvent(element, "keyup", kCode, 0,
        controlKey, shiftKey, altKey = false, metaKey);
  }

  if (metaKey) {
    var kCode = Components.interfaces.nsIDOMKeyEvent.DOM_VK_META;
    Utils.keyEvent(element, "keyup", kCode, 0,
        controlKey, shiftKey, altKey, metaKey = false);
  }
};


Utils.keyEvent = function(element, type, keyCode, charCode, controlState,
                          shiftState, altState, metaState,
                          shouldPreventDefault) {
  var preventDefault =
      shouldPreventDefault == undefined ? false : shouldPreventDefault;

  var ownerDoc = element.ownerDocument;
  var keyboardEvent = ownerDoc.createEvent('KeyEvents');
  var currentView = ownerDoc.defaultView;

  keyboardEvent.initKeyEvent(
      type, //  in DOMString typeArg,
      true, //  in boolean canBubbleArg
      true, //  in boolean cancelableArg
      currentView, //  in nsIDOMAbstractView viewArg
      controlState, //  in boolean ctrlKeyArg
      altState, //  in boolean altKeyArg
      shiftState, //  in boolean shiftKeyArg
      metaState, //  in boolean metaKeyArg
      keyCode, //  in unsigned long keyCodeArg
      charCode);    //  in unsigned long charCodeArg

  if (preventDefault) {
    keyboardEvent.preventDefault();
  }

  return element.dispatchEvent(keyboardEvent);
};


Utils.fireHtmlEvent = function(element, eventName) {
  var doc = element.ownerDocument;
  var e = doc.createEvent("HTMLEvents");
  e.initEvent(eventName, true, true);
  element.dispatchEvent(e);
};


Utils.findForm = function(element) {
  // Are we already on an element that can be used to submit the form?
  try {
    element.QueryInterface(Components.interfaces.nsIDOMHTMLButtonElement);
    return element;
  } catch(e) {
  }

  try {
    var input = element.QueryInterface(Components.interfaces.nsIDOMHTMLInputElement);
    if (input.type == "image" || input.type == "submit")
      return input;
  } catch(e) {
  }

  var form = element;
  while (form) {
    if (form["submit"])
      return form;
    form = form.parentNode;
  }
  return undefined;
};


Utils.fireMouseEventOn = function(element, eventName) {
  Utils.triggerMouseEvent(element, eventName, 0, 0);
};


Utils.triggerMouseEvent = function(element, eventType, clientX, clientY) {
  var event = element.ownerDocument.createEvent("MouseEvents");
  var view = element.ownerDocument.defaultView;

  event.initMouseEvent(eventType, true, true, view, 1, 0, 0, clientX, clientY,
      false, false, false, false, 0, element);
  element.dispatchEvent(event);
};


Utils.dumpText = function(text) {
  var consoleService = Utils.getService(
      "@mozilla.org/consoleservice;1", "nsIConsoleService");
  if (consoleService)
    consoleService.logStringMessage(text);
  else
    dump(text);
};


Utils.dumpn = function(text) {
  Utils.dumpText(text + "\n");
};


Utils.dump = function(obj) {
  var dump = [
    obj.toString(),
    "=============",
    'Supported interfaces:',
  ];
  for (var i in Components.interfaces) {
    try {
      obj.QueryInterface(Components.interfaces[i]);
      dump.push(i);
    } catch (e) {
      // Doesn't support the interface
    }
  }
  dump.push('------------');

  try {
    Utils.dumpProperties(element, dump);
  } catch (e) {
    Utils.dumpText("caught an exception: " + e);
  }

  dump.push("=============");
  Utils.dumpn(dump.join('\n'));
};


Utils.dumpProperties = function(view, rows) {
  for (var i in view) {
    var value = "\t" + i + ": ";
    try {
      if (typeof(view[i]) == typeof(Function)) {
        value += " function()";
      } else {
        value += String(view[i]);
      }
    } catch (e) {
      value += " Cannot obtain value";
    }

    rows.push(value);
  }
};


Utils.stackTrace = function() {
  var stack = Components.stack;
  var i = 5;
  var dump = "";
  while (i && stack.caller) {
    stack = stack.caller;
    dump += stack + "\n";
  }

  Utils.dumpText(dump);
};


Utils.getElementLocation = function(element) {
  var x = element.offsetLeft;
  var y = element.offsetTop;
  var elementParent = element.offsetParent;
  while (elementParent != null) {
    if (elementParent.tagName == "TABLE") {
      var parentBorder = parseInt(elementParent.border);
      if (isNaN(parentBorder)) {
        var parentFrame = elementParent.getAttribute('frame');
        if (parentFrame != null) {
          x += 1;
          y += 1;
        }
      } else if (parentBorder > 0) {
        x += parentBorder;
        y += parentBorder;
      }
    }
    x += elementParent.offsetLeft;
    y += elementParent.offsetTop;
    elementParent = elementParent.offsetParent;
  }

  // Netscape can get confused in some cases, such that the height of the parent
  // is smaller than that of the element (which it shouldn't really be). If this
  // is the case, we need to exclude this element, since it will result in too
  // large a 'top' return value.
  if (element.offsetParent && element.offsetParent.offsetHeight &&
      element.offsetParent.offsetHeight < element.offsetHeight) {
    // skip the parent that's too small
    element = element.offsetParent.offsetParent;
  } else {
    // Next up...
    element = element.offsetParent;
  }
  var location = new Object();
  location.x = x;
  location.y = y;
  return location;
};


Utils.findElementsByXPath = function (xpath, contextNode, session) {
  var doc = session.window.document;
  var result = doc.evaluate(xpath, contextNode, null,
      Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
  var indices = [];
  var element = result.iterateNext();
  while (element) {
    var index = Utils.addToKnownElements(element, session);
    indices.push(index);
    element = result.iterateNext();
  }
  return indices;
};


Utils.getLocationOnceScrolledIntoView = function(element) {
  element.scrollIntoView(true);

  var retrieval = Utils.newInstance(
      "@mozilla.org/accessibleRetrieval;1", "nsIAccessibleRetrieval");

  try {
    element = element.wrappedJSObject ? element.wrappedJSObject : element;

    var clientRect = element.getBoundingClientRect();

    // Firefox 3.5
    if (clientRect['width']) {
      return {
        x : clientRect.left + 3,
        y : clientRect.top,
        width: clientRect.width,
        height: clientRect.height
      };
    }

    // Firefox 3.0
    Utils.dumpn("Falling back to firefox3 mechanism");
    var accessible = retrieval.getAccessibleFor(element);
    var x = {}, y = {}, width = {}, height = {};
    accessible.getBounds(x, y, width, height);

    return {
      x : clientRect.left + 3,
      y : clientRect.top,
      width: width.value,
      height: height.value
    };
  } catch(e) {
    Utils.dumpn(e);
    // Element doesn't have an accessibility node
  }

  // Firefox 2.0

  // Fallback. Use the (deprecated) method to find out where the element is in
  // the viewport. This should be fine to use because we only fall down this
  // code path on older versions of Firefox (I think!)
  var theDoc = element.ownerDocument;
  var box = theDoc.getBoxObjectFor(element);

  // We've seen cases where width is 0, despite the element actually having
  // children with width.  This happens particularly with GWT.
  if (box.width == 0 || box.height == 0) {
    // Check the child, and hope the user doesn't nest this stuff. Walk the
    // children til we find an element. At this point, we know that width and
    // height are a polite fiction
    for (var i = 0; i < element.childNodes.length; i++) {
      var c = element.childNodes[i];
      if (c.nodeType == 1) {
        Utils.dumpn(
            "Width and height are ficticious values, based on child node");
        box = theDoc.getBoxObjectFor(c);
        break;
      }
    }
  }

  return {
    x : box.x + 3,
    y : box.y,
    width: box.width,
    height: box.height
  };
};


Utils.unwrapParameters = function(wrappedParameters, resultArray, session) {
  while (wrappedParameters && wrappedParameters.length > 0) {
    var t = wrappedParameters.shift();

    if (t != null && t.length !== undefined && t.length != null && (t['type']
        === undefined || t['type'] == null)) {
      var innerArray = [];
      Utils.unwrapParameters(t, innerArray);
      resultArray.push(innerArray);
      return;
    }

    if (t['type'] == "ELEMENT") {
      var element = Utils.getElementAt(t['value'], session);
      t['value'] = element.wrappedJSObject ? element.wrappedJSObject : element;
    }

    resultArray.push(t['value']);
  }
};


Utils.wrapResult = function(result, session) {
  // Sophisticated.
  if (null === result || undefined === result) {
    return {resultType: "NULL"};
  } else if (result['tagName']) {
    return {resultType: "ELEMENT",
            response: Utils.addToKnownElements(result, session)};
  } else if (result.constructor.toString().indexOf("Array") != -1) {
    var array = [];
    for (var i = 0; i < result.length; i++) {
      array.push(Utils.wrapResult(result[i], session));
    }
    return {resultType: "ARRAY", response: array};
  } else {
    return {resultType: "OTHER", response: result};
  }
}
