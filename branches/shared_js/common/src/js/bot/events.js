
goog.provide('bot.events');

goog.require('goog.events.EventType');

bot.events.fire = function(target, eventName) {
  var init = bot.events.types_[eventName];

  if (!init) {
    throw new Error('Unknown event type: ' + eventName);
  }

  var event = init(bot.window_);
};

bot.events.initMouseEvent_ = function(doc) {

};

bot.events.types_ = {
  goog.events.EventType.MOUSEMOVE: bot.events.initMouseEvent_
};