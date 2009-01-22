//
//  WebDriverResource.h
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
//
//  This class wraps a standard obj-c method into a method which webdriver can
//  call. The method is cal

#import <Foundation/Foundation.h>
#import "JSONRESTResource.h"

// See file header
@interface WebDriverResource : NSObject<HTTPResource> {
  id target_;
  NSDictionary *methodActions_;
  
  // These two fields are needed for when we make |WebDriverResponse|s.
  // Due to the architecture of |VirtualDirectory|, we have to cache the
  // session and context like this.
  NSString *session_;
  NSString *context_;
  
  BOOL allowOptionalArguments_;
}

@property (nonatomic, copy) NSString *session;
@property (nonatomic, copy) NSString *context;
@property (nonatomic, assign) BOOL allowOptionalArguments;

// Designated initialiser. The dictionary should map the strings 'GET', 'PUT',
// 'POST', 'DELETE' to selectors to be called on the target. Do not set
// dictionary entries for methods you do not want to handle.
- (id)initWithTarget:(id)target
             actions:(NSDictionary *)actionTable;

// Create a resource which will call these selectors on target when method is
// called. The method's argument list must match in type and number.
//
// Send NULL for any method you don't want to handle.
- (id)initWithTarget:(id)target
           GETAction:(SEL)getAction
          POSTAction:(SEL)postAction
           PUTAction:(SEL)putAction
        DELETEAction:(SEL)deleteAction;

+ (WebDriverResource *)resourceWithTarget:(id)target
                                GETAction:(SEL)getAction
                               POSTAction:(SEL)postAction
                                PUTAction:(SEL)putAction
                             DELETEAction:(SEL)deleteAction;

+ (WebDriverResource *)resourceWithTarget:(id)target
                                GETAction:(SEL)getAction
                               POSTAction:(SEL)postAction;  
@end

// This is an extension to VirtualDirectory to allow easy resource additions
@interface HTTPVirtualDirectory (WebDriverResource)

// This helper method for VirtualDirectory sets a selector of the current class
// to be a webdriver method. Its arguments will be taken from POST data and
// the method's return value will be wrapped in a WebDriverResponse.
// See http://code.google.com/p/webdriver/wiki/JsonWireProtocol
- (void)setMyWebDriverHandlerWithGETAction:(SEL)getAction
                                POSTAction:(SEL)postAction
                                 PUTAction:(SEL)putAction
                              DELETEAction:(SEL)deleteAction
                                  withName:(NSString *)name;

// Same as above, but without PUT and DELETE.
- (void)setMyWebDriverHandlerWithGETAction:(SEL)getAction
                                POSTAction:(SEL)postAction
                                  withName:(NSString *)name;
  
@end
