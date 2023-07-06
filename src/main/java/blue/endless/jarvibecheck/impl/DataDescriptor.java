package blue.endless.jarvibecheck.impl;

import java.io.IOException;

public class DataDescriptor {
	public static final int SIGNATURE = 0x08074b50;
	
	public int crc32; //4 bytes
	public int compressedSize; //4 bytes
	public int uncompressedSize; //4 bytes
	
	public static DataDescriptor read(IntelDataInputStream in) throws IOException {
		DataDescriptor header = new DataDescriptor();
		header.crc32 = (int) in.i32();
		// The data descriptor header is optional
		if (header.crc32 == DataDescriptor.SIGNATURE) {
			header.crc32 = (int) in.i32();
		}
		
		header.compressedSize = (int) in.i32();
		header.uncompressedSize = (int) in.i32();
		
		return header;
	}
}
