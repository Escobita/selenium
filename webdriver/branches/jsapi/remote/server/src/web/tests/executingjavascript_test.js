function testCanExecuteJavascriptThatReturnsAString(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.executeScript('return document.title');
  driver.callFunction(function(response) {
    assertTrue(goog.isString(response.value));
    assertEquals('XHTML Test Page', response.value);
  });
}


function testCanExecuteJavascriptThatReturnsANumber(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.executeScript('return document.getElementsByName("checky").length;');
  driver.callFunction(function(response) {
    assertTrue(goog.isNumber(response.value));
    assertEquals(8, response.value);
  });
}


function testCanExecuteJavascriptThatReturnsAWebElement(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.executeScript('return document.getElementById("id1");');
  driver.callFunction(function(response) {
    assertTrue(
        'Result should be a WebElement',
        response.value instanceof webdriver.WebElement);
    response.value.getAttribute('href');
    driver.callFunction(function(response) {
      assertEquals('#', response.value);
    });
    response.value.getAttribute('id');
    driver.callFunction(function(response) {
      assertEquals('id1', response.value);
    });
  });
}


function testCanExecuteScriptThatReturnsABoolean(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.executeScript('return true;');
  driver.callFunction(function(response) {
    assertTrue(goog.isBoolean(response.value));
    assertTrue(response.value);
  });
}


// TODO(jmleyba): Expected failure?
function testThrowsAnExceptionWhenTheJavascriptIsBad(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.executeScript('return squiggle();');
  driver.callFunction(function() {
    fail('Expected an exception from bad javascript');
  });
}


function testCanCallFunctionsDefinedOnThePage(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.executeScript('displayMessage("I like cheese");');
  assertThat(
      driver.findElement(webdriver.By.id('result')).getText(),
      equals('I like cheese'));
}


function testCanPassAStringAsAnArgument(driver) {
  var script = 'return arguments[0] == "fish" ? "fish" : "not fish";';
  driver.get(TEST_PAGES.javascriptPage);
  driver.executeScript(script, 'fish');
  driver.callFunction(function(response) {
    assertTrue(goog.isString(response.value));
    assertEquals('fish', response.value);
  });
  driver.executeScript(script, 'chicken');
  driver.callFunction(function(response) {
    assertTrue(goog.isString(response.value));
    assertEquals('not fish', response.value);
  });
}


function testCanPassABooleanAsAScriptArgument(driver) {
  var script = 'return arguments[0] == true;';
  driver.get(TEST_PAGES.javascriptPage);
  driver.executeScript(script, true);
  driver.callFunction(function(response) {
    assertTrue(!!response.value);
  });
  driver.executeScript(script, false);
  driver.callFunction(function(response) {
    assertFalse(!!response.value);
  });
}


function testCanPassANumberAsAnArgument(driver) {
  var script = 'return arguments[0] + arguments[1];';
  driver.get(TEST_PAGES.javascriptPage);
  driver.executeScript(script, 1, 2);
  driver.callFunction(function(response) {
    assertEquals(3, response.value);
  });
  driver.executeScript(script, 27, -15);
  driver.callFunction(function(response) {
    assertEquals(12, response.value);
  });
}


// TODO(jmleyba): Fails b/c WebDriver queries WebElement ID before it is known.
// Need to update WebDriver to check all parameters in a command for Futures.
function testCanPassAWebElementAsAnArgument(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  var button = driver.findElement(webdriver.By.id('plainButton'));
  driver.executeScript(
      "arguments[0]['flibble'] = arguments[0].getAttribute('id');" +
          "return arguments[0]['flibble'];",
      button);
  driver.callFunction(function(response) {
    assertEquals('plainButton', response.value);
  });
}


// TODO(jmleyba): Expected failures (once todo below is addressed)
// TODO(jmleyba): See failure message
function testThrowsAnExceptionIfAnArgumentIsNotValid(driver) {
  fail('TODO(jmleyba): Restrict valid script argument types');
}
