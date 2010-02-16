/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.webdriver.sessions;

import android.util.Log;

/**
 * Class that represents a single WebDriver session. 
 */
public class Session {

  /**
   * Defines action codes that can be performed by a session
   */
  public enum Actions {
    GET_PAGESOURCE,
    NAVIGATE_BACK,
    NAVIGATE_FORWARD,
    REFRESH,
    EXECUTE_JAVASCRIPT
  }

  /**
   * Interface definition for a callback to be invoked when any property of
   * the session is changed.
   */
  public interface OnChangeListener {
    /**
     * Called after assigning new value to the property
     *
     * @param session Session object.
     */
    void onChange(Session session);
  }

  /**
   * Interface definition for a callback to be invoked when any property of
   * the session is changed.
   */
  public interface ActionRequestListener {
    /**
     * Called after assigning new value to the property
     *
     * @param session Session object.
     */
    Object onActionRequest(Session session, Actions action, Object[] params);
  }

  /**
   * Constructor that creates a session for the given WebDriver context.
   *  
   * @param contextId WebDriver context string.
   */
  public Session(String contextId) {
    mSessionId = generateNextSessionId();
    mContext = contextId;
    mLastUrl = "Session " + mSessionId + ", nothing loaded.";
    mStatus = "Ready.";
  }

  /**
   * Returns unique session id.
   * 
   * @return Unique session id.
   */
  public int getSessionId() {
    return mSessionId;
  }

  /**
   * Set the WebDriver context name to use.
   * @param mContext WebDriver context string.
   */
  public void setContext(String mContext) {
    this.mContext = mContext;
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this);
  }

  /**
   * Return the current WebDriver context string.
   * @return Current WebDriver context string.
   */
  public String getContext() {
    return mContext;
  }

  /**
   * Set the new value of currently loaded URL.
   * Call to this function triggers request to UI subscriber to navigate to
   * the given URL.
   * <p>
   * Note: if given URL is equal to the currently loaded URL, the page
   * won't be reloaded.
   * 
   * @param url The new URL that this session should navigate to.
   */
  public void setUrl(String url) {
    if (mLastUrl.equals(url))
      return;

    this.mLastUrl = url;
    this.mStatus = "Loading " + url.substring(0, Math.min(url.length(), 40));
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this);
  }

  /**
   * Returns last URL that was passed to {@link #setUrl(String)}.
   *  
   * @return Last URL that was passed to {@link #setUrl(String)}.
   */
  public String getLastUrl() {
    return mLastUrl;
  }

  /**
   * Set session status description.
   * 
   * @param status String that represent a status of the session.
   */
  public void setStatus(String status) {
    this.mStatus = status;
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this);
  }

  /**
   * Returns the current status of the session.
   * 
   * @return Status of the session.
   */
  public String getStatus() {
    return mStatus;
  }

  /**
   * Set a callback listener for notifying on any change in session properties.
   * 
   * @param listener Callback listener.
   */
  public void setOnChangeListener(OnChangeListener listener) {
    mOnChangeListener = listener;
  }

  /**
   * Set listener to be notified when session requests a UI service to perform.
   * 
   * @param listener Callback listener.
   */
  public void setActionRequestListener(ActionRequestListener listener) {
    mActionRequestListener = listener;
  }

  @Override
  public String toString() {
    return Integer.toString(mSessionId) + "/" + mContext; 
  }

  /**
   * Returns true if sessions have the same Session Id and same Context.
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof Session)
      return (mSessionId == ((Session)o).getSessionId() &&
          mContext.equals(((Session)o).getContext()));
    else
      return false;
  }

  /**
   * Returns the next unused session id to assign to a newly created session.
   * 
   * @return Unique auto-incrementing session id.
   */
  public static int generateNextSessionId() {
    return ++lastId;
  }

  /**
   * Set title of currently loaded web page. This method is invoked by the UI
   * to notify the session regarding the title change.
   * 
   * @param title String title to set as web page title associated with
   *    this session.
   */
  public void setTitle(String title) {
    this.mTitle = title;
  }

  /**
   * Get title of currently loaded web page.
   * 
   * @return Title of currently loaded web page.
   */
  public String getTitle() {
    return mTitle;
  }

  /**
   * Update session about the content of the web page. This method is invoked
   * by the UI to notify the session regarding the web page content change.
   *   
   * @param pageContent Source of the session web page as a string. 
   */
  public void setPageContent(String pageContent) {
    this.mPageContent = pageContent;
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this);
  }

  /**
   * Returns the source of the web page loaded in the UI.
   *  
   * @return Source of the session web page as a string.
   */
  public String getPageContent() {
    return mPageContent;
  }

  /**
   * Initiate session-related actions (see {@link Actions}.
   * 
   * @param action Action to perform.
   * @param args Arguments of the action.
   * @return Any value returns as a result of action execution or null.
   */
  public Object PerformAction(Actions action, Object[] args) {
    switch(action) {
      case GET_PAGESOURCE:
        break;
      case EXECUTE_JAVASCRIPT:
        if (args.length != 1) {
          Log.e("Session", "Session " + getSessionId() +
              ": Invalid parameters of EXECUTE_JAVASCRIPT" +
              args.length);
          return null;
        }
        break;
    }

    Object result = null;
    if (mActionRequestListener != null)
      result = mActionRequestListener.onActionRequest(this, action, args);

    return result;
  }

  private static int lastId = 1000;
  private int mSessionId;
  private String mContext, mLastUrl, mStatus, mTitle, mPageContent;
  private OnChangeListener mOnChangeListener;
  private ActionRequestListener mActionRequestListener;
}

