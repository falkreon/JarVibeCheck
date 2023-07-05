package blue.endless.jarvibecheck.impl;

public enum FileAttribVersion {
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
	
	FileAttribVersion(int version) {
		this.value = version;
	}
	
	public int value() {
		return value;
	}
	
	public static FileAttribVersion of(int value) {
		for(FileAttribVersion ver : values()) {
			if (ver.value == value) return ver;
		}
		
		return UNKNOWN;
	}
}
