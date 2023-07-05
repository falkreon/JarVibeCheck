package blue.endless.jarvibecheck;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Main {
	public static void main(String... args) {
		if (args.length == 0 || args.length > 1) {
			System.out.println("usage: java -jar JarVibeCheck.jar <path>");
			
			return;
		}
		
		Path p = Path.of(args[0]);
		if (!Files.exists(p)) {
			System.err.println("File \""+p.toString()+"\" does not exist.");
		}
		
		try {
			Optional<String> failReason = JarVibeCheck.process(p);
			
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
}
