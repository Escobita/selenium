//
//  WebDriverResponse.m
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

#import "WebDriverResponse.h"
#import "HTTPJSONResponse.h"

@implementation WebDriverResponse

// These need to be dynamic so we can make sure to clear the cached response
// if it exists when these are changed.
@dynamic isError, value, sessionId, context;

- (id)initWithValue:(id)theValue {
	// Its an NSProxy. There is no init.
//	if (![super init])
//		return nil;

  [self setValue:theValue];

  // The sessionId and context are automatically set by |Context|.
  [self setContext:@"unknown"];
  [self setSessionId:@"unknown"];

  return self;
}

- (id)initWithError:(NSException *)error {
  if (![self initWithValue:[error userInfo]])
    return nil;
  
  [self setIsError:YES];
  return self;
}

+ (WebDriverResponse *)responseWithValue:(id)value {
  return [[[self alloc] initWithValue:value] autorelease];
}

+ (WebDriverResponse *)responseWithError:(id)error {
  return [[[self alloc] initWithError:error] autorelease];
}

- (void)dealloc {
  // I need to call the release methods instead of calling setValue:nil because
  // setValue tries to change the response_ object (which it can't) and throws
  // an exception.
  [value_ release];
  [sessionId_ release];
  [context_ release];
  [response_ release];
  [super dealloc];
}

- (NSDictionary *)convertToJSON {
  NSMutableDictionary *dict = [NSMutableDictionary dictionary];
  [dict setValue:value_ forKey:@"value"];
  [dict setValue:sessionId_ forKey:@"sessionId"];
  [dict setValue:context_ forKey:@"context"];
  [dict setValue:[NSNumber numberWithBool:isError_] forKey:@"error"];
  return dict;
}

- (void)createResponse {
  if (response_ == nil) {
    NSDictionary *dict = [self convertToJSON];
    response_ = [[HTTPJSONResponse alloc] initWithObject:(id)dict];
  }
}

// If we change any of the properties, we can no longer use our cached response
- (void)setResponseDirty {
  if (response_ != nil && [response_ offset] != 0) {
    @throw [NSException exceptionWithName:@"kResponseInUse"
                                   reason:@"Reading has already started on the response. You can't change it now."
                                 userInfo:nil];
  }

  [response_ release];
  response_ = nil;
}

- (void)forwardInvocation:(NSInvocation *)anInvocation {
  [self createResponse];
  [anInvocation setTarget:response_];
  [anInvocation invoke];
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector {
  NSMethodSignature *sig = [super methodSignatureForSelector:aSelector];
  if (sig != nil) {
    return sig;
  } else {
    [self createResponse];
    return [response_ methodSignatureForSelector:aSelector];
  }
}

- (BOOL)respondsToSelector:(SEL)aSelector {
  if ([super respondsToSelector:aSelector])
    return YES;
  
  [self createResponse];
  return [response_ respondsToSelector:aSelector];
}

- (NSString *)description {
  return [NSString stringWithFormat:@"{ value:%@ error:%d }", value_, isError_];
}

#pragma mark Properties

- (BOOL)isError {
  return isError_;
}

- (void)setIsError:(BOOL)newIsError {
  isError_ = newIsError;
  [self setResponseDirty];
}

- (id)value {
  return [[value_ retain] autorelease];
}

- (void)setValue:(id)newValue {
  [newValue retain];
  [value_ release];
  value_ = newValue;
  if (value_ != nil)
    [self setResponseDirty];
}

- (NSString *)sessionId {
  return sessionId_;
}

- (void)setSessionId:(NSString *)newSessionId {
//  NSLog(@"session id set to %@", newSessionId);
  [sessionId_ release];
  sessionId_ = [newSessionId copy];
  [self setResponseDirty];
}

- (NSString *)context {
  return [[context_ retain] autorelease];
}

- (void)setContext:(NSString *)newContext {
//  NSLog(@"context id set to %@", newContext);
  [context_ release];
  context_ = [newContext copy];
  [self setResponseDirty];
}

@end
