package blue.endless.jarvibecheck.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LocalFileHeader {
	public static final int SIGNATURE = 0x04034B50;
	
	public long offset;
	
	public int sig;    //4 bytes, MUST be `0x4034b50`
	public int version;//2 bytes, seen `0x14 00` in the wild
	public int flags;  //2 bytes, `0x00 08`
	public int compression; //2 bytes, `0x08 00` is deflate, `0x00 00` is store
	public int lastModTime; //2 bytes
	public int lastModDate; //2 bytes
	public long crc32;       //4 bytes obviously - typically zeroed out in combination with `00 08` flag
	public long compressedSize; //4 bytes - typically zeroed out in combination with `00 08` flag
	public long uncompressedSize; //4 bytes - typically zeroed out in combination with `00 08` flag
	public int fileNameLength; //2 bytes
	public int extraFieldLength; //2 bytes
	public String fileName;
	//public byte[] extraField;
	
	public static LocalFileHeader read(IntelDataInputStream in, int sig) throws IOException {
		LocalFileHeader result = new LocalFileHeader();
		
		result.offset = in.offset() - 4;
		
		result.sig = sig;
		result.version = in.i16();
		result.flags = in.i16();
		result.compression = in.i16();
		result.lastModTime = in.i16();
		result.lastModDate = in.i16();
		result.crc32 = in.i32();
		result.compressedSize = in.i32();
		result.uncompressedSize = in.i32();
		result.fileNameLength = in.i16();
		result.extraFieldLength = in.i16();
		byte[] fileNameBytes = in.readBytes(result.fileNameLength);
		result.fileName = new String(fileNameBytes, StandardCharsets.ISO_8859_1); // not UTF-8 because this is PKZIP format
		if (result.extraFieldLength>0) in.skip(result.extraFieldLength);
		
		return result;
	}
	
	public void dump() {
		System.out.println(hexLpad(offset)+"  LocalFileHeader");
		System.out.println("  Version: "+FileAttribVersionAndOS.of(version)+" (0x"+Integer.toHexString(version)+")");
		System.out.println("  Flags: 0x"+Integer.toHexString(flags));
		ZipFlags.dumpFlags(flags, compression);
		System.out.println("  Compression: "+CompressionMethod.of(compression)+" (0x"+Integer.toHexString(compression)+")");
		System.out.println("  LastModified: "+DosDateTime.prettyPrint(lastModDate, lastModTime));
		System.out.println("  CRC32: 0x"+Long.toHexString(crc32));
		System.out.println("  Compressed Size: "+compressedSize);
		System.out.println("  Uncompressed Size: "+uncompressedSize);
		System.out.println("  Extra Data Size: "+extraFieldLength);
		System.out.println("  FileName: \""+fileName+"\"");
	}
	
	private String hexLpad(long value) {
		String result = Long.toHexString(value);
		while(result.length() < 4) {
			result = "0"+result;
		}
		
		return result;
	}
}
