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

function Utils() {
}

Utils.getUniqueId = function() {
  if (!Utils._generator) {
    Utils._generator = Utils.getService("@mozilla.org/uuid-generator;1", "nsIUUIDGenerator");
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
    var handle = Utils.newInstance("@googlecode.com/webdriver/fxdriver;1", "nsISupports");
    return handle.wrappedJSObject;
}

Utils.getBrowser = function(context) {
    return context.fxbrowser;
};

Utils.getDocument = function(context) {
    return context.fxdocument;
};

Utils.getActiveElement = function(context) {
  var doc = Utils.getDocument(context);

  var element;
  if (doc["activeElement"]) {
    element = doc.activeElement;
  } else {
    var commandDispatcher = Utils.getBrowser(context).ownerDocument.commandDispatcher;

    doc = Utils.getDocument(context);
    element = commandDispatcher.focusedElement;

    if (element && Utils.getDocument(context) != element.ownerDocument)
      element = null;
  }

  // Default to the body
  if (!element) {
    element = Utils.getDocument(context).body;
  }

  return element;
}

function getTextFromNode(node, toReturn, textSoFar, isPreformatted) {
    if (node['tagName'] && node.tagName == "SCRIPT") {
        return [toReturn, textSoFar];
    }
    var children = node.childNodes;

    for (var i = 0; i < children.length; i++) {
        var child = children[i];

        // Do we need to collapse the text so far?
        if (child["tagName"] && child.tagName == "PRE") {
            toReturn += collapseWhitespace(textSoFar);
            textSoFar = "";
            var bits = getTextFromNode(child, toReturn, "", true);
            toReturn += bits[1];
            continue;
        }

        // Or is this just plain text?
        if (child.nodeName == "#text") {
            if (Utils.isDisplayed(child)) {
                var textToAdd = child.nodeValue;
                textToAdd = textToAdd.replace(new RegExp(String.fromCharCode(160), "gm"), " ");
                textSoFar += textToAdd;
            }
            continue;
        }

        // Treat as another child node.
        var bits = getTextFromNode(child, toReturn, textSoFar, false);
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
;

function isBlockLevel(node) {
    if (node["tagName"] && node.tagName == "BR")
        return true;

    try {
        // Should we think about getting hold of the current document?
        return "block" == Utils.getStyleProperty(node, "display");
    } catch (e) {
        return false;
    }
};

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

    // Hidden input elements are, by definition, never displayed
    if (el.tagName == "input" && el.type == "hidden") {
      return false;
    }

    var visibility = Utils.getStyleProperty(el, "visibility");

    var _isDisplayed = function(e) {
      var display = e.ownerDocument.defaultView.getComputedStyle(e, null).getPropertyValue("display");
      if (display == "none") return display;
      if (e && e.parentNode && e.parentNode.style) {
        return _isDisplayed(e.parentNode);
      }
      return undefined;
    };

    var displayed = _isDisplayed(el);

    return displayed != "none" && visibility != "hidden";
};

Utils.getStyleProperty = function(node, propertyName) {
    if (!node)
      return undefined;

    var value = node.ownerDocument.defaultView.getComputedStyle(node, null).getPropertyValue(propertyName);

    // Convert colours to hex if possible
    var raw = /rgb\((\d{1,3}),\s(\d{1,3}),\s(\d{1,3})\)/.exec(value);
    if (raw) {
        var temp = value.substr(0, raw.index);

        var hex = "#";
        for (var i = 1; i <= 3; i++) {
            var colour = (raw[i] - 0).toString(16);
            if (colour.length == 1)
                colour = "0" + colour;
            hex += colour
        }
        hex = hex.toLowerCase();
        value = temp + hex + value.substr(raw.index + raw[0].length);
    }

    if (value == "inherit" && element.parentNode.style) {
      value = Utils.getStyleProperty(node.parentNode, propertyName);
    }

    return value;
};

function collapseWhitespace(textSoFar) {
    return textSoFar.replace(/\s+/g, " ");
}
;

function getPreformattedText(node) {
    var textToAdd = "";
    return getTextFromNode(node, "", textToAdd, true)[1];
}
;

function isWhiteSpace(character) {
    return character == '\n' || character == ' ' || character == '\t' || character == '\r';
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

Utils.addToKnownElements = function(element, context) {
    var doc = Utils.getDocument(context);
    if (!doc.fxdriver_elements) {
        doc.fxdriver_elements = {};
    }

    var id = Utils.getUniqueId();
    doc.fxdriver_elements[id] = element;

    return id;
};

Utils.getElementAt = function(index, context) {
    var doc = Utils.getDocument(context);
    if (doc.fxdriver_elements)
        return doc.fxdriver_elements[index];
    return undefined;
};

Utils.currentDocument = function(context) {
  if (context) {
    return Utils.getDocument(context);
  } else {
    return document;
  }
};

Utils.platform = function(context) {
  if (!this.userAgentPlatformLowercase) {
    var currentWindow = Utils.currentDocument(context).defaultView;
    this.userAgentPlatformLowercase =
        currentWindow.navigator.platform.toLowerCase();
  }

  return this.userAgentPlatformLowercase;
};

Utils.type = function(context, element, text) {
    // Special-case file input elements. This is ugly, but should be okay
    if (element.tagName == "INPUT") {
      var inputtype = element.getAttribute("type");
      if (inputtype && inputtype.toLowerCase() == "file") {
        element.value = text;
        return;
      }
    }

    try {
        const cid = "@openqa.org/nativeevents;1";
        var obj = Components.classes[cid].createInstance();
        obj = obj.QueryInterface(Components.interfaces.nsINativeEvents);

        // This stuff changes between releases. Do as much up-front work in JS as possible
        var retrieval = Utils.newInstance("@mozilla.org/accessibleRetrieval;1", "nsIAccessibleRetrieval");
        var accessible = retrieval.getAccessibleFor(element.ownerDocument);
        Utils.dumpn("Accessible: " + accessible);
        var accessibleDoc = accessible.QueryInterface(Components.interfaces.nsIAccessibleDocument);
        Utils.dumpn("doc: " + accessibleDoc);
        var supports = accessibleDoc.QueryInterface(Components.interfaces.nsISupports);
        Utils.dumpn("supports: " + supports);

        // Now do the native thing.
        obj.sendKeys(supports, text);
        return;
    } catch (err) {
        // We've not got native events here. Simulate.
        Utils.dumpn(err);
    }
};

Utils.fireHtmlEvent = function(context, element, eventName) {
    var doc = Utils.getDocument(context);
    var e = doc.createEvent("HTMLEvents");
    e.initEvent(eventName, true, true);
    element.dispatchEvent(e);
}

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
}

Utils.fireMouseEventOn = function(context, element, eventName) {
    Utils.triggerMouseEvent(element, eventName, 0, 0);
}

Utils.triggerMouseEvent = function(element, eventType, clientX, clientY) {
    var event = element.ownerDocument.createEvent("MouseEvents");
    var view = element.ownerDocument.defaultView;

    event.initMouseEvent(eventType, true, true, view, 1, 0, 0, clientX, clientY, false, false, false, false, 0, element);
    element.dispatchEvent(event);
}

Utils.findDocumentInFrame = function(browser, frameId) {
    var frame = Utils.findFrame(browser, frameId);
    return frame ? frame.document : null;
};

Utils.findFrame = function(browser, frameId) {
    var stringId = "" + frameId;
    var names = stringId.split(".");
    var frame = browser.contentWindow;
    for (var i = 0; i < names.length; i++) {
        // Try a numerical index first
        var index = names[i] - 0;
        if (!isNaN(index)) {
            frame = frame.frames[index];
            if (!frame) {
                return null;
            }
        } else {
            // Fine. Use the name and loop
            var found = false;
            for (var j = 0; j < frame.frames.length; j++) {
                var f = frame.frames[j];
                if (f.name == names[i] || f.frameElement.id == names[i]) {
                    frame = f;
                    found = true;
                    break;
                }
            }

            if (!found) {
                return null;
            }
        }
    }

    return frame;
};

Utils.dumpText = function(text) {
	var consoleService = Utils.getService("@mozilla.org/consoleservice;1", "nsIConsoleService");
	if (consoleService)
		consoleService.logStringMessage(text);
	else
		dump(text);
}

Utils.dumpn = function(text) {
	Utils.dumpText(text + "\n");
}

Utils.dump = function(element) {
    var dump = "=============\n";

    var rows = [];

    dump += "Supported interfaces: ";
    for (var i in Components.interfaces) {
        try {
            var view = element.QueryInterface(Components.interfaces[i]);
            dump += i + ", ";
        } catch (e) {
            // Doesn't support the interface
        }
    }
    dump += "\n------------\n";

    try {
        Utils.dumpProperties(element, rows);
    } catch (e) {
        Utils.dumpText("caught an exception: " + e);
    }

    rows.sort();
    for (var i in rows) {
        dump += rows[i] + "\n";
    }

    dump += "=============\n\n\n";
    Utils.dumpText(dump);
}

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
}

Utils.stackTrace = function() {
    var stack = Components.stack;
    var i = 5;
    var dump = "";
    while (i && stack.caller) {
        stack = stack.caller;
        dump += stack + "\n";
    }

    Utils.dumpText(dump);
}

Utils.getElementLocation = function(element, context) {
    var x = element.offsetLeft;
    var y = element.offsetTop;
    var elementParent = element.offsetParent;
    while (elementParent != null) {
        if(elementParent.tagName == "TABLE") {
            var parentBorder = parseInt(elementParent.border);
            if(isNaN(parentBorder)) {
                var parentFrame = elementParent.getAttribute('frame');
                if(parentFrame != null) {
                    x += 1;
                    y += 1;
                }
            } else if(parentBorder > 0) {
                x += parentBorder;
                y += parentBorder;
            }
        }
        x += elementParent.offsetLeft;
        y += elementParent.offsetTop;
        elementParent = elementParent.offsetParent;
    }

    // Netscape can get confused in some cases, such that the height of the parent is smaller
    // than that of the element (which it shouldn't really be). If this is the case, we need to
    // exclude this element, since it will result in too large a 'top' return value.
    if (element.offsetParent && element.offsetParent.offsetHeight && element.offsetParent.offsetHeight < element.offsetHeight) {
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

Utils.findElementsByXPath = function (xpath, contextNode, context) {
    var doc = Utils.getDocument(context);
    var result = doc.evaluate(xpath, contextNode, null, Components.interfaces.nsIDOMXPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
    var indices = [];
    var element = result.iterateNext();
    while (element) {
        var index = Utils.addToKnownElements(element, context);
        indices.push(index);
        element = result.iterateNext();
    }
    return indices;
};