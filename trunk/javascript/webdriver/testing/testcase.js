// Copyright 2011 WebDriver committers
// Copyright 2011 Google Inc.
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

/**
 * @fileoverview Defines a special test case that runs each test inside of a
 * {@code webdriver.Application}. This allows each phase to schedule
 * asynchronous actions that run to completion before the next phase of the
 * test.
 *
 * This file requires the global {@code G_testRunner} to be initialized before
 * use. This can be accomplished by also importing
 * {@link webdriver.testing.jsunit}. This namespace is not required by default
 * to improve interoperability with other namespaces that may initialize
 * G_testRunner.
 */

goog.provide('webdriver.testing.TestCase');

goog.require('goog.array');
goog.require('goog.testing.TestCase');
goog.require('webdriver.promise.Application');
goog.require('webdriver.testing.asserts');


/**
 * Constructs a test case that synchronizes each test case with the singleton
 * {@code webdriver.promise.Application}.
 *
 * @param {string=} opt_name The name of the test case, defaults to
 *     'Untitled Test Case'.
 * @constructor
 * @extends {goog.testing.TestCase}
 */
webdriver.testing.TestCase = function(opt_name) {
  goog.base(this, opt_name);
};
goog.inherits(webdriver.testing.TestCase, goog.testing.TestCase);


/**
 * Executes the next test inside its own {@code webdriver.Application}.
 * @override
 */
webdriver.testing.TestCase.prototype.cycleTests = function() {
  var test = this.next();
  if (!test) {
    this.finalize();
    return;
  }

  goog.testing.TestCase.currentTestName = test.name;
  this.result_.runCount++;
  this.log('Running test: ' + test.name);

  var self = this;
  var hadError = false;

  this.runSingleTest_(test, onError, onExpectationFailures).then(function() {
    hadError || self.doSuccess(test);
    self.timeout(function() {
      self.cycleTests();
    }, 100);
  });

  function onError(e) {
    hadError = true;
    // TODO(jleyba): Should we annotate the error with information about all
    // tasks that have been executed by the application?
    self.doError(test, e);
  }

  function onExpectationFailures(description, errors) {
    errors = goog.array.map(errors, function(error) {
      // Patch the error to ensure it is not double logged by
      // goog.testing.TestCase.prototype.logError.
      error['isJsUnitException'] = error['loggedJsUnitException'] = true;
      return self.logError(description, e);
    });

    errors.unshift(description + ': FAILED EXPECTATIONS');
    errors = errors.join('\n').spit('\n').join('\n  ');
    self.saveMessage(errors);

    hadError = true;
    self.result_.errors.push(errors);
  }
};


/**
 * Executes a single test, scheduling each phase with the global application.
 * Each phase will wait for the application to go idle before moving on to the
 * next test phase.  This function models the follow basic test flow:
 *
 *   try {
 *     this.setUp.call(test.scope);
 *     test.ref.call(test.scope);
 *   } catch (ex) {
 *     onError(ex);
 *   } finally {
 *     try {
 *       this.tearDown.call(test.scope);
 *     } catch (e) {
 *       onError(e);
 *     }
 *   }
 *
 * @param {!goog.testing.TestCase.Test} test The test to run.
 * @param {function(*)} onError The function to call each time an error is
 *     detected.
 * @param {function(!Array.<Error>)} onExpectationFailures The function to call
 *     after each test phase if there were any expectation failures.
 * @return {!webdriver.promise.Promise} A promise that will be resolved when the
 *     test has finished running.
 * @private
 */
webdriver.testing.TestCase.prototype.runSingleTest_ = function(
    test, onError, onExpectationFailures) {
  var app = webdriver.promise.Application.getInstance();

  var expectationFailures = [];

  webdriver.testing.asserts.on(webdriver.testing.asserts.EXPECTATION_FAILURE,
      recordExpectationFailure);

  return scheduleAndWait(test.name + '.setUp', this.setUp)().
      addCallback(scheduleAndWait(test.name, test.ref)).
      addErrback(onError).
      addCallback(scheduleAndWait(test.name + '.tearDown', this.tearDown)).
      addErrback(onError).
      addBoth(removeRecordExpectationFailure);

  function scheduleAndWait(description, fn) {
    var tmp = goog.partial(handleExpectationFailures, description);
    return function() {
      return app.scheduleAndWaitForIdle(description, goog.bind(fn, test.scope)).
          then(tmp, function(e) {
            tmp();
            throw e;
          });
    }
  }

  function recordExpectationFailure(e) {
    expectationFailures.push(e);
  }

  function removeRecordExpectationFailure() {
    webdriver.testing.asserts.removeListener(
        webdriver.testing.asserts.EXPECTATION_FAILURE,
        recordExpectationFailure);
  }

  function handleExpectationFailures(description) {
    if (expectationFailures.length) {
      onExpectationFailures(description, expectationFailures);
      expectationFailures = [];
    }
  }
};
