/**
 * @fileoverview Implements the tests in
 * org.openqa.selenium.ElementFindingTest using the JS API.  This file
 * should be loaded by the test_suite.js test bootstrap.
 * @author jmleyba@gmail.com (Jason Leyba)
 */

function testReturnsTitleOfPageIfSet(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(driver.getTitle(), equals('XHTML Test Page'));
  driver.get(TEST_PAGES.simpleTestPage);
  assertThat(driver.getTitle(), equals('Hello WebDriver'));
}


function testAbleToClickOnLinkIdentifiedByText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.linkText('click me')).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testDriverCanFindElementsAfterLoadingMultiplePagesInARow(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.linkText('click me')).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testCanClickOnLinkIdentifiedById(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.id('linkId')).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testCannotFindNonExistantLinkUsingLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat('Should not have found an element by nonexistant link text',
      driver.isElementPresent(webdriver.By.linkText('Not here either')),
      is(false));
  driver.findElement(webdriver.By.linkText('Not here either'));
  driver.expectErrorFromPreviousCommand();
}


function testShouldFindAnElementBasedOnId(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('checky')).isSelected();
  driver.callFunction(function(response) {
    assertFalse(response.value);
  });
}


function testCannotFindNonExistantId(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat('Should not have found an element by nonexistant ID',
      driver.isElementPresent(webdriver.By.id('notThere')),
      is(false));
  driver.findElement(webdriver.By.id('notThere'));
  driver.expectErrorFromPreviousCommand();
}


function testCanFindChildrenOfANode(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.xpath('/html/head'));
  driver.callFunction(function(response) {
    var elements = response.value;
    var head = elements[0];
    head.findElements(webdriver.By.tagName('script'));
    driver.callFunction(function(response) {
      assertEquals(2, response.value.length);
    });
  });
}


function testReturnAnEmptyListWhenThereAreNoChildrenOfANode(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.id('table')).
      findElements(webdriver.By.tagName('tr'));
  driver.callFunction(function(response) {
    assertEquals(0, response.value.length);
  });
}


function testShouldFindElementsByName(driver) {
  driver.get(TEST_PAGES.formPage);
  var element = driver.findElement(webdriver.By.name('checky'));
  assertThat(element.getValue(), is('furrfu'));
}


function testShouldFindElementsByClass(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement(webdriver.By.className('extraDiv')).getText(),
      startsWith('Another div starts here.'));
}


function testShouldFindElementsByClassWhenItIsTheFirstNameAmongMany(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement(webdriver.By.className('nameA')).getText(),
      equals('An H2 title'));
}


function testShouldFindElementsByClassWhenItIsTheLastNameAmongMany(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement(webdriver.By.className('nameC')).getText(),
      equals('An H2 title'));
}


function testShouldFindElementsByClassWhenItIsInTheMiddleAmongMany(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement(webdriver.By.className('nameBnoise')).getText(),
      equals('An H2 title'));
}


function testDoesNotFindElementByClassWhenRealNameIsShorterThanQuery(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat('Should not have found an element',
      driver.isElementPresent(webdriver.By.className('nameB')),
      is(false));
  driver.findElement(webdriver.By.className('nameB'));
  driver.expectErrorFromPreviousCommand();
}


function testShouldBeAbleToFindMultipleElementsByXPath(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.xpath('//div'));
  driver.callFunction(function(response) {
    var elements = response.value;
    assertTrue(
        'Should find more than 1 element, but found ' + elements.length,
        elements.length > 1);
  });
}


function testShouldBeAbleToFindMultipleElementsByLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.linkText('click me'));
  driver.callFunction(function(response) {
    assertEquals(2, response.value.length);
  });
}


function testShouldBeAbleToFindMultipleElementsByPartialLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.partialLinkText('ick me'));
  driver.callFunction(function(response) {
    assertEquals(2, response.value.length);
  });
}


function testFindElementByPartialLinkTextWhenTextIsBeginningOfLink(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.partialLinkText('Create a ')).
      getText();
  driver.callFunction(function(response) {
    assertEquals('Create a new anonymous window', response.value);
  });
}


function testFindElementByPartialLinkTextWhenTextIsInMiddleOfLink(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.partialLinkText('anonymous')).
      getText();
  driver.callFunction(function(response) {
    assertEquals('Create a new anonymous window', response.value);
  });
}


function testFindElementByPartialLinkTextWhenTextIsAtEndOfLink(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.partialLinkText('anonymous window')).
      getText();
  driver.callFunction(function(response) {
    assertEquals('Create a new anonymous window', response.value);
  });
}


function testIsElementPresentReturnsFalseWithNoMatchingPartialLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  var locator = webdriver.By.partialLinkText('anon window');
  var futureResult = driver.isElementPresent(locator);
  assertThat('Should not have found an element', futureResult, is(false));
  driver.findElement(locator);
  driver.expectErrorFromPreviousCommand();
}


function testFindElementsReturnsEmptyArrayWhenNoMatchesByPartialLinkText(
    driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.partialLinkText('anon window'));
  driver.callFunction(function(response) {
    assertEquals(0, response.value.length);
  });
}


function testShouldBeAbleToFindMultipleElementsByName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElements(webdriver.By.name('snack'));
  driver.callFunction(function(response) {
    var elements = response.value;
    assertTrue(
        'Should find more than 1 element, but found ' + elements.length,
        elements.length > 1);
  });
}


function testShouldBeAbleToFindMultipleElementsById(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElements(webdriver.By.id('2'));
  driver.callFunction(function(response) {
    assertEquals(
        'Should have found 8 elements with id "2"', 8,
        response.value.length);
  });
}


function testShouldBeAbleToFindMultipleElementsByClassName(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements(webdriver.By.className('nameC'));
  driver.callFunction(function(response) {
    assertTrue(
        'Should have found more than 1 element with class "nameC", but found ' +
            response.value.length,
        response.value.length > 1);
  });
}


function testWhenFindingByNameDoesNotReturnById(driver) {
  driver.get(TEST_PAGES.formPage);
  function assertValueIs(element, value) {
    assertThat(element.getValue(), is(value));
  }
  assertValueIs(driver.findElement(webdriver.By.name('id-name1')), 'name');
  assertValueIs(driver.findElement(webdriver.By.id('id-name1')), 'id');
  assertValueIs(driver.findElement(webdriver.By.name('id-name2')), 'name');
  assertValueIs(driver.findElement(webdriver.By.id('id-name2')), 'id');
}


function testShouldFindGrandChildren(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('nested_form')).
      findElement(webdriver.By.name('x'));
}


function testShouldNotFindElementOutsideTree(driver) {
  driver.get(TEST_PAGES.formPage);
  var login = driver.findElement(webdriver.By.name('login'));
  assertThat(login.isElementPresent(webdriver.By.name('x')), is(false));
  login.findElement(webdriver.By.name('x'));
  driver.expectErrorFromPreviousCommand();
}


function testShouldReturnElementsThatDoNotSupportTheNameProperty(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement(webdriver.By.name('div1'));
  // We're OK if this executes without error.
}


function testShouldFindHiddenElementsByName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.name('hidden'));
  // We're OK if this executes without error.
}


function testShouldFindAnElementBasedOnTagName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.tagName('input'));
  // We're OK if this executes without error.
}


function testShouldFindElementsByTagName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElements(webdriver.By.tagName('input'));
  driver.callFunction(function(response) {
    assertEquals(
        'Should have found 19 input elements',
        19, response.value.length);
  });
}


function testFindingByCompoundClassNameIsAnError(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  try {
    driver.findElement(webdriver.By.className('a b'));
    fail('Compound class names are not allowed');
  } catch (expected) {
  }

  try {
    driver.findElements(webdriver.By.className('a b'));
    fail('Compound class names are not allowed');
  } catch (expected) {
  }
}


function testShouldBeAbleToClickOnLinksWithNoHrefAttribute(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement(webdriver.By.linkText('No href')).click();
  assertThat(driver.getTitle(), equals('Changed'));
}


function testShouldNotBeAbleToFindAnElementOnABlankPage(driver) {
  driver.get('about:blank');
  driver.findElement(webdriver.By.id('imaginaryButton'));
  driver.expectErrorFromPreviousCommand();
  assertThat(
      driver.isElementPresent(webdriver.By.id('imaginaryButton')), is(false));
}


function testFindingALinkByXpathUsingContainsKeywordShouldWork(driver) {
  driver.get(TEST_PAGES.nestedPage);
  var element = driver.findElement(
      webdriver.By.xpath('//a[contains(.,"hello world")]'));
  assertThat(element.getText(), contains('hello world'));
}
