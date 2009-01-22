//
//  HTTPServerTests.m
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

#import "GTMSenTestCase.h"
#import "HTTPServer.h"
#import "WebDriverHTTPConnection.h"
#import "AsyncSocket.h"
#import "WebDriverHTTPConnection.h"
#import "HTTPResponse.h"
#import "HTTPRedirectResponse.h"

@interface WebDriverHTTPConnection (Internal)

-(void)replyToHTTPRequest;

@end

@interface WebDriverHTTPConnection (TestingAdditions)

- (void)setRequest:(CFHTTPMessageRef)newRequest;

@end

@implementation WebDriverHTTPConnection (TestingAdditions)

- (void)setRequest:(CFHTTPMessageRef)newRequest {
  if (request)
    CFRelease(request);
	
  CFRetain(newRequest);
	
  request = newRequest;
}

@end


@interface HTTPServerTests: SenTestCase {
  HTTPServer *server;
	
  NSString *lastUrl;
  NSString *lastMethod;
  NSData *lastDataBody;
	
  NSObject<HTTPResponse> *response;
}

@end


@implementation HTTPServerTests

-(void) setUp {
  server = [[HTTPServer alloc] init];
  [server setDelegate:self];
  [server setConnectionClass:[WebDriverHTTPConnection class]];
  [server setDocumentRoot:[NSURL fileURLWithPath:[@"/" stringByExpandingTildeInPath]]];
  NSError *error = nil;

  BOOL success = [server start:&error];

  STAssertEquals(YES, success,
				   @"Could not start server: %@",
				   error);
}

-(void) tearDown {
  [server release];
}

- (NSObject<HTTPResponse> *)dummyHTTPResponse {
  NSString *dataString = @"<html><body><h1>hello</h1></body></html>";
  NSData *data = [dataString dataUsingEncoding:NSUTF8StringEncoding];
  return [[[HTTPDataResponse alloc] initWithData:data] autorelease];
}

- (NSObject<HTTPResponse> *)httpResponseForRequest:(CFHTTPMessageRef)request {
  [lastUrl release];
  [lastMethod release];
  [lastDataBody release];
	
  lastUrl = (NSString*)CFURLCopyPath(CFHTTPMessageCopyRequestURL(request));
  lastMethod = (NSString*)CFHTTPMessageCopyRequestMethod(request);
  lastDataBody = (NSData*)CFHTTPMessageCopyBody(request);
	
  if (response == nil)
    return [self dummyHTTPResponse];
  else
    return [[response retain] autorelease];
}

// Test if the server has started.
-(void)testStarted {
  // We don't need to do anything here.
}

- (NSURL *)baseURL {
  return [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:%d/", [server port]]];
}

- (void)injectRequest:(CFHTTPMessageRef)request {
  AsyncSocket *sock = [[AsyncSocket alloc] init];
  WebDriverHTTPConnection *connection = [[WebDriverHTTPConnection alloc] initWithAsyncSocket:sock forServer:server];
  [connection onSocketWillConnect:sock];
  //	[connection onSocket:sock didReadData:(NSData*)data withTag:0];
  [connection setRequest:request];
  [connection replyToHTTPRequest];
	
  [connection autorelease];
  [sock release];
}

- (void)sendTestRequestWithMethod:(NSString *)method data:(NSString *)dataStr {
  STAssertNotNULL(server, @"server is null");
  NSString *testURL = @"/a/b/c";
	
  NSURL *url = [NSURL URLWithString:testURL relativeToURL:[self baseURL]];
  CFHTTPMessageRef request = CFHTTPMessageCreateRequest(NULL, (CFStringRef)method, (CFURLRef)url, kCFHTTPVersion1_1);
	
  NSData *data = nil;
  if (dataStr != nil) {
    data = [dataStr dataUsingEncoding:NSUTF8StringEncoding];
    CFHTTPMessageSetBody(request, (CFDataRef)data);
  }
	
  [self injectRequest:request];
  CFRelease(request);
	
  STAssertNotNULL(lastMethod, @"Did not recieve HTTP request");
  STAssertEqualStrings(method, lastMethod,
                       @"Recieved method incorrect - expected %@ recieved %@",
                       method, lastMethod);
  STAssertEqualStrings(testURL, lastUrl,
                       @"Recieved URL incorrect - expected %@ recieved %@",
                       testURL, lastUrl);

  if (dataStr != nil) {
    BOOL dataMathches = [lastDataBody isEqualToData:data];
    STAssertTrue(dataMathches, @"Body data does not match!");
  }
}

- (void)testGET {
  [self sendTestRequestWithMethod:@"GET" data:nil];
}

- (void)testDELETE {
  [self sendTestRequestWithMethod:@"DELETE" data:nil];
}

- (void)testPUT {
  [self sendTestRequestWithMethod:@"PUT" data:@"hi mum"];
}

- (void)testPOST {
  [self sendTestRequestWithMethod:@"POST" data:@"hello there"];
}

// TODO: This should be broken out into |HTTPRedirectResponse.m|.
- (void)testRedirect {
  NSString *destinationURL = @"/foo/bar/bat";
  response = [HTTPRedirectResponse redirectToURL:destinationURL];
	
  STAssertNotNULL(server, @"server is null");
  NSString *testURL = @"/a/b/c";
	
  NSURL *url = [NSURL URLWithString:testURL relativeToURL:[self baseURL]];
  CFHTTPMessageRef request = CFHTTPMessageCreateRequest(NULL, CFSTR("GET"), (CFURLRef)url, kCFHTTPVersion1_1);
  [self injectRequest:request];
  CFRelease(request);
	
  // TODO: This test is incomplete.
}
@end
