//
//  RESTServiceMapping.m
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

#import "RESTServiceMapping.h"
#import "JSON.h"
#import "HTTPJSONResponse.h"
#import "HTTPVirtualDirectory.h"
#import "HTTPStaticResource.h"
#import "HTTPRedirectResponse.h"
#import "JSONRESTResource.h"
#import "Session.h"
#import "HTTPResponse+Utility.h"

@implementation RESTServiceMapping

//static NSString *urlBase = @"/hub";

- (id)init {
  if (![super init])
    return nil;
  
  serverRoot_ = [[HTTPVirtualDirectory alloc] init];

  // This is to make up for a bug in the java http client
  [serverRoot_ setResource:[HTTPStaticResource redirectWithURL:@"/hub/session/"]
                  withName:@"session"];
  
  HTTPVirtualDirectory *restRoot = [[[HTTPVirtualDirectory alloc] init] autorelease];
  [serverRoot_ setResource:restRoot withName:@"hub"];
  
  HTTPDataResponse *response =
    [[HTTPDataResponse alloc]
                 initWithData:[@"<html><body><h1>hi</h1></body></html>"
            dataUsingEncoding:NSASCIIStringEncoding]];
  
  [restRoot setIndex:[HTTPStaticResource resourceWithResponse:response]];
  [response release];
  
  [restRoot setResource:[[[Session alloc] init] autorelease]
               withName:@"session"];
  
//  [session setIndex:[HTTPResourceResponseWrapper redirectWithURL:@"123/foo"]];
  
//  [restRoot setResource:[JSONRESTServiceHandler JSONResourceWithTarget:self action:@selector(createSession:method:)] withName:@"session"];

  return self;
}

- (NSObject<HTTPResponse> *)httpResponseForRequest:(CFHTTPMessageRef)request {

//  NSLog(@"RESTServiceMapping responseForRequest");

  NSString *method = [(NSString *)CFHTTPMessageCopyRequestMethod(request)
                      autorelease];
  
  // Extract requested URI
  NSURL *uri = [(NSURL *)CFHTTPMessageCopyRequestURL(request) autorelease];
  NSString *path = [uri relativeString]; // TODO: Do I want absoluteString here?
  
  NSData *data = [(NSData*)CFHTTPMessageCopyBody(request) autorelease];
  
  NSLog(@"Responding to request: %@ %@", method, uri);
  
  if (data)
  {
    NSLog(@"data: '%@'", [[[NSString alloc] initWithData:data
                                                encoding:NSUTF8StringEncoding]
                            autorelease]);
  }
  
  id<HTTPResponse,NSObject> response =
    [serverRoot_ httpResponseForQuery:path
                               method:method
                             withData:data];

  // Unfortunately, webdriver only supports absolute redirects. Hack hack hack.
  if ([response isKindOfClass:[HTTPRedirectResponse class]]) {
    [(HTTPRedirectResponse *)response expandRelativeUrlWithBase:uri];
  }
  
  if (response == nil) {
    NSLog(@"404 - could not create response for request at %@", uri);
  } else {
//    NSLog(@"Responding with '%@'", [response description]);
  }
  
  return response;
}

@end
