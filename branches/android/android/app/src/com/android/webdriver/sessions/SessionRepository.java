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
	
	private SessionRepository() { }
	public static SessionRepository getInstance() {
		if (mInstance == null)
			mInstance = new SessionRepository();
		
		return mInstance;
	}

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
	
	public int size() {
		return mSessions.size();
	}
	
	public Session get(int position) {
		if (mSessions.size() >= position + 1)
			return mSessions.get(position);
		else
			return null;
	}
	
	public Session getById(int sessionId) {
		return mIndexes.get(sessionId);
	}
	
	public int indexOf(Session session) {
		return mSessions.indexOf(session);
	}
	
	public void removeAt(int position) {
		remove(mSessions.get(position));
	}
	
	public void removeById(int sessionId) {
		remove(mIndexes.get(sessionId));
	}
	
	public void remove(Session session) {
		mIndexes.remove(session.getSessionId());
		mSessions.remove(session);
		if (mOnSessionChangeListener != null)
			mOnSessionChangeListener.onSessionChange(session,
					SessionChangeType.REMOVED);
	}
	
	public void removeAll() {
		Enumeration<Integer> e = mIndexes.keys();
		while(e.hasMoreElements()) {
			Integer key = e.nextElement();
			remove(mIndexes.get(key));
		}
	}
	
	public void setOnChangeListener(OnSessionChangeListener listener) {
		mOnSessionChangeListener = listener;
	}
	
    public void removeOnChangeListener(OnSessionChangeListener listener) {
        if (listener == mOnSessionChangeListener)
        	mOnSessionChangeListener = null;
    }

  public void onChange(Session session) {
    if (mOnSessionChangeListener != null)
      mOnSessionChangeListener.onSessionChange(session,
          SessionChangeType.UPDATED);
  }

	public void setSessionActionRequestListener(
			SessionActionRequestListener listener) {
		this.mSessionActionRequestListener = listener;
	}
	
	public SessionActionRequestListener getSessionActionRequestListener() {
		return mSessionActionRequestListener;
	}
	
	public Object onActionRequest(Session session, Actions action,
			Object[] params) {
		Object result = null;
		if (mSessionActionRequestListener != null)
			result = mSessionActionRequestListener.onActionRequest(session,
					action, params);
		return result;
	}

    private ArrayList<Session> mSessions = new ArrayList<Session>();
    private Hashtable<Integer,Session> mIndexes =
    	new Hashtable<Integer,Session>();
    private static SessionRepository mInstance = null;
    private OnSessionChangeListener mOnSessionChangeListener;
    private SessionActionRequestListener mSessionActionRequestListener;
}
