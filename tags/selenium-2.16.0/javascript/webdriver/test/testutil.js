// Copyright 2011 Software Freedom Conservancy. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

goog.provide('webdriver.test.testutil');

goog.require('goog.array');
goog.require('goog.json');
goog.require('goog.string');
goog.require('goog.testing.MockClock');
goog.require('goog.testing.recordFunction');


/** @type {?goog.testing.MockClock} */
webdriver.test.testutil.clock = null;

/** @type {Array.<!string>} */
webdriver.test.testutil.messages = [];

/** @type {!Error} */
webdriver.test.testutil.STUB_ERROR = new Error('ouch');
webdriver.test.testutil.STUB_ERROR.stack = '(stub error; stack irrelevant)';

webdriver.test.testutil.throwStubError = function() {
  throw webdriver.test.testutil.STUB_ERROR;
};

webdriver.test.testutil.assertIsStubError = function(error) {
  assertEquals(webdriver.test.testutil.STUB_ERROR, error);
};

webdriver.test.testutil.createMockClock = function() {
  webdriver.test.testutil.clock = new goog.testing.MockClock(true);

  /* Patch to work around the following bug with mock clock:
   *   function testZeroBasedTimeoutsRunInNextEventLoop() {
   *     var count = 0;
   *     setTimeout(function() {
   *       count += 1;
   *       setTimeout(function() { count += 1; }, 0);
   *       setTimeout(function() { count += 1; }, 0);
   *     }, 0);
   *     clock.tick();
   *     assertEquals(1, count);  // Fails; count == 3
   *     clock.tick();
   *     assertEquals(3, count);
   *   }
   */
  webdriver.test.testutil.clock.runFunctionsWithinRange_ = function(endTime) {
    var adjustedEndTime = endTime - this.timeoutDelay_;

    // Repeatedly pop off the last item since the queue is always sorted.
    // Stop once we've collected all timeouts that should run.
    var timeouts = [];
    while (this.queue_.length &&
        this.queue_[this.queue_.length - 1].runAtMillis <= adjustedEndTime) {
      timeouts.push(this.queue_.pop());
    }

    // Now run all timeouts that are within range.
    while (timeouts.length) {
      var timeout = timeouts.shift();

      if (!(timeout.timeoutKey in this.deletedKeys_)) {
        // Only move time forwards.
        this.nowMillis_ = Math.max(this.nowMillis_,
            timeout.runAtMillis + this.timeoutDelay_);
        // Call timeout in global scope and pass the timeout key as
        // the argument.
        timeout.funcToCall.call(goog.global, timeout.timeoutKey);
        // In case the interval was cleared in the funcToCall
        if (timeout.recurring) {
          this.scheduleFunction_(
              timeout.timeoutKey, timeout.funcToCall, timeout.millis, true);
        }
      }
    }
  };

  return webdriver.test.testutil.clock;
};


/**
 * Advances the clock by one tick.
 * @param {number=} opt_n The number of ticks to advance the clock. If not
 *     specified, will advance the clock once for every timeout made.
 *     Assumes all timeouts are 0-based.
 */
webdriver.test.testutil.consumeTimeouts = function(opt_n) {
  // webdriver.promise and webdriver.application only schedule 0 timeouts to
  // yield until the next available event loop.
  for (var i = 0;
       i < (opt_n || webdriver.test.testutil.clock.getTimeoutsMade()); i++) {
    webdriver.test.testutil.clock.tick();
  }
};


/**
 * Asserts the contents of the {@link webdriver.test.testutil.messages} array
 * are as expected.
 * @param {...*} var_args The expected contents.
 */
webdriver.test.testutil.assertMessages = function(var_args) {
  var args = Array.prototype.slice.call(arguments, 0);
  assertArrayEquals(args, webdriver.test.testutil.messages);
};


/**
 * Wraps a call to {@link webdriver.test.testutil.assertMessages} so it can
 * be passed as a callback.
 * @param {...*} var_args The expected contents.
 * @return {!Function} The wrapped function.
 */
webdriver.test.testutil.assertingMessages = function(var_args) {
  var args = goog.array.slice(arguments, 0);
  return function() {
    return webdriver.test.testutil.assertMessages.apply(null, args);
  };
};


/**
 * Asserts an object is a promise.
 * @param {*} obj The object to check.
 */
webdriver.test.testutil.assertIsPromise = function(obj) {
  assertTrue('Value is not a promise: ' + goog.typeOf(obj),
      webdriver.promise.isPromise(obj));
};


/**
 * Asserts an object is not a promise.
 * @param {*} obj The object to check.
 */
webdriver.test.testutil.assertNotPromise = function(obj) {
  assertFalse(webdriver.promise.isPromise(obj));
};

/**
 * Wraps a function. The wrapped function will have several utility functions:
 * <ul>
 * <li>assertCalled: Asserts that the function was called.
 * <li>assertNotCalled: Asserts that the function was not called.
 * </ul> 
 * @param {Function=} opt_fn The function to wrap; defaults to
 *     goog.nullFunction.
 * @return {!Function} The wrapped function.
 * @see goog.testing.recordFunction
 */
webdriver.test.testutil.callbackHelper = function(opt_fn) {
  var callback = goog.testing.recordFunction(opt_fn);

  callback.getExpectedCallCountMessage = function(n, opt_prefix, opt_noJoin) {
    var message = [];
    if (opt_prefix) message.push(opt_prefix);

    var calls = callback.getCalls();
    message.push(
        'Expected to be called ' + n + ' times.',
        '  was called ' + calls.length + ' times:');
    message = goog.array.concat(message,
        goog.array.map(calls, function(call, i) {
          return goog.string.repeat(' ', 4) + 'args(call #' + i + '): ' +
              goog.json.serialize(call.getArguments());
        }));
    return opt_noJoin ? message : message.join('\n');
  };

  callback.assertCalled = function(opt_message) {
    assertEquals(callback.getExpectedCallCountMessage(1, opt_message),
        1, callback.getCallCount());
  };

  callback.assertNotCalled = function(opt_message) {
    assertEquals(callback.getExpectedCallCountMessage(1, opt_message),
        0, callback.getCallCount());
  };

  return callback;
};


/**
 * Creates a utility for managing a pair of callbacks, capable of asserting only
 * one of the pair was ever called.
 *
 * @param {Function=} opt_callback The callback to manage.
 * @param {Function=} opt_errback The errback to manage.
 */
webdriver.test.testutil.callbackPair = function(opt_callback, opt_errback) {
  var pair = {
    callback: webdriver.test.testutil.callbackHelper(opt_callback),
    errback: webdriver.test.testutil.callbackHelper(opt_errback)
  };

  pair.assertEither = function(opt_message) {
    if (!pair.callback.getCallCount() &&
        !pair.errback.getCallCount()) {
      var message = ['Neither callback nor errback has been called'];
      if (opt_message) goog.array.insertAt(message, opt_message);
      fail(message.join('\n'));
    }
  };

  pair.assertNeither = function(opt_message) {
    var message = [opt_message || 'Unexpected callback results:'];
    if (pair.callback.getCallCount()) {
      message = goog.array.concat(message,
          pair.callback.getExpectedCallCountMessage(0,
              'Did not expect callback to be called.', true));
    }
    if (pair.errback.getCallCount()) {
      message = goog.array.concat(message,
          pair.callback.getExpectedCallCountMessage(0,
              'Did not expect errback to be called.', true));
    }
    if (message.length > 1) {
      fail(message.join('\n  -- '));
    }
  };

  pair.assertCallback = function(opt_message) {
    assertCalls(pair.callback, 'callback', pair.errback, 'errback',
        opt_message);
  };

  pair.assertErrback = function(opt_message) {
    assertCalls(pair.errback, 'errback', pair.callback, 'callback',
        opt_message);
  };

  pair.reset = function() {
    pair.callback.reset();
    pair.errback.reset();
  };

  return pair;

  function assertCalls(expectedFn, expectedName, unexpectedFn, unexpectedName,
                       opt_message) {
    var message = [opt_message || 'Unexpected callback results:'];
    if (!expectedFn.getCallCount()) {
      message.push('Expected ' + expectedName + ' to be called.');
    } else if (expectedFn.getCallCount() > 1) {
      message.push(
          'Expected ' + expectedName + ' to be called only once.',
          '  was called ' + expectedFn.getCallCount() + ' times:');
      message = goog.array.concat(message, expectedFn.getFormattedArgs(4));
    }

    if (unexpectedFn.getCallCount()) {
      message.push(
          'Did not expect ' + unexpectedName + ' to be called.',
          '  was called ' + unexpectedFn.getCallCount() + ' times:');
      message = goog.array.concat(message, unexpectedFn.getFormattedArgs(4));
    }

    if (message.length > 1) {
      fail(message.join('\n  -- '));
    }
  }
};


webdriver.test.testutil.assertObjectEquals = function(expected, actual) {
  assertObjectEquals(
      'Expected: ' + goog.json.serialize(expected) + '\n' +
      'Actual:   ' + goog.json.serialize(actual),
      expected, actual);
};
