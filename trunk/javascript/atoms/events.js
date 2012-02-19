// Copyright 2010 WebDriver committers
// Copyright 2010 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @fileoverview Functions to do with firing and simulating events.
 *
 */


goog.provide('bot.events');
goog.provide('bot.events.EventType');

goog.require('bot.Error');
goog.require('bot.ErrorCode');
goog.require('bot.userAgent');
goog.require('goog.array');
goog.require('goog.dom');
goog.require('goog.userAgent');
goog.require('goog.userAgent.product');


/**
 * Whether the browser supports the construction of touch events.
 *
 * @const
 * @type {boolean}
 */
bot.events.SUPPORTS_TOUCH_EVENTS = !goog.userAgent.IE && !goog.userAgent.OPERA;


/**
 * Whether the browser supports a native touch api.
 *
 * @const
 * @type {boolean}
 * @private
 */
bot.events.BROKEN_TOUCH_API_ = (function() {
  if (goog.userAgent.product.ANDROID) {
    // Native touch api supported starting in version 4.0 (Ice Cream Sandwich).
    return !bot.userAgent.isProductVersion(4);
  }
  return !bot.userAgent.IOS;
})();


/**
 * Arguments to initialize an event.
 *
 * @typedef {bot.events.MouseArgs|bot.events.KeyboardArgs|bot.events.TouchArgs}
 */
bot.events.EventArgs;


/**
 * Arguments to initialize a mouse event.
 *
 * @typedef {{clientX: number,
 *            clientY: number,
 *            button: number,
 *            altKey: boolean,
 *            ctrlKey: boolean,
 *            shiftKey: boolean,
 *            metaKey: boolean,
 *            relatedTarget: Element,
 *            wheelDelta: number}}
 */
bot.events.MouseArgs;


/**
 * Arguments to initialize a keyboard event.
 *
 * @typedef {{keyCode: number,
 *            charCode: number,
 *            altKey: boolean,
 *            ctrlKey: boolean,
 *            shiftKey: boolean,
 *            metaKey: boolean,
 *            preventDefault: boolean}}
 */
bot.events.KeyboardArgs;


/**
 * Argument to initialize a touch event.
 *
 * @typedef {{touches: !Array.<bot.events.Touch>,
 *            targetTouches: !Array.<bot.events.Touch>,
 *            changedTouches: !Array.<bot.events.Touch>,
 *            altKey: boolean,
 *            ctrlKey: boolean,
 *            shiftKey: boolean,
 *            metaKey: boolean,
 *            relatedTarget: Element,
 *            scale: number,
 *            rotation: number}}
 */
bot.events.TouchArgs;


/**
 * @typedef {{identifier: number,
 *            screenX: number,
 *            screenY: number,
 *            clientX: number,
 *            clientY: number,
 *            pageX: number,
 *            pageY: number}}
 */
bot.events.Touch;



/**
 * Factory for event objects of a specific type.
 *
 * @constructor
 * @param {string} type Type of the created events.
 * @param {boolean} bubbles Whether the created events bubble.
 * @param {boolean} cancelable Whether the created events are cancelable.
 * @private
 */
bot.events.EventFactory_ = function(type, bubbles, cancelable) {
  /**
   * @type {string}
   * @private
   */
  this.type_ = type;

  /**
   * @type {boolean}
   * @private
   */
  this.bubbles_ = bubbles;

  /**
   * @type {boolean}
   * @private
   */
  this.cancelable_ = cancelable;
};


/**
 * Creates an event.
 *
 * @param {!Element} target Target element of the event.
 * @param {bot.events.EventArgs=} opt_args Event arguments.
 * @return {!Event} Newly created event.
 */
bot.events.EventFactory_.prototype.create = function(target, opt_args) {
  var doc = goog.dom.getOwnerDocument(target);
  var event;

  if (bot.userAgent.IE_DOC_PRE9) {
    event = doc.createEventObject();
  } else {
    event = doc.createEvent('HTMLEvents');
    event.initEvent(this.type_, this.bubbles_, this.cancelable_);
  }

  return event;
};


/**
 * Overriding toString to return the unique type string improves debugging,
 * and it allows event types to be mapped in JS objects without collisions.
 *
 * @return {string} String representation of the event type.
 */
bot.events.EventFactory_.prototype.toString = function() {
  return this.type_;
};



/**
 * Factory for mouse event objects of a specific type.
 *
 * @constructor
 * @param {string} type Type of the created events.
 * @param {boolean} bubbles Whether the created events bubble.
 * @param {boolean} cancelable Whether the created events are cancelable.
 * @extends {bot.events.EventFactory_}
 * @private
 */
bot.events.MouseEventFactory_ = function(type, bubbles, cancelable) {
  goog.base(this, type, bubbles, cancelable);
};
goog.inherits(bot.events.MouseEventFactory_, bot.events.EventFactory_);


/**
 * @inheritDoc
 */
bot.events.MouseEventFactory_.prototype.create = function(target, opt_args) {
  // Only Gecko supports the mouse pixel scroll event.
  if (!goog.userAgent.GECKO && this == bot.events.EventType.MOUSEPIXELSCROLL) {
    throw new bot.Error(bot.ErrorCode.UNSUPPORTED_OPERATION,
        'Browser does not support a mouse pixel scroll event.');
  }

  var args = (/** @type {!bot.events.MouseArgs} */ opt_args);
  var doc = goog.dom.getOwnerDocument(target);
  var event;

  if (bot.userAgent.IE_DOC_PRE9) {
    event = doc.createEventObject();
    event.altKey = args.altKey;
    event.ctrlKey = args.ctrlKey;
    event.metaKey = args.metaKey;
    event.shiftKey = args.shiftKey;
    event.button = args.button;

    // NOTE: ie8 does a strange thing with the coordinates passed in the event:
    // - if offset{X,Y} coordinates are specified, they are also used for
    //   client{X,Y}, event if client{X,Y} are also specified.
    // - if only client{X,Y} are specified, they are also used for offset{x,y}
    // Thus, for ie8, it is impossible to set both offset and client
    // and have them be correct when they come out on the other side.
    event.clientX = args.clientX;
    event.clientY = args.clientY;

    // Sets a property of the event object using Object.defineProperty.
    // Some readonly properties of the IE event object can only be set this way.
    function setEventProperty(prop, value) {
      Object.defineProperty(event, prop, {
        get: function() {
          return value;
        }
      });
    }

    // IE has fromElement and toElement properties, no relatedTarget property.
    // IE does not allow fromElement and toElement to be set directly, but
    // Object.defineProperty can redefine them, when it is available. Do not
    // use Object.defineProperties (plural) because it is even less supported.
    // If defineProperty is unavailable, fall back to setting the relatedTarget,
    // which many event frameworks, including jQuery and Closure, forgivingly
    // pass on as the relatedTarget on their event object abstraction.
    if (this == bot.events.EventType.MOUSEOUT ||
        this == bot.events.EventType.MOUSEOVER) {
      if (Object.defineProperty) {
        var out = (this == bot.events.EventType.MOUSEOUT);
        setEventProperty('fromElement', out ? target : args.relatedTarget);
        setEventProperty('toElement', out ? args.relatedTarget : target);
      } else {
        event.relatedTarget = args.relatedTarget;
      }
    }

    // IE does not allow the wheelDelta property to be set directly,
    // so we can only do it where defineProperty is supported.
    if (this == bot.events.EventType.MOUSEWHEEL && Object.defineProperty) {
      setEventProperty('wheelDelta', args.wheelDelta);
    }
  } else {
    var view = goog.dom.getWindow(doc);
    event = doc.createEvent('MouseEvents');
    var detail = 1;

    // All browser but Firefox provide the wheelDelta value in the event.
    // Firefox provides the scroll amount in the detail field, where it has the
    // opposite polarity of the wheelDelta (upward scroll is negative) and is a
    // factor of 40 less than the wheelDelta value. Opera provides both values.
    // The wheelDelta value is normally some multiple of 40.
    if (this == bot.events.EventType.MOUSEWHEEL) {
      if (!goog.userAgent.GECKO) {
        event.wheelDelta = args.wheelDelta;
      }
      if (goog.userAgent.GECKO || goog.userAgent.OPERA) {
        detail = args.wheelDelta / -40;
      }
    }

    // Only Gecko supports a mouse pixel scroll event, so we use it as the
    // "standard" and pass it along as is as the "detail" of the event.
    if (goog.userAgent.GECKO && this == bot.events.EventType.MOUSEPIXELSCROLL) {
      detail = args.wheelDelta;
    }

    event.initMouseEvent(this.type_, this.bubbles_, this.cancelable_, view,
        detail, /*screenX*/ 0, /*screenY*/ 0, args.clientX, args.clientY,
        args.ctrlKey, args.altKey, args.shiftKey, args.metaKey, args.button,
        args.relatedTarget);
  }

  return event;
};



/**
 * Factory for keyboard event objects of a specific type.
 *
 * @constructor
 * @param {string} type Type of the created events.
 * @param {boolean} bubbles Whether the created events bubble.
 * @param {boolean} cancelable Whether the created events are cancelable.
 * @extends {bot.events.EventFactory_}
 * @private
 */
bot.events.KeyboardEventFactory_ = function(type, bubbles, cancelable) {
  goog.base(this, type, bubbles, cancelable);
};
goog.inherits(bot.events.KeyboardEventFactory_, bot.events.EventFactory_);


/**
 * @inheritDoc
 */
bot.events.KeyboardEventFactory_.prototype.create = function(target, opt_args) {
  var args = (/** @type {!bot.events.KeyboardArgs} */ opt_args);
  var doc = goog.dom.getOwnerDocument(target);
  var event;

  if (goog.userAgent.GECKO) {
    var view = goog.dom.getWindow(doc);
    var keyCode = args.charCode ? 0 : args.keyCode;
    event = doc.createEvent('KeyboardEvent');
    event.initKeyEvent(this.type_, this.bubbles_, this.cancelable_, view,
        args.ctrlKey, args.altKey, args.shiftKey, args.metaKey, keyCode,
        args.charCode);
    // https://bugzilla.mozilla.org/show_bug.cgi?id=501496
    if (this.type_ == bot.events.EventType.KEYPRESS && args.preventDefault) {
      event.preventDefault();
    }
  } else {
    if (bot.userAgent.IE_DOC_PRE9) {
      event = doc.createEventObject();
    } else {  // WebKit, Opera, and IE 9+ in Standards mode.
      event = doc.createEvent('Events');
      event.initEvent(this.type_, this.bubbles_, this.cancelable_);
    }
    event.altKey = args.altKey;
    event.ctrlKey = args.ctrlKey;
    event.metaKey = args.metaKey;
    event.shiftKey = args.shiftKey;
    event.keyCode = args.charCode || args.keyCode;
    if (goog.userAgent.WEBKIT) {
      event.charCode = (this == bot.events.EventType.KEYPRESS) ?
          event.keyCode : 0;
    }
  }

  return event;
};



/**
 * Factory for touch event objects of a specific type.
 *
 * @constructor
 * @param {string} type Type of the created events.
 * @param {boolean} bubbles Whether the created events bubble.
 * @param {boolean} cancelable Whether the created events are cancelable.
 * @extends {bot.events.EventFactory_}
 * @private
 */
bot.events.TouchEventFactory_ = function(type, bubbles, cancelable) {
  goog.base(this, type, bubbles, cancelable);
};
goog.inherits(bot.events.TouchEventFactory_, bot.events.EventFactory_);


/**
 * @inheritDoc
 */
bot.events.TouchEventFactory_.prototype.create = function(target, opt_args) {
  if (!bot.events.SUPPORTS_TOUCH_EVENTS) {
    throw new bot.Error(bot.ErrorCode.UNSUPPORTED_OPERATION,
        'Browser does not support firing touch events.');
  }

  var args = (/** @type {!bot.events.TouchArgs} */ opt_args);
  var doc = goog.dom.getOwnerDocument(target);
  var view = goog.dom.getWindow(doc);

  // Creates a TouchList, using native touch Api, for touch events.
  function createNativeTouchList(touchListArgs) {
    var touches = goog.array.map(touchListArgs, function(touchArg) {
      return doc.createTouch(view, target, touchArg.identifier,
          touchArg.pageX, touchArg.pageY, touchArg.screenX, touchArg.screenY);
    });

    return doc.createTouchList.apply(doc, touches);
  }

  // Creates a TouchList, using simulated touch Api, for touch events.
  function createGenericTouchList(touchListArgs) {
    var touches = goog.array.map(touchListArgs, function(touchArg) {
      // The target field is not part of the W3C spec, but both android and iOS
      // add the target field to each touch.
      return {
        identifier: touchArg.identifier,
        screenX: touchArg.screenX,
        screenY: touchArg.screenY,
        clientX: touchArg.clientX,
        clientY: touchArg.clientY,
        pageX: touchArg.pageX,
        pageY: touchArg.pageY,
        target: target
      };
    });
    touches.item = function(i) {
      return touches[i];
    };
    return touches;
  }

  function createTouchList(touches) {
    return bot.events.BROKEN_TOUCH_API_ ?
        createGenericTouchList(touches) :
        createNativeTouchList(touches);
  }

  // As a performance optimization, reuse the created touchlist when the lists
  // are the same, which is often the case in practice.
  var changedTouches = createTouchList(args.changedTouches);
  var touches = (args.touches == args.changedTouches) ?
      changedTouches : createTouchList(args.touches);
  var targetTouches = (args.targetTouches == args.changedTouches) ?
      changedTouches : createTouchList(args.targetTouches);

  var event;
  if (bot.events.BROKEN_TOUCH_API_) {
    event = doc.createEvent('MouseEvents');
    event.initMouseEvent(this.type_, this.bubbles_, this.cancelable_, view,
        /*detail*/ 1, /*screenX*/ 0, /*screenY*/ 0, args.clientX, args.clientY,
        args.ctrlKey, args.altKey, args.shiftKey, args.metaKey, /*button*/ 0,
        args.relatedTarget);
    event.touches = touches;
    event.targetTouches = targetTouches;
    event.changedTouches = changedTouches;
    event.scale = args.scale;
    event.rotation = args.rotation;
  } else {
    event = doc.createEvent('TouchEvent');
    if (goog.userAgent.product.ANDROID) {
      // Android's initTouchEvent method is not compliant with the W3C spec.
      event.initTouchEvent(touches, targetTouches, changedTouches,
          this.type_, view, /*screenX*/ 0, /*screenY*/ 0, args.clientX,
          args.clientY, args.ctrlKey, args.altKey, args.shiftKey, args.metaKey);
    } else {
      event.initTouchEvent(this.type_, this.bubbles_, this.cancelable_, view,
          /*detail*/ 1, /*screenX*/ 0, /*screenY*/ 0, args.clientX,
          args.clientY, args.ctrlKey, args.altKey, args.shiftKey, args.metaKey,
          touches, targetTouches, changedTouches, args.scale, args.rotation);
    }
    event.relatedTarget = args.relatedTarget;
  }

  return event;
};


/**
 * The types of events this modules supports firing.
 *
 * <p>To see which events bubble and are cancelable, see:
 * http://en.wikipedia.org/wiki/DOM_events
 *
 * @enum {!Object}
 */
bot.events.EventType = {
  BLUR: new bot.events.EventFactory_('blur', false, false),
  CHANGE: new bot.events.EventFactory_('change', true, false),
  FOCUS: new bot.events.EventFactory_('focus', false, false),
  INPUT: new bot.events.EventFactory_('input', false, false),
  PROPERTYCHANGE: new bot.events.EventFactory_('propertychange', false, false),
  SELECT: new bot.events.EventFactory_('select', true, false),
  SUBMIT: new bot.events.EventFactory_('submit', true, true),
  TEXTINPUT: new bot.events.EventFactory_('textInput', true, true),

  // Mouse events.
  CLICK: new bot.events.MouseEventFactory_('click', true, true),
  CONTEXTMENU: new bot.events.MouseEventFactory_('contextmenu', true, true),
  DBLCLICK: new bot.events.MouseEventFactory_('dblclick', true, true),
  MOUSEDOWN: new bot.events.MouseEventFactory_('mousedown', true, true),
  MOUSEMOVE: new bot.events.MouseEventFactory_('mousemove', true, false),
  MOUSEOUT: new bot.events.MouseEventFactory_('mouseout', true, true),
  MOUSEOVER: new bot.events.MouseEventFactory_('mouseover', true, true),
  MOUSEUP: new bot.events.MouseEventFactory_('mouseup', true, true),
  MOUSEWHEEL: new bot.events.MouseEventFactory_(
      goog.userAgent.GECKO ? 'DOMMouseScroll' : 'mousewheel', true, true),
  MOUSEPIXELSCROLL: new bot.events.MouseEventFactory_(
      'MozMousePixelScroll', true, true),

  // Keyboard events.
  KEYDOWN: new bot.events.KeyboardEventFactory_('keydown', true, true),
  KEYPRESS: new bot.events.KeyboardEventFactory_('keypress', true, true),
  KEYUP: new bot.events.KeyboardEventFactory_('keyup', true, true),

  // Touch events.
  TOUCHEND: new bot.events.TouchEventFactory_('touchend', true, true),
  TOUCHMOVE: new bot.events.TouchEventFactory_('touchmove', true, true),
  TOUCHSTART: new bot.events.TouchEventFactory_('touchstart', true, true)
};


/**
 * Fire a named event on a particular element.
 *
 * @param {!Element} target The element on which to fire the event.
 * @param {!bot.events.EventType} type Event type.
 * @param {bot.events.EventArgs=} opt_args Arguments to initialize the event.
 * @return {boolean} Whether the event fired successfully or was cancelled.
 */
bot.events.fire = function(target, type, opt_args) {
  var factory = /** @type {!bot.events.EventFactory_} */ type;
  var event = factory.create(target, opt_args);

  // Ensure the event's isTrusted property is set to false, so that
  // bot.events.isSynthetic() can identify synthetic events from native ones.
  if (!('isTrusted' in event)) {
    event.isTrusted = false;
  }

  if (bot.userAgent.IE_DOC_PRE9) {
    return target.fireEvent('on' + factory.type_, event);
  } else {
    return target.dispatchEvent(event);
  }
};


/**
 * Returns whether the event was synthetically created by the atoms;
 * if false, was created by the browser in response to a live user action.
 *
 * @param {!(Event|goog.events.BrowserEvent)} event An event.
 * @return {boolean} Whether the event was synthetically created.
 */
bot.events.isSynthetic = function(event) {
  var e = event.getBrowserEvent ? event.getBrowserEvent() : event;
  return 'isTrusted' in e ? !e.isTrusted : false;
};
