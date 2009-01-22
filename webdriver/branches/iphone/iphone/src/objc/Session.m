//
//  SessionManager.m
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
#import "JSONRESTResource.h"
#import "Context.h"
#import "HTTPRedirectResponse.h"

@implementation Session

- (id)init {
  if (![super init])
    return nil;

  [self setIndex:[JSONRESTResource
                  JSONResourceWithTarget:self 
                                  action:@selector(createSessionWithData:method:)]];
  nextId_ = 1001;

  return self;
}

- (NSObject<HTTPResponse> *)createSessionWithData:(id)desiredCapabilities
                                           method:(NSString *)method {
//	if (![method isEqualToString:@"POST"])
//		return nil;
  
  int sessionId = nextId_++;
  
  NSLog(@"session %d created", sessionId);

  // Sessions don't really mean anything on the iphone. There's only one
  // browser, only one session and only one context.

  HTTPVirtualDirectory *session = [HTTPVirtualDirectory virtualDirectory];
  [self setResource:session
           withName:[NSString stringWithFormat:@"%d", sessionId]];

  HTTPVirtualDirectory *context = [[[Context alloc]
                                    initWithSessionId:sessionId]
                                    autorelease];
 
  [session setResource:context withName:@"foo"];

  return [HTTPRedirectResponse redirectToURL:
          [NSString stringWithFormat:@"session/%d/foo/", sessionId]];
}

@end
