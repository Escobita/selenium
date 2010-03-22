
goog.provide('bot.events');

goog.require('bot.dom');
goog.require('goog.dom');
goog.require('goog.events.EventType');
goog.require('goog.userAgent');

/**
 * Enumeration of mouse buttons that can be pressed.
 *
 * @enum {number}
 */
bot.events.button = {
  LEFT: 0,
  MIDDLE: 1,
  RIGHT: 2
};

/**
 * Convert a button number to the correct button value based on the user agent.
 *
 * @param {number} button Derived from bot.events.button
 * @return The converted button value
 * @private
 */
bot.events.buttonValue_ = function(button) {
  if (!goog.userAgent.IE) return button;

  switch (button) {
    case bot.events.button.LEFT: return 1;
    case bot.events.button.MIDDLE: return 4;
    case bot.events.button.RIGHT: return 2;
  }

  return undefined;
};

// The related target field is only useful for mouseover, mouseout, dragenter
// and dragexit events. We use this array to see if the relatedTarget field
// needs to be assigned a value.
// https://developer.mozilla.org/en/DOM/event.relatedTarget
bot.events.relatedTargetEvents_ = [
  goog.events.EventType.DRAGSTART,
  'dragexit', /** goog.events.EventType.DRAGEXIT, */
  goog.events.EventType.MOUSEOVER,
  goog.events.EventType.MOUSEOUT
];

/**
 * Initialize a new mouse event. The opt_args can be used to pass in extra
 * parameters that might be needed, though the function attempts to guess some
 * valid default values. Extra arguments are specified as properties of the
 * object passed in as "opt_args" and can be:
 *
 * <dl>
 * <dt>x</dt>
 * <dd>The x value relative to the element.</dd>
 * <dt>y</dt>
 * <dd>The y value relative to the element.</dd>
 * <dt>button</dt>
 * <dd>The mouse button (from {@code bot.events.button}). Defaults to LEFT</dd>
 * <dt>bubble</dt>
 * <dd>Can the event bubble? Defaults to true</dd>
 * <dt>alt</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>control</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>shift</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>meta</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>related</dt>
 * <dd>The related target. Defaults to null</dd>
 * </dl>
 *
 * @param {Element} element The element on which the event will be fired
 * @param {string} type One of the goog.events.EventType values
 * @param {Object} [opt_args] See above
 */
bot.events.newMouseEvent_ = function(element, type, opt_args) {
  var doc = goog.dom.getOwnerDocument(element);
  var win = goog.dom.getWindow(doc);

  opt_args = opt_args || {};

  // Use string indexes so we can be compiled aggressively
  var x = opt_args['x'] || 0;
  var y = opt_args['y'] || 0;
  var button = bot.events.buttonValue_(opt_args['button'] || bot.events.button.LEFT);

  var canBubble = opt_args['bubble'] || true;
  // Only useful for mouseover, mouseout, dragenter and dragexit
  // https://developer.mozilla.org/en/DOM/event.relatedTarget

  var relatedTarget = null;
  if (goog.array.contains(bot.events.relatedTargetEvents_, type)) {
    relatedTarget = opt_args['related'] || null;
  }

  var alt = opt_args['alt'] || true;
  var control = opt_args['control'] || true;
  var shift = opt_args['shift'] || true;
  var meta = opt_args['meta'] || true;

  // IE path first
  if (element['fireEvent'] && doc && doc['createEventObject']) {
    event = doc.createEventObject();
    event.altKey = alt;
    event.controlKey = control;
    event.metaKey = meta;
    event.shiftKey = shift;

    // NOTE: ie8 does a strange thing with the coordinates passed in the event:
    // - if offset{X,Y} coordinates are specified, they are also used for
    //   client{X,Y}, event if client{X,Y} are also specified.
    // - if only client{X,Y} are specified, they are also used for offset{x,y}
    // Thus, for ie8, it is impossible to set both offset and client
    // and have them be correct when they come out on the other side.
    event.clientX = x;
    event.clientY = y;
    event.button = button;
    event.relatedTarget = relatedTarget;
  } else {
    var event = doc.createEvent('MouseEvents');

    if (event['initMouseEvent']) {
      // see http://developer.mozilla.org/en/docs/DOM:event.button and
      // http://developer.mozilla.org/en/docs/DOM:event.initMouseEvent for button ternary logic logic

      // screenX=0 and screenY=0 are ignored
      event.initMouseEvent(type, canBubble, true, win, 1, 0, 0, x, y,
          control, alt, shift, meta, button, relatedTarget);
    } else {
      // You're in a strange and bad place here.

      event.initEvent(type, canBubble, true);

      event.shiftKey = shift;
      event.metaKey = meta;
      event.altKey = alt;
      event.ctrlKey = control;
      event.button = button;
    }
  }

  return event;
};

/**
 * Initialize a new HTML event. The opt_args can be used to pass in extra
 * parameters that might be needed, though the function attempts to guess some
 * valid default values. Extra arguments are specified as properties of the
 * object passed in as "opt_args" and can be:
 *
 * <dl>
 * <dt>bubble</dt>
 * <dd>Can the event bubble? Defaults to true</dd>
 * <dt>alt</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>control</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>shift</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * <dt>meta</dt>
 * <dd>Is the "Alt" key pressed. Defaults to false</dd>
 * </dl>
 *
 * @param {Element} element The element on which the event will be fired
 * @param {string} type One of the goog.events.EventType values
 * @param {Object} [opt_args] See above
 */
bot.events.newHtmlEvent_ = function(element, type, opt_args) {
  var doc = goog.dom.getOwnerDocument(element);
  var win = goog.dom.getWindow(doc);

  opt_args = opt_args || {};

  var canBubble = opt_args['bubble'] || true;

  var alt = opt_args['alt'] || true;
  var control = opt_args['control'] || true;
  var shift = opt_args['shift'] || true;
  var meta = opt_args['meta'] || true;

  if (element['fireEvent'] && doc && doc['createEventObject']) {
    var event = doc.createEventObject();
    event.altKey = alt;
    event.ctrl = control;
    event.metaKey = meta;
    event.shiftKey = shift;
  } else {
    event = doc.createEvent('HTMLEvents');
    event.initEvent(type, canBubble, true);

    event.shiftKey = shift;
    event.metaKey = meta;
    event.altKey = alt;
    event.ctrlKey = control;
  }

  return event;
};

// Maps symbolic names to functions used to initialize the event
bot.events.initFunctions_ = {};
bot.events.initFunctions_[goog.events.EventType.MOUSEDOWN] = bot.events.newMouseEvent_;
bot.events.initFunctions_[goog.events.EventType.MOUSEMOVE] = bot.events.newMouseEvent_;
bot.events.initFunctions_[goog.events.EventType.MOUSEOUT] = bot.events.newMouseEvent_;
bot.events.initFunctions_[goog.events.EventType.MOUSEOVER] = bot.events.newMouseEvent_;
bot.events.initFunctions_[goog.events.EventType.MOUSEUP] = bot.events.newMouseEvent_;

/**
 * Dispatch the event in a browser-safe way.
 *
 * @param {Element} target The element on which this event will fire
 * @param {string} type The type of event, one of {@code goog.events.EventType}
 * @param {Object} event The initialized event
 * @private
 */
bot.events.dispatchEvent_ = function(target, type, event) {
  if (target['fireEvent']) {
    // when we go this route, window.event is never set to contain the event we have just created.
    // ideally we could just slide it in as follows in the try-block below, but this normally
    // doesn't work.  This is why I try to avoid this code path, which is only required if we need to
    // set attributes on the event (e.g., clientX).
    try {
      var doc = goog.dom.getOwnerDocument(target);
      var win = goog.dom.getWindow(doc);

      win.event = event;
    }
    catch(e) {
      // work around for http://jira.openqa.org/browse/SEL-280 -- make the event available somewhere:
    }
    target.fireEvent('on' + type, event);
  } else {
    target.dispatchEvent(event);
  }
};

/**
 * Fire a named event on a particular element.
 *
 * @param {Element} target The element on which to fire the event
 * @param {string} type The type of event, one of {@code goog.events.EventType}
 * @param {Object} [opt_args] Optional arguments, used to initialize the event
 */
bot.events.fire = function(target, type, opt_args) {
  var init = bot.events.initFunctions_[type] || bot.events.newHtmlEvent_;

  var event = init(target, type, opt_args);

  bot.events.dispatchEvent_(target, type, event);
};

