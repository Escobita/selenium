//
//  ElementStore+FindElement.h
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
#import "ElementStore.h"

// This category implements the findElement[s] methods accessable on
// :context/element and :context/elements.
// The methods simply forward the requests to the document element.
@interface ElementStore (FindElement)

// This throws an exception if it can't find the given element.
- (NSArray *)findElementByMethod:(NSString *)method query:(NSString *)query;

// This returns an array of all elements found.
- (NSArray *)findElementsByMethod:(NSString *)method query:(NSString *)query;

@end
