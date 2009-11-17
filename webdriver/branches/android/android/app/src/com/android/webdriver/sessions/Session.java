package com.android.webdriver.sessions;

import android.util.Log;


public class Session {

  /**
   * Defines action codes that can be performed by a session
   */
  public enum Actions {
    GET_PAGESOURCE,
    ELEMENT_EXISTS,
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
    void onChange(Session session, boolean syncronous);
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

  public Session(String contextId) {
    mSessionId = generateNextSessionId();
    mContext = contextId;
    mLastUrl = "Session " + mSessionId + ", nothing loaded.";
    mStatus = "Ready.";
  }

  public int getSessionId() {
    return mSessionId;
  }

  public void setContext(String mContext) {
    this.mContext = mContext;
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this, false);
  }

  public String getContext() {
    return mContext;
  }

  public void setUrl(String url) {
    setUrl(url, false);
  }

  public void setUrl(String url, boolean blocking) {
    if (mLastUrl.equals(url))
      return;

    this.mLastUrl = url;
    this.mStatus = "Loading " + url.substring(0, Math.min(url.length(), 40));
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this, blocking);
  }

  public String getLastUrl() {
    return mLastUrl;
  }

  public void setStatus(String mStatus) {
    this.mStatus = mStatus;
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this, false);
  }

  public String getStatus() {
    return mStatus;
  }

  public void setOnChangeListener(OnChangeListener listener) {
    mOnChangeListener = listener;
  }

  public void removeOnChangeListener(OnChangeListener listener) {
    if (listener == mOnChangeListener)
      mOnChangeListener = null;
  }

  public void setActionRequestListener(ActionRequestListener listener) {
    mActionRequestListener = listener;
  }

  public void removeActionRequestListener(ActionRequestListener listener) {
    if (listener == mActionRequestListener)
      mActionRequestListener = null;
  }

  @Override
  public String toString() {
    return Integer.toString(mSessionId) + "/" + mContext; 
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Session)
      return (mSessionId == ((Session)o).getSessionId() &&
          mContext.equals(((Session)o).getContext()));
    else
      return false;
  }

  public static int generateNextSessionId() {
    return ++lastId;
  }

  public void setTitle(String title) {
    this.mTitle = title;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setPageContent(String pageContent) {
    this.mPageContent = pageContent;
    if (mOnChangeListener != null)
      mOnChangeListener.onChange(this, false);
  }

  public String getPageContent() {
    return mPageContent;
  }

  public Object PerformAction(Actions action, Object[] args) {
    switch(action) {
      case GET_PAGESOURCE:
        break;
      case ELEMENT_EXISTS:
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

