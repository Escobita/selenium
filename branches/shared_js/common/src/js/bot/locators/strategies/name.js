
goog.provide('bot.locators.strategies.name');


bot.locators.strategies.name = function(win, name) {
  var doc = win.document;

  if (doc['getElementsByName']) {
    var results = doc.getElementsByName(name);

    var length = results.length;
    for (var i = 0; i < length; i++) {
      if (results[i].getAttribute('name') === name) {
        return results[i];
      }
    }
  }

  return undefined;  
};

goog.exportProperty(bot.locators.strategies, 'name',
                    bot.locators.strategies.name);
