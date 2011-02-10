goog.provide('selenium.safari.Extension');


//selenium.safari.Extension.SESSION_ID = 'safariSession';

/**
 * The global extension WebDriver object.
 *
 * @constructor
 */
selenium.safari.Extension = function() {

  /**
   * The URL of the remote controller WS server.
   */
  this.wsUrl_ = null;

  /**
   * The WebSocket connected to the remote controller.
   */
  this.ws_ = null;

  /**
   * The sessionId currently in use.
   */
  this.sessionId_ = null;


  /**
   * The bindings for the WS event handling mechanism.
   */
  this.bindings_ = {};

  this.logs = Array();
};

/**
 * Initializes the extension.
 * @private
 */
selenium.safari.Extension.prototype.init = function() {
  var hostPort = "localhost:7055";
  var firstUrl = safari.application.activeBrowserWindow.tabs[0].url;
  if (!firstUrl || firstUrl.indexOf("init_webdriver") == -1) {
     safari.extension.bars[0].contentWindow.document.getElementById('topStatus').innerHTML = "inactive.";
     return;
  }

  hostPort = firstUrl.split("\/")[2];
  this.wsUrl_ = "ws://" + hostPort + "/safaridriver";

  this.setStatus_("Connecting to " + this.wsUrl_);

  this.initBindings_();

  this.ws_ = new WebSocket(this.wsUrl_);
  this.ws_.onopen = goog.bind(this.onWSOpen_, this);
  this.ws_.onmessage = goog.bind(this.onWSMessage_, this);

};

selenium.safari.Extension.prototype.initBindings_ = function() {
  this.bindings_.newSession = goog.bind(this.newSession_, this);
  this.bindings_.quit = goog.bind(this.quit_, this);
  this.bindings_.get = goog.bind(this.get_, this);
};

selenium.safari.Extension.prototype.setStatus_ = function(status) {
  safari.extension.bars[0].contentWindow.document.getElementById('status')
    .innerHTML = status;
};

selenium.safari.Extension.prototype.addLog_ = function(message) {
  // TODO(kurniady): make this multiple-entry-capable
  safari.extension.bars[0].contentWindow.document.getElementById('log')
    .innerHTML = message;
  this.logs.push(message);
};

selenium.safari.Extension.prototype.sendResponse_ = function(status, message, opt_sessionId) {

  var response = {
        "status": status,
        "sessionId": (opt_sessionId ? opt_sessionId : this.sessionId_),
        "value": {"message": message}
      };

  // Scrub nulls.
  if (response.sessionId == null) {
    delete(response.sessionId);
  }
  if (response.message == null) {
    delete(response.message);
  }

  var responseString = JSON.stringify(response);
  this.ws_.send(responseString);
  this.addLog_("Sent response: " + responseString);
  // TODO(kurniady): Status indicator fade-out here.
};

selenium.safari.Extension.prototype.newSession_ = function(json) {
  if (this.sessionId_) {
    // TODO(kurniady): Check which sessionId should be returned in this case.
    this.sendResponse_(500, "SafariDriver does not support multiple sessions");
    return;
  }
  this.sessionId_ = "SAFARI_DRIVER_SESSION";
  this.sendResponse_(0, null);
};

selenium.safari.Extension.prototype.quit_ = function(json) {
  this.sessionId_ = null;
  this.sendResponse_(0, null);
};

selenium.safari.Extension.prototype.get_ = function(json) {
  safari.application.activeBrowserWindow.tabs[0].url = json.url;
  // TODO(kurniady): Add a wait until loaded here.
  this.sendResponse_(0, null);
};

selenium.safari.Extension.prototype.onWSOpen_ = function() {
  this.setStatus_("Connected to " + this.wsUrl_);
  this.addLog_("Connected to " + this.wsUrl_);
};

selenium.safari.Extension.prototype.onWSMessage_ = function(event) {
  // TODO(kurniady): status indicator fade-in here.
  this.addLog_("Received " + event.data);
  var data = JSON.parse(event.data);

  if (data.sessionId && data.sessionId != this.sessionId_) {
    this.sendResponse_(500, "Unknown session " + data.sessionId, data.sessionId);
  }

  var target = this.bindings_[data.command];
  if (target) {
    target(data);
  } else {
    this.sendResponse_(501, data.command + " not implemented yet.");
  }
};

var extension = new selenium.safari.Extension();
setTimeout("extension.init()", 1000);
