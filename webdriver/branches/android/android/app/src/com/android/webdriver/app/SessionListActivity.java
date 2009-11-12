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

package com.android.webdriver.app;


import java.util.ArrayList;
import java.util.Hashtable;

import com.android.webdriver.app.R;
import com.android.webdriver.sessions.Session;
import com.android.webdriver.sessions.SessionRepository;
import com.android.webdriver.sessions.Session.Actions;
import com.android.webdriver.sessions.SessionRepository.SessionChangeType;
import com.android.webdriver.sessions.intents.AddSessionIntentReceiver;
import com.android.webdriver.sessions.intents.DeleteSessionIntentReceiver;
import com.android.webdriver.sessions.intents.DoActionIntentReceiver;
import com.android.webdriver.sessions.intents.GetCurrentUrlIntentReceiver;
import com.android.webdriver.sessions.intents.GetTitleIntentReceiver;
import com.android.webdriver.sessions.intents.IntentReceiverRegistrar;
import com.android.webdriver.sessions.intents.Intents;
import com.android.webdriver.sessions.intents.NavigationIntentReceiver;
import com.android.webdriver.sessions.intents.SetProxyIntentReceiver;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;


/**
 * A main view of the application.
 */
public class SessionListActivity extends ListActivity 
	 implements View.OnCreateContextMenuListener, 
	 			SessionRepository.OnSessionChangeListener,
	 			SessionRepository.SessionActionRequestListener
{
  private SessionRepository sessionRep;
  private ArrayList<WebView> mWebViews = new ArrayList<WebView>();
  private IntentReceiverRegistrar mIntentReg;
      
  public static final long COMMAND_TIMEOUT = 2500L; // in milliseconds
  
  public static final int MENU_ADD = Menu.FIRST + 1;
  public static final int MENU_CLOSE_ALL = Menu.FIRST + 2;
  
  public static final int CTX_MENU_DELETE = Menu.FIRST + 11;
  public static final int CTX_MENU_NAVIGATE = Menu.FIRST + 12;
  
  final Object syncNavObj = new Object();
    
  public SessionListActivity() {
    sessionRep = SessionRepository.getInstance();

    mIntentReg = new IntentReceiverRegistrar(this);
  }

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.getListView().setOnCreateContextMenuListener(this);

        this.setTitle("Android WebDriver Client");
        
        // Use our own list adapter
        setListAdapter(new SessionListAdapter(this));
		sessionRep.setOnChangeListener(this);
		sessionRep.setSessionActionRequestListener(this);
		
		// Registering all intent receivers
		mIntentReg.RegisterReceiver(new NavigationIntentReceiver(),
				Intents.INTENT_NAVIGATE);
		mIntentReg.RegisterReceiver(new AddSessionIntentReceiver(),
				Intents.INTENT_ADDSESSION);
		mIntentReg.RegisterReceiver(new DeleteSessionIntentReceiver(),
				Intents.INTENT_DELETESESSION);
		mIntentReg.RegisterReceiver(new DoActionIntentReceiver(),
				Intents.INTENT_DOACTION);
		mIntentReg.RegisterReceiver(new GetTitleIntentReceiver(),
				Intents.INTENT_GETTITLE);
        mIntentReg.RegisterReceiver(new SetProxyIntentReceiver(),
            Intents.INTENT_SETPROXY);
        mIntentReg.RegisterReceiver(new GetCurrentUrlIntentReceiver(),
            Intents.INTENT_GETURL);

        Log.i("WebDriver", "Loaded.");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem add = menu.add(0, MENU_ADD, 0, R.string.menu_add_session);
    	add.setIcon(R.drawable.ic_menu_add);
    	
        MenuItem close = menu.add(0, MENU_CLOSE_ALL, 0,
        		R.string.close_all_sessions);
        close.setIcon(R.drawable.ic_menu_delete);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD:
            	sessionRep.add("bar");
                return true;
            case MENU_CLOSE_ALL:
            	sessionRep.removeAll();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu,
    		View v, ContextMenu.ContextMenuInfo menuInfo) {
    	AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
    	menu.setHeaderTitle("Session: " +
    			((SessionView)acmi.targetView).getTitle());
    	
    	menu.add(0, CTX_MENU_NAVIGATE, 0, R.string.ctx_menu_navigate);
    	menu.add(0, CTX_MENU_DELETE, 1, R.string.ctx_menu_delete);
    	super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo i =
        	(AdapterContextMenuInfo)item.getMenuInfo();
    	
        Handler urlHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	((SessionListAdapter)getListAdapter()).collapseAll();
            	String url = (String)msg.obj;
            	Intent intent = new Intent(Intents.INTENT_NAVIGATE);
            	intent.putExtra("SessionId",
            			sessionRep.get(i.position).getSessionId());
            	intent.putExtra("URL", url);
            	sendBroadcast(intent);
                Log.i("WebDriver", "Navigating to " + url);
            }
        };
        
        Handler confirmHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	((SessionListAdapter)getListAdapter()).collapseAll();
        		Intent intent = new Intent(Intents.INTENT_DELETESESSION);
            	intent.putExtra("SessionId",
            			sessionRep.get(i.position).getSessionId());
            	sendBroadcast(intent);
        		//mWebViews.remove(i.position);
            }
        };

        switch (item.getItemId()) {
	        case CTX_MENU_NAVIGATE:
	        	Alerts.inputBox(this, urlHandler, "Enter URL:", "");
	            return true;
	        case CTX_MENU_DELETE:
	        	Alerts.Confirm(this, confirmHandler,
	        			"Delete this session?");
	        	return true;
        }

        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
      ((SessionListAdapter)getListAdapter()).toggle(position);
    }

  public void onSessionChange(Session changedSession, SessionChangeType type) {
    Log.d("WebDriver", "Session: " + changedSession.toString() +
        " was " + type.toString());

    switch(type) {
      case ADDED:
        // We might need to add new WebView
        if (mWebViews.size() + 1 == sessionRep.size()) {
          mWebViews.add(createWebView(/* changedSession */));
          Log.w("onSessionChange", "WebView Added");
        }
        break;
      case REMOVED:
        break;
      case UPDATED:
        break;
      case UPDATED_SYNC:
        //int position = sessionRep.indexOf(changedSession);
        //if (position < 0)
        //  Log.e("onSessionChange", "Session: " + changedSession.toString() +
        //      " not found. No synchronous methods available");
        //else
        //  setWebViewParam(mWebViews.get(position), "NAVIGATE_SYNC", true);
        break;
    }
    ((SessionListAdapter)this.getListAdapter()).notifyDataSetChanged();
    
    if (type == SessionChangeType.UPDATED_SYNC) {
      Log.d("WebDriver:onSessionChange", "Going to wait...");
      int position = sessionRep.indexOf(changedSession);
      if (position < 0) {
        Log.e("WebDriver:onSessionChange", "Waiting error");
        return;
      }
      ((NavigationExecutor)getWebViewParam(
          mWebViews.get(position), "NAVIGATION_EXECUTOR"))
            .waitForPageLoad(changedSession);

//      synchronized (changedSession.syncNavObj) {
//        try {
//          changedSession.syncNavObj.wait(SYNC_TIMEOUT);
//        } catch (InterruptedException e) { }
//      }
    }
  }

  /**
   * Creates and returns an empty WebView view.
   * 
   * @return New WebView.
   */
  private WebView createWebView(/* Session s*/) {
      WebView wv = new WebView(this);
      wv.setWebViewClient(new LocalWebViewClient());
      wv.setFocusable(false);
      wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      wv.getSettings().setBuiltInZoomControls(true);
      wv.getSettings().setJavaScriptEnabled(true);
      wv.setWebChromeClient(new LocalWebChromeClient());
      CustomJavaScriptInterface script = new CustomJavaScriptInterface(wv);
      WebViewJSExecutor executor = new WebViewJSExecutor();
      script.setWebViewJSExecutor(executor);
      wv.addJavascriptInterface(script, "webdriver");
      setWebViewParam(wv, "EXECUTOR", executor);
      NavigationExecutor navexecutor = new NavigationExecutor();
      setWebViewParam(wv, "NAVIGATION_EXECUTOR", navexecutor);
      //setWebViewParam(wv, "SESSION_ID", s.getSessionId());
      return wv;
  }

  class NavigationExecutor {
    public void waitForPageLoad(Session session) {
      int position = sessionRep.indexOf(session);
      if (position >= 0) {
          synchronized (this) {
            Log.d("WaitForPageLoad", "Waiting for page load");
            WebView wv = mWebViews.get(position);
            try {
              this.wait(COMMAND_TIMEOUT);
            } catch (InterruptedException ie) {   }
          }
      }
    }
    public void pageLoaded(Session session) {
      synchronized (this) {
        Log.d("WaitForPageLoad", "Finished");
        this.notifyAll();
      }
    }
  }
  
  public Object onActionRequest(Session session, Actions action,
			Object[] params) {
		
        int position = sessionRep.indexOf(session);
        if (position < 0 || position >= sessionRep.size()) {
          Log.e("ActionRequest", "Action: " + action.name() +
              " requested for unknown session: " + session.getSessionId());
          return null;
        }
		switch(action) {
			case GET_DOM:
              return ((WebViewJSExecutor)getWebViewParam(
				mWebViews.get(position), "EXECUTOR")).executeJS(session,
				"window.webdriver.resultMethod"
				+ "(document.documentElement.outerHTML);");
			case ELEMENT_EXISTS:
			  // TODO(abergman): implement
			  break;
			case NAVIGATE_BACK:
              mWebViews.get(position).goBack();
              break;
            case NAVIGATE_FORWARD:
              mWebViews.get(position).goForward();
              break;
            case REFRESH:
              mWebViews.get(position).reload();
              break;
            case EXECUTE_JAVASCRIPT:
              return ((WebViewJSExecutor)getWebViewParam(
                mWebViews.get(position), "EXECUTOR")).executeJS(session,
                params[0].toString());
		}
		return null;
	}

	class WebViewJSExecutor {
		public String executeJS(Session session, String jsCode) {
			int position = sessionRep.indexOf(session);
			if (position >= 0) {
				synchronized (syncObj) {
		          res = "";
		          running = true;
				  Log.d("WebViewJSExecutor", "Running script: " + jsCode);
				  WebView wv = mWebViews.get(position);
				  wv.loadUrl("javascript:" + jsCode);
				  Log.d("WebViewJSExecutor", "waiting...");
				  try {
				    syncObj.wait(COMMAND_TIMEOUT);
				  } catch (InterruptedException ie) {	}
				  running = false;
				}
			}
			if (res.length() == 0)
			  Log.d("WebViewJSExecutor", "Returning empty result");
			return res;
		}
		public void resultAvailable(Session session, String result) {
			synchronized (syncObj) {
			  Log.d("WebViewJSExecutor", "Script finished");
			  if (running) {
	            Log.d("WebViewJSExecutor", "returning result");
			    res = result;
  			    syncObj.notifyAll();
			  }
			}
		}
		
		private final Object syncObj = new Object();
		private String res = "";
		private boolean running = false;
	}
	
    final class CustomJavaScriptInterface {
    	WebView mWebView = null;

    	CustomJavaScriptInterface(WebView wv) {
        	mWebView = wv;
        }

    	public void setWebViewJSExecutor(WebViewJSExecutor executor) {
    		mWebViewJSExecutor = executor;
    	}
    	
        public void resultMethod(String result) {
    		int viewPos = mWebViews.indexOf(mWebView);
        	if (mWebViewJSExecutor != null && viewPos >= 0 &&
        			sessionRep.get(viewPos) != null) {
        		mWebViewJSExecutor.resultAvailable(
        			sessionRep.get(viewPos), result);
        	}
        }
        
        private WebViewJSExecutor mWebViewJSExecutor;
    }

    final class LocalWebViewClient extends WebViewClient
    {
    	@Override
    	public void onPageFinished(WebView view, String url) {
//    		int viewPos = mWebViews.indexOf(view);
//    		if (viewPos >= 0 && sessionRep.get(viewPos) != null)
//    			sessionRep.get(viewPos).setStatus("Loaded: " + url);
    		super.onPageFinished(view, url);
    	}
    	
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
    	}
    }
    
    final class LocalWebChromeClient extends WebChromeClient {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        int viewPos = mWebViews.indexOf(view);
        if (viewPos >= 0 && sessionRep.get(viewPos) != null) {
          Session sess = sessionRep.get(viewPos);
          sess.setStatus("Loaded: " + view.getUrl() + ", " +
              newProgress + "% done");
          if (newProgress == 100) {
            // TODO: update session that page is fully loaded
          }
        }
      }
      
      @Override
      public void onReceivedTitle(WebView view, String title) {
        if (mWebViews != null
            && mWebViews.indexOf(view) >= 0
            && sessionRep.get(mWebViews.indexOf(view)) != null
            && title != null)
          sessionRep.get(mWebViews.indexOf(view)).setTitle(title);

        super.onReceivedTitle(view, title);
      }
    }
    
    /**
     * A custom ListAdapter that presents content of sessions.
     */
    private class SessionListAdapter extends BaseAdapter {
        public SessionListAdapter(Context context)
        {
            mContext = context;
        }

        /**
         * Returns the number of items in the list.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return sessionRep.size();
        }

        /**
         * Return object that represents one row in the list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return sessionRep.get(position);
        }

        /**
         * Use the array index as a unique id.
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return sessionRep.get(position).getSessionId();
        }

        /**
         * Make a SessionView to hold each row.
         * @see android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
        	if (mWebViews.size() < position + 1)
        		for(int i=mWebViews.size(); i<=position; i++)
        			mWebViews.add(createWebView());
          if (position >= sessionRep.size())
            return null;
          
          SessionView sv;
          if (convertView == null) {
            sv = new SessionView(mContext, sessionRep.get(position),
                mExpandedPos == position);
          } else {
            sv = (SessionView)convertView;
          }
          //if (position < mWebViews.size())
            sv.setWebView(mWebViews.get(position));
          sv.setTitle(sessionRep.get(position).toString());
          sv.setUrl(sessionRep.get(position).getLastUrl());
          sv.setStatus(sessionRep.get(position).getStatus());

          sv.setExpanded(mExpandedPos == position);
          
          return sv;
        }

        /**
         * Toggles expanded view for a row at position.
         * 
         * @param position Toggle expanded view at this position
         */
		public void toggle(int position) {
			if (mExpandedPos != -1)
				mWebViews.get(mExpandedPos).setVisibility(LinearLayout.GONE);
        	if (mExpandedPos == position) {
        		mExpandedPos = -1;
        	} else
        		mExpandedPos = position;
            notifyDataSetChanged();
        }
		
		public void collapseAll() {
			this.toggle(-1);
		}
        
        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;
        
        /**
         * Holding expanded state for all webviews.
         */
        private int mExpandedPos = -1;
    }
    
    private class SessionView extends LinearLayout {
        public SessionView(Context context, Session session, boolean expanded) {
            super(context);
            
            this.setOrientation(VERTICAL);

            String title = session.toString();
            mTitle = new TextView(context);
            mTitle.setText(title);
            mTitle.setPadding(0, 15, 0, 0);
            mTitle.setTextAppearance(context, android.R.style.TextAppearance_Large);
            mTitle.setTextColor(Color.GREEN);
            addView(mTitle, new LinearLayout.LayoutParams
            		(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            mStatus = new TextView(context);
            mStatus.setText(session.getStatus());
            addView(mStatus, new LinearLayout.LayoutParams
            		(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            
            Log.d("WebDriver", "Created view: " + title);
        }
        
        /**
         * Method to set the title of a SessionView
         */
        public void setTitle(String title) {
            mTitle.setText(title);
        }
        
        public String getTitle() {
        	return mTitle.getText().toString();
        }

        /**
         * Method to set the status of a SessionView
         */
        public void setStatus(String status) {
            mStatus.setText(status);
        }

        /**
         * Convenience method to set the URL of a WebView
         */
        public void setUrl(String url) {
        	if (url == null)
        		return;
        	
        	String currentUrl = (String)getWebViewParam(mWebView, "URL");
        	if (url.equals(currentUrl))
        		return;		// Same URL
        	
            if (url.length() > 0)
            	if (url.startsWith("http"))
            		mWebView.loadUrl(url);
            	else
            		mWebView.loadData(url, "text/html", "utf-8");
            
            setWebViewParam(mWebView, "URL", url);
        }
        
        /**
         * Convenience method to expand or hide the dialogue
         */
        public void setExpanded(boolean expanded) {
            mWebView.setVisibility(expanded ? VISIBLE : GONE);
        }
        
        public WebView getWebView() {
        	return mWebView;
        }
        
        public void setWebView(WebView wv) {
        	if (wv.equals(this.mWebView))
        		return;
        	
        	if (wv.getParent() != null) {
        		SessionView sv = this.getClass().cast(wv.getParent());
        		wv.setVisibility(GONE);
        		sv.removeView(wv);
        		sv.refreshDrawableState();
        	}
        	addView(wv, LayoutParams.FILL_PARENT, 300);
        	this.mWebView = wv;
        }
        
		private TextView mTitle;
        private TextView mStatus;
        private WebView mWebView;
    }

    
    private Object getWebViewParam(WebView wv, String name) {
    	if (wv.getTag() == null)
    		wv.setTag(new Hashtable<String,Object>());
    	
    	return ((Hashtable<String,Object>)wv.getTag()).get(name);
    }
    
    private void setWebViewParam(WebView wv, String name, Object value) {
    	Hashtable<String,Object> params = null;
    	if (wv.getTag() == null) {
    		params = new Hashtable<String,Object>();
    		wv.setTag(params);
    	} else
    		params = (Hashtable<String,Object>)wv.getTag();
    	
    	params.put(name, value);
    }
}

