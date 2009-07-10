/**
 * @fileoverview Implements the tests in org.openqa.selenium.ChildrenFindingTest
 * using the JS API.  This file should be loaded by the test_suite.js test
 * bootstrap.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

function testFindElementByXpath(driver) {
  driver.get(TEST_PAGES.nestedPage);
  var select =
      driver.findElement(webdriver.By.xpath('//form[@name="form2"]/select'));
  assertThat(select.getAttribute('id'), is('2'));
}


function testFindElementByXpathWhenNoMatch(driver) {
  driver.get(TEST_PAGES.nestedPage);
  var xpath = webdriver.By.xpath('//form[@name="form2"]/select/x');
  assertThat(driver.isElementPresent(xpath), is(false));
  var select = driver.findElement(
      webdriver.By.xpath('//form[@name="form2"]/select'));
  assertThat(select.isElementPresent(webdriver.By.xpath('.//x')), is(false));
}


function testFindElementByName(driver) {
  driver.get(TEST_PAGES.nestedPage);
  assertThat(
      driver.findElement(webdriver.By.name('form2')).
          findElement(webdriver.By.name('selectomatic')).
          getAttribute('id'),
      is('2'));
}


function testFindElementById(driver) {
  driver.get(TEST_PAGES.nestedPage);
  assertThat(
      driver.findElement(webdriver.By.id('2')).getAttribute('name'),
      is('selectomatic'));
}


function testFindElementByIdWhenMultipleMatchesExist(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.id('test_id_div')).
      findElement(webdriver.By.id('test_id')).
      getText();
  driver.callFunction(function(response) {
    assertEquals('inside', response.value);
  });
}


// TODO(jmleyba): Catch expected failures - how?
function testFindElementByIdWhenNoMatchInContext(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.id('test_id_div')).
      findElement(webdriver.By.id('test_id_out'));
}


function testFindElementsById(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.name('form2')).
      findElements(webdriver.By.id('2'));
  driver.callFunction(function(response) {
    assertEquals('Should find two elements', 2, response.value.length);
  });
}


function testFindElementByLinkText(driver) {
  driver.get(TEST_PAGES.nestedPage);
  assertThat(
      driver.findElement(webdriver.By.name('div1')).
          findElement(webdriver.By.linkText('hello world')).
          getAttribute('name'),
      is('link1'));
}


function testFindElementsByLinkTest(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.name('div1')).
      findElements(webdriver.By.linkText('hello world'));
  driver.callFunction(function(response) {
    var webElements = response.value;
    assertEquals('Should find two elements', 2, webElements.length);
    assertThat(webElements[0].getAttribute('name'), is('link1'));
    assertThat(webElements[1].getAttribute('name'), is('link2'));
  });
}


function testFindElementsByLinkText(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.name('div1')).
      findElements(webdriver.By.linkText('hello world'));
  driver.callFunction(function(response) {
    assertEquals('Should find two elements', 2, response.value.length);
  });
}


function testShouldFindChildElementsByClassName(driver) {
  driver.get(TEST_PAGES.nestedPage);
  var one = driver.findElement(webdriver.By.name('classes')).
      findElement(webdriver.By.className('one'));
  assertThat(one.getText(), equals('Find me'));
}


function testShouldFindChildrenByClassName(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.name('classes')).
      findElements(webdriver.By.className('one'));
  driver.callFunction(function(response) {
    assertEquals(2, response.value.length);
  });
}


function testShouldFindChildElementByTagName(driver) {
  driver.get(TEST_PAGES.nestedPage);
  assertThat(
      driver.findElement(webdriver.By.name('div1')).
          findElement(webdriver.By.tagName('A')).
          getAttribute('name'),
      is('link1'));
}


function testShouldFindChildrenByTagName(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.name('div1')).
      findElements(webdriver.By.tagName('A'));
  driver.callFunction(function(response) {
    assertEquals(2, response.value.length);
  });
}
