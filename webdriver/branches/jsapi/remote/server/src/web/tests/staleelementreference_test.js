function testOldPage(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var element = driver.findElement(webdriver.By.id('links'));
  driver.get(TEST_PAGES.xhtmlTestPage);
  element.click();
  driver.expectErrorFromPreviousCommand('Element should be stale');
}


function testShouldNotCrashWhenCallingGetSizeOnAnObsoleteElement(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  var element = driver.findElement(webdriver.By.id('links'));
  driver.get(TEST_PAGES.xhtmlTestPage);
  element.getSize();
  driver.expectErrorFromPreviousCommand('Element should be stale');
}


function testDynamicallyRemovingAnElementFromTheDomCausesAStaleReference(
    driver) {
  driver.get(TEST_PAGES.javascriptPage);
  var toDelete = driver.findElement(webdriver.By.id('deleted'));
  assertThat(toDelete.isDisplayed(), is(true));
  driver.findElement(webdriver.By.id('delete')).click();
  toDelete.isDisplayed();
  driver.expectErrorFromPreviousCommand(
      'Element should be stale at this point');
}
