package blue.endless.jarvibecheck;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import blue.endless.jarvibecheck.impl.LocalFileHeader;
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
 */
public class JarVibeCheck {
	public static Optional<String> process(Path path) {
		Map<Long, LocalFileHeader> fileHeaders = new HashMap<>();
		
		try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
			IntelDataInputStream din = new IntelDataInputStream(in);
			
			while(!din.isEof()) {
				int signature = din.i32();
				if (signature == LocalFileHeader.INTEL_SIGNATURE) {
					LocalFileHeader header = LocalFileHeader.read(din, signature);
					
					header.dump();
					din.skip(header.compressedSize);
				} else {
					
					System.out.println("Found new signature: "+Integer.toHexString(signature));
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Optional.empty();
	}
}
