//
//  Element+FindElement.m
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

#import "Element+FindElement.h"
#import "HTTPVirtualDirectory+AccessViewController.h"
#import "WebViewController+FindElement.h"
#import "ElementStore.h"
#import "WebDriverResource.h"
#import "NSException+WebDriver.h"

@implementation Element (FindElement)

// add the strange element and elements subdirs to the vdir.
- (void)addSearchSubdirs {
  // This represents the element/ subdirectory of the element.
  HTTPVirtualDirectory *findElement = [HTTPVirtualDirectory virtualDirectory];
  // And this represents the elements/
  HTTPVirtualDirectory *findElements = [HTTPVirtualDirectory virtualDirectory];
  
  NSArray *searchMethods = [NSArray arrayWithObjects:@"xpath",
                            @"name",
                            @"id",
                            @"link+text",
                            @"class+name",
                            nil];
  
  for (NSString *method in searchMethods) {
    [findElement setResource:[WebDriverResource resourceWithTarget:self
                                                         GETAction:NULL
                                                        POSTAction:@selector(findElementUsing:)]
                    withName:method];
    [findElements setResource:[WebDriverResource resourceWithTarget:self
                                                          GETAction:NULL
                                                         POSTAction:@selector(findElementsUsing:)]
                     withName:method];    
  }
  
  [self setResource:findElement withName:@"element"];
  [self setResource:findElements withName:@"elements"];
}

// This function takes an array of Element* and returns an array of NSStrings
// of the form "element/$ID". This is needed for |findElementsByMethod:query:|
// below.
- (NSArray *)elementsToElementIds:(NSArray *)array {
  if (array == nil)
    return nil;
  
  NSMutableArray *output = [NSMutableArray arrayWithCapacity:[array count]];
  for (Element *elem in array) {
    [output addObject:[elem url]];
  }
  return output;
}

// Do a search by xpath.
- (NSArray *)elementsByXPath:(NSString *)xpath to:(NSString *)container {
  NSString *query = [NSString stringWithFormat:
   @"var elemsByXpath = function(xpath, context) {\r"
    "var result = document.evaluate(xpath, context, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);\r"
    "var arr = new Array();\r"
    "var element = result.iterateNext();\r"
    "while (element) {\r"
    "  arr.push(element);\r"
    "  element = result.iterateNext();\r"
    "}\r"
    "return arr;\r"
    "}; var %@ = elemsByXpath(\"%@\",%@);", container, xpath, [self jsLocator]];
  [[self viewController] jsEval:@"%@.length", container];
  [[self viewController] jsEval:query];
  return [elementStore_ elementsFromJSArray:container];
}

// Search for elements by their name= attribute.
- (NSArray *)elementsByName:(NSString *)name to:(NSString *)container {
  if ([self isDocumentElement]) {
    NSString *query = [NSString stringWithFormat:
                       @"var %@ = %@.getElementsByName(\"%@\");",
                       container,
                       [self jsLocator],
                       name];
    [[self viewController] jsEval:query];
    return [elementStore_ elementsFromJSArray:container];
  } else {
    // I'm just going to use xpath to do this.
    return [self elementsByXPath:[NSString stringWithFormat:@".//*[@name = '%@']", name]
                              to:container];
  }
}

// Is this element a direct or indirect decendant of the given element?
- (BOOL)elementIsDecendantOfElement:(Element *)element {
  NSString *result = [[self viewController] jsEval:
  @"var elementIsDecendant = function(element, parent) {\r"
   "var tmp = element;\r"
   "while (tmp != null) {\r"
   "  if (tmp == parentElement)\r"
   "    return true;\r"
   "  tmp = tmp.parentNode;\r"
   "}\r"
   "return false;\r"
   "}; elementIsDecendant(%@, %@)", [self jsLocator], [element jsLocator]];
  return [result isEqualToString:@"true"];
}

- (NSArray *)elementsById:(NSString *)anId to:(NSString *)container {
  [[self viewController] getElementById:anId
                                   toJS:[NSString stringWithFormat:@"var %@",
                                         container]];
  Element *element = [self elementFromJSObject:container];
  if (element) {
    if ([self isDocumentElement] || [element elementIsDecendantOfElement:self])
      return [NSArray arrayWithObject:element];
    else
      return [self elementsByXPath:[NSString stringWithFormat:@".//*[@id = '%@']",
                                    anId]
                                to:container];
  }
  else
    return [NSArray array];
}

// Returns an array of the links in the child tree from this element.
- (NSArray *)links {
  NSString *container = @"_WEBDRIVER_links";
  [[self viewController] jsEval:@"var %@ = %@.getElementsByTagName('A');",
    container, [self jsLocator]];
  return [elementStore_ elementsFromJSArray:container];
}

- (NSArray *)findElementsByLinkText:(NSString *)text
                                 to:(NSString *)container {
  NSArray *links = [self links];
  
  NSMutableArray *result = [NSMutableArray array];
  for (Element *elem in links) {
    // I'm going to do a straight comparison. If this search should be case-
    // insensitive or something, use |NSString|'s compare:options:range:
    if ([[elem text] isEqualToString:text]) {
      [result addObject:elem];
    }
  }
  
  return result;
}

- (NSArray *)findElementsByPartialLinkText:(NSString *)text
                                        to:(NSString *)container {
  NSArray *links = [self links];
  
  NSMutableArray *result = [NSMutableArray array];
  for (Element *elem in links) {
    NSRange range = [[elem text] rangeOfString:text];
    if (range.location != NSNotFound)
      [result addObject:elem];
  }
  
  return result;
}

- (NSArray *)findElementsByClassName:(NSString *)class
                                  to:(NSString *)container {
  [[self viewController] jsEval:@"var %@ = %@.getElementsByClassName('%@');",
   container, [self jsLocator], class];
  return [elementStore_ elementsFromJSArray:container];
}

// Find elements by the given method passed in as a string.
- (NSArray *)findElementsByMethod:(NSString *)method query:(NSString *)query {
  NSString *tempStore = @"_WEBDRIVER_elems";
  
  NSArray *result = nil;
  
  if ([method isEqualToString:@"id"]) {
    result = [self elementsById:query to:tempStore];
  }
  else if ([method isEqualToString:@"xpath"]) {
    result = [self elementsByXPath:query to:tempStore];
  }
  else if ([method isEqualToString:@"name"]) {
    result = [self elementsByName:query to:tempStore];
  }
  else if ([method isEqualToString:@"link text"]) {
    result = [self findElementsByLinkText:query to:tempStore];
  }
  else if ([method isEqualToString:@"class name"]) {
    result = [self findElementsByClassName:query to:tempStore];
  }
  else {
    NSLog(@"Cannot search by method %@", method);
    return nil;
  }
  
  result = [self elementsToElementIds:result];
  return result;
}

- (NSArray *)findElementsUsing:(NSDictionary *)dict {
  // This maps the /element/id/element/method to findElementsByMethod.
  // They shouldn't be different, but they are.
  NSString *query = [dict objectForKey:@"value"];
  NSString *method = [dict objectForKey:@"using"];
  return [self findElementsByMethod:method query:query];
}

- (NSArray *)findElementUsing:(NSDictionary *)dict {
  NSArray *results = [self findElementsUsing:dict];
  if (results && [results count] > 0)
    return [NSArray arrayWithObject:[results objectAtIndex:0]];
  else
    @throw([NSException webDriverExceptionWithMessage:@"Unable to locate element"
                                       webDriverClass:@"org.openqa.selenium.NoSuchElementException"]);
}
@end
