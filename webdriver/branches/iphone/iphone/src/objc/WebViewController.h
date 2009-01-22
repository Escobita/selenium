//
//  WebViewController.h
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

#import <UIKit/UIKit.h>

// The WebViewController manages the iWebDriver's WebView.
@interface WebViewController : UIViewController<UIWebViewDelegate>
{
 @private
  // The spec states that the GET message shouldn't return until the new page
  // is loaded. We need to lock the main thread to implement that. That'll
  // happen by polling [view isLoaded] but we can break early if the delegate
  // methods are fired. Note that subframes may still be being loaded.
  NSCondition *loadLock_;
  
  NSString *lastJSResult_;
  
  // Pointer to the status / activity label.
  IBOutlet UILabel *statusLabel_;
  
  // This is nil if the last operation succeeded.
  NSError *lastError_;
}

@property (retain, readonly) UIWebView *webView;

- (CGRect)viewableArea;
- (BOOL)pointIsViewable:(CGPoint)point;

// Some webdriver stuff.
- (id)visible;
- (void)setVisible:(NSNumber *)target;
- (NSString *)currentTitle;
- (NSString *)URL;
- (void)setURL:(NSString *)urlString;
- (void)back;
- (void)forward;
- (NSString *)jsEval:(NSString *)script, ...;
- (NSString *)jsEvalAndBlock:(NSString *)script, ...;
- (BOOL)jsElementIsNull:(NSString *)expression;
- (NSString *)source;

- (void)clickOnPageElementAt:(CGPoint)point;

- (void)addFirebug;

// Calls the same on the main view controller.
- (void)describeLastAction:(NSString *)status;

@end
