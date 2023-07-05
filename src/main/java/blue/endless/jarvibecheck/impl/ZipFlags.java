package blue.endless.jarvibecheck.impl;

public class ZipFlags {
	public static final int ENCRYPTED               = 0x01;
	public static final int COMPRESSION1            = 0x02;
	public static final int COMPRESSION2            = 0x04;
	public static final int DATA_DESCRIPTOR_FOLLOWS = 0x08;
	public static final int ENHANCED_DEFLATE        = 0x10;
	public static final int COMPRESSED_PATCHED_DATA = 0x20;
	public static final int STRONG_ENCRYPTION       = 0x40;
	public static final int UNUSED_BIT7             = 0x80;
	public static final int UNUSED_BIT8            = 0x100;
	public static final int UNUSED_BIT9            = 0x200;
	public static final int UNUSED_BIT10           = 0x400;
	public static final int UNUSED_BIT11           = 0x800;
	public static final int ENHANCED_COMPRESSION  = 0x1000;
	public static final int DATA_IS_MASKED        = 0x2000;
	public static final int PKWARE_RESERVED_BIT14 = 0x4000;
	public static final int PKWARE_RESERVED_BIT15 = 0x8000;
	
	public static void dumpFlags(int flags, int compressionMethod) {
		if ((flags & ENCRYPTED) != 0) System.out.println("    Encrypted");
		if ((flags & DATA_DESCRIPTOR_FOLLOWS) != 0) System.out.println("    CRC and Sizes Are Zero: Data descriptor follows");
		if ((flags & ENHANCED_DEFLATE) != 0) System.out.println("    Enhanced deflate (for use with method 8 / deflate)");
		if ((flags & COMPRESSED_PATCHED_DATA) != 0) System.out.println("    Compressed patched data");
		if ((flags & STRONG_ENCRYPTION) != 0) System.out.println("    Strong Encryption");
		if ((flags & ENHANCED_COMPRESSION) != 0) System.out.println("    Enhanced compression");
		if ((flags & DATA_IS_MASKED) != 0) System.out.println("    Data is masked / encrypted");
		
		if (compressionMethod == 8 || compressionMethod == 9) {
			boolean c1 = (flags & COMPRESSION1) != 0;
			boolean c2 = (flags & COMPRESSION2) != 0;
			if (!c1 & !c2) {
				System.out.println("    Normal (-en) compression option was used.");
			} else if (c1 && !c2) {
				System.out.println("    Maximum (-exx/-ex) compression option was used.");
			} else if (!c1 && c2) {
				System.out.println("    Fast (-ef) compression option was used.");
			} else if (c1 && c2) {
				System.out.println("    Super Fast (-es) compression option was used.");
			}
		}
	}
}
