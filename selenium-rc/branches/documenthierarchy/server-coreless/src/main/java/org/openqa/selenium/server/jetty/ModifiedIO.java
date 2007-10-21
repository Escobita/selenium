package org.openqa.selenium.server.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class ModifiedIO {
	
	private final int BUFFER_SIZE;
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	
	public ModifiedIO() {
		this(DEFAULT_BUFFER_SIZE);
	}
	
	public ModifiedIO(int bufferSize) {
		this.BUFFER_SIZE = bufferSize;
	}
	
	/**
	 * Copy Stream in to Stream out until EOF or exception.
	 */
	public long copy(InputStream in, OutputStream out) throws IOException {
		return copy(in, out, -1);
	}

	public long copy(Reader in, Writer out) throws IOException {
		return copy(in, out, -1);
	}

	/**
	 * Copy Stream in to Stream for byteCount bytes or until EOF or exception.
	 * 
	 * @return Copied bytes count or -1 if no bytes were read *and* EOF was reached
	 */
	public long copy(InputStream in, OutputStream out, long byteCount)
			throws IOException {
		// @todo Add tweak for 10ms enhancement by creating buffer at object creation
		byte buffer[] = new byte[BUFFER_SIZE];
		int len;

		long returnVal = 0;

		if (byteCount >= 0) {
			while (byteCount > 0) {
				if (byteCount < BUFFER_SIZE)
					len = in.read(buffer, 0, (int) byteCount);
				else
					len = in.read(buffer, 0, BUFFER_SIZE);

				if (len == -1) {
					break;
				} else {
					returnVal += len;
				}

				byteCount -= len;
				out.write(buffer, 0, len);
			}
		} else {
			while (true) {
				len = in.read(buffer, 0, BUFFER_SIZE);
				if (len < 0) {
					break;
				} else {
					returnVal += len;
				}
				out.write(buffer, 0, len);
			}
		}

		return returnVal;
	}

	/**
	 * Copy Reader to Writer for byteCount bytes or until EOF or exception.
	 */
	public long copy(Reader in, Writer out, long byteCount) throws IOException {
		char buffer[] = new char[BUFFER_SIZE];
		int len;

		long returnVal = 0;

		if (byteCount >= 0) {
			while (byteCount > 0) {
				if (byteCount < BUFFER_SIZE)
					len = in.read(buffer, 0, (int) byteCount);
				else
					len = in.read(buffer, 0, BUFFER_SIZE);

				if (len == -1) {
					break;
				} else {
					returnVal += len;
				}

				byteCount -= len;
				out.write(buffer, 0, len);
			}
		} else {
			while (true) {
				len = in.read(buffer, 0, BUFFER_SIZE);
				if (len == -1) {
					break;
				} else {
					returnVal += len;
				}
				out.write(buffer, 0, len);
			}
		}

		return returnVal;
	}
}
