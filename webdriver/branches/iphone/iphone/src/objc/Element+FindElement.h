//
//  Element+FindElement.h
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
#import "Element.h"

// Find elements underneath this element in the DOM.
// These methods are accessible via:
// :context/element/X/element[s]/{xpath|name|id|link+text|class+name}
@interface Element (FindElement)

// add the search element/ and elements/ subdirs to the element vdir.
- (void)addSearchSubdirs;

// This will find child elements of an element
- (NSArray *)findElementsByMethod:(NSString *)method query:(NSString *)query;

// This is a wrapper around the wierd /element/id/element[s]/{xpath|name|...}
// methods. These are search methods too, but send their arguments in a dict
// which looks like: {"value":"test_id_out","using":"id","id":"23"}.
- (NSArray *)findElementsUsing:(NSDictionary *)dict;

// As |findElementsUsing:| above, but only returns one argument.
- (NSArray *)findElementUsing:(NSDictionary *)dict;
@end
