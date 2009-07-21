function testCanDeselectASingleOptionFromASelectWhichAllowsMultipleChoices(
    driver) {
  driver.get(TEST_PAGES.formPage);
  var multiSelect = driver.findElement(webdriver.By.id("multi"));
  multiSelect.findElements(webdriver.By.tagName("option"));
  driver.callFunction(function(response) {
    var options = response.value;
    var option = options[0];
    assertThat(option.isSelected(), is(true));
    option.toggle();
    assertThat(option.isSelected(), is(false));
    option.toggle();
    assertThat(option.isSelected(), is(true));
    assertThat(options[2].isSelected(), is(true));
  });
}


function testShouldNotBeAbleToDeselectAnOptionFromANormalSelect(driver) {
  driver.get(TEST_PAGES.formPage);
  var select = driver.findElement(
      webdriver.By.xpath("//select[@name='selectomatic']"));
  select.findElements(webdriver.By.tagName("option"));
  driver.callFunction(function(response) {
    var options = response.value;
    options[0].toggle();
    driver.expectErrorFromPreviousCommand();
  });
}

function testShouldBeAbleToChangeTheSelectedOptionInASelect(driver) {
  driver.get(TEST_PAGES.formPage);
  var selectBox = driver.findElement(
      webdriver.By.xpath("//select[@name='selectomatic']"));
  selectBox.findElements(webdriver.By.tagName("option"));
  driver.callFunction(function(response) {
    var options = response.value;
    var one = options[0];
    var two = options[1];
    assertThat(one.isSelected(), is(true));
    assertThat(two.isSelected(), is(false));

    two.setSelected();
    assertThat(one.isSelected(), is(false));
    assertThat(two.isSelected(), is(true));
  });
}


function testCanSelectMoreThanOneOptionFromASelectWhichAllowsMultipleChoices(
    driver) {
  driver.get(TEST_PAGES.formPage);
  var multiSelect = driver.findElement(webdriver.By.id("multi"));
  multiSelect.findElements(webdriver.By.tagName("option"));
  driver.callFunction(function(response) {
    var options = response.value;
    goog.array.forEach(options, function(option) {
      option.setSelected();
    });
    goog.array.forEach(options, function(option, i) {
      assertThat(
          "Option at index is not selected but should be: " + i,
          option.isSelected(), is(true));
    });
  });
}
