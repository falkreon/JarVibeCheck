package blue.endless.jarvibecheck;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.Map;
import java.util.HashMap;

import blue.endless.jarvibecheck.impl.LocalFileHeader;
import blue.endless.jarvibecheck.impl.CentralDirectoryFile;
import blue.endless.jarvibecheck.impl.DataDescriptor;
import blue.endless.jarvibecheck.impl.EndOfCentralDirectory;
import blue.endless.jarvibecheck.impl.IntelDataInputStream;

/**
 * Jar Vibe Check - Does quick early detection of potentially malicious features. ANY bad vibes will cause the jar to
 * fail the check.
 * 
 * <p>To this utility, a "trivial zip" contains exactly the following, in order:
 * <li>One or more local file entries. An empty zip with no `META-INF/` directory creates bad vibes.
 * <li>Exactly one "central directory", which MUST be the penultimate entry in the file.
 * <li>Exactly one "end of central directory", which MUST be the last entry in the file.
 * 
 * <p>The zip spec allows for zips to be "rewritten" kind of like a CD-RW to carry multiple central directories; the
 * last central directory that appears should be the effective one, but any usage of this feature creates bad vibes.
 * 
 * <p>Local file entries MUST be tightly packed. Any space between files indicates hidden data which creates bad vibes.
 * 
 * <p>All local file entries MUST be referenced by the central directory. Any file which is "hidden" creates bad vibes.
 * 
 * <p>Any hint of encrypted contents is bad vibes.
 * 
 */
public class JarVibeCheck {
	public static Optional<String> process(Path path, boolean verbose) {
		Map<Long, LocalFileHeader> fileHeaders = new HashMap<>();
		
		try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
			IntelDataInputStream din = new IntelDataInputStream(in);
			
			int signature = 0;
			while(!din.isEof()) {
				long offset = din.offset();
				
				signature = (int) din.i32();
				if (signature == LocalFileHeader.SIGNATURE) {
					LocalFileHeader header = LocalFileHeader.read(din, signature);
					fileHeaders.put(offset, header);
					
					if (header.compressedSize == 0 && header.uncompressedSize == 0 && header.crc32 != 0) {
						return Optional.of("A local file header's compressed and uncompressed sizes were both zero, but the crc was set.");
					}
					
					if ((header.flags & 8) == 8) {
						if (header.compression != 0x08) {
							return Optional.of("A data descriptor was used in a local file header, but the compression method wasn't deflate");
						}
						
						if (header.compressedSize != 0 || header.uncompressedSize != 0 || header.crc32 != 0) {
							return Optional.of("A data descriptor was used in a local file header, but non-zero vaues were provided for fields that should be zero");
						}
						
						// Skip to the end of compressed data
						Inflater inflator = new Inflater(true);
						
						inflator.setInput(din.readBytes(1));
						
						byte[] outBuf = new byte[1024];
						int inflated = 1;
						
						while (!inflator.finished()) {
							if (inflator.needsDictionary()) {
								// This shouldn't happen, but if it does, the input isn't valid anyway
								inflator.end();
								return Optional.of("A dictionary was needed for deflate compressed data in a local file header");
							}
							
							if (inflator.needsInput()) {
								inflator.setInput(din.readBytes(1));
								inflated++;
							}
							
							try {
								inflator.inflate(outBuf);
							} catch (DataFormatException e) {
								inflator.end();
								return Optional.of("Deflate compressed data wasn't valid in a local file header");
							}
						}
						
						inflator.end();
						
						// Read the data descriptor
						DataDescriptor dataDesc = DataDescriptor.read(din);
						
						header.crc32 = dataDesc.crc32;
						header.compressedSize = dataDesc.compressedSize;
						header.uncompressedSize = dataDesc.uncompressedSize;
						
						if (inflated != header.compressedSize) {
							return Optional.of("Deflate compressed data size didn't match amount of bytes decompressed in a local file header");
						}
					} else {
						din.skip((int) header.compressedSize);
					}
					
					if (verbose) header.dump();
				} else {
					if (signature == CentralDirectoryFile.SIGNATURE) {
						break;
					} else {
						//TODO: Explain disallowed entries
						return Optional.of("Found inappropriate signature (could be garbage between files or a disallowed entry): 0x"+Integer.toHexString(signature));
					}
					
				}
			}
			
			if (din.isEof()) return Optional.of("File ended too early - no central directory found.");
			
			
			long centralDirectoryOffset = din.offset() - 4;
			
			while (!din.isEof()) {
				if (signature == CentralDirectoryFile.SIGNATURE) {
					CentralDirectoryFile dir = CentralDirectoryFile.read(din, signature);
					
					if (verbose) dir.dump();
					
					LocalFileHeader fileHeader = fileHeaders.get(dir.headerRelativeOffset);
					if (fileHeader == null) {
						
						/*
						System.out.println("Dumping header offsets");
						for(Map.Entry<Long, LocalFileHeader> entry : fileHeaders.entrySet()) {
							System.out.println("  0x"+Long.toHexString(entry.getKey())+": "+entry.getValue().fileName);
						}*/
						
						return Optional.of("Can't find corresponding file header for directory entry \""+dir.fileName+"\" (supposed to be 0x"+Long.toHexString(dir.headerRelativeOffset)+")");
					} else {
						//TODO: Match up ABSOLUTELY EVERYTHING about these files
						
						if (dir.compressedSize != fileHeader.compressedSize) {
							return Optional.of("Compressed file size discrepancy between central directory and local file record.");
						}
						
						if (dir.uncompressedSize != fileHeader.uncompressedSize) {
							return Optional.of("Uncompressed file size discrepancy between central directory and local file record.");
						}
						
						if (!dir.fileName.equals(fileHeader.fileName)) {
							return Optional.of("File name (and possibly location!) is different between central directory and local file record.");
						}
						
					}
				} else {
					if (signature == EndOfCentralDirectory.SIGNATURE) {
						break;
					} else {
						return Optional.of("Found inappropriate signature (could be garbage between entries or a disallowed entry): 0x"+Integer.toHexString(signature));
					}
				}
				
				signature = (int) din.i32();
			}
			
			if (din.isEof()) return Optional.of("File ended too early - no end-of-central-directory found.");
			
			//long eocdOffset = din.offset() - 4;
			
			EndOfCentralDirectory endOfDirectory = EndOfCentralDirectory.read(din, signature);
			if (verbose) endOfDirectory.dump();
			
			if (endOfDirectory.offsetOfCentralDirectory != centralDirectoryOffset) {
				return Optional.of("End of Central Directory does not point to the start of the Central Directory.");
			}
			
			if (!din.assertEof()) {
				return Optional.of("End of Central Directory happened before the end of the file. This file may have been retroactively expanded or there may be hidden items.");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.of("Unknown (IOException while processing the file)");
		}
		
		return Optional.empty();
	}
}
