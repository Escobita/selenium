/**
 * The JS API
 */

var jsapi = (function() {
  var ready_ = false;
  var remote_ = '/hub';

  return {
    setReady: function (state) {
      ready_ = state;
    },

    request_: function(xhr, method, url, options) {
      var endpoint = remote_ + url;

      xhr.open(method, endpoint, false);
      xhr.setRequestHeader('Content-type', 'application/json');
      xhr.send(options ? JSON.stringify(options) : undefined);

      var type = xhr.getResponseHeader('Content-type');
      if (type && type.indexOf('application/json') != -1) {
        var result = JSON.parse(xhr.responseText);
        if (result.error) {
          throw new Error(result.value);
        }
        return result;
      }

      if (type) {
        throw new Error(
            'Incomprehensible response: (' + type + '): ' + xhr.responseText);
      }
    },

    create: function(browser_name) {
      var request = jsapi.request_;

      var sessionId_;
      var xhr_;
      if (window.XMLHttpRequest) {
        xhr_ = new XMLHttpRequest();
      } else if (window.ActiveXObject) {
        xhr_ = new ActiveXObject('Msxml2.XMLHTTP');
      }

      var result = this.request_(xhr_, 'POST', '/session',
          [{browserName: browser_name, version: '', javascriptEnabled: true}]);
      sessionId_ = result.sessionId;

      var convertToLocator = function(value) {
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

      return {
        click: function(locator) {
          var result = request(xhr_, 'POST', '/session/' + sessionId_ + '/ignored/element', convertToLocator(locator));
          var element = result.value[0].replace('element/', '');
          request(xhr_, 'POST', '/session/' + sessionId_ + '/ignored/element/' + element + '/click');
        },

        get: function(url) {
          request(xhr_, 'POST', '/session/' + sessionId_ + '/ignored/url', [url]);
        },

        type: function(locator, value) {
          var result = request(xhr_, 'POST', '/session/' + sessionId_ + '/ignored/element', convertToLocator(locator));
          var element = result.value[0].replace('element/', '');
          request(xhr_, 'POST', '/session/' + sessionId_ + '/ignored/element/' + element + '/value', [{id: element, value: [value]}]);
        }
      };
    }
  };
})();

