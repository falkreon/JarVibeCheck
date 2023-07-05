package blue.endless.jarvibecheck.impl;

import java.io.IOException;
import java.io.InputStream;

public class IntelDataInputStream {
	private final InputStream in;
	private boolean eof = false;
	private long offset = 0L;
	
	public IntelDataInputStream(InputStream in) {
		this.in = in;
	}
	
	public int i8() throws IOException {
		int result = in.read();
		if (result == -1) eof = true;
		offset++;
		return result & 0xFF;
	}
	
	public int i16() throws IOException {
		return
			i8() |
			(i8() << 8);
	}
	
	public long i32() throws IOException {
		return
			(long) i8() |
			((long) i8() << 8) |
			((long) i8() << 16) |
			((long) i8() << 24);
	}
	
	public byte[] readBytes(int length) throws IOException {
		byte[] result = in.readNBytes(length);
		offset += result.length;
		return result;
	}
	
	public void skip(int length) throws IOException {
		offset += length;
		in.skip(length);
	}
	
	public boolean isEof() {
		return eof;
	}
	
	public boolean assertEof() throws IOException {
		if (eof) return true;
		int value = in.read();
		return value == -1;
	}
	
	public long offset() {
		return offset;
	}
}
