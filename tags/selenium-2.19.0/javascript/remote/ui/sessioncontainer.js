// Copyright 2011 WebDriver committers
// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

goog.provide('remote.ui.SessionContainer');
goog.provide('remote.ui.SessionContainer.SessionTab_');

goog.require('goog.array');
goog.require('goog.dom.TagName');
goog.require('goog.events');
goog.require('goog.events.Event');
goog.require('goog.events.EventType');
goog.require('goog.structs.Map');
goog.require('goog.style');
goog.require('goog.ui.Component.EventType');
goog.require('goog.ui.Tab');
goog.require('goog.ui.TabBar');
goog.require('remote.ui.CreateSessionDialog');
goog.require('remote.ui.Event');
goog.require('remote.ui.FieldSet');
goog.require('remote.ui.SessionView');
goog.require('remote.ui.createControlBlock');


/**
 * A fieldset for displaying information about the active sessions on the
 * server.
 * @constructor
 * @extends {remote.ui.FieldSet}
 */
remote.ui.SessionContainer = function() {
  goog.base(this, 'Sessions');

  /**
   * Tabbar widget for selecting individual sessions.
   * @type {!goog.ui.TabBar}
   * @private
   */
  this.tabBar_ = new goog.ui.TabBar(goog.ui.TabBar.Location.START, null);

  /**
   * Widget for viewing details on an individual session.
   * @type {!remote.ui.SessionView}
   * @private
   */
  this.view_ = new remote.ui.SessionView();

  /**
   * Dialog for creating a new session.
   * @type {!remote.ui.CreateSessionDialog}
   * @private
   */
  this.createSessionDialog_ = new remote.ui.CreateSessionDialog();

  /**
   * Button that opens the {@link remote.ui.CreateSessionDialog}.
   * @type {!Element}
   * @private
   */
  this.createButton_ = this.getDomHelper().createDom(
      goog.dom.TagName.BUTTON, null, 'Create Session');

  /**
   * Button that refreshes the list of sessions.
   * @type {!Element}
   * @private
   */
  this.refreshButton_ = this.getDomHelper().createDom(
      goog.dom.TagName.BUTTON, null, 'Refresh Sessions');

  /**
   * List of buttons belonging to this component with event listeners that must
   * be removed upon disposal.
   * @type {!Array.<!Element>}
   */
  this.buttons_ = [];

  /**
   * Tracks any tabs for pending new sessions that are descendants of this
   * component.
   * @type {!Array.<!goog.ui.Tab>}
   * @private
   */
  this.pendingTabs_ = [];

  /**
   * Key for the interval that updates the content of the pending session tabs.
   * @type {number}
   * @private
   */
  this.updateKey_ = setInterval(goog.bind(this.updatePendingTabs_, this), 300);

  this.addChild(this.tabBar_);
  this.addChild(this.view_);
  this.setEnabled(false);

  goog.events.listen(this.createButton_, goog.events.EventType.CLICK,
      goog.bind(this.createSessionDialog_.setVisible, this.createSessionDialog_,
          true));
  goog.events.listen(this.refreshButton_, goog.events.EventType.CLICK,
      goog.bind(this.dispatchEvent, this, remote.ui.Event.Type.REFRESH));
  goog.events.listen(this.tabBar_, goog.ui.Component.EventType.SELECT,
      this.onSessionSelect_, false, this);
  goog.events.listen(this.createSessionDialog_,
      goog.ui.Component.EventType.ACTION, this.onCreateSession_, false, this);
};
goog.inherits(remote.ui.SessionContainer, remote.ui.FieldSet);


/** @override */
remote.ui.SessionContainer.prototype.disposeInternal = function() {
  goog.events.removeAll(this.createButton_);
  goog.events.removeAll(this.refreshButton_);

  clearInterval(this.updateKey_);
  this.createSessionDialog_.dispose();

  delete this.createSessionDialog_;
  delete this.tabBar_;
  delete this.view_;
  delete this.pendingTabs_;
  delete this.buttons_;
  delete this.updateKey_;

  goog.base(this, 'disposeInternal');
};


/** @override */
remote.ui.SessionContainer.prototype.createFieldSetDom = function() {
  this.tabBar_.createDom();
  this.view_.createDom();

  var dom = this.getDomHelper();
  return dom.createDom(goog.dom.TagName.DIV,
      'session-container',
      remote.ui.createControlBlock(dom,
          this.createButton_, this.refreshButton_),
      this.tabBar_.getElement(),
      this.view_.getElement());
};


/**
 * @param {boolean} enabled Whether this container should be enabled.
 */
remote.ui.SessionContainer.prototype.setEnabled = function(enabled) {
  if (enabled) {
    this.createButton_.removeAttribute('disabled');
    this.refreshButton_.removeAttribute('disabled');
  } else {
    this.createButton_.setAttribute('disabled', 'disabled');
    this.refreshButton_.setAttribute('disabled', 'disabled');
  }
};


/**
 * Returns the session associated with the currently selected tab.
 * @return {webdriver.Session} The selected session, or null if there is none.
 */
remote.ui.SessionContainer.prototype.getSelectedSession = function() {
  var tab = this.tabBar_.getSelectedTab();
  return tab ? tab.session_ : null;
};


/**
 * Interval callback that updates the displayed content for pending session
 * tabs.
 * @private
 */
remote.ui.SessionContainer.prototype.updatePendingTabs_ = function() {
  if (!this.pendingTabs_.length) {
    return;
  }

  // Compute the new content once.
  var content = this.pendingTabs_[0].getContent();
  content = content.length === 5 ? '.' : content + '.';

  goog.array.forEach(this.pendingTabs_, function(tab) {
    tab.setContent(content);
  });
};


/**
 * Adjusts the height of the session view so it is always relative to our
 * tab bar.
 * @private
 */
remote.ui.SessionContainer.prototype.adjustSessionViewSize_ = function() {
  var size = goog.style.getSize(this.tabBar_.getElement());
  this.view_.setHeight(size.height + 20);
};


/**
 * Adds a tab to represent a "pending" session. Pending tabs will be replaced
 * in FIFO order as new sessions are registered by the server.
 * @private
 */
remote.ui.SessionContainer.prototype.addPendingTab_ = function() {
  var content = '.';
  if (this.pendingTabs_.length) {
    content = this.pendingTabs_[0].getContent();
  }

  var tab = new goog.ui.Tab(content, null, this.getDomHelper());
  tab.setEnabled(false);
  this.pendingTabs_.push(tab);
  this.tabBar_.addChild(tab, true);
  this.adjustSessionViewSize_();
};


/**
 * Adds a new session to this container. The new session will be selected for
 * viewing.
 * @param {!webdriver.Session} session The new session.
 */
remote.ui.SessionContainer.prototype.addSession = function(session) {
  var tab = new remote.ui.SessionContainer.SessionTab_(session);

  // Replace the first non-session tab with the new session.
  var pending = this.pendingTabs_.shift();
  var index = this.tabBar_.indexOfChild(pending);
  if (index < 0) {  // Only happens if !pending === true.
    this.tabBar_.addChild(tab, true);
  } else {
    this.tabBar_.addChildAt(tab, index, true);
    this.tabBar_.removeChild(pending, true);
  }

  this.adjustSessionViewSize_();
  this.tabBar_.setSelectedTab(tab);
};


/**
 * Removes a session from this container.
 * @param {!webdriver.Session} session The session to remove.
 */
remote.ui.SessionContainer.prototype.removeSession = function(session) {
  var selectedTab = this.tabBar_.getSelectedTab();
  var tabToRemove;
  var n = this.tabBar_.getChildCount();
  for (var i = 0; i < n; ++i) {
    var tab = this.tabBar_.getChildAt(i);
    if (tab.session_.getId() == session.getId()) {
      tabToRemove = tab;
      break;
    }
  }

  if (tabToRemove) {
    this.tabBar_.removeChild(tabToRemove, true);
    tabToRemove.dispose();
    if (selectedTab == tabToRemove && !!this.tabBar_.getChildCount()) {
      this.tabBar_.setSelectedTabIndex(0);
    } else {
      this.view_.update(null);
    }
  }
};


/**
 * Updates the sessions displayed by this container. Any sessions new sessions
 * will be added, while tabs not present in the input list will be removed. The
 * last tab selected before this function was called will be selected when it is
 * finished. If this tab was removed, the first displayed tab will be selected.
 * @param {!Array.<!webdriver.Session>} sessions List of sessions to refresh
 *     our view with.
 */
remote.ui.SessionContainer.prototype.refreshSessions = function(sessions) {
  var newSessionsById = new goog.structs.Map();
  goog.array.forEach(sessions, function(session) {
    newSessionsById.set(session.getId(), session);
  });

  var tabBar = this.tabBar_;
  var selectedTab = tabBar.getSelectedTab();
  var sessionTabs = [];
  var n = tabBar.getChildCount() - this.pendingTabs_.length;
  for (var i = 0; i < n; ++i) {
    sessionTabs.push(tabBar.getChildAt(i));
  }

  // Remove any old sessions and refresh those whose IDs match.
  goog.array.forEach(sessionTabs, function(tab) {
    var id = tab.session_.getId();
    var newSession = newSessionsById.get(id);
    if (newSession) {
      newSessionsById.remove(id);
      tab.session_ = newSession;
    } else {
      tabBar.removeChild(tab, true);
      if (selectedTab === tab) {
        selectedTab = null;
      }
    }
  }, this);

  // Remove all of our pending session tabs. As far as we know, we have an
  // up-to-date list of sessions.
  goog.array.forEach(this.pendingTabs_, function(tab) {
    tabBar.removeChild(tab, true);
  });
  this.pendingTabs_ = [];

  // Add the new sessions.
  goog.array.forEach(newSessionsById.getValues(), this.addSession, this);

  // Update our selection.
  if (selectedTab) {
    this.view_.update(selectedTab.session_);
    tabBar.setSelectedTab(selectedTab);
  } else if (tabBar.getChildCount()) {
    tabBar.setSelectedTabIndex(0);
  } else {
    this.view_.update(null);
  }
};


/**
 * Callback for when the user has selected to create a new session.
 * Dispatches a {@link remote.ui.Event.Type.CREATE} event with the desired
 * capabilities for the new session as data.
 * @private
 */
remote.ui.SessionContainer.prototype.onCreateSession_ = function() {
  this.addPendingTab_();
  var event = new remote.ui.Event(remote.ui.Event.Type.CREATE, this,
      this.createSessionDialog_.getUserSelection());
  this.dispatchEvent(event);
};


/**
 * Event handler for when users select a session from our tabbar.
 * @private
 */
remote.ui.SessionContainer.prototype.onSessionSelect_ = function() {
  var tab = (/** @type {!remote.ui.SessionContainer.SessionTab_} */
      this.tabBar_.getSelectedTab());
  this.view_.update(tab ? tab.session_ : null);
};



/**
 * A single tab in a {@link remote.ui.SessionContainer}. Each tab represents an
 * active session on the WebDriver server.
 * @param {!webdriver.Session} session The session for this tab.
 * @constructor
 * @extends {goog.ui.Tab}
 * @private
 */
remote.ui.SessionContainer.SessionTab_ = function(session) {
  var browser = session.getCapability('browserName') || 'unknown browser';
  browser = browser.toLowerCase().replace(/(^|\b)[a-z]/g, function(c) {
    return c.toUpperCase();
  });

  goog.base(this, browser);

  /**
   * The session for this tab.
   * @type {!webdriver.Session}
   * @private
   */
  this.session_ = session;
};
goog.inherits(remote.ui.SessionContainer.SessionTab_, goog.ui.Tab);


/** @override */
remote.ui.SessionContainer.SessionTab_.prototype.disposeInternal = function() {
  delete this.session_;
  goog.base(this, 'disposeInternal');
};
