package org.openqa.selenium.ie;

import org.openqa.selenium.ie.WebDriverLibrary.HWND;
import org.openqa.selenium.internal.InteractionData;

public class InternetExplorerInteractionData implements InteractionData {
	private final HWND hwnd;
	private final int x;
	private final int y;

	public InternetExplorerInteractionData(HWND hwnd, int x, int y) {
		this.hwnd = hwnd;
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public HWND getHwnd() {
		return hwnd;
	}
}
