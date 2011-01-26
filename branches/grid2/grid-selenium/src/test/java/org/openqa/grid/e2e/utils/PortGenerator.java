package org.openqa.grid.e2e.utils;

public class PortGenerator {

	private static int port = 4443;

	/**
	 * to avoid having 2 tests running in // using the same port.
	 * 
	 * @return
	 */
	public static synchronized int getNewPort() {
		port++;
		return port;
	}
}
