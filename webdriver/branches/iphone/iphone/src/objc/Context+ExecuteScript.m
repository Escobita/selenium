//
//  Context+ExecuteScript.m
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

#import "Context+ExecuteScript.h"
#import "HTTPVirtualDirectory+AccessViewController.h"
#import "JSON.h"
#import "WebViewController.h"
#import "NSException+WebDriver.h"
#import "Element.h"

@implementation Context (ExecuteScript)

// The arguments passed to executeScript look like [{value:123 type:"NUMBER"}]
- (NSArray *)convertArgumentsToJavascript:(NSArray *)arguments {
  NSMutableArray *convertedArguments
  = [NSMutableArray arrayWithCapacity:[arguments count]];
  
  for (id element in arguments) {
    if (![element isKindOfClass:[NSDictionary class]]) {
      NSLog(@"Could not parse argument %@", element);
      [convertedArguments addObject:[NSNull null]];
    }
    
    NSDictionary *arg = (NSDictionary *)element;
    id value = [arg objectForKey:@"value"];
    NSString *type = [arg objectForKey:@"type"];
    if ([type isEqualToString:@"ELEMENT"]) {
      // TODO: Get the element and put a javascript expression for it in the argument
      value = @"{}";
    }
    //    NSLog(@"argument: %@ of type %@", value, type);
    
    // Except for elements, I can just let the JSON code work out the type from
    // the JSON.
    [convertedArguments addObject:value];
  }
  
  return convertedArguments;
}

// Execute the script given. Returns a dictionary with type: and value:
// properties.
- (NSDictionary *)executeScript:(NSString *)code
                  withArguments:(NSArray *)arguments {
  arguments = [self convertArgumentsToJavascript:arguments];
  
  NSString *argsAsString = [arguments JSONRepresentation];
  if (argsAsString == nil)
    argsAsString = @"null";
  
  NSString *script = [NSString stringWithFormat:
                      @"var f = function(){%@}; "
                      "var result = f.apply(null, %@); result",
                      code, argsAsString];
  
  NSString *result = [[self viewController] jsEval:script];  
  NSString *isNull = [[self viewController] jsEval:@"result === null"];
  NSString *jsType = [[self viewController] jsEval:@"typeof result"];
  NSString *isElement = [[self viewController] jsEval:@"result instanceof HTMLElement"];
  /*
   if (arg instanceof String) {
   converted.put("type", "STRING");
   converted.put("value", arg);
   } else if (arg instanceof Number) {
   converted.put("type", "NUMBER");
   converted.put("value", ((Number) arg).longValue());
   } else if (isPrimitiveNumberType(arg)) {
   converted.put("type", "NUMBER");
   converted.put("value", getPrimitiveTypeAsLong(arg));
   } else if (arg instanceof Boolean) {
   converted.put("type", "BOOLEAN");
   converted.put("value", ((Boolean) arg).booleanValue());
   } else if (arg.getClass() == boolean.class) {
   converted.put("type", "BOOLEAN");
   converted.put("value", arg);
   } else if (arg instanceof FirefoxWebElement) {
   converted.put("type", "ELEMENT");
   converted.put("value", ((FirefoxWebElement) arg).getElementId());
   } else {
   throw new IllegalArgumentException("Argument is of an illegal type: " + arg);
   }*/
  
  NSString *value = result;
  NSString *type = [jsType uppercaseString];
  
  if ([value isEqualToString:@""])
    value = nil;
  
  if ([isNull isEqualToString:@"true"]) {
    type = @"NULL";
    value = nil;
  }
  else if ([isElement isEqualToString:@"true"]) {
    // create element then...
    Element *elem = [[self elementStore] elementFromJSObject:@"result"];
    value = [elem url];
    type = @"ELEMENT";
  }
  
  if (value == nil)
    return [NSDictionary dictionaryWithObjectsAndKeys:type, @"type", nil];
  else
    return [NSDictionary dictionaryWithObjectsAndKeys:
            value, @"value",
            type, @"type", nil];
}

@end
