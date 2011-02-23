
#include "nsIBaseWindow.h"
#include "nsIWidget.h"
#import <Cocoa/Cocoa.h>
#include "nsCOMPtr.h"
#include "interactions.h"
#include <vector>
#include "nsIConsoleService.h"
#include "nsServiceManagerUtils.h"
#include "nsComponentManagerUtils.h"
#include "nsIComponentManager.h"
#include "nsStringAPI.h"


void sendKeys(WINDOW_HANDLE windowHandle, const wchar_t* value, int timePerKey) {
	nsCOMPtr<nsIConsoleService> aConsoleService =
    do_GetService( "@mozilla.org/consoleservice;1" );
	
	aConsoleService->LogStringMessage(NS_LITERAL_STRING( "Entered sendKeys").get());
	if (windowHandle) {
		aConsoleService->LogStringMessage(NS_LITERAL_STRING( "Have window handle").get());
	} else {
		aConsoleService->LogStringMessage(NS_LITERAL_STRING( "Do not have window handle").get());
	}
	
	nsIBaseWindow* window = (nsIBaseWindow*) windowHandle;
	nativeWindow* handle;
	window->GetParentNativeWindow(handle);
	
	if (!handle) {
		aConsoleService->LogStringMessage(NS_LITERAL_STRING( "Got the native window").get());
	}	
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
