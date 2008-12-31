package org.openqa.selenium.firefox;

import org.openqa.selenium.internal.InteractionData;

public class FirefoxInteractionData implements InteractionData {
	private final int x;
	private final int y;

	public FirefoxInteractionData(long hwnd, int x, int y) {
		this(x, y);
	}

	public FirefoxInteractionData(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

}
