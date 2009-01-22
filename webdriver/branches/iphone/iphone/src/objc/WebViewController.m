//
//  WebViewController.m
//  iWebDriver
//
//  Copyright 2009 Google Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "WebViewController.h"
#import "HTTPServerController.h"
#import "UIResponder+SimulateTouch.h"

@implementation WebViewController

@dynamic webView;

// Implement viewDidLoad to do additional setup after loading the view,
// typically from a nib.
- (void)viewDidLoad {
  [super viewDidLoad];
  [[self webView] setScalesPageToFit:YES];
  [[self webView] setDelegate:self];
//	[[self webView] loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"about:blank"]]];
  NSLog(@"WebViewController viewDidLoad");
  [[HTTPServerController sharedInstance] setViewController:self];
  loadLock_ = [[NSCondition alloc] init];
  lastJSResult_ = nil;
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
  // Release anything that's not essential, such as cached data
}

- (void)dealloc {
  [[self webView] setDelegate:nil];
  [loadLock_ release];
  [lastJSResult_ release];
  [super dealloc];
}

- (UIWebView *)webView {
  // TODO: make this check the view is a UIWebView
  return (UIWebView *)[self view];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
  NSLog(@"shouldStartLoadWithRequest");
  return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
  NSLog(@"webViewDidStartLoad");
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
  NSLog(@"finished loading");
  [loadLock_ signal];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
  // I'm ignoring the error since thats what webdriver expects.
  NSLog(@"*** WebView failed to load URL with error %@", error);
  [loadLock_ signal];
}

#pragma mark Web view controls

- (void)performSelectorOnWebView:(SEL)selector withObject:(id)obj {
  [[self webView] performSelector:selector withObject:obj];
}

- (void)waitForLoad {
  // Experiment with me on the phone once debugging is working nicely.
  // This delay should be long enough that the webview has isLoading
  // set correctly (but as short as possible - these delays really add up.) 
  [NSThread sleepForTimeInterval:0.2f];
  
  while ([[self webView] isLoading]) {
    // Yield.
    [NSThread sleepForTimeInterval:0.01f];
    
    // Wait 100ms or until the page has loaded.
    //      [loadLock_ waitUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.1f]];
  }  
}

- (void)performSelectorOnView:(SEL)selector
                   withObject:(id)value
                waitUntilLoad:(BOOL)wait {

  /* The problem with this method is that the webview never gives us any clear
   * indication of whether or not its loading and if so, when its done. Asking
   * it to load causes it to begin loading sometime later (isLoading returns NO
   * for awhile.) Even the |webViewDidFinishLoad:| method isn't a sure sign of
   * anything - it will be called multiple times, once for each frame of the
   * loaded page.
   * 
   * The result: The only effective method I can think of is nasty polling.
   */
  
  while ([[self webView] isLoading])
    [NSThread sleepForTimeInterval:0.01f];
  
  [[self webView] performSelectorOnMainThread:selector
                                   withObject:value
                                waitUntilDone:YES];

  NSLog(@"loading %d", [[self webView] isLoading]);
  
  if (wait)
    [self waitForLoad];
}

// Get the specified URL and block until its finished loading
- (void)setURL:(NSString *)urlString {
  NSURLRequest *url = [NSURLRequest requestWithURL:[NSURL URLWithString:urlString]];
  
  [self performSelectorOnView:@selector(loadRequest:)
                   withObject:url
                waitUntilLoad:YES];
  
  //  NSLog(@"Waiting for '%@' to load...", urlString);
}

- (void)back {
  [self describeLastAction:@"back"];
  [self performSelectorOnView:@selector(goBack)
                   withObject:nil
                waitUntilLoad:YES];
}

- (void)forward {
  [self describeLastAction:@"forward"];
  [self performSelectorOnView:@selector(goForward)
                   withObject:nil
                waitUntilLoad:YES];
}

- (id)visible {
  // It is always visible.
  return [NSNumber numberWithBool:YES];  
}

// Ignored for now. We could make sure the app is on the main flipside; though
// I don't consider this necessary.
- (void)setVisible:(NSNumber *)target {
}

// Execute js in the main thread and set lastJSResult_ appropriately.
- (void)jsEvalInternal:(NSString *)script {
  [lastJSResult_ release];
  lastJSResult_ = [[[self webView]
                   stringByEvaluatingJavaScriptFromString:script] retain];

  NSLog(@"jsEval: %@ -> %@", script, lastJSResult_);
}

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
  
  while ([[self webView] isLoading])
    [NSThread sleepForTimeInterval:0.01f];
  
  [self performSelectorOnMainThread:@selector(jsEvalInternal:)
                         withObject:script
                      waitUntilDone:YES];
  
  return [lastJSResult_ copy];
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
  
  NSString *url = [self URL];
  NSString *result = [self jsEval:script];
  if (![url isEqualToString:[self URL]]) {
    [NSThread sleepForTimeInterval:0.01f];
    while ([[self webView] isLoading])
      [NSThread sleepForTimeInterval:0.01f];
  }
  
//  [NSThread sleepForTimeInterval:0.1f];
  
  return result;
}

- (BOOL)jsElementIsNull:(NSString *)expression {
  NSString *isNull = [self jsEval:[NSString stringWithFormat:@"%@ === null",
                                   expression]];
  return [isNull isEqualToString:@"true"];
}

// The current title of the web browser
- (NSString *)currentTitle {
  return [self jsEval:@"document.title"];
}

- (NSString *)source {
  return [self jsEval:@"document.documentElement.innerHTML"];
}

- (NSString *)URL {
  return [self jsEval:@"window.location.href"];
}

- (void)describeLastAction:(NSString *)status {
  [statusLabel_ setText:status];
}

- (CGRect)viewableArea {
  CGRect area;
  area.origin.x = [[self jsEval:@"window.pageXOffset"] intValue];
  area.origin.y = [[self jsEval:@"window.pageYOffset"] intValue];
  area.size.width = [[self jsEval:@"window.innerWidth"] intValue];
  area.size.height = [[self jsEval:@"window.innerHeight"] intValue];
  return area;
}

- (BOOL)pointIsViewable:(CGPoint)point {
//  NSLog(@"bounds: %@", NSStringFromCGRect([[self webView] bounds]));
  return CGRectContainsPoint([self viewableArea], point);
}

// Scroll to make the given point centered on the screen (if possible).
- (void)scrollIntoView:(CGPoint)point {
  // Webkit will clip the given point if it lies outside the window.
  // It may be necessary at some stage to do this using touches.
  [self jsEval:@"window.scroll(%f - window.innerWidth / 2, %f - window.innerHeight / 2);", point.x, point.y];
}

// Translate pixels in webpage-space to pixels in view space.
- (CGPoint)translatePageCoordinateToView:(CGPoint)point {
  CGRect viewBounds = [[self webView] bounds];
  CGRect pageBounds = [self viewableArea];
  
  // ... And then its just a linear transformation.
  float scale = viewBounds.size.width / pageBounds.size.width;
  CGPoint transformedPoint;
  transformedPoint.x = (point.x - pageBounds.origin.x) * scale;
  transformedPoint.y = (point.y - pageBounds.origin.y) * scale;
  
  NSLog(@"%@ -> %@",
        NSStringFromCGPoint(point),
        NSStringFromCGPoint(transformedPoint));
  
  return transformedPoint;
}

- (void)clickOnPageElementAt:(CGPoint)point {
  if (![self pointIsViewable:point]) {
    [self scrollIntoView:point];
  }
  
  CGPoint pointInViewSpace = [self translatePageCoordinateToView:point];
  
  NSLog(@"simulating a click at %@", NSStringFromCGPoint(pointInViewSpace));
  [[self webView] simulateTapAt:pointInViewSpace];
}

// I don't know why, but this doesn't seem to work in the current version of
// mobile safari.
- (void)addFirebug {
  // This is the http://getfirebug.com/lite.html bookmarklet
  [self jsEval:
  @"var firebug=document.createElement('script');firebug.setAttribute('src',"
   "'http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js');"
   "document.body.appendChild(firebug);(function(){if(window.firebug.version)"
   "{firebug.init();}else{setTimeout(arguments.callee);}})();"];
}

@end
