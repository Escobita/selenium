function testLinkWithFormattingTags(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement({id: "links"});
  var res = elem.findElement({partialLinkText: 'link with formatting tags'});
  assertThat(res.getText(), equals('link with formatting tags'));
}


function testLinkWithLeadingSpaces(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement({id: "links"});
  var res = elem.findElement({partialLinkText: "link with leading space"});
  assertThat(res.getText(), equals('link with leading space'));
}


function testLinkWithTrailingSpace(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement({id: "links"});
  var res = elem.findElement({partialLinkText: "link with trailing space"});
  assertThat(res.getText(), equals('link with trailing space'));
}


function testFindMultipleElements(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var elem = driver.findElement({id: "links"});
  elem.findElements({partialLinkText: "link"});
  driver.callFunction(function(response) {
    assertEquals(3, response.value.length);
  });
}
