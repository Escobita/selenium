function testShouldReturnInput(driver) {
  driver.get(TEST_PAGES.formPage);
  var element =  driver.findElement(webdriver.By.id('cheese'));
  assertThat(element.getElementName(), is('input'));
}
