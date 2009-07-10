goog.require('goog.math.Coordinate');
goog.require('goog.math.Size');
goog.require('goog.string');
goog.require('webdriver.By');
goog.require('webdriver.WebElement');
goog.require('webdriver.asserts');

/**
 * Hackery! When this file is loaded, we also want to load all of the individual
 * test files.
 */
(function() {
  var testFiles = [
    'childrenfinding_test.js',
    'correcteventfiring_test.js',
    'draganddrop_test.js',
    'elementattribute_test.js',
    'elementfinding_test.js',
    'elementname_test.js',
    'executingjavascript_test.js',
    'formhandling_test.js'
  ];
  for (var i = 0, file; file = testFiles[i]; i++) {
    document.write(
        '<script type="text/javascript" src="' + file + '"></script>');
  }
})();


function whereIs(file) {
  if (!this.currentLocation) {
    this.currentLocation = window.location.href.match(/(.*\/)/)[1];
  }
  // TODO(jmleyba): This needs to be cleaned up so we can automate running these
  // tests with a Java client.
  return this.currentLocation + '../../../../../common/src/web/' + file;
}


var TEST_PAGES = {
  simpleTestPage: whereIs('simpleTest.html'),
  xhtmlTestPage: whereIs('xhtmlTest.html'),
  formPage: whereIs('formPage.html'),
  metaRedirectPage: whereIs('meta-redirect.html'),
  redirectPage: whereIs('redirect'),
  javascriptEnhancedForm: whereIs('javascriptEnhancedForm.html'),
  javascriptPage: whereIs('javascriptPage.html'),
  framesetPage: whereIs('frameset.html'),
  iframePage: whereIs('iframes.html'),
  dragAndDropPage: whereIs('dragAndDropTest.html'),
  chinesePage: whereIs('cn-test.html'),
  nestedPage: whereIs('nestedElements.html')
};


var openedTestWindow = false;


function setUp(driver) {
  if (!openedTestWindow) {
    openedTestWindow = true;
    driver.callFunction(function() {
      window.open('', 'test_window');
    });
  }
  driver.switchToWindow('test_window');
}


// TODO(jmleyba): Implement setUpPage and tearDownPage
function tearDownPage(driver) {
  // webdriver.logging.setLevel(webdriver.logging.Level.INFO);
  // TODO(jmleyba): How do we make this not close ourselves?
  driver.close();
}
