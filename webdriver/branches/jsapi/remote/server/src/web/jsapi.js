/**
 * The JS API
 */

var jsapi = (function() {
  var ready_ = false;
  var remote_ = '/hub';
  var sessionId_ = undefined;
  var commands_ = [];

  var newXhr = function() {
    if (window.XMLHttpRequest) {
      return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
      return new ActiveXObject('Msxml2.XMLHTTP');
    }
  };

  var setReady = function (state) {
    ready_ = state;
  };

  var private_ = {
    request: function(callback, method, url, options) {
      var endpoint = remote_ + url;

      var xhr = newXhr();
      xhr.open(method, endpoint, true);
      xhr.setRequestHeader('Content-type', 'application/json');
      xhr.onreadystatechange = function(event) {
        if (xhr.readyState == 4) {
          var data;
          var type = xhr.getResponseHeader('Content-type');
          if (type && type.indexOf('application/json') != -1) {
            data = JSON.parse(xhr.responseText);
          }

          callback(data);
        }
      };

      xhr.send(options ? JSON.stringify(options) : undefined);
    },

    nextCommand: function() {
      setReady(true);
    },

    convertToLocator : function(value) {
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
    }
  };

  // Kick off the main event loop
  var process_commands = function() {
    if (ready_ && commands_.length) {
      // Pull off the top command, mark us as not ready and execute
      var args = commands_.shift();
      var method = args.shift();
      try {
        setReady(false);
        method.apply(null, args);
      } catch (e) {
        alert(e);
        // Fall through for now.
        setReady(true);
      }
    }

    window.setTimeout(process_commands, 200);
  };
  process_commands();
  setReady(true);

  // And return something for the user to (ummm...) use
  return {
    click: function(locator) {
      var withElement = function(data) {
        var element = data.value[0].replace('element/', '');
        private_.request(private_.nextCommand, 'POST', '/session/' + private_.sessionId_ + '/ignored/element/'
            + element + '/click');
    };

    private_.request(withElement, 'POST',
        '/session/' + private_.sessionId_ + '/ignored/element', private_.convertToLocator(locator));
    },

    get: function(url) {
      private_.request(private_.nextCommand, 'POST', '/session/' + private_.sessionId_ + '/ignored/url', [url]);
    },

    type: function(locator, value) {
      var withElement = function(data) {
        var element = data.value[0].replace('element/', '');
        private_.request(private_.nextCommand, 'POST', '/session/' + private_.sessionId_ + '/ignored/element/' + element
          + '/value', [{id: element, value: [value]}]);
      };
      private_.request(withElement,  'POST',
          '/session/' + private_.sessionId_ + '/ignored/element', private_.convertToLocator(locator));
    },

    run: function() {
      var args = [];
      for (var i = 0; i < arguments.length; i++) args[i] = arguments[i];
      commands_.push(args);
    },

    use: function(browser_name) {
      this.run(function() {
        var callback = function(data) {
          private_.sessionId_ = data.sessionId;
          private_.nextCommand();
        };
        private_.request(callback, 'POST', '/session',
            [{browserName: browser_name, version: '', javascriptEnabled: true}]);
      });
    }
  };
})();
