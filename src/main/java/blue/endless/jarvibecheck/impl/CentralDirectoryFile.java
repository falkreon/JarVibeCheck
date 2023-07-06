package blue.endless.jarvibecheck.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CentralDirectoryFile {
	public static final int SIGNATURE = 0x02014B50;
	
	public int signature;       //4 bytes
	public int versionMadeBy;   //2 bytes
	public int versionNeededToExtract; //2 bytes
	public int flags;           //2 bytes
	public int compression;     //2 bytes
	public int lastModFileTime; //2 bytes
	public int lastModFileDate; //2 bytes
	public int crc32;           //4 bytes
	public long compressedSize;  //4 bytes
	public long uncompressedSize;//4 bytes
	public int fileNameLength;  //2 bytes
	public int extraFieldLength;//2 bytes
	public int fileCommentLength;//2 bytes
	public int diskNumberStart; //2 bytes
	public int internalFileAttribs; //2 bytes
	public int externalFileAttribs; //4 bytes
	public long headerRelativeOffset; //4 bytes
	
	public String fileName;
	public String fileComment;
	
	
	public static CentralDirectoryFile read(IntelDataInputStream in, int signature) throws IOException {
		CentralDirectoryFile result = new CentralDirectoryFile();
		
		result.signature = signature;
		result.versionMadeBy = in.i16();
		result.versionNeededToExtract = in.i16();
		result.flags = in.i16();
		result.compression = in.i16();
		result.lastModFileTime = in.i16();
		result.lastModFileDate = in.i16();
		result.crc32 = (int) in.i32();
		result.compressedSize = in.i32();
		result.uncompressedSize = in.i32();
		result.fileNameLength = in.i16();
		result.extraFieldLength = in.i16();
		result.fileCommentLength = in.i16();
		result.diskNumberStart = in.i16();
		result.internalFileAttribs = in.i16();
		result.externalFileAttribs = (int) in.i32();
		result.headerRelativeOffset = in.i32();
		
		result.fileName = new String(in.readBytes(result.fileNameLength), StandardCharsets.ISO_8859_1);
		in.skip(result.extraFieldLength);
		result.fileComment = new String(in.readBytes(result.fileCommentLength), StandardCharsets.ISO_8859_1);
		
		return result;
	}
	
	public void dump() {
		System.out.println("CentralDirectoryFile \""+fileName+"\"");
		System.out.println("  VersionMadeBy: "+FileAttribVersionAndOS.of(versionMadeBy)+" (0x"+Integer.toHexString(versionMadeBy)+")");
		System.out.println("  VersionNeededToExtract: "+FileAttribVersionAndOS.of(versionNeededToExtract)+" (0x"+Integer.toHexString(versionNeededToExtract)+")");
		System.out.println("  Flags: 0x"+Integer.toHexString(flags));
		System.out.println("  Compression: 0x"+Integer.toHexString(compression));
		System.out.println("  Last Modified: "+DosDateTime.prettyPrint(lastModFileDate, lastModFileTime));
		System.out.println("  CRC32: 0x"+Integer.toHexString(crc32));
		System.out.println("  CompressedSize: "+compressedSize+" bytes");
		System.out.println("  UncompressedSize: "+uncompressedSize+" bytes");
		System.out.println("  ExtraField: "+extraFieldLength+" bytes");
		System.out.println("  Comment: \""+fileComment+"\"");
		System.out.println("  Disk#: "+diskNumberStart);
		System.out.println("  InternalFileAttribs: 0x"+Integer.toHexString(internalFileAttribs));
		System.out.println("  ExternalFileAttribs: 0x"+Long.toHexString(externalFileAttribs));
		System.out.println("  Header Relative Offset: 0x"+Long.toHexString(headerRelativeOffset));
	}
}
