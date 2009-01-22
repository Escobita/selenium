//
//  WebDriverResponse.h
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
#import "HTTPResponse.h"

@class HTTPJSONResponse;

/* This class mirrors the 'Response' class in webdriver. When webdriver expects
 * a 'response' object, data is wrapped into one of these.
 *
 * Don't confuse HTTPResponse (A response to an HTTP message) and a
 * WebDriverResponse (an HTTPResponse containing a JSON object with fields
 * mirroring webdriver's
 */
@interface WebDriverResponse : NSObject<HTTPResponse> {
  // These fields mirror their equivalents in WebDriver
  BOOL isError_;
  id value_;
  NSString *sessionId_;
  NSString *context_;

  // We're a proxy around this response.
  HTTPJSONResponse *response_;
}

@property (nonatomic) BOOL isError;
@property (nonatomic, retain) id value;
@property (nonatomic, copy) NSString *sessionId;
@property (nonatomic, copy) NSString *context;

- (id)initWithValue:(id)value;
- (id)initWithError:(NSException *)error;

+ (WebDriverResponse *)responseWithValue:(id)value;
+ (WebDriverResponse *)responseWithError:(id)error;

@end
