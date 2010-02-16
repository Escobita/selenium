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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.android.webdriver.sessions.Session.Actions;

public class SessionRepository implements Session.OnChangeListener,
                                          Session.ActionRequestListener {
	
  public static final int SINGLE_SESSION_ID = 1000;  // For single-session mode

  /**
   * Describes what type of change occurred with the session
   */
  public enum SessionChangeType {UPDATED, ADDED, REMOVED}

  /**
   * Interface definition for a callback to be invoked when any property of
   * the session is changed.
   */
  public interface OnSessionChangeListener {
    /**
     * Called after any session was updated.
     *
     * @param changedSession Session object that was changed.
     */
    void onSessionChange(Session changedSession, SessionChangeType type);
  }

  /**
   * Interface definition for a callback to be invoked when any property of
   * the session is changed.
   */
  public interface SessionActionRequestListener {
    /**
     * Called after assigning new value to the property
     *
     * @param session Session object.
     */
    Object onActionRequest(Session session, Actions action, Object[] params);
  }

  /** Private constructor -- for singleton housekeeping. */
  private SessionRepository() { }

  /**
   * Returns a singleton instance of the cookie manager object.
   * 
   * @return Singleton instance of the cookie manager object.
   */
  public static SessionRepository getInstance() {
    if (mInstance == null)
      mInstance = new SessionRepository();

    return mInstance;
  }

  /**
   * Add a new session with a given WebDriver context. Session id is
   * automatically generated and assigned.
   * 
   * @param context WebDriver session context 
   * @return Created session object.
   */
  public Session add(String context) {
    Session s = new Session(context);
    s.setOnChangeListener(this);
    s.setActionRequestListener(this);
    mSessions.add(s);
    mIndexes.put(s.getSessionId(), s);
    if (mOnSessionChangeListener != null)
      mOnSessionChangeListener.onSessionChange(s, SessionChangeType.ADDED);
    return s;
  }

  /**
   * Returns number of active sessions.
   * 
   * @return Number of active sessions.
   */
  public int size() {
    return mSessions.size();
  }

  /**
   * Returns session by its position in session collection.
   * 
   * @param position Position of the session to return [0...size()-1].
   * @return Session at given position or null if position in out of bounds.
   */
  public Session get(int position) {
    if (mSessions.size() >= position + 1)
      return mSessions.get(position);
    else
      return null;
  }

  /**
   * Returns session by session id.
   * 
   * @param sessionId Session id to look for.
   * @return Session with given id or null if not found.
   */
  public Session getById(int sessionId) {
    return mIndexes.get(sessionId);
  }

  /**
   * Returns index of the given session in session collection.
   * 
   * @param session Session to look for.
   * @return Index of the given session in the collection or -1 if not found.
   */
  public int indexOf(Session session) {
    return mSessions.indexOf(session);
  }

  /**
   * Remove session at given position in session collection.
   * 
   * @param position Position in session collection to remove session at.
   */
  public void removeAt(int position) {
    remove(mSessions.get(position));
  }

  /**
   * Remove session by session id.
   * 
   * @param sessionId Session id to look for.
   */
  public void removeById(int sessionId) {
    remove(mIndexes.get(sessionId));
  }

  /**
   * Remove the given session.
   * 
   * @param session Session to remove.
   */
  public void remove(Session session) {
    mIndexes.remove(session.getSessionId());
    mSessions.remove(session);
    if (mOnSessionChangeListener != null)
      mOnSessionChangeListener.onSessionChange(session,
          SessionChangeType.REMOVED);
  }

  /**
   * Remove all sessions from session collection.
   */
  public void removeAll() {
    Enumeration<Integer> e = mIndexes.keys();
    while(e.hasMoreElements()) {
      Integer key = e.nextElement();
      remove(mIndexes.get(key));
    }
  }

  /**
   * Set listener to be notified of session changes.
   * 
   * @param listener Listener to be notified of session changes.
   */
  public void setOnChangeListener(OnSessionChangeListener listener) {
    mOnSessionChangeListener = listener;
  }

  /**
   * Handler of session's change callback.
   * It invokes the external listener and passes session as an argument.
   */
  public void onChange(Session session) {
    if (mOnSessionChangeListener != null)
      mOnSessionChangeListener.onSessionChange(session,
          SessionChangeType.UPDATED);
  }

  /**
   * Sets listener to handle action requests by a session.
   *   
   * @param listener Listener to handle action requests by a session.
   */
  public void setSessionActionRequestListener(
      SessionActionRequestListener listener) {
    this.mSessionActionRequestListener = listener;
  }

  /**
   * Handler for requesting action by the session.
   */
  public Object onActionRequest(Session session, Actions action,
      Object[] params) {
    Object result = null;
    if (mSessionActionRequestListener != null)
      result = mSessionActionRequestListener.onActionRequest(session,
          action, params);
    return result;
  }

  // Private members
  private ArrayList<Session> mSessions = new ArrayList<Session>();
  private Hashtable<Integer,Session> mIndexes =
    new Hashtable<Integer,Session>();
  private static SessionRepository mInstance = null;
  private OnSessionChangeListener mOnSessionChangeListener;
  private SessionActionRequestListener mSessionActionRequestListener;
}
