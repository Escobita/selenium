function testClickingOnSubmitInputElements(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('submitButton')).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testClickingOnUnclickableElementsDoesNothing(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.xpath('//body')).click();
  assertThat(driver.getTitle(), equals('We Leave From Here'));
}


function testShouldBeAbleToClickImageButtons(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('imageButton')).click();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testShouldBeAbleToSubmitForms(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.name('login')).submit();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testShouldSubmitAFormWhenAnyInputElementWithinThatFormIsSubmitted(
    driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id('checky')).submit();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testShouldSubmitAFormWhenAnyElementWithinThatFormIsSubmitted(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.xpath('//form/p')).submit();
  assertThat(driver.getTitle(), equals('We Arrive Here'));
}


function testCannotSubmitANonFormElement(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.xpath('//body')).submit();
  assertThat(driver.getTitle(), equals('We Leave From Here'));
}


function testShouldBeAbleToEnterTextIntoATextAreaBySettingItsValue(driver) {
  driver.get(TEST_PAGES.javascriptPage);
  var textarea = driver.findElement(webdriver.By.id('keyUpArea'));
  var cheesey = 'Brie and cheddar';
  textarea.sendKeys(cheesey);
  assertThat(textarea.getValue(), equals(cheesey));
}


function testShouldEnterDataIntoFormFields(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  var element = driver.findElement(webdriver.By.xpath(
      '//form[@name="someForm"]/input[@id="username"]'));
  element.getValue();
  driver.callFunction(function(response) {
    var originalValue = response.value;
    assertEquals('change', originalValue);

    element.clear();
    element.sendKeys('some text');

    assertThat(
        driver.findElement(webdriver.By.xpath(
            '//form[@name="someForm"]/input[@id="username"]')).getValue(),
        equals('some text'));
  });
}


function testShouldBeAbleToSelectACheckBox(driver) {
  driver.get(TEST_PAGES.formPage);
  var checkbox = driver.findElement(webdriver.By.id('checky'));
  checkbox.isChecked();
  assertThat(checkbox.isChecked(), is(false));
  checkbox.setSelected();
  assertThat(checkbox.isChecked(), is(true));
  checkbox.setSelected();
  assertThat(checkbox.isChecked(), is(true));
}


function testTogglingACheckboxReturnsItsCurrentState(driver) {
  driver.get(TEST_PAGES.formPage);
  var checkbox = driver.findElement(webdriver.By.id('checky'));
  assertThat(checkbox.isChecked(), is(false));
  assertThat(checkbox.toggle(), equals(true));
  assertThat(checkbox.toggle(), equals(false));
}


// TODO(jmleyba): Expected failures?
function testCannotSelectSomethingThatIsDisabled(driver) {
  driver.get(TEST_PAGES.formPage);
  var radioButton = driver.findElement(webdriver.By.id('nothing'));
  assertThat(radioButton.isEnabled(), is(false));
  radioButton.setSelected();
  driver.callFunction(function() {
    fail('Should not have succeeded');
  });
}


function testCanSelectARadioButton(driver) {
  driver.get(TEST_PAGES.formPage);
  var radioButton = driver.findElement(webdriver.By.id('peas'));
  assertThat(radioButton.isChecked(), is(false));
  radioButton.setSelected();
  assertThat(radioButton.isChecked(), is(true));
}


function testCanSelectARadioButtonByClickingOnIt(driver) {
  driver.get(TEST_PAGES.formPage);
  var radioButton = driver.findElement(webdriver.By.id('peas'));
  assertThat(radioButton.isChecked(), is(false));
  radioButton.click();
  assertThat(radioButton.isChecked(), is(true));
}


function testRadioButtonsInSameGroupChangeWhenNewButtonIsSelected(driver) {
  driver.get(TEST_PAGES.formPage);

  var cheeseAndPeas = driver.findElement(webdriver.By.id('cheese_and_peas'));
  var cheese = driver.findElement(webdriver.By.id('cheese'));
  var peas = driver.findElement(webdriver.By.id('peas'));

  assertThat(cheeseAndPeas.isChecked(), is(true));
  assertThat(cheese.isChecked(), is(false));
  assertThat(peas.isChecked(), is(false));

  cheese.click();
  assertThat(cheeseAndPeas.isChecked(), is(false));
  assertThat(cheese.isChecked(), is(true));
  assertThat(peas.isChecked(), is(false));

  peas.click();
  assertThat(cheeseAndPeas.isChecked(), is(false));
  assertThat(cheese.isChecked(), is(false));
  assertThat(peas.isChecked(), is(true));

  peas.click();
  assertThat(cheeseAndPeas.isChecked(), is(false));
  assertThat(cheese.isChecked(), is(false));
  assertThat(peas.isChecked(), is(true));
}


// TODO(jmleyba): expected failure?
function testThrowsAnExceptionWhenTogglingTheStateOfARadioButton(driver) {
  driver.get(TEST_PAGES.formPage);
  var cheese = driver.findElement(webdriver.By.id('cheese'));
  cheese.toggle();
  driver.callFunction(function() {
    fail('Should not be able to toggle a radio button');
  });
}


function testThrowsAnExceptionWhenTogglingOptionNotInAMultiSelect(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.name('selectomatic')).
      findElements(webdriver.By.tagName('option'));
  driver.callFunction(function(response) {
    response.value[0].toggle();
    driver.callFunction(function() {
      fail('Should not be able to toggle an option not in a multi-select');
    });
  });
}


function testCanToggleOptionsInAMultiSelect(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.name('multi')).
      findElements(webdriver.By.tagName('option'));
  driver.callFunction(function(response) {
    var option = response.value[0];
    assertThat(option.isSelected(), is(true));
    assertThat(option.toggle(), is(false));
    assertThat(option.isSelected(), is(false));
    assertThat(option.toggle(), is(true));
    assertThat(option.isSelected(), is(true));
  });
}


function testCanAlterTheContentsOfAFileUploadInputElement(driver) {
  driver.get(TEST_PAGES.formPage);
  var uploadElement = driver.findElement(webdriver.By.id('upload'));
  assertThat(uploadElement.getValue(), equals(''));
  uploadElement.sendKeys('/some/file/path');
  assertThat(uploadElement.getValue(), equals('/some/file/path'));
}


// TODO(jmleyba): Expected failures
function testThrowsAnExceptionWhenSelectingAnUnselectableElement(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.xpath('//title')).setSelected();
  driver.callFunction(function() {
    fail('Should not be able to select unselectable element');
  });
}


function testSendkingKeyboardEventsShouldAppendTextInInputs(driver) {
  driver.get(TEST_PAGES.formPage);
  var element = driver.findElement(webdriver.By.id('working'));
  element.sendKeys('Some');
  assertThat(element.getValue(), equals('Some'));
  element.sendKeys(' text');
  assertThat(element.getValue(), equals('Some text'));
}


function testSendingKeyboardEventsShouldAppendTextInTextAreas(driver) {
  driver.get(TEST_PAGES.formPage);
  var element = driver.findElement(webdriver.By.id('withText'));
  element.sendKeys('. Some text');
  assertThat(element.getValue(), equals('Example text. Some text'));
}


function testCanClearTextFromInputElements(driver) {
  driver.get(TEST_PAGES.formPage);
  var element = driver.findElement(webdriver.By.id('working'));
  element.sendKeys('Some text');
  assertThat(element.getValue(), equals('Some text'));
  element.clear();
  assertThat(element.getValue(), equals(''));
}


function testCanClearTextFromTextAreas(driver) {
  driver.get(TEST_PAGES.formPage);
  var element = driver.findElement(webdriver.By.id('withText'));
  element.sendKeys('. Some text');
  assertThat(element.getValue(), equals('Example text. Some text'));
  element.clear();
  assertThat(element.getValue(), equals(''));
}
