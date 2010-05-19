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

@end

/**
 From pre-common-merge safaridriver, just for reference.
 
 - (void)waitForLoad {
 // TODO(josephg): Test sleep intervals on the device.
 // This delay should be long enough that the webview has isLoading
 // set correctly (but as short as possible - these delays slow down testing.)
 
 // - The problem with [view isLoading] is that it gets set in a separate
 // worker thread. So, right after asking the webpage to load a URL we need to
 // wait an unspecified amount of time before isLoading will correctly tell us
 // whether the page is loading content.
 
 [NSThread sleepForTimeInterval:0.2f];
 
 while ([webView_ isLoading]) {
 // Yield.
 [NSThread sleepForTimeInterval:0.01f];
 }  
 }
 
 // All method calls on the view need to be done from the main thread to avoid
 // synchronization errors. This method calls a given selector in this class
 // optionally with an argument.
 //
 // If called with waitUntilLoad:YES, we wait for a web page to be loaded in the
 // view before returning.
 - (void)performSelectorOnWebView:(SEL)selector
 withObject:(id)value
 waitUntilLoad:(BOOL)wait {
 
 /* The problem with this method is that the UIWebView never gives us any clear
 * indication of whether or not it's loading and if so, when its done. Asking
 * it to load causes it to begin loading sometime later (isLoading returns NO
 * for awhile.) Even the |webViewDidFinishLoad:| method isn't a sure sign of
 * anything - it will be called multiple times, once for each frame of the
 * loaded page.
 *
 * The result: The only effective method I can think of is nasty polling.
 

while ([webView_ isLoading])
[NSThread sleepForTimeInterval:0.01f];

[webView_ performSelectorOnMainThread:selector
                           withObject:value
                        waitUntilDone:YES];

NSLog(@"loading %d", [[self webView] isLoading]);

if (wait) {
  [self waitForLoad];
}
}

// Get the specified URL and block until it's finished loading.
- (void)setURL:(NSString *)urlString {
  
  [self performSelectorOnWebView:@selector(setMainFrameURL:)
                      withObject:urlString
                   waitUntilLoad:YES];
}

- (void)back {
  [self performSelectorOnWebView:@selector(goBack)
                      withObject:nil
                   waitUntilLoad:YES];
}

- (void)forward {
  [self performSelectorOnWebView:@selector(goForward)
                      withObject:nil
                   waitUntilLoad:YES];
}

- (void)reload {
  [self performSelectorOnWebView:@selector(reload:)
                      withObject:self
                   waitUntilLoad:YES];
}

- (NSString *)URL {
  return [webView_ mainFrameURL];
}

- (NSString*)currentTitle {
  return [webView_ mainFrameTitle];
}

- (NSString *)source {
  return [self jsEval:@"document.documentElement.innerHTML"];
}

// Execute js in the main thread and set lastJSResult_ appropriately.
// This function must be executed on the main thread. Its designed to be called
// using performSelectorOnMainThread:... which doesn't return a value - so
// the return value is passed back through a class parameter.
- (void)jsEvalInternal:(NSString *)script {
  [lastJSResult_ release];
  lastJSResult_ = [[[self webView]
                    stringByEvaluatingJavaScriptFromString:script] retain];
  
  NSLog(@"jsEval: %@ -> %@", script, lastJSResult_);
}

// Evaluate the given JS format string & arguments. Argument list is the same
// as [NSString stringWithFormat:...].
- (NSString *)jsEval:(NSString *)format, ... {
  if (format == nil) {
    [NSException raise:@"invalidArguments" format:@"Invalid arguments for jsEval"];
  }
  
  va_list argList;
  va_start(argList, format);
  NSString *script = [[[NSString alloc] initWithFormat:format
                                             arguments:argList]
                      autorelease];
  va_end(argList);
  
  [self performSelectorOnMainThread:@selector(jsEvalInternal:)
                         withObject:script
                      waitUntilDone:YES];
  
  return [[lastJSResult_ copy] autorelease];
}

- (NSString *)jsEvalAndBlock:(NSString *)format, ... {
  if (format == nil) {
    [NSException raise:@"invalidArguments" format:@"Invalid arguments for jsEval"];
  }
  
  va_list argList;
  va_start(argList, format);
  NSString *script = [[[NSString alloc] initWithFormat:format
                                             arguments:argList]
                      autorelease];
  va_end(argList);
  
  NSString *result = [self jsEval:@"%@", script];
  
  [self waitForLoad];
  
  return result;
}

- (BOOL)jsElementIsNullOrUndefined:(NSString *)expression {
  NSString *isNull = [self jsEval:@"%@ === null || %@ === undefined",
                      expression, expression];
  return [isNull isEqualToString:@"true"];
}
*/