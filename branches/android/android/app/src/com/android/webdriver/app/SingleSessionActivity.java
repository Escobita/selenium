/* Copyright (C) 2009 The Android Open Source Project
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

import com.android.webdriver.sessions.Session.Actions;
import com.android.webdriver.sessions.intents.*;
import com.android.webdriver.sessions.intents.DoActionIntentReceiverLite.ActionRequestListener;
import com.android.webdriver.sessions.intents.GetCurrentUrlIntentReceiverLite.UrlRequestListener;
import com.android.webdriver.sessions.intents.GetTitleIntentReceiverLite.TitleRequestListener;
import com.android.webdriver.sessions.intents.NavigationIntentReceiverLite.NavigateRequestListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class SingleSessionActivity extends Activity
                                   implements ActionRequestListener,
                                              TitleRequestListener,
                                              NavigateRequestListener,
                                              UrlRequestListener {
  
  public static final int MENU_MULTI_SESSION = Menu.FIRST + 3;
  

  public SingleSessionActivity() {
    mIntentReg = new IntentReceiverRegistrar(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, MENU_MULTI_SESSION, 0, R.string.menu_multiple_session)
        .setIcon(R.drawable.ic_menu_share);
  
      return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          case MENU_MULTI_SESSION:
            PreferencesRepository.getInstance().setMode(false);
            this.setResult(WebDriver.RESULT_SWITCH_MODE);
            this.finish();
            return true;
      }

      return super.onOptionsItemSelected(item);
  }
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Request the progress bar to be shown in the title and set it to 0
      requestWindowFeature(Window.FEATURE_PROGRESS);
      setProgressBarVisibility(false);
      setProgress(0);

      setContentView(R.layout.single_session_layout);

      this.setTitle("WebDriver Client Lite");

      // Configure WebView
      mWebView = (WebView) findViewById(R.id.webview);
      mWebView.setWebViewClient(new LocalWebViewClient());
      mWebView.setFocusable(false);
      mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      mWebView.getSettings().setBuiltInZoomControls(true);
      mWebView.getSettings().setJavaScriptEnabled(true);
      mWebView.setWebChromeClient(new LocalWebChromeClient());
      mWebView.addJavascriptInterface(new CustomJavaScriptInterface(),
          "webdriver");

      // Registering all intent receivers
      NavigationIntentReceiverLite navIR = new NavigationIntentReceiverLite();
      navIR.setNavigateRequestListener(this);
      mIntentReg.registerReceiver(navIR, Intents.INTENT_NAVIGATE);
      
      mIntentReg.registerReceiver(new AddSessionIntentReceiverLite(),
          Intents.INTENT_ADDSESSION);
      mIntentReg.registerReceiver(new DeleteSessionIntentReceiverLite(),
          Intents.INTENT_DELETESESSION);
      
      DoActionIntentReceiverLite doActIR = new DoActionIntentReceiverLite();
      doActIR.setActionRequestListener(this);
      mIntentReg.registerReceiver(doActIR, Intents.INTENT_DOACTION);
      
      GetTitleIntentReceiverLite titleIR = new GetTitleIntentReceiverLite();
      titleIR.setTitleRequestListener(this);
      mIntentReg.registerReceiver(titleIR, Intents.INTENT_GETTITLE);
      
      mIntentReg.registerReceiver(new SetProxyIntentReceiver(),
          Intents.INTENT_SETPROXY);
      
      GetCurrentUrlIntentReceiverLite urlIR =
        new GetCurrentUrlIntentReceiverLite();
      urlIR.setUrlRequestListener(this);
      mIntentReg.registerReceiver(urlIR, Intents.INTENT_GETURL);
      
//      mIntentReg.RegisterReceiver(new CookieIntentReceiver(),
//          Intents.INTENT_COOKIES);

      Log.i("WebDriver Lite", "Loaded.");
  }
  
  public void navigateTo(String url) {
    if (url == null)
      return;
  
    if (url.equals(currentUrl))
        return;     // Same URL
    
    if (url.length() > 0)
        if (url.startsWith("http"))
          mWebView.loadUrl(url);
        else
          mWebView.loadData(url, "text/html", "utf-8");
    
    currentUrl = url;
  }

  public String getLastUrl() {
    return currentUrl;
  }
  
  public String getWebViewTitle() {
    return mWebView.getTitle();
  }
  
  public String executeJS(String script) {
    return jsExecutor.executeJS(script);
  }
  
  public void navigateBackOrForward(int steps) {
    mWebView.goBackOrForward(steps);
  }
  
  public void reload() {
    mWebView.reload();
  }
  
  public void setStatus(String status) {
    ((TextView)findViewById(R.id.status)).setText(status);
  }
  
  class SimpleWebViewJSExecutor {
    public String executeJS(String jsCode) {
      synchronized (syncObj) {
        res = "";
        running = true;
        Log.d("WebViewJSExecutor", "Running script: " + jsCode);
        mWebView.loadUrl("javascript:" + jsCode);
        Log.d("WebViewJSExecutor", "waiting...");
        try {
          syncObj.wait(SessionListActivity.COMMAND_TIMEOUT);
        } catch (InterruptedException ie) {   }
        running = false;
      }
      if (res.length() == 0)
        Log.d("WebViewJSExecutor", "Returning empty result");
      return res;
    }
    
    public void resultAvailable(String result) {
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
  
  final class LocalWebViewClient extends WebViewClient
  {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      setProgressBarVisibility(true);   // Showing progress bar in title
      setProgress(0);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      setProgressBarVisibility(false);   // Hiding progress bar in title
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
      setStatus(view.getUrl());
      setProgress(newProgress * 100);
    }
  }

  final class CustomJavaScriptInterface {
    public void resultMethod(String result) {
      jsExecutor.resultAvailable(result);
    }
  }

  private String currentUrl = "";
  private WebView mWebView = null;
  private IntentReceiverRegistrar mIntentReg;
  private SimpleWebViewJSExecutor jsExecutor = new SimpleWebViewJSExecutor();


  @Override
  public Object onActionRequest(Actions action, Object[] args) {
    String actionRes = "";
    
    switch(action) {
      case EXECUTE_JAVASCRIPT:
        if (args.length == 1)
          actionRes = this.executeJS(args[0].toString());
        else
          Log.w("WebDriverLite:ExecuteJavaScript",
              "Incorrect arguments for action: " + action.toString());
        break;
      case GET_PAGESOURCE:
        actionRes = this.executeJS(
        "window.webdriver.resultMethod(document.documentElement.outerHTML);");        
        break;
      case NAVIGATE_BACK:
        this.navigateBackOrForward(-1);
        break;
      case NAVIGATE_FORWARD:
        this.navigateBackOrForward(1);
        break;
      case REFRESH:
        this.reload();
        break;
    }
    return actionRes;
  }

  @Override
  public String onTitleRequest() {
    return this.getWebViewTitle();
  }

  @Override
  public void onNavigateRequest(String url) {
    this.navigateTo(url);
  }

  @Override
  public String onUrlRequest() {
    return this.getLastUrl();
  }
  
}
