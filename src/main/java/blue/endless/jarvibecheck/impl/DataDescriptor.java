package blue.endless.jarvibecheck.impl;

import java.io.IOException;

public class DataDescriptor {
	public static final int SIGNATURE = 0x08074b50;
	
	public long crc32; //4 bytes
	public long compressedSize; //4 bytes
	public long uncompressedSize; //4 bytes
	
	public static DataDescriptor read(IntelDataInputStream in) throws IOException {
		DataDescriptor header = new DataDescriptor();
		header.crc32 = in.i32();
		// The data descriptor header is optional
		if (header.crc32 == DataDescriptor.SIGNATURE) {
			header.crc32 = in.i32();
		}
		
		header.compressedSize = in.i32();
		header.uncompressedSize = in.i32();
		
		return header;
	}
}
