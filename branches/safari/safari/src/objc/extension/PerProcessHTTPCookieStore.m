//
//  PerProcessHTTPCookieStore
//  SafariDriver
//
//  Copyright 2010 WebDriver committers
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
#import "PerProcessHTTPCookieStore.h"
#import "SafariExtensionPaths.h"

#import <objc/objc.h>
#import <objc/objc-class.h>

@interface NSObject(NSHTTPCookieStoreInetrnal) 
- (id)initWithStorageLocation:(NSURL*)url;
@end

static id cookieStoreInitImpl(id self_) {
  NSURL* storageURL = NULL;
  NSString* pidPath = [[SafariExtensionPaths instance] cookieStorePath];
  storageURL = [NSURL fileURLWithPath:pidPath];
  return [self_ initWithStorageLocation:storageURL];
}

#if __OBJC2__

static id new_cookieStoreInternalInitIMP(id self_, SEL sel_) 
{
  return cookieStoreInitImpl(self_);
}


@implementation PerProcessHTTPCookieStore

+ (void)overrideHTTPCookieStoreInternal {
  Class class = objc_getClass("NSHTTPCookieStorageInternal");
  class_replaceMethod(class,@selector(init),(IMP)&new_cookieStoreInternalInitIMP,"@:");
}

+ (void)initialize {
  if(self == [PerProcessHTTPCookieStore class]) {
    [self overrideHTTPCookieStoreInternal];
  }
}

+ (void)makeSurePerProcessHTTPCookieStoreLinkedIn {
}

@end
#else

@interface NSHTTPCookieStorageInternal : NSObject {
}
- (id)initWithStorageLocation:(NSURL*)storageFileURL;
@end

@interface NSHTTPCookieStorageInternalOverride : NSHTTPCookieStorageInternal

- (id)init;
+ (void)makeSurePerProcessHTTPCookieStoreLinkedIn;

@end

@implementation NSHTTPCookieStorageInternalOverride

- (id)init
{
  if ([self respondsToSelector:@selector(initWithStorageLocation:)]) {
    return cookieStoreInitImpl(self);
  } else {
    // Todo (MiklosFazekas): on pre 10.6 systems there seems to be no way to change the cookie storage location
    // Todo (MiklosFazekas): check if we can find a magic function in CFNetwork. Maybe we can do a 
    //     CFHTTPCookieStorageCreateFromFile, and then set as the cookie store...
    // CF_EXPORT CFTypeRef _CFHTTPCookieStorageGetDefault();
    // CF_EXPORT void _CFHTTPCookieStorageSetDefaultLocation(CFTypeRef store,CFURLRef url);
    NSLog(@"Using shared cookie store, cannot set cookie store location on this os");
    [super init];
  }
}

+ (void)load 
{
  [self poseAsClass:[NSHTTPCookieStorageInternal class]];
}

@end

@implementation PerProcessHTTPCookieStore

+ (void)makeSurePerProcessHTTPCookieStoreLinkedIn
{
  // dummy, just so that we can call it to make sure it's linked in
}

@end

#endif

