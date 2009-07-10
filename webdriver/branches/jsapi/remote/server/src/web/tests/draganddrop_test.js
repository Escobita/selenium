/**
 * @fileoverview Implements the tests in
 * org.openqa.selenium.DragAndDropTest using the JS API.  This file
 * should be loaded by the test_suite.js test bootstrap.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

// TODO(jmleyba): Need to limit the tests in this file to FF only.


function drag(element, initialLocation, moveRightBy, moveDownBy) {
  element.dragAndDropBy(moveRightBy, moveDownBy);
  var expectedLocation = initialLocation.clone();
  expectedLocation.x += moveRightBy;
  expectedLocation.y += moveDownBy;
  return expectedLocation;
}


function testDragAndDrop(driver) {
  driver.get(TEST_PAGES.dragAndDropPage);
  var img = driver.findElement(webdriver.By.id('test1'));
  img.getLocation();
  driver.callFunction(function(response) {
    var initialLocation = response.value;

    var expectedLocation1 = drag(img, initialLocation, 500, 300);
    assertThat(img.getLocation(), isTheSameLocationAs(expectedLocation1));

    var expectedLocation2 = drag(img, expectedLocation1, -100, -50);
    assertThat(img.getLocation(), isTheSameLocationAs(expectedLocation2));

    drag(img, expectedLocation2, 0, 0);
    assertThat(img.getLocation(), isTheSameLocationAs(expectedLocation2));

    var expectedLocation3 = drag(img, expectedLocation2, 1, -1);
    assertThat(img.getLocation(), isTheSameLocationAs(expectedLocation3));
  });
}


function testDragAndDropToElement(driver) {
  driver.get(TEST_PAGES.dragAndDropPage);
  var img1 = driver.findElement(webdriver.By.id('test1'));
  var img2 = driver.findElement(webdriver.By.id('test2'));
  var img1Location = img1.dragAndDropBy(100, 100);
  var img2Location = img2.dragAndDropTo(img1);
  assertThat(img1Location, isTheSameLocationAs(img2Location));
}


function testDragElementInDiv(driver) {
  driver.get(TEST_PAGES.dragAndDropPage);
  var img = driver.findElement(webdriver.By.id('test3'));
  img.getLocation();
  driver.callFunction(function(response) {
    var initialLocation = response.value;
    var expectedLocation = drag(img, initialLocation, 100, 100);
    assertThat(img.getLocation(), isTheSameLocationAs(expectedLocation));
  });
}


function testDragTooFar(driver) {
  driver.get(TEST_PAGES.dragAndDropPage);
  var img = driver.findElement(webdriver.By.id('test1'));
  img.getLocation();
  driver.callFunction(function(response) {
    var initialLocation = response.value;
    drag(img, initialLocation, Number.MIN_VALUE, Number.MIN_VALUE);
    assertThat(img.getLocation(), isTheSameLocationAs(initialLocation));
    drag(img, initialLocation, Number.MAX_VALUE, Number.MAX_VALUE);
    //We don't know where the img is dragged to , but we know it's not too
    //far, otherwise this function will not return for a long long time
  });
}


function testMouseSpeed(driver) {
  driver.get(TEST_PAGES.dragAndDropPage);
  driver.setMouseSpeed(webdriver.WebDriver.Speed.SLOW);
  assertThat(driver.getMouseSpeed(), equals(webdriver.WebDriver.Speed.SLOW));
  driver.setMouseSpeed(webdriver.WebDriver.Speed.MEDIUM);
  assertThat(driver.getMouseSpeed(), equals(webdriver.WebDriver.Speed.MEDIUM));
  driver.setMouseSpeed(webdriver.WebDriver.Speed.FAST);
  assertThat(driver.getMouseSpeed(), equals(webdriver.WebDriver.Speed.FAST));
}
