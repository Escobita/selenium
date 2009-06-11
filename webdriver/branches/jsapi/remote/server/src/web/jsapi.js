/**
 * The JS API
 */

// Create a default JS API implementation if the object does not already exist
if (!jsapi) {
  var jsapi = (function() {
    var remote_ = '/hub';
    var sessionId_ = undefined;

    var newXhr = function() {
      if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
      } else if (window.ActiveXObject) {
        return new ActiveXObject('Msxml2.XMLHTTP');
      }
    };

    var map = {
      click:   {method: 'POST', url: '/session/:sessionId/ignored/element/:elementId/click'},
      element: {method: 'POST', url: '/session/:sessionId/ignored/element'},
      execute: {method: 'POST', url: '/session/:sessionId/ignored/execute'},
      init:    {method: 'POST', url: '/session'},
      get:     {method: 'POST', url: '/session/:sessionId/ignored/url'},
      type:    {method: 'POST', url: '/session/:sessionId/ignored/element/:elementId/value'}
    };

    return {
      init: function() {
        // Nothing to do
      },

      // Map a command name to a function and call that
      execute: function(command, callback) {
        // Set the callback, if necessary
        callback = callback || function() {
          jsapi.setReady(true);
        };

        // Which command are we executing?
        var to_execute = map[command.name];
        // TODO(simonstewart): Handle the case where there's nothing to execute

        // prepare the end point
        var endpoint = remote_ + to_execute.url;
        endpoint = endpoint.replace(/:sessionId/, jsapi.sessionId_);
        if (command.elementId !== undefined) {
          endpoint = endpoint.replace(/:elementId/, command.elementId);
        }

        var xhr = newXhr();
        xhr.open(to_execute.method, endpoint, true);
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function() {
          if (xhr.readyState == 4) {
            var data;
            var type = xhr.getResponseHeader('Content-type');
            if (type && type.indexOf('application/json') != -1) {
              data = JSON.parse(xhr.responseText);
            }

            callback(data);
          }
        };

        xhr.send(command.args ? JSON.stringify(command.args) : undefined);
      },

      quit: function() {
        // Shutdown the browser instance
      }
    };
  })();
}

// Common functionality shared by all implementations of the JS API
jsapi.commands_ = [];
jsapi.ready_ = false;

jsapi.setReady = function(state) {
  if (state === undefined) {
    jsapi.ready_ = true;
  }
  jsapi.ready_ = state;
};

jsapi.process_commands_ = function() {
  if (jsapi.ready_ && jsapi.commands_.length) {
    // Pull off the top command, mark us as not ready and execute
    var args = jsapi.commands_.shift();
    var method = args.shift();
    try {
      jsapi.setReady(false);
      method.apply(null, args);
    } catch (e) {
      alert(e);
      // Fall through for now.
      jsapi.setReady(true);
    }
  }
  window.setTimeout(jsapi.process_commands_, 200);
};

jsapi.to_locator_ = function(value) {
  // Default to using ID as the locator
  if (value instanceof String) {
    return ['id', value];
  }

  // And then loop through the alternatives
  if (value.id) {
    return ['id', value.id];
  }
  if (value.name) {
    return ['name', value.name];
  }
  if (value.link) {
    return ['link text', value.link];
  }
  if (value.xpath) {
    return ['xpath', value.xpath];
  }

  throw new Error("Unmappable locator: " + value);
};

jsapi.on_element = function(locator, command, args, callback) {
  var after_found = function(data) {
    var element = data.value[0].replace('element/', '');
    jsapi.execute({name: command, args: args, elementId: element}, callback);
  };
  jsapi.execute({name: 'element', args: jsapi.to_locator_(locator)}, after_found);
};

// And now the JS API functions
function use(browser_name) {
  this.run(function() {
    var callback = function(data) {
      jsapi.sessionId_ = data.sessionId;
      jsapi.setReady(true);
    };
    jsapi.execute({name: 'init', args: [
      {
        browserName: browser_name,
        version: '',
        javascriptEnabled: true
      }
    ]}, callback);
  });
}

function run() {
  var args = [];
  for (var i = 0; i < arguments.length; i++) args[i] = arguments[i];
  jsapi.commands_.push(args);
}

// Methods analogous to WebDriver methods
function get(url) {
  jsapi.execute({name: 'get', args: [url]});
}

function script(to_execute) {
  var raw_func = to_execute;
  if (to_execute instanceof Function) {
    // Convert the function to a string.
    raw_func = to_execute.toString();
    // Delete up to the first open brace and after the closing brace
    raw_func = raw_func.replace(/^.*?{/, '').replace(/}.*?$/, '');
  }

  alert(raw_func);
  jsapi.execute({name: 'execute', args: [raw_func]});
}

// Methods from WebElement
function click(locator) {
  jsapi.on_element(locator, 'click');
}

function type(locator, value) {
  jsapi.on_element(locator, 'type', [[value]]);
}

function evalWithElement(toEval, locator, args) {
  var func = arguments.shift();
  var element_locator = arguments.shift();
  var params = [];
  while (arguments) {
    params.push(arguments.shift());
  }

  jsapi.execute({name: 'findElement', args: [element_locator]},
      function(data) {
        params.splice(0, 0, func.toString(), data);
        jsapi.execute({name: 'eval', args: data});
      });
}

function setValue(locator, value) {
  var setter = function(element, value) {
    element.value = value;
  };
  evalWithElement(setter, locator, value);
}

/*
 var echo = function(message) {
 var e = document.getElementById("echo");
 if (e) {
 e.innerHTML = message;
 }
 };

 var trace = function(message) {
 var e = document.getElementById("current");
 if (e) {
 e.innerHTML = message;
 }
 };


 present: function(locator) {
 request_(nextCommand, 'POST', '/session/' + sessionId_ + '/ignored/element', convertToLocator_(locator));
 }
 };
 })();

 }
 */

// Kick everything off
jsapi.init();
window.setTimeout(jsapi.process_commands_, 0);
jsapi.setReady(true);