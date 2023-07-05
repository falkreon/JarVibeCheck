package blue.endless.jarvibecheck;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Main {
	public static void main(String... args) {
		if (args.length == 0) {
			printUsage();
			return;
		}
		
		Path p = null;
		boolean verbose = false;
		
		for(String s : args) {
			if (s.startsWith("--")) {
				if (s.equals("--verbose")) verbose = true;
			} else {
				if (p == null) {
					p = Path.of(s);
				} else {
					printUsage();
					return;
				}
			}
		}
		
		if (!Files.exists(p)) {
			System.err.println("File \""+p.toString()+"\" does not exist.");
		}
		
		try {
			Optional<String> failReason = JarVibeCheck.process(p, verbose);
			
			if (!failReason.isEmpty()) {
				System.out.println("Check FAILED: "+failReason.get());
				System.exit(1);
			}
		} catch (Throwable t) {
			System.out.println("Check FAILED: Unknown (crashed the parser!)");
			t.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Check complete.");
	}
	
	public static void printUsage() {
		System.out.println("usage: java -jar JarVibeCheck.jar <path> [--verbose]");
	}
}
