/*
 *  PlatformSpecificDefs.h
 *  SafariDriver
 *
 *  Created by Andrian Kurniady on 5/19/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#if defined(TARGET_OS_IPHONE) && TARGET_OS_IPHONE
// IPhone Deps here
#import <UIKit/UIKit.h>
typedef UIImage ImageType;
typedef UIWebView WebViewType;
#else
// Safari Deps here
#import <WebKit/WebKit.h>
typedef NSImage ImageType;
typedef WebView WebViewType;
#endif
