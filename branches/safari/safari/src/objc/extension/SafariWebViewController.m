//
//  SafariWebViewController.m
//  SafariDriver
//
//  Created by Andrian Kurniady on 10/13/09.
//  Adapted from WebViewController.m from iPhone Driver by josephg.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "SafariWebViewController.h"

@implementation SafariWebViewController

+ (id)sharedInstance {
  static id instance = nil;
  if (instance == nil) {
    instance = [[SafariWebViewController alloc] init];
    WebViewController *wvc = [WebViewController sharedInstance];
    [wvc setDelegate:instance];
  }
  return instance;
}

- (id)init {
  if (self = [super init]) {
    WebDriverPreferences* preferences = [WebDriverPreferences sharedInstance];
    cachePolicy_ = [preferences cache_policy];
    
    // Kicks off the HTTP Server
    NSLog(@"Starting HTTP Server...");
    [HTTPServerController sharedInstance];
    NSLog(@"HTTP Server started...");
  }
  return self;
}

- (id)initWithWebView:(WebViewType*)webView {
  webView_ = webView;
  [[WebViewController sharedInstance] initWithWebView:webView];
}

// Methods from WebViewControlerDelegate
- (void)describeLastAction:(NSString *)status {
  NSLog(status);
}

- (ImageType *)screenshot {
  return nil;
}

- (void)clickOnPageElementAt:(CGPoint)point {
}

- (void)loadURL:(NSString *)url {
  [webView_ performSelectorOnMainThread:@selector(setMainFrameURL:) 
                                   withObject:url
                                waitUntilDone:YES];
}

@end
