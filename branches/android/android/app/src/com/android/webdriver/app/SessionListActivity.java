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
import com.android.webdriver.sessions.intents.CookieIntentReceiver;
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
 * A multi-session view of the application.
 */
public class SessionListActivity extends ListActivity 
	 implements View.OnCreateContextMenuListener, 
	 			SessionRepository.OnSessionChangeListener,
	 			SessionRepository.SessionActionRequestListener
{
  /** Timeout of all asynchronous commands */
  public static final long COMMAND_TIMEOUT = 2500L; // in milliseconds
  
  /** Options menu items */
  public static final int MENU_ADD = Menu.FIRST + 1;
  public static final int MENU_CLOSE_ALL = Menu.FIRST + 2;
  public static final int MENU_SINGLE_SESSION = Menu.FIRST + 3;
  
  /** Context (long click) menu items */
  public static final int CTX_MENU_DELETE = Menu.FIRST + 11;
  public static final int CTX_MENU_NAVIGATE = Menu.FIRST + 12;

  private SessionRepository sessionRep;
  private ArrayList<WebView> mWebViews = new ArrayList<WebView>();
  private IntentReceiverRegistrar mIntentReg;


  public SessionListActivity() {
    sessionRep = SessionRepository.getInstance();

    mIntentReg = new IntentReceiverRegistrar(this);
  }

  /** Called with the activity is first created. */
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
    mIntentReg.registerReceiver(new NavigationIntentReceiver(),
        Intents.INTENT_NAVIGATE);
    mIntentReg.registerReceiver(new AddSessionIntentReceiver(),
        Intents.INTENT_ADDSESSION);
    mIntentReg.registerReceiver(new DeleteSessionIntentReceiver(),
        Intents.INTENT_DELETESESSION);
    mIntentReg.registerReceiver(new DoActionIntentReceiver(),
        Intents.INTENT_DOACTION);
    mIntentReg.registerReceiver(new GetTitleIntentReceiver(),
        Intents.INTENT_GETTITLE);
    mIntentReg.registerReceiver(new SetProxyIntentReceiver(),
        Intents.INTENT_SETPROXY);
    mIntentReg.registerReceiver(new GetCurrentUrlIntentReceiver(),
        Intents.INTENT_GETURL);
    mIntentReg.registerReceiver(new CookieIntentReceiver(),
        Intents.INTENT_COOKIES);

    Log.i("Multi-session WebDriver", "Loaded.");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ADD, 0, R.string.menu_add_session)
      .setIcon(R.drawable.ic_menu_add);

    menu.add(0, MENU_CLOSE_ALL, 0, R.string.close_all_sessions)
      .setIcon(R.drawable.ic_menu_delete);

    menu.add(0, MENU_SINGLE_SESSION, 0, R.string.menu_single_session)
      .setIcon(R.drawable.ic_menu_share);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_ADD:                // Add a new session
        sessionRep.add("bar");
        return true;
      case MENU_CLOSE_ALL:          // Close all sessions
        sessionRep.removeAll();
        return true;
      case MENU_SINGLE_SESSION:     // Switch to single-session mode
        sessionRep.removeAll();
        PreferencesRepository.getInstance().setMode(true);
        this.setResult(WebDriver.RESULT_SWITCH_MODE);
        this.finish();
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
    final AdapterContextMenuInfo i = (AdapterContextMenuInfo)item.getMenuInfo();

    // Handler for inputing navigation URL
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

    // Handler for confirmation of removing all sessions
    Handler confirmHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        ((SessionListAdapter)getListAdapter()).collapseAll();
        Intent intent = new Intent(Intents.INTENT_DELETESESSION);
        intent.putExtra("SessionId",
            sessionRep.get(i.position).getSessionId());
        sendBroadcast(intent);
      }
    };

    switch (item.getItemId()) {
      case CTX_MENU_NAVIGATE:   // Navigate session to URL
        Alerts.inputBox(this, urlHandler, "Enter URL:", "");
        return true;
      case CTX_MENU_DELETE:     // Delete all sessions 
        Alerts.Confirm(this, confirmHandler,
        "Delete this session?");
        return true;
    }

    return super.onContextItemSelected(item);
  }

  /**
   * Handler for list item (session) click.
   * Toggles between collapsed and expanded view of that session.
   */
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    ((SessionListAdapter)getListAdapter()).toggle(position);
  }

  /**
   * Handler for reflecting changes of a single session.
   * Updates UI data set and creates WebView if needed.
   * 
   * @param changedSession Session that was added/updated/removed.
   * @param type Type of the change: Add, Update, Delete, etc.
   */
  public void onSessionChange(Session changedSession, SessionChangeType type) {
    Log.d("WebDriver", "Session: " + changedSession.toString() +
        " was " + type.toString());

    switch(type) {
      case ADDED:
        // We might need to add WebView control for new session.
        if (mWebViews.size() + 1 == sessionRep.size()) {
          mWebViews.add(createWebView());
        }
        break;
      case REMOVED:
        break;
      case UPDATED:
        break;
    }
    ((SessionListAdapter)this.getListAdapter()).notifyDataSetChanged();
  }

  /**
   * Creates and returns an empty WebView view.
   * 
   * @return New WebView.
   */
  private WebView createWebView() {
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
    return wv;
  }

  /**
   * Callback for requesting UI actions by session model.
   * 
   * @param session Session that requires an action.
   * @param action Action to be performed by the UI.
   * @param params Action's additional parameters 
   */
  public Object onActionRequest(Session session, Actions action,
      Object[] params) {

    int position = sessionRep.indexOf(session);
    if (position < 0 || position >= sessionRep.size()) {
      Log.e("ActionRequest", "Action: " + action.name() +
            " requested for unknown session: " + session.getSessionId());
      return null;
    }
    switch(action) {
      case GET_PAGESOURCE:
        return ((WebViewJSExecutor)getWebViewParam(
            mWebViews.get(position), "EXECUTOR")).executeJS(session,
                "window.webdriver.resultMethod"
                + "(document.documentElement.outerHTML);");
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

  /**
   * Class that wraps synchronization housekeeping of
   * execution of JavaScript code within WebView.
   */
  class WebViewJSExecutor {
    
    /**
     * Executes a given JavaScript code within WebView associated
     * with a given session and returns execution result.
     * <p>
     * Note: execution is limited in time to {@link #COMMAND_TIMEOUT}
     *       to prevent "application not responding" alerts.
     * 
     * @param session Session that should execute the code.
     * @param jsCode JavaScript code to execute.
     * @return Results (if returned) or an empty string.
     */
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
    
    /**
     * Callback to report results of JavaScript code execution.
     * 
     * @param session Session that had been executed the code.
     * @param result Results (if returned) or an empty string.
     */
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
    private boolean running = false;  // Is JavaScript code currently running?
  }

  /**
   * Custom module that is added to the WebView's JavaScript engine
   * to enable callbacks to java code.
   * This is required since WebView doesn't expose the underlying DOM.
   */
  final class CustomJavaScriptInterface {
    WebView mWebView = null;

    CustomJavaScriptInterface(WebView wv) {
      mWebView = wv;
    }

    /**
     * Assigns the JavaScript executor to use.
     * 
     * @param executor JavaScript executor to use for running the script.
     */
    public void setWebViewJSExecutor(WebViewJSExecutor executor) {
      mWebViewJSExecutor = executor;
    }

    /**
     * A callback from JavaScript to Java that passes
     * execution result as a parameter.
     * <p>
     * This method is accessible from WebView's JS DOM as
     * windows.webdriver.resultMethod().
     *
     * @param result Result that should be returned to Java code from WebView. 
     */
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

  /**
   * This class overrides WebView default behavior when loading new URL. 
   * It makes sure that the URL is always loaded by the WebView.  
   */
  final class LocalWebViewClient extends WebViewClient
  {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true;
    }
  }

  /**
   * Subscriber class to be notified when the underlying
   * WebView loads new content or changes title. 
   */
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
    public SessionListAdapter(Context context) {
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
     * 
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
      return sessionRep.get(position).getSessionId();
    }

    /**
     * Make a SessionView to hold one row (session).
     * 
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
      if (mWebViews.size() < position + 1)
        // Adding WebView elements if required.
        for (int i = mWebViews.size(); i <= position; i++)
          mWebViews.add(createWebView());

      if (position >= sessionRep.size())
        return null;

      SessionView sv;
      if (convertView == null) {
        sv = new SessionView(mContext, sessionRep.get(position),
              mExpandedPos == position);
      } else {
        sv = (SessionView) convertView;
      }

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
	
    /** Collapses all ListView rows */
    public void collapseAll() {
      this.toggle(-1);
    }

    /** Remember our context so we can use it when constructing views. */
    private Context mContext;
    
    /** Holding expanded state for all webviews. */
    private int mExpandedPos = -1;
  }

  /**
   * View control for a single session.
   */
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

    /**
     * Returns title of the URL loaded in the WebView
     *
     *  @return Status of the session.
     */  
    public String getTitle() {
      return mTitle.getText().toString();
    }

    /**
     * Method to set the status of a session.
     * 
     * @param status String to set the session status to.
     */
    public void setStatus(String status) {
      mStatus.setText(status);
    }

    /**
     * Method for setting the URL of a WebView.
     * @param url URL/test to assign to the WebView.
     *  If begins with 'http' it's considered to be a url address,
     *  otherwise handled as plain text to display within WebView.
     */
    public void setUrl(String url) {
      if (url == null)
        return;

      String currentUrl = (String)getWebViewParam(mWebView, "URL");
      if (url.equals(currentUrl))
        return;		// Same URL - nothing should be done.

      if (url.length() > 0)
        if (url.startsWith("http"))
          mWebView.loadUrl(url);
        else
          mWebView.loadData(url, "text/html", "utf-8");

      setWebViewParam(mWebView, "URL", url);
    }

    /**
     * Method to expand or hide the WebView according to session state.
     * 
     * @param expanded True to make the WebView control visible.
     */
    public void setExpanded(boolean expanded) {
      mWebView.setVisibility(expanded ? VISIBLE : GONE);
    }

    /**
     * Returns the underlying WebView control.
     * 
     * @return WebView control associated with the current session.
     */
    public WebView getWebView() {
      return mWebView;
    }

    /**
     * Associate a given WebView control with the session.
     * 
     * @param wv WebView control to associate with the current session.
     */
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


  /**
   * Service routine for retrieving data associated with the given WebView.
   * 
   * @param wv WebView to extract the data from.
   * @param name Unique key name of the data to return. 
   * @return Data object (if found) or null if the key does not exists. 
   */
  private Object getWebViewParam(WebView wv, String name) {
    if (wv.getTag() == null)
      wv.setTag(new Hashtable<String,Object>());

    return ((Hashtable<String,Object>)wv.getTag()).get(name);
  }

  /**
   * Store data object associated with a given WebView by unique key name.
   *  
   * @param wv WebView to store the data for.
   * @param name Unique key to identify with the data.
   * @param value Value object to store.
   */
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
