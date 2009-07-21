function testShouldWaitForDocumentToBeLoaded(driver) {
  driver.get(TEST_PAGES.simpleTestPage);
  assertThat(driver.getTitle(), equals("Hello WebDriver"));
}


function testShouldFollowRedirectsSentInTheHttpResponseHeaders(driver) {
  driver.get(TEST_PAGES.redirectPage);
  assertThat(driver.getTitle(), equals("We Arrive Here"));
}


function testShouldFollowMetaRedirects(driver) {
  driver.get(TEST_PAGES.metaRedirectPage);
  assertThat(driver.getTitle(), equals("We Arrive Here"));
}


function testShouldBeAbleToGetAFragmentOnTheCurrentPage(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.get(TEST_PAGES.xhtmlTestPage + "#text");
}


function testShouldReturnWhenGettingAUrlThatDoesNotResolve(driver) {
  // Of course, we're up the creek if this ever does get registered
  driver.get("http://www.thisurldoesnotexist.comx/");
}


function testShouldReturnWhenGettingAUrlThatDoesNotConnect(driver) {
  // Here's hoping that there's nothing here. There shouldn't be
  driver.get("http://localhost:3001");
}


function testShouldBeAbleToLoadAPageWithFramesetsAndWaitUntilAllFramesAreLoaded(
    driver) {
  driver.get(TEST_PAGES.framesetPage);
  driver.switchToFrame(0);
  var pageNumber = driver.findElement(
      webdriver.By.xpath("//span[@id='pageNumber']"));
  assertThat(pageNumber.getText(), equals("1"));

  driver.switchToFrame(1);
  assertThat(
      driver.findElement(webdriver.By.xpath("//span[@id='pageNumber']")).
          getText(),
      equals('2'));
}


function testShouldDoNothingIfThereIsNothingToGoBackTo(driver) {
  driver.callFunction(function() {
    window.open('', 'a_clean_test_window');
  });
  driver.switchToWindow('a_clean_test_window');
  driver.get(TEST_PAGES.formPage);
  driver.back();
  assertThat(driver.getTitle(), equals("We Leave From Here"));
  driver.close();
}


function testShouldBeAbleToNavigateBackInTheBrowserHistory(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id("imageButton")).submit();
  assertThat(driver.getTitle(), equals("We Arrive Here"));
  driver.back();
  assertThat(driver.getTitle(), equals("We Leave From Here"));
}


function testShouldBeAbleToNavigateBackInTheBrowserHistoryInPresenceOfIframes(
    driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.findElement(webdriver.By.name("sameWindow")).click();
  assertThat(driver.getTitle(), equals("This page has iframes"));
  driver.back();
  assertThat(driver.getTitle(), equals("XHTML Test Page"));
}


function testShouldBeAbleToNavigateForwardsInTheBrowserHistory(driver) {
  driver.get(TEST_PAGES.formPage);
  driver.findElement(webdriver.By.id("imageButton")).submit();
  assertThat(driver.getTitle(), equals("We Arrive Here"));
  driver.back();
  assertThat(driver.getTitle(), equals("We Leave From Here"));
  driver.forward();
  assertThat(driver.getTitle(), equals("We Arrive Here"));
}


// TODO(jmleyba): This will suppress the test when _run_ on Firefox. But what if
// we're using a remote driver controlling something that isn't Firefox? We want
// the test to run in that case...
if (!goog.userAgent.GECKO) {
  var testShouldBeAbleToAccessPagesWithAnInsecureSslCertificate = function(
      driver) {
    var url = toSecureUrl(TEST_PAGES.simpleTestPage);
    driver.get(url);
    assertThat(driver.getTitle(), equals("Hello WebDriver"));
  };
}


function testShouldBeAbleToRefreshAPage(driver) {
  driver.get(TEST_PAGES.xhtmlTestPage);
  driver.refresh();
  assertThat(driver.getTitle(), equals("XHTML Test Page"));
}
