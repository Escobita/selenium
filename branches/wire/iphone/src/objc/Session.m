//
//  Session.m
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

#import "Session.h"
#import "SessionRoot.h"
#import "JSONRESTResource.h"
#import "Context.h"
#import "HTTPRedirectResponse.h"
#import "RootViewController.h"
#import "WebDriverResource.h"
#import "WebDriverUtilities.h"
#import "HTTPVirtualDirectory+Remove.h"

@implementation Session
  
- (id) initWithSessionRootAndSessionId:(SessionRoot*)root
                             sessionId:(int)sessionId {
  self = [super init];
  if (!self) {
    return nil;
  }
  sessionRoot_ = root;
  sessionId_ = sessionId;
  context_ = [[[Context alloc] initWithSessionId:sessionId] autorelease];
  
  [self setIndex:[WebDriverResource
                  resourceWithTarget:self
                  GETAction:NULL
                  POSTAction:NULL
                  PUTAction:NULL
                  DELETEAction:@selector(deleteSession)]];
  [self setResource:context_ withName:[Context contextName]];

  [self cleanSessionStatus];

  return self;
}

- (void)cleanSessionStatus{
  [WebDriverUtilities cleanCookies];
  [WebDriverUtilities cleanCache];
  [WebDriverUtilities cleanDatabases];
}
  
- (void)deleteSession {
  [context_ deleteContext];
  [contents removeAllObjects];
  // Tell the session root to remove this resource.
  [sessionRoot_ deleteSessionWithId:sessionId_];
}

- (void)deleteAllCookies {
  NSArray *cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
  NSEnumerator *enumerator = [cookies objectEnumerator];
  NSHTTPCookie *cookie = nil;
  while ((cookie = [enumerator nextObject])) {
    [[NSHTTPCookieStorage sharedHTTPCookieStorage] deleteCookie:cookie];
  }
}

- (void)dealloc {
  [super dealloc];
}

@end
