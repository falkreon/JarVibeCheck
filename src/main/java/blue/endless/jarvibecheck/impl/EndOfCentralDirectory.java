package blue.endless.jarvibecheck.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EndOfCentralDirectory {
	public static final int SIGNATURE = 0x06054B50;
	
	public int signature;  //4 bytes
	public int diskNumber; //2 bytes
	public int centralDirectoryStartDisk; //2 bytes
	public int entriesOnThisDisk; //2 bytes
	public int totalEntries; //2 bytes
	public long centralDirectorySize; //4 bytes
	public long offsetOfCentralDirectory; //4 bytes
	public int commentLength;
	public String comment;
	
	public static EndOfCentralDirectory read(IntelDataInputStream in, int signature) throws IOException {
		EndOfCentralDirectory result = new EndOfCentralDirectory();
		
		result.signature = signature;
		result.diskNumber = in.i16();
		result.centralDirectoryStartDisk = in.i16();
		result.entriesOnThisDisk = in.i16();
		result.totalEntries = in.i16();
		result.centralDirectorySize = in.i32();
		result.offsetOfCentralDirectory = in.i32();
		result.commentLength = in.i16();
		result.comment = new String(in.readBytes(result.commentLength), StandardCharsets.ISO_8859_1);
		
		return result;
	}
	
	public void dump() {
		System.out.println("EndOfDirectory");
		System.out.println("  DiskNumber: "+diskNumber);
		System.out.println("  CentralDirectoryStartDisk: "+centralDirectoryStartDisk);
		System.out.println("  EntriesOnThisDisk: "+entriesOnThisDisk);
		System.out.println("  TotalEntries: "+totalEntries);
		System.out.println("  CentralDirectorySize: "+centralDirectorySize+" bytes");
		System.out.println("  OffsetOfCentralDirectory: "+offsetOfCentralDirectory+" bytes into this disk");
		System.out.println("  Comment: \""+comment+"\"");
	}
}
