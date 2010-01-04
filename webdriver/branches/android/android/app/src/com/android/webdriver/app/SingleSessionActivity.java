package com.android.webdriver.app;

import com.android.webdriver.sessions.intents.*;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class SingleSessionActivity extends Activity {
  
  public static final int SINGLE_SESSION_ID = 1000;  // We have only one session


  public SingleSessionActivity() {
    mIntentReg = new IntentReceiverRegistrar(this);
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
      mIntentReg.registerReceiver(new NavigationIntentReceiverLite(),
          Intents.INTENT_NAVIGATE);
      mIntentReg.registerReceiver(new AddSessionIntentReceiverLite(),
          Intents.INTENT_ADDSESSION);
      mIntentReg.registerReceiver(new DeleteSessionIntentReceiverLite(),
          Intents.INTENT_DELETESESSION);
      mIntentReg.registerReceiver(new DoActionIntentReceiverLite(),
          Intents.INTENT_DOACTION);
      mIntentReg.registerReceiver(new GetTitleIntentReceiverLite(),
          Intents.INTENT_GETTITLE);
      mIntentReg.registerReceiver(new SetProxyIntentReceiver(),
          Intents.INTENT_SETPROXY);
      mIntentReg.registerReceiver(new GetCurrentUrlIntentReceiverLite(),
          Intents.INTENT_GETURL);
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
}
