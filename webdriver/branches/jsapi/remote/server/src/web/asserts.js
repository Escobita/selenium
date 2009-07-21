goog.provide('webdriver.asserts');

goog.require('goog.math.Coordinate');
goog.require('goog.string');
goog.require('webdriver.Future');


webdriver.asserts.assertIsMatcher_ = function(obj) {
  if (!goog.isFunction(obj.matches) || !goog.isFunction(obj.describe)) {
    throw new Error(
        'Object is not a matcher; must have "matches" and "describe" methods');
  }
};


webdriver.asserts.assertIsFuture_ = function(obj) {
  if (!(obj instanceof webdriver.Future)) {
    throw new Error('Argument is not an instance of webdriver.Future');
  }
};


webdriver.asserts.assertIsString_ = function(isExpected, matcherName, value) {
  if (!goog.isString(value)) {
    throw new Error(
        (isExpected ? 'Expected' : 'Actual') +
        ' value for "' + matcherName + '" must be a string, but is ' +
        goog.typeOf(value));
  }
};


webdriver.asserts.getValue_ = function(obj) {
  return obj instanceof webdriver.Future ? obj.getValue() : obj;
};


webdriver.asserts.getValueAndType_ = function(obj) {
  var value = webdriver.asserts.getValue_(obj);
  return '<' + value.toString() + '> (' + goog.typeOf(value) + ')';
};


webdriver.asserts.Matcher = function(matchFn, describeFn) {
  this.matches = matchFn;
  this.describe = describeFn;
};



// ----------------------------------------------------------------------------
//
// The following section of code defines a bunch of global functions.  Ideally,
// these would be in the webdriver.asserts namespace, but that makes the tests
// unreadable:
//
// assertThat(x, equals(y));
//   -- vs --
// webdriver.asserts.assertThat(x, webdriver.asserts.equals(y));
//
// TODO(jmleyba): Maybe we should namespace these and leave it up to the user if
// they want to expose them with the aliases or the with keyword?
// ----------------------------------------------------------------------------

function assertThat(a, b, opt_c) {
  var args = goog.array.slice(arguments, 0);
  var message = args.length > 2 ? (args[0] + '\n') : '';
  var future = args.length > 2 ? args[1] : args[0];
  var matcher = args.length > 2 ? args[2] : args[1];

  webdriver.asserts.assertIsFuture_(future);
  webdriver.asserts.assertIsMatcher_(matcher);

  future.getDriver().callFunction(function() {
    if (!matcher.matches(future)) {
      throw new Error(message +
          'Expected ' + webdriver.asserts.getValueAndType_(future) +
          ' to ' + matcher.describe());
    }
  });
};


function equals(expected) {
  return new webdriver.asserts.Matcher(
      function (actual) {
        return webdriver.asserts.getValue_(expected) ===
               webdriver.asserts.getValue_(actual);
      },
      function () {
        return 'equal ' + webdriver.asserts.getValueAndType_(expected);
      });
}


var is = equals;
var returns = equals;


function not(matcher) {
  return new webdriver.asserts.Matcher(
      function (actual) {
        return !matcher.matches(actual);
      },
      function () {
        return 'not ' + matcher.describe();
      });
}

function isNot(value) {
  return not(equals(value));
}


function isTheSameLocationAs(expected) {
  return new webdriver.asserts.Matcher(
      function (actual) {
        var ev = webdriver.asserts.getValue_(expected);
        var av = webdriver.asserts.getValue_(actual);
        return goog.math.Coordinate.equals(ev, av);
      },
      function () {
        return 'equal ' + webdriver.asserts.getValueAndType_(expected);
      });
}


function contains(expected) {
  return new webdriver.asserts.Matcher(
      function (actual) {
        var ev = webdriver.asserts.getValue_(expected);
        var av = webdriver.asserts.getValue_(actual);
        webdriver.asserts.assertIsString_(true, 'contains', ev);
        webdriver.asserts.assertIsString_(false, 'contains', av);
        return goog.string.contains(av, ev);
      },
      function () {
        return 'contain ' + webdriver.asserts.getValueAndType_(expected);
      });
}

function matchesRegex(regex) {
  if (!(regex instanceof RegExp)) {
    throw new Error('IllegalArgument; must be a RegExp, but was: ' + regex +
                    '(' + goog.typeOf(regex) + ')');
  }
  return new webdriver.asserts.Matcher(
      function (actual) {
        var av = webdriver.asserts.getValue_(actual);
        webdriver.asserts.assertIsString_(false, 'contains', av);
        return av.match(regex) != null;
      },
      function () {
        return 'match regex ' + regex;
      });
}


function startsWith(expected) {
  return new webdriver.asserts.Matcher(
      function (actual) {
        var ev = webdriver.asserts.getValue_(expected);
        var av = webdriver.asserts.getValue_(actual);
        webdriver.asserts.assertIsString_(true, 'contains', ev);
        webdriver.asserts.assertIsString_(false, 'contains', av);
        return goog.string.startsWith(av, ev);
      },
      function () {
        return 'start with ' + webdriver.asserts.getValueAndType_(expected);
      });
}

