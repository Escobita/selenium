//
//  WebDriverSignleton.m
//  iWebDriver
//
//  Copyright 2010 WebDriver contributors
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

#import "WebViewControllerSingleton.h"
#import "HTTPServerController.h"
#import "WebDriverPreferences.h"
#import "WebDriverRequestFetcher.h"
#import "WebViewController.h"

@implementation WebViewControllerSingleton

+ (WebViewController*)instance
{
  NSString* mode = [[WebDriverPreferences sharedInstance] mode];
  if ([mode isEqualToString:@"Server"]) {
    return [[HTTPServerController sharedInstance] viewController];
  } else {
    return [[WebDriverRequestFetcher sharedInstance] viewController];
  }
}

@end
