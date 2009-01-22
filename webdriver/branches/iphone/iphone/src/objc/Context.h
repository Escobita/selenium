//
//  Context.h
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

#import <Foundation/Foundation.h>
#import "HTTPVirtualDirectory.h"

@class ElementStore;

// This class matches the /:session/:context/ virtual directory which webdriver
// expects. The context manages the global behaviour of the page - getUrl,
// forward, back, execute javascript, etc. Most of the actual functionality
// resides in |WebViewController| since its cleaner that way. This vdir is
// responsible for mapping the REST calls to code execution.
@interface Context : HTTPVirtualDirectory {
  int sessionId_;
  
  ElementStore *elementStore_;
}

@property (nonatomic, readonly) ElementStore *elementStore;

- (id)initWithSessionId:(int)sessionId;

@end
