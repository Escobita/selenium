//
//  Element.m
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

#import "ElementStore.h"
#import "JSONRESTResource.h"
#import "HTTPRedirectResponse.h"
#import "WebViewController.h"
#import "HTTPVirtualDirectory+AccessViewController.h"
#import "WebDriverResource.h"
#import "ElementStore+FindElement.h"
#import "Element.h"

static const NSString *JSARRAY = @"_WEBDRIVER_ELEM_CACHE";

@implementation ElementStore

@synthesize document = document_;

- (void)configureJSStore {
  [[self viewController] jsEval:[NSString stringWithFormat:
                                 @"if (typeof %@ !== \"object\") var %@ = [];",
                                 JSARRAY,
                                 JSARRAY]];
}

- (id)init {
  if (![super init])
    return nil;
  
  [self configureJSStore];
  
  [self setIndex:[WebDriverResource resourceWithTarget:self
                                             GETAction:NULL
                                            POSTAction:@selector(findElementByMethod:query:)]];
  
  document_ = [self elementFromJSObject:@"document"];
  
  return self;
}

- (void)dealloc {
  [super dealloc];
}

+ (ElementStore *)elementStore {
  return [[[self alloc] init] autorelease];
}

- (NSString *)generateElementId {
  return [NSString stringWithFormat:@"%d", nextElementId_++];
}

- (NSString *)jsLocatorForElementWithId:(NSString *)elementId {
  // This is a bit of a hack. Remember that element 0 is always the document.
  // Sometimes the URL will change and we won't reset ELEM_CACHE[0] = document
  // again. Instead of setting it, we'll just refer to element 0 as the document
  // everywhere that it counts.
  if ([elementId isEqualToString:@"0"])
    return @"document";
  return [NSString stringWithFormat:@"%@[%@]", JSARRAY, elementId];
}

- (NSString *)jsLocatorForElement:(Element *)element {
  return [self jsLocatorForElementWithId:[element elementId]];
}

- (Element *)elementFromJSObject:(NSString *)jsObject {
  if ([[self viewController] jsElementIsNull:jsObject])
    return nil;
  
  NSString *elementId = [self generateElementId];
  Element *element = [[Element alloc] initWithId:elementId inStore:self];
  [element autorelease];

  [self configureJSStore];
  
  // Set the javascript cache to contain the object
  [[self viewController] jsEval:[NSString stringWithFormat:@"%@ = %@;",
                                 [self jsLocatorForElementWithId:elementId],
                                 jsObject]];

  // Add the element to the REST interface
  [self setResource:element withName:elementId];

  return element;
}

- (NSArray *)elementsFromJSArray:(NSString *)container {
  // all I really want to do is map elementFromJSObject on the array. Sigh.
  NSString *lenStr = [[self viewController]
                      jsEval:[NSString stringWithFormat:@"%@.length",
                              container]];
  int length = [lenStr intValue];
  
  NSMutableArray *result = [NSMutableArray arrayWithCapacity:length];
  for (int i = 0; i < length; i++) {
    [result addObject:[self
                       elementFromJSObject:[NSString
                                            stringWithFormat:@"%@[%d]",
                                            container,
                                            i]]];
  }
  return result;
}

@end
