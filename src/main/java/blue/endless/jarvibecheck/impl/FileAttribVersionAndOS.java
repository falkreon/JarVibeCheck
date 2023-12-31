package blue.endless.jarvibecheck.impl;

public class FileAttribVersionAndOS {
	public enum FileAttribOS {
		DOS(0),
		AMIGA(1),
		OPEN_VMS(2),
		UNIX(3),
		VM_CMS(4),
		ATARI_ST(5),
		OS2_HPFS(6),
		MAC(7),
		Z_SYSTEM(8),
		CPM(9),
		NTFS(10),
		MVS(11),
		VSE(12),
		ACORN_RISC(13),
		VFAT(14), //By experimentation, this is the version gradle produces in linux
		MVS_ALTERNATE(15),
		BE_OS(16),
		TANDEM(17),
		OS_400(18),
		OSX(19),
		UNKNOWN(-1)
		;
	
		private final int value;
	
		FileAttribOS(int version) {
			this.value = version;
		}
	
		public int value() {
			return value;
		}
	
		public static FileAttribOS of(int value) {
			for(FileAttribOS ver : values()) {
				if (ver.value == value) return ver;
			}
			
			return UNKNOWN;
		}
	}
	
	FileAttribOS os;
	int version;
	
	FileAttribVersionAndOS(FileAttribOS os, int version) {
		this.os = os;
		this.version = version;
	}
	
	FileAttribVersionAndOS(int os, int version) {
		this(FileAttribOS.of(os), version);
	}
	
	FileAttribVersionAndOS(int combined) {
		this(((combined >> 8) & 0xFF), (combined & 0xFF));
	}
	
	public static FileAttribVersionAndOS of(int combined) {
		return new FileAttribVersionAndOS(combined);
	}
	
	public String toString() {
		// APPNOTE.TXT section 4.4.2.3:
		// ... The (lower byte)/10 indicates the major version number, 
		// and the (lower byte) mod 10 is the minor version number.  
		return "OS: " + os + ", version: " + version / 10 + "." + version % 10;
	}
}
