package blue.endless.jarvibecheck.impl;

public enum CompressionMethod {
	STORE(0),
	SHRINK(1),
	REDUCE_FACTOR1(2),
	REDUCE_FACTOR2(3),
	REDUCE_FACTOR3(4),
	REDUCE_FACTOR4(5),
	IMPLODE(6),
	TOKENIZE(7),
	DEFLATE(8),
	DEFLATE64(9),
	PKWARE_IMPLODE(10),
	PKWARE_RESERVED(11),
	BZIP2(12),
	UNKNOWN(-1)
	;
	
	private final int value;
	
	CompressionMethod(int value) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
	
	public static CompressionMethod of(int value) {
		for(CompressionMethod ver : values()) {
			if (ver.value == value) return ver;
		}
		
		return UNKNOWN;
	}
}
