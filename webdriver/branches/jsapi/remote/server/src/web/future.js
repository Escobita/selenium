goog.provide('webdriver.Future');

webdriver.Future = function(driver) {
  this.driver_ = driver;
  this.value_ = webdriver.Future.NOT_SET_;
};

webdriver.Future.NOT_SET_ = {};

webdriver.Future.prototype.getValue = function() {
  if (this.value_ === webdriver.Future.NOT_SET_) {
    throw new Error('Value has not been set yet');
  }
  return this.value_;
};


webdriver.Future.prototype.getDriver = function() {
  return this.driver_;
};


webdriver.Future.prototype.setValue = function(value) {
  this.value_ = value;
};


webdriver.Future.prototype.setValueFromResponse = function(response) {
  this.setValue(response.value);
};
