
#include "interactions.h"

void sendKeys(WINDOW_HANDLE windowHandle, const wchar_t* value, int timePerKey) {
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
