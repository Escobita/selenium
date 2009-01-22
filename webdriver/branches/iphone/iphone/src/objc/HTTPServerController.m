//
//  WebServerController.m
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

#import "HTTPServerController.h"
#import "WebDriverHTTPServer.h"
#import "WebDriverHTTPConnection.h"
#import "RESTServiceMapping.h"
#import <sys/socket.h>

@implementation HTTPServerController

@synthesize statusLabel=statusLabel_;
@synthesize viewController=viewController_;
@synthesize serviceMapping=serviceMapping_;

-(NSString *)getAddress {
  
  CFHostRef host = CFHostCreateWithName(NULL, CFSTR("localhost"));
  
  CFStreamError error;
  if (!CFHostStartInfoResolution(host, kCFHostAddresses, &error)) {
    NSLog(@"error: %d", error.error);
    CFRelease(host);
    return nil;
  }
  
  Boolean hasBeenResolved;
  NSArray *addresses = (NSArray *)CFHostGetAddressing(host, &hasBeenResolved);
  NSLog(@"%d addresses", [addresses count]);
  for (NSData *data in addresses) {
    struct sockaddr sock;
//    struct in_addr asdf;
    [data getBytes:&sock];
    for (int i = 0; i < 14; i++) {
      NSLog(@"%d", sock.sa_data[i]);
    }
//    NSLog(@"%d %d %d %d  %d %d %d %d", sock.sa_data[0], sock.sa_data[1], sock.sa_data[2], sock.sa_data[3]);
  }
  
//  if (hasBeenResolved

//  CFRelease(host);
  
  return @"unknown";
}

-(id) init {
  if (![super init])
    return nil;
  
  server_ = [[WebDriverHTTPServer alloc] init];

  [server_ setType:@"_http._tcp."];
  [server_ setPort:16000];
  [server_ setDelegate:self];
  [server_ setConnectionClass:[WebDriverHTTPConnection class]];
  
  NSError *error;
  BOOL success = [server_ start:&error];
  
  if(!success) {
    NSLog(@"Error starting HTTP Server: %@", error);
  }

  NSLog(@"HTTP server started on addr %@ port %d",
        @"unknown",
//        [self getAddress],
        [server_ port]);
  
  if (statusLabel_) {
    [statusLabel_ setText:[NSString stringWithFormat:@"Started on port %d",
                           [server_ port]]];
  }

  serviceMapping_ = [[RESTServiceMapping alloc] init];

  return self;
}

// Singleton

static HTTPServerController *singleton = nil;

+(HTTPServerController*) sharedInstance {
  if (singleton == nil) {
    singleton = [[HTTPServerController alloc] init];
  }
  
  return singleton;
}

- (NSObject<HTTPResponse> *)httpResponseForRequest:(CFHTTPMessageRef)request {
  return [serviceMapping_ httpResponseForRequest:request];
}

@end
