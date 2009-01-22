//
//  WebServerController.h
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
#import <UIKit/UIKit.h>

@class HTTPServer;
@class WebViewController;
@class RESTServiceMapping;

// A shared object of this class creates, configures and controls the web server
@interface HTTPServerController : NSObject {
  HTTPServer *server_;
	
  WebViewController *viewController_;
	
  IBOutlet UILabel *statusLabel_;
	
  RESTServiceMapping *serviceMapping_;
}

@property (retain, nonatomic) UILabel *statusLabel;
@property (retain, nonatomic) WebViewController *viewController;
@property (readonly, nonatomic) RESTServiceMapping *serviceMapping;

+(HTTPServerController *)sharedInstance;

@end
