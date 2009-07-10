/**
 * @fileoverview Implements the tests in
 * org.openqa.selenium.ElementAttributeTest using the JS API.  This file
 * should be loaded by the test_suite.js test bootstrap.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

function testReturnsNullForValueOfAnAttributeThatIsNotListed(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  assertThat(
      driver.findElement(webdriver.By.xpath('//body')).
          getAttribute('cheese'),
      is(null));
}


function testReturnsEmptyAttributeValuesWhenPresentAndValueIsEmpty(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  assertThat(
      driver.findElement(webdriver.By.xpath('//body')).
          getAttribute('style'),
      is(''));
}


function testReturnsTheValueOfTheDisabledAttributeEventIfItIsMissing(driver) {
  driver.get(TEST_PAGES.formPage);
  assertThat(
      driver.findElement(webdriver.By.xpath("//input[@id='working']")).
          getAttribute('disabled'),
      equals(false));
}


function testReturnsTheValueOfTheIndexAttributeEvenIfItIsMissing(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('multi')).
      findElements(webdriver.By.tagName('option'));
  driver.callFunction(function(response) {
    var index = response.value[1].getAttribute('index');
    assertThat(index, equals(1));
  });
}


function testIndicatesTheElementsThatAreDisabledAreNotEnabled(driver) {
  var element;
  driver.get(TEST_PAGES.formPage);
  element = driver.findElement(webdriver.By.xpath("//input[@id='notWorking']"));
  assertThat(element.isEnabled(), is(false));
  element = driver.findElement(webdriver.By.xpath("//input[@id='working']"));
  assertThat(element.isEnabled(), is(true));
}


function testIndicatesWhenATextAreaIsDisabled(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.xpath("//textarea[@id='notWorkingArea']")).
      isEnabled();
  driver.callFunction(function(response) {
    assertFalse(response.value);
  });
}


function testIndicatesWhenASelectIsDisabled(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.name('selectomatic')).
      isEnabled();
  driver.callFunction(function(response) {
    assertTrue(response.value);
  });
  driver.findElement(webdriver.By.name('no-select')).
      isEnabled();
  driver.callFunction(function(response) {
    assertFalse(response.value);
  });
}


function testReturnsTheValueOfCheckedForACheckboxEvenIfItLacksThatAttribute(
    driver) {
  driver.get(TEST_PAGES.formPage);
  var checkbox =
      driver.findElement(webdriver.By.xpath("//input[@id='checky']"));
  assertThat(checkbox.getAttribute('checked'), is(false));
  checkbox.setSelected();
  assertThat(checkbox.getAttribute('checked'), is(true));
}


function testReturnsTheValueOfCheckedForRadioButtonsEvenIfTheyLackTheAttribute(
    driver) {
  driver.get(TEST_PAGES.formPage);
  var cheese = driver.findElement(webdriver.By.id('cheese'));
  var peas = driver.findElement(webdriver.By.id('peas'));
  var cheeseAndPeas = driver.findElement(webdriver.By.id('cheese_and_peas'));
  assertThat(cheese.getAttribute('checked'), is(false));
  assertThat(peas.getAttribute('checked'), is(false));
  assertThat(cheeseAndPeas.getAttribute('checked'), is(true));
  cheese.click();
  assertThat(cheese.getAttribute('checked'), is(true));
  assertThat(peas.getAttribute('checked'), is(false));
  assertThat(cheeseAndPeas.getAttribute('checked'), is(false));
  peas.click();
  assertThat(cheese.getAttribute('checked'), is(false));
  assertThat(peas.getAttribute('checked'), is(true));
  assertThat(cheeseAndPeas.getAttribute('checked'), is(false));
  peas.click();
  assertThat(cheese.getAttribute('checked'), is(false));
  assertThat(peas.getAttribute('checked'), is(true));
  assertThat(cheeseAndPeas.getAttribute('checked'), is(false));
}


function testReturnsTheValueOfSelectedForOptionsEvenIfTheyLackTheAttribute(
    driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.xpath("//select[@name='selectomatic']")).
      findElements(webdriver.By.tagName('option'));
  driver.callFunction(function(response) {
    var webElements = response.value;
    assertThat(webElements[0].isSelected(), is(true));
    assertThat(webElements[1].isSelected(), is(false));
  });
}


function testReturnsValueOfClassAttributeOfAnElement(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement(webdriver.By.xpath('//h1')).
          getAttribute('class'),
      equals('header'));
}


function testReturnsTheContentsOfATextAreaAsItsValue(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('withText')).getValue();
  driver.callFunction(function(response) {
    assertEquals(response.value, 'Example text');
  });
}


function testTreatsReadonlyAsAValue(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.name('readonly')).
      getAttribute('readonly');
  driver.callFunction(function(readOnly) {
    driver.findElement(webdriver.By.name('x')).
        getAttribute('readonly');
    driver.callFunction(function(notReadOnly) {
      assertFalse(
          'Expected not to be <' + readOnly +
              '>, but was <' + notReadOnly + '>',
          readOnly == notReadOnly);
    });
  });
}
