
goog.provide('bot.locators');

goog.require('bot');
goog.require('bot.locators.strategies');

bot.locators.findElement = function(target) {
  var finder_func = bot.locators.strategies.lookup(target);
  return finder_func.call();
};