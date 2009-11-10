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


#import "Element.h"
#import "JSONRESTResource.h"
#import "HTTPRedirectResponse.h"
#import "ElementStore.h"
#import "WebDriverResource.h"
#import "HTTPVirtualDirectory+AccessViewController.h"
#import "Element+FindElement.h"
#import "Attribute.h"
#import "MainViewController.h"
#import "WebViewController.h"
#import "NSString+SBJSON.h"
#import "NSException+WebDriver.h"

@implementation Element

@synthesize elementId = elementId_;

- (id)initWithId:(NSString *)elementId inStore:(ElementStore *)store {
  if (![super init])
    return nil;
  
  elementId_ = [elementId copy];
  
  // Note that this is not retained as per delegate pattern.
  elementStore_ = store;

  [self setMyWebDriverHandlerWithGETAction:NULL
                                POSTAction:@selector(click:)
                                  withName:@"click"];
//  [self setMyWebDriverHandlerWithGETAction:NULL
//                                POSTAction:@selector(clickSimulate:)
//                                  withName:@"click"];
  
  [self setMyWebDriverHandlerWithGETAction:NULL
                                POSTAction:@selector(clearWrapper:)
                                  withName:@"clear"];
  
  [self setMyWebDriverHandlerWithGETAction:NULL
                                POSTAction:@selector(submitWrapper:)
                                  withName:@"submit"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(text)
                                POSTAction:NULL
                                  withName:@"text"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(value)
                                POSTAction:@selector(sendKeys:)
                                  withName:@"value"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(isChecked)
                                POSTAction:@selector(setChecked:)
                                  withName:@"selected"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(isEnabled)
                                POSTAction:NULL
                                  withName:@"enabled"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(isDisplayed)
                                POSTAction:NULL
                                  withName:@"displayed"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(locationAsDictionary)
                                POSTAction:NULL
                                  withName:@"location"];

  [self setMyWebDriverHandlerWithGETAction:@selector(sizeAsDictionary)
                                POSTAction:NULL
                                  withName:@"size"];
  
  [self setMyWebDriverHandlerWithGETAction:@selector(name) 
                                POSTAction:NULL
                                  withName:@"name"];
    
  [self addSearchSubdirs];
  
  [self setResource:[Attribute attributeDirectoryForElement:self]
           withName:@"attribute"];
  
  return self;
}

- (void)dealloc {
  [elementId_ release];
  [super dealloc];
}

+ (Element *)elementFromJSObject:(NSString *)object
                         inStore:(ElementStore *)store {
  return [store elementFromJSObject:object];
}

// Same as |elementFromJSObject:inStore:| above, but using the element's store.
- (Element *)elementFromJSObject:(NSString *)object {
  return [Element elementFromJSObject:object inStore:elementStore_];
}

// This method returns the string representation of a javascript object.
- (NSString *)jsLocator {
  return [elementStore_ jsLocatorForElement:self];
}

// Is this element a (/the) document?
- (BOOL)isDocumentElement {
  if ([elementId_ isEqualToString:@"0"]) {
    return YES;
  } else {
  // I have no idea how to clean this indenting - It looks like lisp.
    return [[[self viewController]
             jsEval:@"%@ instanceof HTMLDocument", [self jsLocator]]
            isEqualToString:@"true"];
  }
}

// Get the element's URL relative to the context.
- (NSString *)url {
  return [NSString stringWithFormat:@"element/%@", [self elementId]];
}

#pragma mark Webdriver methods

- (void)click:(NSDictionary *)dict {
  // TODO: Get the pixel coordinates and simulate a tap. Wait for page to
  // load before continuing.
  NSString *locator = [self jsLocator];
  [[self viewController] jsEvalAndBlock:
   @"(function(element) {\r"
   "  function triggerMouseEvent(element, eventType) {\r"
   "    var event = element.ownerDocument.createEvent('MouseEvents');\r"
   "    var view = element.ownerDocument.defaultView;\r"
   "    event.initMouseEvent(eventType, true, true, view, 1, 0, 0, 0, 0,\r"
   "        false, false, false, false, 0, element);\r"
   "    element.dispatchEvent(event);\r"
   "  }\r"
   "  triggerMouseEvent(element, 'mouseover');\r"
   "  triggerMouseEvent(element, 'mousemove');\r"
   "  triggerMouseEvent(element, 'mousedown');\r"
   "  if (element.ownerDocument.activeElement != element) {\r"
   "    if (element.ownerDocument.activeElement) {\r"
   "      element.ownerDocument.activeElement.blur();\r"
   "    }\r"
   "    element.focus();\r"
   "  }\r"
   "  triggerMouseEvent(element, 'mouseup');\r"
   "  triggerMouseEvent(element, 'click');\r"
   "})(%@);\r", locator];
}

// This returns the pixel position of the element on the page in page
// coordinates.
- (CGPoint)location {
  NSString *container = @"_WEBDRIVER_pos";
  [[self viewController] jsEval:
  @"var locate = function(elem) {\r"
  "  var x = 0, y = 0;\r"
  "  while (elem && elem.offsetParent) {\r"
  "    x += elem.offsetLeft;\r"
  "    y += elem.offsetTop;\r"
  "    elem = elem.offsetParent;\r"
  "  }\r"
  "  return {x: x, y: y};\r"
  "};\r"
  "var %@ = locate(%@);", container, [self jsLocator]];

  float x = [[self viewController] floatProperty:@"x" ofObject:container];
  float y = [[self viewController] floatProperty:@"y" ofObject:container];
  
  return CGPointMake(x, y);
}

// Fetches the size of the element in page coordinates.
- (CGSize)size {
  float width = [[[self viewController] jsEval:@"%@.offsetWidth",
                  [self jsLocator]] 
                 floatValue];
  float height = [[[self viewController] jsEval:@"%@.offsetHeight",
                   [self jsLocator]] 
                  floatValue];
 
  return CGSizeMake(width, height);
}

// Fetches the bounds of the element in page coordinates. This is built from
// |size| and |location|.
- (CGRect)bounds {
  CGRect bounds;
  bounds.origin = [self location];
  bounds.size = [self size];
  return bounds;
}

- (void)clickSimulate:(NSDictionary *)dict {
  CGRect currentPosition = [self bounds];
  CGPoint midpoint = CGPointMake(CGRectGetMidX(currentPosition),
                                 CGRectGetMidY(currentPosition));
  [[[MainViewController sharedInstance] webViewController]
   clickOnPageElementAt:midpoint];
}

- (void)clear {
  NSString *locator = [self jsLocator];
  [[self viewController] jsEval:
   [NSString stringWithFormat:
    @"(function(elem) {\r"
     "  if (((elem instanceof HTMLInputElement && elem.type == 'text') ||\r"
     "       elem instanceof HTMLTextAreaElement) && elem.value) {\r"
     "    elem.value = '';\r"
     "    var e = elem.ownerDocument.createEvent('HTMLEvents');\r"
     "    e.initEvent('change', true, true);\r"
     "    elem.dispatchEvent(e);\r"
     "  }\r"
     "})(%@);", locator]];
}

- (void)clearWrapper:(NSDictionary *)ignored {
  return [self clear];
}

- (void)submit {
  NSString *locator = [self jsLocator];
  [[self viewController] jsEvalAndBlock:
   [NSString stringWithFormat:
    @"(function(elem) {\r"
    "  var current = elem;\r"
    "  while (current && current != elem.ownerDocument.body) {\r"
    "    if (current.tagName.toLowerCase() == 'form') {\r"
    "      current.submit();\r"
    "      return;\r"
    "    }\r"
    "    current = current.parentNode;\r"
    "  }\r"
    "})(%@);", locator]];
}
- (void)submitWrapper:(NSDictionary *)ignored {
  return [self submit];
}

- (NSString *)text {
  return [[[self viewController] jsEval:[NSString stringWithFormat:
                                        @"%@.innerText", [self jsLocator]]]
          stringByTrimmingCharactersInSet:
            [NSCharacterSet whitespaceAndNewlineCharacterSet]];
}

- (void)sendKeys:(NSDictionary *)dict {
  [[self viewController] 
   jsEval:[NSString stringWithFormat:@"%@.value=\"%@\"", [self jsLocator], [[dict objectForKey:@"value"] componentsJoinedByString:@""]]];	
}

- (NSString *)value {
  return [[self viewController]
          jsEval:[NSString stringWithFormat:@"%@.value", [self jsLocator]]];
}

// This method is only valid on option elements, checkboxes and radio buttons.
- (NSNumber *)isChecked {
  BOOL isSelectable = [[[self viewController]
                        jsEval:[NSString stringWithFormat:
                                @"var elem = %@;\r"
                                "elem instanceof HTMLOptionElement ||\r"
                                "(elem instanceof HTMLInputElement &&\r"
                                " elem.type in {'checkbox':0, 'radio':0});",
                                [self jsLocator]]] isEqualToString:@"true"];
  if (!isSelectable) {
    return [NSNumber numberWithBool:NO];
  }

  BOOL selected = [[[self viewController] jsEval:
   [NSString stringWithFormat:
    @"(function(elem) {\r"
    "  if (elem.tagName.toLowerCase() == 'option') {\r"
    "    return elem.selected;\r"
    "  } else {\r"
    "    return elem.checked;\r"
    "  }\r"
    "})(%@)", [self jsLocator]]] isEqualToString:@"true"];
  
  return [NSNumber numberWithBool:selected];
}

// This method is only valid on option elements, checkboxes and radio buttons.
- (void)setChecked:(NSNumber *)numValue {
  if (![[self isEnabled] boolValue]) {
    @throw [NSException webDriverExceptionWithMessage:@"You may not select a disabled element"
                                       webDriverClass:@"java.lang.UnsupportedOperationException"];
  }
  
  BOOL isSelectable = [[[self viewController]
                        jsEval:[NSString stringWithFormat:
                                @"var elem = %@;\r"
                                "elem instanceof HTMLOptionElement ||\r"
                                "(elem instanceof HTMLInputElement &&\r"
                                " elem.type in {'checkbox':0, 'radio':0});",
                                [self jsLocator]]] isEqualToString:@"true"];
  if (!isSelectable) {
    @throw [NSException webDriverExceptionWithMessage:@"You may not select an unselectable element"
                                       webDriverClass:@"java.lang.UnsupportedOperationException"];
  }

  [[self viewController] jsEval:
   [NSString stringWithFormat:
    @"(function(elem) {\r"
     "  var changed = false;\r"
     "  if (elem.tagName.toLowerCase() == 'option') {\r"
     "    if (!elem.selected) {\r"
     "      elem.selected = changed = true;\r"
     "    }\r"
     "  } else {\r"
     "    if (!elem.checked) {\r"
     "      elem.checked = changed = true;\r"
     "    }\r"
     "  }\r"
     "  if (changed) {\r"
     "    var e = elem.ownerDocument.createEvent('HTMLEvents');\r"
     "    e.initEvent('change', true, true);\r"
     "    elem.dispatchEvent(e);\r"
     "  }\r"
     "})(%@)", [self jsLocator]]];
}

// Like |checked| above, we should check that the element is valid.
- (void)toggleSelected {
  NSString *jsLocator = [self jsLocator];
  [[self viewController] jsEval:[NSString
                stringWithFormat:@"%@.focus(); %@.checked = !%@.checked",
                                 jsLocator, jsLocator, jsLocator]];
}

- (NSNumber *)isEnabled {
  BOOL enabled = [[[self viewController]
                  jsEval:[NSString stringWithFormat:@"%@.disabled",
                          [self jsLocator]]] isEqualToString:@"false"];
  
  return [NSNumber numberWithBool:enabled];
}

- (NSNumber *)isDisplayed {
  @throw [NSException webDriverExceptionWithMessage:@"Not Implemented"
                                     webDriverClass:nil];
}

- (NSDictionary *)locationAsDictionary {
  CGPoint location = [self location];
  return [NSDictionary dictionaryWithObjectsAndKeys:
          [NSNumber numberWithFloat:location.x], @"x",
          [NSNumber numberWithFloat:location.y], @"y",
          nil];
}

- (NSDictionary *)sizeAsDictionary {
  CGSize size = [self size];
  return [NSDictionary dictionaryWithObjectsAndKeys:
          [NSNumber numberWithFloat:size.width], @"width",
          [NSNumber numberWithFloat:size.height], @"height",
          nil];  
}

// Get an attribute with the given name.
- (NSString *)attribute:(NSString *)name {
  BOOL hasAttribute = [[[self viewController] jsEval:
                        @"%@.hasAttribute('%@')", [self jsLocator], name]
                       isEqualToString:@"true"];
  if (hasAttribute) {
    return [[self viewController] jsEval:
            @"%@.getAttribute('%@')",
            [self jsLocator],
            name];
  }
  
  if ([name isEqualToString:@"disabled"]) {
    return [[self viewController] jsEval:@"%@.disabled", [self jsLocator]];
  } else if (([name isEqualToString:@"checked"] || [name isEqualToString:@"selected"])
             && [[self name] isEqualToString:@"input"]) {
    return [[self viewController] jsEval:@"%@.checked", [self jsLocator]];
  } else if ([name isEqualToString:@"selected"]
             && [[self name] isEqualToString:@"option"]) {
    return [[self viewController] jsEval:@"%@.selected", [self jsLocator]];
  } else if ([name isEqualToString:@"index"]
             && [[self name] isEqualToString:@"option"]) {
    return [[self viewController] jsEval:@"%@.index", [self jsLocator]];
  }
  
  return nil;
}

// Get the tag name of this element, not the value of the name attribute:
// will return "input" for the element <input name="foo">
- (NSString *)name {
  return [[[self viewController] jsEval:
          @"%@.nodeName",
          [self jsLocator]] lowercaseString];
}

@end
