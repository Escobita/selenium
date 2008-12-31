package org.openqa.selenium.ie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

class JobbieLibraryInstance {
	public static WebDriverLibrary getLibraryInstance() {
		try {
			return (WebDriverLibrary) Native.loadLibrary("InternetExplorerDriver", WebDriverLibrary.class);
		} catch (UnsatisfiedLinkError e) {
			// Not a problem, let's assume we have no dll on the path
		}

		File dll = writeResourceToDisk("InternetExplorerDriver.dll");
		NativeLibrary.addSearchPath("InternetExplorerDriver", dll.getParent());
		return (WebDriverLibrary) Native.loadLibrary("InternetExplorerDriver",
				WebDriverLibrary.class);
	}

	private static File writeResourceToDisk(String resourceName)
			throws UnsatisfiedLinkError {
		InputStream is = InternetExplorerDriver.class
				.getResourceAsStream(resourceName);
		if (is == null)
			is = InternetExplorerDriver.class.getResourceAsStream("/"
					+ resourceName);

		FileOutputStream fos = null;

		try {
			File dll = File.createTempFile("webdriver", null);
			dll.deleteOnExit();
			fos = new FileOutputStream(dll);

			int count;
			byte[] buf = new byte[4096];
			while ((count = is.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, count);
			}

			return dll;
		} catch (IOException e2) {
			throw new UnsatisfiedLinkError("Cannot create temporary DLL: "
					+ e2.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e2) {
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e2) {
				}
			}
		}
	}
}