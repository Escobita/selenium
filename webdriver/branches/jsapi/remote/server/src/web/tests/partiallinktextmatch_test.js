function testLinkWithFormattingTags(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement(webdriver.By.id("links"));
  var res = elem.findElement(
      webdriver.By.partialLinkText('link with formatting tags'));
  assertThat(res.getText(), equals('link with formatting tags'));
}


function testLinkWithLeadingSpaces(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement(webdriver.By.id("links"));
  var res = elem.findElement(
      webdriver.By.partialLinkText("link with leading space"));
  assertThat(res.getText(), equals('link with leading space'));
}


function testLinkWithTrailingSpace(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement(webdriver.By.id("links"));
  var res = elem.findElement(
      webdriver.By.partialLinkText("link with trailing space"));
  assertThat(res.getText(), equals('link with trailing space'));
}


function testFindMultipleElements(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement(webdriver.By.id("links"));
  elem.findElements(webdriver.By.partialLinkText("link"));
  driver.callFunction(function(response) {
    assertEquals(3, response.value.length);
  });
}
