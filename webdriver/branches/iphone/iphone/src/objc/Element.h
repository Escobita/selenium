//
//  Element.h
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
#import "HTTPVirtualDirectory.h"

@class ElementStore;

// This represents a web element accessible via :context/element/X where X is
// the element's elementId. (These are numbers indexed from 0, with 0 reserved
// for the document).
@interface Element : HTTPVirtualDirectory {
 @private
  // The ID of the element. The element is accessible in JS at
  // ELEM_ARRAY[id] or in REST at .../context/element/id
  NSString *elementId_;
  
  // A link back to the element store. Needed for creating new elements.
  // Not retained to avoid a circular dependancy.
  ElementStore *elementStore_;
}

@property (nonatomic, readonly, copy) NSString *elementId;

// Designated initialiser. You probably shouldn't use this directly - instead
// use elementFromJSObject. That allows the ElementStore to specify the
// element's id.
- (id)initWithId:(NSString *)elementId inStore:(ElementStore *)store;

// Create a new element from a JSON expression and put the element into the
// element store specified.
+ (Element *)elementFromJSObject:(NSString *)object
                         inStore:(ElementStore *)store;

// Same as |elementFromJSObject:inStore:| above, but using the element's store.
- (Element *)elementFromJSObject:(NSString *)object;
- (NSString *)url;

// Is this element the document?
- (BOOL)isDocumentElement;

// Get a javascript string through which the element can be accessed and used
// in JS.
- (NSString *)jsLocator;

// Simulate a click on the element. A dict is passed in through REST, but it is
// ignored.
- (void)click:(NSDictionary *)dict;

// Clear the contents of this input field.
- (void)clear;

// Submit this form, or the form containing this element.
- (void)submit;

// The text contained in the element
- (NSString *)text;

// Type these keys into the element
- (void)sendKeys:(NSString *)keys;

// This method is only valid on checkboxes and radio buttons. That should be
// checked.
- (NSNumber *)checked;

// This method is only valid on checkboxes and radio buttons.
- (void)setChecked:(NSNumber *)numValue;

// Like |checked| above, we should check that the element is valid.
- (void)toggleSelected;

// Is the element enabled?
- (NSNumber *)enabled;

// Is the element displayed on the screen?
- (NSNumber *)displayed;

@end
