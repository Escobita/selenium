
goog.provide('bot.locators.strategies');

goog.require('bot.locators.strategies.id');
goog.require('bot.locators.strategies.name');
goog.require('goog.object');

bot.locators.strategies.known_ = {};

bot.locators.strategies.lookup = function(target) {
  var key = goog.object.getAnyKey(target);

  if (key) {
    var strategy = 'by_' + key;

    if (strategy in bot.locators.strategies) {
      return function find() {
        return bot.locators.strategies[strategy].call(undefined, bot.window_, target[key]);
      }
    }
  }
  throw new Error('Unsupported locator strategy: ' + key);
};
