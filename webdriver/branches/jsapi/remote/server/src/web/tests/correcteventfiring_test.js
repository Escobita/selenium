/**
 * @fileoverview Implements the tests in
 * org.openqa.selenium.CorrectEventFiringTest using the JS API.  This file
 * should be loaded by the test_suite.js test bootstrap.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

function assertResultTextIs(driver, text) {
  assertThat(
      driver.findElement(webdriver.By.id('result')).getText(),
      equals(text));
}


function assertResultTextContains(driver, text) {
  assertThat(
      driver.findElement(webdriver.By.id('result')).getText(),
      contains(text));
}


function clickOnElementWhichRecordsEvents(driver) {
  driver.findElement(webdriver.By.id('plainButton')).click();
}

function assertEventFired(driver, event) {
  driver.findElement(webdriver.By.id('result')).
      getText();
  driver.callFunction(function(response) {
    assertTrue('No ' + event + ' fired: ' + response.value,
        goog.string.contains(response.value, event));
  });
}


function testShouldFireFocusEventWhenClicking(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  clickOnElementWhichRecordsEvents(driver);
  assertEventFired(driver, 'focus');
}


function testShouldFireClickEventWhenClicking(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  clickOnElementWhichRecordsEvents(driver);
  assertEventFired(driver, 'click');
}


function testShouldFireMouseDownEventWhenClicking(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  clickOnElementWhichRecordsEvents(driver);
  assertEventFired(driver, 'mousedown');
}


function testShouldFireMouseUpEventWhenClicking(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  clickOnElementWhichRecordsEvents(driver);
  assertEventFired(driver, 'mouseup');
}


function testShouldFireEventsInTheRightOrder(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  clickOnElementWhichRecordsEvents(driver);
  var text = driver.findElement(webdriver.By.id('result')).getText();
  assertThat(text, matchesRegex(/mousedown focus ([^ ] )*mouseup click/));
}


function testShouldIssueMouseDownEvents(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('mousedown')).click();
  assertResultTextIs(driver, 'mouse down');
}


function testShouldIssueClickEvents(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('mouseclick')).click();
  assertResultTextIs(driver, 'mouse click');
}


function testShouldIssueMouseUpEvents(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('mouseup')).click();
  assertResultTextIs(driver, 'mouse up');
}


function testMouseEventsShouldBubbleUpToContainingElements(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('child')).click();
  assertResultTextIs(driver, 'mouse down');
}


function testShouldEmitOnChangeEventsWhenSelectingElements(driver) {
  var fooOption;
  var barOption;

  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('selector')).
      findElements(webdriver.By.tagName('option'));
  driver.callFunction(function(response) {
    var optionElements = response.value;
    fooOption = optionElements[0];
    barOption = optionElements[1];
    assertTrue('fooOption is undefined', goog.isDef(fooOption));
    assertTrue('barOption is undefined', goog.isDef(barOption));
  });
  driver.findElement(webdriver.By.id('result')).getText();
  driver.callFunction(function(response) {
    var initialText = response.value;
    fooOption.setSelected();
    assertResultTextIs(driver, initialText);
    barOption.setSelected();
    assertResultTextIs(driver, 'bar');
  });
}


function testShouldEmitOnChangeEventsWhenChangingTheStateOfACheckbox(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('checkbox')).setSelected();
  assertResultTextIs(driver, 'checkbox thing');
}


function testShouldEmitClickEventWhenClickingOnATextInputElement(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  var clicker = driver.findElement(webdriver.By.id('clickField'));
  clicker.click();
  clicker.getValue();
  driver.callFunction(function(response) {
    assertEquals('Clicked', response.value);
  });
}

function testClearingAnElementCausesTheOnChangeHandlerToFire(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.id('clearMe')).clear();
  assertResultTextIs(driver, 'Cleared');
}
