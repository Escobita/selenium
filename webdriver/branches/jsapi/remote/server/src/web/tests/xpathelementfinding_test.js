function testIsElementPresentCorrectlyReturnsFalseWhenSearchingWithXPath(
    driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(driver.isElementPresent(webdriver.By.xpath('//a[@id="not here"]')),
             is(false));
}


function testCanFindSingleElementByXPath(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  var element = driver.findElement(webdriver.By.xpath('//h1'));
  assertThat(element.getText(), equals('XHTML Might Be The Future'));
}


function testCanFindElementsByXPath(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.xpath('//div'));
  driver.callFunction(function(response) {
    assertEquals(5, response.value.length);
  });
}



function testCanFindManyElementsRepeatedlyByXPath(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.xpath('//node()[contains(@id, "id")]'));
  driver.callFunction(function(response) {
    assertEquals(3, response.value.length);
  });
  driver.findElements(webdriver.By.xpath('//node()[contains(@id, "nope")]'));
  driver.callFunction(function(response) {
    assertEquals(0, response.value.length);
  });
}


function testCanIdentifyElementsByClassThroughXPath(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  var header = driver.findElement(webdriver.By.xpath('//h1[@class="header"]'));
  assertThat(header.getText(), equals('XHTML Might Be The Future'));
}


function testCanSearchForMultipleAttributes(driver) {
  var xpath =
      "//form[@name='optional']/input[@type='submit' and @value='Click!']";
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(driver.isElementPresent(webdriver.By.xpath(xpath)), is(false));
  driver.get(TEST_PAGES.formPage);
  assertThat(driver.isElementPresent(webdriver.By.xpath(xpath)), is(true));
}


function testCanLocateElementsWithGivenText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.isElementPresent(webdriver.By.xpath('//a[text()="click me too"]')),
      is(false));
  assertThat(
      driver.isElementPresent(webdriver.By.xpath('//a[text()="click me"]')),
      is(true));
  assertThat(
      driver.findElement(webdriver.By.xpath('//a[text()="click me"]')).
          getText(),
      equals('click me'));
}
