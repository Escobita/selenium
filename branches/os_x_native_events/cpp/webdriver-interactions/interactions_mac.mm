
#include "nsIBaseWindow.h"
#include "nsIWidget.h"
#import <Cocoa/Cocoa.h>
#include <mach/mach_time.h>
#include "nsCOMPtr.h"
#include "interactions.h"
#include "logging.h"
#include <vector>
#include "nsIConsoleService.h"
#include "nsServiceManagerUtils.h"
#include "nsComponentManagerUtils.h"
#include "nsIComponentManager.h"
#include "nsStringAPI.h"
#include "nsIXULWindow.h"

// From
// http://stackoverflow.com/questions/1597383/cgeventtimestamp-to-nsdate
// Which credits Apple sample code for this routine.
uint64_t UpTimeInNanoseconds(void) {
	uint64_t time;
	uint64_t timeNano;
	static mach_timebase_info_data_t sTimebaseInfo;
	
	time = mach_absolute_time();
	
	// Convert to nanoseconds.
	
	// If this is the first time we've run, get the timebase.
	// We can use denom == 0 to indicate that sTimebaseInfo is
	// uninitialised because it makes no sense to have a zero
	// denominator is a fraction.
	if (sTimebaseInfo.denom == 0) {
		(void) mach_timebase_info(&sTimebaseInfo);
	}
	
	// This could overflow; for testing needs we probably don't care.
	timeNano = time * sTimebaseInfo.numer / sTimebaseInfo.denom;
	return timeNano;
}

NSTimeInterval TimeIntervalSinceSystemStartup() {
	return UpTimeInNanoseconds() / 1000000000.0;
}


void sendKeys(WINDOW_HANDLE windowHandle, const wchar_t* value, int timePerKey) {
	nsCOMPtr<nsIConsoleService> aConsoleService = do_GetService("@mozilla.org/consoleservice;1" );
	
	aConsoleService->LogStringMessage(NS_LITERAL_STRING("Entered sendKeys").get());

	nsCOMPtr<nsIBaseWindow> baseWindow(do_QueryInterface((nsISupports*) windowHandle));
	if (!baseWindow) {
		LOG(FATAL) << "No base window";
		return;
	}
	nsCOMPtr<nsIWidget> parentWidget;
	baseWindow->GetParentWidget(getter_AddRefs(parentWidget));
	if (!parentWidget) {
		LOG(FATAL) << "No parent widget";
	} else {
		LOG(FATAL) << "There is a parent widget";
	}
	
	nsCOMPtr<nsIWidget> mainWidget;
	baseWindow->GetMainWidget(getter_AddRefs(mainWidget));
	if (!mainWidget) {
		LOG(FATAL) << "We do not have the widget";
		aConsoleService->LogStringMessage(NS_LITERAL_STRING("Did not get the widget").get());
		return;
	}

	
	void* win = mainWidget->GetNativeData(NS_NATIVE_DISPLAY);
	nsIWidget* curr = mainWidget;
	while (!win && (curr && curr != nsnull)) {
		LOG(FATAL) << "Looping to hunt out the parent window";
		win = curr->GetNativeData(NS_NATIVE_DISPLAY);
		if (curr->GetParent() == curr) {
			LOG(FATAL) << "My parent is myself";
			break;
		} 
		curr = curr->GetParent();
	}
	
	if (!win) {
		LOG(FATAL) << "Could not find native window";
		aConsoleService->LogStringMessage(NS_LITERAL_STRING( "Do not have the native window. Bailing.").get());
//		return;
	}	
//	LOG(FATAL) << "Class? " << [win class];

	
	[[NSApplication sharedApplication] activateIgnoringOtherApps:YES];
	[[[NSApplication sharedApplication] keyWindow] makeKeyAndOrderFront:nil];
	
//	// For now, let's hand craft a single event....
	NSEvent* keydown = [NSEvent keyEventWithType:NSKeyDown
										location:NSMakePoint(0, 0)
								   modifierFlags:0
									   timestamp:TimeIntervalSinceSystemStartup()
									windowNumber:[[[NSApplication sharedApplication] keyWindow] windowNumber]
										 context:nil
									  characters: @"a" 
					 charactersIgnoringModifiers: @"a"
									   isARepeat:NO
										 keyCode:0];
	
	NSEvent* keyup = [NSEvent keyEventWithType:NSKeyUp
										location:NSMakePoint(0, 0)
								   modifierFlags:0
									   timestamp:TimeIntervalSinceSystemStartup()
									windowNumber:[[[NSApplication sharedApplication] keyWindow] windowNumber]
										 context:nil
									  characters: @"a" 
					 charactersIgnoringModifiers: @"a"
									   isARepeat:NO
										 keyCode:0];
	
	LOG(FATAL) << "Sending spoofed keyboard events";
	
	[[NSApplication sharedApplication] performSelector:@selector(sendEvent:) withObject:keydown afterDelay:0.0];
	[[NSApplication sharedApplication] performSelector:@selector(sendEvent:) withObject:keyup afterDelay:0.1];
}

void sendKeyPress(WINDOW_HANDLE windowHandle, const wchar_t* value) {
}

void sendKeyRelease(WINDOW_HANDLE windowHandle, const wchar_t* value) {
}

BOOL_TYPE pending_input_events() {
	return 0;
}

// Mouse interactions
WD_RESULT clickAt(WINDOW_HANDLE directInputTo, long x, long y, long button) {
	return 0;
}

WD_RESULT mouseDownAt(WINDOW_HANDLE directInputTo, long x, long y, long button) {
	return 0;
}

WD_RESULT mouseUpAt(WINDOW_HANDLE directInputTo, long x, long y, long button) {
	return 0;
}

WD_RESULT mouseMoveTo(WINDOW_HANDLE directInputTo, long duration, long fromX, long fromY, long toX, long toY) {
	return 0;
}
