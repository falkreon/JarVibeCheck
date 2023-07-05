package blue.endless.jarvibecheck.impl;

import java.io.IOException;
import java.io.InputStream;

public class IntelDataInputStream {
	private final InputStream in;
	private boolean eof = false;;
	
	public IntelDataInputStream(InputStream in) {
		this.in = in;
	}
	
	public int i8() throws IOException {
		int result = in.read();
		if (result == -1) eof = true;
		return result & 0xFF;
	}
	
	public int i16() throws IOException {
		return
			i8() |
			(i8() << 8);
	}
	
	public int i32() throws IOException {
		return
			i8() |
			(i8() << 8) |
			(i8() << 16) |
			(i8() << 24);
	}
	
	public byte[] readBytes(int length) throws IOException {
		return in.readNBytes(length);
	}
	
	public void skip(int length) throws IOException {
		in.skip(length);
	}
	
	public boolean isEof() {
		return eof;
	}
}
