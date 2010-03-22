
goog.provide('bot.locators.strategies');

goog.require('bot.locators.strategies.className');
goog.require('bot.locators.strategies.css');
goog.require('bot.locators.strategies.id');
goog.require('bot.locators.strategies.name');
goog.require('bot.locators.strategies.xpath');
goog.require('goog.object');

bot.locators.strategies.known_ = {
  'className': bot.locators.strategies.className,
  'css':       bot.locators.strategies.css,
  'id':        bot.locators.strategies.id,
  'name':      bot.locators.strategies.name,
  'xpath':     bot.locators.strategies.xpath
};

/**
 * Lookup a particular element finding strategy based on the sole property of
 * the "target". The value of this key is used to locate the element.  
 *
 * @param {*} target A JS object with a single key.
 * @return {Function} The finder function, ready to be called
 */
bot.locators.strategies.lookupSingle = function(target) {
  var key = goog.object.getAnyKey(target);

  if (key) {
    var strategy = bot.locators.strategies.known_[key];
    if (strategy && goog.isFunction(strategy.single)) {
      return goog.bind(strategy.single, null, bot.window_, target[key]);
    }
  }
  throw new Error('Unsupported locator strategy: ' + key);
};

/**
 * Lookup all elements finding strategy based on the sole property of
 * the "target". The value of this key is used to locate the elements.  
 *
 * @param {*} target A JS object with a single key.
 * @return {Function} The finder function, ready to be called
 */
bot.locators.strategies.lookupMany = function(target) {
  var key = goog.object.getAnyKey(target);

  if (key) {
    var strategy = bot.locators.strategies.known_[key];
    if (strategy && goog.isFunction(strategy.many)) {
      return goog.bind(bot.locators.strategies.known_[key].many, null, bot.window_, target[key]);
    }
  }
  throw new Error('Unsupported locator strategy: ' + key);
};
