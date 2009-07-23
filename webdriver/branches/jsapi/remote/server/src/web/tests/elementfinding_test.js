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
  driver.findElement({linkText: 'click me'}).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testDriverCanFindElementsAfterLoadingMultiplePagesInARow(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement({linkText: 'click me'}).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testCanClickOnLinkIdentifiedById(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement({id: 'linkId'}).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testCannotFindNonExistantLinkUsingLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat('Should not have found an element by nonexistant link text',
      driver.isElementPresent({linkText: 'Not here either'}),
      is(false));
  driver.findElement({linkText: 'Not here either'});
  driver.expectErrorFromPreviousCommand();
}


function testShouldFindAnElementBasedOnId(driver) {
  driver.get(TEST_PAGES.formPage);
  var selected = driver.findElement({id: 'checky'}).isSelected();
  assertThat(selected, is(false));
}


function testCannotFindNonExistantId(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat('Should not have found an element by nonexistant ID',
      driver.isElementPresent({id: 'notThere'}),
      is(false));
  driver.findElement({id: 'notThere'});
  driver.expectErrorFromPreviousCommand();
}


function testCanFindChildrenOfANode(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements({xpath: '/html/head'});
  driver.callFunction(function(response) {
    var elements = response.value;
    var head = elements[0];
    head.findElements({tagName: 'script'});
    driver.callFunction(function(response) {
      assertEquals(2, response.value.length);
    });
  });
}


function testReturnAnEmptyListWhenThereAreNoChildrenOfANode(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement({id: 'table'}).
      findElements({tagName: 'tr'});
  driver.callFunction(function(response) {
    assertEquals(0, response.value.length);
  });
}


function testShouldFindElementsByName(driver) {
  driver.get(TEST_PAGES.formPage);
  var element = driver.findElement({name: 'checky'});
  assertThat(element.getValue(), is('furrfu'));
}


function testShouldFindElementsByClass(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement({className: 'extraDiv'}).getText(),
      startsWith('Another div starts here.'));
}


function testShouldFindElementsByClassWhenItIsTheFirstNameAmongMany(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement({className: 'nameA'}).getText(),
      equals('An H2 title'));
}


function testShouldFindElementsByClassWhenItIsTheLastNameAmongMany(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement({className: 'nameC'}).getText(),
      equals('An H2 title'));
}


function testShouldFindElementsByClassWhenItIsInTheMiddleAmongMany(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat(
      driver.findElement({className: 'nameBnoise'}).getText(),
      equals('An H2 title'));
}


function testDoesNotFindElementByClassWhenRealNameIsShorterThanQuery(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  assertThat('Should not have found an element',
      driver.isElementPresent({className: 'nameB'}),
      is(false));
  driver.findElement({className: 'nameB'});
  driver.expectErrorFromPreviousCommand();
}


function testShouldBeAbleToFindMultipleElementsByXPath(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements({xpath: '//div'});
  driver.callFunction(function(response) {
    var elements = response.value;
    assertTrue(
        'Should find more than 1 element, but found ' + elements.length,
        elements.length > 1);
  });
}


function testShouldBeAbleToFindMultipleElementsByLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements({linkText: 'click me'});
  driver.callFunction(function(response) {
    assertEquals(2, response.value.length);
  });
}


function testShouldBeAbleToFindMultipleElementsByPartialLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements({partialLinkText: 'ick me'});
  driver.callFunction(function(response) {
    assertEquals(2, response.value.length);
  });
}


function testFindElementByPartialLinkTextWhenTextIsBeginningOfLink(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement({partialLinkText: 'Create a '}).getText();
  driver.callFunction(function(response) {
    assertEquals('Create a new anonymous window', response.value);
  });
}


function testFindElementByPartialLinkTextWhenTextIsInMiddleOfLink(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement({partialLinkText: 'anonymous'}).getText();
  driver.callFunction(function(response) {
    assertEquals('Create a new anonymous window', response.value);
  });
}


function testFindElementByPartialLinkTextWhenTextIsAtEndOfLink(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement({partialLinkText: 'anonymous window'}).getText();
  driver.callFunction(function(response) {
    assertEquals('Create a new anonymous window', response.value);
  });
}


function testIsElementPresentReturnsFalseWithNoMatchingPartialLinkText(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  var locator = {partialLinkText: 'anon window'};
  var futureResult = driver.isElementPresent(locator);
  assertThat('Should not have found an element', futureResult, is(false));
  driver.findElement(locator);
  driver.expectErrorFromPreviousCommand();
}


function testFindElementsReturnsEmptyArrayWhenNoMatchesByPartialLinkText(
    driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements({partialLinkText: 'anon window'});
  driver.callFunction(function(response) {
    assertEquals(0, response.value.length);
  });
}


function testShouldBeAbleToFindMultipleElementsByName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElements({name: 'snack'});
  driver.callFunction(function(response) {
    var elements = response.value;
    assertTrue(
        'Should find more than 1 element, but found ' + elements.length,
        elements.length > 1);
  });
}


function testShouldBeAbleToFindMultipleElementsById(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElements({id: '2'});
  driver.callFunction(function(response) {
    assertEquals(
        'Should have found 8 elements with id "2"', 8,
        response.value.length);
  });
}


function testShouldBeAbleToFindMultipleElementsByClassName(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElements({className: 'nameC'});
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
  assertValueIs(driver.findElement({name: 'id-name1'}), 'name');
  assertValueIs(driver.findElement({id: 'id-name1'}), 'id');
  assertValueIs(driver.findElement({name: 'id-name2'}), 'name');
  assertValueIs(driver.findElement({id: 'id-name2'}), 'id');
}


function testShouldFindGrandChildren(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement({id: 'nested_form'}).
      findElement({name: 'x'});
}


function testShouldNotFindElementOutsideTree(driver) {
  driver.get(TEST_PAGES.formPage);
  var login = driver.findElement({name: 'login'});
  assertThat(login.isElementPresent({name: 'x'}), is(false));
  login.findElement({name: 'x'});
  driver.expectErrorFromPreviousCommand();
}


function testShouldReturnElementsThatDoNotSupportTheNameProperty(driver) {
  driver.get(TEST_PAGES.nestedPage);
  driver.findElement({name: 'div1'});
  // We're OK if this executes without error.
}


function testShouldFindHiddenElementsByName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement({name: 'hidden'});
  // We're OK if this executes without error.
}


function testShouldFindAnElementBasedOnTagName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement({tagName: 'input'});
  // We're OK if this executes without error.
}


function testShouldFindElementsByTagName(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElements({tagName: 'input'});
  driver.callFunction(function(response) {
    assertEquals(
        'Should have found 19 input elements',
        19, response.value.length);
  });
}


function testFindingByCompoundClassNameIsAnError(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  try {
    driver.findElement({className: 'a b'});
    fail('Compound class names are not allowed');
  } catch (expected) {
  }

  try {
    driver.findElements({className: 'a b'});
    fail('Compound class names are not allowed');
  } catch (expected) {
  }
}


function testShouldBeAbleToClickOnLinksWithNoHrefAttribute(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  driver.findElement({linkText: 'No href'}).click();
  assertThat(driver.getTitle(), equals('Changed'));
}


function testShouldNotBeAbleToFindAnElementOnABlankPage(driver) {
  driver.get('about:blank');
  driver.findElement({id: 'imaginaryButton'});
  driver.expectErrorFromPreviousCommand();
  assertThat(
      driver.isElementPresent({id: 'imaginaryButton'}), is(false));
}


function testFindingALinkByXpathUsingContainsKeywordShouldWork(driver) {
  driver.get(TEST_PAGES.nestedPage);
  var element = driver.findElement({xpath: '//a[contains(.,"hello world")]'});
  assertThat(element.getText(), contains('hello world'));
}
