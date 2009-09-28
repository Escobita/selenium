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
import com.android.webdriver.sessions.intents.GetTitleIntentReceiver;
import com.android.webdriver.sessions.intents.IntentReceiverRegistrar;
import com.android.webdriver.sessions.intents.Intents;
import com.android.webdriver.sessions.intents.NavigationIntentReceiver;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
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
    private WebViewJSExecutor mJSExecutor = new WebViewJSExecutor();
    
    public static final long JS_COMMAND_TIMEOUT = 60000L; // in milliseconds 

    public static final int MENU_ADD = Menu.FIRST + 1;
    public static final int MENU_CLOSE_ALL = Menu.FIRST + 2;

    public static final int CTX_MENU_DELETE = Menu.FIRST + 11;
    public static final int CTX_MENU_NAVIGATE = Menu.FIRST + 12;
    
	public SessionListActivity() {
		sessionRep = SessionRepository.getInstance();

		mIntentReg = new IntentReceiverRegistrar(this);

		// Generate some data
        // TODO: remove when well tested
//        for (int i=0; i<3; i++) {
//        	sessionRep.add("foo");
//        }
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
    };
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo i =
        	(AdapterContextMenuInfo)item.getMenuInfo();
    	
        Handler urlHandler = new Handler() {
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
            public void handleMessage(Message msg) {
            	((SessionListAdapter)getListAdapter()).collapseAll();
        		Intent intent = new Intent(Intents.INTENT_DELETESESSION);
            	intent.putExtra("SessionId",
            			sessionRep.get(i.position).getSessionId());
            	sendBroadcast(intent);
        		mWebViews.remove(i.position);
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

		// TODO: Add operation-specific code
		switch(type) {
			case ADDED:
				break;
			case REMOVED:
				break;
			case UPDATED:
				break;
		}
		((SessionListAdapter)this.getListAdapter()).notifyDataSetChanged();
	}

	public Object onActionRequest(Session session, Actions action,
			Object[] params) {
		
		// TODO: Add action specific code
		switch(action) {
			case GET_DOM:
				int position = sessionRep.indexOf(session);
				if (position >= 0) {
					return ((WebViewJSExecutor)getWebViewParam(
						mWebViews.get(position), "EXECUTOR")).executeJS(session,
						"window.webdriver.resultMethod"
						+ "(document.documentElement.outerHTML);");
				}
				break;
			case ELEMENT_EXISTS:
				break;
		}
		return null;
	}

	class WebViewJSExecutor {
		public String executeJS(Session session, String jsCode) {
			int position = sessionRep.indexOf(session);
			if (position >= 0) {
				syncObj = session;
				synchronized (this) {
					WebView wv = mWebViews.get(position);
					wv.loadUrl("javascript:" + jsCode);
					try {
						this.wait(JS_COMMAND_TIMEOUT);
					} catch (InterruptedException ie) {	}
				}
			}
			return res;
		}
		public void resultAvailable(Session session, String result) {
			synchronized (this) {
				res = result;
				this.notifyAll();
			}
		}
		
		private Object syncObj;
		private String res = "";
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
    	};
    	
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
    	}
    	
    }
    
    final class LocalPictureListener implements WebView.PictureListener
    {
		public void onNewPicture(WebView view, Picture picture) {
//    		int viewPos = mWebViews.indexOf(view);
//    		if (viewPos >= 0 && sessionRep.get(viewPos) != null)
//    			sessionRep.get(viewPos).setStatus("Loaded: " + 
//    					picture.getWidth() + "x" + picture.getHeight());
		}
    	
    }

    final class LocalWebChromeClient extends WebChromeClient {
    	@Override
    	public void onProgressChanged(WebView view, int newProgress) {
    		int viewPos = mWebViews.indexOf(view);
    		if (viewPos >= 0 && sessionRep.get(viewPos) != null)
    			sessionRep.get(viewPos).setStatus("Loaded: " + 
    					view.getUrl() + ", " + newProgress + "% done");
    	};
    	
    	@Override
    	public void onReceivedTitle(WebView view, String title) {
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
        	SessionView sv;
            if (convertView == null) {
                sv = new SessionView(mContext, sessionRep.get(position),
                		mExpandedPos == position);
            } else {
                sv = (SessionView)convertView;
            }
            sv.setWebView(mWebViews.get(position));
            sv.setTitle(sessionRep.get(position).toString());
            sv.setUrl(sessionRep.get(position).getLastUrl());
            sv.setStatus(sessionRep.get(position).getStatus());

            Log.d("WebDriver", "Setting explanded for position: " + position +
            		" to " + (mExpandedPos == position));
            
            sv.setExpanded(mExpandedPos == position);
            
            return sv;
        }

        /**
         * Creates and returns an empty WebView view.
         * 
         * @return New WebView.
         */
        private WebView createWebView() {
            WebView wv = new WebView(mContext);
            wv.setWebViewClient(new LocalWebViewClient());
            wv.setFocusable(false);
            wv.getSettings().setBuiltInZoomControls(true);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.setWebChromeClient(new LocalWebChromeClient());
			CustomJavaScriptInterface script = new CustomJavaScriptInterface(wv);
			WebViewJSExecutor executor = new WebViewJSExecutor();
			script.setWebViewJSExecutor(executor);
			wv.addJavascriptInterface(script, "webdriver");
			setWebViewParam(wv, "EXECUTOR", executor);

            //wv.setPictureListener(new LocalPictureListener());
            // TODO: Enable?
            return wv;
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
        	
            if (url.length() == 0)
            	mWebView.loadData("Nothing loaded", "text/html", "utf-8");
            else
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

